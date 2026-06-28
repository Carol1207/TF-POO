package grupo5;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AbaRelatorios extends VerticalLayout {

    private final ServicoDeEstacionamento servico;
    private ComboBox<String> comboRelatorios;
    private DatePicker dataInicio;
    private DatePicker dataFim;
    private TextField campoFiltroCpf;
    private TextArea areaResultado;

    public AbaRelatorios(ServicoDeEstacionamento servico) {
        this.servico = servico;

        setAlignItems(Alignment.CENTER);
        setSizeFull();

        H2 titulo = new H2("Central de Relatórios");

        comboRelatorios = new ComboBox<>("Selecione o Relatório");
        comboRelatorios.setItems(
                "1. Valor Total Arrecadado",
                "2. Situação de um Cliente",
                "3. Registros de Cliente Cadastrado",
                "4. Registros de Veículos Avulsos",
                "5. Lista de Clientes Impedidos",
                "6. Top 10 Clientes Mais Frequentes"
        );
        comboRelatorios.setWidth("400px");
        comboRelatorios.setValue("1. Valor Total Arrecadado");

        dataInicio = new DatePicker("Data Inicial");
        dataInicio.setValue(LocalDate.now().minusMonths(1));

        dataFim = new DatePicker("Data Final");
        dataFim.setValue(LocalDate.now());

        campoFiltroCpf = new TextField("CPF/CNPJ do Cliente (Se aplicável)");

        Button btnGerar = new Button("Gerar Relatório");
        btnGerar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGerar.addClickListener(e -> gerarRelatorio());

        HorizontalLayout filtros = new HorizontalLayout(dataInicio, dataFim, campoFiltroCpf);
        filtros.setAlignItems(Alignment.BASELINE);

        areaResultado = new TextArea("Resultado");
        areaResultado.setWidth("800px");
        areaResultado.setHeight("400px");
        areaResultado.setReadOnly(true);

        add(titulo, comboRelatorios, filtros, btnGerar, areaResultado);
    }

    private void gerarRelatorio() {
        Relatorios relatorios = servico.getRelatorios();
        String opcao = comboRelatorios.getValue();
        StringBuilder sb = new StringBuilder();

        LocalDateTime inicio = dataInicio.getValue() != null ? dataInicio.getValue().atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime fim = dataFim.getValue() != null ? dataFim.getValue().atTime(23, 59, 59) : LocalDateTime.MAX;
        String cpfCnpj = campoFiltroCpf.getValue().trim();

        if (opcao == null) return;

        try {
            if (opcao.startsWith("1")) {
                double total = relatorios.getValorArrecadado(inicio, fim, null);
                sb.append(String.format("Valor Total Arrecadado no período: R$ %.2f\n", total));
            } 
            else if (opcao.startsWith("2")) {
                if (cpfCnpj.isEmpty()) {
                    sb.append("Por favor, digite o CPF/CNPJ no campo de filtro.");
                } else {
                    Relatorios.SituacaoCliente sit = relatorios.getSituacaoCliente(cpfCnpj);
                    if (sit == null) {
                        sb.append("Cliente não encontrado.");
                    } else {
                        sb.append("Cliente: ").append(sit.cliente.getNome()).append("\n");
                        sb.append("Saldo/Débito: R$ ").append(sit.saldoOuDebito).append("\n");
                        sb.append("Veículos Atualmente Estacionados:\n");
                        if (sit.veiculosEstacionados.isEmpty()) {
                            sb.append("  - Nenhum");
                        } else {
                            for (Ticket t : sit.veiculosEstacionados) {
                                sb.append("  - Placa: ").append(t.getPlaca())
                                  .append(" | Entrada: ").append(t.getHoraEntrada()).append("\n");
                            }
                        }
                    }
                }
            }
            else if (opcao.startsWith("3")) {
                if (cpfCnpj.isEmpty()) {
                    sb.append("Por favor, digite o CPF/CNPJ no campo de filtro.");
                } else {
                    List<Ticket> tickets = relatorios.getRegistrosClienteCadastrado(cpfCnpj, inicio, fim);
                    sb.append("Registros de Histórico (").append(tickets.size()).append(" encontrados):\n");
                    for (Ticket t : tickets) {
                        sb.append("Placa: ").append(t.getPlaca())
                          .append(" | Entrada: ").append(t.getHoraEntrada())
                          .append(" | Saída: ").append(t.getHoraSaida())
                          .append(" | Cobrado: R$ ").append(t.getValorCobrado()).append("\n");
                    }
                }
            }
            else if (opcao.startsWith("4")) {
                List<Ticket> tickets = relatorios.getRegistrosAvulsos(inicio, fim);
                sb.append("Registros Avulsos (").append(tickets.size()).append(" encontrados):\n");
                for (Ticket t : tickets) {
                    sb.append("Placa: ").append(t.getPlaca())
                      .append(" | Entrada: ").append(t.getHoraEntrada())
                      .append(" | Saída: ").append(t.getHoraSaida())
                      .append(" | Cobrado: R$ ").append(t.getValorCobrado()).append("\n");
                }
            }
            else if (opcao.startsWith("5")) {
                Relatorios.ClientesImpedidos impedidos = relatorios.getClientesImpedidos();
                sb.append("--- Placas Avulsas Bloqueadas (Inadimplentes) ---\n");
                for (String p : impedidos.placasBloqueadasAvulsas) {
                    sb.append("Placa: ").append(p).append("\n");
                }
                sb.append("\n--- Empresas Inadimplentes ---\n");
                for (Empresa e : impedidos.empresasInadimplentes) {
                    sb.append("Empresa: ").append(e.getNome()).append(" | CNPJ: ").append(e.getCpf_cnpj())
                      .append(" | Débitos: R$ ").append(e.getDebitos()).append("\n");
                }
            }
            else if (opcao.startsWith("6")) {
                int ano = dataFim.getValue() != null ? dataFim.getValue().getYear() : LocalDate.now().getYear();
                List<Map.Entry<String, Long>> top10 = relatorios.getTop10ClientesFrequentes(ano);
                sb.append("Top 10 Clientes Frequentes em ").append(ano).append(":\n\n");
                int rank = 1;
                for (Map.Entry<String, Long> entry : top10) {
                    sb.append(rank).append("º lugar: ").append(entry.getKey())
                      .append(" (").append(entry.getValue()).append(" visitas)\n");
                    rank++;
                }
            }
            areaResultado.setValue(sb.toString());
        } catch (Exception ex) {
            areaResultado.setValue("Erro ao gerar relatório: " + ex.getMessage());
        }
    }
}
