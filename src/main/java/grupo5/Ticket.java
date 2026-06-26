package grupo5;

import java.time.LocalDateTime;

public class Ticket {
    private String placa;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;
    private double valorCalculado;
    private double desconto;
    private double valorCobrado;
    private Cliente cliente;

    public Ticket(String placa, LocalDateTime horaEntrada, Cliente cliente) {
        if (!Estacionamento.verificaPlaca(placa)) {
            throw new IllegalArgumentException();
        }
        this.placa = placa;
        this.horaEntrada = horaEntrada;
        this.cliente = cliente;
    }

    public String getPlaca() {
        return placa;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }

    public double getValorCalculado() {
        return valorCalculado;
    }

    public double getValorCobrado() {
        return valorCobrado;
    }    

    public Cliente getCliente() {
        return cliente;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setHoraSaida(LocalDateTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public void setValorCalculado(double valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public void setValorCobrado(double valorCobrado) {
        this.valorCobrado = valorCobrado;
    }



}
