package gui.building;

import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import data.LevelDataIO;
import data.map.resources.BuildingActionResource;
import data.map.resources.BuildingResource;
import data.map.resources.ImageResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.PropertyKeys;
import gui.ValidationHelper;

public abstract class BaseBuildingGui extends ChildBaseGui {
	
	protected BuildingResource buildingResource;
	
	private JPopupMenu menu;
	private List<String> selectedPopupItems = new ArrayList<String>();
	private List<Integer> selectedPopupIds = new ArrayList<Integer>();
	private boolean menuShowing;
	private boolean menuVisable;
	private String allowedOnNoneText = "Nothing Selected";
	private JButton allowedOnButton, buildingTextButton;
	private JSpinner workerSpinner;
	
	private JTextField nameTxt;
	
	private JComboBox<String> imageCombo;
	private int[] imageIds;
	private int imageIndex;
	
	private String onMapWarning;

	public BaseBuildingGui(String title, JFrame parent) {
		super(title, parent);
		setCanWriteXml(true);
	}
	
	@Override
	protected int addComponents(JFrame frame) {

	    JPanel panel = new JPanel();
	    
	    nameTxt = addLabelAndTextFieldToPanelWithListener(panel, "Name", 0, 0, 10, true, false);
	    
      	buildingTextButton = new JButton("Localized Texts");
      	addGuiButtonAndListener(new BuildingTextsGui("Building text", frame), buildingTextButton);
    	panel.add(buildingTextButton);
    	
    	topPanel(panel);
    	
    	frame.add(panel, getRightPaddedGridBagConstraints(0, 0));
    	
    	panel = new JPanel();

       	imageCombo = addLabelAndComboToPanel(panel, "Image", 0, 0, getImageNameAndSortIds());
      	
       	workerSpinner = addLabelAndNumberSpinnerToPanel(panel, "Workers", 0, 0, 99, 1);
       	workerSpinner.setValue(1);
       	
       	workerSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady())  setDirtyStateAndConfigure(true);
			}
		});
      	
      	frame.add(panel, getRightPaddedGridBagConstraints(0, 1));
      	
      	panel = new JPanel();
      	
		menu = new JPopupMenu();
		menu.addPopupMenuListener(new PopupMenuListener() {			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {menuVisable = true;}			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {menuVisable = false;}			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		
		initMenu();
		
      	allowedOnButton = new JButton(allowedOnNoneText);
      	allowedOnButton.addActionListener(this);
      	allowedOnButton.addFocusListener(this);
      	
      	panel.add(new JLabel("Allowed On"));
      	
      	panel.add(allowedOnButton);
      	
      	frame.add(panel, getRightPaddedGridBagConstraints(0, 2));
		
		return 3;
	}
	
	private boolean isImageActionInUse(){
		
		if(buildingResource.getBuildingActionList()!=null && !buildingResource.getBuildingActionList().isEmpty()){
			
			for (BuildingActionResource buildingAction : buildingResource.getBuildingActionList()) {
				
				if(buildingAction.getBusyAction() > -1 || buildingAction.getIssueIdle() > -1){
					
					displayWarning("Image's Action are currently being referenced in Action: "+buildingAction.getTitle());
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected void initMenu(){
		
		setFormReady(false);
		
		menu.removeAll();
		selectedPopupItems = new ArrayList<String>();
		selectedPopupIds = new ArrayList<Integer>();
		
		createMenuPopupItem("Grass (default)", -1, menu);
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		
		setupPopupGridLayout(menu, mapItems.size()+1);
		
		for (MapItemResource mapItemResource : mapItems) {
			
			createMenuPopupItem(mapItemResource.getName(), mapItemResource.getId(), menu);
		}
		
		setFormReady(true);
		
	}
	
	protected void createMenuPopupItem(final String name, final int id, JPopupMenu menu){
		
//		final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);
//		menuItem.setUI(new StayOpenCheckBoxMenuItemUI());//fails on a mac (all checked)
		final JCheckBox menuItem = new JCheckBox(name);

		menuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			
				if(menuItem.isSelected()){
					
					selectedPopupItems.add(name);
					selectedPopupIds.add(id);
				}else{
					
					selectedPopupItems.remove(name);
					selectedPopupIds.remove((Integer)id);
				}
				updateButtonText();
			}
		});
		
		menu.add(menuItem);
	}
	
	protected void updateButtonText(){
		
		if(isFormReady()) setDirtyStateAndConfigure(true);
		
		StringBuilder sb = new StringBuilder();
		for (String name : selectedPopupItems) {
			if(sb.length() > 0) sb.append(", ");
			if(sb.length() > 25){
				sb.append(" ...");
				break;
			}
			sb.append(name);

		}
		if(sb.length()==0) sb.append(allowedOnNoneText);
		allowedOnButton.setText(sb.toString());
	}
	
	protected String[] getImageNameAndSortIds(){
		
		List<ImageResource> buildingImageResource = Resource.getBuildingImageResourceList();
		imageIds = new int[buildingImageResource.size()];
		String[] names = new String[buildingImageResource.size()];
		
		int i = 0;
		for (ImageResource imageResource : buildingImageResource) {
			
			imageIds[i] = imageResource.getId();
			names[i] = imageResource.getNameFromDir();
			i++;
		}
		
		return names;
	}
	
	protected int getImageComboIndexById(int id){
		
		for (int i = 0; i < imageIds.length; i++) {
			
			if(imageIds[i] == id) return i;
		}
		
		return -1;//should be unreachable
	}
	
	@Override
	protected void sliderUpdateLightComponents(int pos) {

		super.sliderUpdateLightComponents(pos);
		
		buildingResource = Resource.getBuildingResourceById(pos);
		
		onMapWarning = null;
		
		initLightFields();		
	}
	
	@Override
	protected void sliderAllComponents(int pos){
		super.sliderAllComponents(pos);
		
    	imageCombo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(isFormReady()){
					
					getOnMapWarning();
				
					if(isImageActionInUse() || onMapWarning!=""){
					
						setFormReady(false);
						imageCombo.setSelectedIndex(imageIndex);
						setFormReady(true);
						
						if(onMapWarning!="") displayWarning(onMapWarning);
						
					}else if(isFormReady()){
						setDirtyStateAndConfigure(true);
						imageIndex = imageCombo.getSelectedIndex();
					}
				}
			}
		});
    	configureMenuPopupItem(buildingResource.getAllowedon());
	}
	
	
	protected void initLightFields(){
		
		setFormReady(false);
		
		nameTxt.setText(buildingResource.getName());
		nameTxt.setCaretPosition(0);
		imageCombo.setSelectedIndex(getImageComboIndexById(buildingResource.getImageResourceId()));
		workerSpinner.setValue(buildingResource.getWorkers());
		
		imageIndex = imageCombo.getSelectedIndex();

		setFormReady(true);
	}
	
	protected void clearFields(){
		
		setFormReady(false);
		
		nameTxt.setText("");
		imageCombo.setSelectedIndex(0);
		workerSpinner.setValue(1);
		selectedPopupItems = new ArrayList<String>();
		selectedPopupIds = new ArrayList<Integer>();
		configureMenuPopupItem(selectedPopupIds);
		setFormReady(true);
	}
	
	protected void configureMenuPopupItem(List<Integer> selectedIds){
		
		getOnMapWarning();

		setFormReady(false);
		JCheckBox checkBox = (JCheckBox)menu.getComponent(0);
		checkBox.setSelected(selectedIds.contains(-1));
		checkBox.setEnabled(onMapWarning=="");
		int i = 1;
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		for (MapItemResource mapItemResource : mapItems) {
			
			checkBox = (JCheckBox)menu.getComponent(i);
			checkBox.setSelected(selectedIds.contains(mapItemResource.getId()));
			checkBox.setEnabled(onMapWarning=="");
			i++;
		}
		setFormReady(true);
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button==allowedOnButton){
			
			if(!menuShowing) menu.show(allowedOnButton, 0, allowedOnButton.getHeight());

	    	menuShowing = menuVisable;

		}
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(isNewItem()){
			clearFields();
		}else{

			initLightFields();
			configureMenuPopupItem(buildingResource.getAllowedon());
		}
	}
	
	@Override
	protected void saveData() {

		ValidationHelper validationHelper = new ValidationHelper();		
		validationHelper.validateString("Name", nameTxt.getText());
		buildingResource.setName(validationHelper.getStringResult());	
		buildingResource.setImageResourceId(imageIds[imageCombo.getSelectedIndex()]);	
		buildingResource.setWorkers((int)workerSpinner.getValue());		
		buildingResource.setAllowedon(selectedPopupIds);
		buildingResource.setWarehouse(isWarehouseGui());
		
		if(isNewItem()) Resource.addBuildingResource(buildingResource);
		setFormReady(false);//turn off the textbox listener
		initMenu();
		initLightFields();
		configureMenuPopupItem(buildingResource.getAllowedon());
		updateSlider(Resource.getFilteredBuildingResourceIds(isWarehouseGui()), false);
		setFormReady(true);//turn them back on
		postSaveData();

	}

	@Override
	protected void newButtonClicked() {
		
		setNewItem(true);
		buildingResource = new BuildingResource();
		int id = Resource.getNextBuildingResourceId();
		buildingResource.setId(id);
		List<Integer> ids = Resource.getFilteredBuildingResourceIds(isWarehouseGui());
		ids.add(id);
		updateSlider(ids, true);
		clearFields();
		onMapWarning = "";
		initMenu();
		
	}

	@Override
	protected boolean deleteActions() {
		
		boolean enableButton = true;
		
		if(isNewItem()){
			
			List<Integer> ids = Resource.getFilteredBuildingResourceIds(isWarehouseGui());
			if(ids.isEmpty()){
				System.out.println("delete:  trying to delete an empty item :|");
			}else{
				setNewItem(false);
				buildingResource = Resource.getBuildingResourceById(ids.get(ids.size()-1));
				updateSlider(ids, false);
				updateSliderPositionById(buildingResource.getId());//no state change
				sliderAllComponents(buildingResource.getId());//so force a refresh
				setNewButtonEnablement(true);
			}
		}else{
			System.out.println("delete: deleing an existing item");

			//rethink this, if get is null, then do new item
			Resource.removeBuildingResource(buildingResource);
			
			List<Integer> ids = Resource.getFilteredBuildingResourceIds(isWarehouseGui());
			
			if(!ids.isEmpty()){
				
				initMenu();
				updateSlider(ids, false);
				updateSliderPositionById(buildingResource.getId());
			}else{
				
				setNewItem(true);
				buildingResource = new BuildingResource();
				int id = Resource.getNextBuildingResourceId();
				buildingResource.setId(id);
				clearFields();
				ids.add(id);
				updateSlider(ids, true);
				enableButton = false;
			}
			
		}
		
		return enableButton;
	}
	
	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		ValidationHelper validationHelper = new ValidationHelper();
		
		if(!validationHelper.validateString("Name", nameTxt.getText()))
			return validationHelper.getWarning();
		
		if((buildingResource.getInfoResource()==null || buildingResource.getInfoResource().getTextMap().isEmpty()))
			return "at least one localized text must be set";

		if(selectedPopupIds.isEmpty()) return "no 'Allowed On' items have been selected";
		
		return null;
	}

	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		boolean isNew = true;
		
		if(Resource.getBuildingResourceList() != null || !Resource.getBuildingResourceList().isEmpty()){
			
			for (BuildingResource building : Resource.getBuildingResourceList()) {
				
				if(building.isWarehouse() == isWarehouseGui()){
					isNew = false;
					break;
				}
			}
		}
		
		if(isNew){
			buildingResource = new BuildingResource();
			buildingResource.setId(Resource.getNextBuildingResourceId());
		}

		setNewItem(isNew);

		

	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		addToPassedProperties(PropertyKeys.BUILDING_RESOURCE, buildingResource);
		
		ValidationHelper validationHelper = new ValidationHelper();

		if(validationHelper.validateString("Name", nameTxt.getText())){
			
			addToPassedProperties(PropertyKeys.BUILDING_RESOURCE_NAME, validationHelper.getStringResult());
		}else{
			
			return validationHelper.getWarning();
		}
		
		if(childName.equals(BuildingActionGui.class.getName())){
			
			ImageResource imageResource = Resource.getBuildingImageResourceById(imageIds[imageCombo.getSelectedIndex()]);
			
			addToPassedProperties(PropertyKeys.IMAGE_RESOURCE, imageResource);
		}
		
		if(childName.equals(WarehouseStoredItemGui.class.getName())){
			
			addToPassedProperties(PropertyKeys.BUILDING_ONMAPWARNING, getOnMapWarning());
		}
		
		return null;
	}
	
	@Override
	public void focusLost(FocusEvent e) {

		super.focusLost(e);
		//solves tabbing and clicking other widgets, but not clicking form itself
		if(e.getComponent()==allowedOnButton) menuShowing = menuVisable;
	}
	
	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(buildingTextButton);
		if(childDirty) setDirtyChildren(true);	
		updateButtonLabelWithState(buildingTextButton, "Localized Texts", childDirty);
		
	}
	
	@Override
	protected String preDeleteChecks(){
		
		if(getOnMapWarning()!="") return onMapWarning;
		
		return null;
	}
	
	protected void topPanel(JPanel panel){}
	

	protected boolean isWarehouseGui(){
		
		return this instanceof WarehouseGui;
	}
	
	private String getOnMapWarning(){
		
		if(onMapWarning==null){
			
			onMapWarning = LevelDataIO.checkBuildingResourceUsedInMap(buildingResource.getId());
			if(onMapWarning == null) onMapWarning = "";//stop re-check
		}
		
		return onMapWarning;
	}

}
