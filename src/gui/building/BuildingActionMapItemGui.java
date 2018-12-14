package gui.building;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingMapItemActionResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class BuildingActionMapItemGui extends ChildBaseGui {

	public BuildingActionMapItemGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private BuildingActionRequireResource buildingActionRequireResource;
	
	private JComboBox<String> mapItemCombo, mapActionCombo;
	private JButton addButton, editButton, deleteButton;
	private JTable table;
	private ITableUpdateHook hook;	
	private int selectedRow = -1;
	private List<BuildingMapItemActionResource> tableItems, revertTableItems;
	
	private List<MapItemResource> mapItems;
	
	@Override
	protected int addComponents(JFrame frame) {
		
		Object[] destructionNames = new Object[]{"Map Item", "Map Item Action"};
		int[] sizes = new int[]{200, 250}; 
			
		String[] mapItemNames = new String[mapItems.size()];
		for (int i = 0; i < mapItemNames.length; i++) {
			mapItemNames[i] = mapItems.get(i).getName();
		}
      	
		JPanel panel = new JPanel();
	
		mapItemCombo = addLabelAndComboToPanel(panel, "Map Item", 0, 0, mapItemNames);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
		mapActionCombo = addLabelAndComboToPanel(panel, "Map Item Action", 0, 0, new String[]{});
		
		mapItemCombo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				configureAction();
				
			}
		});
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 1));
		
		panel = new JPanel();
		
      	addButton = addTableButton(panel, "Add", true);
      	editButton = addTableButton(panel, "Edit", false);
      	deleteButton = addTableButton(panel, "Remove", false);
		
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 2));
		
      	table = createTable(destructionNames, sizes, 0, 3);
      	hook = addHookToTable(table);
      	
      	addListenerToTable(table, editButton, deleteButton);
        
        tableItems = new ArrayList<BuildingMapItemActionResource>();
        configureTableMap(tableItems);
        displayTable(hook, tableItems);
        
        configureAction();
		
		return 4;
	}
	
	protected void configureAction(){
		
		int index = mapItemCombo.getSelectedIndex();
		MapItemResource mapItemResource = null;			
		mapItemResource = mapItems.get(index);
		String[] mapItemActionNames = getActionFromMapItem(mapItemResource);
		mapActionCombo.removeAllItems();
		for (String name : mapItemActionNames) {
			mapActionCombo.addItem(name);
		}
	}
	
	protected String[] getActionFromMapItem(MapItemResource mapItemResource){
		
		List<String> mapItemActions = new ArrayList<String>();
		
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
	
	protected void configureTableMap(List<BuildingMapItemActionResource> tableItems){
		
		List<BuildingMapItemActionResource> resourceList = buildingActionRequireResource.getBuildingMapItemActionResource();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(BuildingMapItemActionResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<BuildingMapItemActionResource> tableItems){
		
		hook.clearTable();

		for (BuildingMapItemActionResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1);
		}
	}
	
	protected void addRowFromResource(BuildingMapItemActionResource resource, ITableUpdateHook hook, int row){
		
		int mapItemId = resource.getMapItem();
		MapItemResource mapItemResource = Resource.getMapItemResourceById(mapItemId);

		Object[] data = new Object[]{
				
				 mapItemResource.getName(),
				 resource.getAction()==-1 ? "<none>" : mapItemResource.getMapItemActionById(resource.getAction()).getInternalName()
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
		
		int mapItemIndex = mapItemCombo.getSelectedIndex();
		int mapActionIndex = mapActionCombo.getSelectedIndex();
		
		MapItemResource mapItemResource = mapItems.get(mapItemIndex);
		
		int mapItemId = mapItemResource.getId();
		int mapActionId;
		

		MapItemActionResource action = mapItemResource.getMapItemActionByIndex(mapActionIndex);
			
//			if(action.getMapitem() > -1) return "map item cannot change into another map item when worked on";
			
		mapActionId = action.getId();

		
		for (BuildingMapItemActionResource resource : tableItems) {
			
			if(resource.getMapItem()==mapItemId && resource.getAction()==mapActionId){
				return "entry already present";
			}
		}
		
	
		
		BuildingMapItemActionResource resource = new BuildingMapItemActionResource();
		
		resource.setMapItem(mapItemId);
		resource.setAction(mapActionId);
		
		//already checked that it's not duped
		tableItems.add(resource);
		
		addRowFromResource(resource, hook, -1);
		
		setDirtyStateAndConfigure(true);

		return null;
	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			
			BuildingMapItemActionResource resource = tableItems.get(selectedRow);
			
			MapItemResource selectedMapItemResource = Resource.getMapItemResourceById(resource.getMapItem());
			
			int i = 0;
			
			for (MapItemResource mapItemResource : mapItems) {
				
				if(selectedMapItemResource.getId() == selectedMapItemResource.getId()){
				
					mapItemCombo.setSelectedIndex(i);
					//cannot have dupe name so should be safe

					mapActionCombo.setSelectedItem(mapItemResource.getMapItemActionById(resource.getAction()).getInternalName());

					break;
				
				}
				i++;
			}
			

		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		buildingActionRequireResource = (BuildingActionRequireResource) properties.get(PropertyKeys.BUILDING_ACTION_REQUIRE_RESOURCE);

		mapItems = (List<MapItemResource>) properties.get(PropertyKeys.BUILDING_ACTION_REQUIRE_MAPITEMS);
	}
	
	@Override
	protected void saveData() {

		revertTableItems = copyTableItems(tableItems);
		buildingActionRequireResource.setBuildingMapItemActionResource(copyTableItems(tableItems));

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<BuildingMapItemActionResource>();
			configureTableMap(tableItems);

		}else{
			
			tableItems = copyTableItems(revertTableItems);
		}
		
		displayTable(hook, tableItems);

	}
	
	protected List<BuildingMapItemActionResource> copyTableItems(List<BuildingMapItemActionResource> tableItems){
		
		ArrayList<BuildingMapItemActionResource> tableItemsCopy = new ArrayList<BuildingMapItemActionResource>();
		
		for (BuildingMapItemActionResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}

	@Override
	protected void newButtonClicked() {
		
		mapItemCombo.setSelectedIndex(0);
		mapActionCombo.setSelectedIndex(0);

	}

	@Override
	protected boolean deleteActions() {

		boolean enableButton = true;

		tableItems = new ArrayList<BuildingMapItemActionResource>();
		saveData();	
        displayTable(hook, tableItems);
		
		return enableButton;
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		//
		return null;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Building Action Map Items\n\n")
						 .append("On this form you can set the map items and the map item's action to be used.  You can have multiple map items and/or the same map item with a different action selected.\n\n")
						 .append("From the Map Item dropdown combo box select a map item.\n\n")
						 .append("From the Map Item Action dropdown combo box select a map item action, these are the actions you set against the map item.\n")
						 .append("When you are happy with your Map Item / Map Item Action selection, click Add to add it to the table.\n\n")
						 .append("You can Edit or Remove a previously added row, by selecting the row and using the Edit and Remove buttons.").toString();
	}

}
