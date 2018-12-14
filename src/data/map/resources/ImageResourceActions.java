package data.map.resources;

import javax.swing.ImageIcon;

public class ImageResourceActions {
	
//	private boolean byTime;

	private int skip;
	private int totalNumberImages;
	
//	public boolean isByTime() {
//		return byTime;
//	}
//	public void setByTime(boolean byTime) {
//		this.byTime = byTime;
//	}

	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public int getTotalNumberImages() {
		return totalNumberImages;
	}
	public void setTotalNumberImages(int totalNumberImages) {
		this.totalNumberImages = totalNumberImages;
	}
	
	public boolean hasAnotherImage(int timePassed){
		
		return timePassed/skip<totalNumberImages;
		
	}

	public int imagesLeft(int timePassed){

		return totalNumberImages * skip - timePassed;

	}
	
	public int getCurrentFrame(int timePassed){
		
		return getCurrentFrame(timePassed, this.skip);
	}
	
	public int getCurrentFrame(int timePassed, int skip){
		
		return timePassed/skip;
	}
	
	public int getTimePassedFromFrame(int frame){
		
		return skip*frame;
	}

	//if this is too slow, will have to load images into a list on init and then start the action
	//could break this loading down into chunks if needed and reuse the same image list (idle)
	public ImageIcon getImage(int timePassed, String directory, int skip){
		
//		if(Constants.DEBUG) System.out.println("skip"+skip);
//		if(Constants.DEBUG) System.out.println("total"+totalNumberImages);
//		if(Constants.DEBUG) System.out.println("time"+timePassed);

		ImageIcon texture = null;
		
		int requiredImage = getCurrentFrame(timePassed, skip);
		
		//cannot allow requiredImage>totalNumberImages
		if(requiredImage<totalNumberImages){

//            texture = ImageUtils.getTextureFromPath(directory + requiredImage, directory);

//			if(Constants.DEBUG && texture==null)System.out.println("could not find filename "+directory+requiredImage+ " with any allowed image extention");
		}

		return texture;


	}
	
	public ImageIcon getImage(int timePassed, String directory){
		
		return getImage(timePassed, directory, this.skip);
	}


}
