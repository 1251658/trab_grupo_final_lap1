package hospital.gestao.simulacao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Configuracao {

    // --- Configurações de Ficheiro (CRUD) ---
    public static final String SEPARADOR = ";";
    public static final String PATH_CONFIG = "config/config.csv"; // Caminho do ficheiro de configurações
    public static final String PATH_UTENTES = "data/utentes_em_espera.csv";
    public static final String PATH_MEDICOS = "data/medicos_ativos.csv";

    // --- Configurações de Tempo e Prioridade (Lidas do Ficheiro) ---
    // Estes valores devem ser lidos da PATH_CONFIG
    // Usamos valores default caso o ficheiro não seja encontrado ou haja erro de formato.
    public static int TEMPO_CONSULTA_BAIXA = 1;
    public static int TEMPO_CONSULTA_MEDIA = 2;
    public static int TEMPO_CONSULTA_URGENTE = 3;

    // --- Métodos de Controlo (Implementação Aluno 4) ---

    /**
     * Tenta carregar as configurações do ficheiro PATH_CONFIG.
     * Se falhar, usa os valores default (hardcoded).
     */
    public static void carregarConfiguracoes() {
        System.out.println("⏳ Carregando configurações...");
        File ficheiro = new File(PATH_CONFIG);
        boolean carregado = false;

        try (Scanner scanner = new Scanner(ficheiro)) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                String[] dados = linha.split(SEPARADOR);

                if (dados.length == 2) {
                    String chave = dados[0].trim().toUpperCase();
                    // Garante que o valor é um número inteiro válido
                    int valor = Integer.parseInt(dados[1].trim());

                    switch (chave) {
                        case "TEMPO_BAIXA":
                            TEMPO_CONSULTA_BAIXA = valor;
                            carregado = true;
                            break;
                        case "TEMPO_MEDIA":
                            TEMPO_CONSULTA_MEDIA = valor;
                            carregado = true;
                            break;
                        case "TEMPO_URGENTE":
                            TEMPO_CONSULTA_URGENTE = valor;
                            carregado = true;
                            break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("⚠️ CONFIG: Ficheiro de configurações não encontrado. Usando defaults.");
        } catch (NumberFormatException e) {
            System.err.println("⚠️ CONFIG: Formato numérico inválido no ficheiro. Usando defaults.");
        }

        if (carregado) {
            System.out.println("✅ Configurações carregadas com sucesso (Baixa: " + TEMPO_CONSULTA_BAIXA + ", Média: " + TEMPO_CONSULTA_MEDIA + ", Urgente: " + TEMPO_CONSULTA_URGENTE + ").");
        } else {
            System.out.println("✅ Configurações usando defaults (Baixa: " + TEMPO_CONSULTA_BAIXA + ", Média: " + TEMPO_CONSULTA_MEDIA + ", Urgente: " + TEMPO_CONSULTA_URGENTE + ").");
        }
    }
}