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

    // carrega os dados quando o sistema inicia
    @PostConstruct
    public void init() {
        try {
            Map<String, Cliente> clientes = PersistenciaClientes.carregarClientes();
            if (clientes != null) {
                cadastroCliente.setClientes(clientes);
            }

            List<Ticket> tickets = PersistenciaTickets.carregarTickets("encerrados.dat", clientes);
            if (tickets != null) {
                estacionamento.setTicketsEncerrados(tickets);
            }
            System.out.println("Dados de estacionamento carregados");
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados na inicialização: " + e.getMessage());
        }
    }

    // salva os dados por segurança quando o sistema é encerrado (backup)
    @PreDestroy
    public void destroy() {
        try {
            PersistenciaClientes.armazenarClientes(cadastroCliente.getClientes());
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
