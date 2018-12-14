package gui.image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.MultiImageResourceAction;
import game.ImageHelper;
import gui.ChildBaseGui;
import gui.ImageSliderHook;
import gui.PropertyKeys;

public class ImageSingleIdleGui extends ChildBaseGui implements ImageSliderHook{

	private ImageResource imageResource;
	private Coords span;
	private String INNERFOLDER = "idle/sequence1";
	
	private List<JButton> imageButtons;
	private JPanel imagePanel;

	public ImageSingleIdleGui(String title, JFrame parent) {
		super(title, parent);
	}

	@Override
	protected int addComponents(JFrame frame) {
		
        imageButtons = new ArrayList<JButton>();
		frame.add(imagePanel = getMultiImageSelection(null, imageButtons, false), getRightPaddedGridBagConstraints(0, 0));
		
		return 1;
	}
	
	protected void postDrawGui(){
		
		setDeleteButtonEnablement(false);
		
		//there is no slider, so we need to init here
		initFields(getRevertAmount(INNERFOLDER)==0);
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
		
		span = (Coords) properties.get(PropertyKeys.IMAGE_RESOURCE_SPAN);

	}

	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);

			
		int buttonPos = imageButtons.indexOf(button);
		
		if(buttonPos > -1){

			if(ImageHelper.imageButtonClicked(buttonPos, 0, getImageOrder(INNERFOLDER), getImageTotal(INNERFOLDER), span, imagePanel, null, 8, INNERFOLDER, this, frame)){
				
				setPendingImageSaveAndConfigure(true);
			}

			
			
		}else{
			
			System.out.println("button not reconized");
		}
		
	}
	
	
	protected void initFields(boolean fromResource) {
			
		ArrayList<String> imageOrder = new ArrayList<String>();
		
		addImageOrder(INNERFOLDER, imageOrder);
		addImageTotal(INNERFOLDER, imageOrder.size());
		
		int total = 0;
		
		if(fromResource){
			if(imageResource.getIdleId()>-1) total = imageResource.getIdleTotal();
		}else{
			total = getRevertAmount(INNERFOLDER);
		}
		
		addImageTotal(INNERFOLDER, total);
		
		if(total>0){
		
			if(fromResource){
				ImageHelper.copyPngFromReourceToTemp(null, total, null , -1, imageResource.getDirectory(), INNERFOLDER, frame);
			}else{
				ImageHelper.copyFromRevertToTemp(total, INNERFOLDER, null, frame);
			}
			
			
			for (int i = 0; i < total; i++) {
				imageOrder.add(String.valueOf(i));
			}
		}
		
		ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(INNERFOLDER), getImageTotal(INNERFOLDER), 8, 0, INNERFOLDER);

	}

	@Override
	protected void saveData() {
		
		List<String> imageOrder = getImageOrder(INNERFOLDER);
		int total = getImageTotal(INNERFOLDER);
		
		MultiImageResourceAction mainResource = new MultiImageResourceAction();
		mainResource.setId(1);
		mainResource.setInternalName("main");
		mainResource.setSequence(1);
		mainResource.setTotalNumberImages(total);
		if(imageResource.getIdles().isEmpty()){
			imageResource.addIdle(mainResource);
		}else{
			imageResource.setIdle(0, mainResource);
		}
		imageResource.setIdleId(1);
		
		setRevertAmount(INNERFOLDER, total);
		
		ImageHelper.copyFromTempToRevert(total, INNERFOLDER, imageOrder, frame);
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		int size = getImageTotal(INNERFOLDER);
		
		if(size!=1 && size!=2 && size!=4 && size!=8){
			return "The number of idle images must be either 1, 2, 4 or 8 (currently "+size+")";
		}
		
		return null;
	}

	@Override
	protected void newButtonClicked() {
		
		if(getImageTotal(INNERFOLDER) > 0){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all images?", "Remove Images", JOptionPane.YES_NO_OPTION);
			
			if(dialogResult == JOptionPane.YES_OPTION){

				ArrayList<String> imageOrder = new ArrayList<String>();
				
				addImageOrder(INNERFOLDER, imageOrder);		
				addImageTotal(INNERFOLDER, imageOrder.size());		

//				setPendingImageSaveAndConfigure((imageResource.getIdleId() == -1 && getRevertAmount(INNERFOLDER) > -1)||
//												(imageResource.getIdleId() > -1 && getRevertAmount(INNERFOLDER) == -1));
				setPendingImageSaveAndConfigure(true);//cannot save without images anyway
			
				ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(INNERFOLDER), getImageTotal(INNERFOLDER), 8, 0, INNERFOLDER);	
				
			}
		}
		
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		initFields(!isRevertMapSet());
	}

//	@Override
//	protected void deleteConfirmation(){
//		
//		int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to clear ALL images?", "Remove images", JOptionPane.YES_NO_OPTION);
//		if(dialogResult == JOptionPane.YES_OPTION){
//
//			deleteConfirmationAccepted();
//		}
//	}
	
	@Override
	protected boolean deleteActions() {
		
		//nothing to do here
		
		return true;
		
	}
	
	@Override
	public void updatedImageTotal(int total) {
		addImageTotal(INNERFOLDER, total);
		
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Idle Images\n")
						 .append("Idle images are a looped set of images that are displayed when a map item is not being interacted with, created or destroyed.\n")
						 .append("The idle image set can be comprised of 1, 2, 4 or 8 images.  These will be used over a 8 game tick period.\n")
						 .append("if 8 images are supplied then each image will be used, 1 for each game tick that passes, once all 8 images have been used then the sequence will start again.\n")
						 .append("if 4 images are supplied then each image will be used for 2 game ticks each, the sequence will start again when 8 game ticks have passed.\n")
						 .append("if 2 images are supplied then both will be held for 4 game ticks each, and a single image will be used for every game tick (and appear static)\n\n")
						 .append("Use the Add button to add either 1,2,4 or 8 images, the order of these can be changed using the Swap button after being added.\n")
						 .append("The Clear button can be use the remove the last image in the sequence.\n")
						 .append("(NB.  The New button will clear all the images after confirmation)").toString();
	}



}
