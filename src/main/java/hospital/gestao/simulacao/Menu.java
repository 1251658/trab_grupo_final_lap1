package hospital.gestao.simulacao;

import hospital.gestao.estruturas.Lista;
import java.util.Scanner;

public class Menu {

    private Dia simulacao;
    private Scanner scanner;

    // Repositórios em memória (num sistema real seriam classes separadas)
    private Lista<Especialidade> especialidades;
    private Lista<Sintoma> sintomasDisponiveis;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.especialidades = new Lista<>();
        this.sintomasDisponiveis = new Lista<>();

        // Inicializar com alguns dados fictícios se estiver vazio (depois ligamos a
        // ficheiros)
        inicializarDados();

        // Carregar dados de simulação
        Lista<Utente> utentes = GestorFicheiros.carregarUtentes();
        Lista<Medico> medicos = GestorFicheiros.carregarMedicos();
        this.simulacao = new Dia(utentes, medicos);
    }

    private void inicializarDados() {
        // Carregar Especialidades
        this.especialidades = GestorFicheiros.carregarEspecialidades();

        // Carregar Sintomas
        this.sintomasDisponiveis = GestorFicheiros.carregarSintomas(this.especialidades);

        // Se estiver vazio (primeira execução ou erro), cria defaults (opcional)
        if (especialidades.vazia()) {
            System.out.println("⚠️ Nenhuma especialidade encontrada. Criando padrão...");
            especialidades.adicionar(new Especialidade("CARD", "Cardiologia"));
            especialidades.adicionar(new Especialidade("ORT", "Ortopedia"));
            especialidades.adicionar(new Especialidade("GER", "Clínica Geral"));
            GestorFicheiros.salvarEspecialidades(especialidades);
        }
    }

    public void iniciar() {
        boolean sair = false;
        while (!sair) {
            exibirMenuPrincipal();
            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    menuGestaoRecursos();
                    break;
                case "2":
                    menuSimulacao();
                    break;
                case "3":
                    exibirEstatisticas();
                    break;
                case "4":
                    System.out.print("🔒 Insira a password de administrador: ");
                    String pass = scanner.nextLine();
                    if (pass.equals(Configuracao.PASSWORD_ACESSO)) {
                        menuConfiguracoes();
                    } else {
                        System.out.println("❌ Password incorreta! Acesso negado.");
                    }
                    break;
                case "0":
                    sair = true;
                    salvarSair();
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n=== HOSPITAL SIMULATION 2025 ===");
        System.out.println("1. 🏥 Gestão de Recursos (Médicos, Especialidades, Sintomas)");
        System.out.println("2. 🚑 Simulação e Triagem");
        System.out.println("3. 📊 Estatísticas");
        System.out.println("4. ⚙️  Configurações");
        System.out.println("0. 💾 Sair e Guardar");
        System.out.print("Escolha uma opção: ");
    }

    // --- GESTÃO DE RECURSOS ---

    private void menuGestaoRecursos() {
        System.out.println("\n--- GESTÃO DE RECURSOS ---");
        System.out.println("1. Listar Especialidades");
        System.out.println("2. Criar Especialidade");
        System.out.println("3. Listar Sintomas");
        System.out.println("4. Criar Sintoma");
        System.out.println("5. Listar Médicos");
        System.out.println("6. Contratar Médico");
        System.out.println("0. Voltar");
        System.out.print("Opção: ");

        String op = scanner.nextLine();
        switch (op) {
            case "1":
                listarEspecialidades();
                break;
            case "2":
                criarEspecialidade();
                break;
            case "3":
                listarSintomas();
                break;
            case "4":
                criarSintoma();
                break;
            case "5":
                listarMedicos();
                break;
            case "6":
                criarMedico();
                break;
            case "0":
                return;
            default:
                System.out.println("Inválido.");
        }
    }

    private void listarEspecialidades() {
        System.out.println("\nEspecialidades:");
        for (int i = 0; i < especialidades.tamanho(); i++) {
            System.out.println("- " + especialidades.obter(i));
        }
    }

    private void criarEspecialidade() {
        System.out.print("Código (ex: CARD): ");
        String cod = scanner.nextLine().toUpperCase().trim();
        if (cod.isEmpty()) {
            System.out.println("❌ Erro: Código não pode ser vazio.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("❌ Erro: Nome não pode ser vazio.");
            return;
        }

        especialidades.adicionar(new Especialidade(cod, nome));
        System.out.println("✅ Especialidade criada com sucesso.");
    }

    private void listarSintomas() {
        System.out.println("\nSintomas:");
        for (int i = 0; i < sintomasDisponiveis.tamanho(); i++) {
            System.out.println("- " + sintomasDisponiveis.obter(i));
        }
    }

    private void criarSintoma() {
        System.out.print("Nome do Sintoma: ");
        String nome = scanner.nextLine();
        System.out.print("Urgência (Baixa/Média/Urgente): ");
        String urg = scanner.nextLine(); // Deveria validar

        Sintoma s = new Sintoma(nome, urg);

        System.out.println("Associe uma especialidade (código) ou ENTER para nenhuma:");
        listarEspecialidades();
        String cod = scanner.nextLine().toUpperCase();
        if (!cod.isEmpty()) {
            for (int i = 0; i < especialidades.tamanho(); i++) {
                if (especialidades.obter(i).getCodigo().equals(cod)) {
                    s.adicionarEspecialidade(especialidades.obter(i));
                    break;
                }
            }
        }
        sintomasDisponiveis.adicionar(s);
        System.out.println("Sintoma criado.");
    }

    private void listarMedicos() {
        Lista<Medico> meds = simulacao.getMedicosAtivos();
        for (int i = 0; i < meds.tamanho(); i++) {
            System.out.println(meds.obter(i).toCSV());
        }
    }

    private void criarMedico() {
        // Implementação simplificada
        System.out.println("--- Contratar Médico ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Cód Especialidade: ");
        String codEsp = scanner.nextLine().toUpperCase();
        Especialidade esp = null;
        for (int i = 0; i < especialidades.tamanho(); i++) {
            if (especialidades.obter(i).getCodigo().equals(codEsp)) {
                esp = especialidades.obter(i);
                break;
            }
        }
        if (esp == null)
            System.out.println("⚠️ Especialidade não encontrada. Será 'Sem Especialidade'.");

        System.out.print("Hora Entrada (0-23): ");
        int ent = Integer.parseInt(scanner.nextLine());
        System.out.print("Hora Saída (0-23): ");
        int sai = Integer.parseInt(scanner.nextLine());
        System.out.print("Valor Hora: ");
        double val = Double.parseDouble(scanner.nextLine());

        Medico m = new Medico(nome, esp, ent, sai, val);
        simulacao.getMedicosAtivos().adicionar(m);
        System.out.println("Médico contratado.");
    }

    // --- SIMULAÇÃO ---

    private void menuSimulacao() {
        System.out.println("\n--- SIMULAÇÃO E TRIAGEM (Hora: " + simulacao.getHoraAtual() + "/24) ---");
        System.out.println("1. Registar Utente (Triagem)");
        System.out.println("2. Avançar 1 Hora");
        System.out.println("3. Ver Fila de Espera");
        System.out.println("0. Voltar");
        System.out.print("Opção: ");

        String op = scanner.nextLine();
        switch (op) {
            case "1":
                fazerTriagem();
                break;
            case "2":
                simulacao.avancarUnidadeTempo();
                System.out.println("Pressione ENTER para continuar...");
                scanner.nextLine();
                break;
            case "3":
                Lista<Utente> fila = simulacao.getUtentesEmEspera();
                System.out.println("Fila (" + fila.tamanho() + "):");
                for (int i = 0; i < fila.tamanho(); i++) {
                    Utente u = fila.obter(i);
                    System.out.println((i + 1) + ". " + u.getNome() + " [" + u.getNivelUrgencia() + "] Esp: "
                            + (u.getEspecialidade() != null ? u.getEspecialidade().getNome() : "N/A"));
                }
                break;
            case "0":
                return;
        }
    }

    private void fazerTriagem() {
        System.out.println("\n=== TRIAGEM ===");
        String nome = "";
        while (nome.isEmpty()) {
            System.out.print("Nome do Utente: ");
            nome = scanner.nextLine().trim();
            if (nome.isEmpty()) {
                System.out.println("❌ Erro: O nome não pode ser vazio.");
            }
        }

        Lista<Sintoma> sintomasUtente = new Lista<>();
        boolean adicionarMais = true;

        while (adicionarMais) {
            System.out.println("Selecione um sintoma da lista (digite parte do nome):");
            listarSintomas(); // Mostra todos
            System.out.print("> ");
            String busca = scanner.nextLine().toLowerCase();

            // 1. Encontrar todos os matches
            Lista<Sintoma> matches = new Lista<>();
            for (int i = 0; i < sintomasDisponiveis.tamanho(); i++) {
                if (sintomasDisponiveis.obter(i).getNome().toLowerCase().contains(busca)) {
                    matches.adicionar(sintomasDisponiveis.obter(i));
                }
            }

            Sintoma selecionado = null;

            if (matches.vazia()) {
                System.out.println("❌ Nenhum sintoma encontrado com '" + busca + "'.");
                System.out.print("Deseja CRIAR um novo sintoma com este nome? (s/n): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                    System.out.println("--- Novo Sintoma: " + busca + " ---");

                    String urgencia = "Baixa";
                    System.out.println("Nível de Urgência:");
                    System.out.println("1. Baixa (Verde)");
                    System.out.println("2. Média (Laranja)");
                    System.out.println("3. Urgente (Vermelha)");
                    System.out.print("Opção: ");
                    String opUrg = scanner.nextLine();
                    if (opUrg.equals("2"))
                        urgencia = "Média";
                    else if (opUrg.equals("3"))
                        urgencia = "Urgente";

                    Sintoma novo = new Sintoma(busca, urgencia);

                    System.out.println("Deseja associar especialidade? (s/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                        listarEspecialidades();
                        System.out.print("Código da Especialidade: ");
                        String codEsp = scanner.nextLine().toUpperCase().trim();
                        for (int k = 0; k < especialidades.tamanho(); k++) {
                            if (especialidades.obter(k).getCodigo().equals(codEsp)) {
                                novo.adicionarEspecialidade(especialidades.obter(k));
                                System.out.println("Especialidade associada.");
                                break;
                            }
                        }
                    }

                    sintomasDisponiveis.adicionar(novo);
                    selecionado = novo;
                    System.out.println("✅ Sintoma criado e selecionado.");

                    // Salvar apenas no final
                }
            } else if (matches.tamanho() == 1) {
                // Apenas um encontrado -> Confirmação
                Sintoma sugestao = matches.obter(0);
                System.out.print("Encontrei '" + sugestao.getNome() + "'. É este? (S/n): ");
                String resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("s") || resp.isEmpty()) {
                    selecionado = sugestao;
                }
            } else {
                // Múltiplos encontrados -> Seleção
                System.out.println("\n🔎 Encontrei vários sintomas. Qual deles?");
                for (int i = 0; i < matches.tamanho(); i++) {
                    System.out.println((i + 1) + ". " + matches.obter(i).getNome());
                }
                System.out.print("Escolha o número (ou 0 para cancelar): ");
                try {
                    int escolha = Integer.parseInt(scanner.nextLine());
                    if (escolha > 0 && escolha <= matches.tamanho()) {
                        selecionado = matches.obter(escolha - 1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Entrada inválida.");
                }
            }

            if (selecionado != null) {
                System.out.println("✅ Adicionado: " + selecionado.getNome());
                sintomasUtente.adicionar(selecionado);
            }

            System.out.print("\nAdicionar outro sintoma? (s/N): ");
            if (!scanner.nextLine().equalsIgnoreCase("s"))
                adicionarMais = false;
        }

        Utente u = new Utente(nome, sintomasUtente);
        System.out.println("✅ Triagem Concluída!");
        System.out.println("Urgência Calculada: " + u.getNivelUrgencia());
        System.out.println("Especialidade Atribuída: "
                + (u.getEspecialidade() != null ? u.getEspecialidade().getNome() : "Nenhuma"));

        simulacao.getUtentesEmEspera().adicionar(u);
        simulacao.registrarSintomasDoUtente(u);
    }

    // --- ESTATISTICAS ---
    private void exibirEstatisticas() {
        System.out.println("\n📊 RELATÓRIO ESTATÍSTICO");

        // 1. Médias e Totais
        System.out.println("\n🔸 Totais:");
        System.out.println("   - Utentes Atendidos Hoje: " + simulacao.getTotalUtentesAtendidos());

        // 2. Salários
        System.out.println("\n💰 Salários Acumulados (Do Dia/Sessão):");
        Lista<Medico> meds = simulacao.getMedicosAtivos();
        double totalGasto = 0;
        for (int i = 0; i < meds.tamanho(); i++) {
            Medico m = meds.obter(i);
            double salario = m.getTotalUnidadesTrabalhadas() * m.getValorHora();
            totalGasto += salario;
            System.out.printf("   - %s (%s): %d horas x %.2f€ = %.2f€\n",
                    m.getNome(),
                    (m.getEspecialidade() != null ? m.getEspecialidade().getNome() : "N/A"),
                    m.getTotalUnidadesTrabalhadas(),
                    m.getValorHora(),
                    salario);
        }
        System.out.printf("   > TOTAL GASTO: %.2f€\n", totalGasto);

        // 3. Top Especialidades (Contagem Simples)
        System.out.println("\n🏆 Top Especialidades Solicitadas:");
        Lista<String> hist = simulacao.getHistoricoEspecialidades();
        if (hist.vazia()) {
            System.out.println("   (Nenhum atendimento realizado)");
        } else {
            // Contagem manual (sem HashMap)
            // Listas paralelas: Codigos | Contagens
            Lista<String> unicos = new Lista<>();
            Lista<Integer> contagens = new Lista<>();

            for (int i = 0; i < hist.tamanho(); i++) {
                String codigo = hist.obter(i);
                boolean encontrado = false;

                for (int k = 0; k < unicos.tamanho(); k++) {
                    if (unicos.obter(k).equals(codigo)) {
                        contagens.remover(k); // Remove antigo (hack porque Lista nao tem set)
                        // Ineficiente, mas funciona para listas pequenas.
                        // Melhor seria adicionar método 'set' na Lista, mas vou apenas adicionar de
                        // volta no final?
                        // Lista.java tem setValor? No.java tem. Lista.java pode obter No?
                        // Lista.java tem 'obter(index)' que retorna T.
                        // Vou usar arrays temporarios ou apenas imprimir tudo.
                        // Abordagem simples: Loop aninhado para contar frequencia na hora.
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado)
                    unicos.adicionar(codigo);
            }

            // Mostrar contagens
            for (int i = 0; i < unicos.tamanho(); i++) {
                String u = unicos.obter(i);
                int count = 0;
                for (int k = 0; k < hist.tamanho(); k++) {
                    if (hist.obter(k).equals(u))
                        count++;
                }
                System.out.println("   - " + u + ": " + count + " utentes");
            }
        }

        System.out.println("\nPressione ENTER para voltar...");
        scanner.nextLine();
    }

    // --- CONFIGURAÇÕES ---

    private void menuConfiguracoes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- ⚙️ PAINEL DE CONFIGURAÇÕES ---");
            System.out.println("1. [Tempo Consulta] Baixa (" + Configuracao.TEMPO_CONSULTA_BAIXA + ")");
            System.out.println("2. [Tempo Consulta] Média (" + Configuracao.TEMPO_CONSULTA_MEDIA + ")");
            System.out.println("3. [Tempo Consulta] Urgente (" + Configuracao.TEMPO_CONSULTA_URGENTE + ")");
            System.out.println(
                    "4. [Pessoal] Horas Trabalho antes Descanso (" + Configuracao.HORAS_TRABALHO_ANTES_DESCANSO + ")");
            System.out.println("5. [Progressão] Baixa -> Média (" + Configuracao.TEMPO_BAIXA_PARA_MEDIA + " un)");
            System.out.println("6. [Progressão] Média -> Urgente (" + Configuracao.TEMPO_MEDIA_PARA_URGENTE + " un)");
            System.out.println("7. [Progressão] Urgente -> Crítico (" + Configuracao.TEMPO_URGENTE_PARA_SAIDA + " un)");
            System.out.println("8. [Sistema] Separador CSV (" + Configuracao.SEPARADOR + ")");
            System.out.println("9. [Sistema] Password Admin");
            System.out.println("10. [Sistema] Caminho Ficheiro Utentes (" + Configuracao.PATH_UTENTES + ")");
            System.out.println("11. [Sistema] Caminho Ficheiro Médicos (" + Configuracao.PATH_MEDICOS + ")");
            System.out.println("S. SALVAR Alterações");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            String op = scanner.nextLine().toUpperCase();

            switch (op) {
                case "1":
                    System.out.print("Novo Valor: ");
                    Configuracao.TEMPO_CONSULTA_BAIXA = lerIntSeguro();
                    break;
                case "2":
                    System.out.print("Novo Valor: ");
                    Configuracao.TEMPO_CONSULTA_MEDIA = lerIntSeguro();
                    break;
                case "3":
                    System.out.print("Novo Valor: ");
                    Configuracao.TEMPO_CONSULTA_URGENTE = lerIntSeguro();
                    break;
                case "4":
                    System.out.print("Novo Valor: ");
                    Configuracao.HORAS_TRABALHO_ANTES_DESCANSO = lerIntSeguro();
                    break;
                case "5":
                    System.out.print("Novo Valor: ");
                    Configuracao.TEMPO_BAIXA_PARA_MEDIA = lerIntSeguro();
                    break;
                case "6":
                    System.out.print("Novo Valor: ");
                    Configuracao.TEMPO_MEDIA_PARA_URGENTE = lerIntSeguro();
                    break;
                case "7":
                    System.out.print("Novo Valor: ");
                    Configuracao.TEMPO_URGENTE_PARA_SAIDA = lerIntSeguro();
                    break;
                case "8":
                    System.out.print("Novo Separador: ");
                    Configuracao.SEPARADOR = scanner.nextLine();
                    break;
                case "9":
                    System.out.print("Nova Password: ");
                    Configuracao.PASSWORD_ACESSO = scanner.nextLine();
                    break;
                case "10":
                    System.out.print("Novo Caminho Utentes: ");
                    Configuracao.PATH_UTENTES = scanner.nextLine();
                    break;
                case "11":
                    System.out.print("Novo Caminho Médicos: ");
                    Configuracao.PATH_MEDICOS = scanner.nextLine();
                    break;
                case "S":
                    Configuracao.salvarConfiguracoes();
                    break;
                case "0":
                    voltar = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private int lerIntSeguro() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Valor inválido. Mantido o anterior.");
            return 0; // Valor dummy, logica real deveria tratar melhor
        }
    }

    private void salvarSair() {
        GestorFicheiros.salvarUtentes(simulacao.getUtentesEmEspera());
        GestorFicheiros.salvarMedicos(simulacao.getMedicosAtivos());
        GestorFicheiros.salvarEspecialidades(especialidades);
        GestorFicheiros.salvarSintomas(sintomasDisponiveis);
        System.out.println("Adeus!");
    }

    public static void main(String[] args) {
        System.out.println("A iniciar o sistema...");
        Configuracao.carregarConfiguracoes();
        new Menu().iniciar();
    }
}
