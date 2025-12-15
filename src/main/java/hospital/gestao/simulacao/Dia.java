package hospital.gestao.simulacao;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Dia {

    private int horaAtual;
    private final int UNIDADES_POR_DIA = 24;

    private List<Utente> utentesEmEspera;
    private List<Medico> medicosAtivos;
    private int horaSimulacao;

    //inicializa o dia
    public Dia(List<Utente> utentesEmEspera, List<Medico> medicosAtivos) {
        this.horaAtual = 1;
        this.utentesEmEspera = utentesEmEspera;
        this.medicosAtivos = medicosAtivos;
        System.out.println("Simulação do Dia iniciada. Hora Atual: " + horaAtual);
    }

    //logica avanço
    public void avancarUnidadeTempo() {
        //aumenta a hora e verifica se o dia terminou
        horaAtual++;

        if (horaAtual > UNIDADES_POR_DIA) {
            horaAtual = 1; //recomeca da unidade de tempo 1
            System.out.println("\n*** O ciclo de 24 unidades de tempo terminou! O dia recomeçou. ***");
        }

        horaSimulacao = horaAtual;

        System.out.println("\n==============================================");
        System.out.println("⌚ AVANÇO DE TEMPO: Hora Atual = " + horaSimulacao + "/" + UNIDADES_POR_DIA);
        System.out.println("==============================================");

        // Processar o tempo dos médicos (descanso/horário)
        aplicarLogicaTempoMedicos();

        // 1. Verifica a disponibilidade (informa sobre médicos que saíram de descanso/entraram no turno)
        verificarDisponibilidadeMedicos();

        // 2. Aplica a progressão de urgência
        aplicarProgressaoUrgencia();

        // 3. Tenta alocar utentes aos médicos disponíveis
        alocarUtentesAosMedicos();

        // 4. Só liberta o médico e utente DEPOIS de todos os outros processos.
        aplicarLogicaConsultaESaida();
    }

    //metodos auxiliares

    //simular fim consulta
    private void aplicarLogicaConsultaESaida() {
        Iterator<Utente> iterator = utentesEmEspera.iterator();

        while (iterator.hasNext()) {
            Utente utente = iterator.next();

            if (utente.estaEmConsulta()) {
                boolean consultaTerminada = utente.aplicarLogicaConsulta();

                if (consultaTerminada) {
                    // Liberta o primeiro médico em serviço (limitação estrutural temporária)
                    libertarPrimeiroMedicoEmServico();

                    //remover o utente da lista (saída do hospital)
                    iterator.remove();
                    System.out.println("➡️ Cliente " + utente.getNome() + " teve alta e saiu do hospital.");
                }
            }
        }
    }

    // liberar medico (libera o primeiro que encontrar em serviço)
    private void libertarPrimeiroMedicoEmServico() {
        for (Medico medico : medicosAtivos) {
            if (medico.estaEmServico()) {
                medico.setEmServico(false);
                System.out.println("🔔 NOTIFICAÇÃO: Dr. " + medico.getNome() + " terminou o serviço e está AGORA disponível.");
                return;
            }
        }
    }

    private void aplicarLogicaTempoMedicos() {
        System.out.println("--- Processamento de Médicos (Horários e Descanso) ---");

        for (Medico medico : medicosAtivos) {
            medico.aplicarLogicaTempo(horaSimulacao);
        }
    }

    private void verificarDisponibilidadeMedicos() {
        System.out.println("--- Verificação de Disponibilidade ---");

        for (Medico medico : medicosAtivos) {
            if (medico.isDisponivel(horaSimulacao)) {
                System.out.println("✅ NOTIFICAÇÃO: Médico " + medico.getNome() + " está AGORA disponível.");
            } else if (medico.estaEmDescanso()) {
                // notificação descanso
            } else if (medico.getHoraSaidaConfigurada() < horaSimulacao && medico.estaEmServico()) {
                System.out.println("🔔 NOTIFICAÇÃO: Médico " + medico.getNome() + " está após o horário, mas AINDA em serviço.");
            }
        }
    }

    private void aplicarProgressaoUrgencia() {
        System.out.println("--- Progressão de Urgência dos Pacientes ---");

        Iterator<Utente> iterator = utentesEmEspera.iterator();
        while (iterator.hasNext()) {
            Utente utente = iterator.next();

            //so pacientes em espera progridem
            if (!utente.estaEmConsulta()) {
                boolean subiuNivel = utente.progredirUrgencia(horaSimulacao);

                if (subiuNivel) {
                    System.out.println("🚨 ALERTA: Cliente " + utente.getNome() + " avançou para nível: " + utente.getNivelUrgencia() + "!");

                    // Tratamento de Paciente Crítico (Sai da Urgência)
                    if (utente.getNivelUrgencia().equals("Crítico/Saída")) {
                        iterator.remove(); // Remove o paciente que atingiu o nível máximo de urgência/saída
                        System.out.println("🔥 Cliente " + utente.getNome() + " atingiu nível CRÍTICO e foi removido da espera.");
                    }
                }
            }
        }
    }

    private void alocarUtentesAosMedicos() {
        System.out.println("--- Tentativa de Alocação de Pacientes ---");

        // 📢 DIAGNÓSTICO: Inicial
        System.out.println(">>> INÍCIO DA ALOCAÇÃO: Utentes em fila = " + utentesEmEspera.size());

        //ordenar utentes por prioridade(Urgente > Média > Baixa)
        utentesEmEspera.sort((u1, u2) -> u2.getPrioridade() - u1.getPrioridade());

        for (Utente utente : utentesEmEspera) {

            if (utente.estaEmConsulta() || utente.getPrioridade() == 0) {
                // 📢 DIAGNÓSTICO: Identifica porque o utente foi ignorado
                System.out.println("DIAGNÓSTICO ALOCAÇÃO: Utente " + utente.getNome() + " ignorado (em consulta? " + utente.estaEmConsulta() + " | Prioridade: " + utente.getPrioridade() + ")");
                continue;
            }

            Medico medicoAlocado = null;

            //encontrar medico disp
            for (Medico medico : medicosAtivos) {
                if (medico.isDisponivel(horaSimulacao)) {
                    medicoAlocado = medico;
                    break;
                }
            }

            //atribuir e atualizar status
            if (medicoAlocado != null) {
                //começa a contagem do tempo de consulta(utente)
                utente.iniciarConsulta();

                //fica em serviço(med)
                medicoAlocado.setEmServico(true);

                //correcao getter
                System.out.println("✅ ALOCAÇÃO EFETUADA: Cliente " + utente.getNome() +
                        " (" + utente.getNivelUrgencia() + ") alocado ao Dr. " + medicoAlocado.getNome() +
                        ". Duração prevista: " + utente.getDuracaoConsulta() + " un.");

            } else {
                System.out.println("DIAGNÓSTICO ALOCAÇÃO: Sem médicos disponíveis para " + utente.getNome());
            }
        }
    }

    public int getHoraAtual() {
        return horaAtual;
    }

    // Getter para a lista de utentes em espera.
    public List<Utente> getUtentesEmEspera() {
        return utentesEmEspera;
    }

    // Getter para a lista de médicos ativos (necessário para persistência em TesteSimulacao).
    public List<Medico> getMedicosAtivos() {
        return medicosAtivos;
    }
}