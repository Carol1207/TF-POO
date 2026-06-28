package grupo5;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.util.StandardCharset;

/*
    * Formato do arquivo CSV de tickets:
    * placa, horaEntrada, horaSaida, cpfCnpj, valorCalculado, desconto, valorCobrado
    * 
    * onde:
    *   placa               :   placa do veículo
    *   horaEntrada         :   ISO_LOCAL_DATE_TIME (ex.: 2026-06-17T10:30:00)
    *   horaSaida           :   ISO_LOCAL_DATE_TIME ou "null" se veículo ainda estacionado
    *   cpfCnpj             :   CPF ou CNPJ do cliente, ou "0" para cliente avulso
    *   valorCalculado      :   valor calculado ou -1 se ainda não calculado
    *   desconto            :   desconto aplicado ou -1 se ainda não calculado
    *   valorCobrado        :   valor cobrado ou -1 se ainda não calculado

*/

public class PersistenciaTickets {

    public static void armazenarTickets(List<Ticket> tickets, String nomeArquivo) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(nomeArquivo), StandardCharsets.UTF_8)) {
            for (Ticket ticket : tickets) {
                String cpfCnpj = (ticket.getCliente() != null) ? ticket.getCliente().getCpf_cnpj() : "0";
                String horaSaida = (ticket.getHoraSaida() != null) ? ticket.getHoraSaida().toString() : "null";
                String linha = ticket.getPlaca() + "," +
                        ticket.getHoraEntrada().toString() + "," +
                        horaSaida + "," +
                        cpfCnpj + "," +
                        ticket.getValorCalculado() + "," +
                        ticket.getDesconto() + "," +
                        ticket.getValorCobrado();
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao armazenar tickets: " + e.getMessage(), e);
        }
    }

    public static List<Ticket> carregarTickets(String nomeArquivo, Map<String, Cliente> clientes) {
        List<Ticket> tickets = new ArrayList<>();

        try (var linhas = Files.lines(Path.of(nomeArquivo), StandardCharset.UTF_8)) {
            linhas.map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .forEach(l -> {
                        String[] partes = l.split(",");
                        String placa = partes[0];
                        LocalDateTime entrada = LocalDateTime.parse(partes[1]);
                        LocalDateTime saida = partes[2].equals("null") ? null : LocalDateTime.parse(partes[2]);
                        String cpfCnpj = partes[3];
                        double valorCalculado = Double.parseDouble(partes[4]);
                        double desconto = Double.parseDouble(partes[5]);
                        double valorCobrado = Double.parseDouble(partes[6]);

                        Cliente cliente = cpfCnpj.equals("0") ? null : clientes.get(cpfCnpj);

                        Ticket ticket = new Ticket(placa, entrada, cliente);

                        if (saida == null) {
                            ticket.setHoraSaida(saida);
                            ticket.setValorCalculado(valorCalculado);
                            ticket.setDesconto(desconto);
                            ticket.setValorCobrado(valorCobrado);
                        }

                        tickets.add(ticket);
                    });
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tickets: " + e.getMessage(), e);
        }
        return tickets;
    }

}
