package hospital.gestao.simulacao;

import hospital.gestao.estruturas.Lista;

public class Dia {

    private int horaAtual;
    private final int UNIDADES_POR_DIA = 24;

    private Lista<Utente> utentesEmEspera;
    private Lista<Medico> medicosAtivos;
    private int horaSimulacao;

    // 🆕 Estatísticas
    private int totalUtentesAtendidos;
    private Lista<String> historicoEspecialidades; // Lista de códigos de especialidades atendidas
    private java.util.Map<String, Integer> statsSintomas; // Contagem de sintomas

    // inicializa o dia
    public Dia(Lista<Utente> utentesEmEspera, Lista<Medico> medicosAtivos) {
        this.horaAtual = 1;
        this.utentesEmEspera = utentesEmEspera;
        this.medicosAtivos = medicosAtivos;
        this.totalUtentesAtendidos = 0;
        this.historicoEspecialidades = new Lista<>();
        this.statsSintomas = new java.util.HashMap<>();
        System.out.println("Simulação do Dia iniciada. Hora Atual: " + horaAtual);
    }

    public void registrarSintomasDoUtente(Utente u) {
        Lista<Sintoma> lista = u.getSintomas();
        if (lista == null)
            return;
        for (int i = 0; i < lista.tamanho(); i++) {
            String nome = lista.obter(i).getNome();
            statsSintomas.put(nome, statsSintomas.getOrDefault(nome, 0) + 1);
        }
    }

    public java.util.Map<String, Integer> getStatsSintomas() {
        return statsSintomas;
    }

    public int getTotalUtentesAtendidos() {
        return totalUtentesAtendidos;
    }

    public Lista<String> getHistoricoEspecialidades() {
        return historicoEspecialidades;
    }

    // logica avanço
    public void avancarUnidadeTempo() {
        // aumenta a hora e verifica se o dia terminou
        horaAtual++;

        if (horaAtual > UNIDADES_POR_DIA) {
            horaAtual = 1; // recomeca da unidade de tempo 1
            System.out.println("\n*** O ciclo de 24 unidades de tempo terminou! O dia recomeçou. ***");
        }

        horaSimulacao = horaAtual;

        System.out.println("\n==============================================");
        System.out.println("⌚ AVANÇO DE TEMPO: Hora Atual = " + horaSimulacao + "/" + UNIDADES_POR_DIA);
        System.out.println("==============================================");

        // Processar o tempo dos médicos (descanso/horário)
        aplicarLogicaTempoMedicos();

        // 1. Verifica a disponibilidade (informa sobre médicos que saíram de
        // descanso/entraram no turno)
        verificarDisponibilidadeMedicos();

        // 2. Aplica a progressão de urgência
        aplicarProgressaoUrgencia();

        // 3. Tenta alocar utentes aos médicos disponíveis
        alocarUtentesAosMedicos();

        // 4. Só liberta o médico e utente DEPOIS de todos os outros processos.
        aplicarLogicaConsultaESaida();
    }

    // metodos auxiliares

    // simular fim consulta
    private void aplicarLogicaConsultaESaida() {
        // Iteração com index reverso ou controle de indice é melhor para remoção,
        // mas aqui vamos usar o controle de indice 'i'
        for (int i = 0; i < utentesEmEspera.tamanho(); i++) {
            Utente utente = utentesEmEspera.obter(i);

            if (utente.estaEmConsulta()) {
                boolean consultaTerminada = utente.aplicarLogicaConsulta();

                if (consultaTerminada) {
                    // Liberta o primeiro médico em serviço (limitação estrutural temporária)
                    libertarPrimeiroMedicoEmServico();

                    // Remover da lista
                    utentesEmEspera.remover(i);
                    i--; // Ajusta o índice após remoção

                    // 🆕 Atualizar Estatísticas
                    totalUtentesAtendidos++;
                    if (utente.getEspecialidade() != null) {
                        historicoEspecialidades.adicionar(utente.getEspecialidade().getCodigo());
                    } else {
                        historicoEspecialidades.adicionar("GERAL/SEM_ESP");
                    }

                    System.out.println("➡️ Cliente " + utente.getNome() + " teve alta e saiu do hospital.");
                }
            }
        }
    }

    // liberar medico (libera o primeiro que encontrar em serviço)
    private void libertarPrimeiroMedicoEmServico() {
        for (int i = 0; i < medicosAtivos.tamanho(); i++) {
            Medico medico = medicosAtivos.obter(i);
            if (medico.estaEmServico()) {
                medico.setEmServico(false);
                System.out.println(
                        "🔔 NOTIFICAÇÃO: Dr. " + medico.getNome() + " terminou o serviço e está AGORA disponível.");
                return;
            }
        }
    }

