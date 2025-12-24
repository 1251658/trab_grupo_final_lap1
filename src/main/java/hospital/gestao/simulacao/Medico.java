package hospital.gestao.simulacao;

public class Medico {
    private String nome;
    private Especialidade especialidade; // 🆕 Campo de Especialidade
    private int horaEntrada;
    private int horaSaidaConfigurada;
    private boolean emServico;

    // 🆕 NOVO CAMPO: Necessário para o cálculo de salários (Aluno 2)
    private double valorHora;

    private int unidadesTrabalhadasSeguidas;
    private int unidadesEmDescanso;

    // 🆕 Estatística: Total acumulado no dia (não reseta com descanso)
    private int totalUnidadesTrabalhadas;

    private static final int HORAS_PARA_DESCANSO = 5;
    private static final int UNIDADES_DE_DESCANSO_REQUERIDAS = 1;

    // 🆕 CONSTRUTOR ATUALIZADO: Recebe agora a Especialidade e o valorHora
    public Medico(String nome, Especialidade especialidade, int entrada, int saida, double valorHora) {
        this.nome = nome;
        this.especialidade = especialidade; // Inicializa a especialidade
        this.horaEntrada = entrada;
        this.horaSaidaConfigurada = saida;
        this.valorHora = valorHora;
        this.emServico = false;
        this.unidadesTrabalhadasSeguidas = 0;
        this.unidadesEmDescanso = 0;
        this.totalUnidadesTrabalhadas = 0;
    }

    // getters
    public String getNome() {
        return nome;
    }

    // 🆕 Getter para Especialidade
    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public boolean estaEmServico() {
        return emServico;
    }

    public boolean estaEmDescanso() {
        return unidadesEmDescanso > 0;
    }

    public int getHoraSaidaConfigurada() {
        return horaSaidaConfigurada;
    }

    // 🆕 NOVO GETTER: Necessário para o cálculo do Aluno 2
    public double getValorHora() {
        return valorHora;
    }

    public int getTotalUnidadesTrabalhadas() {
        return totalUnidadesTrabalhadas;
    }

    // setters
    public void setEmServico(boolean emServico) {
        this.emServico = emServico;

        // correcao descanco
        if (!emServico && unidadesTrabalhadasSeguidas >= HORAS_PARA_DESCANSO) {
            iniciarDescansoObrigatorio();
        }
    }

    // 🆕 Setters para restauração de estado (Persistência)
    public void setUnidadesTrabalhadasSeguidas(int unidades) {
        this.unidadesTrabalhadasSeguidas = unidades;
    }

    public void setUnidadesEmDescanso(int unidades) {
        this.unidadesEmDescanso = unidades;
    }

    public void setTotalUnidadesTrabalhadas(int total) {
        this.totalUnidadesTrabalhadas = total;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    // disponibilidade
    public boolean isDisponivel(int horaAtual) {
        // PRIORIDADE 1: Não disponível se estiver em descanso ou precisa descansar
        if (estaEmDescanso() || (unidadesTrabalhadasSeguidas >= HORAS_PARA_DESCANSO && !emServico)) {
            iniciarDescansoObrigatorio(); // Garante que o descanso inicia se o status for 'precisa descansar'
            return false;
        }

        // priori 2
        if (emServico) {
            return false;
        }

        // priori 3
        if (horaAtual < horaEntrada || horaAtual > horaSaidaConfigurada) {
            return false;
        }

        return true;
    }

    // metodo auxiliar
    private void iniciarDescansoObrigatorio() {
        if (unidadesEmDescanso == 0) {
            unidadesEmDescanso = UNIDADES_DE_DESCANSO_REQUERIDAS;
            System.out.println("⚠️ ALERTA DESCANSO: Médico " + nome + " ("
                    + (especialidade != null ? especialidade.getNome() : "Sem Esp.") + ") atingiu "
                    + HORAS_PARA_DESCANSO + " horas e INICIOU " + UNIDADES_DE_DESCANSO_REQUERIDAS
                    + " un. de descanso.");
        }
    }

    /**
     * Processa a passagem de uma unidade de tempo.
     */
    public void aplicarLogicaTempo(int horaAtual) {

        if (isHorarioTrabalho(horaAtual) || emServico) {

            // logica descanso
            if (unidadesEmDescanso > 0) {
                unidadesEmDescanso--;
                System.out.println("💤 Médico " + nome + " está descansando. Faltam " + unidadesEmDescanso + " un.");
                if (unidadesEmDescanso == 0) {
                    unidadesTrabalhadasSeguidas = 0;
                }
                return; // Impede contagem de horas trabalhadas se estiver descansando
            }

            // contar horas trabalhadas
            if (emServico) {
                unidadesTrabalhadasSeguidas++;
                totalUnidadesTrabalhadas++; // 🆕 Incrementa o total
            }
        }

        if (horaAtual > horaSaidaConfigurada && !emServico) {
            System.out.println("👋 Médico " + nome + " saiu do hospital (fora de serviço).");
        }
    }

    private boolean isHorarioTrabalho(int horaAtual) {
        return horaAtual >= horaEntrada && horaAtual <= horaSaidaConfigurada;
    }

    // 🆕 ATUALIZAÇÃO DO toCSV(): Inclui o código da especialidade
    /**
     * Formata o estado atual do Medico para uma linha CSV.
     * Campos:
     * nome;codEspecialidade;horaEntrada;horaSaidaConfigurada;unidadesTrabalhadasSeguidas;unidadesEmDescanso;valorHora
     */
    public String toCSV() {
        String sep = Configuracao.SEPARADOR;
        String codEsp = (especialidade != null) ? especialidade.getCodigo() : "N/A";
        return nome + sep +
                codEsp + sep +
                horaEntrada + sep +
                horaSaidaConfigurada + sep +
                unidadesTrabalhadasSeguidas + sep +
                unidadesEmDescanso + sep +
                totalUnidadesTrabalhadas;
    }
}