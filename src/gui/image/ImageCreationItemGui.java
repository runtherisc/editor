package gui.image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.BuildingCreationResource;
import data.map.resources.CreationItemResource;
import data.map.resources.DestructionItemResource;
import data.map.resources.ImageResource;
import data.map.resources.ItemResource;
import data.map.resources.LifecycleItemResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class ImageCreationItemGui extends ChildBaseGui{

	public ImageCreationItemGui(String title, JFrame parent) {
		super(title, parent);

	}
	
	private BuildingCreationResource buildingCreationResource;
	private ImageResource imageResource;
	
	private JTable creationTable;
	private ITableUpdateHook creationHook;	
	private JComboBox<String> creationItemCombo, creationIdleCombo;
	private JSpinner creationAmount;
	private JCheckBox creationCheckbox;
	private int creationSelectedRow = -1;
	
	private JTable destructionTable;
	private ITableUpdateHook destructionHook;	
	private JComboBox<String> destructionItemCombo, destructionIdleCombo;
	private JSpinner destructionAmount;
	private JCheckBox destructionCheckbox;
	private int destructionSelectedRow = -1;
	
	private List<LifecycleItemResource> creationTableItems, creationRevertTableItems;
	private List<LifecycleItemResource> destructionTableItems, destructionRevertTableItems;
	
	private List<ItemResource> items;
	
	private JButton creationAdd = new JButton("Add"), creationEdit = new JButton("Edit"), creationDelete = new JButton("Remove");
	private JButton destructionAdd= new JButton("Add"), destructionEdit = new JButton("Edit"), destructionDelete= new JButton("Remove");

	@Override
	protected int addComponents(JFrame frame) {
		
		Object[] creationNames = new Object[]{"Item", "Amount", "Idle Override", "From Warehouse?"};
		Object[] destructionNames = new Object[]{"Item", "Amount", "Idle Override", "Only When Stocked?"};
		int[] sizes = new int[]{150, 80, 150, 150}; 
		
		items = Resource.getWarehouseItemInternalNames();	
		
		String[] itemNames = new String[items.size()];
		for (int i = 0; i < itemNames.length; i++) {
			itemNames[i] = items.get(i).getName();
		}
		
		String[] idleNames = imageResource.getIdleNames(true);	
		
		JPanel panel = new JPanel();
		
		
		creationItemCombo = addLabelAndComboToPanel(panel, "Creation Item", 0, 0, itemNames);
		
		creationAmount = addLabelAndNumberSpinnerToPanel(panel, "Creation Amount", 0, 0, 999, 1);
		creationAmount.setValue(1);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
			
		creationIdleCombo = addLabelAndComboToPanel(panel, "Creation Idle", 0, 0, idleNames);
		
		creationCheckbox = addLabelAndCheckBoxToPanel(panel, "From Warehouse?", 0, 0);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
      	creationTable = createTable(creationNames, sizes, 0, 3);
      	creationHook = addHookToTable(creationTable);
		
//		addTableButtons(panel, creationAdd, creationEdit, creationDelete, creationTable, true);
      	creationAdd = addTableButton(panel, "Add", true);
      	creationEdit = addTableButton(panel, "Edit", false);
      	creationDelete = addTableButton(panel, "Remove", false);
		addListenerToTable(creationTable, true, creationEdit, creationDelete);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 2));

      	frame.add(new JSeparator(), getSidePaddedGridBagConstraints(0, 4));
      	
		panel = new JPanel();
		
		
		destructionItemCombo = addLabelAndComboToPanel(panel, "Destruction Item", 0, 0, itemNames);
		
		destructionAmount = addLabelAndNumberSpinnerToPanel(panel, "Destruction Amount", 0, 0, 999, 1);
		destructionAmount.setValue(1);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 5));
		
		panel = new JPanel();
		
		destructionIdleCombo = addLabelAndComboToPanel(panel, "Destruction Idle", 0, 0, idleNames);
		
		destructionCheckbox = addLabelAndCheckBoxToPanel(panel, "Only When Stocked?", 0, 0);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 6));
		
		panel = new JPanel();
		
      	destructionTable = createTable(destructionNames, sizes, 0, 8);
      	destructionHook = addHookToTable(destructionTable);
		
