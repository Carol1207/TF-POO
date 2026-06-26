package grupo5;

import java.util.HashMap;

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

}
