package gui.building;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
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

import data.map.resources.BuildingItemResource;
import data.map.resources.BuildingResource;
import data.map.resources.ItemResource;
import data.map.resources.Resource;
import data.map.resources.WorkerActionResource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class WarehouseStoredItemGui extends ChildBaseGui {

	public WarehouseStoredItemGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private BuildingResource buildingResource;
	
	private JTable table;
	private ITableUpdateHook hook;	
	private JSpinner amountSpinner, carrySpinner;
	private JComboBox<String> itemCombo, workerInCombo, workerOutCombo;
	private List<ItemResource> items, workers;
	private JButton addButton, editButton, deleteButton, makeButton;
	private int selectedRow = -1;
	private int editingRow;
	private List<BuildingItemResource> tableItems, revertTableItems;
	private String onMapWarning;
	
	private List<Integer> importComboIds;
	private JComboBox<String> importCombo;
	private JButton importButton, upButton, downButton;
	
	@Override
	protected int addComponents(JFrame frame) {
		
		//all table items
		// item id (drop down of Warehouse Items) 
		// max amount
		// worker in (all that have images set - preset and disabled if item has images) 
		// worker out (all that have images set - preset and disabled if item has images) 
		// make requirement button (like the requirement item)
		
		Object[] tableNames = new Object[]{"Item", "Amount", "Worker Out", "Worker In", "Carry", "Make Items"};
		int[] sizes = new int[]{150, 80, 150, 150, 80, 80}; 
		
		items = Resource.getWarehouseItemInternalNames();	
		
		String[] itemNames = new String[items.size()];
		for (int i = 0; i < itemNames.length; i++) {
			itemNames[i] = items.get(i).getName();
		}
		
		workers = Resource.getWorkerItemInternalNames();
		
		String[] workersNames = new String[workers.size()];
		for (int i = 0; i < workersNames.length; i++) {
			workersNames[i] = workers.get(i).getName();
		}
		
		JPanel panel = new JPanel();
		
		itemCombo = addLabelAndComboToPanel(panel, "Warehouse Item", 0, 0, itemNames);
		
		itemCombo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				 configureWorkerCombos();
				 if(editingRow > -1){

					 if(tableItems.get(editingRow).getId() == items.get(itemCombo.getSelectedIndex()).getId()){
						 
						 editButton.setText("*Edit");
					 }else{
						 
						 editButton.setText("Edit");
					 }
				 }
				
			}
		});
		amountSpinner = addLabelAndNumberSpinnerToPanel(panel, "Max Amount", 0, 0, 9999, 1);
		amountSpinner.setValue(1);
		
		carrySpinner = addLabelAndNumberSpinnerToPanel(panel, "Worker Carry", 0, 0, 999, 1);
		carrySpinner.setValue(1);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
		workerOutCombo = addLabelAndComboToPanel(panel, "Worker Out", 0, 0, workersNames);
		workerInCombo = addLabelAndComboToPanel(panel, "Worker In", 0, 0, workersNames);
		

		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
		addButton = addTableButton(panel, "Add", true);
		editButton = addTableButton(panel, "Edit", false);
		deleteButton = addTableButton(panel, "Remove", false);
		
        upButton = addTableButton(panel, "Move Up", false);
        downButton = addTableButton(panel, "Move Down", false);
		
      	makeButton = new JButton("Make Item");
      	addGuiButtonAndListener(new WarehouseItemMakeGui("Make ", frame), makeButton);
      	makeButton.setEnabled(false);
    	panel.add(makeButton);
      	
      	frame.add(panel, getSidePaddedGridBagConstraints(0, 3));
      	
      	table = createTable(tableNames, sizes, 0, 4);
      	hook = addHookToTable(table);
        
        frame.add(new JSeparator(), getSidePaddedGridBagConstraints(0, 5));
        
        String[] importItems = configureImageResourceComboItems();
        
        panel = new JPanel();
        

        
		importCombo = addLabelAndComboToPanel(panel, "Import from another warehouse", 0, 0, importItems);
		
		importButton = new JButton("Import");
		importButton.addActionListener(this);
		importButton.addFocusListener(this);
      	panel.add(importButton);
      	
      	if(importItems.length==0){
      		
      		importCombo.setEnabled(false);
      		importButton.setEnabled(false);
      	}
      	
      	frame.add(panel, getSidePaddedGridBagConstraints(0, 6));
      	
      	addListenerToTable(table, editButton, deleteButton, makeButton, upButton, downButton);
        
        tableItems = new ArrayList<BuildingItemResource>();
        configureTableList(tableItems, buildingResource.getBuildingItemMap().values());
        displayTable(hook, tableItems);
		
        configureWorkerCombos();
        
        clearEditingRow();
		
		return 7;
	}
	
	protected void configureWorkerCombos(){
		
		ItemResource item = items.get(itemCombo.getSelectedIndex());
		boolean contains = workers.contains(item);
		if(contains){
			//visual only
			workerInCombo.setSelectedItem(item.getName());
			workerOutCombo.setSelectedItem(item.getName());
		}
		
		workerInCombo.setEnabled(!contains);
		workerOutCombo.setEnabled(!contains);
	}
	
	protected void configureTableList(List<BuildingItemResource> tableItems, Collection<BuildingItemResource> resourceList){

		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(BuildingItemResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<BuildingItemResource> tableItems){
		
		hook.clearTable();

		for (BuildingItemResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1);
		}
	}
	
	protected String[] configureImageResourceComboItems(){
		
		List<Integer> warehouseComboIds = Resource.getFilteredBuildingResourceIds(true);;

		List<String> names = new ArrayList<String>();
		importComboIds = new ArrayList<Integer>();

		for (int id : warehouseComboIds) {
			
			if(buildingResource.getId() != id){
				
				names.add(Resource.getBuildingResourceById(id).getName());
				importComboIds.add(id);
			}			
		}
		
		return names.toArray(new String[]{});
		
	}
	
	protected void swapRows(int topRow){
				
		BuildingItemResource topItem = tableItems.get(topRow);
		tableItems.set(topRow, tableItems.get(topRow+1));
		tableItems.set(topRow+1, topItem);
		
		addRowFromResource(tableItems.get(topRow), hook, topRow);
		addRowFromResource(tableItems.get(topRow+1), hook, topRow+1);
		
		setDirtyStateAndConfigure(true);

	}
	
	protected void addRowFromResource(BuildingItemResource resource, ITableUpdateHook hook, int row){

		String workerIn;
		String workerOut;
		String carry;
		
		if(workers.contains(Resource.getItemResourceById(resource.getId()))){
			workerOut = Resource.getItemInternalNameById(resource.getId());
			workerIn = "N/A";
			carry = "N/A";
		}else{
			WorkerActionResource action = resource.getFirstWorkerActionResource();
			workerOut = Resource.getItemInternalNameById(action.getWorkerout());
			workerIn = Resource.getItemInternalNameById(action.getWorkerin());
			carry = String.valueOf(action.getCarry());
		}
		
		Object[] data = new Object[]{
				
				Resource.getItemInternalNameById(resource.getId()),
				resource.getAmount(),
				workerOut,
				workerIn,
				carry,
				resource.getWarehouseMake().size()
			};

			hook.addDataRowToTable(data, row);
	}
	
	protected void addListenerToTable(final JTable table, final JButton edit, final JButton delete, final JButton makeButton, final JButton upButton, final JButton downButton){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {

				
				selectedRow = table.getSelectedRow();
				
				if(selectedRow==-1){
					
					edit.setEnabled(false);
					delete.setEnabled(false);
					makeButton.setEnabled(false);
					upButton.setEnabled(false);
					downButton.setEnabled(false);
					
				}else{
	
					edit.setEnabled(true);
					delete.setEnabled(true);
					makeButton.setEnabled(true);
					upButton.setEnabled(true);
					downButton.setEnabled(true);
				}
			}
        });
	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		if(childName.equals(WarehouseItemMakeGui.class.getName())){
			
			if(selectedRow>-1){
				addToPassedProperties(PropertyKeys.BUILDING_ITEM_RESOURCE, tableItems.get(selectedRow));
			}else return "Please select a row first";
		}
		
		return null;
	}
	
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == deleteButton){

			deleteResourcesFromTable();

		}else if(button == addButton){

			String warning = addResourceToTableAndList();
			
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == editButton){

			editResourceFromTable();
			
		}else if(button == importButton){
			
			if(onMapWarning == ""){
			
				if(confirmImport()) importRequirement();
			
			}else displayWarning(onMapWarning);
		
		}else if(button == upButton){
			
			if(selectedRow > 0){
				int oldRow = selectedRow;
				swapRows(oldRow-1);
				table.setRowSelectionInterval(0, oldRow-1);
			}			
			
		}else if(button == downButton){
			
			if(selectedRow < tableItems.size()-1){
				int oldRow = selectedRow;
				swapRows(oldRow);
				table.setRowSelectionInterval(0, oldRow+1);
			}	
		}
	}
	
	protected boolean confirmImport(){
		
		String importItem = (String)importCombo.getSelectedItem();
		return JOptionPane.showConfirmDialog(null, "import stored items from "+importItem+"? (Current items will be overwritten)", "Import Requirements?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

	}
	
	protected void importRequirement(){
		
		int id = importComboIds.get(importCombo.getSelectedIndex());
		BuildingResource buidlingResourceSource = Resource.getBuildingResourceById(id);

        tableItems = new ArrayList<BuildingItemResource>();
        configureTableList(tableItems, buidlingResourceSource.getBuildingItemMap().values());
        displayTable(hook, tableItems);
        
		
		setDirtyStateAndConfigure(true);
		
	}
	
	protected void deleteResourcesFromTable(){
		
		if(selectedRow > -1){
			
			if(onMapWarning!=""){
				
				displayWarning(onMapWarning);
				return;
			}
			
			tableItems.remove(selectedRow);
			hook.removeRow(selectedRow);
			setDirtyStateAndConfigure(true);
		}
	}
	
	protected String addResourceToTableAndList(){
		
		ItemResource item = items.get(itemCombo.getSelectedIndex());

		int i = 0;
		int row = -1;
		
		for (BuildingItemResource resource : tableItems) {
			
			if(item.getId() == resource.getId()){
				
				if(onMapWarning!="") return onMapWarning;
				
				if(addOverwriteConfirmation()){
					row = i;
					break;
				}else{
					return null;
				}
			}
			i++;
		}
		
		BuildingItemResource resource;
		
		if(editingRow > -1 && editingRow == row){
			
			resource = tableItems.get(editingRow).copy();
		}else{
			resource = new BuildingItemResource();
		}
			
		
		resource.setId(item.getId());
		int amount = (int)amountSpinner.getValue();
		resource.setAmount((short)amount);
		
		resource.setWorkerActions(new ArrayList<WorkerActionResource>());

		if(!workers.contains(item)){
			
			int carry = (int)carrySpinner.getValue();

			System.out.println("adding worker action");
			resource.addWorkerAction(new WorkerActionResource(
					workers.get(workerInCombo.getSelectedIndex()).getId(), 
					workers.get(workerOutCombo.getSelectedIndex()).getId(),
					(short)carry));
		}

		if(row > -1){
			tableItems.remove(row);
			tableItems.add(row, resource);
			
		}else tableItems.add(resource);
		
		addRowFromResource(resource, hook, row);
		setDirtyStateAndConfigure(true);
		clearEditingRow();
		return null;
	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			
			editingRow = selectedRow;
			editButton.setText("*Edit");
			
			BuildingItemResource resource = tableItems.get(selectedRow);
			
			for (ItemResource item : items) {
				
				if(item.getId() == resource.getId()){
					
					itemCombo.setSelectedItem(item.getName());
					break;
				}
			}
			amountSpinner.setValue((int)resource.getAmount());
			
			if(!resource.getWorkerActions().isEmpty()){
				setWorkerCombo(resource.getFirstWorkerActionResource().getWorkerin(), workerInCombo);
				setWorkerCombo(resource.getFirstWorkerActionResource().getWorkerout(), workerOutCombo);
				carrySpinner.setValue((int)resource.getFirstWorkerActionResource().getCarry());
			}

		}
	}
	
	protected void setWorkerCombo(int id, JComboBox<String> workerCombo){
		
		int i = 0;
		for (ItemResource itemResource : workers) {
			if(itemResource.getId()==id){
				workerCombo.setSelectedIndex(i);
				break;
			}
			i++;
		}

	}
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	protected List<BuildingItemResource> copyTableItems(List<BuildingItemResource> tableItems){
		
		ArrayList<BuildingItemResource> tableItemsCopy = new ArrayList<BuildingItemResource>();
		
		for (BuildingItemResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}
	
	@Override
	protected void saveData() {

		revertTableItems = copyTableItems(tableItems);
		
		buildingResource.clearBuildingItemMap();
		for (BuildingItemResource buildingItemResource : tableItems) {
			buildingResource.putBuildingItem(buildingItemResource.copy(), buildingItemResource.getId());
		}

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<BuildingItemResource>();
			configureTableList(tableItems, buildingResource.getBuildingItemMap().values());

		}else{
			
			tableItems = copyTableItems(revertTableItems);
		}
		
		displayTable(hook, tableItems);


	}

	@Override
	protected void newButtonClicked() {
		
		workerInCombo.setSelectedIndex(0);
		workerOutCombo.setSelectedIndex(0);
		itemCombo.setSelectedIndex(0);
		amountSpinner.setValue(1);
		carrySpinner.setValue(1);
		clearEditingRow();
	}
	
	@Override
	protected void deleteConfirmation(){
		
		if(onMapWarning!=""){
			
			displayWarning(onMapWarning);
			return;
		}
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Remove ALL requirements from the table?", "Clear Tables", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}

	@Override
	protected boolean deleteActions() {
		boolean enableButton = true;

		tableItems = new ArrayList<BuildingItemResource>();
		saveData();	
        displayTable(hook, tableItems);
        clearEditingRow();
		
		return enableButton;
	}
	
	protected void clearEditingRow(){
		
		editingRow = -1;
		editButton.setText("Edit");
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		// nothing
		return null;
	}

	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		buildingResource = (BuildingResource) properties.get(PropertyKeys.BUILDING_RESOURCE);
		onMapWarning = (String) properties.get(PropertyKeys.BUILDING_ONMAPWARNING);

	}
	
	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(makeButton);
		if(childDirty) setDirtyChildren(true);	
		updateButtonLabelWithState(makeButton, "Make Item", childDirty);
		
		displayTable(hook, tableItems);
		
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Warehouse Stored Items\n\n")
						 .append("This is where you can define what items belong in the warehouse, the maximum number of each item allowed, the warehouse worker carrying them and how many they can carry at a time.  Once added you can configure if the warehouse can make the item if required.\n\n")
						 .append("The Warehouse Item dropdown combo box cantains all of the items and workers that you have created that could reside in the warehouse.\n\n")
						 .append("The Max Amount box is where you can set the maximum amount if this item that is allowed in the warehouse (The number of items that the warehouse contains can be set after adding to the map, but still cannot exceed the maximum amount)\n\n")
						 .append("The Worker Carry box is where you can set how many of the item that a warehouse worker can carry when taking the item to buildings or other warehouses (This is ignored if the item is a worker itself)\n\n")
						 .append("The Worker Out and Worker In dropdown combo box contains all the workers you have defined (including workers that do not appear in warehouses)\n")
						 .append("Worker out would be the images you have created of a warehouse worker carrying the item you are configuring.\n")
						 .append("Worker in is the worker returning from delivering the item, so would be empty handed, this worker image set could be used for all items Worker In if wanted.\n\n")
						 .append("When you are happy with your selections, click the Add button to add them to the table.  You can Edit or Remove a previous added row, by selecting the row and then using the Edit/Remove buttons.\n\n")
						 .append("You can change the order of the warehouses stored items, by selecting the item you want to move and then clicking the Move Up and Move Down buttons to change it's position.\n\n")
						 .append("The Make button allows you to define when a item can be created in a warehouse.  Select the row of the item and then click the Make button to enter the form that allows you to configure what is needed.\n\n")
						 .append("You can import stored items from an existing warehouse.  Select the warehouse you want to import from in the combobox and click the import button.\n")
						 .append("The imported items will replace any existing items that are already in the warehouse.").toString();
	}



}
