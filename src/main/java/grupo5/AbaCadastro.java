package grupo5;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class AbaCadastro extends VerticalLayout {

    private final ServicoDeEstacionamento servico;

    private ComboBox<String> comboTipo;
    private TextField campoCpfCnpj;
    private TextField campoNome;
    private TextField campoCelular;
    private NumberField campoValorExtra;
    private TextField campoPlacas;

    public AbaCadastro(ServicoDeEstacionamento servico) {
        this.servico = servico;

        setAlignItems(Alignment.CENTER);
        setSizeFull();

        H2 titulo = new H2("Cadastro de Clientes Especiais");

        comboTipo = new ComboBox<>("Tipo de Cliente");
        comboTipo.setItems("Estudante", "Professor", "Empresa");
        comboTipo.setValue("Estudante");

        campoCpfCnpj = new TextField("CPF / CNPJ");
        campoNome = new TextField("Nome / Razão Social");
        campoCelular = new TextField("Celular");
        
        campoValorExtra = new NumberField("Saldo Inicial (R$)");
        campoValorExtra.setValue(0.0);

        campoPlacas = new TextField("Placas (Separadas por vírgula)");
        campoPlacas.setPlaceholder("Ex: ABC1A23, XYZ9B87");
        campoPlacas.setWidth("300px");

        // Altera o label do valor extra dependendo do tipo de cliente selecionado
        comboTipo.addValueChangeListener(e -> {
            String tipo = e.getValue();
            if ("Professor".equals(tipo)) {
                campoValorExtra.setVisible(false);
            } else if ("Empresa".equals(tipo)) {
                campoValorExtra.setVisible(true);
                campoValorExtra.setLabel("Débito Inicial (R$)");
            } else {
                campoValorExtra.setVisible(true);
                campoValorExtra.setLabel("Saldo Inicial (R$)");
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(comboTipo, campoCpfCnpj, campoNome, campoCelular, campoValorExtra, campoPlacas);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        formLayout.setMaxWidth("600px");

        Button btnSalvar = new Button("Salvar Cliente");
        btnSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSalvar.addClickListener(e -> salvarCliente());

        add(titulo, formLayout, btnSalvar);
    }

    private void salvarCliente() {
        try {
            String tipo = comboTipo.getValue();
            String cpfCnpj = campoCpfCnpj.getValue().trim();
            String nome = campoNome.getValue().trim();
            String celular = campoCelular.getValue().trim();
            double valorExtra = campoValorExtra.isVisible() && campoValorExtra.getValue() != null ? campoValorExtra.getValue() : 0.0;
            String placasText = campoPlacas.getValue().trim();

            if (cpfCnpj.isEmpty() || nome.isEmpty()) {
                mostrarErro("CPF/CNPJ e Nome são obrigatórios!");
                return;
            }

            // Verifica se cliente já existe
            CadastroCliente cadastro = servico.getCadastroCliente();
            if (cadastro.getClientes().containsKey(cpfCnpj)) {
                mostrarErro("Já existe um cliente cadastrado com este CPF/CNPJ.");
                return;
            }

            Cliente novoCliente;
            switch (tipo) {
                case "Estudante":
                    novoCliente = new Estudante(cpfCnpj, nome, celular, (int) valorExtra);
                    break;
                case "Professor":
                    novoCliente = new Professor(cpfCnpj, nome, celular);
                    break;
                case "Empresa":
                    novoCliente = new Empresa(cpfCnpj, nome, celular, (int) valorExtra);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo inválido.");
            }

            // Adiciona as placas
            if (!placasText.isEmpty()) {
                String[] arrayPlacas = placasText.split(",");
                for (String p : arrayPlacas) {
                    novoCliente.cadastraVeiculo(p.trim().toUpperCase());
                }
            }

            // Insere na memória
            cadastro.getClientes().put(cpfCnpj, novoCliente);
            for (String placa : novoCliente.getPlacas()) {
                cadastro.getPlacasClientes().put(placa, novoCliente);
            }

            mostrarSucesso("Cliente " + nome + " cadastrado com sucesso!");
            limparFormulario();

        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void limparFormulario() {
        campoCpfCnpj.clear();
        campoNome.clear();
        campoCelular.clear();
        campoValorExtra.setValue(0.0);
        campoPlacas.clear();
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
