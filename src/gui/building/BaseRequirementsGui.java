package gui.building;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingMapItemActionResource;
import data.map.resources.ItemResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import data.map.resources.WorkerActionResource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public abstract class BaseRequirementsGui extends ChildBaseGui {

	public BaseRequirementsGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private String name;
	private JTable table;
	private ITableUpdateHook hook;	
	private JSpinner amountSpinner, carrySpinner;
	private JComboBox<String> itemCombo, workerInCombo, workerOutCombo;
	private JButton addButton = new JButton("Add"), editButton = new JButton("Edit"), deleteButton = new JButton("Remove");
	private JButton mapItemButton;
	
	private int editingRow = -1;
	private JLabel editingLabel;
	private int selectedRow = -1;
	private List<MapItemResource> mapItems;
	private List<Integer> items, workers;
	
	private boolean hasArea;
	
	List<MapItemResource> mapItemsToPass;
	
	@Override
	protected int addComponents(JFrame frame) {
		
		//require/produce gui: all table items
		//amount
		//carry
		//item
		//worker in //optional for mine on require (must have a map item)
		//worker out
		//map item (if not set, then warehouse is used)
		//map item's action
		
		//*either worker in/out OR map item/action OR both, but never neither	
		
		Object[] tableNames = new Object[]{"Row", "Item", "Amount", "Carry", "Worker Out", "Worker In", "Map Items"};
		int[] sizes = new int[]{80, 150, 80, 80, 150, 150, 80}; 
		
		items = new ArrayList<Integer>();
		String[] itemNames = initItemNames(items);
		
		List<ItemResource> resourceWorkers = Resource.getWorkerItemInternalNames();
		workers = new ArrayList<Integer>();
		
		String[] workersNames = new String[resourceWorkers.size()+1];
		workersNames[0] = "<none>";
		workers.add(-1);
		for (int i = 1; i < workersNames.length; i++) {
			workersNames[i] = resourceWorkers.get(i-1).getName();
			workers.add(resourceWorkers.get(i-1).getId());
		}
		
		mapItems = Resource.getMapItemResourceList();
		
		String[] mapItemNames = new String[mapItems.size()+1];
		mapItemNames[0] = "<none>";
		for (int i = 1; i < mapItemNames.length; i++) {
			mapItemNames[i] = mapItems.get(i-1).getName();
		}
		
		JPanel panel = new JPanel();
		
		itemCombo = addLabelAndComboToPanel(panel, getComponetNamePrefix()+" Item", 0, 0, itemNames);
		
		itemCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				configureWorkerCombos();
				
			}
			
		});
		
		amountSpinner = addLabelAndNumberSpinnerToPanel(panel, getComponetNamePrefix()+" Amount", 0, 0, 999, 1);
		amountSpinner.setValue(1);
		
		carrySpinner = addLabelAndNumberSpinnerToPanel(panel, getComponetNamePrefix()+" Carry", 0, 0, 999, 1);
		carrySpinner.setValue(1);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
		workerOutCombo = addLabelAndComboToPanel(panel, getComponetNamePrefix()+" Worker Out", 0, 0, workersNames);
		workerInCombo = addLabelAndComboToPanel(panel, getComponetNamePrefix()+" Worker In", 0, 0, workersNames);
			
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
		editingLabel = new JLabel();
		editingLabel.setForeground(Color.BLUE);
		panel.add(editingLabel);
		
		addButton = addTableButton(panel, "Add", true);
		editButton = addTableButton(panel, "Edit", false);
		deleteButton = addTableButton(panel, "Remove", false);
		
      	mapItemButton = new JButton("Map Items");
      	addGuiButtonAndListener(new BuildingActionMapItemGui(getComponetNamePrefix() + " Map Items", frame), mapItemButton);
    	panel.add(mapItemButton);
    	mapItemButton.setEnabled(false);
      	
      	frame.add(panel, getSidePaddedGridBagConstraints(0, 2));
      	
      	table = createTable(tableNames, sizes, 0, 3);
      	hook = addHookToTable(table);
      	
      	addListenerToTable(table, editButton, deleteButton, mapItemButton);
        
        clearTable();
        configureTableList();
        displayTable(getTableItems());
        
        configureWorkerCombos();
        
        editingRow = -1;
        updateEditingLabel();

		return 4;
	}
	
	protected void updateEditingLabel(){
		
		if(editingRow==-1) editingLabel.setText("Editing New Entry");
		else editingLabel.setText("Editing "+(editingRow + 1));
	}
	
	protected void configureWorkerCombos(){
		
		int itemId = items.get(itemCombo.getSelectedIndex());
		
		ItemResource item = Resource.getItemResourceById(itemId);
		boolean contains = workers.contains(itemId) && itemId > -1;
		if(contains){
			//visual only
			workerInCombo.setSelectedItem(item.getName());
			workerOutCombo.setSelectedItem(item.getName());
		}
		
		workerInCombo.setEnabled(!contains);
		workerOutCombo.setEnabled(!contains);
	}
	
	protected void displayTable(List<? extends BuildingActionRequireResource> tableItems){
		
		hook.clearTable();
		
		int i = 1;

		for (BuildingActionRequireResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1, i);
			
			i++;
		}
	}
	
	protected void addRowFromResource(BuildingActionRequireResource resource, ITableUpdateHook hook, int row, int rowId){
	
		String itemName = Resource.getItemInternalNameById(resource.getItem());
		String amount = String.valueOf(resource.getAmount());
		String workerIn = "<none>";
		String workerOut = "<none>";
		String carry = "N/A";
//		String mapItem = "<none>";
//		String mapItemAction = "<none>";
		
		WorkerActionResource workerAction = resource.getFirstWorkerAction();
		
		if(workerAction!=null){
			workerIn = Resource.getItemInternalNameById(workerAction.getWorkerin()==-1 ? resource.getItem() : workerAction.getWorkerin());
			workerOut = Resource.getItemInternalNameById(workerAction.getWorkerout());
			carry = String.valueOf(workerAction.getCarry());
		}
		
//		BuildingMapItemActionResource firstMapItem = resource.getFirstBuildingMapItemActionResource();
//		if(firstMapItem!=null){
//			
//			int mapItemId = firstMapItem.getMapItem();
//			MapItemResource mapItemResource = Resource.getMapItemResourceById(mapItemId);
//			mapItem = mapItemResource.getName();
//			
//			mapItemAction = mapItemResource.getMapItemActionByIndex(firstMapItem.getAction()).getInternalName();
			
			if(resource.getItem()==-1){
				itemName = "N/A";
				amount = "N/A";
				carry = "N/A";
			}
//		}
			
		int mapItems = 0;
		if(resource.getBuildingMapItemActionResource()!=null) mapItems = resource.getBuildingMapItemActionResource().size();
		
		Object[] data = new Object[]{

				rowId,
				itemName,
				amount,
				carry,
				workerOut,
				workerIn,
				mapItems
				
			};

			hook.addDataRowToTable(data, row);
	}
	
	protected void addListenerToTable(final JTable table, final JButton edit, final JButton delete, final JButton mapItemButton){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				selectedRow = table.getSelectedRow();
				
				if(selectedRow==-1){
					
					edit.setEnabled(false);
					delete.setEnabled(false);
					mapItemButton.setEnabled(false);
					
				}else{
					
					configMapItemsFromSelectedRow();
	
					edit.setEnabled(true);
					delete.setEnabled(true);
					mapItemButton.setEnabled(!mapItemsToPass.isEmpty() && hasArea);
				}	
			}
        });
	}
	
	protected void configMapItemsFromSelectedRow(){
		
		mapItemsToPass = new ArrayList<MapItemResource>();
		
		int item = getTableItems().get(selectedRow).getItem();
		//:|
		boolean isProduced = items.get(0)==-1;
		
		for(MapItemResource mapItem : Resource.getMapItemResourceList()){
			
			if((item==-1 && isProduced) || (!isProduced && mapItem.getItemIdsFromAttrubutes().contains(item))
					&& !mapItem.getMapItemActionList().isEmpty()){
				
				mapItemsToPass.add(mapItem);
			}
			
		}
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
			
		}
	}
	
	protected void deleteResourcesFromTable(){
		
		if(selectedRow > -1){
			
			getTableItems().remove(selectedRow);
			setDirtyStateAndConfigure(true);
			displayTable(getTableItems());
			editingRow = -1;
			updateEditingLabel();
		}
	}
	
	protected String addResourceToTableAndList(){
		
		int workerInIndex = workerInCombo.getSelectedIndex();
		int workerOutIndex = workerOutCombo.getSelectedIndex();
		
		if((workerInIndex==0 && workerOutIndex!=0)||(workerInIndex!=0 && workerOutIndex==0))
			return "either both worker in and out must be set or neither";

		int item = items.get(itemCombo.getSelectedIndex());
		
		int i = 0;
		
		if(item!=-1){
			
			for (BuildingActionRequireResource resource : getTableItems()) {
				
				//item must be unique if it is set
				if(i != editingRow && item == resource.getItem()){
					if(addOverwriteConfirmation(Resource.getItemInternalNameById(item))){
						editingRow = i;
						break;
					}else{
						return null;
					}
				}
				i++;
			}
		}
		
		List<BuildingMapItemActionResource> mapItems = new ArrayList<BuildingMapItemActionResource>();
		
		if(editingRow > -1){
			
			mapItems = getTableItems().get(editingRow).getBuildingMapItemActionResource();
		}
		
		BuildingActionRequireResource resource = createNewResource();
		
		resource.setBuildingMapItemActionResource(mapItems);
		
		resource.setItem(item);
		int amount = (int)amountSpinner.getValue();
		resource.setAmount((short)amount);
		int carry = (int)carrySpinner.getValue();
		
		if(workerInIndex!=0 && workerOutIndex!=0){
			boolean contains = workers.contains(item) && item > -1;
			int workerInId = contains ? -1 : workers.get(workerInIndex);
			resource.addWorkerAction(new WorkerActionResource(workerInId, workers.get(workerOutIndex), (short)carry));
		}

		int rowId;
		
		if(editingRow > -1){
			getTableItems().remove(editingRow);
			addRowToTableItems(editingRow, resource);
			rowId = editingRow + 1;
			
		}else{
			addRowToTableItems(-1, resource);
			rowId = table.getRowCount() + 1;
		}
		
		addRowFromResource(resource, hook, editingRow, rowId);
		setDirtyStateAndConfigure(true);
		editingRow = -1;
		updateEditingLabel();
		return null;
	}
	
	protected boolean addOverwriteConfirmation(String name){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing Item; "+name+" instead?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			
			BuildingActionRequireResource resource = getTableItems().get(selectedRow);
			
			if(resource.getItem()!=-1){
				
				itemCombo.setSelectedItem(Resource.getItemInternalNameById(resource.getItem()));

				amountSpinner.setValue((int)resource.getAmount());
			}
			
			WorkerActionResource workerAction = resource.getFirstWorkerAction();
			
			if(workerAction!=null){
				
				if(resource.getItem()!=-1)
					carrySpinner.setValue((int)workerAction.getCarry());
				
				workerInCombo.setSelectedItem(Resource.getItemInternalNameById(workerAction.getWorkerin()));
				workerOutCombo.setSelectedItem(Resource.getItemInternalNameById(workerAction.getWorkerout()));
			}else{
				workerInCombo.setSelectedIndex(0);
				workerOutCombo.setSelectedIndex(0);
			}
			
			editingRow = selectedRow;
			updateEditingLabel();
			
		}
	}
	
	protected String[] getActionFromMapItem(MapItemResource mapItemResource){
		
		List<String> mapItemActions = new ArrayList<String>();
		
		mapItemActions.add("none");
		
		if(mapItemResource!=null){
			
			List<MapItemActionResource> actionList = mapItemResource.getMapItemActionList();
			if(actionList!=null){
				
				for (MapItemActionResource mapItemActionResource : actionList) {
					
					mapItemActions.add(mapItemActionResource.getInternalName());
				}
			}
		}
		
		return mapItemActions.toArray(new String []{});
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		name = (String) properties.get(PropertyKeys.BUILDING_ACTION_RESOURCE_NAME);
		
		hasArea = (boolean) properties.get(PropertyKeys.BUILDING_ACTION_AREA);

	}
	
	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Remove ALL requirements from the table?", "Clear Tables", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}

	@Override
	protected void newButtonClicked() {

		amountSpinner.setValue(1);
		carrySpinner.setValue(1);
		itemCombo.setSelectedIndex(0);
		workerInCombo.setSelectedIndex(0);
		workerOutCombo.setSelectedIndex(0);
		
		editingRow = -1;
		updateEditingLabel();
	}

	@Override
	protected boolean deleteActions() {
		boolean enableButton = true;

		clearTable();
		saveData();	
        displayTable(getTableItems());
		
		return enableButton;
	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		if(selectedRow > -1 && !mapItemsToPass.isEmpty()){

			BuildingActionRequireResource resource = getTableItems().get(selectedRow);
			
			addToPassedProperties(PropertyKeys.BUILDING_ACTION_REQUIRE_RESOURCE, resource);
			
			addToPassedProperties(PropertyKeys.BUILDING_ACTION_REQUIRE_MAPITEMS, mapItemsToPass);

		}else return "Row must be selected from the table first";
		
		return null;
	}

	@Override
	protected void dirtyButtonUpdate() {

		super.dirtyButtonUpdate();
		
		displayTable(getTableItems());
	}



	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		
		int i = 1;
		
		for (BuildingActionRequireResource resource : getTableItems()) {
			
			if(resource.getBuildingMapItemActionResource()==null || resource.getBuildingMapItemActionResource().isEmpty()){

				if(resource.getWorkerActions()==null || resource.getWorkerActions().isEmpty()){
					
					return "Either worker in/out OR Map item needs to be set on row "+i;
				}
				if(resource.getItem()==-1){
					
					return "Either Map item or warehouse item needs to be set on row "+i;
				}
			}
			i++;
		}
		return null;
	}


	
	@Override
	protected String getFrameTitle(){
		
		return getTitle() + " for " + name;
	}
	
	protected abstract String getComponetNamePrefix();
	
//	protected abstract List<? extends BuildingActionRequireResource> getResourceList();
	
	protected abstract BuildingActionRequireResource createNewResource();
	
//	protected abstract BuildingActionRequireResource copyResource(BuildingActionRequireResource source);
	
	protected abstract void clearTable();
	
	protected abstract List<? extends BuildingActionRequireResource> getTableItems();
	
	protected abstract void configureTableList();
	
	protected abstract String[] initItemNames(List<Integer> items);
	
	protected abstract void addRowToTableItems(int row, BuildingActionRequireResource resource);

}
