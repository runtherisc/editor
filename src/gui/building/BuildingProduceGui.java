package gui.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import data.map.resources.BuildingActionProduceResource;
import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingActionResource;
import data.map.resources.ItemResource;
import data.map.resources.Resource;
import gui.PropertyKeys;

public class BuildingProduceGui extends BaseRequirementsGui {

	public BuildingProduceGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private BuildingActionResource buildingActionResource;
	private List<BuildingActionProduceResource> tableItems, revertTableItems;
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		super.passedBundle(properties);
	
		buildingActionResource = (BuildingActionResource) properties.get(PropertyKeys.BUILDING_ACTION_RESOURCE);
	}
	
	@Override
	protected BuildingActionRequireResource createNewResource(){
		
		return new BuildingActionProduceResource();
	}

	@Override
	protected String getComponetNamePrefix() {
		return "Produced";
	}
	
	@Override
	protected String[] initItemNames(List<Integer> items){
		
		List<ItemResource> resourceItems = Resource.getWarehouseItemInternalNames();
		
		String[] itemNames = new String[resourceItems.size()+1];
		itemNames[0] = "<none>";
		items.add(-1);
		for (int i = 1; i < itemNames.length; i++) {
			itemNames[i] = resourceItems.get(i-1).getName();
			items.add(resourceItems.get(i-1).getId());
		}
		
		return itemNames;
	}

	@Override
	protected void clearTable() {
		tableItems = new ArrayList<BuildingActionProduceResource>();
		
	}
	
	@Override
	protected void configureTableList(){
		
		List<BuildingActionProduceResource> resourceList = buildingActionResource.getProduces();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(BuildingActionProduceResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	
	protected List<BuildingActionProduceResource> copyTableItems(List<BuildingActionProduceResource> tableItems){
		
		ArrayList<BuildingActionProduceResource> tableItemsCopy = new ArrayList<BuildingActionProduceResource>();
		
		for (BuildingActionProduceResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}

	@Override
	protected List<? extends BuildingActionRequireResource> getTableItems() {
		
		return tableItems;
	}

	@Override
	protected void addRowToTableItems(int row, BuildingActionRequireResource resource) {
		
		if(row == -1) tableItems.add((BuildingActionProduceResource) resource);
		else tableItems.add(row, (BuildingActionProduceResource) resource);
		
	}

	@Override
	protected void saveData() {
		
		revertTableItems = copyTableItems(tableItems);
		buildingActionResource.setProduceList(copyTableItems(tableItems));
		
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<BuildingActionProduceResource>();
			configureTableList();

		}else{
			
			tableItems = copyTableItems(revertTableItems);
		}
		
		displayTable(tableItems);
		
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("")
						 .append("Building Production\n\n")
						 .append("This is where you can configure what a building produces for the task you selected on the previous form.\n")
						 .append("A building can produce items that get taken to it's paired warehouse, or Map Items that get placed on the map within it's selected area.\n\n")
						 .append("The Produced Item dropdown combo box contains all warehouse items, you can either select an item you want to be created and taken to the warehouse, or select <none> if you wish to place a map item\n\n")
						 .append("Produced Amount is how many if the item is produced and is required if the item is being returned to the warehouse.\n\n")
						 .append("Produced Carry is how many of this item a worker can carry when returning it to the warehouse.\n\n")
						 .append("Required Worker Out dropdown combo box contains all the workers that have been created.  This would typically be a worker image set of a worker carrying the item.\n\n")
						 .append("Required Worker In dropdown combo box contains all the workers that have been created.  This would typically be the worker image set of a empty handed worker that would be returning to the building.\n\n")
						 .append("When you are happy with your selection, click the Add button to add them to the table.\n")
						 .append("If you selected <none> for the Produced Item, you will need to configure at least one map item.  To do this, selected the newly added row and click the Map Items button.  If multiple map items are added, then one at random will be selected.\n")
						 .append("Remember that an area needs to be set on the preivous form before you can add a Map Item.\n\n")
						 .append("You can Edit or Remove a previously added row, by selecting the row and using the Edit and Remove buttons.").toString();
	}



}
