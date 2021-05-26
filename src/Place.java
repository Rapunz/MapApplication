

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

public abstract class Place extends Polygon {

	private static final int TRIANGLE_HEIGHT = 30;
	private static final int TRIANGLE_HALF_WIDTH = 15;
	private static final int MARKED_STROKE_WIDTH = 5;

	private String name;
	private Position position;
	private Category category;
	private boolean marked;

	public Place(String name, Position position, Category category) {
		super(position.getX(), position.getY(), position.getX() - TRIANGLE_HALF_WIDTH,
				position.getY() - TRIANGLE_HEIGHT, position.getX() + TRIANGLE_HALF_WIDTH,
				position.getY() - TRIANGLE_HEIGHT);

		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name is missing");
		}

		this.name = name;
		this.position = position;

		if (category == null) {
			category = Category.NONE;
		}
		this.category = category;

		style();
	}

	private void style() {
		setFill(category.getColor());
		setStroke(Color.YELLOW);
		setStrokeWidth(0);
		setStrokeType(StrokeType.INSIDE);
	}

	public Category getCategory() {
		return category;
	}

	protected abstract String getClassType();

	public String getName() {
		return name;
	}

	public Position getPosition() {
		return position;
	}

	public void setMarked(boolean marked) {
		int strokeWidth = marked ? MARKED_STROKE_WIDTH : 0;
		setStrokeWidth(strokeWidth);
		this.marked = marked;
	}

	public boolean isMarked() {
		return marked;
	}

	public String getSaveFormat() {
		return getClassType() + "," + getCategory() + "," + position.getX() + "," + position.getY() + "," + name;
	}

	@Override
	public String toString() {
		return "Name: " + getName() + " " + getPosition();
	}

}
