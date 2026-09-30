package dao;

import model.Desenvolvedora;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Executa o CRUD da tabela desenvolvedora, sem misturar SQL com a apresentação. */
public class DesenvolvedoraDAO {

    /** Insere o registro e atribui ao objeto o ID gerado pelo PostgreSQL. */
    public void salvar(Desenvolvedora desenvolvedora) throws SQLException {
        desenvolvedora.validar();
        if (desenvolvedora.getId() != 0) {
            throw new IllegalArgumentException("Para salvar, use uma desenvolvedora sem ID.");
        }
        String sql = """
                INSERT INTO desenvolvedora (razao_social, pais_origem, ano_criacao)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherDados(comando, desenvolvedora);
            if (comando.executeUpdate() != 1) {
                throw new SQLException("Não foi possível inserir a desenvolvedora.");
            }
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("O banco não retornou o ID da desenvolvedora.");
                }
                desenvolvedora.setId(chaves.getInt("id"));
            }
        }
    }

    /** Retorna Optional.empty() quando não existe registro com o ID informado. */
    public Optional<Desenvolvedora> buscarPorId(int id) throws SQLException {
        validarId(id);
        String sql = "SELECT id, razao_social, pais_origem, ano_criacao FROM desenvolvedora WHERE id = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    /** Lista também desenvolvedoras que ainda não têm jogos cadastrados. */
    public List<Desenvolvedora> listarTodos() throws SQLException {
        String sql = "SELECT id, razao_social, pais_origem, ano_criacao FROM desenvolvedora ORDER BY id";
        List<Desenvolvedora> desenvolvedoras = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                desenvolvedoras.add(mapear(resultado));
            }
        }
        return desenvolvedoras;
    }

    /** Atualiza os atributos; false informa que o ID não foi encontrado. */
    public boolean atualizar(Desenvolvedora desenvolvedora) throws SQLException {
        desenvolvedora.validar();
        validarId(desenvolvedora.getId());
        String sql = """
                UPDATE desenvolvedora
                SET razao_social = ?, pais_origem = ?, ano_criacao = ?
                WHERE id = ?
                """;
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherDados(comando, desenvolvedora);
            comando.setInt(4, desenvolvedora.getId());
            return comando.executeUpdate() == 1;
        }
    }

    /** A chave estrangeira impede a exclusão quando ainda existem jogos vinculados. */
    public boolean deletar(int id) throws SQLException {
        validarId(id);
        String sql = "DELETE FROM desenvolvedora WHERE id = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() == 1;
        }
    }

    /** Associa valores aos parâmetros; dados opcionais são gravados como SQL NULL. */
    private void preencherDados(PreparedStatement comando, Desenvolvedora desenvolvedora)
            throws SQLException {
        comando.setString(1, desenvolvedora.getRazaoSocial());
        comando.setString(2, desenvolvedora.getPaisOrigem());
        if (desenvolvedora.getAnoCriacao() == 0) {
            comando.setNull(3, Types.INTEGER);
        } else {
            comando.setInt(3, desenvolvedora.getAnoCriacao());
        }
    }

    /** Converte a linha corrente do ResultSet em um objeto de domínio. */
    private Desenvolvedora mapear(ResultSet resultado) throws SQLException {
        Integer ano = resultado.getObject("ano_criacao", Integer.class);
        return new Desenvolvedora(
                resultado.getInt("id"),
                resultado.getString("razao_social"),
                resultado.getString("pais_origem"),
                ano == null ? 0 : ano);
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Informe um ID positivo.");
        }
    }
}
