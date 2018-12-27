package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import data.Constants;
import data.LevelData;
import data.LevelDataIO;
import data.map.MapCell;
import data.map.MapObjectItem;
import data.map.MapUtils;
import data.map.resources.BuildingResource;
import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import game.items.AbstractBuilding;
import game.items.Warehouse;
import gui.BaseGui;
import gui.HelpMenu;
import gui.PropertyKeys;
import gui.level.IDirtyMap;
import gui.level.LevelMapItemConfigureGui;
import gui.level.LevelWarehouseConfigureGui;

public class MapEditor extends BaseGui implements MouseMotionListener, MouseListener{


	private PaintScreen paintScreen;
	private int gridX;
	private int gridY;
    private int panelHeight = 56;
    private int gridDisplayX = 50;
    private int gridDisplayY = 25;
    
    private int mapHeight = (int)(gridDisplayY * Constants.Y_BLOCK + panelHeight);
    private int width = (int)(gridDisplayX * Constants.X_BLOCK);
    
    private int mouseAdjX = 8;
    private int mouseAdjY = 24;
    
    private JPanel toppanel;
    private JButton saveButton, closeButton, helpButton;
//	private List<MapItemResource> mapItems;
//	private List<BuildingResource> buildings;
	
//	private String addModeText = "Mode: Add To Map";
//	private String removeModeText = "Mode: Remove From Map";
    private String[] modeEntries = new String[]{"Add To Map", "Remove From Map", "Configure"};
	private JComboBox<String> mapsCombo, modeCombo;
	
	private int mouseDragStartX = -1;
	private int mouseDragStartY = -1;
	private boolean mouseDragged = false;
	
	private JFrame frame;
	private int level;
	private String mapImageFilename;
	
	private IDirtyMap iDirtyMap; //for refresh and updating etc
	
    
	public MapEditor(String mapImageFilename, int level, String title, JFrame parent, int gridX, int gridY, IDirtyMap iDirtyMap){
		
		super(title, parent);
		
		
		
		this.level = level;
		this.mapImageFilename = mapImageFilename;
		this.iDirtyMap = iDirtyMap;
		this.gridX = gridX;
		this.gridY = gridY;
		
		frame = new JFrame();
		
		setupCloseListener(frame);
		
    	frame.setTitle(title);
		
		setFrame(frame);
		
		addComponents(frame);
		
		parent.setEnabled(false);

		//on map creation, grid x and grid y, overscroll? (needs to be stored in json)
		//save json when set
		
		//select item, add/remove button
		//configure warehouse
		//save
		//revert (reload json)
		//close
	}
	
	@Override
	protected int addComponents(JFrame frame) {
		
		if(LevelDataIO.doesTempLevelExist(level)){
			
			LevelDataIO.loadData(level);
		}else{	
			LevelData.getInstance(true).init(gridX, gridY, Constants.OVER_SCROLL, level);
		}
		

//		frame.setLayout(null);
    	
		JLayeredPane pane = new JLayeredPane();
    	
    	toppanel = new JPanel(new GridBagLayout());
    	
    	JPanel firstRow = new JPanel();
    	
    	mapsCombo = new JComboBox<String>(getMapItems());
    	
    	mapsCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				int selected = mapsCombo.getSelectedIndex();
				
				initSelectedArea(selected);
			}
    		
    	});
    	
    	firstRow.add(mapsCombo);
    	
    	firstRow.add(new JLabel("Mode:"));
    	
    	modeCombo = new JComboBox<String>(modeEntries);
    	
    	modeCombo.addItemListener(new ItemListener() {

    			@Override
    			public void itemStateChanged(ItemEvent e) {
    				
    				int mode = modeCombo.getSelectedIndex();
    				paintScreen.setMode(mode);
    				
    				if(mode==0){
    					initSelectedArea(mapsCombo.getSelectedIndex());
    					mapsCombo.setEnabled(true);
    				}else{
    					mapsCombo.setEnabled(false);
    				}

    			}
        		
        	});
    	
    	firstRow.add(modeCombo);
    	
    	saveButton = new JButton("Save");
    	saveButton.addActionListener(this);
    	
    	firstRow.add(saveButton);
    	
    	helpButton = new JButton("Help");
    	helpButton.addActionListener(this);
        helpButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    	
    	firstRow.add(helpButton);
    	
    	closeButton = new JButton("Close");
    	closeButton.addActionListener(this);
        closeButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    	
    	firstRow.add(closeButton);
    	
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	
    	toppanel.add(firstRow, gbc);
	
