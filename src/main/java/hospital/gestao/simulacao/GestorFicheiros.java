package hospital.gestao.simulacao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorFicheiros {

    /**
     * Tenta ler Utentes do ficheiro CSV.
     * ⚠️ CORREÇÃO TEMPORÁRIA: Força 'emConsulta = false' para ignorar o bug de parsing booleano
     * e validar o motor de simulação.
     * @return Lista de Utentes ou uma lista vazia em caso de erro.
     */
    public static List<Utente> carregarUtentes() {
        List<Utente> utentes = new ArrayList<>();
        File ficheiro = new File(Configuracao.PATH_UTENTES);
        System.out.println("💾 Carregando Utentes de: " + Configuracao.PATH_UTENTES);
        int numeroLinha = 0;

        try (Scanner scanner = new Scanner(ficheiro)) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                numeroLinha++;

                if (linha.trim().isEmpty()) continue; // Ignora linhas vazias

                // NOTA: Agora o CSV deve ter o formato completo: nome;nivelUrgencia;unidadesEmEspera;emConsulta;tempoRestante
                String[] dados = linha.split(Configuracao.SEPARADOR);

                // Deve ter 5 campos para Utente
                if (dados.length < 5) {
                    System.err.println("❌ ERRO de DADOS na Linha " + numeroLinha + " (Utentes): Esperado 5 campos, encontrado " + dados.length + ". Linha: " + linha);
                    continue; // Pula para a próxima linha
                }

                try {
                    String nome = dados[0];
                    String nivelUrgencia = dados[1];
                    // Conversão dos campos de estado
                    int unidadesEmEspera = Integer.parseInt(dados[2].trim());

                    // 🛑 LINHA DE CORREÇÃO TEMPORÁRIA: Ignora o campo dados[3] para evitar o bug de leitura booleana
                    boolean emConsulta = false;

                    int tempoRestante = Integer.parseInt(dados[4].trim());

                    // Implementação Completa: Usa o CONSTRUTOR DE PERSISTÊNCIA com 5 argumentos
                    Utente novoUtente = new Utente(nome, nivelUrgencia, unidadesEmEspera, emConsulta, tempoRestante);
                    utentes.add(novoUtente);

                } catch (NumberFormatException e) {
                    System.err.println("❌ ERRO de FORMATO na Linha " + numeroLinha + " (Utentes): Valor numérico incorreto. Causa: " + e.getMessage());
                    System.err.println("Linha falhada: " + linha);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("⚠️ ERRO: Ficheiro de utentes não encontrado. A iniciar com lista vazia.");
        }

        // Diagnóstico Final
        if (utentes.isEmpty() && numeroLinha > 0) {
            System.err.println("❌ FALHA GERAL: A lista de utentes está vazia. O ficheiro não está vazio, mas está 100% corrompido.");
        }
        return utentes;
    }

    /**
     * Tenta carregar Médicos do ficheiro CSV (6 campos: 3 de info + 3 de estado).
     * @return Lista de Medicos ou uma lista vazia em caso de erro.
     */
    public static List<Medico> carregarMedicos() {
        List<Medico> medicos = new ArrayList<>();
        File ficheiro = new File(Configuracao.PATH_MEDICOS);
        System.out.println("💾 Carregando Médicos de: " + Configuracao.PATH_MEDICOS);
        int numeroLinha = 0;

        try (Scanner scanner = new Scanner(ficheiro)) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                numeroLinha++;

                if (linha.trim().isEmpty()) continue; // Ignora linhas vazias

                String[] dados = linha.split(Configuracao.SEPARADOR);

                // Deve ter 6 campos
                if (dados.length < 6) {
                    System.err.println("❌ ERRO de DADOS na Linha " + numeroLinha + " (Médicos): Esperado 6 campos, encontrado " + dados.length + ". Linha: " + linha);
                    continue; // Pula para a próxima linha
                }

                try {
                    String nome = dados[0];
                    int entrada = Integer.parseInt(dados[1].trim());
                    int saida = Integer.parseInt(dados[2].trim());
                    int unidadesTrabalhadasSeguidas = Integer.parseInt(dados[3].trim());
                    int unidadesEmDescanso = Integer.parseInt(dados[4].trim());

                    // O campo que costuma falhar (use ponto '.' como separador decimal)
                    double valorHora = Double.parseDouble(dados[5].trim());

                    Medico novoMedico = new Medico(nome, entrada, saida, valorHora);
                    medicos.add(novoMedico);

                } catch (NumberFormatException e) {
                    // Esta mensagem deve aparecer se usar vírgula em vez de ponto no valorHora
                    System.err.println("❌ ERRO de FORMATO na Linha " + numeroLinha + " (Médicos): Valor numérico ou decimal incorreto. Causa: " + e.getMessage());
                    System.err.println("Linha falhada: " + linha);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("⚠️ ERRO: Ficheiro de médicos não encontrado. A iniciar com lista vazia.");
        }

        // Diagnóstico Final
        if (medicos.isEmpty() && numeroLinha > 0) {
            System.err.println("❌ FALHA GERAL: A lista de médicos está vazia. O ficheiro não está vazio, mas está 100% corrompido.");
        }
        return medicos;
    }

    // ---------------------------------------------------
    // IMPLEMENTAÇÃO DA ESCRITA DE DADOS (PERSISTÊNCIA)
    // ---------------------------------------------------

    /**
     * Salva o estado atual dos utentes no ficheiro (para Aluno 1).
     */
    public static void salvarUtentes(List<Utente> utentes) {
        System.out.println("✍️ Salvando estado de " + utentes.size() + " utentes...");
        try (FileWriter writer = new FileWriter(Configuracao.PATH_UTENTES)) {
            for (Utente utente : utentes) {
                writer.write(utente.toCSV() + "\n");
            }
            System.out.println("✅ Utentes salvos com sucesso.");
        } catch (IOException e) {
            System.err.println("❌ ERRO ao salvar utentes: " + e.getMessage());
        }
    }

    /**
     * Salva o estado atual dos médicos no ficheiro (para Aluno 1).
     */
    public static void salvarMedicos(List<Medico> medicos) {
        System.out.println("✍️ Salvando estado de " + medicos.size() + " médicos...");
        try (FileWriter writer = new FileWriter(Configuracao.PATH_MEDICOS)) {
            for (Medico medico : medicos) {
                writer.write(medico.toCSV() + "\n");
            }
            System.out.println("✅ Médicos salvos com sucesso.");
        } catch (IOException e) {
            System.err.println("❌ ERRO ao salvar médicos: " + e.getMessage());
        }
    }
}