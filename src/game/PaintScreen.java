package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import data.Constants;
import data.LevelData;
import data.map.ImageStatus;
import data.map.ImageStatus.STATUS;
import data.map.MapCell;
import data.map.MapUtils;
import data.map.resources.Coords;
import data.map.resources.Resource;

public class PaintScreen extends JPanel {

//    Image blueImage;
//    Image redImage;
//    Image greenImage;
//    Image yellowImage;
//    byte [][] grid;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean halfTick = false;
	
    private float drawGridDisplacementX = 0;
    private float drawGridDisplacementY = 0;

    private int gridWidth;
    private int gridHeight;
    private int overscroll;
    
    private int gridDisplayX; 
    private int gridDisplayY;
    
    private int panelSize;
    
	private int currentItemAreaX = -1;
	private int currentItemAreaY = -1;
	private Coords itemArea;
	private boolean selectedAreaPresent;
	
//	private boolean isAddMode;
	//0 - add
	//1 - remove
	//2 - configure
	private int mode;
	
	private boolean mapItem;
	private int placeItemIndex;
    
    private BufferedImage island;
	
	public void setHalfTick(boolean halfTick){
		
		this.halfTick = halfTick;
	}
    
    public PaintScreen(int gridWidth, int gridHight, int overScroll, int panelSize, int gridDisplayX, int gridDisplayY, String mapImageFilename) {
    	
        this.gridWidth = gridWidth;
        this.gridHeight = gridHight;
        this.overscroll = overScroll;
        this.gridDisplayX = gridDisplayX;
        this.gridDisplayY = gridDisplayY;
        this.panelSize = panelSize;

        try {
        	String islandPath = getMapImagePath(mapImageFilename);
        	System.out.println(islandPath);
			island = ImageIO.read(new File(islandPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
//        ImageIcon red = new ImageIcon(this.getClass().getResource("red.jpg"));
//        redImage = red.getImage();
//        ImageIcon green = new ImageIcon(this.getClass().getResource("green.jpg"));
//        greenImage = green.getImage();
//        ImageIcon yellow = new ImageIcon(this.getClass().getResource("yellow.jpg"));
//        yellowImage = yellow.getImage();

    }
    
//    public void gridToDisplay(byte [][] grid){
//    	
//    	this.grid =grid;
//    	repaint();
//    }
    
    public void paint(Graphics g) {
    	
//    	System.out.println("painting....");
    	
    	MapCell[][] grid = LevelData.getInstance().getMapGrid();

        Graphics2D g2d = (Graphics2D) g;
        if(grid!=null){

        	g2d.drawImage(island, (int)(0-(drawGridDisplacementX * Constants.X_BLOCK)), (int)(0-(drawGridDisplacementY * Constants.Y_BLOCK))+panelSize, 
        			(int)((gridWidth + overscroll * 2) * Constants.X_BLOCK), (int)((gridHeight + overscroll * 2) * Constants.Y_BLOCK), null);
        	
        	boolean drawingFirst = true;
        	boolean drawingOthers = false;
        	short currentDrawLevel = 1;
        	
            int xFrom = (int)(Constants.DISPLAY_WIDTH+drawGridDisplacementX+Constants.OVERDRAW);
            int xTo = (int)(drawGridDisplacementX-Constants.OVERDRAW);
            int yFrom = (int)(drawGridDisplacementY-Constants.OVERDRAW);
            int yTo = (int)(Constants.DISPLAY_HEIGHT+drawGridDisplacementY+Constants.OVERDRAW);

            if(xTo<0) xTo=0;
            if(xFrom>gridWidth)xFrom=gridWidth;
            if(yFrom<0)yFrom=0;
            if(yTo> gridHeight)yTo= gridHeight;
        	
        	while(drawingFirst || drawingOthers){
        	
        		drawingFirst = false;

			    for (int y = yFrom; y < yTo; y=y+1) {
			    	for (int x = xFrom-1; x > xTo-1; x=x-1) {

		        		MapCell cell = grid[x][y];
		        		if(cell!=null){
		        			List<ImageStatus> mapObjsImage = cell.getMapObjectImages();
		        			if(mapObjsImage!=null && !mapObjsImage.isEmpty()){
		        				
		        				for (ImageStatus imageStatus : mapObjsImage) {
		        					
			        				if(drawingOthers){	        				
			        					
			        					if(imageStatus.getDrawLevel()>0) continue;
			        					
			        					drawImage(imageStatus, g2d, x, y);

			        				}else{//draw flat objects
			        					
			        					if(imageStatus.getDrawLevel()!=currentDrawLevel) continue;
			        					
			        					drawingFirst = true;
			        					
			        					drawImage(imageStatus, g2d, x, y);
			        				}
								}
		        			}
		        			
//		        			if(drawingOthers){
//	            				List<WorkerImageStatus> workers = cell.getWorkerImages();
//		        				
//		        				if(workers!=null && !workers.isEmpty()){
//		        					
//			        				for (WorkerImageStatus workerImageStatus : workers) {
//										
//			        					drawWorker(workerImageStatus, g2d, x, y);
//									}
//		        				}
//		        			}
		        		}
		        	}
		        }
		        if(drawingOthers){
		        	
		        	drawingOthers=false;
		        	drawingFirst=false;
		        }else{
		        	if(!drawingFirst){
		        		
		        		drawingOthers=true;		        		
		        	}else{
		        		currentDrawLevel++;
		        	}
		        }
        	}
        	
        	int itemX = getCurrentItemAreaX();
        	int itemY = getCurrentItemAreaY();
        	
        	if(isSelectedAreaPresent()){
        		
        		int spanX = getItemArea().x();
        		int spanY = getItemArea().y();
        		int itemXPos = (int)((itemX+overscroll+1)*Constants.X_BLOCK -spanX*Constants.X_BLOCK);
        		int itemYPos = (int)((itemY+overscroll+1)*Constants.Y_BLOCK -spanY*Constants.Y_BLOCK);

        		Color color;
		
        		if(mode==0){
        			
	        		boolean canAdd;
	        		
	        		itemX = (int)(itemX + drawGridDisplacementX);
	        		itemY = (int)(itemY + drawGridDisplacementY);

	        		if(isMapItem()){
	        			canAdd = MapUtils.canAddMapItemToMap(new Coords(itemX, itemY), Resource.getMapItemResource(getPlaceItemIndex()), getItemArea());
	        		}else{
	        			canAdd = MapUtils.canAddBuildingToMap(new Coords(itemX, itemY), Resource.getBuildingResource(getPlaceItemIndex()), getItemArea());
	        		}

	        		if(canAdd){
	        			color = Color.GREEN;
	        		}else{
	        			color = Color.RED;
	        		}
        		}else if(mode==1){
        			
        			color = Color.RED;
        		
        		}else{
        			
        			color = Color.GREEN;
        		}
        		
	        	BufferedImage image = new BufferedImage((int)(spanX*Constants.X_BLOCK), (int)(spanY*Constants.Y_BLOCK), BufferedImage.TYPE_INT_RGB);
	        	Graphics2D gcolor = image.createGraphics();
	        	gcolor.setPaint(color);
	        	gcolor.fillRect(0, 0, image.getWidth(), image.getHeight());  
	        	gcolor.drawImage(image, 0, 0, null);
	        	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));	
	        	g2d.drawImage(image, itemXPos, itemYPos + panelSize, null);
	        	
        	}

        }
    }
    

    
    private void drawImage(ImageStatus imageStatus, Graphics2D g2d, int x, int y){
   	
    	if(imageStatus.getStatus()!=STATUS.HIDDEN){
    	
	    	Coords imagePos = imageStatus.getImagesPosistion(x-drawGridDisplacementX, y-drawGridDisplacementY);
	    	
//	    	System.out.println("imagex "+imagePos.x+ " imagey "+imagePos.y);
			
			g2d.drawImage(imageStatus.getImage(), imagePos.x, imagePos.y + panelSize, null);
    	}
    }
    

	public void adjustDrawGridDisplacementX(float adjustment) {
		drawGridDisplacementX = drawGridDisplacementX + adjustment;
		if(drawGridDisplacementX < 0)drawGridDisplacementX = 0;
		if(drawGridDisplacementX > (gridWidth  + overscroll * 2) - gridDisplayX) drawGridDisplacementX = (gridWidth + overscroll * 2) - gridDisplayX;

	}

	public void adjustDrawGridDisplacementY(float adjustment) {
		drawGridDisplacementY = drawGridDisplacementY + adjustment;
		if(drawGridDisplacementY < 0)drawGridDisplacementY = 0;
		if(drawGridDisplacementY > (gridHeight + overscroll * 2) - gridDisplayY) drawGridDisplacementY = (gridHeight + overscroll * 2) - gridDisplayY;

	}

	public float getDrawGridDisplacementX() {
		return drawGridDisplacementX;
	}

	public float getDrawGridDisplacementY() {
		return drawGridDisplacementY;
	}

	protected String getMapImagePath(String mapImageFilename){
		
		return ImageHelper.getTempFolderPath().append(mapImageFilename).append(".png").toString();
	}

	public int getCurrentItemAreaX() {
		return currentItemAreaX;
	}

	public void setCurrentItemAreaX(int currentItemAreaX) {
		this.currentItemAreaX = currentItemAreaX;
	}

	public int getCurrentItemAreaY() {
		return currentItemAreaY;
	}

	public void setCurrentItemAreaY(int currentItemAreaY) {
		this.currentItemAreaY = currentItemAreaY;
	}

	public Coords getItemArea() {
		return itemArea;
	}

	public void setItemArea(Coords itemArea) {
		this.itemArea = itemArea;
	}

	public boolean isMapItem() {
		return mapItem;
	}

	public void setMapItem(boolean mapItem) {
		this.mapItem = mapItem;
	}

	public int getPlaceItemIndex() {
		return placeItemIndex;
	}

	public void setPlaceItemIndex(int placeItemIndex) {
		this.placeItemIndex = placeItemIndex;
	}

	public boolean isSelectedAreaPresent() {
		return selectedAreaPresent;
	}

	public void setSelectedAreaPresent(boolean selectedAreaPresent) {
		this.selectedAreaPresent = selectedAreaPresent;
	}

//	public boolean isAddMode() {
//		return isAddMode;
//	}
//
//	public void setAddMode(boolean isAddMode) {
//		this.isAddMode = isAddMode;
//	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	
	
	
	
}

