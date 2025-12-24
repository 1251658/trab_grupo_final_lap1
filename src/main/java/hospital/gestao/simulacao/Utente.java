package hospital.gestao.simulacao;

import hospital.gestao.estruturas.Lista;

/**
 * Representa um paciente no hospital.
 */
public class Utente {

    private String nome;
    private Lista<Sintoma> sintomas; // 🆕 Lista de Sintomas
    private Especialidade especialidadeAtribuida; // 🆕 Especialidade calculada

    private String nivelUrgencia; // Calculado com base no pior sintoma
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

    // 🆕 Construtor com Sintomas
    public Utente(String nome, Lista<Sintoma> sintomas) {
        this.nome = nome;
        this.sintomas = sintomas;
        this.unidadesEmEspera = 0;
        this.emConsulta = false;
        this.duracaoConsulta = 0;
        this.tempoRestanteConsulta = 0;

        realizarTriagemAutomatica(); // Calcula urgência e especialidade
        iniciarConsulta(); // Define duracaoConsulta de base (mesmo sem estar em consulta)
    }

    // 🆕 Construtor de PERSISTÊNCIA (Usado pelo GestorFicheiros)
    public Utente(String nome, String nivelUrgencia, int unidadesEmEspera, boolean emConsulta,
            int tempoRestanteConsulta, String codEspecialidade) {
        this.nome = nome;
        this.sintomas = new Lista<>(); // Vazio por enquanto ao carregar do CSV legado
        this.nivelUrgencia = nivelUrgencia;
        this.unidadesEmEspera = unidadesEmEspera;
        this.emConsulta = emConsulta;
        this.tempoRestanteConsulta = tempoRestanteConsulta;

        // Reconstituir Especialidade
        if (codEspecialidade != null && !codEspecialidade.equals("N/A")) {
            this.especialidadeAtribuida = new Especialidade(codEspecialidade, "Especialidade " + codEspecialidade);
        } else {
            this.especialidadeAtribuida = null;
        }

        // Define durações baseadas na urgência carregada
        // Não chamamos iniciarConsulta() aqui pois resetaria unidadesEmEspera
        if (BAIXA.equals(nivelUrgencia)) {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_BAIXA;
        } else if (MEDIA.equals(nivelUrgencia)) {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_MEDIA;
        } else if (URGENTE.equals(nivelUrgencia)) {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_URGENTE;
        } else {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_BAIXA;
        }
    }

    public Lista<Sintoma> getSintomas() {
        return sintomas;
    }

    // 🆕 Método de Triagem
    public void realizarTriagemAutomatica() {
        // 1. Calcular Nível de Urgência (Pior cenário vence)
        int piorNivel = 0; // 0=Baixa, 1=Média, 2=Urgente

        for (int i = 0; i < sintomas.tamanho(); i++) {
            Sintoma s = sintomas.obter(i);
            int nivelSintoma = converterNivel(s.getNivelUrgencia());
            if (nivelSintoma > piorNivel) {
                piorNivel = nivelSintoma;
            }
        }

        this.nivelUrgencia = desconverterNivel(piorNivel);

        // 2. Calcular Especialidade com base na frequência ("Moda")
        // Filtra apenas sintomas do nível mais grave
        this.especialidadeAtribuida = null;
        Lista<Especialidade> candidatas = new Lista<>();

        for (int i = 0; i < sintomas.tamanho(); i++) {
            Sintoma s = sintomas.obter(i);
            if (converterNivel(s.getNivelUrgencia()) == piorNivel) {
                if (!s.getEspecialidadesAssociadas().vazia()) {
                    // Adiciona todas as especialidades associadas deste sintoma urgente
                    for (int k = 0; k < s.getEspecialidadesAssociadas().tamanho(); k++) {
                        candidatas.adicionar(s.getEspecialidadesAssociadas().obter(k));
                    }
                }
            }
        }

        // Encontrar a mais frequente na lista de candidatas
        if (!candidatas.vazia()) {
            Especialidade maisFrequente = null;
            int maxContagem = -1;

            for (int i = 0; i < candidatas.tamanho(); i++) {
                Especialidade atual = candidatas.obter(i);
                int contagem = 0;
                // Contar quantas vezes 'atual' aparece (por código para ser seguro)
                for (int j = 0; j < candidatas.tamanho(); j++) {
                    if (candidatas.obter(j).getCodigo().equals(atual.getCodigo())) {
                        contagem++;
                    }
                }

                if (contagem > maxContagem) {
                    maxContagem = contagem;
                    maisFrequente = atual;
                }
            }
            this.especialidadeAtribuida = maisFrequente;
        }
    }

