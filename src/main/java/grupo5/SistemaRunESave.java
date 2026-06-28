package grupo5;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class SistemaRunESave {
    PersistenciaClientes persCli = new PersistenciaClientes();
    
    @PostConstruct
    public void carregarDados(){
        System.out.println("Carregando dados do sistema...");
        persCli.carregarClientes();
    }

    @PreDestroy
    public void salvarArquivoDat(List<Cliente> clientes) {
        System.out.println("Armazenando dados do sistema...");
        persCli.armazenarClientes(null);
    }
}
