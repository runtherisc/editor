package gui.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingActionResource;
import data.map.resources.BuildingAreaResource;
import data.map.resources.BuildingMapItemActionResource;
import data.map.resources.BuildingResource;
import data.map.resources.ImageResource;
import data.map.resources.MultiImageResourceAction;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;
import gui.ValidationHelper;

public class BuildingActionGui extends ChildBaseGui {

	public BuildingActionGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private BuildingResource buildingResource;
	private ImageResource imageResource;
	
	private JTable table;
	private ITableUpdateHook hook;	
	private List<BuildingActionResource> tableItems, revertTableItems;
	private BuildingAreaResource revertArea;
	
	private JSpinner areaSize;
	private JCheckBox fixedArea, mustFulFill;
	private JButton requirementsButton, productionButton, localizedTextButton;
	private JTextField nameTxt;
	private JComboBox<String> imageCombo, issueIdleCombo;
	
	private JButton addButton, editButton, deleteButton;
	private int selectedRow = -1;
	private int editingRow;
	
	private List<Integer> imageBusyIds, imageIdleIds;
	
	@Override
	protected int addComponents(JFrame frame) {
		
		//non table items:
		//area size
		//fix area
		
		//table items:
		//busy (work from home, can be none)
		//mustfulfill boolean
		//internal title
		
		//table buttons: require selected row
		//info (title only) mandatory
		//require
		//produce  ?require and produce on the same form?
		
		//table:
		//| title | busy | must fulfill | texts | require | produce |
		//| xxx   | none | true         | 1     | 1       | 0       |
		
		Object[] tableNames = new Object[]{"Name", "Image Action", "Issue Idle", "Must Fulfill", "Texts", "Requires", "Produces"};
		int[] sizes = new int[]{150, 150, 150, 80, 80, 80, 80};
		
		
		JPanel panel = new JPanel();

		JLabel imageset = new JLabel("Image Set: "+imageResource.getNameFromDir());
		imageset.setEnabled(false);
		panel.add(imageset);
		
		areaSize = addLabelAndNumberSpinnerToPanel(panel, " Area Size", 0, 0, 20, 0);
		areaSize.setValue(0);
		
		areaSize.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady()) setDirtyStateAndConfigure(true);
				
			}
		});
		
		fixedArea = new JCheckBox("Fixed Area");
		panel.add(fixedArea);
		
		fixedArea.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(isFormReady()) setDirtyStateAndConfigure(true);
				
			}
		});
		
		frame.add(panel, getRightPaddedGridBagConstraints(0, 0));
		
		frame.add(new JSeparator(), getNoPaddingGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
		nameTxt = addLabelAndTextFieldToPanel(panel, "Name", 0, 0, 10, true);
		
		imageCombo = addLabelAndComboToPanel(panel, "Image Action", 0, 0, getImageBusyNamesAndConfigureIds());
		issueIdleCombo = addLabelAndComboToPanel(panel, "Issue Idle", 0, 0, getImageIdleNamesAndConfigureIds());
		
		mustFulFill = new JCheckBox("Must Fulfill");
		panel.add(mustFulFill);
		
		frame.add(panel, getRightPaddedGridBagConstraints(0, 2));
		
		panel = new JPanel();
		
      	addButton = addTableButton(panel, "Add", true);
      	editButton = addTableButton(panel, "Edit", false);
      	deleteButton = addTableButton(panel, "Remove", false);
      	
      	localizedTextButton = new JButton("Localized Texts");
      	addGuiButtonAndListener(new BuildingActionTextsGui("Action text", frame), localizedTextButton);
    	panel.add(localizedTextButton);
		localizedTextButton.setEnabled(false);
		
      	requirementsButton = new JButton("Required");
      	addGuiButtonAndListener(new BuildingRequireGui("Building Requirements", frame), requirementsButton);
    	panel.add(requirementsButton);
		requirementsButton.setEnabled(false);
    	
      	productionButton = new JButton("Produced");
      	addGuiButtonAndListener(new BuildingProduceGui("Building Production", frame), productionButton);
    	panel.add(productionButton);
		productionButton.setEnabled(false); 
    	
    	frame.add(panel, getRightPaddedGridBagConstraints(0, 3));
    	
      	table = createTable(tableNames, sizes, 0, 4);
      	hook = addHookToTable(table);
    	
      	addListenerToTable(table, editButton, deleteButton, requirementsButton, productionButton, localizedTextButton);
        
        tableItems = new ArrayList<BuildingActionResource>();
        configureTableMap(tableItems);
        displayTable(hook, tableItems);
        
        editingRow = -1;
		
		return 5;
	}
	
	@Override
	protected void postDrawGui() {
		super.postDrawGui();
		
		initFields();
	}
	
	protected void initFields(){
		
		setFormReady(false);
		
		BuildingAreaResource areaResource = buildingResource.getBuildingAreaResource();
		
		if(areaResource!=null){
			
			setFormReady(false);
			areaSize.setValue(areaResource.getAreaSize());
			fixedArea.setSelected(areaResource.isFixed());
			setFormReady(true);
		}
		
		setFormReady(true);
	}
	protected void configureTableMap(List<BuildingActionResource> tableItems){
		
		List<BuildingActionResource> resourceList = buildingResource.getBuildingActionList();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(BuildingActionResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<BuildingActionResource> tableItems){
		
		hook.clearTable();

		for (BuildingActionResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1);
		}
	}
	
	protected void addRowFromResource(BuildingActionResource resource, ITableUpdateHook hook, int row){
		
		int imageBusyId = resource.getBusyAction();
		int imageIdleId = resource.getIssueIdle();
		
		Object[] data = new Object[]{

				resource.getTitle(),
				imageBusyId==-1 ? "<none>" : imageResource.getBusyById(imageBusyId).getInternalName(),
				imageIdleId==-1 ? "<none>" : imageResource.getIdleById(imageIdleId).getInternalName(),
				resource.isFulfill(),
				resource.getInfoResource().getTextMap().size(),
				resource.getRequirements().size(),
				resource.getProduces().size()
				
			};

			hook.addDataRowToTable(data, row);
	}
	
	protected void addListenerToTable(final JTable table, final JButton edit, final JButton delete, 
			final JButton requirementsButton, final JButton productionButton, final JButton localizedTextButton){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {

				
				selectedRow = table.getSelectedRow();
				
				if(selectedRow==-1){
					
					edit.setEnabled(false);
					delete.setEnabled(false);
					requirementsButton.setEnabled(false);
					productionButton.setEnabled(false); 
					localizedTextButton.setEnabled(false);
					
				}else{
	
					edit.setEnabled(true);
					delete.setEnabled(true);
					requirementsButton.setEnabled(true);
					productionButton.setEnabled(true); 
					localizedTextButton.setEnabled(true);
				}
				
			}
        	
        	
        });
        
	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){	
		
		if(selectedRow > -1){
			
			if(childName.equals(BuildingProduceGui.class.getName()) || childName.equals(BuildingRequireGui.class.getName())){
				
				if(Resource.getWarehouseItemInternalNames().size()==0) return "items must be created first";
			}
			
			BuildingActionResource resource = tableItems.get(selectedRow);
			
			addToPassedProperties(PropertyKeys.BUILDING_ACTION_RESOURCE, resource);

			addToPassedProperties(PropertyKeys.BUILDING_ACTION_RESOURCE_NAME, resource.getTitle());
			
			addToPassedProperties(PropertyKeys.BUILDING_ACTION_AREA, (int)areaSize.getValue() > 0);

		}else return "Row must be selected from the table first";
		
		return null;
	}
	
	protected String[] getImageBusyNamesAndConfigureIds(){
		
		List<MultiImageResourceAction> busys = imageResource.getBusy();

		imageBusyIds = new ArrayList<Integer>();

		return configureIdsAndNames(imageBusyIds, busys);
	}
	
	protected String[] getImageIdleNamesAndConfigureIds(){
		
		List<MultiImageResourceAction> idles = imageResource.getIdles();

		imageIdleIds = new ArrayList<Integer>();

		return configureIdsAndNames(imageIdleIds, idles);

	}
	
	private String[] configureIdsAndNames(List<Integer> ids, List<MultiImageResourceAction> actions){
		
		String[] names = new String[actions.size()+1];
		
		ids.add(-1);
		names[0] = "<none>";
		int i = 1;
		for (MultiImageResourceAction multiImageResourceAction : actions) {
			names[i] = multiImageResourceAction.getInternalName();
			ids.add(multiImageResourceAction.getId());
			i++;
		}
		
		return names;
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
		
		for (BuildingActionResource resource : tableItems) {
			
			if(name.equals(resource.getTitle())){
				if(addOverwriteConfirmation()){
					row = i;
					break;
				}else{
					return null;
				}
			}
			i++;
		}
		
		BuildingActionResource resource;
		
		if(editingRow > -1 && editingRow == row){
			
			resource = tableItems.get(editingRow).copy();
		}else{
			resource = new BuildingActionResource();
		}
		
		resource.setTitle(name);
		resource.setBusyAction(imageBusyIds.get(imageCombo.getSelectedIndex()));
		resource.setIssueIdle(imageIdleIds.get(issueIdleCombo.getSelectedIndex()));
		resource.setFulfill(mustFulFill.isSelected());

		if(row > -1){
			tableItems.remove(row);
			tableItems.add(row, resource);
			
		}else tableItems.add(resource);
		
		addRowFromResource(resource, hook, row);
		setDirtyStateAndConfigure(true);
		clearFields();
		
		setDirtyStateAndConfigure(true);
		
		return null;
	}
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			
			editingRow = selectedRow;
			
			BuildingActionResource resource = tableItems.get(selectedRow);
			
			nameTxt.setText(resource.getTitle());
			imageCombo.setSelectedIndex(getIndexOfId(imageBusyIds, resource.getBusyAction()));
			issueIdleCombo.setSelectedIndex(getIndexOfId(imageIdleIds, resource.getIssueIdle()));
			mustFulFill.setSelected(resource.isFulfill());
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
	
	protected List<BuildingActionResource> copyTableItems(List<BuildingActionResource> tableItems){
		
		ArrayList<BuildingActionResource> tableItemsCopy = new ArrayList<BuildingActionResource>();
		
		for (BuildingActionResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}
	
	@Override
	protected void saveData() {
		
		revertTableItems = copyTableItems(tableItems);
		buildingResource.setBuildingAction(copyTableItems(tableItems));
		buildingResource.setBuildingAreaResource(new BuildingAreaResource(fixedArea.isSelected(), (int)areaSize.getValue()));
		revertArea = new BuildingAreaResource(fixedArea.isSelected(), (int)areaSize.getValue());

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<BuildingActionResource>();
			initFields();
			configureTableMap(tableItems);

		}else{
			setFormReady(false);
			tableItems = copyTableItems(revertTableItems);
			areaSize.setValue(revertArea.getAreaSize());
			fixedArea.setSelected(revertArea.isFixed());
			setFormReady(true);
		}
		
		displayTable(hook, tableItems);

	}

	@Override
	protected void newButtonClicked() {
		
		areaSize.setValue(0);
		fixedArea.setSelected(false);
		clearFields();

	}
	
	protected void clearFields(){
		
		nameTxt.setText("");
		imageCombo.setSelectedIndex(0);
		issueIdleCombo.setSelectedIndex(0);
		mustFulFill.setSelected(false);
		editingRow = -1;
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

		tableItems = new ArrayList<BuildingActionResource>();
		saveData();	
        displayTable(hook, tableItems);
        editingRow = -1;
		
		return enableButton;
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		String validation = null;

		for (BuildingActionResource buildingActionResource : tableItems) {
			
			if((int)areaSize.getValue()==0){
				
				validation = hasMapItemInRequirement(buildingActionResource.getRequirements(), buildingActionResource.getTitle());
				if(validation==null) validation = hasMapItemInRequirement(buildingActionResource.getProduces(), buildingActionResource.getTitle());
			}
			if(validation==null) validation = validateText(buildingActionResource);
			if(validation!=null) break;
			
		}
		
		System.out.println("validation "+validation);
		
		return validation;
	}
	
	private String hasMapItemInRequirement(List<? extends BuildingActionRequireResource> list, String title){
		
		for (BuildingActionRequireResource buildingActionRequireResource : list) {
			
			List<BuildingMapItemActionResource> mapItemList = buildingActionRequireResource.getBuildingMapItemActionResource();
		
			if(mapItemList!=null){
				
				for (BuildingMapItemActionResource buildingMapItemActionResource : mapItemList) {
					
					if(buildingMapItemActionResource.getMapItem() > -1){

						return Resource.getMapItemResourceById(buildingMapItemActionResource.getMapItem()).getName() + " is used in '"+title+"' so area cannot be 0";
					}
				}
			}
		
		}
		
		return null;
	}
	
	private String validateText(BuildingActionResource buildingActionResource){
		
		if(buildingActionResource.getInfoResource().getLocaleKeys().isEmpty())
			return buildingActionResource.getTitle() + " does not have any Localized Texts set";
		
		return null;
	}

	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
		
		buildingResource = (BuildingResource) properties.get(PropertyKeys.BUILDING_RESOURCE);

	}
	
	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(localizedTextButton);
		if(childDirty) setDirtyChildren(true);	
		updateButtonLabelWithState(localizedTextButton, "Localized Texts", childDirty);
		
		childDirty = isChildGuiXmlWritePending(requirementsButton);	
		if(childDirty) setDirtyChildren(true);	
		updateButtonLabelWithState(requirementsButton, "Required", childDirty);
		
		childDirty = isChildGuiXmlWritePending(productionButton);		
		if(childDirty) setDirtyChildren(true);		
		updateButtonLabelWithState(productionButton, "Produced", childDirty);
		
		displayTable(hook, tableItems);
		
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Building Actions\n\n")
						 .append("Building Actions is where you can define what tasks a building does, a task consists of the items it requires, items it produces etc. to complete the task.\n\n")
						 .append("Buildings that interact with Map Items require a area of work, this will be a square where you can define it's size, this area will be global for all map items that the building interacts with.\n\n")
						 .append("The Area Size box is where you can define the size of the square area a worker will search for a map item.\n\n")
						 .append("The building by default will be in the approximately in the centre of the search area, if you do not want the player to be able to change the position of the search area ingame, check the Fixed Area checkbox.\n\n")
						 .append("Below the global area components are tasks that the building can execute.\n\n")
						 .append("The Name textbox is used by the editor to identify the task, an example task could be; plant tree, cut down tree, turn wood into a plant etc.\n\n")
						 .append("The Image Action dropdown combo box is all the actions that were added to the image resource set defined on the previous form.\n")
						 .append("These images are displayed when a worker is working at the building (eg a sawmill cutting wood), and takes place after the required items have been collected by the building and before any items are returned/placed.\n")
						 .append("Select <none> if the worker does not need to work at the building for this task.\n\n")
						 .append("The Issue Idle dropdown combo box shows all the idle image sets that have been added to the image resource that was defined on the previous form.\n")
						 .append("If there is an issue with the current task then this idle image set will be shown.\n")
						 .append("Issue Idle is optional, so you can select <none> the image resource's main idle will be displayed instead when there is an issue.\n\n")
						 .append("If Must Fulfill is not checked, then the building will move on to it's next task if it cannot proceed with the current task, if no tasks can be fulfilled, then it will display an issue.\n")
						 .append("The caveat to this rule is if the task is in mid progress (eg items have been collected), then an issue will be shown.\n")
						 .append("Checking Must Fulfill means the task must be completed before it will move onto the next task.\n\n")
						 .append("When you are happy with the values entered for Name, Image Action, Isle Idle and Must Fulfill, click the Add button to add then to the table.\n\n")
						 .append("Once added to the table, you must configure the Localized Text, to do this, select the row of the task from the table and click the Localized Texts button.\n\n")
						 .append("To configure the required items for the task, select the row and then click the Required button. Setting required items is an optional requirement.\n\n")
						 .append("To configure the items the task produces, select the row and click the Produced button.  Setting produced items is an optional requirement.").toString();
	}



}
