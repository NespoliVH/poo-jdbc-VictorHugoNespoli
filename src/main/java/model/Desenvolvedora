package model;

/** Representa o lado 1 da associação: uma desenvolvedora pode ter vários jogos. */
public class Desenvolvedora {
    private int id;
    private String razaoSocial;
    private String paisOrigem;
    private int anoCriacao;

    public Desenvolvedora() {
        // Permite criar o objeto e preencher seus atributos pelos setters.
    }

    public Desenvolvedora(String razaoSocial, String paisOrigem, int anoCriacao) {
        this(0, razaoSocial, paisOrigem, anoCriacao);
    }

    public Desenvolvedora(int id, String razaoSocial, String paisOrigem, int anoCriacao) {
        setId(id);
        setRazaoSocial(razaoSocial);
        setPaisOrigem(paisOrigem);
        setAnoCriacao(anoCriacao);
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

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        if (razaoSocial == null || razaoSocial.isBlank() || razaoSocial.length() > 120) {
            throw new IllegalArgumentException("A razão social deve ter de 1 a 120 caracteres.");
        }
        this.razaoSocial = razaoSocial.trim();
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        if (paisOrigem != null && paisOrigem.length() > 60) {
            throw new IllegalArgumentException("O país deve ter até 60 caracteres.");
        }
        this.paisOrigem = paisOrigem == null || paisOrigem.isBlank()
                ? null : paisOrigem.trim();
    }

    public int getAnoCriacao() {
        return anoCriacao;
    }

    /** O valor 0 representa ano não informado e será gravado como SQL NULL. */
    public void setAnoCriacao(int anoCriacao) {
        if (anoCriacao < 0 || anoCriacao > 9999) {
            throw new IllegalArgumentException("Informe um ano entre 1 e 9999, ou 0 se desconhecido.");
        }
        this.anoCriacao = anoCriacao;
    }

    /** Impede salvar um objeto incompleto criado pelo construtor padrão. */
    public void validar() {
        if (razaoSocial == null) {
            throw new IllegalArgumentException("Informe a razão social da desenvolvedora.");
        }
    }

    @Override
    public String toString() {
        return "Desenvolvedora{id=" + id
                + ", razaoSocial='" + razaoSocial + '\''
                + ", paisOrigem='" + (paisOrigem == null ? "não informado" : paisOrigem) + '\''
                + ", anoCriacao=" + (anoCriacao == 0 ? "não informado" : anoCriacao) + '}';
    }
}
