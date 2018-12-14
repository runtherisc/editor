package data.map.resources;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import gui.EditorGeneral;


public class ImageResource {
	
	/*
	 * creation is always by time
	 * idle is always by loop
	 * working is always by time
	 * destruction is always by time
	 */
	
	private int id;
	private Coords span;
	private ExitPoint[] hotspots;
	private boolean[][] walkover;
	private boolean canTouch; //TODO remove this, solve with can walk overs
	private List<Image> idleImages = new ArrayList<Image>();
	private ImageResourceActions creation;
	private List<BuildingCreationResource> buildingCreationList = new ArrayList<BuildingCreationResource>();
	private List<BuildingLifecycleResource> buildingUpgradeList = new ArrayList<BuildingLifecycleResource>();
    private BuildingLifecycleResource destructionResource;
	private List<MultiImageResourceAction> busy = new ArrayList<MultiImageResourceAction>();
	private List<MultiImageResourceAction> idles = new ArrayList<MultiImageResourceAction>();
	private ImageResourceActions destruction;
	private ImageResourceActions upgrade;
	private MovementImageResource movement;
	private String directory;
//	private int idleSkip;
//    private int idleTotal;
	private int idleId = -1;
    
    //for editor only
    private boolean isMapObject;

	public boolean isMapObject() {
		return isMapObject;
	}
	public void setMapObject(boolean isMapObject) {
		this.isMapObject = isMapObject;
	}
	public int getId(){return id;}
	public void setId(int id){this.id = id;}
	
	

    public int getIdleId() {
		return idleId;
	}
	public void setIdleId(int idleId) {
		this.idleId = idleId;
	}
	
	//global back up?
	public int getIdleTotal() {

        return getIdleById(idleId).getTotalNumberImages();
    }
//    public void setIdleTotal(int idleTotal) {
//        this.idleTotal = idleTotal;
//    }
	public int getIdleSkip() {
		
		int idleTotal = getIdleTotal();
		
		int idleSkip = 0;
		
		if (idleTotal > 0) idleSkip = 8 / idleTotal;
		
		return idleSkip;
	}
//	public void setIdleSkip(int idleSkip) {
//		this.idleSkip = idleSkip;
//	}
	public ImageResourceActions getCreation() {
//		if (creation==null)if(Constants.DEBUG) System.out.println("c null");
		return creation;
	}
	public void setCreation(ImageResourceActions creation) {
//		if(Constants.DEBUG) System.out.println("setting creation");
		this.creation = creation;
	}
	public List<MultiImageResourceAction> getBusy() {
		return busy;
	}
	public void setBusy(List<MultiImageResourceAction> actions) {
		this.busy = actions;
	}
	public void addBusy(MultiImageResourceAction action){
		
		busy.add(action);
	}
	
	public void setBusy(int index, MultiImageResourceAction action){
		
		busy.set(index, action);
	}
	
	public MultiImageResourceAction getBusyByIndex(int pos){

		MultiImageResourceAction busyImageResource = null;

		if(pos>-1 && pos<busy.size()){

			busyImageResource = busy.get(pos);
		}

		return busyImageResource;
	}
	
	public List<MultiImageResourceAction> getIdles() {
		return idles;
	}
	public void setIdles(List<MultiImageResourceAction> idles) {
		this.idles = idles;
	}
	public void addIdle(MultiImageResourceAction idle){
		
		idles.add(idle);
	}
	
	public void setIdle(int index, MultiImageResourceAction idle){
		
		idles.set(index, idle);
	}
	
	public boolean isIdlesEmpty(){
		
		return idles==null || idles.isEmpty();
	}
	
	public String[] getIdleNames(boolean addEmpty){
		
		List<String> names = new ArrayList<String>();
		
		if(addEmpty) names.add("<none selected>");
		
		if(idles!=null && !idles.isEmpty()){

			for (MultiImageResourceAction idle : idles) {
				
				names.add(idle.getInternalName());
			}
		}
		
		return names.toArray(new String[]{});
	}
	
	public String getIdleNameFromId(int id){
		
		if(id==-1) return "";
		
		if(idles!=null && !idles.isEmpty()){
			
			for (MultiImageResourceAction idle : idles) {
				
				if(idle.getId()==id) return idle.getInternalName();
			}
		}
		
		return "name not found";
	}
	
	public int getIdleIndexFromId(int id, boolean hasEmpty){
		
		if(id==-1) return 0;
		
		if(idles!=null && !idles.isEmpty()){
			
			int i = 0;
			
			for (MultiImageResourceAction idle : idles) {
				
				if(idle.getId()==id) return hasEmpty ? i + 1 : i;
						
				i++;
			}
		}
		
		System.out.println("id "+id+" not found, returning -1");
		
		return -1;
		
	}
	
