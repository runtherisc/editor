package data.map.resources;

public class Coords {
	
	
	public Coords(int x, int y){
		
		this.x = x;
		this.y = y;
	}

    public Coords(float x, float y) {

        this.x = (int) x;
        this.y = (int) y;
    }
	
	public int x = -1;
	public int y = -1;



    public int x() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int y() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isValid(){
		
		return !(x==-1 && y==-1);
	}
	
	@Override
	public boolean equals(Object other){

		return other!=null && 
			   other instanceof Coords && 
			   ((Coords)other).x() == x && 
			   ((Coords)other).y() == y;
	}

	@Override
	public String toString(){
		
		return x +"|" +y;
	}
}