//		addTableButtons(panel, destructionAdd, destructionEdit, destructionDelete, destructionTable, false);
      	destructionAdd = addTableButton(panel, "Add", true);
      	destructionEdit = addTableButton(panel, "Edit", false);
      	destructionDelete = addTableButton(panel, "Remove", false);
		addListenerToTable(destructionTable, false, destructionEdit, destructionDelete);
      	
		frame.add(panel, getSidePaddedGridBagConstraints(0, 7));

      	
      	creationTableItems = new ArrayList<LifecycleItemResource>();
        configureTableMap(creationTableItems, buildingCreationResource.getOnlyCreationItems());
        displayTable(creationHook, creationTableItems);
        
        destructionTableItems = new ArrayList<LifecycleItemResource>();
        configureTableMap(destructionTableItems, buildingCreationResource.getOnlyDestructionItems());
        displayTable(destructionHook, destructionTableItems);
		
		return 9;
	}
	
	
	
	@Override
	protected void postDrawGui() {
		super.postDrawGui();
//		setNewButtonEnablement(false);
	}

	


	protected void addListenerToTable(final JTable table, final boolean isCreationTable, final JButton edit, final JButton delete){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {

				
				int selectedRow = table.getSelectedRow();
				
				if(isCreationTable) creationSelectedRow = selectedRow;
				else destructionSelectedRow = selectedRow;
				
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
	
	protected JButton addTableButton(JPanel panel, String text, boolean enable){
		
		JButton button = new JButton(text);
		button.addActionListener(this);
		button.addFocusListener(this);
		button.setEnabled(enable);
      	panel.add(button);

      	return button;
	}
	
	
	protected void configureTableMap(List<LifecycleItemResource> tableItems, List<LifecycleItemResource> resourceList){

		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(LifecycleItemResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<LifecycleItemResource> tableItems){
		
		hook.clearTable();

		for (LifecycleItemResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1);
		}
	}
	
	protected void addRowFromResource(LifecycleItemResource resource, ITableUpdateHook hook, int row){
		
		boolean bool = false;
		
		if(resource instanceof CreationItemResource) bool = ((CreationItemResource)resource).isFromWarehouse();
		else if(resource instanceof DestructionItemResource) bool = ((DestructionItemResource)resource).isOnlyWhenStocked();
		
		Object[] data = new Object[]{
				
				Resource.getItemInternalNameById(resource.getId()),
				resource.getAmount(),
				imageResource.getIdleNameFromId(resource.getIdleId()),
				bool
			};

			hook.addDataRowToTable(data, row);
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == creationDelete){

			deleteResourcesFromTable(creationTableItems, creationSelectedRow, creationHook);

		}else if(button == creationAdd){

			String warning = addResourceToTableAndMap(true, creationItemCombo, creationAmount, creationIdleCombo, 
					creationCheckbox, creationTableItems, creationHook);
			
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == creationEdit){

			editResourceFromTable(creationItemCombo, creationAmount, creationIdleCombo, 
					creationCheckbox, creationTableItems, creationSelectedRow);
			
		}else if(button == destructionDelete){

			deleteResourcesFromTable(destructionTableItems, destructionSelectedRow, destructionHook);

		}else if(button == destructionAdd){

			String warning = addResourceToTableAndMap(false, destructionItemCombo, destructionAmount, destructionIdleCombo, 
					destructionCheckbox, destructionTableItems, destructionHook);
			
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == destructionEdit){

			editResourceFromTable(destructionItemCombo, destructionAmount, destructionIdleCombo, 
					destructionCheckbox, destructionTableItems, destructionSelectedRow);
			
		}
	}
	
	protected void deleteResourcesFromTable(List<LifecycleItemResource> tableItems, int selectedRow, ITableUpdateHook hook){
		
		if(selectedRow > -1){
			
			tableItems.remove(selectedRow);
			hook.removeRow(selectedRow);
			setDirtyStateAndConfigure(true);
		}
	}
	
	protected String addResourceToTableAndMap(boolean isCreation, JComboBox<String> itemCombo, 
			JSpinner amountSpinner, JComboBox<String> idleCombo, JCheckBox checkbox, 
			List<LifecycleItemResource> tableItems, ITableUpdateHook hook){
		
		ItemResource item = items.get(itemCombo.getSelectedIndex());
		
		int i = 0;
		int row = -1;
		
		for (LifecycleItemResource lifecycleItemResource : tableItems) {
			
			if(lifecycleItemResource.getId() == item.getId()){
				row = i;
				break;
			}
			i++;
		}
		
		LifecycleItemResource resource;
		
		if(isCreation){
			
			resource = new CreationItemResource(checkbox.isSelected());
			
		}else{
			
			resource = new DestructionItemResource(checkbox.isSelected());
			
		}
		
		resource.setId(item.getId());
		int amount = (int)amountSpinner.getValue();
		resource.setAmount((short)amount);
		
		int idleId = idleCombo.getSelectedIndex()-1;
		if(idleId > -1) idleId = imageResource.getIdleByIndex(idleId).getId();		
		resource.setIdleId(idleId);
//		resource.setIdleId(idleCombo.getSelectedIndex()-1);
		
		if(row > -1){
			if(addOverwriteConfirmation()){
				tableItems.remove(row);
				tableItems.add(row, resource);
			}else{
				return null;
			}
			
		}else{
			tableItems.add(resource);
		}
		
		addRowFromResource(resource, hook, row);
		setDirtyStateAndConfigure(true);
		return null;
	}
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	protected void editResourceFromTable(JComboBox<String> itemCombo, JSpinner amountSpinner, JComboBox<String> idleCombo, 
			JCheckBox checkbox, List<LifecycleItemResource> tableItems, int selectedRow){
		
		if(selectedRow > -1){
			
			LifecycleItemResource resource = tableItems.get(selectedRow);
			
			for (ItemResource item : items) {
				
				if(item.getId() == resource.getId()){
					
					itemCombo.setSelectedItem(item.getName());
					break;
				}
			}
			amountSpinner.setValue((int)resource.getAmount());
			idleCombo.setSelectedIndex(imageResource.getIdleIndexFromId(resource.getIdleId(), true));
			
			if(resource instanceof CreationItemResource){
				
				checkbox.setSelected(((CreationItemResource)resource).isFromWarehouse());
			}else{
				
				checkbox.setSelected(((DestructionItemResource)resource).isOnlyWhenStocked());
			}
		}
	}

	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		buildingCreationResource = (BuildingCreationResource) properties.get(PropertyKeys.IMAGE_CREATION_REQUIREMENT);
		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
		
		System.out.println("recieved endframe: " + buildingCreationResource.getEndFrame());
	}

	@Override
	protected void saveData() {
		
		creationRevertTableItems = copyTableItems(creationTableItems);
		destructionRevertTableItems = copyTableItems(destructionTableItems);
		buildingCreationResource.setLifecycleItems(copyTableItems(creationTableItems), copyTableItems(destructionTableItems));
		
	}
	
	protected List<LifecycleItemResource> copyTableItems(List<LifecycleItemResource> tableItems){
		
		ArrayList<LifecycleItemResource> tableItemsCopy = new ArrayList<LifecycleItemResource>();
		
		for (LifecycleItemResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		return null;
	}

	@Override
	protected void newButtonClicked() {
		// Nothing to do
		
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(creationRevertTableItems==null){
			creationTableItems = new ArrayList<LifecycleItemResource>();
			configureTableMap(creationTableItems, buildingCreationResource.getOnlyCreationItems());
		}
		else creationTableItems = copyTableItems(creationRevertTableItems);
		
		if(destructionRevertTableItems==null){
			destructionTableItems = new ArrayList<LifecycleItemResource>();
			configureTableMap(destructionTableItems, buildingCreationResource.getOnlyDestructionItems());
		}
		else destructionTableItems = copyTableItems(destructionRevertTableItems);
		
		displayTable(creationHook, creationTableItems);
		displayTable(destructionHook, destructionTableItems);
	}
	
	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Remove ALL requirements from both tables?", "Clear Tables", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}
	
	@Override
	protected boolean deleteActions() {
		boolean enableButton = true;
		
		creationTableItems = new ArrayList<LifecycleItemResource>();
		destructionTableItems = new ArrayList<LifecycleItemResource>();
		saveData();
        displayTable(creationHook, creationTableItems);
        displayTable(destructionHook, destructionTableItems);
		
		return enableButton;
	}



	@Override
	protected String getHelpText() {
		return new StringBuilder("Creation Requirement Items.\n\n")
						 .append("There are two parts to this form, items that are needed for creation and items that are returned after destruction.\n\n")
						 .append("The top half of the form is for items that are needed for creation.\n")
						 .append("From the Creation Item dropdown combo box, select the warehouse item that is needed for this stage of construction.\n")
						 .append("A worker will make there way to the building, where as an item will be delivered by a warehouse worker.\n")
						 .append("You can select how many of this item is required in the Creation Amount box.\n")
						 .append("The Creation Idle dropdown combo box contains all of the idle sets that have be configured in the 'Idle Image' section.\n")
						 .append("This is used to override the idle set on the previous form, for example, it could be an image with a plank of wood, or a builder pacing, this idle set will be shown after the item/worker from Creation Item has arrived at the building.\n")
						 .append("Selecting <none selected> will not changed the previously configured idle req on the previous form.\n")
						 .append("Finally we have the From Warehouse checkbox, when this is checked the item/worker will come from the warehouse, when it is unchecked the item/worker will return to the warehouse.\n")
						 .append("Note: If you do not return a worker (From Warehouse unchecked) then the worker will be lost in the construction of the building.\n")
						 .append("When you have selected all desired values (Creation Item, Creation Amount, Creation Idle and From Warehouse), check the top Add Button to add these values to the top table.\n\n")
						 .append("The bottom half of the form is for items that are return if the building is marked for destruction at this stage.\n")
						 .append("From the Destruction Item dropdown combo, select the warehouse item/worker that might need to be returned to the warehouse.\n")
						 .append("Select how many should be returned in the Destruction Amount box.\n")
						 .append("The Destruction Idle will override the destruction req set on the previous form, if the item/worker is still pending being returned to the warehouse.\n")
						 .append("If you do not wish to override the previously set destruction req, then select <None selected>\n")
						 .append("The Only When Stocked? checkbox should be checked if the item/worker being return is one that is also a creation item, this will ensure it only gets return if it has already been delivered.\n")
						 .append("If it is unchecked, then the item/worker will always be returned.\n")
						 .append("When all destruction values have been set (Destruction Item, Destruction Amount, Destruction Idle and Only When Stocked?), click the bottom Add button to add it to the bottom table.\n\n")
						 .append("You can edit/remove a previously added row on both tables, by selected the row and then clicking either the Edit or the Remove button above the table").toString();
		
	}

}
