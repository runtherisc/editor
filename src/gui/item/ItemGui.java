package gui.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.map.resources.BuildingActionProduceResource;
import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingActionResource;
import data.map.resources.BuildingCreationResource;
import data.map.resources.BuildingItemResource;
import data.map.resources.BuildingResource;
import data.map.resources.Coords;
import data.map.resources.CreationItemResource;
import data.map.resources.DestructionItemResource;
import data.map.resources.ImageResource;
import data.map.resources.ItemMakeResource;
import data.map.resources.ItemResource;
import data.map.resources.LifecycleItemResource;
import data.map.resources.MakeRequireResource;
import data.map.resources.MapItemAttResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import data.map.resources.WorkerActionResource;
import data.map.resources.WorkerImageResource;
import game.ImageHelper;
import gui.ChildBaseGui;
import gui.ImageSliderHook;
import gui.PropertyKeys;
import gui.ValidationHelper;

public class ItemGui extends ChildBaseGui implements ImageSliderHook{
	
	private JButton clearAll, localizedTextButton;
	
	private ItemResource itemResource;
	
	private JTextField nameTxt;
	private JSpinner spanX;
	private JSpinner spanY;
	
	private List<JButton> imageButtons;
	private JPanel imagePanel;
	
	private String[] filenames = new String[]{"10", "11", "20", "21", "30", "31", "40", "41"};
	
	private String[] defaultWorkerTitles = new String[]{
			"Up 1",
			"Up 2",
			"Right 1",
			"Right 2",
			"Down 1",
			"Down 2",
			"Left 1",
			"Left 2"
		};

	public ItemGui(String title, JFrame parent){
		
		super(title, parent);
		setupSlider(Resource.getAllItemResourceIds());
		setCanWriteXml(true);
		
	}

