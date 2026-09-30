package util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Centraliza a configuração e a abertura das conexões JDBC. */
public final class ConnectionFactory {

    private ConnectionFactory() {
        // Classe utilitária: não deve ser instanciada.
    }

    /** Abre uma conexão; quem a utilizar deve fechá-la com try-with-resources. */
    public static Connection getConnection() throws SQLException {
        try {
            // Carregamento explícito solicitado no enunciado.
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver PostgreSQL não encontrado. Execute o projeto pelo Maven.", e);
        }

        Properties configuracao = carregarConfiguracao();
        String url = obterValor(configuracao, "DB_URL", "db.url",
                "jdbc:postgresql://localhost:5432/poo_exercicios");
        String usuario = obterValor(configuracao, "DB_USER", "db.user", "postgres");
        String senha = obterValor(configuracao, "DB_PASSWORD", "db.password", null);

        if (senha == null) {
            throw new SQLException("Configure db.password em database.properties "
                    + "ou a variável de ambiente DB_PASSWORD.");
        }

        Properties propriedades = new Properties();
        propriedades.setProperty("user", usuario);
        propriedades.setProperty("password", senha);
        propriedades.setProperty("connectTimeout", "10");
        return DriverManager.getConnection(url, propriedades);
    }

    /** Lê o arquivo local, quando existir, sem incluir senhas no código-fonte. */
    private static Properties carregarConfiguracao() throws SQLException {
        Properties propriedades = new Properties();
        Path caminho = Path.of("database.properties");
        if (Files.exists(caminho)) {
            try (Reader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
                propriedades.load(leitor);
            } catch (IOException e) {
                throw new SQLException("Não foi possível ler database.properties.", e);
            }
        }
        return propriedades;
    }

    /** Variáveis de ambiente têm prioridade sobre o arquivo de configuração. */
    private static String obterValor(Properties propriedades, String variavel,
                                     String chave, String padrao) {
        String valorAmbiente = System.getenv(variavel);
        return valorAmbiente != null
                ? valorAmbiente : propriedades.getProperty(chave, padrao);
    }
}
