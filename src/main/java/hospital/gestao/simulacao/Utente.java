package hospital.gestao.simulacao;

/**
 * mock temp
 */
public class Utente {

    private String nome;
    private String nivelUrgencia;
    private int unidadesEmEspera;
    private boolean emConsulta;

    // VARIÁVEIS ADICIONADAS PARA GESTÃO DA CONSULTA (Aluno 3)
    private int duracaoConsulta; // Tempo total necessário (1, 2 ou 3 un.)
    private int tempoRestanteConsulta; // Tempo que falta para terminar

    // Níveis de Urgência
    private static final String BAIXA = "Baixa"; // Verde
    private static final String MEDIA = "Média"; // Laranja
    private static final String URGENTE = "Urgente"; // Vermelha
    private static final String SAIDA_CRITICA = "Crítico/Saída";

    public Utente(String nome, String nivelUrgenciaInicial) {
        this.nome = nome;
        this.nivelUrgencia = nivelUrgenciaInicial;
        this.unidadesEmEspera = 0;
        this.emConsulta = false;
        // Inicializa as variáveis de consulta (opcional, mas boa prática)
        this.duracaoConsulta = 0;
        this.tempoRestanteConsulta = 0;
    }

    // --- Getters ---
    public String getNome() { return nome; }
    public String getNivelUrgencia() { return nivelUrgencia; }
    public boolean estaEmConsulta() { return emConsulta; }
    public int getUnidadesEmEspera() { return unidadesEmEspera; }
    public int getDuracaoConsulta() { return duracaoConsulta; } // Getter adicionado

    // setters
    public void setEmConsulta(boolean emConsulta) {
        this.emConsulta = emConsulta;

    }


    //logica prioridade e progressao

    public int getPrioridade() {
        if (URGENTE.equals(nivelUrgencia)) {
            return 3;
        } else if (MEDIA.equals(nivelUrgencia)) {
            return 2;
        } else if (BAIXA.equals(nivelUrgencia)) {
            return 1;
        }
        return 0;
    }

    /**
     * Tenta progredir o nível de urgência com base no tempo de espera acumulado.
     * Requisitos: Baixa->Média (3 un.), Média->Urgente (3 un.), Urgente->Saída (2 un.).
     * @return true se o nível de urgência subiu, false caso contrário.
     */
    public boolean progredirUrgencia(int horaAtual) {
        if (emConsulta || SAIDA_CRITICA.equals(nivelUrgencia)) {
            return false;
        }

        unidadesEmEspera++;
        boolean progrediu = false;

        // 1. Baixa para Média: 3 unidades de tempo
        if (BAIXA.equals(nivelUrgencia) && unidadesEmEspera >= 3) {
            nivelUrgencia = MEDIA;
            unidadesEmEspera = 0;
            progrediu = true;
            // 2. Média para Urgente: 3 unidades de tempo
        } else if (MEDIA.equals(nivelUrgencia) && unidadesEmEspera >= 3) {
            nivelUrgencia = URGENTE;
            unidadesEmEspera = 0;
            progrediu = true;
            // 3. Urgente para Saída/Crítico: 2 unidades de tempo
        } else if (URGENTE.equals(nivelUrgencia) && unidadesEmEspera >= 2) {
            nivelUrgencia = SAIDA_CRITICA;
            unidadesEmEspera = 0;
            progrediu = true;
        }

        return progrediu;
    }


    //gestão de consultas

    /**
     * Inicia a consulta, define o tempo de duração e zera o tempo de espera.
     */
    public void iniciarConsulta() {
        this.emConsulta = true;
        this.unidadesEmEspera = 0;

        // Configura a duração com base no nível de urgência atual
        if (BAIXA.equals(nivelUrgencia)) {
            this.duracaoConsulta = 1; // Baixa: 1 un.
        } else if (MEDIA.equals(nivelUrgencia)) {
            this.duracaoConsulta = 2; // Média: 2 un.
        } else if (URGENTE.equals(nivelUrgencia)) {
            this.duracaoConsulta = 3; // Urgente: 3 un.
        } else {
            this.duracaoConsulta = 1; // Default
        }
        this.tempoRestanteConsulta = this.duracaoConsulta;
    }

    /**
     * Reduz o tempo restante da consulta em uma unidade.
     * @return true se a consulta terminou.
     */
    public boolean aplicarLogicaConsulta() {
        if (emConsulta) {
            tempoRestanteConsulta--;
            if (tempoRestanteConsulta <= 0) {
                return true; // Consulta terminada
            }
        }
        return false;
    }
}