package grupo5;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ServicoDeEstacionamento {

    private final CadastroCliente cadastroCliente;
    private final Estacionamento estacionamento;
    private final Relatorios relatorios;

    public ServicoDeEstacionamento() {
        this.cadastroCliente = new CadastroCliente();
        this.estacionamento = new Estacionamento(this.cadastroCliente);
        this.relatorios = new Relatorios(this.estacionamento, this.cadastroCliente);
    }

    @PostConstruct
    public void init() {
        try {
            // Carrega os clientes
            Map<String, Cliente> clientes = PersistenciaClientes.carregarClientes();
            if (clientes != null) {
                cadastroCliente.setClientes(clientes);
            }

            // Carrega o histórico de tickets (encerrados)
            List<Ticket> tickets = PersistenciaTickets.carregarTickets("encerrados.dat", clientes);
            if (tickets != null) {
                estacionamento.setTicketsEncerrados(tickets);
            }
            System.out.println("Dados de estacionamento carregados");
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados na inicialização: " + e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            // Salva os clientes
            PersistenciaClientes.armazenarClientes(cadastroCliente.getClientes());

            // Salva o histórico de tickets (encerrados)
            PersistenciaTickets.armazenarTickets(estacionamento.getTicketsEncerrados(), "encerrados.dat");
            System.out.println("Dados de estacionamento salvos");
        } catch (Exception e) {
            System.err.println("Erro ao salvar dados no encerramento: " + e.getMessage());
        }
    }

    public CadastroCliente getCadastroCliente() {
        return cadastroCliente;
    }

    public Estacionamento getEstacionamento() {
        return estacionamento;
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }
}
