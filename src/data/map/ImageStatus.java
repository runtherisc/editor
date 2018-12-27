package data.map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import data.Constants;
import data.LevelData;
import data.map.resources.BuildingCreationResource;
import data.map.resources.BuildingResource;
import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.ImageResourceActions;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import game.ImageHelper;

public class ImageStatus {
	
	public ImageStatus(ImageResource imageResource, boolean isMapObject, Coords span, int resourceId){

		this.imageResource = imageResource;
		this.isMapObject = isMapObject;
		this.resourceId = resourceId;
//        initModel(span);
	}
	
	public enum STATUS{
		CREATION,//upgrade?
		IDLE,
		ACTION,
		DESTRUCTION,
		STATIC,
		HIDDEN,
		CREATION_DESTRUCTION,
		MOVING
	}
	
	
	private STATUS status;
	transient private ImageResource imageResource;
	private int timePassed;
	private boolean isMapObject;	
	private int buildingImageIndex;// this is used on creation/action, if >0 then loops though idle creation images (creation starts from sequence1/, action starts from sequence0/)
	private int sequenceOverride = -1;
	private short drawLevel;//for draw first objects(flat ground objects) order it needs to be drawn
	//for restoring saved games
	private int resourceId;
	private byte direction;
	private boolean alternative;
	private int issuesId = -1;

	public void updateImageResource(ImageResource imageResource){

		this.imageResource = imageResource;
	}
	
	public boolean isMapObject() {
		return isMapObject;
	}

	public void setMapObject(boolean isMapObject) {
		this.isMapObject = isMapObject;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public int getBuildingImageIndex() {
		return buildingImageIndex;
	}

	public void setBuildingImageIndex(int index) {
		this.buildingImageIndex = index;
	}

	public short getDrawLevel() {
		return drawLevel;
	}

	public void setDrawLevel(short drawLevel) {
		this.drawLevel = drawLevel;
	}

	
	public void initCreationStatus(int resourceIndex, boolean isHidden, boolean isMapObject){
		
		STATUS status = STATUS.STATIC;
		
		int imageResourceIndex = -1;
		
		ImageResourceActions imageAction = imageResource.getCreation();
		if(imageAction!=null){		
			status = (isHidden ? STATUS.HIDDEN : STATUS.CREATION);

			List<BuildingCreationResource> list;

			if(isMapObject){

				list = Resource.getMapItemResourceById(resourceIndex).getImageResource().getBuildingCreationList();

			}else {

				list = Resource.getBuildingResourceById(resourceIndex).getImageResource().getBuildingCreationList();

			}

			if (list != null && !list.isEmpty()) imageResourceIndex = 0;
			
			
		}else if(imageResource.getMovement()!=null){
			status=STATUS.MOVING;			
		}else if(imageResource.getIdleTotal()!=0){						
			status=STATUS.IDLE;				
		}else{
			System.out.println("failed to set image status");
		}
		

		setBuildingImageIndex(imageResourceIndex);
		setStatus(status);
	}

    public void constructionComplete(){

        buildingImageIndex = 0;
        if(imageResource.getMovement()==null) setStatus(STATUS.IDLE);
        else setStatus(STATUS.MOVING);
    }


	public void restoreStateAfterLoad(){

		if(resourceId > -1){

			if(isMapObject()){

				MapItemResource mapItemR = Resource.getMapItemResourceById(resourceId);
				updateImageResource(mapItemR.getImageResource());
//				if(Constants.DEBUG) Log.d(Constants.GENERAL_TAG, "load is setting imagestatus for "+mapItemR.getName());
			}else {

				BuildingResource buildingR = Resource.getBuildingResourceById(resourceId);
				updateImageResource(buildingR.getImageResource());
//				if(Constants.DEBUG) Log.d(Constants.GENERAL_TAG, "load is setting imagestatus for "+buildingR.getName());
			}

		}
	}
	
	public MapItemResource getMapItemResource(){
		
		if(isMapObject()){
			return Resource.getMapItemResourceById(resourceId);
		}
		
		return null;
	}
	
	public Coords getImagesPosistion(float x, float y){
		
		Coords span = imageResource.getSpan();
		
		int overScroll = LevelData.getInstance().getOverScroll();

		return new Coords( (x+overScroll+1)*Constants.X_BLOCK -span.x*Constants.X_BLOCK,
                           (y+overScroll+1)*Constants.Y_BLOCK-span.y*Constants.Y_BLOCK);
	

	}

	public Image getImage(){

		try {
			Coords span = imageResource.getSpan();
			String filePath = ImageHelper.getStaticFilePath(imageResource.getDirectory());
//			System.out.println(filePath);
			return resizeImage(ImageIO.read(new File(filePath)), span.x, span.y);
		} catch (IOException e) {
			System.out.println("could not find "+ImageHelper.getStaticFilePath(imageResource.getDirectory()));
			e.printStackTrace();
		}
		
		return null;

	}
	
	private Image resizeImage(Image image, int spanx, int spany){
		
//        System.out.println("resizing...");
        int scaledx = (int)(spanx*Constants.X_BLOCK);
        int scaledy = (int)(spany*Constants.Y_BLOCK);
        BufferedImage scaledBI = new BufferedImage(scaledx, scaledy, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledBI.createGraphics();
        g.drawImage(image, 0, 0, scaledx, scaledy, null); 
        g.dispose();
        image = null;
        return scaledBI;
    }
	
	public int getIssuesId() {
		return issuesId;
	}

	public void setIssuesId(int issuesId) {
		this.issuesId = issuesId;
	}

}
