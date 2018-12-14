package gui.mapobject;

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
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import data.LevelDataIO;
import data.map.resources.BuildingActionProduceResource;
import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingActionResource;
import data.map.resources.BuildingMapItemActionResource;
import data.map.resources.BuildingResource;
import data.map.resources.ImageResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.PropertyKeys;
import gui.ValidationHelper;

public class MapObjectGui extends ChildBaseGui{

	public MapObjectGui(String title, JFrame parent) {
		super(title, parent);
		setupSlider(Resource.getAllMapItemResourceIds());
		setCanWriteXml(true);
	}
	
	private MapItemResource mapItemResource;
	
	private JTextField nameTxt;
	private JButton localizedTextButton, allowedOnButton, actionsButton, attributesButton;
	
	private List<String> selectedPopupItems = new ArrayList<String>();
	private List<Integer> selectedPopupIds = new ArrayList<Integer>();
	boolean menuShowing;
	boolean menuVisable;//popup menu called popupMenuWillBecomeInvisible before button gets pressed, hence the double boolean
	private JPopupMenu menu;
	private String allowedOnNoneText = "Nothing Selected";
	
	private JComboBox<String> imageCombo;
	private int[] imageIds;
	
	private JCheckBox drawFlat;
	private JCheckBox showAtts;
	
	private int imageIndex;
	
	private String onMapWarning;
	
	@Override
	protected int addComponents(JFrame frame) {

		JPanel panel = new JPanel();
      	
      	nameTxt = addLabelAndTextFieldToPanelWithListener(panel, "Name", 0, 0, 10, true, false);
    	
    	imageCombo = addLabelAndComboToPanel(panel, "Image", 0, 0, getImageNameAndSortIds());

    	
    	drawFlat = new JCheckBox("Is Flat");
    	
    	drawFlat.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady())  setDirtyStateAndConfigure(true);
			}
		});
    	
    	panel.add(drawFlat);
    	
    	showAtts = new JCheckBox("Show Attributes");
    	
    	showAtts.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady())  setDirtyStateAndConfigure(true);
			}
		});
    	
    	panel.add(showAtts);
    	
        frame.add(panel, getRightPaddedGridBagConstraints(0, 0));
		
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
      	
        frame.add(panel, getRightPaddedGridBagConstraints(0, 1));
		
        panel = new JPanel();
        
      	localizedTextButton = new JButton("Localized Texts");
      	addGuiButtonAndListener(new MapObjectTextsGui("Map Item text", frame), localizedTextButton);
    	panel.add(localizedTextButton);
      	
		actionsButton = new JButton("Actions");
      	addGuiButtonAndListener(new ActionGui("Map Item Actions for ", frame), actionsButton);
    	panel.add(actionsButton);
    	
		attributesButton = new JButton("Attributes");
      	addGuiButtonAndListener(new AttributesGui("Map Item Attributes for ", frame), attributesButton);
    	panel.add(attributesButton);
		
		frame.add(panel, getAllPaddingGridBagConstraints(0, 2));
		
