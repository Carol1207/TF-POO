package grupo5;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Relatorios {

    private Estacionamento estacionamento;
    private CadastroCliente cadastroCliente;

    public Relatorios(Estacionamento estacionamento, CadastroCliente cadastroCliente) {
        this.estacionamento = estacionamento;
        this.cadastroCliente = cadastroCliente;
    }

    public double getValorArrecadado(LocalDateTime inicio, LocalDateTime fim, CategoriaCliente categoria) {
        return estacionamento.getTicketsEncerrados().stream()
                .filter(t -> t.getHoraSaida() != null && !t.getHoraSaida().isBefore(inicio)
                        && !t.getHoraSaida().isAfter(fim))
                .filter(t -> categoria == null || Estacionamento.categoriaCliente(t.getCliente()) == categoria)
                .mapToDouble(Ticket::getValorCobrado)
                .sum();
    }

    public SituacaoCliente getSituacaoCliente(String cpfCnpj) {
        Cliente cliente = cadastroCliente.getClientes().get(cpfCnpj);
        if (cliente == null) {
            return null;
        }

        List<Ticket> veiculosEstacionados = estacionamento.getTicketsAtivos().values().stream()
                .filter(t -> t.getCliente() != null && t.getCliente().getCpf_cnpj().equals(cpfCnpj))
                .collect(Collectors.toList());

        double saldoDebito = 0.0;
        if (cliente instanceof Estudante) {
            saldoDebito = ((Estudante) cliente).getSaldo();
        } else if (cliente instanceof Empresa) {
            saldoDebito = ((Empresa) cliente).getDebitos();
        }

        return new SituacaoCliente(cliente, veiculosEstacionados, saldoDebito);
    }

    public static class SituacaoCliente {
        public Cliente cliente;
        public List<Ticket> veiculosEstacionados;
        public double saldoOuDebito;

        public SituacaoCliente(Cliente cliente, List<Ticket> veiculosEstacionados, double saldoOuDebito) {
            this.cliente = cliente;
            this.veiculosEstacionados = veiculosEstacionados;
            this.saldoOuDebito = saldoOuDebito;
        }
    }

    public List<Ticket> getRegistrosClienteCadastrado(String cpfCnpj, LocalDateTime inicio, LocalDateTime fim) {
        return estacionamento.getTicketsEncerrados().stream()
                .filter(t -> t.getCliente() != null && t.getCliente().getCpf_cnpj().equals(cpfCnpj))
                .filter(t -> t.getHoraSaida() != null && !t.getHoraSaida().isBefore(inicio)
                        && !t.getHoraSaida().isAfter(fim))
                .collect(Collectors.toList());
    }

    public List<Ticket> getRegistrosAvulsos(LocalDateTime inicio, LocalDateTime fim) {
        return estacionamento.getTicketsEncerrados().stream()
                .filter(t -> t.getCliente() == null)
                .filter(t -> t.getHoraSaida() != null && !t.getHoraSaida().isBefore(inicio)
                        && !t.getHoraSaida().isAfter(fim))
                .collect(Collectors.toList());
    }

    public ClientesImpedidos getClientesImpedidos() {
        List<String> placasBloqueadas = new ArrayList<>(estacionamento.getPlacasBloqueadas());

        List<Empresa> empresasInadimplentes = cadastroCliente.getClientes().values().stream()
                .filter(c -> c instanceof Empresa)
                .map(c -> (Empresa) c)
                .filter(Empresa::isInadimplente)
                .collect(Collectors.toList());

        return new ClientesImpedidos(placasBloqueadas, empresasInadimplentes);
    }

    public static class ClientesImpedidos {
        public List<String> placasBloqueadasAvulsas;
        public List<Empresa> empresasInadimplentes;

        public ClientesImpedidos(List<String> placasBloqueadasAvulsas, List<Empresa> empresasInadimplentes) {
            this.placasBloqueadasAvulsas = placasBloqueadasAvulsas;
            this.empresasInadimplentes = empresasInadimplentes;
        }

        public List<String> getPlacasBloqueadasAvulsas() {
            // TODO Auto-generated method stub
            return placasBloqueadasAvulsas;
        }
    }

    public List<Map.Entry<String, Long>> getTop10ClientesFrequentes(int ano) {
        Map<String, Long> contagem = estacionamento.getTicketsEncerrados().stream()
                .filter(t -> t.getHoraSaida() != null && t.getHoraSaida().getYear() == ano)
                .collect(Collectors.groupingBy(
                        t -> (t.getCliente() == null) ? "Avulso (Placa: " + t.getPlaca() + ")"
                                : "Cadastrado (CPF/CNPJ: " + t.getCliente().getCpf_cnpj() + ") - "
                                        + t.getCliente().getNome(),
                        Collectors.counting()));

        return contagem.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .collect(Collectors.toList());
    }
}
