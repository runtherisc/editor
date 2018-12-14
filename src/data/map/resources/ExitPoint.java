package data.map.resources;

public class ExitPoint extends Coords{
	
	public ExitPoint(int x, int y) {
		
		super(x, y);

	}

	public ExitPoint(int x, int y, int direction) {
		
		super(x, y);
		this.direction = direction;
	}
	
	
	private int direction = -1;

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	
}
