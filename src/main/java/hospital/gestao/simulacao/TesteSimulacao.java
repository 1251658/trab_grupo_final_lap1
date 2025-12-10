package hospital.gestao.simulacao;

import java.util.ArrayList;
import java.util.List;

public class TesteSimulacao {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("        INÍCIO DO TESTE COMPLETO DA SIMULAÇÃO       ");
        System.out.println("==================================================");

        //prep mock

        // Paciente A: Teste de Progressão e Consulta Curta
        Utente utenteA = new Utente("Ana", "Baixa"); // Precisa de 3 un. para Média. Consulta: 1 un. (se alocado como Baixa)
        // Paciente B: Teste de Prioridade e Consulta Longa
        Utente utenteB = new Utente("Bruno", "Urgente"); // Consulta: 3 un. (se alocado como Urgente)

        List<Utente> utentesEmEspera = new ArrayList<>();
        utentesEmEspera.add(utenteA);
        utentesEmEspera.add(utenteB);

        // medico 1: hora 1-24
        Medico medico1 = new Medico("Dr. Silva", 1, 24);
        //medico 2: hora 10-14.
        Medico medico2 = new Medico("Dra. Costa", 10, 14);

        List<Medico> medicosAtivos = new ArrayList<>();
        medicosAtivos.add(medico1);
        medicosAtivos.add(medico2);

        //iniciar motor

        Dia simulador = new Dia(utentesEmEspera, medicosAtivos);

        //simular

        //avance 15
        for (int i = 0; i < 15; i++) {
            simulador.avancarUnidadeTempo();

            //logica para pausar a execucao
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("\n==================================================");
        System.out.println("       FIM DO TESTE: VERIFICAR LOGS ACIMA         ");
        System.out.println("==================================================");
    }
}