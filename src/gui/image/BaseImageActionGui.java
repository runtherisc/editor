package gui.image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.ImageResourceActions;
import game.ImageHelper;
import gui.ChildBaseGui;
import gui.ImageSliderHook;
import gui.PropertyKeys;

public abstract class BaseImageActionGui extends ChildBaseGui implements ImageSliderHook{

	protected ImageResource imageResource;
	private Coords span;
	
	private JSpinner skipSpin;
	
	protected List<JButton> imageButtons;
	protected JPanel imagePanel;

	public BaseImageActionGui(String title, JFrame parent) {
		super(title, parent);

	}

	@Override
	protected int addComponents(JFrame frame) {
		
		JPanel panel = new JPanel();
		
		int gridx = 0;
		
//		skipSpin = addLabelAndTextFieldToPanelWithListener(panel, "Skip", gridx, 0, 2, true, false);
		
		skipSpin = addLabelAndNumberSpinnerToPanel(panel, "Skip", gridx, 0, 20, 1);
		
       	
		skipSpin.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady())  setDirtyStateAndConfigure(true);
			}
		});
		
		frame.add(panel, getAllPaddingGridBagConstraints(0, 0));
		
        imageButtons = new ArrayList<JButton>();
		frame.add(imagePanel = getMultiImageSelection(null, imageButtons, enableImageSlider()), getRightPaddedGridBagConstraints(0, 1));
		
		if(enableImageSlider()) initImageSliderFromPanel(imagePanel);
		
		return 2;
	}

	@Override
	protected void imageSliderUpdated(int value){
		
		ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(getInnerFolders()), getImageTotal(getInnerFolders()), getMaxDisplayImages(), value, getInnerFolders());
	}
	
	protected void postDrawGui(){
		
		setDeleteButtonEnablement(false);
		
		//there is no slider, so we need to init here
		initFields();
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
			
			System.out.println("Inner folder on add more images"+getInnerFolders());

			if(ImageHelper.imageButtonClicked(buttonPos, getImageSliderValue(), getImageOrder(getInnerFolders()), getImageTotal(getInnerFolders()), span, imagePanel, null, getMaxDisplayImages(), getInnerFolders(), this, frame)){
				
				if(isDirtyOnImageChange()) setPendingImageSaveAndConfigure(true);
			}


		}else{
			
			System.out.println("button not reconized");
		}
		
	}
	
	protected boolean isDirtyOnImageChange(){
		
		return true;
	}
	
	
	protected void initFields() {
		
		initFieldsFromResource();
		
		boolean fromResource = !(isNewItem() || isPendingWriteXml());

		initImagesFromResource(fromResource);
		
		displayImages(0);

	}

	@Override
	protected void saveData() {
		
		ImageResourceActions resource = getResource();
		if(resource==null) resource = getNewRersource();
		
//		ValidationHelper validationHelper = new ValidationHelper();
//		validationHelper.validateInt("Skip", skipSpin.getText(), 1, 10, false);
		
		resource.setSkip((int)skipSpin.getValue());
		
		if(isPendingImageSave()){
		
			List<String> imageOrder = getImageOrder(getInnerFolders());
			int total = getImageTotal(getInnerFolders());
			
			if(total > 0) resource.setTotalNumberImages(total);
			else resource = null;
			
			setRevertAmount(getInnerFolders(), total);
			
			ImageHelper.copyFromTempToRevert(total, getInnerFolders(), imageOrder, frame);
		}
		
		setResource(resource);
	
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
////		ValidationHelper validationHelper = new ValidationHelper();
//		
//		int skip = (int)skipSpin.getValue();
//		
////		if(validationHelper.validateInt("Skip", skipSpin.getText(), 1, 10, false))
////			skip = validationHelper.getIntResult();
////		else return validationHelper.getWarning();
//		
//		int numberOfImages = getImageTotal(getInnerFolders());
//		
//		if((skip > 0 && numberOfImages == 0) || (skip == 0 && numberOfImages > 0))
//			return "images and skip must be set or neither";
		
		return null;
	}

	@Override
	protected void newButtonClicked() {
		
		if(getImageTotal(getInnerFolders()) > 0){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all images?", "Remove Images", JOptionPane.YES_NO_OPTION);
			
			if(dialogResult == JOptionPane.YES_OPTION){

				ArrayList<String> imageOrder = new ArrayList<String>();
				
				addImageOrder(getInnerFolders(), imageOrder);		
				addImageTotal(getInnerFolders(), 0);

				setPendingImageSaveAndConfigure(getResource()!=null && getResource().getTotalNumberImages() != 0);	
			
				displayImages(0);
				
			}
		}
		
	}
	
	private void initImagesFromResource(boolean fromResource){
		
		System.out.println("init image from resource? "+fromResource);
		
		ArrayList<String> imageOrder = new ArrayList<String>();
		
		addImageOrder(getInnerFolders(), imageOrder);
		
		int total = 0;
		
		if(fromResource){
			if(getResource()!=null){
				total = getResource().getTotalNumberImages();
			}
		}else{
			total = getRevertAmount(getInnerFolders());
		}
		
		System.out.println("total images "+total);
		
		addImageTotal(getInnerFolders(), total);
		
		if(total>0){

			if(fromResource){

				ImageHelper.copyPngFromReourceToTemp(null, total, null , -1, imageResource.getDirectory(), getInnerFolders(), frame);
			}else{
				ImageHelper.copyFromRevertToTemp(total, getInnerFolders(), null, frame);
			}
			
			for (int i = 0; i < total; i++) {
				imageOrder.add(String.valueOf(i));
			}
		}
	}
	
	private void initFieldsFromResource(){
		
		setFormReady(false);
		if(skipSpin!=null){
			ImageResourceActions resource = getResource();
			if(resource!=null){
				
				if(resource.getSkip() > 0) skipSpin.setValue(resource.getSkip());
			
			}else skipSpin.setValue(1);
		}
		setFormReady(true);
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		initFieldsFromResource();
		
		System.out.println("isPendingImageSave" + isPendingImageSave());
		
		if(isPendingImageSave()){
			
			initImagesFromResource(!isRevertMapSet());
	
			displayImages(getImageSliderValue());
		}

	}

	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to clear ALL images?", "Remove images", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}
	
	@Override
	protected boolean deleteActions() {
		
		//nothing to do here
		
		return true;
		
	}
	
	protected void displayImages(int offset){
	
		ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(getInnerFolders()), getImageTotal(getInnerFolders()), getMaxDisplayImages(), offset, getInnerFolders());
	}

	protected ImageResourceActions getNewRersource(){
		
		return new ImageResourceActions();
	}
	
	protected int getMaxDisplayImages(){
		return Integer.MAX_VALUE;
	}
	
	protected boolean enableImageSlider(){
		return true;
	}
	
	public void updatedImageTotal(int total){
		addImageTotal(getInnerFolders(), total);
	}
	
	protected abstract String getInnerFolders();

	protected abstract ImageResourceActions getResource();
	
	protected abstract void setResource(ImageResourceActions resource);
}
