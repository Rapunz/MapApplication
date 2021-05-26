

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddDescribedDialog extends AddDialog {

	private TextField descriptionField = new TextField();

	public AddDescribedDialog() {
		setTitle("Add new described place");
		getGrid().addRow(1, new Label("Description:"), descriptionField);
	}

	public String getDescription() {
		return descriptionField.getText();
	}
}
