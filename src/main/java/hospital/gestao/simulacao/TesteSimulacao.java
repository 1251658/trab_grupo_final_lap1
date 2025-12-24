package hospital.gestao.simulacao;

import hospital.gestao.estruturas.Lista;

public class TesteSimulacao {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     INÍCIO DO SISTEMA: CARREGAMENTO DE DADOS     ");
        System.out.println("==================================================");

        // 1. Carregar configurações (Aluno 4)
        Configuracao.carregarConfiguracoes();

        // 🆕 LINHA DE DIAGNÓSTICO
        System.out.println(
                "📢 DIAGNÓSTICO: Tempo Urgente carregado (Configuração): " + Configuracao.TEMPO_CONSULTA_URGENTE);

        // 2. Carregar dados (Aluno 1 - GestorFicheiros)
        Lista<Utente> utentesIniciais = GestorFicheiros.carregarUtentes();
        Lista<Medico> medicosIniciais = GestorFicheiros.carregarMedicos();

        System.out.println("DEBUG: Utentes loaded: " + utentesIniciais.tamanho());
        System.out.println("DEBUG: Medicos loaded: " + medicosIniciais.tamanho());

        if (utentesIniciais.vazia() || medicosIniciais.vazia()) {
            System.err.println(
                    "❌ ERRO CRÍTICO: Não foi possível carregar Utentes e/ou Médicos. A simulação não pode iniciar.");
            return;
        }

        // 3. Inicializar a simulação
        Dia simulador = new Dia(utentesIniciais, medicosIniciais);

        System.out.println("✅ INICIALIZAÇÃO BEM-SUCEDIDA. Médicos: " + medicosIniciais.tamanho()
                + ", Utentes em espera: " + utentesIniciais.tamanho());
        System.out.println("==================================================");

        // --- LOOP PRINCIPAL DE SIMULAÇÃO ---
        for (int i = 0; i < 10; i++) {
            simulador.avancarUnidadeTempo();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }

        // 4. Salvar o estado final (Aluno 1)
        GestorFicheiros.salvarUtentes(simulador.getUtentesEmEspera());
        GestorFicheiros.salvarMedicos(simulador.getMedicosAtivos()); // Adicionei o método getMedicosAtivos na classe
                                                                     // Dia

        System.out.println("\n==================================================");
        System.out.println("            SIMULAÇÃO FINALIZADA                  ");
        System.out.println("==================================================");
    }
}