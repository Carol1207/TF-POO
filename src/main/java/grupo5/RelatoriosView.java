package grupo5;

import grupo5.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDateTime;

@PageTitle("Relatórios | Estacionamento")
@Route(value = "relatorios", layout = MainLayout.class)
public class RelatoriosView extends VerticalLayout {

    private final Relatorios relatorios;
    private final Grid<String> gridPlacasBloqueadas = new Grid<>();
    private final Span txtFaturamento = new Span("R$ 0.00");

    public RelatoriosView(Relatorios relatorios) {
        this.relatorios = relatorios;
        setSizeFull();

        add(new H2("Painel de Relatórios e Auditoria"));

        // Seção 1: Calculadora de Arrecadação
        H3 subFaturamento = new H3("Consultar Valor Arrecadado");
        DateTimePicker dataInicio = new DateTimePicker("Data Início", LocalDateTime.now().minusDays(30));
        DateTimePicker dataFim = new DateTimePicker("Data Fim", LocalDateTime.now());
        ComboBox<CategoriaCliente> comboCategoria = new ComboBox<>("Categoria", CategoriaCliente.values());
        comboCategoria.setValue(CategoriaCliente.AVULSO);

        Button btnCalcular = new Button("Calcular Total", e -> {
            double total = relatorios.getValorArrecadado(dataInicio.getValue(), dataFim.getValue(), comboCategoria.getValue());
            txtFaturamento.setText("Total Arrecadado: R$ " + total);
        });

        HorizontalLayout filtroFaturamento = new HorizontalLayout(dataInicio, dataFim, comboCategoria, btnCalcular);
        filtroFaturamento.setVerticalComponentAlignment(Alignment.BOTTOM, btnCalcular);
        
        txtFaturamento.getStyle().set("font-weight", "bold").set("font-size", "1.2em");

        // Seção 2: Clientes Impedidos / Bloqueados
        H3 subImpedidos = new H3("Lista de Placas e Empresas Impedidas");
        gridPlacasBloqueadas.addColumn(placa -> placa).setHeader("Placa com Restrição");
        
        Button btnCarregarRestricoes = new Button("Atualizar Lista Negra", e -> {
            ClientesImpedidos impedidos = relatorios.getClientesImpedidos();
            // Junta as listas fictícias ou reais geradas pelo método do UML
            gridPlacasBloqueadas.setItems(impedidos.getPlacasBloqueadasAvulsas());
        });

        add(subFaturamento, filtroFaturamento, txtFaturamento, subImpedidos, btnCarregarRestricoes, gridPlacasBloqueadas);
    }
}
