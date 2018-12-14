package gui.image;

import javax.swing.JFrame;

import data.map.resources.ImageResourceActions;

public class ImageDestructionGui extends BaseImageActionGui {

	public ImageDestructionGui(String title, JFrame parent) {
		super(title, parent);
	}

	@Override
	protected String getInnerFolders() {
		return "destruction/";
	}

	@Override
	protected ImageResourceActions getResource() {
		return imageResource.getDestruction();
	}

	@Override
	protected void setResource(ImageResourceActions resource) {
		imageResource.setDestruction(resource);
	}

	@Override
	protected String getHelpText() {

		return new StringBuilder("Destruction Images\n\n")
				 .append("This is where you can define the images that are used during destruction.\n\n")
				 .append("The skip is the number of game ticks that the image is held for before the game displays the next image.\n\n")
				 .append("Click the Add button and select the images you want to display, you can select multiple images and they will be added in an alphanumic order, depending on their filename\n")
				 .append("When images have been added, you can swap them with the image to their right (by clicking Swap) in order to put them im the right posistions.\n")
				 .append("If an incorrect image has been added, it can be removed if it the last by clicking on Clear.\n")
				 .append("You can scroll through the added images by dragging the slider horizontally\n\n")
				 .append("Note for buildings: If there are no destruction images set, then the player will not be able to destroy the building after it has been fully built.").toString();
	}

}
