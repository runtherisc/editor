package game.path;

import data.map.resources.ExitPoint;


public class BestPath implements Runnable{
	
	public enum PATH_FINDER{
		
		CUSTOM,
		CUSTOM_BOTH,
		ASTAR
	};
	
	ExitPoint[] sourceExits;
	ExitPoint[] destExits;
	int sourceX; 
	int sourceY; 
	int destX; 
	int destY; 
	PATH_FINDER pathFinder;
	
	String key;

    boolean forceFail;

	@Override
	public void run() {

	}

}

