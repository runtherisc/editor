package gui.level;

import java.util.ArrayList;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.ItemResource;
import data.map.resources.LevelResource;
import data.map.resources.LevelTargetResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class LevelTargetsGui extends ChildBaseGui {

	public LevelTargetsGui(String title, JFrame parent) {
		super(title, parent);

	}

	private LevelResource levelResource;
	
	private JTable table;
	private ITableUpdateHook hook;	
	private List<ItemResource> items;
	private JComboBox<String> itemCombo;
	private JSpinner amountSpinner, bronzeSpinner, silverSpinner, goldSpinner;
	private JButton addButton, editButton, deleteButton;
	private int selectedRow = -1;
	private List<LevelTargetResource> tableItems, revertTableItems;
	
	@Override
	protected int addComponents(JFrame frame) {
		
		Object[] destructionNames = new Object[]{"Item", "Amount"};
		int[] sizes = new int[]{150, 80}; 
		
		items = Resource.getWarehouseItemInternalNames();	
		
		String[] itemNames = new String[items.size()];
		for (int i = 0; i < itemNames.length; i++) {
			itemNames[i] = items.get(i).getName();
		}
      	
		JPanel panel = new JPanel();
		
		goldSpinner = addLabelAndNumberSpinnerToPanel(panel, "Gold (seconds)", 0, 0, 9997, 1);
		
		silverSpinner = addLabelAndNumberSpinnerToPanel(panel, "Silver (seconds)", 0, 0, 9998, 1);
		
		frame.add(panel, getNoPaddingGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
		bronzeSpinner = addLabelAndNumberSpinnerToPanel(panel, "Bronze (seconds)", 0, 0, 9999, 2);
		
		configureSpinners(goldSpinner, silverSpinner);
		configureSpinners(silverSpinner, bronzeSpinner);
		configureSpinners(bronzeSpinner, null);
		
		frame.add(panel, getNoPaddingGridBagConstraints(0, 1));
		
		frame.add(new JSeparator(), getNoPaddingGridBagConstraints(0, 2));
		
		panel = new JPanel();
	
		itemCombo = addLabelAndComboToPanel(panel, "Item", 0, 0, itemNames);
		
		amountSpinner = addLabelAndNumberSpinnerToPanel(panel, "Amount", 0, 0, Short.MAX_VALUE, 1);
		amountSpinner.setValue(1);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 3));
		
		panel = new JPanel();
		
      	addButton = addTableButton(panel, "Add", true);
      	editButton = addTableButton(panel, "Edit", false);
      	deleteButton = addTableButton(panel, "Remove", false);
		
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 4));
		
      	table = createTable(destructionNames, sizes, 0, 5);
      	hook = addHookToTable(table);
      	
      	addListenerToTable(table, editButton, deleteButton);
        
        tableItems = new ArrayList<LevelTargetResource>();
        configureTableMap(tableItems);
        displayTable(hook, tableItems);
		
		return 6;
	}
	
	
	
	@Override
	protected void postDrawGui() {

		super.postDrawGui();
		
		initTimeSpinners();
	}
	
	private void initTimeSpinners(){
		
		setFormReady(false);
		if(levelResource.getBronze() >= 2) bronzeSpinner.setValue(levelResource.getBronze());
		else bronzeSpinner.setValue(2);
		
		if(levelResource.getSilver() >= 1) silverSpinner.setValue(levelResource.getSilver());
		else silverSpinner.setValue(1);
		
		goldSpinner.setValue(levelResource.getGold());
		setFormReady(true);
	}



	private void configureSpinners(final JSpinner primary, final JSpinner secondary){
		
		primary.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(secondary!=null){
					int value = (Integer)primary.getValue();
					if((Integer)secondary.getValue() <= value){
						secondary.setValue(value + 1);
					}
					SpinnerNumberModel model = (SpinnerNumberModel)secondary.getModel();
					model.setMinimum(value + 1);
				}
				if(isFormReady())setDirtyStateAndConfigure(true);
				
			}
		});
	}
	
	protected void configureTableMap(List<LevelTargetResource> tableItems){
		
		List<LevelTargetResource> resourceList = levelResource.getTargets();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(LevelTargetResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(ITableUpdateHook hook, List<LevelTargetResource> tableItems){
		
		hook.clearTable();

		for (LevelTargetResource resource : tableItems) {
			
			addRowFromResource(resource, hook, -1);
		}
	}
	
	protected void addRowFromResource(LevelTargetResource resource, ITableUpdateHook hook, int row){
		
		Object[] data = new Object[]{
				
				Resource.getItemInternalNameById(resource.getItem()),
				resource.getAmount()
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
		
		for (LevelTargetResource resource : tableItems) {
			
			if(item.getId() == resource.getItem()){
				if(addOverwriteConfirmation()){
					row = i;
					break;
				}else{
					return null;
				}
			}
			i++;
		}
		
		LevelTargetResource resource = new LevelTargetResource();
		
		resource.setItem(item.getId());
		int amount = (int)amountSpinner.getValue();
		resource.setAmount((short)amount);

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
			
			LevelTargetResource resource = tableItems.get(selectedRow);
			
			for (ItemResource item : items) {
				
				if(item.getId() == resource.getItem()){
					
					itemCombo.setSelectedItem(item.getName());
					break;
				}
			}
			amountSpinner.setValue(resource.getAmount());

		}
	}
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		levelResource = (LevelResource) properties.get(PropertyKeys.LEVEL_RESOURCE);

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<LevelTargetResource>();
			configureTableMap(tableItems);

		}else{
			
			tableItems = copyTableItems(revertTableItems);
		}
		
		displayTable(hook, tableItems);
		
		initTimeSpinners();

	}

	@Override
	protected void newButtonClicked() {
		
		itemCombo.setSelectedIndex(0);
		amountSpinner.setValue(1);

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

		tableItems = new ArrayList<LevelTargetResource>();
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
		levelResource.setTargets(copyTableItems(tableItems));
		
		levelResource.setBronze((int)bronzeSpinner.getValue());
		levelResource.setSilver((int)silverSpinner.getValue());
		levelResource.setGold((int)goldSpinner.getValue());

	}
	
	protected List<LevelTargetResource> copyTableItems(List<LevelTargetResource> tableItems){
		
		ArrayList<LevelTargetResource> tableItemsCopy = new ArrayList<LevelTargetResource>();
		
		for (LevelTargetResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Level Target Items\n\n")
						 .append("This is where you can set the items a player must collect/create in order to complete the level.  You can also set a gold, silver and bronze times for the player to achive.\n\n")
						 .append("The Gold (seconds) box is for the highest accolade.  Enter a value in seconds (8 game ticks at normal speed) that you feel would be achivable, but differcult.\n\n")
						 .append("The Silver (seconds) box is the next target time, for players that failed to achive the gold time, this value is also in seconds and ofcourse must be higher than the gold time.\n\n")
						 .append("The Bronze (seconds) box is the last target time, for players that failed to achive the silver time, this value is also in seconds.  If a player fails to complete the level in this time, they can continue to play, but will not receive any accolade for the level.\n\n")
						 .append("Below the target times is where you can set the items needed to complete the level.\n\n")
						 .append("The Item dropdown combo box contains all the warehouse items.  Select an item that is possible to create or collect with the buildings on this level.\n\n")
						 .append("Select how many of this items is required to complete the level in the amount box.\n\n")
						 .append("When you are happy with the item and it's amount, click the Add button to add it to the table below.\n\n")
						 .append("You can Edit or Remove a previously added row, by selecting the row and using the Edit and Remove buttons.").toString();
	}

}
