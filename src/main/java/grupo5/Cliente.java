package grupo5;

import java.time.LocalDateTime;
import java.util.HashSet;

public abstract class Cliente {
    private String CPF_CNPJ;
    private String nome;
    private String celular;
    private HashSet<String> placas_veiculos;

    public Cliente (String CPF_CNPJ, String nome, String celular, int tam) {
        this.CPF_CNPJ = CPF_CNPJ;
        this.nome = nome;
        this.celular = celular;
        this.placas_veiculos = new HashSet<>(tam);
    }

    public String getCpf_cnpj() { return CPF_CNPJ; }

    public String getNome() { return nome; }

    public String getCelular() { return celular; }

    public HashSet<String> getPlacas() { return placas_veiculos; }

    public abstract void cadastraVeiculo(String placa);

    public void removeVeiculo(String placa) {
        placas_veiculos.remove(placa);
    }

    public abstract Ticket calculaCusto(Ticket ticket, LocalDateTime horaSaida);

}
