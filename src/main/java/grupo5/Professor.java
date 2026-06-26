package grupo5;

import java.time.LocalDateTime;

public class Professor extends Cliente {

    public Professor(String CPF_CNPJ, String nome, String celular) {
        super(CPF_CNPJ, nome, celular, 2);
    }

    @Override
    public void cadastraVeiculo(String placa) {
        if (getPlacas().size() >= 2) {
            throw new SetFullException();
        }
        getPlacas().add(placa);
    }

    @Override
    public Ticket calculaCusto(Ticket ticket, LocalDateTime horaSaida) {

        ticket.setHoraSaida(horaSaida);
        ticket.setValorCalculado(0.0);
        ticket.setDesconto(0.0);
        ticket.setValorCobrado(0.0);

        return ticket;
    }

}
