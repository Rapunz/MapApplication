
import javafx.scene.paint.Color;

public enum Category {
	BUS("Bus", Color.RED), UNDERGROUND("Underground", Color.BLUE), TRAIN("Train", Color.GREEN),
	NONE("None", Color.BLACK);

	private final String name;
	private final Color color;

	Category(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return name;
	}
}
