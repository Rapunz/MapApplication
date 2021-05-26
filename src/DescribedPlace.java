

public class DescribedPlace extends Place {

	private static final String CLASS_TYPE = "Described";
	private String description;

	public DescribedPlace(String name, Position position, Category category, String description) {
		super(name, position, category);

		if (description == null || description.isEmpty()) {
			throw new IllegalArgumentException("Description is missing");
		}
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	protected String getClassType() {
		return CLASS_TYPE;
	}

	@Override
	public String getSaveFormat() {
		return super.getSaveFormat() + "," + getDescription();
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "Description: " + getDescription();
	}
}