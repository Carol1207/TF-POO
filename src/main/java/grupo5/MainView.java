package grupo5;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
// tela principal com as abas de navegação
public class MainView extends VerticalLayout {

    public MainView(@Autowired ServicoDeEstacionamento servico) {

        AbaGuarita abaGuarita = new AbaGuarita(servico);
        AbaCadastro abaCadastro = new AbaCadastro(servico);
        AbaRelatorios abaRelatorios = new AbaRelatorios(servico);

        abaCadastro.setVisible(false);
        abaRelatorios.setVisible(false);

        Tab tabGuarita = new Tab("Guarita");
        Tab tabCadastro = new Tab("Cadastro de Clientes");
        Tab tabRelatorios = new Tab("Relatórios");

        Tabs tabs = new Tabs(tabGuarita, tabCadastro, tabRelatorios);
        tabs.setWidthFull();

        tabs.addSelectedChangeListener(event -> {
            // oculta todas as abas e mostra apenas a selecionada
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

        add(tabs, abaGuarita, abaCadastro, abaRelatorios);
        setSizeFull();
        setPadding(false);
    }
}
