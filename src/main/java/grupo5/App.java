package grupo5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    // Instancia o repositório de clientes global da aplicação
    @Bean
    public CadastroCliente cadastroCliente() {
        CadastroCliente cadastro = new CadastroCliente();
        // Inicialize clientes mockados para teste aqui se desejar
        return cadastro;
    }

    // Instancia o mecanismo do estacionamento injetando o cadastro de clientes
    @Bean
    public Estacionamento estacionamento(CadastroCliente cadastroCliente) {
        return new Estacionamento(cadastroCliente);
    }

    // Instancia o motor de relatórios associado
    @Bean
    public Relatorios relatorios(Estacionamento estacionamento, CadastroCliente cadastroCliente) {
        return new Relatorios(estacionamento, cadastroCliente);
    }
}
