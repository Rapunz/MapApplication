

public class NamedPlace extends Place {
	private static final String CLASS_TYPE = "Named";

	public NamedPlace(String name, Position position, Category category) {
		super(name, position, category);
	}

	@Override
	protected String getClassType() {
		return CLASS_TYPE;
	}
}