	@Override
	public int addComponents(JFrame frame) {
    
      	JPanel panel = new JPanel();
      	
      	nameTxt = addLabelAndTextFieldToPanelWithListener(panel, "Name", 0, 0, 10, true, false);
//      	nameTxt.setHorizontalAlignment(JLabel.LEFT);
      	
      	localizedTextButton = new JButton("Localized Texts");
      	addGuiButtonAndListener(new ItemTextsGui("Item text", frame), localizedTextButton);
    	panel.add(localizedTextButton, getRightPaddedGridBagConstraints(1, 0));

        frame.add(panel, getRightPaddedGridBagConstraints(0, 0));

        imageButtons = new ArrayList<JButton>();
		frame.add(imagePanel = getMultiImageSelection(defaultWorkerTitles, imageButtons, false), getRightPaddedGridBagConstraints(0, 1));

		JPanel panel2 = new JPanel();
      	
		spanX = addLabelAndNumberSpinnerToPanel(panel2, "Span X", 0, 0, 9, 0);
       	
		spanX.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady())  setDirtyStateAndConfigure(true);
			}
		});
		
		spanY = addLabelAndNumberSpinnerToPanel(panel2, "Span Y", 0, 0, 9, 0);
       	
		spanY.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady())  setDirtyStateAndConfigure(true);
			}
		});
		
      	clearAll = new JButton("Clear All Images");
      	clearAll.addActionListener(this);
      	panel2.add(clearAll, getRightPaddedGridBagConstraints(4, 0));

		frame.add(panel2, getRightPaddedGridBagConstraints(0, 3));

		return 4;
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == clearAll && getImageTotal(null) > 0 &&
				(int)spanX.getValue() > 0 && (int)spanY.getValue() > 0){

			setPendingImageSaveAndConfigure(itemResource.getResource()!=null);
			setDirtyStateAndConfigure(isDirtyAfterImageClear());
			
//			itemResource.setResource(null);		
			setFormReady(false);
			clearFields(true);
			setFormReady(true);
			
		}else{
			
			int buttonPos = imageButtons.indexOf(button);
			Coords span = getSpan();
			if(buttonPos > -1){
				
				if(span !=null){

					if(ImageHelper.imageButtonClicked(buttonPos, 0, getImageOrder(null), getImageTotal(null), span, imagePanel, filenames, filenames.length, "", this, frame)){
						
						setPendingImageSaveAndConfigure(true);
					}

					if(getImageTotal(null)==0) setPendingImageSave(itemResource.getResource()!=null);

				}else{
					
					displayWarning("Span X and Span Y must be set before images can be added");
				}
			}
		}
		
	}
	
	private boolean isDirtyAfterImageClear(){
		
		return itemResource.getResource()!=null ||
				((itemResource.getName()==null && nameTxt.getText().length() > 0) || 
				(itemResource.getName()!=null && !itemResource.getName().equals(nameTxt.getText())));
	}
	
	//nothing to receive, but we can still configure on entry
	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		if(Resource.getItemResourceList() == null || Resource.getItemResourceList().isEmpty()){
			setNewItem(true);
			itemResource = new ItemResource();
			itemResource.setId(Resource.getNextItemResourceId());

		}else{
			setNewItem(false);
			itemResource = Resource.getItemResource(0);
		}
		
	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		System.out.println("childName" + childName);
		
		addToPassedProperties(PropertyKeys.ITEM_RESOURCE, itemResource);
		
		if(childName.equals(ItemTextsGui.class.getName())){
			
			ValidationHelper validationHelper = new ValidationHelper();

			if(validationHelper.validateString("Name", nameTxt.getText())){
				
				addToPassedProperties(PropertyKeys.ITEM_RESOURCE_NAME, validationHelper.getStringResult());
			}else{
				
				return validationHelper.getWarning();
			}

		}
		
		return null;
	}

	@Override
	protected void sliderUpdateLightComponents(int pos) {

		super.sliderUpdateLightComponents(pos);
		
		itemResource = Resource.getItemResourceById(pos);
		
		System.out.println("update light slider");
		
		initLightFields();
	}


	@Override
	protected void sliderAllComponents(int pos) {
		
		System.out.println("update heavy slider");

		super.sliderAllComponents(pos);
		
		System.out.println("pending image save "+isPendingImageSave());
		
		//heavy slider only call on slider selection, should be save to setup here?
		if(!isPendingImageSave()){
			
			initImages();
		}
		
		ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), getSpan(), imagePanel, getImageOrder(null), getImageTotal(null), 8, 0, "");
		
		initLightFields();
	}
	
	private void initImages(){
		
		ArrayList<String>  imageOrder = new ArrayList<String>();
		addImageOrder(null, imageOrder);
		addImageTotal(null, 0);
		
		if(itemResource.getResource()!=null){
		
			ImageHelper.copyPngFromReourceToTemp(filenames, filenames.length, "0" , itemResource.getResource().getResource(), Resource.getWorkerPath(), "", frame);
		
			for (int i = 0; i < 8; i++) {
				imageOrder.add(filenames[i]);
			}
			addImageTotal(null, imageOrder.size());
		}else{
			ImageHelper.deleteTempFolder();
		}
	}

	
	private Coords getSpan(){
		
		Coords span = ImageHelper.getSpanFromTextboxes(spanX, spanY);
		
		if(span == null && itemResource.getResource()!=null) span = itemResource.getResource().getSpan();
		
		return span;

	}
	
	
	protected void initLightFields(){
		
		nameTxt.setText(itemResource.getName());
      	nameTxt.setCaretPosition(0);
		
		boolean clearSpan = true;
		
		setFormReady(false);
		
		if(itemResource.getResource()!=null){
			
			Coords span = itemResource.getResource().getSpan();
			
			if(span!=null){
				
				spanX.setValue(span.x());
				spanY.setValue(span.y());
				clearSpan = false;
			}
		}
		
		if(clearSpan){
			
			spanX.setValue(0);
			spanY.setValue(0);
		}
		
		setFormReady(true);
	}


	
	@Override
	protected void postDrawGui() {
		super.postDrawGui();
		
		if(isNewItem()){
			clearFields(false);
			setFormReady(true);
		//sliderAllComponents isn't called when there is only one item
		}else if(Resource.getNumberOfItemRes()==1){
			sliderAllComponents(itemResource.getId());
		}
	}


	@Override
	protected void saveData() {
		
		//mandatory
		itemResource.setName(nameTxt.getText().trim());

			
		//optional
		int spanXvalue = (int) spanX.getValue();
		int spanYvalue = (int) spanY.getValue();
	
		if(spanXvalue > 0 && spanYvalue > 0 && getImageTotal(null)==8){
		
			WorkerImageResource workerImageResource = new WorkerImageResource();
			workerImageResource.setSpan(new Coords(spanXvalue, spanYvalue));

			int folder;
			if(itemResource.getResource()!=null) folder = itemResource.getResource().getResource();
			else folder = Resource.getNextWorkerResourceFolderNumber();
			workerImageResource.setResource(folder);
			
			if(isPendingImageSave()){

				ImageHelper.copyPngFromTempToResource("0", folder, Resource.getWorkerPath(), filenames, filenames.length, getImageOrder(null), "", frame);
				
//				ImageHelper.deleteTempFolder();
				
				setPendingImageSaveAndConfigure(false);
			
			}
		
			//workerImageResource.setResource(resource);//set image
			itemResource.setResource(workerImageResource);
			
		}else if(spanXvalue==0 && spanYvalue==0 && getImageTotal(null)==0){
			
			if(itemResource.getResource()!=null){
				
				ImageHelper.deleteResourceFolder("0", itemResource.getResource().getResource(), Resource.getWorkerPath(), "");
			}
			
			itemResource.setResource(null);
		}

		if(isNewItem()) Resource.addItemResource(itemResource);
		setFormReady(false);//turn off the textbox listener
		initLightFields();
		updateSlider(Resource.getAllItemResourceIds(), false);
		setFormReady(true);//turn them back on
		postSaveData();
		
		
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		ValidationHelper validationHelper = new ValidationHelper();
		
		if(!validationHelper.validateString("Name", nameTxt.getText()))
			return validationHelper.getWarning();

		int spanXvalue = (int) spanX.getValue();
		int spanYvalue = (int) spanY.getValue();
		
		if((spanXvalue > 0 && spanYvalue == 0) || (spanXvalue == 0 && spanYvalue > 0))
			return "Both Span X and Span Y must be set (or neither)";
		
		int totalImages =  getImageTotal(null);
		
		if(spanXvalue == 0 && spanYvalue == 0 && totalImages != 0) 
			return "Span X and Span y must be set with images";
			
		if(spanXvalue > 0 && spanYvalue > 0 && totalImages != 8) 
			return "Images must be added when Span X and Span Y are set";

		if(totalImages > 0 && totalImages < 8)
			return "You must have either all 8 images selected, or no images selected";

		if(itemResource.getInfoResource().getLocaleKeys().isEmpty() && totalImages==0)
			return "Either Localized Text, images or both must be set";	

		
		return null;
	}
	
	protected void clearFields(boolean imagesOnly){
		
		if(!imagesOnly){
			nameTxt.setText("");
		}
		setFormReady(false);
		spanX.setValue(0);
		spanY.setValue(0);
		setFormReady(true);
		ImageHelper.deleteTempFolder();
		ArrayList<String> imageOrder = new ArrayList<String>();
		addImageOrder(null, imageOrder);
		addImageTotal(null, 0);
		ImageHelper.displayImagesAndButtons(null, getSpan(), imagePanel, getImageOrder(null), getImageTotal(null), 8, 0, "");
	}

	@Override
	protected void newButtonClicked() {

		setNewItem(true);
		itemResource = new ItemResource();
		int id = Resource.getNextItemResourceId();
		itemResource.setId(id);
		List<Integer> ids = Resource.getAllItemResourceIds();
		ids.add(id);
		updateSlider(ids, true);
		clearFields(false);
//		addToPassedProperties(PropertyKeys.ITEM_RESOURCE, itemResource);

	}

	@Override
	protected void revertUnsavedChanges(int pos) {	
		
		System.out.println("reverting changes");
		if(isNewItem()){
			clearFields(false);
		}else{
			if(isPendingImageSave()){
				initImages();
				ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), getSpan(), imagePanel, getImageOrder(null), getImageTotal(null), 8, 0, "");
			}
			initLightFields();
		}
		
	}

	@Override
	protected boolean deleteActions() {
		
		boolean enableButton = true;
		
		if(isNewItem()){
			
			List<Integer> ids = Resource.getAllItemResourceIds();
			if(ids.isEmpty()){
				System.out.println("delete:  trying to delete an empty item :|");
			}else{
				setNewItem(false);
				itemResource = Resource.getItemResourceById(ids.get(ids.size()-1));
				updateSlider(ids, false);
				updateSliderPositionById(itemResource.getId());//no state change
				sliderAllComponents(itemResource.getId());//so force a refresh
				setNewButtonEnablement(true);
			}
		}else{
			System.out.println("delete: deleing an existing item");

			Resource.removeItemResource(itemResource);
			
			List<Integer> ids = Resource.getAllItemResourceIds();
			
			if(!ids.isEmpty()){
				
				updateSlider(ids, false);
				updateSliderPositionById(itemResource.getId());
			}else{
				
				setNewItem(true);
				itemResource = new ItemResource();
				int id = Resource.getNextItemResourceId();
				itemResource.setId(id);
				clearFields(false);
				ids.add(id);
				updateSlider(ids, true);
				enableButton = false;
			}
			
		}
		
//		addToPassedProperties(PropertyKeys.ITEM_RESOURCE, itemResource);
		
		return enableButton;
		
	}

	@Override
	protected String preDeleteChecks(){
		
		int id = itemResource.getId();
		
		//image
		List<ImageResource> allImageResources = Resource.getBuildingImageResourceList();	
		
		if(allImageResources!=null && !allImageResources.isEmpty()){
			
			for (ImageResource imageResource : allImageResources) {
				
				System.out.println("checking imageResource "+imageResource.getNameFromDir());
			
				if(imageResource.getBuildingCreationList()!=null){
					
					System.out.println("imageResource.getBuildingCreationList() size "+imageResource.getBuildingCreationList().size());
					
					int row = 1;
					
					for (BuildingCreationResource resource : imageResource.getBuildingCreationList()) {
						
						System.out.println("looking at row "+row);

						for (LifecycleItemResource lifecycleItemResource : resource.getLifecycleItems()) {
							if(lifecycleItemResource.getId()==id && lifecycleItemResource instanceof CreationItemResource) 
								return "item used on Building Image '"+imageResource.getNameFromDir()+" -> Creation Item Requirement' from row "+row;
							if(lifecycleItemResource.getId()==id && lifecycleItemResource instanceof DestructionItemResource) 
								return "item used on Building Image '"+imageResource.getNameFromDir()+" -> Destruction Item Requirement' from row "+ row;
						}
						
						row ++;
					}
				}
			}
		}
		
		//map attributes
		List<MapItemResource> allMapitemsResources = Resource.getMapItemResourceList();
		
		if(allMapitemsResources!=null && !allMapitemsResources.isEmpty()){
			
			for (MapItemResource mapItemResource : allMapitemsResources) {
				
				if(mapItemResource.getMapItemAttList() != null){
					
					for(MapItemAttResource mapItemAttResource : mapItemResource.getMapItemAttList()){
						
						if(mapItemAttResource.getId() == id){
							
							return "item used in the map item attributes of " + mapItemResource.getName();
						}
					}
				}
			}
		}
		
		//all buildings
		List<BuildingResource> allBuildingResources = Resource.getBuildingResourceList();
		if(allBuildingResources!=null && !allBuildingResources.isEmpty()){
			for (BuildingResource buildingResource : allBuildingResources) {
				
				//warehouse
				if(buildingResource.isWarehouse()){
					
					Collection<BuildingItemResource> buildingItemList = buildingResource.getBuildingItemMap().values();
					if(buildingItemList!=null && !buildingItemList.isEmpty()){
						for(BuildingItemResource buildingItem : buildingItemList){
							if(buildingItem.getId() == id){
								return "item/worker used in warehouse '"+buildingResource.getName()+"' as a stored item";
							}
							WorkerActionResource workerAction = buildingItem.getFirstWorkerActionResource();
							if(workerAction!=null){
								if(workerAction.getWorkerin()==id) return "worker used in warehouse '"+buildingResource.getName()+"' as a worker in on item '"+Resource.getItemInternalNameById(buildingItem.getId())+"'";
								if(workerAction.getWorkerout()==id) return "worker used in warehouse '"+buildingResource.getName()+"' as a worker out on item '"+Resource.getItemInternalNameById(buildingItem.getId())+"'";
							}
							List<ItemMakeResource> makeList = buildingItem.getWarehouseMake();
							for (ItemMakeResource itemMakeResource : makeList) {
								List<MakeRequireResource> makeRequirementList = itemMakeResource.getMakeRequirements();
								for (MakeRequireResource makeRequireResource : makeRequirementList) {
									if(makeRequireResource.getId() == id){
										return "item/worker used in warehouse '"+buildingResource.getName()+"' stored item: '"
												+Resource.getItemInternalNameById(buildingItem.getId())+ "' make requirements";
									}
								}
							}
						}
					}
					
				//building	
				}else{
					
					List<BuildingActionResource> actionList = buildingResource.getBuildingActionList();
					if(actionList!=null && !actionList.isEmpty()){
						for (BuildingActionResource buildingActionResource : actionList) {
							
							List<BuildingActionProduceResource> produces = buildingActionResource.getProduces();
							if(produces!=null && !produces.isEmpty()){
								for (BuildingActionProduceResource buildingActionProduceResource : produces) {
									if(buildingActionProduceResource.getItem() == id){
										return "item used in building '"+buildingResource.getName()+"' action: '"+buildingActionResource.getTitle()+"' with produced";
									}
									WorkerActionResource workerAction = buildingActionProduceResource.getFirstWorkerAction();
									if(workerAction!=null){
										if(workerAction.getWorkerin()==id) return "worker used in building '"+buildingResource.getName()+"' as a worker in on action: '"+buildingActionResource.getTitle()+"' with produced";
										if(workerAction.getWorkerout()==id) return "worker used in building '"+buildingResource.getName()+"' as a worker out on action: '"+buildingActionResource.getTitle()+"' with produced";
									}
								}
								
							}
							List<BuildingActionRequireResource> requires = buildingActionResource.getRequirements();
							if(requires!=null && !requires.isEmpty()){
								for (BuildingActionRequireResource buildingActionRequireResource : requires) {
									if(buildingActionRequireResource.getItem() == id){
										return "item used in building '"+buildingResource.getName()+"' action: '"+buildingActionResource.getTitle()+"' with required";
									}
									WorkerActionResource workerAction = buildingActionRequireResource.getFirstWorkerAction();
									if(workerAction!=null){
										if(workerAction.getWorkerin()==id) return "worker used in building '"+buildingResource.getName()+"' as a worker in on action: '"+buildingActionResource.getTitle()+"' with required";
										if(workerAction.getWorkerout()==id) return "worker used in building '"+buildingResource.getName()+"' as a worker out on action: '"+buildingActionResource.getTitle()+"' with required";
									}
								}
								
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	protected void refreshImages(){
		
//		if(!isPendingImageSave() && itemResource.getResource()!=null){
//			
//			ImageHelper.copyPngFromReourceToTemp(filenames, filenames.length, "0" , itemResource.getResource().getResource(), workerPath);
//
//		}
		
		System.out.println("refreshImages() called");
		
		Coords span = getSpan();
		
		if(span!=null){
			
			System.out.println("refreshing images "+span.x()+" : "+span.y());
			
			ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(null), getImageTotal(null), 8, 0, "");
		}
		
	}

	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(localizedTextButton);
		
		if(childDirty) setDirtyChildren(true);
		
		updateButtonLabelWithState(localizedTextButton, "Localized Texts", childDirty);
		
	}
	
	@Override
	public void updatedImageTotal(int total) {
		addImageTotal(null, total);
		
	}

	@Override
	protected String getHelpText() {

		return new StringBuilder("There are 3 types of items that you can create, these are as follows:\n\n")
						 .append("1.Warehouse Item\n")
						 .append("This is an item that can be stored in a warehouse, used in building construction, required or produced by a building or in a map object that can then be collected by a building.\n")
						 .append("Example warehouse items are; wood, plank, bread, iron etc.\n")
						 .append("A warehouse item is defined by having localized description(s) but without any images set\n\n")
						 .append("2.Basic Worker\n")
						 .append("A worker can appear in a warehouse and also walk to and from buildings\n")
						 .append("Example workers are builder, woodcutter, farmer etc.\n")
						 .append("A basic worker is defined by having localized descriptions(s) and images set\n\n")
						 .append("3.Busy Worker\n")
						 .append("A busy worker does not appear in a warehouse but does walk to and from buildings\n")
						 .append("Example busy workers are; woodcutter carrying wood, farmer carrying water, farmer carrying corn etc\n")
						 .append("Localized descriprition(s) are not set, but images are\n\n")
						 .append("Name Textbox (Mandatory)\n")
						 .append("The name field is only used in the editor to identify the item and is not used in the game.\n\n")
						 .append("Localized button (Optional)\n")
						 .append("The localized button open the item description gui and allows you to enter text that will be displayed to the user in the game\n\n")
						 .append("Span X and Span Y (Optional)\n")
						 .append("If you are supplying images, you must set the size the images will be displayed in the game.\n")
						 .append("On a device that is a normal screensize, a span of 1 will be equal to 16 pixels.  This will be resized based on screensize.\n")
						 .append("The maximum allowed span is 9, this would typically be used for large map object or buildings.  An ideal span for a worker would be a Span X of 1 and a Span Y of 2.\n")
						 .append("Note that images will always be resized based on their Span X and Span Y, regardless of their original size\n\n")
						 .append("Images (Optional)\n")
						 .append("If the item is a worker, you can define the images to show the simple animation of them walking.  Each direction (up, down, left and right) has 2 frames of animation\n")
						 .append("Click the Add button and select up to 8 images, these images will be displayed corresponding to the label above the image\n")
						 .append("you must supply all 8 images (2 frames per direction)\n")
						 .append("When images have been added, you can swap them with the image to their right (by clicking Swap) in order to put them im the right posistions.\n")
						 .append("If an incorrect image has been added, it can be removed if it the last one on the right\n\n")
						 .append("Clear All Images\n")
						 .append("If you wish to quickly remove all the images that have been added, the Clear All Images button will remove any images that have been added.\n").toString();
	}


}
