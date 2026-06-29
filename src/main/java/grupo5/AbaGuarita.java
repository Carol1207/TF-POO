package grupo5;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDateTime;

public class AbaGuarita extends VerticalLayout {

    private final ServicoDeEstacionamento servico;
    private final TextField campoPlaca;

    public AbaGuarita(ServicoDeEstacionamento servico) {
        this.servico = servico;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        H2 titulo = new H2("Controle de Cancela (Guarita)");
        Paragraph sub = new Paragraph("Digite a placa do veículo para registrar entrada ou saída.");

        campoPlaca = new TextField("Placa do Veículo");
        campoPlaca.setPlaceholder("Ex: ABC1D23");
        campoPlaca.setWidth("300px");

        Button btnEntrada = new Button("Registrar Entrada");
        btnEntrada.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnEntrada.addClickListener(e -> registrarEntrada());

        Button btnSaida = new Button("Registrar Saída");
        btnSaida.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnSaida.addClickListener(e -> registrarSaida());

        HorizontalLayout botoes = new HorizontalLayout(btnEntrada, btnSaida);

        add(titulo, sub, campoPlaca, botoes);
    }

    private void registrarEntrada() {
        String placa = campoPlaca.getValue().trim().toUpperCase();
        if (placa.isEmpty()) {
            mostrarErro("A placa não pode estar vazia.");
            return;
        }

        try {
            servico.getEstacionamento().entrada(placa, LocalDateTime.now());
            mostrarSucesso("Entrada do veículo " + placa + " registrada com sucesso!");
            campoPlaca.clear();
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void registrarSaida() {
        String placa = campoPlaca.getValue().trim().toUpperCase();
        if (placa.isEmpty()) {
            mostrarErro("A placa não pode estar vazia.");
            return;
        }

        try {
            Estacionamento est = servico.getEstacionamento();
            Ticket ticket = est.calculaCustoSaida(placa, LocalDateTime.now());
            est.saida(ticket, ticket.getValorCobrado());
            String msg = String.format("Saída registrada! Placa: %s | Valor Cobrado: R$ %.2f",
                    placa, ticket.getValorCobrado());
            mostrarSucesso(msg);
            campoPlaca.clear();
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void mostrarSucesso(String msg) {
        Notification notif = Notification.show(msg, 3000, Notification.Position.MIDDLE);
        notif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void mostrarErro(String msg) {
        Notification notif = Notification.show("Erro: " + msg, 4000, Notification.Position.MIDDLE);
        notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
