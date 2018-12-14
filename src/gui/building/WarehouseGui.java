package gui.building;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.map.resources.Resource;

public class WarehouseGui extends BaseBuildingGui {

	public WarehouseGui(String title, JFrame parent) {
		super(title, parent);
		List<Integer> ids = Resource.getFilteredBuildingResourceIds(true);
		if(ids.isEmpty()) ids.add(Resource.getNextBuildingResourceId());
		setupSlider(ids);
	}
	
	private JButton storedItemsButton;

	@Override
	protected void topPanel(JPanel panel){
		
      	storedItemsButton = new JButton("Stored Items");
      	addGuiButtonAndListener(new WarehouseStoredItemGui("Stored Items", frame), storedItemsButton);
    	panel.add(storedItemsButton);
	}
	
	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(storedItemsButton);
		if(childDirty) setDirtyChildren(true);	
		updateButtonLabelWithState(storedItemsButton, "Stored Items", childDirty);
		
	}
	
	@Override
	protected String validatePreSaveDataAndReturnIssues() {

		String warning = super.validatePreSaveDataAndReturnIssues();
		
		if(warning==null){
			
			if((buildingResource.getBuildingItemMap()==null || buildingResource.getBuildingItemMap().isEmpty()))
				return "at least one warehouse item must be set";
		}
		
		return warning;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Warehouses\n\n")
						 .append("Every level requires a warehouse, it's the place to store the items and workers need for the buildings and to complete the levels.\n\n")
						 .append("Not only can warehouses store items, but they can also create items (sometimes from other items) periodically.\n\n")
						 .append("The Name text field is a mandatory requirement and is used to identify the warehouse in the editor.\n\n")
						 .append("Localized text are required to describe the warehouse to the player.\n\n")
						 .append("Stored Items is where you can set what items can be stored in the warehouse and the maximum amounts.  It is also where you can setup periodical item creation.\n\n")
						 .append("The image dropdown combo box contains all of the building image resource sets.  You must select a image set that the warehouse uses.  Multiple warehouses can use the same image resource set.\n\n")
						 .append("The Workers box is where you can set the number of workers that work in the warehouse.  They are used to transport items to and from building during their construction and destruction stages.  They are also used to transfer items to other warehouses.\n\n")
						 .append("The Allowed On is a multiple selection popup that allows you to select all the map items that the warehouse is allowed to be placed upon.\n")
						 .append("Grass (default) is always in the list and means anywhere where a item has not already been placed, if this is unchecked, then you can only place it upon items you have selected, rather than as well as.\n")
						 .append("Warehouses can be placed across items, for example, if you have grass checked and a mountain item checked, then this item could be place half on the mountain and half on the grass.").toString();
	}
}
