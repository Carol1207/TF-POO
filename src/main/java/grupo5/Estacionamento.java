package grupo5;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Estacionamento {
    public static final int MAX_VAGAS = 9000;
    public static final double VALOR_DIARIA = 20.0;
    public static final double VALOR_HORA = 3.0;
    private HashMap<String, Ticket> ticketsAtivos;
    private List<Ticket> ticketsEncerrados;
    private HashSet<String> placasBloqueadas;

    public Estacionamento() {
        ticketsAtivos = new HashMap<>();
        ticketsEncerrados = new ArrayList<>();
        placasBloqueadas = new HashSet<>();
    }

    public HashMap<String, Ticket> getTicketsAtivos() {
        return ticketsAtivos;
    }

    public List<Ticket> getTicketsEncerrados() {
        return ticketsEncerrados;
    }

    public HashSet<String> getPlacasBloqueadas() {
        return placasBloqueadas;
    }

    /*
     * public void entrada(String placa, LocalDateTime horaEntrada) {
     * if (ticketsAtivos.size() >= MAX_VAGAS) {
     * throw new IllegalStateException();
     * }
     * ticketsAtivos.put(placa, new Ticket(placa));
     * }
     */

    public void saida(String placa, LocalDateTime horaSaida) {
        if (!ticketsAtivos.containsKey(placa)) {
            throw new IllegalStateException();
        }

        Ticket ticket = ticketsAtivos.get(placa);
    }

    // Métodos static auxiliares

    public static boolean verificaPlaca(String placa) {
        if (placa == null) {
            return false;
        }

        // Padrão de placa MERCOSUL: 3 letras + 1 dígito + 1 letra + 2 dígitos
        // Exemplo: ABC1D23
        return placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");
    }

    public static CategoriaCliente categoriaCliente(Cliente cliente) {
        return switch (cliente) {
            case null -> CategoriaCliente.AVULSO;
            case Empresa em -> CategoriaCliente.EMPRESA;
            case Professor pr -> CategoriaCliente.PROFESSOR;
            case Estudante es -> CategoriaCliente.ESTUDANTE;
            default -> CategoriaCliente.AVULSO;
        };
    }

    // Retorna a quantidade de dias entre duas datas
    // Se as datas forem no mesmo dia, retorna 0
    public static int quantidadeDiarias(LocalDateTime entrada, LocalDateTime saida) {
        if (saida.isBefore(entrada)) {
            throw new IllegalArgumentException("Saída anterior à entrada");
        }
        return (int) ChronoUnit.DAYS.between(entrada.toLocalDate(), saida.toLocalDate());

    }

    // Quantidade de horas entre o horário de entrada e o de saída
    // ou a quantidade de horas até a meia noite se mudou data
    public static int horasAteSaidaOuMeiaNoite(LocalDateTime entrada, LocalDateTime saida) {
        if (saida.isBefore(entrada)) {
            throw new IllegalArgumentException("Saída anterior à entrada");
        }

        if (entrada.toLocalDate().equals(saida.toLocalDate())) {
            return (int) ChronoUnit.HOURS.between(entrada, saida);
        }

        LocalDateTime meiaNoite = entrada.toLocalDate().plusDays(1).atStartOfDay();
        return (int) ChronoUnit.HOURS.between(entrada, meiaNoite);
    }

}
