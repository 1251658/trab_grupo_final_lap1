package hospital.gestao.simulacao;

import hospital.gestao.estruturas.Lista;

public class Sintoma {
    private String nome;
    private String nivelUrgencia; // "Baixa", "Média" ou "Urgente"
    private Lista<Especialidade> especialidadesAssociadas;

    public Sintoma(String nome, String nivelUrgencia) {
        this.nome = nome;
        this.nivelUrgencia = nivelUrgencia;
        this.especialidadesAssociadas = new Lista<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNivelUrgencia() {
        return nivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }

    public Lista<Especialidade> getEspecialidadesAssociadas() {
        return especialidadesAssociadas;
    }

    public void adicionarEspecialidade(Especialidade especialidade) {
        if (especialidade != null) {
            this.especialidadesAssociadas.adicionar(especialidade);
        }
    }

    public String toCSV() {
        String cod = "N/A";
        if (!especialidadesAssociadas.vazia()) {
            cod = especialidadesAssociadas.obter(0).getCodigo();
        }
        return nome + Configuracao.SEPARADOR + nivelUrgencia + Configuracao.SEPARADOR + cod;
    }

    @Override
    public String toString() {
        return nome + " [" + nivelUrgencia + "]";
    }
}
