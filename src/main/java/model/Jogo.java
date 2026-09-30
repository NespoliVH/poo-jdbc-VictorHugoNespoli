package model;

import java.math.BigDecimal;
import java.util.Locale;

/** Representa o lado N da associação, mantendo uma referência ao objeto pai. */
public class Jogo {
    private int id;
    private String titulo;
    private double precoVenda;
    private double tamanhoGB;
    private Desenvolvedora desenvolvedora;

    public Jogo() {
        // Os dados obrigatórios devem ser preenchidos antes de salvar.
    }

    public Jogo(String titulo, double precoVenda, double tamanhoGB,
                Desenvolvedora desenvolvedora) {
        this(0, titulo, precoVenda, tamanhoGB, desenvolvedora);
    }

    public Jogo(int id, String titulo, double precoVenda, double tamanhoGB,
                Desenvolvedora desenvolvedora) {
        setId(id);
        setTitulo(titulo);
        setPrecoVenda(precoVenda);
        setTamanhoGB(tamanhoGB);
        setDesenvolvedora(desenvolvedora);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("O ID não pode ser negativo.");
        }
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank() || titulo.length() > 150) {
            throw new IllegalArgumentException("O título deve ter de 1 a 150 caracteres.");
        }
        this.titulo = titulo.trim();
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        validarDecimal(precoVenda, 99999999.99, "Preço de venda");
        this.precoVenda = precoVenda;
    }

    public double getTamanhoGB() {
        return tamanhoGB;
    }

    public void setTamanhoGB(double tamanhoGB) {
        validarDecimal(tamanhoGB, 9999.99, "Tamanho em GB");
        this.tamanhoGB = tamanhoGB;
    }

    public Desenvolvedora getDesenvolvedora() {
        return desenvolvedora;
    }

    public void setDesenvolvedora(Desenvolvedora desenvolvedora) {
        if (desenvolvedora == null) {
            throw new IllegalArgumentException("O jogo deve ter uma desenvolvedora.");
        }
        this.desenvolvedora = desenvolvedora;
    }

    /** Confere os campos obrigatórios e se o pai já recebeu um ID do banco. */
    public void validar() {
        if (titulo == null) {
            throw new IllegalArgumentException("Informe o título do jogo.");
        }
        if (desenvolvedora == null || desenvolvedora.getId() <= 0) {
            throw new IllegalArgumentException("Salve a desenvolvedora antes de salvar o jogo.");
        }
        desenvolvedora.validar();
    }

    /** Respeita a precisão dos NUMERIC do enunciado sem arredondar silenciosamente. */
    private static void validarDecimal(double valor, double maximo, String campo) {
        if (!Double.isFinite(valor) || valor < 0 || valor > maximo) {
            throw new IllegalArgumentException(campo + " deve estar entre 0 e " + maximo + '.');
        }
        if (BigDecimal.valueOf(valor).stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException(campo + " deve ter no máximo duas casas decimais.");
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.forLanguageTag("pt-BR"),
                "Jogo{id=%d, titulo='%s', precoVenda=R$ %.2f, tamanhoGB=%.2f, desenvolvedora=%s}",
                id, titulo, precoVenda, tamanhoGB, desenvolvedora);
    }
}
