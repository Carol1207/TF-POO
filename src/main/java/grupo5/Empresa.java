package grupo5;

import java.time.LocalDateTime;

public class Empresa extends Cliente {
    public static final double DIARIA = 20.0;
    private int debitos;

    public Empresa(String CPF_CNPJ, String nome, String celular, int valorExtra) {
        super(CPF_CNPJ, nome, celular, 100);
        debitos = valorExtra;
    }

    public int getDebitos() {
        return debitos;
    }

    public static final double MULTA = 20.0;
    private boolean inadimplente;

    public void adicionaDebito(int valor) {
        this.debitos += valor;
    }

    public boolean isInadimplente() {
        return inadimplente;
    }

    public void setInadimplente(boolean inadimplente) {
        this.inadimplente = inadimplente;
    }

    public void pagarBoleto() {
        this.debitos = 0;
        this.inadimplente = false;
    }

    @Override
    public void cadastraVeiculo(String placa) {
        getPlacas().add(placa);
    }

    @Override
    public Ticket calculaCusto(Ticket ticket, LocalDateTime horaSaida) {
        int dias = Estacionamento.quantidadeDiarias(ticket.getHoraEntrada(), horaSaida);
        double custo = (dias + 1) * DIARIA + (dias * MULTA);

        ticket.setHoraSaida(horaSaida);
        ticket.setValorCalculado(custo);
        ticket.setDesconto(0.0);
        ticket.setValorCobrado(custo);

        this.debitos += custo;

        return ticket;
    }
}