//		frame.add(new MultiSelectionComboBox().getContent(), getAllPaddingGridBagConstraints(0, 0));
		
		return 3;
	}
	
	private boolean isImageActionInUse(){
		
		if(mapItemResource.getMapItemActionList()!=null && !mapItemResource.getMapItemActionList().isEmpty()){
			
			for (MapItemActionResource mapItemActionResource : mapItemResource.getMapItemActionList()) {
				
				if(mapItemActionResource.getBusy() > -1){
					
					displayWarning("Image's Action are currently being referenced in Action: "+mapItemActionResource.getInternalName());
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected String[] getImageNameAndSortIds(){
		
		List<ImageResource> mapImageResource = Resource.getMapImageResourceList();
		imageIds = new int[mapImageResource.size()];
		String[] names = new String[mapImageResource.size()];
		
		int i = 0;
		for (ImageResource imageResource : mapImageResource) {
			
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
	
	protected void initMenu(){
		
		setFormReady(false);
		
		menu.removeAll();
		selectedPopupItems = new ArrayList<String>();
		selectedPopupIds = new ArrayList<Integer>();
		
		createMenuPopupItem("Grass (default)", -1, menu);
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
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
	
	protected void updateButtonText(){
		
		if(isFormReady()) setDirtyStateAndConfigure(true);
		
		StringBuilder sb = new StringBuilder();
		for (String name : selectedPopupItems) {
			if(sb.length() > 0) sb.append(", ");
			if(sb.length() > 35){
				sb.append(" ...");
				break;
			}
			sb.append(name);

		}
		if(sb.length()==0) sb.append(allowedOnNoneText);
		allowedOnButton.setText(sb.toString());
	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		addToPassedProperties(PropertyKeys.MAP_OBJECT_RESOURCE, mapItemResource);
		
		ValidationHelper validationHelper = new ValidationHelper();

		if(validationHelper.validateString("Name", nameTxt.getText())){
			
			addToPassedProperties(PropertyKeys.MAP_OBJECT_RESOURCE_NAME, validationHelper.getStringResult());
		}else{
			
			return validationHelper.getWarning();
		}
		
		if(childName.equals(ActionGui.class.getName())){
			
			ImageResource imageResource = Resource.getMapImageResourceById(imageIds[imageCombo.getSelectedIndex()]);
			
//			List<MultiImageResourceAction> busys = imageResource.getBusy();
//			
//			if(busys != null && !busys.isEmpty()){
			
			if(!selectedPopupIds.isEmpty()){
				
				addToPassedProperties(PropertyKeys.IMAGE_RESOURCE, imageResource);
				addToPassedProperties(PropertyKeys.MAP_OBJECT_RESOURCE_ALLOWEDON, selectedPopupIds);
				
			}else return "Allowed On list must be configured first";
				
//			}else return "Actions must be set on the image resource";
			
		}else if(childName.equals(AttributesGui.class.getName())){
			
			getOnMapWarning();
			if(onMapWarning!="") return onMapWarning;
			if(Resource.getWarehouseItemInternalNames().isEmpty()) return "Items (with localized descriptions) need to be defined first";
		}
		
		return null;
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		if(Resource.getMapItemResourceList() == null || Resource.getMapItemResourceList().isEmpty()){
			setNewItem(true);
			mapItemResource = new MapItemResource();
			mapItemResource.setId(Resource.getNextMapItemResourceId());

		}else{
			setNewItem(false);
			mapItemResource = Resource.getMapItemResource(0);
		}
		
	}

	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button==allowedOnButton && checkChangeToAnotherMapItem()){
			
			if(!menuShowing) menu.show(allowedOnButton, 0, allowedOnButton.getHeight());

	    	menuShowing = menuVisable;

		}
	}
	
	private boolean checkChangeToAnotherMapItem(){
		
		List<MapItemActionResource> actionList = mapItemResource.getMapItemActionList();
		if(actionList!=null && !actionList.isEmpty()){
			
			for (MapItemActionResource mapItemActionResource : actionList) {
				
				if(mapItemActionResource.getMapitem() != -1){
					
					displayWarning("action '"+mapItemActionResource.getInternalName()+"' changes this into '" + Resource.getMapItemResourceById(mapItemActionResource.getMapitem()).getName()+ "'");
					return false;
				}
			}
			
			if(!isNewItem()){
				
				List<MapItemResource> mapItems = Resource.getMapItemResourceList();
				for (MapItemResource mapItemResource : mapItems) {
					
					actionList = mapItemResource.getMapItemActionList();
					if(actionList!=null && !actionList.isEmpty()){
					
					for (MapItemActionResource mapItemActionResource : actionList) {
						
						if(mapItemActionResource.getMapitem() == this.mapItemResource.getId()){
							
							displayWarning("map item changes from '" + mapItemResource.getName()+ "' action '"+mapItemActionResource.getInternalName()+"'");
							return false;
						}
					}
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	protected void sliderUpdateLightComponents(int pos) {

		super.sliderUpdateLightComponents(pos);
		
		mapItemResource = Resource.getMapItemResourceById(pos);
		
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
    	
    	configureMenuPopupItem(mapItemResource.getAllowedon());
	}
	
	protected void initLightFields(){
		
		setFormReady(false);
		
		nameTxt.setText(mapItemResource.getName());
		nameTxt.setCaretPosition(0);
		imageCombo.setSelectedIndex(getImageComboIndexById(mapItemResource.getImageResourceId()));
		drawFlat.setSelected(mapItemResource.isDrawFirst());
		showAtts.setSelected(mapItemResource.isShowAttributes());
		imageIndex = imageCombo.getSelectedIndex();
		setFormReady(true);
	}
	
	protected void clearFields(){
		
		setFormReady(false);
		
		nameTxt.setText("");
		imageCombo.setSelectedIndex(0);
		drawFlat.setSelected(false);
		showAtts.setSelected(false);
		selectedPopupItems = new ArrayList<String>();
		selectedPopupIds = new ArrayList<Integer>();
		configureMenuPopupItem(selectedPopupIds);
		setFormReady(true);
	}

	@Override
	protected void newButtonClicked() {
		
		setNewItem(true);
		mapItemResource = new MapItemResource();
		int id = Resource.getNextMapItemResourceId();
		mapItemResource.setId(id);
		List<Integer> ids = Resource.getAllMapItemResourceIds();
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
			
			List<Integer> ids = Resource.getAllMapItemResourceIds();
			if(ids.isEmpty()){
				System.out.println("delete:  trying to delete an empty item :|");
			}else{
				setNewItem(false);
				mapItemResource = Resource.getMapItemResourceById(ids.get(ids.size()-1));
				updateSlider(ids, false);
				updateSliderPositionById(mapItemResource.getId());//no state change
				sliderAllComponents(mapItemResource.getId());//so force a refresh
				setNewButtonEnablement(true);
			}
		}else{
			System.out.println("delete: deleing an existing item");

//			mapItemResource = 
			Resource.removeMapItemResource(mapItemResource);
			List<Integer> ids = Resource.getAllMapItemResourceIds();
			
			if(!ids.isEmpty()){
				
				initMenu();
				updateSlider(ids, false);
				updateSliderPositionById(mapItemResource.getId());
			}else{
				
				setNewItem(true);
				mapItemResource = new MapItemResource();
				int id = Resource.getNextMapItemResourceId();
				mapItemResource.setId(id);
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
		
		if((mapItemResource.getInfoResource()==null || mapItemResource.getInfoResource().getTextMap().isEmpty()))
			return "at least one localized text must be set";

		if(selectedPopupIds.isEmpty()) return "no 'Allowed On' items have been selected";
		
		return null;
	}


	
	@Override
	protected void saveData() {
		
		ValidationHelper validationHelper = new ValidationHelper();		
		validationHelper.validateString("Name", nameTxt.getText());
		mapItemResource.setName(validationHelper.getStringResult());	
		mapItemResource.setImageResourceId(imageIds[imageCombo.getSelectedIndex()]);	
		mapItemResource.setDrawFirst(drawFlat.isSelected());	
		mapItemResource.setShowAttributes(showAtts.isSelected());
		mapItemResource.setAllowedon(selectedPopupIds);
		
		if(isNewItem()) Resource.addMapItemResource(mapItemResource);
		setFormReady(false);//turn off the textbox listener
		initMenu();
		initLightFields();
		configureMenuPopupItem(mapItemResource.getAllowedon());
		updateSlider(Resource.getAllMapItemResourceIds(), false);
		setFormReady(true);//turn them back on
		postSaveData();
		
	}
	
	
	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(isNewItem()){
			clearFields();
		}else{

			initLightFields();
			configureMenuPopupItem(mapItemResource.getAllowedon());
		}
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
		
		boolean childDirty = isChildGuiXmlWritePending(localizedTextButton);		
		if(childDirty) setDirtyChildren(true);		
		updateButtonLabelWithState(localizedTextButton, "Localized Texts", childDirty);
		
		childDirty = isChildGuiXmlWritePending(actionsButton);	
		if(childDirty) setDirtyChildren(true);	
		updateButtonLabelWithState(actionsButton, "Actions", childDirty);
		
		childDirty = isChildGuiXmlWritePending(attributesButton);		
		if(childDirty) setDirtyChildren(true);		
		updateButtonLabelWithState(attributesButton, "Attributes", childDirty);
		
	}
	
	@Override
	protected String preDeleteChecks(){
		
		int id = mapItemResource.getId();
		
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		for (MapItemResource mapItem : mapItems) {
			
			List<Integer> allowedOn = mapItem.getAllowedon();
			for (int mapItemId : allowedOn) {
				if(id == mapItemId) return "map item is used in the allowed on list for map item '"+mapItem.getName()+"'";
			}
		}
		
		List<BuildingResource> buildings = Resource.getBuildingResourceList();
		if(buildings!=null && !buildings.isEmpty()){
			for (BuildingResource buildingResource : buildings) {
				List<Integer> allowedOn = buildingResource.getAllowedon();
				for (int mapItemId : allowedOn) {
					if(id == mapItemId) return "map item is used in the allowed on list for building '"+buildingResource.getName()+"'";
				}
				if(!buildingResource.isWarehouse()){
					List<BuildingActionResource> actionList = buildingResource.getBuildingActionList();
					if(actionList!=null && !actionList.isEmpty()){
						for (BuildingActionResource buildingActionResource : actionList) {
							List<BuildingActionProduceResource> produces = buildingActionResource.getProduces();
							if(produces!=null && !produces.isEmpty()){
								for (BuildingActionProduceResource buildingActionProduceResource : produces) {
									BuildingMapItemActionResource produceMapItem = buildingActionProduceResource.getFirstBuildingMapItemActionResource();
									if(produceMapItem!=null && produceMapItem.getMapItem()==id) 
										return "map item used in building '"+buildingResource.getName()+"' action: '"+buildingActionResource.getTitle()+"' with produced";
								}
							}
							List<BuildingActionRequireResource> requires = buildingActionResource.getRequirements();
							if(requires!=null && !requires.isEmpty()){
								for (BuildingActionRequireResource buildingActionRequireResource : requires) {
									BuildingMapItemActionResource requiredMapItem = buildingActionRequireResource.getFirstBuildingMapItemActionResource();
									if(requiredMapItem!=null && requiredMapItem.getMapItem()==id) 
										return "map item used in building '"+buildingResource.getName()+"' action: '"+buildingActionResource.getTitle()+"' with required";
								}
							}
						}
					}
				}
			}			
		}
		
		if(getOnMapWarning()!="") return onMapWarning;

		return null;
	}
	
	

	
	private String getOnMapWarning(){
		
		if(onMapWarning==null){
			
			onMapWarning = LevelDataIO.checkMapItemResourceIdsUsed(mapItemResource.getId());
			if(onMapWarning == null) onMapWarning = "";//stop re-check
		}
		
		return onMapWarning;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Map Item.\n\n")
						 .append("This is where you can create and configure a Map Item (eg a tree), what image set it uses, what attributes it has (eg wood) and what actions it can do (actions happen after a certain amount of time has passed, an attribute has become depleted or when a building's worker interacts with it)\n\n")
						 .append("The Name text field is a mandatory and is used to reference the map item within this editor.\n\n")
						 .append("The Image dropdown combo box has all the image resource sets that have been created.  You must reference a image set.\n\n")
						 .append("The Is Flat check box determines how the map item is to be drawn on the screen.  When this is checked, the map item will be draw on the screen first, this will give the appearance of the item being flat as all other items and workers will be drawn on top of it.  If it is unchecked, then any non-flat items and workers that above it will be drawn after, giving the appearance that they are behind it.\n\n")
						 .append("The Show Attributes checkbox will show the player how many attributes are remaining on the map item when they click on it, if you do not want the player to know, then leave this unchecked.\n\n")
						 .append("The Allowed On is a multiple selection popup that allows you to select all the map items that this item is allowed to be placed upon.\n")
						 .append("Grass (default) is always in the list and means anywhere where a item has not already been placed, if this is unchecked, then you can only place it upon items you have selected, rather than as well as.\n")
						 .append("Items can be placed across items, for example, if you have grass checked and a mountain item checked, then this item could be place half on the mountain and half on the grass.\n")
						 .append("Map Items must be saved to the xml before they will appear on the Allowed On list.\n\n")
						 .append("Localized Text is mandatory and shows the texts displayed to the player.\n\n")
						 .append("Actions allows you to set the image action sets that will be used from the selected image resource set from the Image dropdown combo.\n")
						 .append("You can set what happens to the map item after the action, if it will be in a creation state, destruction state or nothing, or if it will change into another map item that has the same span.  Action after time has passed is also configured here.\n\n")
						 .append("Attributes allows you to set what items are in the map item (eg wood), and what Action is fired when the attribute has been depleted").toString();
	}

}