    private int converterNivel(String nivel) {
        if (URGENTE.equals(nivel))
            return 2;
        if (MEDIA.equals(nivel))
            return 1;
        return 0; // Baixa
    }

    private String desconverterNivel(int valor) {
        if (valor == 2)
            return URGENTE;
        if (valor == 1)
            return MEDIA;
        return BAIXA;
    }

    // --- Getters ---
    public String getNome() {
        return nome;
    }

    public String getNivelUrgencia() {
        return nivelUrgencia;
    }

    public boolean estaEmConsulta() {
        return emConsulta;
    }

    public int getUnidadesEmEspera() {
        return unidadesEmEspera;
    }

    public int getDuracaoConsulta() {
        return duracaoConsulta;
    }

    public Especialidade getEspecialidade() {
        return especialidadeAtribuida;
    }

    // setters
    public void setEmConsulta(boolean emConsulta) {
        this.emConsulta = emConsulta;
    }

    // 🆕 NOVO SETTER: Necessário para carregar o estado 'unidadesEmEspera' do CSV
    public void setUnidadesEmEspera(int unidadesEmEspera) {
        this.unidadesEmEspera = unidadesEmEspera;
    }

    // 🆕 NOVO SETTER: Necessário para carregar o estado 'tempoRestanteConsulta' do
    // CSV
    public void setTempoRestanteConsulta(int tempoRestanteConsulta) {
        this.tempoRestanteConsulta = tempoRestanteConsulta;
    }

    // logica prioridade e progressao

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
     * Requisitos: Baixa->Média (3 un.), Média->Urgente (3 un.), Urgente->Saída (2
     * un.).
     * 
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

    // gestão de consultas

    /**
     * Inicia a consulta, define o tempo de duração e zera o tempo de espera.
     */
    public void iniciarConsulta() {
        this.emConsulta = true;
        this.unidadesEmEspera = 0;

        // ATUALIZAÇÃO: Usar a classe Configuracao para tempos dinâmicos
        if (BAIXA.equals(nivelUrgencia)) {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_BAIXA;
        } else if (MEDIA.equals(nivelUrgencia)) {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_MEDIA;
        } else if (URGENTE.equals(nivelUrgencia)) {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_URGENTE;
        } else {
            this.duracaoConsulta = Configuracao.TEMPO_CONSULTA_BAIXA; // Default
        }
        this.tempoRestanteConsulta = this.duracaoConsulta;
    }

    /**
     * Reduz o tempo restante da consulta em uma unidade.
     * 
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

    // 🆕 toCSV() (Não precisa de alteração)
    /**
     * Formata o estado atual do Utente para uma linha CSV.
     * Campos:
     * nome;nivelUrgencia;unidadesEmEspera;emConsulta;tempoRestanteConsulta;codEspecialidade
     */
    public String toCSV() {
        String sep = Configuracao.SEPARADOR;
        String codEsp = (especialidadeAtribuida != null) ? especialidadeAtribuida.getCodigo() : "N/A";
        return nome + sep +
                nivelUrgencia + sep +
                unidadesEmEspera + sep +
                emConsulta + sep +
                tempoRestanteConsulta + sep +
                codEsp;
    }
}