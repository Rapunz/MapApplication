
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public abstract class AddDialog extends Alert {

	private static final int DEFAULT_SPACING = 10;

	private TextField nameField = new TextField();
	private GridPane grid = new GridPane();

	public AddDialog() {
		super(AlertType.CONFIRMATION);
		grid.addRow(0, new Label("Name:"), nameField);
		grid.setVgap(DEFAULT_SPACING);
		grid.setHgap(DEFAULT_SPACING);
		getDialogPane().setContent(grid);
		setHeaderText(null);
	}

	protected GridPane getGrid() {
		return grid;
	}

	public String getName() {
		return nameField.getText();
	}
}
