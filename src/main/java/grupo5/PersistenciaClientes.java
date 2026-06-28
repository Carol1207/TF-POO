package grupo5;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/*
    * Formato do arquivo CSV "clientes.dat":
    * tipo, cpf_cnpj, nome, celular, valorExtra, placa1, placa2, ...
    * 
    * onde:
    *   tipo        :   Estudante | Professor | Empresa
    *   cpf_cnpj    :   CPF (Estudante/Professor) ou CNPJ (Empresa)
    *   nome        :   nome completo ou razão social
    *   celular     :   número de telefone
    *   valorExtra  :   créditos (Estudante), débitos (Empresa) ou 0 (Professor)
    *   placas      :   zero ou mais placas de veículos associados    

*/

public class PersistenciaClientes {
    private static final String NOME_ARQUIVO = "clientes.dat";

    public static void armazenarClientes(Map<String, Cliente> clientes) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(NOME_ARQUIVO), StandardCharsets.UTF_8)) {
            for (Cliente cliente : clientes.values()) {
                StringBuilder linha = new StringBuilder();
                if (cliente instanceof Estudante estudante) {
                    linha.append("Estudante, ");
                    linha.append(estudante.getCpf_cnpj()).append(", ");
                    linha.append(estudante.getNome()).append(", ");
                    linha.append(estudante.getCelular()).append(", ");
                    linha.append(estudante.getSaldo());
                }

                if (cliente instanceof Professor professor) {
                    linha.append("Professor, ");
                    linha.append(professor.getCpf_cnpj()).append(", ");
                    linha.append(professor.getNome()).append(", ");
                    linha.append(professor.getCelular());
                    linha.append(0);
                }

                if (cliente instanceof Empresa empresa) {
                    linha.append("Empresa, ");
                    linha.append(empresa.getCpf_cnpj()).append(", ");
                    linha.append(empresa.getNome()).append(", ");
                    linha.append(empresa.getCelular());
                    linha.append(empresa.getDebitos());
                }

                else {
                    throw new IllegalArgumentException(
                            "Tipo de cliente desconhecido: " + cliente.getClass().getSimpleName());
                }

                for (String placa : cliente.getPlacas()) {
                    linha.append(", ").append(placa);
                }

                writer.write(linha.toString());
                writer.newLine();

            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao armazenar clientes: " + e.getMessage(), e);
        }
    }

    public static Map<String, Cliente> carregarClientes() {
        Map<String, Cliente> clientes = new HashMap<>();

        try (var linhas = Files.lines(Path.of(NOME_ARQUIVO), StandardCharsets.UTF_8)) {
            linhas.map(String::trim) // Remove brancos no início e fim da linha
                    .filter(l -> !l.isEmpty()) // Ignora as linhas em branco
                    .forEach(l -> {
                        String[] partes = l.split(",");
                        String tipo = partes[0];
                        String cpfCnpj = partes[1];
                        String nome = partes[2];
                        String celular = partes[3];
                        double valorExtra = Double.parseDouble(partes[4]);
                        Cliente cliente = switch (tipo) {
                            case "Estudante" -> new Estudante(cpfCnpj, nome, celular, (int) valorExtra);
                            case "Professor" -> new Professor(cpfCnpj, nome, celular);
                            case "Empresa" -> new Empresa(cpfCnpj, nome, celular, (int) valorExtra);
                            default -> throw new IllegalArgumentException("Tipo de cliente desconhecido: " + tipo);
                        };

                        // As placas estarão da "partes[5]" em diante

                        for (int i = 5; i < partes.length; i++) {
                            cliente.cadastraVeiculo(partes[i]);
                        }
                        clientes.put(cpfCnpj, cliente);
                    });
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }
}
