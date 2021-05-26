

public class PlaceParser {

	private static final int CLASS_INDEX = 0;
	private static final int CATEGORY_INDEX = 1;
	private static final int XPOS_INDEX = 2;
	private static final int YPOS_INDEX = 3;
	private static final int NAME_INDEX = 4;
	private static final int DESCRIPTION_INDEX = 5;

	public static Place parse(String string) {

		String[] tokens = string.split(",");
		String classType = tokens[CLASS_INDEX];
		Category category = Category.valueOf(tokens[CATEGORY_INDEX].toUpperCase());
		int x = Integer.parseInt(tokens[XPOS_INDEX]);
		int y = Integer.parseInt(tokens[YPOS_INDEX]);
		String name = tokens[NAME_INDEX];

		switch (classType) {
		case "Described":
			String description = tokens[DESCRIPTION_INDEX];
			return new DescribedPlace(name, new Position(x, y), category, description);
		case "Named":
			return new NamedPlace(name, new Position(x, y), category);
		default:
			throw new IllegalArgumentException("The place type " + classType + " is unrecognized");
		}
	}
}
