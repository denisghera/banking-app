package ro.uvt.dp.gui;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class TicketListCell extends ListCell<String> {
    private final HBox content;
    private final Text ticketText;
    private final Button resolveButton;

    public TicketListCell(TicketResolver resolver) {
        super();

        ticketText = new Text();
        resolveButton = new Button("Resolve");
        content = new HBox(10, ticketText, resolveButton);

        resolveButton.setOnAction(event -> {
            String ticketDetails = getItem();
            if (ticketDetails != null && !ticketDetails.isEmpty()) {
                String ticketId = extractTicketId(ticketDetails);
                resolver.resolve(ticketId);
            }
        });
    }
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            ticketText.setText(item);
            setGraphic(content);
        }
    }
    private String extractTicketId(String ticketDetails) {
        String[] lines = ticketDetails.split("\n");
        for (String line : lines) {
            if (line.startsWith("ID: ")) {
                return line.substring(4);
            }
        }
        return null;
    }
    public interface TicketResolver {
        void resolve(String ticketId);
    }
}
