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
    *   tipo        :   estudante | professor | empresa
    *   cpf_cnpj    :   CPF (estudante/professor) ou CNPJ (empresa)
    *   nome        :   nome completo ou razão social
    *   celular     :   número de telefone
    *   valorExtra  :   créditos (estudante) ou débitos (empresa)
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
                } else if (cliente instanceof Professor professor) {
                    linha.append("Professor, ");
                    linha.append(professor.getCpf_cnpj()).append(", ");
                    linha.append(professor.getNome()).append(", ");
                    linha.append(professor.getCelular()).append(", ");
                    linha.append(0);
                } else if (cliente instanceof Empresa empresa) {
                    linha.append("Empresa, ");
                    linha.append(empresa.getCpf_cnpj()).append(", ");
                    linha.append(empresa.getNome()).append(", ");
                    linha.append(empresa.getCelular()).append(", ");
                    linha.append(empresa.getDebitos());
                } else {
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
            linhas.map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .forEach(l -> {
                        String[] partes = l.split(",");
                        String tipo = partes[0].trim();
                        String cpfCnpj = partes[1].trim();
                        String nome = partes[2].trim();
                        String celular = partes[3].trim();
                        double valorExtra = Double.parseDouble(partes[4].trim());
                        Cliente cliente = switch (tipo.toLowerCase()) {
                            case "estudante" -> new Estudante(cpfCnpj, nome, celular, (int) valorExtra);
                            case "professor" -> new Professor(cpfCnpj, nome, celular);
                            case "empresa" -> new Empresa(cpfCnpj, nome, celular, (int) valorExtra);
                            default -> throw new IllegalArgumentException("Tipo de cliente desconhecido: " + tipo);
                        };

                        for (int i = 5; i < partes.length; i++) {
                            cliente.cadastraVeiculo(partes[i].trim().toUpperCase());
                        }
                        clientes.put(cpfCnpj, cliente);
                    });
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }
}
