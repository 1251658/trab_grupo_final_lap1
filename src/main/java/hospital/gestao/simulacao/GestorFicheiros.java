package hospital.gestao.simulacao;

import hospital.gestao.estruturas.Lista;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GestorFicheiros {

    /**
     * Tenta ler Utentes do ficheiro CSV.
     * ⚠️ CORREÇÃO TEMPORÁRIA: Força 'emConsulta = false' para ignorar o bug de
     * parsing booleano
     * e validar o motor de simulação.
     * 
     * @return Lista de Utentes ou uma lista vazia em caso de erro.
     */
    public static Lista<Utente> carregarUtentes() {
        Lista<Utente> utentes = new Lista<>();
        File ficheiro = new File(Configuracao.PATH_UTENTES);
        System.out.println("💾 Carregando Utentes de: " + Configuracao.PATH_UTENTES);
        int numeroLinha = 0;

        try (Scanner scanner = new Scanner(ficheiro)) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                numeroLinha++;

                if (linha.trim().isEmpty())
                    continue; // Ignora linhas vazias

                // NOTA: Agora o CSV deve ter o formato completo:
                // nome;nivelUrgencia;unidadesEmEspera;emConsulta;tempoRestante
                String[] dados = linha.split(Configuracao.SEPARADOR);

                // Deve ter 5 campos para Utente
                if (dados.length < 5) {
                    System.err.println("❌ ERRO de DADOS na Linha " + numeroLinha
                            + " (Utentes): Esperado 5 campos, encontrado " + dados.length + ". Linha: " + linha);
                    continue; // Pula para a próxima linha
                }

                try {
                    String nome = dados[0];
                    String nivelUrgencia = dados[1];
                    // Conversão dos campos de estado
                    int unidadesEmEspera = Integer.parseInt(dados[2].trim());

                    // Parse do boolean
                    boolean emConsulta = Boolean.parseBoolean(dados[3].trim());

                    int tempoRestante = Integer.parseInt(dados[4].trim());

                    String codEspecialidade = "N/A";
                    if (dados.length >= 6) {
                        codEspecialidade = dados[5].trim();
                    }

                    // Implementação Completa: Usa o CONSTRUTOR DE PERSISTÊNCIA com 6 argumentos
                    Utente novoUtente = new Utente(nome, nivelUrgencia, unidadesEmEspera, emConsulta, tempoRestante,
                            codEspecialidade);
                    utentes.adicionar(novoUtente);
                    System.out.println("✅ Utente carregado: " + nome);

                } catch (NumberFormatException e) {
                    System.err.println("❌ ERRO de FORMATO na Linha " + numeroLinha
                            + " (Utentes): Valor numérico incorreto. Causa: " + e.getMessage());
                    System.err.println("Linha falhada: " + linha);
                } catch (Exception e) {
                    System.err.println("❌ ERRO GENÉRICO na Linha " + numeroLinha + " (Utentes): " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("⚠️ ERRO: Ficheiro de utentes não encontrado. A iniciar com lista vazia.");
        }

        // Diagnóstico Final
        if (utentes.vazia() && numeroLinha > 0) {
            System.err.println(
                    "❌ FALHA GERAL: A lista de utentes está vazia. O ficheiro não está vazio, mas está 100% corrompido.");
        }
        return utentes;
    }

    /**
     * Tenta carregar Médicos do ficheiro CSV.
     * 7 Campos:
     * nome;codEspecialidade;horaEntrada;horaSaida;unidadesTrabalhadas;unidadesDescanso;valorHora
     * 8 Campos: ... ;totalUnidadesTrabalhadas
     * 
     * @return Lista de Medicos ou uma lista vazia em caso de erro.
     */
    public static Lista<Medico> carregarMedicos() {
        Lista<Medico> medicos = new Lista<>();
        File ficheiro = new File(Configuracao.PATH_MEDICOS);
        System.out.println("💾 Carregando Médicos de: " + Configuracao.PATH_MEDICOS);
        int numeroLinha = 0;

        try (Scanner scanner = new Scanner(ficheiro)) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                numeroLinha++;

                if (linha.trim().isEmpty())
                    continue; // Ignora linhas vazias

                String[] dados = linha.split(Configuracao.SEPARADOR);

                try {
                    String nome;
                    String codEspecialidade = "N/A";
                    int entrada, saida, unidadesTrabalhadas, unidadesDescanso;
                    double valorHora;
                    int totalTrabalhado = 0;

                    // Verifica compatibilidade com versão anterior (6 campos) vs versão atual (7 ou
                    // 8 campos)
                    if (dados.length == 6) {
                        // LEGADO (Sem especialidade)
                        nome = dados[0];
                        entrada = Integer.parseInt(dados[1].trim());
                        saida = Integer.parseInt(dados[2].trim());
                        unidadesTrabalhadas = Integer.parseInt(dados[3].trim());
                        unidadesDescanso = Integer.parseInt(dados[4].trim());
                        valorHora = Double.parseDouble(dados[5].trim());
                        System.out.println(
                                "⚠️ AVISO: Médico " + nome + " carregado do formato antigo (Sem Especialidade).");
                    } else if (dados.length >= 7) {
                        // NOVO FORMATO (Com especialidade na pos 1)
                        nome = dados[0];
                        codEspecialidade = dados[1].trim();
                        entrada = Integer.parseInt(dados[2].trim());
                        saida = Integer.parseInt(dados[3].trim());
                        unidadesTrabalhadas = Integer.parseInt(dados[4].trim());
                        unidadesDescanso = Integer.parseInt(dados[5].trim());
                        valorHora = Double.parseDouble(dados[6].trim());

                        if (dados.length >= 8) {
                            totalTrabalhado = Integer.parseInt(dados[7].trim());
                        }
                    } else {
                        System.err.println("❌ ERRO de DADOS na Linha " + numeroLinha
                                + " (Médicos): Dados insuficientes. Esperado 6, 7 ou 8 campos. Ignorando.");
                        continue;
                    }

                    // Tenta encontrar o objeto Especialidade (aqui apenas criamos um Dummy,
                    // num sistema real teríamos de buscar no Repositorio de Especialidades)
                    Especialidade esp = null;
                    if (!"N/A".equals(codEspecialidade)) {
                        esp = new Especialidade(codEspecialidade, "Especialidade " + codEspecialidade);
                    }

                    Medico novoMedico = new Medico(nome, esp, entrada, saida, valorHora);

                    // Restaurar estado
                    novoMedico.setUnidadesTrabalhadasSeguidas(unidadesTrabalhadas);
                    novoMedico.setUnidadesEmDescanso(unidadesDescanso);
                    novoMedico.setTotalUnidadesTrabalhadas(totalTrabalhado);

                    medicos.adicionar(novoMedico);

                } catch (NumberFormatException e) {
                    System.err.println("❌ ERRO de FORMATO na Linha " + numeroLinha
                            + " (Médicos): Valor numérico ou decimal incorreto.");
                    System.err.println("Linha falhada: " + linha);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("⚠️ ERRO: Ficheiro de médicos não encontrado. A iniciar com lista vazia.");
        }

        // Diagnóstico Final
        if (medicos.vazia() && numeroLinha > 0) {
            System.err.println(
                    "❌ FALHA GERAL: A lista de médicos está vazia. O ficheiro não está vazio, mas está 100% corrompido.");
        }
        return medicos;
    }

    // ---------------------------------------------------
    // IMPLEMENTAÇÃO DA ESCRITA DE DADOS (PERSISTÊNCIA)
    // ---------------------------------------------------

    /**
     * Salva o estado atual dos utentes no ficheiro (para Aluno 1).
     */
    public static void salvarUtentes(Lista<Utente> utentes) {
        System.out.println("✍️ Salvando estado de " + utentes.tamanho() + " utentes...");
        try (FileWriter writer = new FileWriter(Configuracao.PATH_UTENTES)) {
            for (int i = 0; i < utentes.tamanho(); i++) {
                writer.write(utentes.obter(i).toCSV() + "\n");
            }
            System.out.println("✅ Utentes salvos com sucesso.");
        } catch (IOException e) {
            System.err.println("❌ ERRO ao salvar utentes: " + e.getMessage());
        }
    }

    /**
     * Salva o estado atual dos médicos no ficheiro (para Aluno 1).
     */
    public static void salvarMedicos(Lista<Medico> medicos) {
        System.out.println("✍️ Salvando estado de " + medicos.tamanho() + " médicos...");
        try (FileWriter writer = new FileWriter(Configuracao.PATH_MEDICOS)) {
            for (int i = 0; i < medicos.tamanho(); i++) {
                writer.write(medicos.obter(i).toCSV() + "\n");
            }
            System.out.println("✅ Médicos salvos com sucesso.");
        } catch (IOException e) {
            System.err.println("❌ ERRO ao salvar médicos: " + e.getMessage());
        }
    }

    public static Lista<Especialidade> carregarEspecialidades() {
        Lista<Especialidade> especialidades = new Lista<>();
        try (Scanner scanner = new Scanner(new File(Configuracao.PATH_ESPECIALIDADES))) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                if (linha.trim().isEmpty())
                    continue;
                String[] dados = linha.split(Configuracao.SEPARADOR);
                if (dados.length >= 2) {
                    especialidades.adicionar(new Especialidade(dados[0].trim(), dados[1].trim()));
                }
            }
            System.out.println("✅ Especialidades carregadas: " + especialidades.tamanho());
        } catch (FileNotFoundException e) {
            System.out.println("⚠️ Ficheiro de especialidades não encontrado.");
        }
        return especialidades;
    }

    public static void salvarEspecialidades(Lista<Especialidade> especialidades) {
        try (FileWriter writer = new FileWriter(Configuracao.PATH_ESPECIALIDADES)) {
            for (int i = 0; i < especialidades.tamanho(); i++) {
                Especialidade e = especialidades.obter(i);
                writer.write(e.getCodigo() + Configuracao.SEPARADOR + e.getNome() + "\n");
            }
            System.out.println("✅ Especialidades salvas.");
        } catch (IOException e) {
            System.err.println("❌ Erro ao salvar especialidades: " + e.getMessage());
        }
    }

    public static Lista<Sintoma> carregarSintomas(Lista<Especialidade> especialidades) {
        Lista<Sintoma> sintomas = new Lista<>();
        try (Scanner scanner = new Scanner(new File(Configuracao.PATH_SINTOMAS))) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                if (linha.trim().isEmpty())
                    continue;
                String[] dados = linha.split(Configuracao.SEPARADOR);
                if (dados.length >= 2) {
                    Sintoma s = new Sintoma(dados[0].trim(), dados[1].trim());
                    // Associar especialidade se existir
                    if (dados.length >= 3) {
                        String codEsp = dados[2].trim();
                        for (int i = 0; i < especialidades.tamanho(); i++) {
                            if (especialidades.obter(i).getCodigo().equals(codEsp)) {
                                s.adicionarEspecialidade(especialidades.obter(i));
                                break;
                            }
                        }
                    }
                    sintomas.adicionar(s);
                }
            }
            System.out.println("✅ Sintomas carregados: " + sintomas.tamanho());
        } catch (FileNotFoundException e) {
            System.out.println("⚠️ Ficheiro de sintomas não encontrado.");
        }
        return sintomas;
    }

    public static void salvarSintomas(Lista<Sintoma> sintomas) {
        try (FileWriter writer = new FileWriter(Configuracao.PATH_SINTOMAS)) {
            for (int i = 0; i < sintomas.tamanho(); i++) {
                Sintoma s = sintomas.obter(i);
                // Usando metodo toCSV atualizado
                writer.write(s.toCSV() + "\n");
            }
            System.out.println("✅ Sintomas salvos.");
        } catch (IOException e) {
            System.err.println("❌ Erro ao salvar sintomas: " + e.getMessage());
        }
    }
}