    private void aplicarLogicaTempoMedicos() {
        System.out.println("--- Processamento de Médicos (Horários e Descanso) ---");

        for (int i = 0; i < medicosAtivos.tamanho(); i++) {
            medicosAtivos.obter(i).aplicarLogicaTempo(horaSimulacao);
        }
    }

    private void verificarDisponibilidadeMedicos() {
        System.out.println("--- Verificação de Disponibilidade ---");

        for (int i = 0; i < medicosAtivos.tamanho(); i++) {
            Medico medico = medicosAtivos.obter(i);
            if (medico.isDisponivel(horaSimulacao)) {
                System.out.println("✅ NOTIFICAÇÃO: Médico " + medico.getNome() + " está AGORA disponível.");
            } else if (medico.estaEmDescanso()) {
                // notificação descanso
            } else if (medico.getHoraSaidaConfigurada() < horaSimulacao && medico.estaEmServico()) {
                System.out.println(
                        "🔔 NOTIFICAÇÃO: Médico " + medico.getNome() + " está após o horário, mas AINDA em serviço.");
            }
        }
    }

    private void aplicarProgressaoUrgencia() {
        System.out.println("--- Progressão de Urgência dos Pacientes ---");

        for (int i = 0; i < utentesEmEspera.tamanho(); i++) {
            Utente utente = utentesEmEspera.obter(i);

            // so pacientes em espera progridem
            if (!utente.estaEmConsulta()) {
                boolean subiuNivel = utente.progredirUrgencia(horaSimulacao);

                if (subiuNivel) {
                    System.out.println("🚨 ALERTA: Cliente " + utente.getNome() + " avançou para nível: "
                            + utente.getNivelUrgencia() + "!");

                    // Tratamento de Paciente Crítico (Sai da Urgência)
                    if (utente.getNivelUrgencia().equals("Crítico/Saída")) {
                        utentesEmEspera.remover(i); // Remove o paciente que atingiu o nível máximo de urgência/saída
                        i--; // Ajusta o indice
                        System.out.println(
                                "🔥 Cliente " + utente.getNome() + " atingiu nível CRÍTICO e foi removido da espera.");
                    }
                }
            }
        }
    }

    private void alocarUtentesAosMedicos() {
        System.out.println("--- Tentativa de Alocação de Pacientes ---");

        // 📢 DIAGNÓSTICO: Inicial
        System.out.println(">>> INÍCIO DA ALOCAÇÃO: Utentes em fila = " + utentesEmEspera.tamanho());

        // ordenar utentes por prioridade(Urgente > Média > Baixa)
        utentesEmEspera.ordenar((u1, u2) -> u2.getPrioridade() - u1.getPrioridade());

        for (int i = 0; i < utentesEmEspera.tamanho(); i++) {
            Utente utente = utentesEmEspera.obter(i);

            if (utente.estaEmConsulta() || utente.getPrioridade() == 0) {
                // 📢 DIAGNÓSTICO: Identifica porque o utente foi ignorado
                System.out.println("DIAGNÓSTICO ALOCAÇÃO: Utente " + utente.getNome() + " ignorado (em consulta? "
                        + utente.estaEmConsulta() + " | Prioridade: " + utente.getPrioridade() + ")");
                continue;
            }

            Medico medicoAlocado = null;

            // encontrar medico disp
            for (int k = 0; k < medicosAtivos.tamanho(); k++) {
                Medico medico = medicosAtivos.obter(k);

                // Regra 1: Disponibilidade de Horário/Descanso
                if (!medico.isDisponivel(horaSimulacao)) {
                    continue;
                }

                // Regra 2: Correspondência de Especialidade
                Especialidade espUtente = utente.getEspecialidade();
                Especialidade espMedico = medico.getEspecialidade();

                // Se o utente tem especialidade definida, o médico TEM de ter a mesma.
                // Se o utente não tem (ex: legado), assumimos que qualquer médico serve (ou
                // Clínica Geral).
                if (espUtente != null) {
                    if (espMedico == null || !espMedico.getCodigo().equals(espUtente.getCodigo())) {
                        continue; // Especialidade não corresponde, procura outro médico
                    }
                }

                medicoAlocado = medico;
                break;
            }

            // atribuir e atualizar status
            if (medicoAlocado != null) {
                // começa a contagem do tempo de consulta(utente)
                utente.iniciarConsulta();

                // fica em serviço(med)
                medicoAlocado.setEmServico(true);

                // correcao getter
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
    public Lista<Utente> getUtentesEmEspera() {
        return utentesEmEspera;
    }

    // Getter para a lista de médicos ativos (necessário para persistência em
    // TesteSimulacao).
    public Lista<Medico> getMedicosAtivos() {
        return medicosAtivos;
    }
}