package grupo5;

import java.time.LocalDateTime;

public class Estudante extends Cliente {
    private static final int MAX_VEICULOS = 1;
    private double saldo;

    public Estudante(String CPF_CNPJ, String nome, String celular, double valorExtra) {
        super(CPF_CNPJ, nome, celular, MAX_VEICULOS);
        this.saldo = valorExtra;
    }

    public double getSaldo() {
        return saldo;
    }

    public void adicionaSaldo(double saldo) {
        if (saldo <= 0) {
            throw new IllegalArgumentException();
        }
        this.saldo += saldo;
    }

    @Override
    public void cadastraVeiculo(String placa) {
        if (getPlacas().size() >= MAX_VEICULOS) {
            throw new SetFullException();
        }
        getPlacas().add(placa);
    }

    public static final double VALOR_DIARIA = 10.0;

    @Override
    public Ticket calculaCusto(Ticket ticket, LocalDateTime horaSaida) {
        int dias = Estacionamento.quantidadeDiarias(ticket.getHoraEntrada(), horaSaida);
        double custo = (dias + 1) * VALOR_DIARIA;

        ticket.setHoraSaida(horaSaida);
        ticket.setValorCalculado(custo);
        ticket.setDesconto(0.0);
        ticket.setValorCobrado(custo);

        this.saldo -= custo;

        return ticket;
    }

}