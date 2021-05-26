

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CoordinatesDialog extends Alert {

	private static final int STANDARD_SPACING = 5;
	private TextField xfield = new TextField();
	private TextField yfield = new TextField();

	public CoordinatesDialog() {
		super(AlertType.CONFIRMATION);
		GridPane grid = new GridPane();
		grid.addRow(0, new Label("x:"), xfield);
		grid.addRow(1, new Label("y:"), yfield);
		grid.setVgap(STANDARD_SPACING);
		getDialogPane().setContent(grid);
		setHeaderText(null);
		setTitle("Input Coordinates");
	}

	public int getXcoordinate() {
		return Integer.parseInt(xfield.getText());
	}

	public int getYcoordinate() {
		return Integer.parseInt(yfield.getText());
	}
}
