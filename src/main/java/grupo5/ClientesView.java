package grupo5;

import grupo5.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Clientes | Estacionamento")
@Route(value = "clientes", layout = MainLayout.class)
public class ClientesView extends VerticalLayout {

    private final CadastroCliente cadastroCliente;
    private final Grid<Cliente> gridClientes = new Grid<>(Cliente.class, false);

    public ClientesView(CadastroCliente cadastroCliente) {
        this.cadastroCliente = cadastroCliente;
        setSizeFull();

        H2 titulo = new H2("Clientes Cadastrados no Sistema");

        gridClientes.addColumn(Cliente::getNome).setHeader("Nome").setSortable(true);
        gridClientes.addColumn(Cliente::getCpf_cnpj).setHeader("CPF / CNPJ");
        gridClientes.addColumn(Cliente::getCelular).setHeader("Celular");
        
        // Coluna dinâmica mapeando as subclasses da herança do UML
        gridClientes.addColumn(cliente -> {
            if (cliente instanceof Estudante) return "Estudante";
            if (cliente instanceof Professor) return "Professor";
            if (cliente instanceof Empresa) return "Empresa";
            return "Desconhecido";
        }).setHeader("Tipo de Vínculo");

        // Detalhes financeiros específicos de cada subclasse
        gridClientes.addColumn(cliente -> {
            if (cliente instanceof Estudante) {
                return "Saldo: R$ " + ((Estudante) cliente).getSaldo();
            } else if (cliente instanceof Empresa) {
                return "Débitos: R$ " + ((Empresa) cliente).getDebitos() + 
                       (((Empresa) cliente).isInadimplente() ? " (INADIMPLENTE)" : " (OK)");
            }
            return "Isento / Diária Fixa";
        }).setHeader("Situação Financeira");

        gridClientes.setItems(cadastroCliente.getClientes().values());

        add(titulo, gridClientes);
    }
}
