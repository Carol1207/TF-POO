package grupo5;

import java.time.LocalDateTime;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Operações | Estacionamento")
@Route(value = "", layout = MainLayout.class)
public class OperacoesView extends VerticalLayout {

    private final Estacionamento estacionamento;
    private final Grid<Ticket> gridAtivos = new Grid<>(Ticket.class, false);

    public OperacoesView(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
        setSizeFull();

        H2 titulo = new H2("Controle de Entrada e Saída");
        
        // Formulário de Entrada
        TextField txtPlaca = new TextField("Placa do Veículo");
        txtPlaca.setPlaceholder("ABC-1234");
        
        Button btnEntrada = new Button("Registrar Entrada", e -> {
            String placa = txtPlaca.getValue().toUpperCase().trim();
            if (placa.isEmpty()) {
                Notification.show("Insira uma placa válida!");
                return;
            }
            if (estacionamento.verificaPlaca(placa)) {
                estacionamento.entrada(placa, LocalDateTime.now());
                atualizarGrid();
                txtPlaca.clear();
                Notification.show("Entrada registrada com sucesso!");
            } else {
                Notification.show("ATENÇÃO: Placa bloqueada ou sem autorização!");
            }
        });
        btnEntrada.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout formEntrada = new HorizontalLayout(txtPlaca, btnEntrada);
        formEntrada.setVerticalComponentAlignment(Alignment.AUTO, btnEntrada);

        // Grid de Tickets Ativos
        configurarGrid();

        add(titulo, formEntrada, new H2("Veículos no Pátio"), gridAtivos);
        atualizarGrid();
    }

    private void configurarGrid() {
        gridAtivos.addColumn(Ticket::getPlaca).setHeader("Placa");
        gridAtivos.addColumn(Ticket::getHoraEntrada).setHeader("Hora Entrada");
        gridAtivos.addColumn(t -> t.getCliente() != null ? t.getCliente().getNome() : "Avulso").setHeader("Cliente");
        
        gridAtivos.addComponentColumn(ticket -> {
            Button btnSaida = new Button("Liberar / Saída", e -> abrirDialogoSaida(ticket));
            btnSaida.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return btnSaida;
        }).setHeader("Ações");
    }

    private void abrirDialogoSaida(Ticket ticketSimplificado) {
        // Calcula o custo com base na regra de negócio
        Ticket ticketCalculado = estacionamento.calculaCustoSaida(ticketSimplificado.getPlaca(), LocalDateTime.now());

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalhamento de Saída - " + ticketCalculado.getPlaca());

        VerticalLayout dialogLayout = new VerticalLayout(
            new Span("Valor Calculado: R$ " + ticketCalculado.getValorCalculado()),
            new Span("Desconto Aplicado: R$ " + ticketCalculado.getDesconto()),
            new Span("Valor Final Cobrado: R$ " + ticketCalculado.getValorCobrado())
        );

        NumberField txtValorPago = new NumberField("Valor Pago pelo Cliente (R$)");
        txtValorPago.setValue(ticketCalculado.getValorCobrado());
        dialogLayout.add(txtValorPago);

        Button btnConfirmar = new Button("Confirmar Pagamento e Saída", e -> {
            estacionamento.saida(ticketCalculado, txtValorPago.getValue());
            atualizarGrid();
            dialog.close();
            Notification.show("Ticket encerrado e veículo liberado!");
        });
        btnConfirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(dialogLayout);
        dialog.getFooter().add(btnConfirmar, new Button("Cancelar", e -> dialog.close()));
        dialog.open();
    }

    private void atualizarGrid() {
        gridAtivos.setItems(estacionamento.getTicketsAtivos().values());
    }
}
