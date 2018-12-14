package gui.image;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.Resource;
import game.ImageHelper;
import gui.ChildBaseGui;
import gui.ImageSliderHook;
import gui.PropertyKeys;
import gui.ValidationHelper;

public abstract class BaseImageGui extends ChildBaseGui implements ImageSliderHook{

	public BaseImageGui(String title, JFrame parent) {
		super(title, parent);
		setupSlider(getAllImageIds());
		setCanWriteXml(true);
	}
	
	protected ImageResource imageResource;
	
//	private JButton idleButton, walkover;
	private JTextField nameTxt;
	private JSpinner spanX, spanY;
	private String[] filenames = new String[]{"static"};
	
	private String idleImagesStr = "Idle Images";
	private String creationStr = "Creation";
	private String destructionStr = "Destruction";
	private String busyStr = "Action";
	
	private JButton walkOverBtn;
	
	protected List<JButton> imageButtons;
	protected JPanel imagePanel;
	
	private Coords spanPastToWalkover;
	
	private String walkoverStr = "Walkover Grid";
	
	protected String onMapWarning;
	
	protected Map<String, JButton> childrenButtons = new HashMap<String, JButton>();
	
	protected void addGenericImageConponents(JPanel panel){
      	
      	JPanel panel2 = new JPanel(new GridBagLayout());
      	
      	nameTxt = addLabelAndTextFieldToPanelWithListener(panel2, "Name", 0, 0, 17, true, false);
		spanX = addLabelAndNumberSpinnerToPanel(panel2, "Span X", 2, 0, 9, 0);
       	
		spanX.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady()){
					
					String issue = checkIfItemChanges(imageResource.getId());
					if(issue=="") issue = getOnMapWarning();
					if(issue==""){
					
						  setDirtyStateAndConfigure(true);
					}else{
						
						displayWarning(issue);
						setFormReady(false);
						spanX.setValue(imageResource.getSpan().x());
						setFormReady(true);
					}
				}
			}
		});
		
		spanY = addLabelAndNumberSpinnerToPanel(panel2, "Span Y", 4, 0, 9, 0);
       	
		spanY.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady()) {
					
					String issue = checkIfItemChanges(imageResource.getId());
					if(issue=="") issue = getOnMapWarning();
					if(issue==""){
					
						 setDirtyStateAndConfigure(true);
					}else{
						
						displayWarning(issue);
						setFormReady(false);
						spanY.setValue(imageResource.getSpan().y());
						setFormReady(true);
					}
				}
			}
		});
      	
      	panel.add(panel2, getSlightPaddingGridBagConstraints(0, 0));

        //row 1
      	panel2 = new JPanel(new GridBagLayout());
      	
      	JButton idleButton = new JButton(idleImagesStr);
      	
      	addIdleGui(idleButton);
      	
      	childrenButtons.put(idleImagesStr, idleButton);
    	
      	panel2.add(idleButton, getSlightPaddingGridBagConstraints(0, 0));
      	
      	walkOverBtn = new JButton(walkoverStr);

      	addGuiButtonAndListener(new ImageWalkoverGui("Walkover detection", frame), walkOverBtn);
      	
	  	panel2.add(walkOverBtn, getSlightPaddingGridBagConstraints(1, 0));
    	
	  	panel.add(panel2, getNoPaddingGridBagConstraints(0, 1));

	  	//row 2
	  	panel2 = new JPanel(new GridBagLayout());

      	
      	JButton creationButton = new JButton(creationStr);    	

      	addGuiButtonAndListener(new ImageCreationGui("Image Creation", frame), creationButton);
      	
      	childrenButtons.put(creationStr, creationButton);
      	
      	panel2.add(creationButton, getSlightPaddingGridBagConstraints(0, 0));
    	
      	JButton destructionButton = new JButton(destructionStr);  
      	
      	addGuiButtonAndListener(new ImageDestructionGui("Image Destruction", frame), destructionButton);
    	
      	childrenButtons.put(destructionStr, destructionButton);
      	
      	panel2.add(destructionButton, getSlightPaddingGridBagConstraints(1, 0));
    	
    	JButton busyButton = new JButton(busyStr);  

    	addGuiButtonAndListener(new ImageActionGui("Image Action", frame), busyButton);
      	
      	childrenButtons.put(busyStr, busyButton);
    	
      	panel2.add(busyButton, getSlightPaddingGridBagConstraints(2, 0));
      	
      	panel.add(panel2, getNoPaddingGridBagConstraints(0, 2));

      	
      	
	}
	
	protected void addIdleGui(JButton idleButton){
		
		addGuiButtonAndListener(new ImageMultiIdleGui("Idle Images", frame), idleButton);
	}
	
	protected void addStaticConponetsToPanel(JPanel panel){
		
        imageButtons = new ArrayList<JButton>();
        panel.add(imagePanel = getMultiImageSelection(filenames, imageButtons, false), getRightPaddedGridBagConstraints(1, 0));
	}

	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == imageButtons.get(0)){
			
			Coords span = getSpan();
			
			if(span !=null){
				
				if(ImageHelper.imageButtonClicked(0, 0, getImageOrder(null), getImageTotal(null), span, imagePanel, filenames, filenames.length, "", this, frame)){
					
					setPendingImageSaveAndConfigure(true);
				}
	
				
				
			}else{
				
				displayWarning("Span X and Span Y must be set before images can be added");
			}
		}
		
		
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {

		if(getImageResourceList() == null || getImageResourceList().isEmpty()){
			setNewItem(true);
			imageResource = new ImageResource();
			imageResource.setId(Resource.getNextImageResourceId());
		}else{
			setNewItem(false);
			imageResource = getFirstImageResource();
		}

	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		System.out.println("adding to passed bundle");
		
		if(this instanceof MapImageGui) imageResource.setMapObject(true);
	
		addToPassedProperties(PropertyKeys.IMAGE_RESOURCE, imageResource);
		
//		if(childName.equals(ImageIdleGui.class.getName()) || 
//			childName.equals(ImageCreationGui.class.getName()) ||
//			childName.equals(ImageDestructionGui.class.getName())){
		

//		else{
			
		int spanXvalue = (int) spanX.getValue();
		int spanYvalue = (int) spanY.getValue();
		
		if(spanXvalue == 0 || spanYvalue == 0) return "Span X and Span Y must be set first";
		
		Coords span = new Coords(spanXvalue, spanYvalue);
		
		addToPassedProperties(PropertyKeys.IMAGE_RESOURCE_SPAN, span);
		
		if(childName.equals(ImageWalkoverGui.class.getName())){
			
//			if(imageResource.getDirectory()==null) return "name must be set first";
			
			if(!ImageHelper.isStaticImageAddedToTemp()) return "static image must be added first";
			
			if(getOnMapWarning()!=null && getOnMapWarning().length() > 0) return getOnMapWarning();

			spanPastToWalkover = span;
			
		}else if(childName.equals(ImageCreationRequirementGui.class.getName())){
			
			if(Resource.getWarehouseItemInternalNames().isEmpty()) return "No Warehouse Items have been added";
			
			if(imageResource.isIdlesEmpty()) return "Idle images must be set first";
			
			if(imageResource.getCreation()==null) return "All creation images must be added first";
			
			addToPassedProperties(PropertyKeys.REQUIREMENT_MAX_FRAME, imageResource.getCreation().getTotalNumberImages());
			
			addToPassedProperties(PropertyKeys.IMAGE_CREATION_SAVE_PENDING, !getChildsRevertMap(childrenButtons.get(creationStr)).isEmpty());
			
		}else if(childName.equals(ImageDestructionRequirementGui.class.getName())){
			
			if(Resource.getWarehouseItemInternalNames().isEmpty()) return "No Warehouse Items have been added";
			
			if(imageResource.isIdlesEmpty()) return "Idle images must be set first";
			
		}
//		}}
		
		
		
		return null;
	}

	@Override
	protected void sliderUpdateLightComponents(int pos) {

		super.sliderUpdateLightComponents(pos);

		imageResource = getImageResourceById(pos);		
		
		spanPastToWalkover = imageResource.getSpan();
		
		onMapWarning = null;
		
		System.out.println("update light slider");
		
		initLightFields();
		
		
	}

	@Override
	protected void sliderAllComponents(int pos) {

		super.sliderAllComponents(pos);
		
		configureMandatoryChildrenCompletion(!isNewItem());

		if(!isPendingImageSave()){
			
			initImages();
		
		}
		
//		ImageHelper.displayStaticImage(staticImageLabel, staticButton, getSpan(false));
		ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), getSpan(), imagePanel, getImageOrder(null), getImageTotal(null), 1, 0, "");
		