	public MultiImageResourceAction getIdleByIndex(int pos){

		MultiImageResourceAction idleImageResource = null;

		if(pos>-1 && pos<idles.size()){

			idleImageResource = idles.get(pos);
		}

		return idleImageResource;
	}
	
	public MultiImageResourceAction getIdleById(int requiredId){

		for(MultiImageResourceAction idle : idles){

			if(idle.getId() == requiredId){

				return idle;
			}
		}

		System.err.println("ERROR: image dir "+this.directory+" with id "+this.id+" doesn't have a idle action with id of " +requiredId);
		return null;
	}
	
//	public static int getNextBusyResourceId(ImageResource imageResource){
//		
//		return Resource.findLastId(getAllBusyResourceIds(imageResource));		
//
//	}
//	
//	public static int getNextBusyResourceDirectory(ImageResource imageResource){
//		
//		return Resource.findLastId(getAllBusyResourceDirs(imageResource));		
//
//	}
//	
//	public static List<Integer> getAllBusyResourceDirs(ImageResource imageResource){
//		
//		List<Integer> dirs = new ArrayList<>();
//		
//		for (BusyImageResourceAction resource : imageResource.getBusy()) {
//			
//			dirs.add(resource.getDirectory());
//		}
//		
//		return dirs;
//	}
//	
//	public static List<Integer> getAllBusyResourceIds(ImageResource imageResource){
//		
//		List<Integer> ids = new ArrayList<>();
//		
//		for (BusyImageResourceAction resource : imageResource.getBusy()) {
//			
//			ids.add(resource.getId());
//		}
//		
//		return ids;
//	}

	public MultiImageResourceAction getBusyById(int requiredId){

		for(MultiImageResourceAction biz : busy){

			if(biz.getId() == requiredId){

				return biz;
			}
		}

		System.err.println("ERROR: image dir "+this.directory+" with id "+this.id+" doesn't have a busy action with id of " +requiredId);
		return null;
	}
	
	public MovementImageResource getMovement() {
		return movement;
	}

	public void setMovement(MovementImageResource movement) {
		this.movement = movement;
	}

	public void addBuildingCreation(BuildingCreationResource buildingCreation){
		
		buildingCreationList.add(buildingCreation);
	}
	
	public void setBuildingCreation(List<BuildingCreationResource> buildingCreationList){
		
		this.buildingCreationList = buildingCreationList;
	}
	
	public List<BuildingCreationResource> getBuildingCreationList(){
		
		return buildingCreationList;
	}

	public void addBuildingUpgrade(BuildingLifecycleResource buildingUpgrade){
		
		buildingUpgradeList.add(buildingUpgrade);
	}

    public BuildingLifecycleResource getDestructionResource() {
        return destructionResource;
    }

    public void setDestructionResource(BuildingLifecycleResource destructionResource) {
        this.destructionResource = destructionResource;
    }

    public List<BuildingLifecycleResource> getBuildingUpgradeList(){

		return buildingUpgradeList;
	}
	public ImageResourceActions getDestruction() {
		return destruction;
	}
	public void setDestruction(ImageResourceActions destruction) {
		this.destruction = destruction;
	}
	public ImageResourceActions getUpgrade() {
		return upgrade;
	}
	public void setUpgrade(ImageResourceActions upgrade) {
		this.upgrade = upgrade;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		System.out.println("setting dir "+directory);
		this.directory = directory;
	}
	
	public void setDirectoryByName(String name){
		
		this.directory = "Images/" + name + "/";
	}

	public List<Image> getIdleImages() {
		return idleImages;
	}
	public Image getIdleImage(int timePassed){
		
//		if(Constants.DEBUG) System.out.println("idleSkip"+idleSkip);
//		if(Constants.DEBUG) System.out.println("total"+idleImages.size());
//		if(Constants.DEBUG) System.out.println("time"+timePassed);
		
		int idleSkip = getIdleSkip();
		
		if(idleSkip==0) return null;
		int imageIndex = timePassed/idleSkip;
		return idleImages.get(imageIndex);
	}
	public void setIdleImages(List<Image> idleImages) {
		this.idleImages = idleImages;
	}
	public int getIdleImageSize(){
		return this.idleImages.size();
	}
	public void addIdleImage(Image image){
		
//		if(Constants.DEBUG) System.out.println(new File(filename).exists());
//        ImageIcon imageicon = new ImageIcon(filename);
		if(image!=null){
			idleImages.add(image);
		}
	}
	public boolean isCanTouch() {
		return canTouch;
	}
	public void setCanTouch(boolean canTouch) {
		this.canTouch = canTouch;
	}

	public Coords getSpan() {
		return span;
	}
	public void setSpan(Coords span) {
		this.span = span;
	}
	
