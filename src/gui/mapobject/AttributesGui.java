package gui.mapobject;

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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.ItemResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemAttResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class AttributesGui extends ChildBaseGui {

	public AttributesGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private String name;
	private MapItemResource mapItemResource;
	
	private JTable table;
	private ITableUpdateHook hook;	
	private List<ItemResource> items;
	private JComboBox<String> itemCombo, actionCombo;
	private JSpinner amountSpinner;
	private JButton addButton, editButton, deleteButton;
	private int selectedRow = -1;
	private List<MapItemAttResource> tableItems, revertTableItems;
	
	private List<Integer> actionIds;
	
	@Override
	protected int addComponents(JFrame frame) {
	
		//all table
		// - item (id)
		// - amount
		// - on Depletion
		
		Object[] destructionNames = new Object[]{"Item", "Amount", "On Depletion"};
		int[] sizes = new int[]{150, 80, 150}; 
		
		items = Resource.getWarehouseItemInternalNames();	
		
		String[] itemNames = new String[items.size()];
		for (int i = 0; i < itemNames.length; i++) {
			itemNames[i] = items.get(i).getName();
		}
      	
		JPanel panel = new JPanel();
	
		itemCombo = addLabelAndComboToPanel(panel, "Item", 0, 0, itemNames);
		
		amountSpinner = addLabelAndNumberSpinnerToPanel(panel, "Amount", 0, 0, Short.MAX_VALUE, 1);
		amountSpinner.setValue(1);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
		actionCombo = addLabelAndComboToPanel(panel, "Action When Depleted", 0, 0, geActionNamesAndConfigureIds());
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
      	addButton = addTableButton(panel, "Add", true);
      	editButton = addTableButton(panel, "Edit", false);
      	deleteButton = addTableButton(panel, "Remove", false);
		
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 2));
		
      	table = createTable(destructionNames, sizes, 0, 3);
      	hook = addHookToTable(table);
      	
      	addListenerToTable(table, editButton, deleteButton);
        
        tableItems = new ArrayList<MapItemAttResource>();
        configureTableMap(tableItems);
        displayTable(hook, tableItems);
		
		return 4;
	}
	
	protected String[] geActionNamesAndConfigureIds(){
		
		List<MapItemActionResource> actions = mapItemResource.getMapItemActionList();

		String[] actionNames = new String[actions.size()+1];
		actionIds = new ArrayList<Integer>();
		actionIds.add(-1);
		actionNames[0] = "<none>";
		int i = 1;
		for (MapItemActionResource mapItemActionResource : actions) {
			actionNames[i] = mapItemActionResource.getInternalName();
			actionIds.add(mapItemActionResource.getId());
			i++;
		}
		
		return actionNames;
	}
	
	protected void configureTableMap(List<MapItemAttResource> tableItems){
		
		List<MapItemAttResource> resourceList = mapItemResource.getMapItemAttList();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(MapItemAttResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<MapItemAttResource> tableItems){
		
		hook.clearTable();

		for (MapItemAttResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1);
		}
	}
	
	protected void addRowFromResource(MapItemAttResource resource, ITableUpdateHook hook, int row){
		
		Object[] data = new Object[]{
				
				Resource.getItemInternalNameById(resource.getId()),
				resource.getAmount(),
				resource.getOnDepletion()==-1 ? "none" : mapItemResource.getMapItemActionById(resource.getOnDepletion()).getInternalName()
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
			
			tableItems.remove(selectedRow);
			hook.removeRow(selectedRow);
			setDirtyStateAndConfigure(true);
		}
	}
	
	protected String addResourceToTableAndMap(){
		
		ItemResource item = items.get(itemCombo.getSelectedIndex());

		int i = 0;
		int row = -1;
		
		for (MapItemAttResource resource : tableItems) {
			
			if(item.getId() == resource.getId()){
				if(addOverwriteConfirmation()){
					row = i;
					break;
				}else{
					return null;
				}
			}
			i++;
		}
		
		MapItemAttResource resource = new MapItemAttResource();
		
		resource.setId(item.getId());
		int amount = (int)amountSpinner.getValue();
		resource.setAmount((short)amount);
		resource.setOnDepletion(actionIds.get(actionCombo.getSelectedIndex()));

		if(row > -1){
			tableItems.remove(row);
			tableItems.add(row, resource);
			
		}else tableItems.add(resource);
		
		addRowFromResource(resource, hook, row);
		setDirtyStateAndConfigure(true);
		return null;
	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			
			MapItemAttResource resource = tableItems.get(selectedRow);
			
			for (ItemResource item : items) {
				
				if(item.getId() == resource.getId()){
					
					itemCombo.setSelectedItem(item.getName());
					break;
				}
			}
			amountSpinner.setValue((int)resource.getAmount());
			actionCombo.setSelectedIndex(getIndexOfId(actionIds, resource.getOnDepletion()));

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
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		mapItemResource = (MapItemResource) properties.get(PropertyKeys.MAP_OBJECT_RESOURCE);
		
		name = (String) properties.get(PropertyKeys.MAP_OBJECT_RESOURCE_NAME);

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<MapItemAttResource>();
			configureTableMap(tableItems);

		}else{
			
			tableItems = copyTableItems(revertTableItems);
		}
		
		displayTable(hook, tableItems);

	}

	@Override
	protected void newButtonClicked() {
		
		itemCombo.setSelectedIndex(0);
		amountSpinner.setValue(1);
		actionCombo.setSelectedIndex(0);

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

		tableItems = new ArrayList<MapItemAttResource>();
		saveData();	
        displayTable(hook, tableItems);
		
		return enableButton;
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		// nothing to do
		return null;
	}

	@Override
	protected void saveData() {
		
		revertTableItems = copyTableItems(tableItems);
		mapItemResource.setMapItemAttributes(copyTableItems(tableItems));

	}
	
	protected List<MapItemAttResource> copyTableItems(List<MapItemAttResource> tableItems){
		
		ArrayList<MapItemAttResource> tableItemsCopy = new ArrayList<MapItemAttResource>();
		
		for (MapItemAttResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}
	
	@Override
	protected String getFrameTitle(){
		
		return getTitle() + name;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Map Item Attributes\n\n")
						 .append("This is where you can set what items are contained within a map item and what happens when the item is depleted.\n\n")
						 .append("Select the desired item from the Item dropdown combo box and enter the maximum amount of items in the Amount box.\n")
						 .append("When this map item is created in game, it will contain all items with the maximum amount.  If the map item is added to the map via the map editor, then you can click on the map item and configure the amount of items it has, but you cannot exceed the maximum amount of items.\n")
						 .append("The Action When Depleted dropdown combox box allows you to select an Action for when the item Amount reaches zero.  These actions are what you set in the Action form on this map item.\n")
						 .append("If you do not want anything to happen when the item is depleted, then select <none> from the Action When Depleted combo.\n\n")
						 .append("When you are happy with your selection, click the Add button to add the Item, Amount and Action When Depleted to the table.\n")
						 .append("You can Edit or Remove previously add rows, by selecting the row and using the Edit or Remove buttons.").toString();
	}

}
