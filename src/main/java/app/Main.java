package app;

import dao.DesenvolvedoraDAO;
import dao.JogoDAO;
import model.Desenvolvedora;
import model.Jogo;

import java.sql.SQLException;
import java.util.List;

/** Demonstra, pelo console, todas as operações exigidas no trabalho. */
public class Main {

    public static void main(String[] args) {
        try {
            demonstrarCrud();
        } catch (SQLException e) {
            System.err.println("Falha ao acessar o banco: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Confira o PostgreSQL, os scripts SQL e database.properties.");
            System.exit(1);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Falha na demonstração: " + e.getMessage());
            System.exit(1);
        }
    }

    /** Usa dados fictícios próprios e os remove ao terminar com sucesso. */
    private static void demonstrarCrud() throws SQLException {
        DesenvolvedoraDAO desenvolvedoraDAO = new DesenvolvedoraDAO();
        JogoDAO jogoDAO = new JogoDAO();

        System.out.println("LOJA DE JOGOS DIGITAIS E PRODUTORAS");
        System.out.println("Aluno: Victor Hugo Nespoli Ferreira");

        System.out.println("\n1. Inserção de uma desenvolvedora e de dois jogos (1:N)");
        Desenvolvedora estudio = new Desenvolvedora("Aurora Games Ltda.", "Brasil", 2018);
        desenvolvedoraDAO.salvar(estudio);
        Jogo aventura = new Jogo("Aventura Cósmica", 129.90, 25.50, estudio);
        Jogo corrida = new Jogo("Corrida Lunar", 79.90, 12.75, estudio);
        jogoDAO.salvar(aventura);
        jogoDAO.salvar(corrida);
        verificar(estudio.getId() > 0 && aventura.getId() > 0 && corrida.getId() > 0,
                "Os IDs gerados não foram atribuídos aos objetos.");
        System.out.printf("IDs gerados: desenvolvedora=%d; jogos=%d e %d%n",
                estudio.getId(), aventura.getId(), corrida.getId());

        System.out.println("\n2. Listagem das desenvolvedoras");
        List<Desenvolvedora> desenvolvedoras = desenvolvedoraDAO.listarTodos();
        desenvolvedoras.forEach(System.out::println);
        verificar(desenvolvedoras.stream().anyMatch(d -> d.getId() == estudio.getId()),
                "A desenvolvedora inserida não apareceu na listagem.");

        System.out.println("\n3. Listagem de jogos e suas desenvolvedoras (INNER JOIN)");
        List<Jogo> jogos = jogoDAO.listarTodos();
        jogos.forEach(System.out::println);
        long jogosDoEstudio = jogos.stream()
                .filter(j -> j.getDesenvolvedora().getId() == estudio.getId()).count();
        verificar(jogosDoEstudio == 2, "A consulta não recuperou os dois jogos associados.");

        System.out.println("\n4. Busca por ID nas duas entidades");
        System.out.println(desenvolvedoraDAO.buscarPorId(estudio.getId()).orElseThrow(
                () -> new IllegalStateException("Desenvolvedora não encontrada.")));
        Jogo jogoEncontrado = jogoDAO.buscarPorId(aventura.getId()).orElseThrow(
                () -> new IllegalStateException("Jogo não encontrado."));
        verificar(jogoEncontrado.getDesenvolvedora().getRazaoSocial().equals(estudio.getRazaoSocial()),
                "O JOIN não recuperou os atributos da desenvolvedora.");
        System.out.println(jogoEncontrado);

        System.out.println("\n5. Atualização das duas entidades e nova leitura do banco");
        estudio.setRazaoSocial("Aurora Games Brasil Ltda.");
        estudio.setAnoCriacao(2019);
        verificar(desenvolvedoraDAO.atualizar(estudio), "A desenvolvedora não foi atualizada.");
        aventura.setTitulo("Aventura Cósmica: Edição Especial");
        aventura.setPrecoVenda(99.90);
        aventura.setTamanhoGB(27.25);
        verificar(jogoDAO.atualizar(aventura), "O jogo não foi atualizado.");

        Desenvolvedora estudioAtualizado = desenvolvedoraDAO.buscarPorId(estudio.getId()).orElseThrow(
                () -> new IllegalStateException("Desenvolvedora não encontrada após atualização."));
        Jogo jogoAtualizado = jogoDAO.buscarPorId(aventura.getId()).orElseThrow(
                () -> new IllegalStateException("Jogo não encontrado após atualização."));
        verificar(estudioAtualizado.getRazaoSocial().equals(estudio.getRazaoSocial())
                        && estudioAtualizado.getAnoCriacao() == 2019,
                "A atualização da desenvolvedora não foi persistida.");
        verificar(jogoAtualizado.getTitulo().equals(aventura.getTitulo())
                        && Double.compare(jogoAtualizado.getPrecoVenda(), 99.90) == 0
                        && Double.compare(jogoAtualizado.getTamanhoGB(), 27.25) == 0
                        && jogoAtualizado.getDesenvolvedora().getRazaoSocial()
                        .equals(estudio.getRazaoSocial()),
                "A atualização do jogo ou a associação não foi persistida corretamente.");
        System.out.println(estudioAtualizado);
        System.out.println(jogoAtualizado);

        System.out.println("\n6. Tentativa de excluir a desenvolvedora com jogos vinculados");
        try {
            desenvolvedoraDAO.deletar(estudio.getId());
            throw new IllegalStateException("O banco deveria impedir esta exclusão.");
        } catch (SQLException e) {
            // Dependendo da versão: 23503 = foreign_key_violation;
            // 23001 = restrict_violation. Outros erros não contam como teste aprovado.
            if (!"23503".equals(e.getSQLState()) && !"23001".equals(e.getSQLState())) {
                throw e;
            }
            System.out.println("Exclusão bloqueada corretamente por ON DELETE RESTRICT (SQLState "
                    + e.getSQLState() + ").");
        }

        System.out.println("\n7. Remoção dos jogos e, depois, da desenvolvedora");
        verificar(jogoDAO.deletar(aventura.getId()), "O primeiro jogo não foi excluído.");
        verificar(jogoDAO.deletar(corrida.getId()), "O segundo jogo não foi excluído.");
        verificar(desenvolvedoraDAO.deletar(estudio.getId()), "A desenvolvedora não foi excluída.");
        verificar(jogoDAO.buscarPorId(aventura.getId()).isEmpty()
                        && jogoDAO.buscarPorId(corrida.getId()).isEmpty()
                        && desenvolvedoraDAO.buscarPorId(estudio.getId()).isEmpty(),
                "Ainda existem registros da demonstração após a exclusão.");

        System.out.println("\nDemonstração concluída: CRUD, JOIN e integridade referencial verificados.");
        System.out.println("Somente os registros criados nesta execução foram removidos.");
    }

    /** As verificações são executadas sempre, sem depender da opção -ea do Java. */
    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new IllegalStateException(mensagem);
        }
    }
}
