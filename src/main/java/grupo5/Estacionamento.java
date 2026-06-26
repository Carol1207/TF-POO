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
    private CadastroCliente cadastroCliente;

    public Estacionamento(CadastroCliente cadastroCliente) {
        this.cadastroCliente = cadastroCliente;
        this.ticketsAtivos = new HashMap<>();
        this.ticketsEncerrados = new ArrayList<>();
        this.placasBloqueadas = new HashSet<>();
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

    public void entrada(String placa, LocalDateTime horaEntrada) {
        if (!verificaPlaca(placa)) {
            throw new IllegalArgumentException("Placa inválida.");
        }
        if (ticketsAtivos.size() >= MAX_VAGAS) {
            throw new IllegalStateException("Estacionamento lotado.");
        }
        if (placasBloqueadas.contains(placa)) {
            throw new IllegalStateException("Veículo bloqueado por falta de pagamento.");
        }

        Cliente cliente = null;
        if (cadastroCliente != null && cadastroCliente.getPlacasClientes().containsKey(placa)) {
            cliente = cadastroCliente.getPlacasClientes().get(placa);

            if (cliente instanceof Empresa) {
                if (((Empresa) cliente).isInadimplente()) {
                    throw new IllegalStateException("Empresa inadimplente.");
                }
            } else if (cliente instanceof Estudante) {
                if (((Estudante) cliente).getSaldo() < 0) {
                    throw new IllegalStateException("Estudante com saldo negativo.");
                }
            } else if (cliente instanceof Professor) {
                Cliente finalCliente = cliente;
                boolean jaEstacionado = ticketsAtivos.values().stream()
                        .anyMatch(t -> t.getCliente() == finalCliente);
                if (jaEstacionado) {
                    cliente = null; // trata como cliente avulso
                }
            }
        }

        ticketsAtivos.put(placa, new Ticket(placa, horaEntrada, cliente));
    }

    public Ticket calculaCustoSaida(String placa, LocalDateTime horaSaida) {
        if (!ticketsAtivos.containsKey(placa)) {
            throw new IllegalStateException("Veículo não encontrado.");
        }

        Ticket ticket = ticketsAtivos.get(placa);
        Cliente cliente = ticket.getCliente();

        if (cliente != null) {
            return cliente.calculaCusto(ticket, horaSaida);
        } else {
            int dias = quantidadeDiarias(ticket.getHoraEntrada(), horaSaida);
            int horasPrimeiroDia = horasAteSaidaOuMeiaNoite(ticket.getHoraEntrada(), horaSaida);

            double custoDia1 = (horasPrimeiroDia > 6) ? VALOR_DIARIA : Math.max(1, horasPrimeiroDia) * VALOR_HORA;
            double custoTotal = custoDia1 + (dias * VALOR_DIARIA);

            // desconto cliente frequente
            boolean frequente = ticketsEncerrados.stream()
                    .anyMatch(t -> t.getCliente() == null
                            && t.getPlaca().equals(placa)
                            && t.getHoraSaida() != null
                            && t.getHoraSaida().isAfter(horaSaida.minusDays(3)));

            double desconto = frequente ? custoTotal * 0.10 : 0.0;
            double valorCobrado = custoTotal - desconto;

            ticket.setHoraSaida(horaSaida);
            ticket.setValorCalculado(custoTotal);
            ticket.setDesconto(desconto);
            ticket.setValorCobrado(valorCobrado);

            return ticket;
        }
    }

    public void saida(Ticket ticket, double valorPago) {
        if (valorPago < ticket.getValorCobrado()) {
            if (ticket.getCliente() == null) {
                // avulso se recusa a pagar: libera saída, mas bloqueia placa
                placasBloqueadas.add(ticket.getPlaca());
            }
        }

        ticketsAtivos.remove(ticket.getPlaca());
        ticketsEncerrados.add(ticket);
    }

    // Métodos static auxiliares

    public static boolean verificaPlaca(String placa) {
        if (placa == null) {
            return false;
        }

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

    public static int quantidadeDiarias(LocalDateTime entrada, LocalDateTime saida) {
        if (saida.isBefore(entrada)) {
            throw new IllegalArgumentException("Saída anterior à entrada");
        }
        return (int) ChronoUnit.DAYS.between(entrada.toLocalDate(), saida.toLocalDate());

    }

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
