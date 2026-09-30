package dao;

import model.Desenvolvedora;
import model.Jogo;
import util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Executa o CRUD de jogos e recupera sua desenvolvedora por INNER JOIN. */
public class JogoDAO {

    // Os aliases distinguem os IDs das duas tabelas no mesmo ResultSet.
    private static final String SELECT_COM_DESENVOLVEDORA = """
            SELECT j.id AS jogo_id, j.titulo, j.preco_venda, j.tamanho_gb,
                   d.id AS desenvolvedora_id, d.razao_social, d.pais_origem, d.ano_criacao
            FROM jogo j
            INNER JOIN desenvolvedora d ON d.id = j.desenvolvedora_id
            """;

    /** Persiste o jogo associado a uma desenvolvedora já cadastrada. */
    public void salvar(Jogo jogo) throws SQLException {
        jogo.validar();
        if (jogo.getId() != 0) {
            throw new IllegalArgumentException("Para salvar, use um jogo sem ID.");
        }
        String sql = """
                INSERT INTO jogo (titulo, preco_venda, tamanho_gb, desenvolvedora_id)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherDados(comando, jogo);
            if (comando.executeUpdate() != 1) {
                throw new SQLException("Não foi possível inserir o jogo.");
            }
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("O banco não retornou o ID do jogo.");
                }
                jogo.setId(chaves.getInt("id"));
            }
        }
    }

    /** Consulta o jogo e reconstrói o objeto Desenvolvedora com todos os atributos. */
    public Optional<Jogo> buscarPorId(int id) throws SQLException {
        validarId(id);
        String sql = SELECT_COM_DESENVOLVEDORA + " WHERE j.id = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    /** Lista jogos com os dados associados, utilizando um único INNER JOIN. */
    public List<Jogo> listarTodos() throws SQLException {
        String sql = SELECT_COM_DESENVOLVEDORA + " ORDER BY j.id";
        List<Jogo> jogos = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                jogos.add(mapear(resultado));
            }
        }
        return jogos;
    }

    /** Atualiza os dados e permite mudar a associação para outra desenvolvedora existente. */
    public boolean atualizar(Jogo jogo) throws SQLException {
        jogo.validar();
        validarId(jogo.getId());
        String sql = """
                UPDATE jogo
                SET titulo = ?, preco_venda = ?, tamanho_gb = ?, desenvolvedora_id = ?
                WHERE id = ?
                """;
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherDados(comando, jogo);
            comando.setInt(5, jogo.getId());
            return comando.executeUpdate() == 1;
        }
    }

    /** Exclui apenas o jogo identificado; não exclui sua desenvolvedora. */
    public boolean deletar(int id) throws SQLException {
        validarId(id);
        String sql = "DELETE FROM jogo WHERE id = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() == 1;
        }
    }

    private void preencherDados(PreparedStatement comando, Jogo jogo) throws SQLException {
        comando.setString(1, jogo.getTitulo());
        // O modelo mantém double, como solicitado. O JDBC envia decimais ao NUMERIC.
        comando.setBigDecimal(2, BigDecimal.valueOf(jogo.getPrecoVenda()));
        comando.setBigDecimal(3, BigDecimal.valueOf(jogo.getTamanhoGB()));
        comando.setInt(4, jogo.getDesenvolvedora().getId());
    }

    /** Mapeia cada linha do JOIN para dois objetos Java associados. */
    private Jogo mapear(ResultSet resultado) throws SQLException {
        Integer ano = resultado.getObject("ano_criacao", Integer.class);
        Desenvolvedora desenvolvedora = new Desenvolvedora(
                resultado.getInt("desenvolvedora_id"),
                resultado.getString("razao_social"),
                resultado.getString("pais_origem"),
                ano == null ? 0 : ano);

        return new Jogo(
                resultado.getInt("jogo_id"),
                resultado.getString("titulo"),
                resultado.getBigDecimal("preco_venda").doubleValue(),
                resultado.getBigDecimal("tamanho_gb").doubleValue(),
                desenvolvedora);
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Informe um ID positivo.");
        }
    }
}
