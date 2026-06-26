package grupo5;

import java.time.LocalDateTime;

public class Professor extends Cliente {

    public Professor (String CPF_CNPJ, String nome, String celular) {
        super(CPF_CNPJ, nome, celular, 2);
    }

    @Override
    public void cadastraVeiculo(String placa) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Ticket calculaCusto(Ticket ticket, LocalDateTime horaSaida) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