	public Coords[] getHotspots() {
		return hotspots;
	}
	public void setHotspots(ExitPoint[] hotspots) {
		
		if(hotspots.length!=4) System.err.println("ERROR: There must be 4 hotspots");
//		if(Constants.DEBUG) System.out.println("xxx");
//		
//		for (int i = 0; i < hotspots.length; i++) {
//			if(hotspots[i]==null) if(Constants.DEBUG) System.out.println(i+": null");
//			else if(Constants.DEBUG) System.out.println(i + ":" + hotspots[i].x +"|"+hotspots[i].y);
//		}
//		
//		if(Constants.DEBUG) System.out.println("xxx");
		
		this.hotspots = hotspots;
	}
	
	public void setFirstHotSpot(Coords hotspot){
		
		ExitPoint firstExit;
		
		if(hotspot==null) firstExit = new ExitPoint(-1, -1, 0);
		else firstExit = new ExitPoint(hotspot.x, hotspot.y, 0);
		
		setHotspots(new ExitPoint[]{
				firstExit,
				new ExitPoint(-1, -1, 1),
				new ExitPoint(-1, -1, 2),
				new ExitPoint(-1, -1, 3),
		});
	}
	
	public void nullHotspots(){
		
		hotspots = null;
	}
	
//	public boolean isFirstHotspotSet(){
//		
//		return getFirstHotspot().x > -1;
//	}
	
	public ExitPoint getFirstHotspot(){
		
		if(hotspots==null) return new ExitPoint(-1, -1);
		
		return getValidHotspots(hotspots)[0];
	}
	
	public ExitPoint[] getOrderedHotspots(int distX, int distY){

		
		ExitPoint[] orderedHotspots = hotspots;
		
		boolean left = false;
		boolean up = false;
		
		if(distX<0) left = true; 
		if(distY<0) up = true; 
		
		//TODO improve this
//		if(!left && up){
//		
//		orderedHotspots = new Point[]{
//				hotspots[0],
//				hotspots[1],
//				hotspots[2],
//				hotspots[3]
//		};
//		
//	}else
		if(left && up){
			
			orderedHotspots = new ExitPoint[]{
					hotspots[3],
					hotspots[0],
					hotspots[1],
					hotspots[2]
			};
			
		}else if(left && !up){
			
			orderedHotspots = new ExitPoint[]{
					hotspots[2],
					hotspots[3],
					hotspots[0],
					hotspots[1]
			};
			
		}else if(!left && !up){
			
			orderedHotspots = new ExitPoint[]{
					hotspots[1],
					hotspots[2],
					hotspots[3],
					hotspots[0]
			};
			
		};
		
		return getValidHotspots(orderedHotspots);
	}
	
	private ExitPoint[] getValidHotspots(ExitPoint[] hotspots){
		
		List<ExitPoint> finalHotspots = new ArrayList<ExitPoint>();

		for (int i = 0; i < hotspots.length; i++) {
			
			if(hotspots[i].isValid()) finalHotspots.add(hotspots[i]);
		}
		
		if(finalHotspots.isEmpty()) System.err.println("ERROR: There were no vaild hotspots, item dir:" + directory);
		
		return finalHotspots.toArray(new ExitPoint[]{});
	
	}


	public boolean[][] getWalkover() {

		return walkover;
	}
	public void setWalkover(boolean[][] walkover) {
		
		this.walkover = walkover;
	}
	public void fillWalkoverGrid(boolean value){
		
		for (int i = 0; i < walkover.length; i++) {
			for (int j = 0; j < walkover[0].length; j++) {
				walkover[i][j] = value;
			}
		}
	}

	public List<LifecycleItemResource> getDestructionItems(boolean duringCreation, int index){

		System.out.println("duringCreation "+duringCreation+" index"+index);

		if(destructionResource==null) return null;

		if(!duringCreation) return destructionResource.getLifecycleItems();

		List<LifecycleItemResource> destructionItems = new ArrayList<LifecycleItemResource>();

		if(index > -1) {

			List<LifecycleItemResource> items = buildingCreationList.get(index).getLifecycleItems();

			for(LifecycleItemResource item : items){

				if((item instanceof CreationItemResource && !((CreationItemResource)item).isFromWarehouse()) ||
						item instanceof DestructionItemResource){

					destructionItems.add(item);
					System.out.println("adding" + item.toString());
				}
			}
		}

		return destructionItems;
	}
	
	public String getNameFromDir(){
		
		String name = getDirectory();
		
		if(name.endsWith("/")) name = name.substring(0, name.length()-1);

		int lastSlash = name.lastIndexOf("/");
		
		return name.substring(lastSlash+1);
	}
	
	//assumes name is filename safe before called
	public void setDirPathFromName(String name){
		
		String dir = EditorGeneral.getWorkFolderPath();
		
		if(!dir.endsWith("/")) dir = dir + "/";
		
		setDirectory(new StringBuilder(dir).append(name).append("/").toString());
		
	}

}
