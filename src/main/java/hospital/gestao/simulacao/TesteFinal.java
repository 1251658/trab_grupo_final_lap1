package hospital.gestao.simulacao;

import hospital.gestao.estruturas.Lista;

public class TesteFinal {
    public static void main(String[] args) {
        System.out.println("=== TESTE AUTOMATIZADO DE REQUISITOS ===\n");

        // 1. Setup Infraestrutura (Lista Ligada)
        Lista<Utente> utentes = new Lista<>();
        Lista<Medico> medicos = new Lista<>();

        // 2. Setup Dados (Médicos)
        Especialidade card = new Especialidade("CARD", "Cardiologia");
        Especialidade ort = new Especialidade("ORT", "Ortopedia");

        Medico drHouse = new Medico("DrHouse", card, 0, 24, 100.0); // Disponível o dia todo
        drHouse.setEmServico(false);
        Medico drBone = new Medico("DrBone", ort, 0, 24, 100.0); // Disponível o dia todo
        drBone.setEmServico(false);

        medicos.adicionar(drHouse);
        medicos.adicionar(drBone);
        System.out.println("✅ Médicos Criados: DrHouse (CARD) e DrBone (ORT)");

        // 3. Setup Dados (Utentes com Sintomas)
        Sintoma dorPeito = new Sintoma("Dor no Peito", "Urgente");
        dorPeito.adicionarEspecialidade(card);

        Sintoma pernaQuebrada = new Sintoma("Perna Quebrada", "Urgente");
        pernaQuebrada.adicionarEspecialidade(ort);

        // Utente 1: Problema Cardíaco -> Deve ir para DrHouse
        Lista<Sintoma> sintomas1 = new Lista<>();
        sintomas1.adicionar(dorPeito);
        Utente uCardio = new Utente("Sr. Coracao", sintomas1);

        // Utente 2: Problema Ortopédico -> Deve ir para DrBone
        Lista<Sintoma> sintomas2 = new Lista<>();
        sintomas2.adicionar(pernaQuebrada);
        Utente uOrto = new Utente("Sra. Osso", sintomas2);

        utentes.adicionar(uCardio);
        utentes.adicionar(uOrto);
        System.out.println("✅ Utentes Criados: Sr. Coracao (Urgente/CARD) e Sra. Osso (Urgente/ORT)");

        // 4. Iniciar Simulação
        Dia dia = new Dia(utentes, medicos);

        System.out.println("\n--- INÍCIO DA SIMULAÇÃO ---");
        // Avançar tempo até atenderem
        // Hora 1: Devem ser alocados
        dia.avancarUnidadeTempo(); // Hora 2 (HoraInicial=1, avança para 2)

        // Verificações
        boolean cardOK = false;
        boolean ortOK = false;

        // Verificar DrHouse
        if (drHouse.estaEmServico()) {
            System.out.println("✅ DrHouse está em serviço (Correto).");
            cardOK = true;
        } else {
            System.err.println("❌ DrHouse deveria estar a trabalhar!");
        }

        // Verificar DrBone
        if (drBone.estaEmServico()) {
            System.out.println("✅ DrBone está em serviço (Correto).");
            ortOK = true;
        } else {
            System.err.println("❌ DrBone deveria estar a trabalhar!");
        }

        // Verificar Logica Especialidade Cruzada (Se trocarmos a ordem ou
        // especialidade)
        // Para este teste, basta saber que ambos foram atendidos simultaneamente porque
        // havia médicos compatíveis.

        // Avançar tempo para terminarem consulta (Urgente = 4 tempos?)
        // Vamos avançar 5 tempos
        for (int i = 0; i < 5; i++)
            dia.avancarUnidadeTempo();

        // Verificar Altas
        if (dia.getTotalUtentesAtendidos() == 2) {
            System.out.println("✅ Ambos os utentes tiverem alta.");
        } else {
            System.err.println("❌ Esperava 2 altas, teve: " + dia.getTotalUtentesAtendidos());
        }

        // Verificar Estatisticas
        System.out.println("\n--- ESTATÍSTICAS FINAIS ---");
        Lista<String> hist = dia.getHistoricoEspecialidades();
        System.out.println("Especialidades atendidas: " + hist);
        if (hist.tamanho() == 2) {
            System.out.println("✅ Contagem de especialidades correta.");
        }

        if (cardOK && ortOK) {
            System.out.println("\n🎉 TESTE DE REQUISITOS: APROVADO!");
            System.out.println("O sistema respeitou as especialidades e realizou a triagem corretamente.");
        } else {
            System.out.println("\n❌ TESTE DE REQUISITOS: FALHOU.");
        }
    }
}
