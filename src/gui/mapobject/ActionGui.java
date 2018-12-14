package gui.mapobject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemAttResource;
import data.map.resources.MapItemResource;
import data.map.resources.MultiImageResourceAction;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;
import gui.ValidationHelper;

public class ActionGui extends ChildBaseGui {
	

	public ActionGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private String name;
	private List<Integer> allowedon;
	
	private MapItemResource mapItemResource;
	private ImageResource imageResource;
	
	private JTable table;
	private ITableUpdateHook hook;	
	private String[] states = new String[]{"Creation","Destruction","None"};
	private JComboBox<String> stateCombo, imageCombo, mapItemCombo, timeCombo;
	private int selectedRow = -1;
	private List<MapItemActionResource> tableItems, revertTableItems;
	private int revertTime, revertedTimeActionId;
	
	private List<Integer> imageBusyIds;
	private List<Integer> mapItemIdList;
	
	private JButton addButton, editButton, deleteButton;
	private JSpinner timeSpinner;
	private JTextField nameTxt;


	@Override
	protected int addComponents(JFrame frame) {
		
		//empty apple tree (10)
		//<busy id="0" name="planting" total="16" skip="2" directory="1"/>
		//<busy id="1" name="cutting" total="12" skip="2" directory="2"/>
		
		//full apple tree (11)
		//<busy id="0" name="picking apples" total="12" skip="2" directory="1"/>
		
		/*
		 * empty apple tree
		 * time="12" onElapse="2"
		 * <mapitematt id="1" amount="1" onDepletion="-1"/>
		 * <action id="0" state="1" mapitem="-1" busy="0"/>
		 * <action id="1" state="2" mapitem="-1" busy="1"/>
		 * <action id="2" state="1" mapitem="11" busy="-1"/>
		 * 
		 * full apple tree
		 * <mapitematt id="2" amount="3" onDepletion="1"/>
		 * <action id="0" state="3" mapitem="-1" busy="0"/>
		 * <action id="1" state="3" mapitem="10" busy="-1"/>
		 */
		
		//empty tree time elapse, no image animation, creation of full apple tree
		//apples deplete, no image animation, apple tree (no creation)
		
		//if they has a image animation, it would be executed before changing into another map object
		
		
		// <!--> state 1=creation, 2=destruction, 3=none <-->
		// busy is the image's busy
		// map item, what it turns into
		
		Object[] destructionNames = new Object[]{"Name", "Image Action", "Map Item", "State"};
		int[] sizes = new int[]{150, 100, 150, 150}; 
      	
		JPanel panel = new JPanel();
	
		nameTxt = addLabelAndTextFieldToPanel(panel, "Name", 0, 0, 10, true);
		
		imageCombo = addLabelAndComboToPanel(panel, "Image Action", 0, 0, getImageBusyNamesAndConfigureIds());
		

		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();


		
		mapItemCombo = addLabelAndComboToPanel(panel, "Map Item", 0, 0, getMapItemNamesAndConfigureIds());
		
		stateCombo = addLabelAndComboToPanel(panel, "State", 0, 0, states);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
      	addButton = addTableButton(panel, "Add", true);
      	editButton = addTableButton(panel, "Edit", false);
      	deleteButton = addTableButton(panel, "Remove", false);
      	
      	frame.add(panel, getSidePaddedGridBagConstraints(0, 2));
      	
 
		
      	table = createTable(destructionNames, sizes, 0, 3);
      	hook = addHookToTable(table);
      	
     	panel = new JPanel();
     	
		timeSpinner = addLabelAndNumberSpinnerToPanel(panel, "After", 0, 0, Short.MAX_VALUE, 1);
		timeSpinner.setValue(1);
		
		timeSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady()) setDirtyStateAndConfigure(true);
				
			}
		});
		
		panel.add(timeSpinner);
		
		timeCombo = addLabelAndComboToPanel(panel, "second(s), do", 0, 0, getMapItemActionNames());
		
		timeCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(isFormReady()) setDirtyStateAndConfigure(true);		
			}
		});
		
		panel.add(timeCombo);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 4));
      	
      	addListenerToTable(table, editButton, deleteButton);
        
        tableItems = new ArrayList<MapItemActionResource>();
        configureTableMap(tableItems);
        displayTable(hook, tableItems);
		
		return 5;
		
		
		//time
		//elapse
		//for table
		// - state
		// - map item
		// - busy (image)

	}
	
	
	
	@Override
	protected void postDrawGui() {
		super.postDrawGui();
		
		if(mapItemResource.getTime() > 0){
			setFormReady(false);
			timeCombo.setSelectedIndex(getTableIndexFromId(mapItemResource.getOnElapse())+1);
			timeSpinner.setValue(mapItemResource.getTime());		
		}
		setFormReady(true);
		
		for(MapItemActionResource resource : mapItemResource.getMapItemActionList()){
			
			if(checkForUsage(resource)!=null){
				setDeleteButtonEnablement(false);
				break;
			}
		}
	}



	protected String[] getMapItemActionNames(){
		
		List<MapItemActionResource> actionList = mapItemResource.getMapItemActionList();
		
		List<String> names = new ArrayList<String>();
		
		names.add("Nothing");
		
		for (MapItemActionResource action : actionList) {
			
			names.add(action.getInternalName());
		}
		
		return names.toArray(new String[]{});
	}
	
	protected String[] getMapItemNamesAndConfigureIds(){
		
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		
		List<String> mapItemNameList = new ArrayList<String>();
		mapItemIdList = new ArrayList<Integer>();

		mapItemNameList.add(getThisItemStr());
		mapItemIdList.add(-1);
		
		for (MapItemResource mapItem : mapItems) {
			
			if(mapItemResource.getId() != mapItem.getId() && checkSpansMatch(mapItem) && checkAllowedOn(mapItem)){
				
				mapItemNameList.add(mapItem.getName());
				mapItemIdList.add(mapItem.getId());
			}
		}
		
		return mapItemNameList.toArray(new String[]{});
	}
	
	private boolean checkSpansMatch(MapItemResource mapItem2){
		
		Coords span1 = imageResource.getSpan();
		Coords span2 = mapItem2.getImageResource().getSpan();
		
		return span1.x() == span2.x() && span1.y() == span2.y();
	}
	
	private boolean checkAllowedOn(MapItemResource mapItem){

		if(allowedon.size() != mapItem.getAllowedon().size()){

			return false;
		}
		
		for (int id : mapItem.getAllowedon()) {
			
			if(!allowedon.contains(id)){

				return false;
			}
		}
		
		return true;
	}
	
	protected String[] getImageBusyNamesAndConfigureIds(){
		
		List<MultiImageResourceAction> busys = imageResource.getBusy();

		String[] busyNames = new String[busys.size()+1];
		imageBusyIds = new ArrayList<Integer>();
		imageBusyIds.add(-1);
		busyNames[0] = "<none>";
		int i = 1;
		for (MultiImageResourceAction multiImageResourceAction : busys) {
			busyNames[i] = multiImageResourceAction.getInternalName();
			imageBusyIds.add(multiImageResourceAction.getId());
			i++;
		}
		
		return busyNames;
	}
	
	protected void initTimeCombo(String[] items){
		
		timeCombo.removeAllItems();
		
		for (String item : items) {
			
//			System.out.println("adding.."+item);
			timeCombo.addItem(item);
		}
	}
	
	protected void configureTableMap(List<MapItemActionResource> tableItems){
		
		List<MapItemActionResource> resourceList = mapItemResource.getMapItemActionList();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(MapItemActionResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<MapItemActionResource> tableItems){
		
		hook.clearTable();

		for (MapItemActionResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1);
		}
	}
	
	protected void addRowFromResource(MapItemActionResource resource, ITableUpdateHook hook, int row){
		
		Object[] data = new Object[]{

				resource.getInternalName(),
				resource.getBusy()==-1 ? "none" : imageResource.getBusyById(resource.getBusy()).getInternalName(),
				resource.getMapitem()==-1 ? getThisItemStr() : Resource.getMapItemResourceById(resource.getMapitem()).getName(),
				states[resource.getState()-1]
				
			};

			hook.addDataRowToTable(data, row);
	}
	
	protected void addListenerToTable(final JTable table, final JButton edit, final JButton delete){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {

				
				selectedRow = table.getSelectedRow();
				
				if(selectedRow==-1){
					
					edit.setEnabled(false);
					delete.setEnabled(false);
					
				}else{
	
					edit.setEnabled(true);
					delete.setEnabled(true);
				}
				
			}
        	
        	
        });
        
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == deleteButton){

			deleteResourcesFromTable();

		}else if(button == addButton){

			String warning = addResourceToTableAndMap();
			
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == editButton){

			editResourceFromTable();
			
		}
	}
	
	protected void deleteResourcesFromTable(){
		
		if(selectedRow > -1){
			
			String warning = checkForUsage(tableItems.get(selectedRow));
			if(warning!=null){
				
				displayWarning(warning);
				return;
			}
			
			timeCombo.removeItemAt(selectedRow+1);
			tableItems.remove(selectedRow);
			hook.removeRow(selectedRow);
			setDirtyStateAndConfigure(true);
		}
	}
	



	protected String addResourceToTableAndMap(){
		
		
		ValidationHelper validationHelper = new ValidationHelper();

		if(!validationHelper.validateString("Name", nameTxt.getText()))
			return validationHelper.getWarning();
		
		String name = validationHelper.getStringResult();

		int i = 0;
		int row = -1;

		int id = -1;
		
		for (MapItemActionResource resource : tableItems) {
			
			if(name.equals(resource.getInternalName())){
				if(addOverwriteConfirmation()){
					row = i;
					id = resource.getId();
					break;
				}else{
					return null;
				}
			}
			i++;
		}
		
		MapItemActionResource resource = new MapItemActionResource();
		
		resource.setInternalName(name);
		resource.setState(stateCombo.getSelectedIndex()+1);
		resource.setBusy(imageBusyIds.get(imageCombo.getSelectedIndex()));
		resource.setMapitem(mapItemIdList.get(mapItemCombo.getSelectedIndex()));

		if(row > -1){
			resource.setId(id);
			tableItems.remove(row);
			tableItems.add(row, resource);
			
		}else{
			resource.setId(getNextId());
			tableItems.add(resource);
			timeCombo.addItem(resource.getInternalName());
		}
		
		addRowFromResource(resource, hook, row);
		setDirtyStateAndConfigure(true);
		return null;
	}
	
	private int getNextId(){
		
		List<Integer> ids = new ArrayList<Integer>();
		
		for (MapItemActionResource resource : tableItems) {
			
			ids.add(resource.getId());
		}
		
		return Resource.findUnusedId(ids);
	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			
			MapItemActionResource resource = tableItems.get(selectedRow);
			
			nameTxt.setText(resource.getInternalName());
			stateCombo.setSelectedIndex(resource.getState()-1);
			imageCombo.setSelectedIndex(getIndexOfId(imageBusyIds, resource.getBusy()));
			mapItemCombo.setSelectedIndex(getIndexOfId(mapItemIdList, resource.getMapitem()));

		}
	}
	
	private int getIndexOfId(List<Integer> list, int id){
		
		int i = 0;
		for (int itemId : list) {
			
			if(id == itemId) return i;
			i++;
		}
		
		return -1;
	}
	
	private int getTableIndexFromId(int id){
		
		int i = 0 ;
		for (MapItemActionResource resource : tableItems) {
			if(id==resource.getId()){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}

	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		mapItemResource = (MapItemResource) properties.get(PropertyKeys.MAP_OBJECT_RESOURCE);
		
		name = (String) properties.get(PropertyKeys.MAP_OBJECT_RESOURCE_NAME);
		
		allowedon = (ArrayList<Integer>) properties.get(PropertyKeys.MAP_OBJECT_RESOURCE_ALLOWEDON);
		
		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
	}
	
	@Override
	protected void saveData() {
		
		if(timeCombo.getSelectedIndex()==0){
			mapItemResource.setOnElapse(0);
			mapItemResource.setTime(-1);	
		}else{
			
			mapItemResource.setOnElapse(tableItems.get(timeCombo.getSelectedIndex()-1).getId());
			mapItemResource.setTime((int)timeSpinner.getValue());
		}
		
		revertTime = mapItemResource.getTime();
		revertedTimeActionId = revertTime==-1 ? -1 : mapItemResource.getOnElapse();
		
		revertTableItems = copyTableItems(tableItems);
		mapItemResource.setMapItemAction(copyTableItems(tableItems));

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		setFormReady(false);
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<MapItemActionResource>();
			configureTableMap(tableItems);
			initTimeCombo(getMapItemActionNames());
			
			if(mapItemResource.getTime() > 0){				
				timeCombo.setSelectedIndex(getTableIndexFromId(mapItemResource.getOnElapse())+1);
				timeSpinner.setValue(mapItemResource.getTime());
			}else{
				timeCombo.setSelectedIndex(0);
				timeSpinner.setValue(1);
			}
		}else{
			
			tableItems = copyTableItems(revertTableItems);
			initTimeCombo(getMapItemActionNames());
			timeCombo.setSelectedIndex(getTableIndexFromId(revertedTimeActionId)+1);
			timeSpinner.setValue(revertTime < 1 ? 1 : revertTime);
		}
		
		displayTable(hook, tableItems);
		
		setFormReady(true);

	}

	@Override
	protected void newButtonClicked() {
		
		clearFields();
		timeSpinner.setValue(1);
		timeCombo.setSelectedIndex(0);
		
		if(mapItemResource.getTime()!=-1) setDirtyStateAndConfigure(true);

	}
	
	protected void clearFields(){
		
		nameTxt.setText("");
		stateCombo.setSelectedIndex(0);
		imageCombo.setSelectedIndex(0);
		mapItemCombo.setSelectedIndex(0);
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		//validation done on add to table
		return null;
	}



	
	protected List<MapItemActionResource> copyTableItems(List<MapItemActionResource> tableItems){
		
		ArrayList<MapItemActionResource> tableItemsCopy = new ArrayList<MapItemActionResource>();
		
		for (MapItemActionResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}
	
	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Remove ALL requirements from the table?", "Clear Tables", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}
	
	@Override
	protected boolean deleteActions() {
		boolean enableButton = true;

		tableItems = new ArrayList<MapItemActionResource>();
		timeSpinner.setValue(1);
		timeCombo.setSelectedIndex(0);
		saveData();	
		initTimeCombo(getMapItemActionNames());
        displayTable(hook, tableItems);
		
		return enableButton;
	}
	
	@Override
	protected String getFrameTitle(){
		
		return getTitle() + name;
	}
	
	private String getThisItemStr(){
		
		return "<"+name+">";
	}
	
	protected String checkForUsage(MapItemActionResource mapItemActionResource) {
		
		int id = mapItemActionResource.getId();
		
		List<MapItemAttResource> attList = mapItemResource.getMapItemAttList();
		
		for (MapItemAttResource mapItemAttResource : attList) {
			
			if(mapItemAttResource.getOnDepletion() == id)
				return "Action used on this map item's Attribute for "+Resource.getItemInternalNameById(mapItemAttResource.getId());
		}
		
		//TODO building check

		return null;
	}



	@Override
	protected String getHelpText() {
		//you can only select item to change into that have the same span
		// TODO Auto-generated method stub
		return new StringBuilder("Map Item Actions\n\n")
						 .append("This is where you decide what happens to a map item after either a buildings worker has interacted with it, a certain amount of time has passed or one of it's attributes (items) has become depleted.\n\n")
						 .append("The Action referenced for an item depleting is set on the Attributes section of the map item.\n")
						 .append("Time running out is set on this form and the Action referenced for a worker interacting with the map item is set against the building.\n\n")
						 .append("An Action is made up from four main components, the Name Textbox which is used by the editor to identify the Action when it is being referenced.\n\n")
						 .append("The Image Action dropdown combo box that has all the sequences of action images that were added to the image resource set (image resource set was defined on the previous form).  These get displayed before the map item changes into another map item or changes status.\n\n")
						 .append("The Map Item dropdown combo box is for the map item you would like this map item to turn into once the Image Action image set has completed, the first item in the list that is marked in <> is this map item (with no changes at all).\n")
						 .append("Any others are map items that image resource has the same span as this map item's image resource and is allowed on the same map items(all map items resources that have a different span are filtered from the list.)\n")
						 .append("Note that when changing into another map item, all attributes are replaced with the new map items attributes and will be set to their maximums.\n\n")
						 .append("Finally we have the map items status, there are three statuses; creation, destruction and none.\n")
						 .append("The status will be applied to the new map item (or the current if the map item was not changed).\n")
						 .append("Creation status will display the map items creation images and then the map item will become idle once completed.\n")
						 .append("Destruction status will display the destruction images and remove the item from the map after destruction has completed.\n")
						 .append("None status will leave the current/new map item in an idle state. (note that workers and time will only interact with a map item that is in an idle state).\n\n")
						 .append("When you are happy with all four componets, click the Add button to add them to the table.  You can Edit or Remove previously added rows, by selecting the row and then clicking Edit/Remove.\n\n")
						 .append("Below the table we have the componets that allow you to configure what action to called after a certain amount of time has passed.\n")
						 .append("You can set the number of seconds (8 game ticks is about a second on normal game speed) that need to pass before an action is called by enter a number in the box shown after 'After'.\n")
						 .append("Select the Action that should happen from the dropdown combo box shown after 'do', if you do not want any actions after a certain amout of time, select Nothing from the combo box.").toString();
	}


}
