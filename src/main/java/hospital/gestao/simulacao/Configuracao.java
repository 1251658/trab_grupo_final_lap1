package hospital.gestao.simulacao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Configuracao {
    // --- Caminhos dos Ficheiros ---
    public static String PATH_CONFIG = "config/config.csv";
    public static String PATH_UTENTES = "data/utentes_em_espera.csv";
    public static String PATH_MEDICOS = "data/medicos_ativos.csv";
    public static String PATH_ESPECIALIDADES = "data/especialidades.csv";
    public static String PATH_SINTOMAS = "data/sintomas.csv";

    // --- Configurações de Segurança ---
    public static String PASSWORD_ACESSO = "admin123"; // Default

    // --- Configurações de Tempo e Lógica ---
    public static String SEPARADOR = ";";
    public static int TEMPO_CONSULTA_BAIXA = 2; // Default
    public static int TEMPO_CONSULTA_MEDIA = 3;
    public static int TEMPO_CONSULTA_URGENTE = 4;
    public static int HORAS_TRABALHO_ANTES_DESCANSO = 5;

    // --- Progressão de Urgência (Unidades de Tempo) ---
    public static int TEMPO_BAIXA_PARA_MEDIA = 3;
    public static int TEMPO_MEDIA_PARA_URGENTE = 3;
    public static int TEMPO_URGENTE_PARA_SAIDA = 2;

    public static void carregarConfiguracoes() {
        File f = new File(PATH_CONFIG);
        if (!f.exists()) {
            System.out.println("⚠️ Ficheiro de configuração não encontrado " + PATH_CONFIG + ". Usando defaults.");
            salvarConfiguracoes(); // Cria o ficheiro com defaults
            return;
        }

        System.out.println("⏳ Carregando configurações...");
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String linha = sc.nextLine().trim();
                if (linha.isEmpty() || linha.startsWith("#"))
                    continue;

                String[] partes = linha.split("=", 2);
                if (partes.length == 2) {
                    String chave = partes[0].trim().toUpperCase();
                    String valorStr = partes[1].trim();

                    try {
                        switch (chave) {
                            case "SEPARADOR":
                                SEPARADOR = valorStr;
                                break;
                            case "PATH_UTENTES":
                                PATH_UTENTES = valorStr;
                                break;
                            case "PATH_MEDICOS":
                                PATH_MEDICOS = valorStr;
                                break;
                            case "PATH_ESPECIALIDADES":
                                PATH_ESPECIALIDADES = valorStr;
                                break;
                            case "PATH_SINTOMAS":
                                PATH_SINTOMAS = valorStr;
                                break;
                            case "PASSWORD":
                                PASSWORD_ACESSO = valorStr;
                                break;
                            case "TEMPO_BAIXA":
                                TEMPO_CONSULTA_BAIXA = Integer.parseInt(valorStr);
                                break;
                            case "TEMPO_MEDIA":
                                TEMPO_CONSULTA_MEDIA = Integer.parseInt(valorStr);
                                break;
                            case "TEMPO_URGENTE":
                                TEMPO_CONSULTA_URGENTE = Integer.parseInt(valorStr);
                                break;
                            case "HORAS_DESCANSO":
                                HORAS_TRABALHO_ANTES_DESCANSO = Integer.parseInt(valorStr);
                                break;
                            case "PROG_BAIXA_MEDIA":
                                TEMPO_BAIXA_PARA_MEDIA = Integer.parseInt(valorStr);
                                break;
                            case "PROG_MEDIA_URGENTE":
                                TEMPO_MEDIA_PARA_URGENTE = Integer.parseInt(valorStr);
                                break;
                            case "PROG_URGENTE_SAIDA":
                                TEMPO_URGENTE_PARA_SAIDA = Integer.parseInt(valorStr);
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ Erro ao ler conf: " + chave + " -> " + e.getMessage());
                    }
                }
            }
            System.out.println("✅ Configurações carregadas.");
        } catch (Exception e) {
            System.err.println("❌ Erro grave ao ler config: " + e.getMessage());
        }
    }

    public static void salvarConfiguracoes() {
        try (FileWriter writer = new FileWriter(PATH_CONFIG)) {
            writer.write("# Arquivo de configuracao gerado automaticamente\n");
            writer.write("SEPARADOR=" + SEPARADOR + "\n");
            writer.write("PATH_UTENTES=" + PATH_UTENTES + "\n");
            writer.write("PATH_MEDICOS=" + PATH_MEDICOS + "\n");
            writer.write("PATH_ESPECIALIDADES=" + PATH_ESPECIALIDADES + "\n");
            writer.write("PATH_SINTOMAS=" + PATH_SINTOMAS + "\n");
            writer.write("PASSWORD=" + PASSWORD_ACESSO + "\n");
            writer.write("TEMPO_BAIXA=" + TEMPO_CONSULTA_BAIXA + "\n");
            writer.write("TEMPO_MEDIA=" + TEMPO_CONSULTA_MEDIA + "\n");
            writer.write("TEMPO_URGENTE=" + TEMPO_CONSULTA_URGENTE + "\n");
            writer.write("HORAS_DESCANSO=" + HORAS_TRABALHO_ANTES_DESCANSO + "\n");
            writer.write("PROG_BAIXA_MEDIA=" + TEMPO_BAIXA_PARA_MEDIA + "\n");
            writer.write("PROG_MEDIA_URGENTE=" + TEMPO_MEDIA_PARA_URGENTE + "\n");
            writer.write("PROG_URGENTE_SAIDA=" + TEMPO_URGENTE_PARA_SAIDA + "\n");

            System.out.println("✅ Configurações persistidas em " + PATH_CONFIG);
        } catch (IOException e) {
            System.err.println("❌ Erro ao salvar config: " + e.getMessage());
        }
    }
}