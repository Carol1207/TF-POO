package grupo5;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class MainView extends VerticalLayout {

    public MainView(@Autowired ServicoDeEstacionamento servico) {
        // Inicializa as 3 abas principais
        AbaGuarita abaGuarita = new AbaGuarita(servico);
        AbaCadastro abaCadastro = new AbaCadastro(servico);
        AbaRelatorios abaRelatorios = new AbaRelatorios(servico);

        // Oculta todas por padrão exceto a primeira
        abaCadastro.setVisible(false);
        abaRelatorios.setVisible(false);

        // Cria os botões (Tabs) superiores
        Tab tabGuarita = new Tab("Guarita");
        Tab tabCadastro = new Tab("Cadastro de Clientes");
        Tab tabRelatorios = new Tab("Relatórios");

        Tabs tabs = new Tabs(tabGuarita, tabCadastro, tabRelatorios);
        tabs.setWidthFull();

        // Lógica para alternar as abas quando o usuário clica
        tabs.addSelectedChangeListener(event -> {
            abaGuarita.setVisible(false);
            abaCadastro.setVisible(false);
            abaRelatorios.setVisible(false);

            if (event.getSelectedTab().equals(tabGuarita)) {
                abaGuarita.setVisible(true);
            } else if (event.getSelectedTab().equals(tabCadastro)) {
                abaCadastro.setVisible(true);
            } else if (event.getSelectedTab().equals(tabRelatorios)) {
                abaRelatorios.setVisible(true);
            }
        });

        // Adiciona tudo na tela principal
        add(tabs, abaGuarita, abaCadastro, abaRelatorios);
        setSizeFull();
        setPadding(false);
    }
}
