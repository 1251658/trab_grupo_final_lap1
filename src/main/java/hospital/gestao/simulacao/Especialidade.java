package hospital.gestao.simulacao;

public class Especialidade {
    private String codigo;
    private String nome;

    public Especialidade(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome + " (" + codigo + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Especialidade that = (Especialidade) obj;
        return codigo != null ? codigo.equals(that.codigo) : that.codigo == null;
    }
}
