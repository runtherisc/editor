package gui.building;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.map.resources.Resource;

public class BuildingGui extends BaseBuildingGui {
	
	private JButton actionButton;

	public BuildingGui(String title, JFrame parent) {
		super(title, parent);
		List<Integer> ids = Resource.getFilteredBuildingResourceIds(false);
		if(ids.isEmpty()) ids.add(Resource.getNextBuildingResourceId());
		setupSlider(ids);
	}
	
	@Override
	protected void topPanel(JPanel panel){
		
		actionButton = new JButton("Actions");
      	addGuiButtonAndListener(new BuildingActionGui("Building Actions", frame), actionButton);
    	panel.add(actionButton);
	}

	
	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(actionButton);
		if(childDirty) setDirtyChildren(true);	
		updateButtonLabelWithState(actionButton, "Actions", childDirty);
		
	}
	
	

	@Override
	protected String validatePreSaveDataAndReturnIssues() {

		String warning = super.validatePreSaveDataAndReturnIssues();
		
		if(warning==null){
			
			if((buildingResource.getBuildingActionList()==null || buildingResource.getBuildingActionList().isEmpty()))
				return "at least one building action must be set";
		}
		
		return warning;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Buildings\n\n")
				 .append("Buildings interact with map items and take items to and from warehouses, this is defined in the action section.\n\n")
				 .append("The Name text field is a mandatory requirement and is used to identify the building in the editor.\n\n")
				 .append("Localized text are required to describe the building to the player.\n\n")
				 .append("Actions are required and is where you define what a building does.\n\n")
				 .append("The image dropdown combo box contains all of the building image resource sets.  You must select a image set that the building uses.  Multiple buildings can use the same image resource set.\n\n")
				 .append("The Workers box is where you can set the number of workers that work in the building.\n")
				 .append("All workers will help with the work, but they will only work on the following states and will not move onto the next state until the previous has been completed.\n")
				 .append("These states are; getting required items (from map items and/or a warehouse), working (at the building) and depositing items (either to the map and/or to the warehouse).\n\n")
				 .append("The Allowed On is a multiple selection popup that allows you to select all the map items that the building is allowed to be placed upon.\n")
				 .append("Grass (default) is always in the list and means anywhere where a item has not already been placed, if this is unchecked, then you can only place it upon items you have selected, rather than as well as.\n")
				 .append("Buildings can be placed across items, for example, if you have grass checked and a mountain item checked, then this item could be place half on the mountain and half on the grass.").toString();
	}

}