//		initLightFields();
	}
	
	private void initImages(){
		
//		ImageHelper.deleteTempFolder();
		
		if(imageResource.getDirectory()!=null){
		
			ImageHelper.copyPngFromReourceToTemp(filenames, 1, null, -1, imageResource.getDirectory(), "", frame);
			ArrayList<String> imageOrder = new ArrayList<String>();
			imageOrder.add(filenames[0]);
			addImageOrder(null, imageOrder);
			addImageTotal(null, imageOrder.size());
		}
	}
	
	private void configureMandatoryChildrenCompletion(boolean formComplete){
		
		setChildFormComplete(walkOverBtn, formComplete);
	}

	@Override
	protected void postDrawGui() {

		super.postDrawGui();
		
		if(isNewItem()){
			clearFields(false);
			setFormReady(true);
		//sliderAllComponents isn't called when there is only one item
		}else if(getNumberOfImageResources()==1){
			sliderAllComponents(getFirstImageResource().getId());
		}
	}
	
	@Override
	protected void newButtonClicked() {

		setNewItem(true);
		imageResource = new ImageResource();
		int id = Resource.getNextImageResourceId();
		imageResource.setId(id);
		List<Integer> ids = getAllImageIds();
		ids.add(id);
		updateSlider(ids, true);
		clearFields(false);
		configureMandatoryChildrenCompletion(false);
		onMapWarning = "";
		
	}

	@Override
	protected void saveData() {

//		ValidationHelper validationHelper = new ValidationHelper();
		
		String oldDir = imageResource.getDirectory();
		imageResource.setDirectoryByName(nameTxt.getText().trim().toLowerCase().replace(" ", "_"));
		String newDir = imageResource.getDirectory();
		if(oldDir !=null && oldDir != newDir){
			
			ImageHelper.renameImageFolder(oldDir, newDir);
		}
		
		imageResource.setSpan(new Coords((int)spanX.getValue(), (int)spanY.getValue()));
		
		System.out.println("pending save "+isPendingImageSave()+ "images "+getImageTotal(null));
		
		if(isPendingImageSave()){

			ImageHelper.copyPngFromTempToResource(null, -1, newDir, filenames, filenames.length, getImageOrder(null), "", frame);
			
			setPendingImageSaveAndConfigure(false);
		
		}
		
		for (JButton jButton : childrenButtons.values()) {		
			
			if(isChildGuiImageSavePending(jButton)){

				ImageHelper.processImageOrderMaps(getChildsRevertMap(jButton), newDir, frame);
			}
		}
		
	}
	
	@Override
	protected void postSaveData(){

		setFormReady(false);//turn off the textbox listener
		initLightFields();
		updateSlider(getAllImageIds(), false);
		setFormReady(true);//turn them back on
		
		super.postSaveData();
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {

		ValidationHelper validationHelper = new ValidationHelper();
		
		if(!validationHelper.validateFileName("Name", nameTxt.getText()))
			return validationHelper.getWarning();
		
		if(Resource.isImageNameUsed(validationHelper.getStringResult(), imageResource.getId())){
			return "Image Name is already in use";
		}
		
		int spanXvalue = (int) spanX.getValue();
		int spanYvalue = (int) spanY.getValue();
		
		if(spanXvalue == 0 || spanYvalue == 0) return "Span X and Span Y must be set";
		
		if(getImageTotal(null)==0) return "Static image must be set";
		
		System.out.println("walkover form complete? "+isChildFormComplete(walkOverBtn));
		
		if((spanPastToWalkover==null && !getSpan().equals(imageResource.getSpan())) || !isChildFormComplete(walkOverBtn)){
			
			return "Walkover Grid must be configured";
			
		}else if(spanPastToWalkover!=null && !spanPastToWalkover.equals(getSpan())){
			
			return "Walkover Grid must be redone as span has changed";
		}
		
		if(imageResource.getIdleId()==-1 && imageResource.getMovement()==null) return "main idle has not been set, use 'Idle Images' button";

		return null;
	}

	@Override
	protected void revertUnsavedChanges(int pos) {

		if(isNewItem()){
			clearFields(false);
		}else{
			if(isPendingImageSave()){
				initImages();
				ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), getSpan(), imagePanel, getImageOrder(null), getImageTotal(null), 1, 0, "");
			}
			initLightFields();
		}
	}

	@Override
	protected boolean deleteActions() {

		boolean enableButton = true;
		
		if(isNewItem()){
			
			List<Integer> ids = getAllImageIds();
			if(ids.isEmpty()){
				System.out.println("delete:  trying to delete an empty item :|");
			}else{
				setNewItem(false);
				imageResource = getImageResourceById(ids.get(ids.size()-1));
				updateSlider(ids, false);
				updateSliderPositionById(imageResource.getId());//no state change
				sliderAllComponents(imageResource.getId());//so force a refresh
				setNewButtonEnablement(true);
			}
		}else{
			System.out.println("delete: deleing an existing item");
			
//			String usageWarning = null;
			String usageWarning = getAnyUsageWarning();
			
			if(usageWarning==null){
				removeImageResourceAndGetNext(imageResource);
				List<Integer> ids = getAllImageIds();
				
				if(!ids.isEmpty()){
					
					updateSlider(ids, false);
					updateSliderPositionById(imageResource.getId());
				}else{
					
					setNewItem(true);
					imageResource = new ImageResource();
					int id = Resource.getNextImageResourceId();
					imageResource.setId(id);
					clearFields(false);
					ids.add(id);
					updateSlider(ids, true);
					enableButton = false;
				}
			}else{
				displayWarning(usageWarning);
			}
			
		}
		
		return enableButton;
	}
	
	protected String getAnyUsageWarning(){
		
		String usageWarning = null;
		
		
		
		return usageWarning;
	}

	@Override
	protected void refreshImages(){

		Coords span = getSpan();
		
		if(span!=null){
			
//			ImageHelper.displayStaticImage(staticImageLabel, staticButton, span);
			ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(null), getImageTotal(null), 1, 0, "");
		}
	}

	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		for (String label : childrenButtons.keySet()) {
			
			JButton jButton = childrenButtons.get(label);
			
			boolean childDirty = isChildGuiXmlWritePending(jButton);
			
			if(childDirty) setDirtyChildren(true);
			
			updateButtonLabelWithState(jButton, label, childDirty);
		}
		
		boolean childDirty = isChildGuiXmlWritePending(walkOverBtn);
		
		if(childDirty) setDirtyChildren(true);
		
		updateButtonLabelWithState(walkOverBtn, walkoverStr, childDirty);
		
	}
	
	protected void initLightFields(){

		nameTxt.setText(imageResource.getNameFromDir());
		nameTxt.setCaretPosition(0);
		
		
		setFormReady(false);
		spanX.setValue(imageResource.getSpan().x());
		spanY.setValue(imageResource.getSpan().y());
		setFormReady(true);

	}
	
	private Coords getSpan(){
		
		Coords span = ImageHelper.getSpanFromTextboxes(spanX, spanY);
		
		if(span == null && imageResource.getSpan()!=null) span = imageResource.getSpan();
		
		return span;
	}
	
	protected void clearFields(boolean imagesOnly){
		
		if(!imagesOnly){
			nameTxt.setText("");
		}
		setFormReady(false);
		spanX.setValue(0);
		spanY.setValue(0);
		setFormReady(true);
//		ImageHelper.deleteTempFolder(); <<<< should this be there?
		ArrayList<String> imageOrder = new ArrayList<String>();
		addImageOrder(null, imageOrder);
		addImageTotal(null, 0);
		ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), getSpan(), imagePanel, imageOrder, 0, 1, 0, "");
	}
	
	public void updatedImageTotal(int total){
		addImageTotal(null, total);
	}
	
	protected String checkIfItemChanges(int id){return "";};
	
	protected abstract String getOnMapWarning();
	
	protected abstract List<Integer> getAllImageIds();
	
	protected abstract List<ImageResource> getImageResourceList();
	
	protected abstract ImageResource getFirstImageResource(); 
	
	protected abstract ImageResource getImageResourceById(int id);
	
	protected abstract int getNumberOfImageResources();
	
	protected abstract void removeImageResourceAndGetNext(ImageResource imageResource);

}