//    	JPanel secondRow = new JPanel();
//
//    	secondRow.add(createMovementButton("up", false, -scrollStep));
//    	secondRow.add(createMovementButton("down", false, scrollStep));
//    	secondRow.add(createMovementButton("left", true, -scrollStep));
//    	secondRow.add(createMovementButton("right", true, scrollStep));
//    	
//    	gbc = new GridBagConstraints();
//    	gbc.gridx = 0;
//    	gbc.gridy = 1;
//    	
//    	toppanel.add(secondRow, gbc);
//    	
//    	JPanel thirdRow = new JPanel();
//    	
//
//    	
//    	gbc = new GridBagConstraints();
//    	gbc.gridx = 0;
//    	gbc.gridy = 1;
//    	
//    	toppanel.add(thirdRow, gbc);
    	
    	toppanel.setMinimumSize(new Dimension(width, panelHeight));
    	toppanel.setSize(new Dimension(width, panelHeight));
    	
    	pane.add(toppanel, 2, 0);
    	
    	LevelData levelData = LevelData.getInstance();

		paintScreen = new PaintScreen(levelData.getGridX(), levelData.getGridY(), levelData.getOverScroll(), panelHeight, gridDisplayX, gridDisplayY, mapImageFilename);
    	
    	paintScreen.setMaximumSize(new Dimension(width, mapHeight));
    	paintScreen.setSize(new Dimension(width, mapHeight));
        
    	pane.add(paintScreen, 1, 0);
    	
    	frame.add(pane, BorderLayout.CENTER);

    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    	frame.setSize(width, mapHeight + 20);

    	frame.setLocationRelativeTo(null);

    	frame.setVisible(true);
        
    	frame.addMouseListener(this);
    	frame.addMouseMotionListener(this);
    	
    	//init
    	paintScreen.setMode(0);
    	initSelectedArea(0);
    	
    	return 0;
		
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame){
		
		super.otherActions(button, frame);
		
		if(button == saveButton){
			
			if(LevelData.getInstance().getWarehouses()!=null && LevelData.getInstance().getWarehouses().size() > 0){
				
				System.out.println("LevelData.getInstance().getWarehouses().size() "+LevelData.getInstance().getWarehouses().size());
			
				LevelDataIO.saveGame(level);
				
				setPendingWriteXml(true);
				setDirtyChildren(false);
				clearAllChildrenPendingWriteXml(buttonToGuiMap.values());
				dirtyButtonUpdate();
				setPendingImageSaveAndConfigure(false);
			}else{
				
				JOptionPane.showMessageDialog(null, "At least one warehouse must be added before saving", "Unable to Save", JOptionPane.WARNING_MESSAGE);
			}
			
		}else if(button == closeButton){
			
			if((anythingDirty() && confirmDirtyClose()) || (!anythingDirty() && additionalCloseConfirmation())){
				
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				closeDown();
				
			}
		}else if(button == helpButton){
			
			new HelpMenu(getTitle(), getHelpText());
		}
		
	}
	
	private void initSelectedArea(int selectedItem){
		
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		
		ImageResource imageResource;
		
		if(selectedItem >= mapItems.size()){
			paintScreen.setMapItem(false);			
			imageResource = Resource.getBuildingResource(selectedItem-mapItems.size()).getImageResource();
			paintScreen.setPlaceItemIndex(selectedItem-mapItems.size());
		}else{
			paintScreen.setMapItem(true);			
			imageResource = Resource.getMapItemResource(selectedItem).getImageResource();
			paintScreen.setPlaceItemIndex(selectedItem);
		}
		
		paintScreen.setItemArea(imageResource.getSpan());
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	private String[] getMapItems(){
		
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		List<BuildingResource> buildings = Resource.getBuildingResourceList();
		
		int totalMapItems = mapItems.size();
		
		String[] mapItemNames = new String[totalMapItems+buildings.size()];

		for (int i = 0; i < mapItemNames.length; i++) {
			if(i >= totalMapItems){
				BuildingResource building = buildings.get(i-totalMapItems);
				if(building.isWarehouse()) mapItemNames[i] = "[W] " + building.getName();
				else mapItemNames[i] = "[B] " + building.getName();
			}else{
				mapItemNames[i] = "[M] " + mapItems.get(i).getName();
			}
		}
		
		return mapItemNames;
	}
	
	private void setMapsCombo(int id, boolean isMapItem){
		
		int pos = 0;
		
		if(!isMapItem){		
			
			for (BuildingResource resource : Resource.getBuildingResourceList()) {
				
				if(resource.getId() == id) break;
				pos++;
			}
			pos = pos + Resource.getMapItemResourceList().size();
		
		}else{
			
			for (MapItemResource resource : Resource.getMapItemResourceList()) {
				
				if(resource.getId() == id) break;
				pos++;
			}
		}
		

		mapsCombo.setSelectedIndex(pos);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		if(!mouseDragged){
		
			int gridX = (e.getX()-mouseAdjX)/16;
			int gridY = (e.getY()-mouseAdjY-panelHeight)/16;
		     
	//		System.out.println("place");
	//		System.out.println("x:"+e.getX()+" y:"+e.getY());
	//		System.out.println("x:"+gridX+" y:"+gridY);
			int overScroll = LevelData.getInstance().getOverScroll();
			
			gridX = (int)(gridX + paintScreen.getDrawGridDisplacementX() - overScroll);
			gridY = (int)(gridY + paintScreen.getDrawGridDisplacementY() - overScroll);
			
			if(paintScreen.getMode()==0){

				if(paintScreen.isMapItem()){
					
					MapUtils.addMapItemToMap(Resource.getMapItemResource(paintScreen.getPlaceItemIndex()).getId(), new Coords(gridX, gridY), true);				
				}else{
					
					MapUtils.addBuildingToMap(Resource.getBuildingResourceIdFromIndex(paintScreen.getPlaceItemIndex()), new Coords(gridX, gridY), true);
				}
				setDirtyStateAndConfigure(true);
			}else{
				

				if(gridX > -1 && gridY > -1 && gridX < LevelData.getInstance().getGridX() && gridY < LevelData.getInstance().getGridY()){
				
					MapCell[][] mapGrid = LevelData.getInstance().getMapGrid();
					
					MapCell cell = mapGrid[gridX][gridY];
					
					AbstractBuilding building = null;
					MapObjectItem lastItem = null;
					
					if(cell!=null){
						building = cell.getBuilding();
						lastItem = cell.getLastMapObject();
					}
					
					if(paintScreen.getMode()==1){
						if(building!=null){
							MapUtils.removeBuildingFromMap(building);
							mapGrid[building.getPosX()][building.getPosY()].removeBuildingImageStatus();
						}else if(lastItem!=null){
							MapUtils.removeObjectFromMap(lastItem);
							mapGrid[lastItem.getCoords().x()][lastItem.getCoords().y()].removeLastImageStatus();
						}
						setDirtyStateAndConfigure(true);
					}else{
						if(building!=null && building instanceof Warehouse){
							
							System.out.println("configure warehouse");
							addToPassedProperties(PropertyKeys.LEVEL_MAP_WAREHOUSE, building);
							LevelWarehouseConfigureGui configWarehouse = new LevelWarehouseConfigureGui("Warehouse Configure", frame);
							openChildGui(configWarehouse);
							
						}else if(lastItem!=null){
							
							System.out.println("configue last map item");
							addToPassedProperties(PropertyKeys.LEVEL_MAP_MAPITEM, lastItem);
							LevelMapItemConfigureGui configMapItem = new LevelMapItemConfigureGui("Map Item Configure", frame);
							openChildGui(configMapItem);
						}
					}
					paintScreen.setSelectedAreaPresent(false);
				}
			}
			
			paintScreen.repaint();
		}
		
		mouseDragged = false;
		mouseDragStartX = -1;
		mouseDragStartY = -1;

	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		paintScreen.setSelectedAreaPresent(false);
		paintScreen.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		int scrollspeeddecrease = 1;
		
		if(mouseDragStartX > -1 && mouseDragStartY > -1){

			mouseDragged = true;
			int xStep = mouseDragStartX - e.getX();
			int yStep = mouseDragStartY - e.getY();
			if(xStep!=0) paintScreen.adjustDrawGridDisplacementX(xStep/scrollspeeddecrease);
			if(yStep!=0) paintScreen.adjustDrawGridDisplacementY(yStep/scrollspeeddecrease);
			paintScreen.setSelectedAreaPresent(false);
			
			paintScreen.repaint();
		}
		
		mouseDragStartX = e.getX();
		mouseDragStartY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		int gridX = (e.getX()-mouseAdjX)/16;
		int gridY = (e.getY()-mouseAdjY-panelHeight)/16;
		
		int overScroll = LevelData.getInstance().getOverScroll();
		
		gridX = (int)(gridX - overScroll);
		gridY = (int)(gridY - overScroll);
	     
//		System.out.println("x:"+e.getX()+" y:"+e.getY());
//		System.out.println("x:"+gridX+" y:"+gridY);
		
		if(paintScreen.getMode()==0){
		
			if(gridY > -4){
				if(gridX != paintScreen.getCurrentItemAreaX() || gridY != paintScreen.getCurrentItemAreaY()){
					
					paintScreen.setSelectedAreaPresent(true);
					paintScreen.setCurrentItemAreaX(gridX);
					paintScreen.setCurrentItemAreaY(gridY);				
					paintScreen.repaint();
					
				}
			}else if(paintScreen.isSelectedAreaPresent()){
				//mouse on menu
				paintScreen.setSelectedAreaPresent(false);
				paintScreen.repaint();
			}
		}else{
			
			int x = (int)(gridX + paintScreen.getDrawGridDisplacementX());
			int y = (int)(gridY + paintScreen.getDrawGridDisplacementY());

			if(x > -1 && y > -1 && x < LevelData.getInstance().getGridX() && y < LevelData.getInstance().getGridY()){
			
				MapCell[][] mapGrid = LevelData.getInstance().getMapGrid();
				
				MapCell cell = mapGrid[x][y];
				
				AbstractBuilding building = null;
				MapObjectItem lastItem = null;
				
				if(cell!=null){
					building = cell.getBuilding();
					lastItem = cell.getLastMapObject();
				}
				
				ImageResource imageResource = null;
				int itemX = -1;
				int itemY = -1;
				
				if(building!=null && (paintScreen.getMode()==1 ||
						(paintScreen.getMode()==2 && building instanceof Warehouse))){
					
					itemX = building.getPosX();
					itemY = building.getPosY();
					if(itemX != paintScreen.getCurrentItemAreaX() || itemY != paintScreen.getCurrentItemAreaY()){
						
						paintScreen.setMapItem(false);
						imageResource = Resource.getBuildingResourceById(building.getResourceId()).getImageResource();
						setMapsCombo(building.getResourceId(), false);
						
					}
				
				}else if(lastItem!=null && !lastItem.isCovered()){
					
					itemX = lastItem.getCoords().x();
					itemY = lastItem.getCoords().y();

					if(itemX != paintScreen.getCurrentItemAreaX() || itemY != paintScreen.getCurrentItemAreaY()){

						paintScreen.setMapItem(true);
						imageResource = Resource.getMapItemResourceById(lastItem.getItemId()).getImageResource();
						setMapsCombo(lastItem.getItemId(), true);
					}
					
				}else if(paintScreen.isSelectedAreaPresent()){

					paintScreen.setSelectedAreaPresent(false);
					paintScreen.repaint();
				}
				
				paintScreen.setCurrentItemAreaX((int)(itemX - paintScreen.getDrawGridDisplacementX()));
				paintScreen.setCurrentItemAreaY((int)(itemY - paintScreen.getDrawGridDisplacementY()));	
				
				if(imageResource!=null){
					
					paintScreen.setItemArea(imageResource.getSpan());
					
					paintScreen.setSelectedAreaPresent(true);			
					paintScreen.repaint();
				}
			}
		}
	}
	
	@Override
	protected void closeDown() {
		
		parentFrame.setEnabled(true);					
		parentFrame.setDefaultCloseOperation(frame.getDefaultCloseOperation());
		
		if(isPendingWriteXml()) iDirtyMap.mapUpdated();
		
		frame.dispose();
		
	}
	
	protected String getHelpText(){
		
		return new StringBuilder("Map Editor\n\n")
						 .append("The Map Editor allows you to create a map that can be used for a level.\n")
						 .append("Each map must contain at least one warehouse.  It is important to remember to configure this to contain all the items that the player needs to build the first buildings (eg planks, workers, etc)\n\n")
						 .append("To scroll the map, left click and hold down the left mouse button on the map and move the mouse.\n\n")
						 .append("The first dropdown combo box contains all the map items, buildings and warehouses that can be placed on the map.\n")
						 .append("These are prefixed with a [M] for map items, [B] for buildings and [W] for warehouses.\n")
						 .append("All Map Items and Buildings will adhere to their Allowed On lists when being place on the map.\n")
						 .append("This dropdown combo box is only selectable in Add To Map mode.\n\n")
						 .append("There are three modes, Add to Map, Remove From Map and Configure, these are shown in the Mode dropdown combo box.\n\n")
						 .append("Add To Map mode allows you to place the Map Item or Building selected in the first dropdown combo box.\n")
						 .append("The coloured box shows the area it will take when it is placed with a left click (without mouse movement).\n")
						 .append("If the box is green, then the item/building can be placed.  If red, then it cannot be placed there.  The area on the edge of the map is a no placement area.\n\n")
						 .append("Remove from Map mode allows you to remove an item/building from the map, when the item can be removed a red box will appear on top of it, a left click will remove it.  You can only remove an Map Item if it is not covered by a Building or another Map Item.\n\n")
						 .append("Configure mode allows you to configure the amount of items a Map Item or a Warehouse is holding.  You can only configure a Map Item if it is not covered by another Map Item.\n")
						 .append("A green box will appear on an item/warehouse can be configured and a left click will bring up the configuration form.\n")
						 .append("By default, a warehouse will contain no items, a map item will contain max items.  These will adhere to the maximums set against them.\n\n").toString();
	}


	@Override
	protected void dirtyButtonUpdate() {}
	@Override
	protected void saveData() {}
	
}
