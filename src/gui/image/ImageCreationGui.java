package gui.image;

import java.util.List;

import javax.swing.JFrame;

import data.map.resources.BuildingCreationResource;
import data.map.resources.ImageResourceActions;
import game.ImportHelper;

public class ImageCreationGui extends BaseImageActionGui {
	
	public ImageCreationGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		String issues = super.validatePreSaveDataAndReturnIssues();
		
		if(issues!=null) return issues;
				
		List<BuildingCreationResource> creationList = imageResource.getBuildingCreationList();
		
		if(creationList != null && creationList.size() > 1){
				
			int newLastFrame = getImageTotal(getInnerFolders())-1;
			
			if(newLastFrame < 1) imageResource.setBuildingCreation(null);
			else ImportHelper.adjustBuildingCreationRequirementEndframe(creationList, newLastFrame);
		}
		
		return null;
	}

	@Override
	protected String getInnerFolders() {
		return "creation/";
	}

	@Override
	protected ImageResourceActions getResource() {
		return imageResource.getCreation();
	}

	@Override
	protected void setResource(ImageResourceActions resource) {
		imageResource.setCreation(resource);
	}

	@Override
	protected String getHelpText() {
		
		return new StringBuilder("Creation Images\n\n")
						 .append("This is where you can define the images that are used during creation.\n\n")
						 .append("The skip is the number of game ticks that the image is held for before the game displays the next image.\n\n")
						 .append("Click the Add button and select the images you want to display, you can select multiple images and they will be added in an alphanumic order, depending on their filename\n")
						 .append("When images have been added, you can swap them with the image to their right (by clicking Swap) in order to put them im the right posistions.\n")
						 .append("If an incorrect image has been added, it can be removed if it the last by clicking on Clear.\n")
						 .append("You can scroll through the added images by dragging the slider horizontally").toString();
	}

}
