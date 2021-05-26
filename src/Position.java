

public class Position {

	private int xpos;
	private int ypos;

	public Position(int xpos, int ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
	}

	public int getX() {
		return xpos;
	}

	public int getY() {
		return ypos;
	}

	@Override
	public int hashCode() {
		return xpos * 100000 + ypos;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof Position) {
			Position p = (Position) other;
			return xpos == p.xpos && ypos == p.ypos;
		} else
			return false;
	}

	@Override
	public String toString() {
		return "[" + xpos + "," + ypos + "]";
	}
}
