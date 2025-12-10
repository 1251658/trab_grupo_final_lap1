package hospital.gestao.simulacao;

public class Medico {
    private String nome;
    private int horaEntrada;
    private int horaSaidaConfigurada;
    private boolean emServico;

    private int unidadesTrabalhadasSeguidas;
    private int unidadesEmDescanso;

    private static final int HORAS_PARA_DESCANSO = 5;
    private static final int UNIDADES_DE_DESCANSO_REQUERIDAS = 1;

    public Medico(String nome, int entrada, int saida) {
        this.nome = nome;
        this.horaEntrada = entrada;
        this.horaSaidaConfigurada = saida;
        this.emServico = false;
        this.unidadesTrabalhadasSeguidas = 0;
        this.unidadesEmDescanso = 0;
    }

    // getters
    public String getNome() { return nome; }
    public boolean estaEmServico() { return emServico; }
    public boolean estaEmDescanso() { return unidadesEmDescanso > 0; }
    public int getHoraSaidaConfigurada() { return horaSaidaConfigurada; }

    // setters
    public void setEmServico(boolean emServico) {
        this.emServico = emServico;

        // correcao descanco
        if (!emServico && unidadesTrabalhadasSeguidas >= HORAS_PARA_DESCANSO) {
            iniciarDescansoObrigatorio();
        }
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
            System.out.println("⚠️ ALERTA DESCANSO: Médico " + nome + " atingiu " + HORAS_PARA_DESCANSO + " horas e INICIOU " + UNIDADES_DE_DESCANSO_REQUERIDAS + " un. de descanso.");
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

            //contar horas trabalhadas
            if (emServico) {
                unidadesTrabalhadasSeguidas++;
            }
        }

        if (horaAtual > horaSaidaConfigurada && !emServico) {
            System.out.println("👋 Médico " + nome + " saiu do hospital (fora de serviço).");
        }
    }

    private boolean isHorarioTrabalho(int horaAtual) {
        return horaAtual >= horaEntrada && horaAtual <= horaSaidaConfigurada;
    }
}