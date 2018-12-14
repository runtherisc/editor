package gui.building;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.BuildingItemResource;
import data.map.resources.ItemMakeResource;
import data.map.resources.ItemResource;
import data.map.resources.MakeRequireResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class WarehouseItemMakeGui extends ChildBaseGui {

	public WarehouseItemMakeGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private BuildingItemResource buildingItemResource;
	private List<ItemMakeResource> createTableItems, revertCreateTableItems;
	
	private List<MakeRequireResource> requiredItems;
	
	private JTable requiredTable;
	private ITableUpdateHook requiredHook;	
	private JTable createTable;
	private ITableUpdateHook createHook;	
	private JSpinner createAmountSpinner, requiredAmountSpinner, freqSpinner;
	private List<ItemResource> items;
	private JComboBox<String> itemCombo;
	private JButton addItem, deleteItem, addButton, editButton, deleteButton;
	private int selectedRow = -1;
	private int requiredSelectedRow = -1;
	private int editingRow = -1;
	private String newEntryText = "Editing New Entry";
	private JLabel rowLabel;
	
	@Override
	protected int addComponents(JFrame frame) {
		// amount
		// frequency
		// table items
		// - amount
		// - item
		
		//top table with amount and freq
		
		//linked to the bottom table with item and amount
		//all bottom componets are only selectable when top table item is selected
		
		Object[] requiredNames = new Object[]{"Required Item", "Amount"};
		int[] requiredSizes = new int[]{150, 150}; 
		
		Object[] creationNames = new Object[]{"Row", "Create Amount", "Frequency", "Required Items"};
		int[] creationSizes = new int[]{30, 100, 100, 170}; 
		
		items = Resource.getWarehouseItemInternalNames();	
		
		String[] itemNames = new String[items.size()];
		for (int i = 0; i < itemNames.length; i++) {
			itemNames[i] = items.get(i).getName();
		}
		
		JPanel panel = new JPanel();
		
		createAmountSpinner = addLabelAndNumberSpinnerToPanel(panel, "Create Amount", 0, 0, 999, 1);
		createAmountSpinner.setValue(1);
		
		freqSpinner = addLabelAndNumberSpinnerToPanel(panel, "Frequency (seconds)", 0, 0, 999, 0);
		freqSpinner.setValue(0);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		frame.add(new JSeparator(), getNoPaddingGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
		itemCombo = addLabelAndComboToPanel(panel, "Required Item", 0, 0, itemNames);
		
		requiredAmountSpinner = addLabelAndNumberSpinnerToPanel(panel, "Amount", 0, 0, 9999, 1);
		requiredAmountSpinner.setValue(1);
		
		addItem = addTableButton(panel, "Add", true);
		deleteItem = addTableButton(panel, "Remove", false);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 2));
		
		requiredTable = createTable(requiredNames, requiredSizes, 0, 3);
		requiredHook = addHookToTable(requiredTable);
		
		addRequiredListenerToTable(requiredTable, deleteItem);
		
		frame.add(new JSeparator(), getNoPaddingGridBagConstraints(0, 4));

		panel = new JPanel();
		
		rowLabel = new JLabel(newEntryText);
		rowLabel.setForeground(Color.BLUE);
		
		panel.add(rowLabel);
		
		addButton = addTableButton(panel, "Add", true);
		editButton = addTableButton(panel, "Edit", false);
		deleteButton = addTableButton(panel, "Remove", false);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 5));
		
		
		createTable = createTable(creationNames, creationSizes, 0, 6);
		createHook = addHookToTable(createTable);
		
      	addListenerToTable(createTable, editButton, deleteButton);
        
      	createTableItems = new ArrayList<ItemMakeResource>();
        configureTableMap(createTableItems);
        displayTable(createHook, createTableItems, true);
        
        requiredItems = new ArrayList<MakeRequireResource>();
        
        editingRow = -1;
		
		return 7;
	}
	
	protected void configureTableMap(List<ItemMakeResource> tableItems){
		
		List<ItemMakeResource> resourceList = buildingItemResource.getWarehouseMake();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(ItemMakeResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<ItemMakeResource> tableItems, boolean clearTable){
		
		if(clearTable) hook.clearTable();
		int rowNumber = 0;
		for (ItemMakeResource resource : tableItems) {
			
			int row = clearTable ? -1 : rowNumber;
			addRowFromResource(resource, hook, row, rowNumber);
			rowNumber++;
		}
	}
	
	protected void addRowFromResource(ItemMakeResource resource, ITableUpdateHook hook, int row, int rowNumber){
		
		StringBuilder makeReq = new StringBuilder();
		
		for (MakeRequireResource makeRequireResource : resource.getMakeRequirements()) {
			if(makeReq.length()>0) makeReq.append(", ");
			makeReq.append(Resource.getItemInternalNameById(makeRequireResource.getId()));
		}

		Object[] data = new Object[]{
				
				rowNumber,
				resource.getAmount(),
				resource.getFrequency(),
				makeReq.toString()
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
	
	protected void addRequiredListenerToTable(final JTable table, final JButton delete){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {

				
				requiredSelectedRow = table.getSelectedRow();
				
				if(requiredSelectedRow==-1){
					delete.setEnabled(false);
					
				}else{
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
			
		}else if(button == addItem){
				
			addRequireToTableAndList();
			
		}else if(button == deleteItem){
			
			deleteRequireFromTable();
		}
	}
	
	protected void deleteResourcesFromTable(){
		
		if(selectedRow > -1){
			
			createTableItems.remove(selectedRow);
			createHook.removeRow(selectedRow);
			displayTable(createHook, createTableItems, false);
			editingRow = -1;
			rowLabel.setText(newEntryText);
			setDirtyStateAndConfigure(true);
		}
	}
	
	protected String addResourceToTableAndMap(){
		
		ItemMakeResource resource = new ItemMakeResource();

		int amount = (int)createAmountSpinner.getValue();
		resource.setAmount((short)amount);
		resource.setFrequency((int)freqSpinner.getValue());
		
		List<MakeRequireResource> makeRequirements = new ArrayList<MakeRequireResource>();
		for (MakeRequireResource makeRequireResource : requiredItems) {
			
			makeRequirements.add(makeRequireResource.copy());
		}
		resource.setMakeRequirements(makeRequirements);
		
		requiredItems = new ArrayList<MakeRequireResource>();
		requiredHook.clearTable();
		
		int rowNumber = editingRow == -1 ? createTableItems.size() : editingRow;
		
		System.out.println("editing row" + editingRow);
		
		addRowFromResource(resource, createHook, editingRow, rowNumber);
		
		if(editingRow > -1){
			createTableItems.remove(editingRow);
			createTableItems.add(editingRow, resource);
			
		}else createTableItems.add(resource);
		
		editingRow = -1;
		rowLabel.setText(newEntryText);
		
		setDirtyStateAndConfigure(true);
		return null;
	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			
			ItemMakeResource resource = createTableItems.get(selectedRow);
			
			createAmountSpinner.setValue((int)resource.getAmount());
			freqSpinner.setValue(resource.getFrequency());
			
			requiredItems = new ArrayList<MakeRequireResource>();
			requiredHook.clearTable();
			
			List<MakeRequireResource> makeRequirements = resource.getMakeRequirements();
			
			for (MakeRequireResource makeRequireResource : makeRequirements) {
				
				requiredItems.add(makeRequireResource.copy());
				addRequiredRowFromResource(makeRequireResource, requiredHook, -1);
			}
			
			editingRow = selectedRow;
			rowLabel.setText("Editing Row "+editingRow);

		}
	}
	
	protected void deleteRequiredFromTableAndList(){
		
		if(selectedRow > -1){
			
			requiredItems.remove(selectedRow);
			requiredHook.removeRow(selectedRow);
		}
	}
	
	protected void addRequireToTableAndList(){
		
		ItemResource item = items.get(itemCombo.getSelectedIndex());
		
		int i = 0;
		int row = -1;
		
		for (MakeRequireResource resource : requiredItems) {
			
			if(item.getId() == resource.getId()){

				row = i;
				break;
			}
			i++;
		}
		
		MakeRequireResource resource = new MakeRequireResource();
		int amount = (int)requiredAmountSpinner.getValue();
		resource.setAmount((short)amount);
		resource.setId(item.getId());
		
		if(row > -1){
			requiredItems.remove(row);
			requiredItems.add(row, resource);
			
		}else requiredItems.add(resource);

		addRequiredRowFromResource(resource, requiredHook, row);
	}
	
	protected void addRequiredRowFromResource(MakeRequireResource resource, ITableUpdateHook requiredHook, int row){
		
		Object[] data = new Object[]{
				
				Resource.getItemInternalNameById(resource.getId()),
				resource.getAmount()
			};

		requiredHook.addDataRowToTable(data, row);
	}
	
	protected void deleteRequireFromTable(){
		
		if(requiredSelectedRow > -1){
			
			requiredItems.remove(requiredSelectedRow);
			requiredHook.removeRow(requiredSelectedRow);			
		}
	}
	
	protected List<ItemMakeResource> copyTableItems(List<ItemMakeResource> tableItems){
		
		ArrayList<ItemMakeResource> tableItemsCopy = new ArrayList<ItemMakeResource>();
		
		for (ItemMakeResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}
	
	@Override
	protected void saveData() {
		
		revertCreateTableItems = copyTableItems(createTableItems);
		buildingItemResource.setWarehouseMake(copyTableItems(createTableItems));

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertCreateTableItems==null){			
			
			createTableItems = new ArrayList<ItemMakeResource>();
			configureTableMap(createTableItems);

		}else{
			
			createTableItems = copyTableItems(revertCreateTableItems);
		}
		
		displayTable(createHook, createTableItems, true);

	}

	@Override
	protected void newButtonClicked() {
		createAmountSpinner.setValue(1);
		freqSpinner.setValue(1);
		requiredAmountSpinner.setValue(1);
		itemCombo.setSelectedIndex(0);
		requiredItems = new ArrayList<MakeRequireResource>();
		requiredHook.clearTable();

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

		createTableItems = new ArrayList<ItemMakeResource>();
		saveData();	
        displayTable(createHook, createTableItems, true);
        
        newButtonClicked();//to clear the top table
		
		return enableButton;
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
	
		return null;
	}

	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		buildingItemResource = (BuildingItemResource) properties.get(PropertyKeys.BUILDING_ITEM_RESOURCE);

	}
	
	@Override
	protected String getFrameTitle(){
		
		return getTitle() + Resource.getItemInternalNameById(buildingItemResource.getId());
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Warehouse Make Item\n\n")
						 .append("Using this form you can setup item creation within a warehouse, including what items are required and how often the player is allowed to create the item.\n")
						 .append("You can setup multiple recipes (items required) per item with a different creation frequency for each recipe.\n\n")
						 .append("This form is in three sections, the top section is the fastest frequency the player is allowed to create the item for this recipe and how many items it will create.\n\n")
						 .append("The middle section is where you can set the items required for this recipe and their amounts\n\n")
						 .append("The bottom section shows a table with all the previously added recipes from the top two sections.\n\n")
						 .append("In the Create Amount box, enter how many of the selected item you wish to create.\n\n")
						 .append("In the Frequency (seconds) box, enter the number of seconds (8 game ticks) you would like the player to wait before they can create the item, prior to just creating one (leave as 0 for no wait)\n\n")
						 .append("The Required Item dropdown combo box contains all the warehouse items you have created, select an item that will be required to make the target item, note that this item must be in the warehouse before the target item can be created, so make sure the warehouse can store the item\n\n")
						 .append("In the Amount box, select how many of the required item is needed\n\n")
						 .append("Once the required item and it's amount have been enter, click the Add button next to the Amount box to add it to the middle table.  Repeat this for any other items that are required to make the target item.\n")
						 .append("You can remove a required item from the middle table, by selecting the row and then clicking the Remove button that is next to the Add button in the middle.\n\n")
						 .append("When all required items have been added to the middle table and you have set the desired Create Amount and Frequency, click the Add button above the bottom table to add this recipe to the bottom table.\n\n")
						 .append("Edit or Remove previously added recipes, by selecting the row on the bottom table and using the Edit/Remove buttons above the bottom table").toString();
	}

}
