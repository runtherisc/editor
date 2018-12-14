package gui;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.map.resources.Resource;
import gui.building.BuildingGui;
import gui.building.WarehouseGui;
import gui.image.BuildingImageGui;
import gui.image.MapImageGui;
import gui.item.ItemGui;
import gui.level.LevelGui;
import gui.mapobject.MapObjectGui;

public class MainMenuGui extends BaseGui{
	
	JButton itemsButton, mapImageButton, buildingImageButton, mapObjectButton, warehouseButton, buildingButton, levelButton, helpButton;
	
	public MainMenuGui(){
		
		super("Little Hoarders Editor");

	}
	
	
	@Override
	protected int addComponents(JFrame frame) {
		

		itemsButton = createGuiButtonAndListener(new ItemGui("Items", frame), "Item", Resource.getNumberOfItemRes());
		frame.add(itemsButton, getAllPaddingGridBagConstraints(0, 0));
		
		JPanel panel = new JPanel(new GridBagLayout());
		mapImageButton = createGuiButtonAndListener(new MapImageGui("Map Item Images", frame), "Map Item Images", Resource.getNumberOfMapImageRes());
		panel.add(mapImageButton, getSidePaddedGridBagConstraints(0, 0));
		
		buildingImageButton = createGuiButtonAndListener(new BuildingImageGui("Building Images", frame), "Building Images", Resource.getNumberOfBuildingImageRes());
		panel.add(buildingImageButton, getSidePaddedGridBagConstraints(1, 0));
		frame.add(panel, getNoPaddingGridBagConstraints(0, 1));
		
		mapObjectButton = createGuiButtonAndListener(new MapObjectGui("Map Items", frame), "Map Item", Resource.getNumberOfMapItemRes());
		frame.add(mapObjectButton, getAllPaddingGridBagConstraints(0, 2));
		
		panel = new JPanel(new GridBagLayout());
		warehouseButton = createGuiButtonAndListener(new WarehouseGui("Warehouses", frame), "Warehouse", Resource.getNumberOfBuildingRes(true));
		panel.add(warehouseButton, getSidePaddedGridBagConstraints(0, 0));
		
		buildingButton = createGuiButtonAndListener(new BuildingGui("Buildings", frame), "Building", Resource.getNumberOfBuildingRes(false));
		panel.add(buildingButton, getSidePaddedGridBagConstraints(1, 0));
		frame.add(panel, getNoPaddingGridBagConstraints(0, 3));

		levelButton = createGuiButtonAndListener(new LevelGui("Level Diag", frame, true), "Level", Resource.getNumberOfLevelRes());
		frame.add(levelButton, getAllPaddingGridBagConstraints(0, 4));
        
        helpButton = new JButton("Help");
        frame.add(helpButton, getAllPaddingGridBagConstraints(0, 6));
        
        helpButton.addActionListener(this);
        
        return 7;
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame){
		
		super.otherActions(button, frame);
		
		if(button == helpButton){
			
			System.out.println("help");
			new HelpMenu("Little Hoarders Editor", getText());
		}
	}
	
	private String getText(){
		return new StringBuilder("From the main menu you can define all the elements needed to create a Little Hoarders ecosystem.\n\n")
						 .append("The main elements are:\n\n")
						 .append("-Items (including workers) eg wood, planks, woodcutters, sawmill workers etc.\n")
						 .append("-Map Item Images used by map items (eg Trees, ponds, deer etc.)\n")
						 .append("-Building Images used by buildings (eg warehouses, woodcutters huts etc.)\n")
						 .append("-Map Items eg Trees (containing wood), ponds (containing fish and water) etc.\n")
						 .append("-Warehouses which are buildings that can store items and be paired with.\n")
						 .append("-Buildings, eg woodcutter (that cut down trees to get wood), sawmill (that take wood form a warehouse and turn it into planks) etc.\n")
						 .append("-Levels; what is required to complete a level, level maps etc.\n\n")
						 .append("Some elements cannot be defined until you have other elements defined, eg you cannot create a warehouse until you have defined a set of images that it will use.\n\n")
						 .append("It is also worth noting that you cannot delete something if it is used by something else, eg you cannot delete a tree if a woodcutter is set to cut it down or it is placed on a map.\n\n")
						 .append("The best way is to start at the top, defining Items first then working you way down and finally defining Level requirements.\n\n")
						 .append("This editor is made with two basic form types; Slider form types and Table form types.\n\n")
						 .append("The slider form type has a slider with all the ids of each item that has been created, this can be dragged back and forth for you to edit a previously created item.\n\n")
						 .append("The Table form type has a table where each row is an item that has been created.\n\n")
						 .append("There is a 'Help' button on both these form types to explain the various fields and how they are used.\n\n")
						 .append("The 'New' button on a slider form creates another item on the slider, for you to complete the fields and create an item.  On the Table form, it clears the fields back to their default state.\n\n")
						 .append("The 'Delete' button deletes the currently displayed item on the slider, on the Table form type, it clears the entire table (this is disabled on some forms), this action cannot be reverted, but you will be warned.\n\n")
						 .append("The 'Save' button will save your changes for that form, but must also be saved on the parent (previous) form, the changes will only be permanent when saved on a 'Save To XML' main form.\n\n")
						 .append("The 'Revert' button will undo your changes back to the last saved state where possible.\n\n")
						 .append("The 'Close' will close the current form, you will be warned if there are any unsaved changes.  You cannot edit a parent form whilst it has a child open.\n\n")					 
						 .append("It is a good idea to have a set of png images ready for use before creating your ecosystem (I will supply an example project with images that you can use, but it might be fun to create your own).  Having all the frames with alphanumeric naming will make it easier to import them in order into the editor.\n\n")
						 .append("After you have created your Little Hoarders project, you will need some webspace to upload it to.  If you are using a hosting website, it is important that the filenaming and file structure does not change afer uploading, or the game will not be able to find the assets.\n\n").toString();
	}


	@Override
	protected void saveData() {}
	
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		if(childName.equals(MapObjectGui.class.getName())){
			
			if(Resource.getNumberOfMapImageRes()==0) return "Map Item Images need to be created first";
		
		}else if(childName.equals(BuildingGui.class.getName()) || childName.equals(WarehouseGui.class.getName())){
			
			if(Resource.getWarehouseItemInternalNames().isEmpty()) return "An item needs to be created first";
			if(Resource.getWorkerItemInternalNames().isEmpty()) return "A worker needs to be created first";
			if(Resource.getNumberOfBuildingImageRes()==0) return "Building Images need to be created first";
			
		}else if(childName.equals(LevelGui.class.getName())){
			
			if(Resource.getNumberOfBuildingRes(true)==0) return "Warehouse needs to be created first";
		}
		return null;
	}

	@Override
	protected void dirtyButtonUpdate() {
		
		updateButtonLabelWithItems(itemsButton, "Item", Resource.getNumberOfItemRes());
		updateButtonLabelWithItems(mapImageButton, "Map Item Images", Resource.getNumberOfMapImageRes());
		updateButtonLabelWithItems(buildingImageButton, "Building Images", Resource.getNumberOfBuildingImageRes());
		updateButtonLabelWithItems(mapObjectButton, "Map Item", Resource.getNumberOfMapItemRes());
		updateButtonLabelWithItems(warehouseButton, "Warehouses", Resource.getNumberOfBuildingRes(true));
		updateButtonLabelWithItems(buildingButton, "Buildings", Resource.getNumberOfBuildingRes(false));
		updateButtonLabelWithItems(levelButton, "Level", Resource.getNumberOfLevelRes());
	}


}
