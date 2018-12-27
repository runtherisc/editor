package game;

import javax.swing.JPanel;

public class PaintScreenOld extends JPanel {

//    Image blueImage;
//    Image redImage;
//    Image greenImage;
//    Image yellowImage;
//    byte [][] grid;
//
//	boolean halfTick = false;
//	
//    private float drawGridDisplacementX = 0;
//    private float drawGridDisplacementY = 0;
//
//    private int gridWidth;
//    private int gridHeight;
//    
//    private BufferedImage island;
//	
//	public void setHalfTick(boolean halfTick){
//		
//		this.halfTick = halfTick;
//	}
//    
//    public PaintScreen(int gridWidth, int gridHight) {
//    	
//        this.gridWidth = gridWidth;
//        this.gridHeight = gridHight;
//
//        try {
//			island = ImageIO.read(new File("assets/island.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
////        ImageIcon red = new ImageIcon(this.getClass().getResource("red.jpg"));
////        redImage = red.getImage();
////        ImageIcon green = new ImageIcon(this.getClass().getResource("green.jpg"));
////        greenImage = green.getImage();
////        ImageIcon yellow = new ImageIcon(this.getClass().getResource("yellow.jpg"));
////        yellowImage = yellow.getImage();
//
//    }
//    
////    public void gridToDisplay(byte [][] grid){
////    	
////    	this.grid =grid;
////    	repaint();
////    }
//    
//    public void paint(Graphics g) {
//    	
//    	MapCell[][] grid = LevelData.getInstance().getMapGrid();
//
//        Graphics2D g2d = (Graphics2D) g;
//        if(grid!=null){
//        	
////        	if(Constants.DEBUG) System.out.println("paint");
//        	
//        	
////        	String tempGrassPath = "Images/grass.gif";
////        	
////        	Image grassIcon = new ImageIcon(tempGrassPath).getImage();
////        	
////	        for (int x = 0; x < grid.length; x=x+1) {
////	        	for (int y = 0; y < grid[x].length; y=y+1) {
////	        		
////	        		g2d.drawImage(grassIcon, x*Constants.BLOCK_SIZE-Constants.BLOCK_SIZE, y*Constants.BLOCK_SIZE-Constants.BLOCK_SIZE, null);
////	        	}
////	        		
////	        }
//        	
//
//        	
////        	createImage(new FilteredImageSource(image.getSource(),
////        	        new CropImageFilter(73, 63, 141, 131)));
//        	
////        	int islandx1 = (int)(Constants.DISPLAY_WIDTH+drawGridDisplacementX+Constants.OVERDRAW)*Constants.BLOCK_SIZE;
////        	int islandy1 = (int)(Constants.DISPLAY_HEIGHT+drawGridDisplacementY+Constants.OVERDRAW)*Constants.BLOCK_SIZE;
////        	int islandy2 = (int)(drawGridDisplacementY-Constants.OVERDRAW)*Constants.BLOCK_SIZE;
////        	int islandx2 = (int)(drawGridDisplacementX-Constants.OVERDRAW)*Constants.BLOCK_SIZE;
////        	
////        	
////        	g2d.drawImage(island.getSubimage(islandx1, islandy1, islandx2, islandy2), 0, 0, null);
//        	g2d.drawImage(island, 0, 0, null);
//        	
//        	boolean drawingFirst = true;
//        	boolean drawingOthers = false;
//        	short currentDrawLevel = 1;
//        	
//            int xFrom = (int)(Constants.DISPLAY_WIDTH+drawGridDisplacementX+Constants.OVERDRAW);
//            int xTo = (int)(drawGridDisplacementX-Constants.OVERDRAW);
//            int yFrom = (int)(drawGridDisplacementY-Constants.OVERDRAW);
//            int yTo = (int)(Constants.DISPLAY_HEIGHT+drawGridDisplacementY+Constants.OVERDRAW);
//
//            if(xTo<0) xTo=0;
//            if(xFrom>gridWidth)xFrom=gridWidth;
//            if(yFrom<0)yFrom=0;
//            if(yTo> gridHeight)yTo= gridHeight;
//        	
//        	while(drawingFirst || drawingOthers){
//        	
//        		drawingFirst = false;
////              for (int y = 0; y < grid[0].length; y++) {
////              for (int x = grid.length-1; x>-1; x--) {
//			    for (int y = yFrom; y < yTo; y=y+1) {
//			    	for (int x = xFrom-1; x > xTo-1; x=x-1) {
//
//		        		MapCell cell = grid[x][y];
//		        		if(cell!=null){
//		        			List<ImageStatus> mapObjsImage = cell.getMapObjectImages();
//		        			if(mapObjsImage!=null && !mapObjsImage.isEmpty()){
//	//	        				if(Constants.DEBUG) System.out.println("mapobjimage was not null or empty");
//		        				
//		        				for (ImageStatus imageStatus : mapObjsImage) {
//		        					
//			        				if(drawingOthers){//draw non flat object and building				        				
//			        					
//			        					if(imageStatus.getDrawLevel()>0) continue;
//			        				
//			        					
////			        					if(Constants.DEBUG) System.out.println("drawing others");
//			        					
//			        					drawImage(imageStatus, g2d, x, y);
//
//			        				}else{//draw flat objects
//			        					
//			        					if(imageStatus.getDrawLevel()!=currentDrawLevel) continue;
//			        					
////			        					if(Constants.DEBUG) System.out.println("draw level "+currentDrawLevel);
//			        					drawingFirst = true;
//			        					
//			        					drawImage(imageStatus, g2d, x, y);
//			        				}
//								}
//		        			}
//		        			
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
//		        		}
//		        	}
//		        }
//		        if(drawingOthers){
//		        	
//		        	drawingOthers=false;
//		        	drawingFirst=false;
//		        }else{
//		        	if(!drawingFirst){
//		        		
//		        		drawingOthers=true;		        		
//		        	}else{
//		        		currentDrawLevel++;
//		        	}
//		        }
//        	}
//
//        }
//    }
//    
//
//    
//    private void drawImage(ImageStatus imageStatus, Graphics2D g2d, int x, int y){
//    	
////    	Image image = imageStatus.getImage();
//
////	        					MediaTracker tracker = new MediaTracker(this);
////	        					tracker.addImage(image, 0);
////	        					try {
////									tracker.waitForID(0);
////								} catch (InterruptedException e) {
////									
////
////								}
////	        					System.out.println("error id" + tracker.statusID(MediaTracker.COMPLETE, true));
//    	
//    	if(imageStatus.getStatus()!=STATUS.HIDDEN){
//    	
//	    	Coords imagePos = imageStatus.getImagesPosistion(x, y);
//			
//			g2d.drawImage(imageStatus.getImage(), imagePos.x, imagePos.y, null);
//    	}
//    }
//    
//    private void drawWorker(WorkerImageStatus workerImageStatus, Graphics2D g2d, int x, int y){
//
//		//		MediaTracker tracker = new MediaTracker(this);
//		//		tracker.addImage(image, 0);
//		//		try {
//		//			tracker.waitForID(0);
//		//		} catch (InterruptedException e) {
//		//			e.printStackTrace();
//		//		}
//
//    	
////    	Coords imagePos = workerImageStatus.getImagesPosistion(x, y, halfTick);
////    	
////    	Image workerImage = workerImageStatus.getImage(halfTick);
////		
////    	if(workerImage!=null){
////    		g2d.drawImage(workerImage, imagePos.x, imagePos.y, null);
////    	}
//	}
    
}

