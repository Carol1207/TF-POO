package grupo5;

import java.util.HashMap;
import java.util.Map;

public class CadastroCliente {
    private HashMap<String, Cliente> clientes;
    private HashMap<String, Cliente> placasClientes;

    public CadastroCliente() {
        clientes = new HashMap<>();
        placasClientes = new HashMap<>();
    }

    public HashMap<String, Cliente> getClientes() {
        return clientes;
    }

    public HashMap<String, Cliente> getPlacasClientes() {
        return placasClientes;
    }

    public void setClientes(Map<String, Cliente> clientesCarregados) {
        this.clientes = new HashMap<>(clientesCarregados);
        this.placasClientes.clear();
        for (Cliente cliente : this.clientes.values()) {
            for (String placa : cliente.getPlacas()) {
                this.placasClientes.put(placa, cliente);
            }
        }
    }

}