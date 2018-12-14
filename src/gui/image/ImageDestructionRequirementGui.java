package gui.image;

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
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.BuildingLifecycleResource;
import data.map.resources.DestructionItemResource;
import data.map.resources.ImageResource;
import data.map.resources.ItemResource;
import data.map.resources.LifecycleItemResource;
import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class ImageDestructionRequirementGui extends ChildBaseGui{

	public ImageDestructionRequirementGui(String title, JFrame parent) {
		super(title, parent);
	}

	private ImageResource imageResource;
	private BuildingLifecycleResource destructionResource;
	
	private JTable table;
	private ITableUpdateHook hook;	
	private JComboBox<String> defaultIdleCombo, itemCombo, overrideIdleCombo;
	private JSpinner amountSpinner;
	private int selectedRow = -1;
	
	private List<LifecycleItemResource> tableItems, revertTableItems;
	
	List<ItemResource> items;

	private JButton destructionAdd, destructionEdit, destructionDelete;
//	private JButton destructionAdd= new JButton("Add"), destructionEdit = new JButton("Edit"), destructionDelete= new JButton("Remove");

	boolean ignoreDefaultCombo;
	
	@Override
	protected int addComponents(JFrame frame) {

		Object[] destructionNames = new Object[]{"Item", "Amount", "Idle Override"};
		int[] sizes = new int[]{150, 80, 150}; 
		
		items = Resource.getWarehouseItemInternalNames();	
		
		String[] itemNames = new String[items.size()];
		for (int i = 0; i < itemNames.length; i++) {
			itemNames[i] = items.get(i).getName();
		}
		
		String[] idleNames = imageResource.getIdleNames(true);	
		
		JPanel panel = new JPanel();
		
		defaultIdleCombo = addLabelAndComboToPanel(panel, "Default Idle", 0, 0, idleNames);
		
		defaultIdleCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(!ignoreDefaultCombo) setDirtyStateAndConfigure(true);
			}
		});
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		frame.add(new JSeparator(), getSidePaddedGridBagConstraints(0, 1));
      	
		panel = new JPanel();
	
		itemCombo = addLabelAndComboToPanel(panel, "Item", 0, 0, itemNames);
		
		amountSpinner = addLabelAndNumberSpinnerToPanel(panel, "Amount", 0, 0, 999, 1);
		amountSpinner.setValue(1);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 2));
		
		panel = new JPanel();
		
		overrideIdleCombo = addLabelAndComboToPanel(panel, "Idle Override", 0, 0, idleNames);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 3));
		
		panel = new JPanel();
		
      	destructionAdd = addTableButton(panel, "Add", true);
      	destructionEdit = addTableButton(panel, "Edit", false);
      	destructionDelete = addTableButton(panel, "Remove", false);
		
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 4));
		
      	table = createTable(destructionNames, sizes, 0, 5);
      	hook = addHookToTable(table);
      	
      	addListenerToTable(table, destructionEdit, destructionDelete);
        
        tableItems = new ArrayList<LifecycleItemResource>();
        configureTableMap(tableItems, destructionResource);
        displayTable(hook, tableItems);
		
		return 6;
	}
	
	
	
	@Override
	protected void postDrawGui() {
		super.postDrawGui();

		ignoreDefaultCombo = true;
		if(destructionResource!=null){ 
			defaultIdleCombo.setSelectedIndex(imageResource.getIdleIndexFromId(destructionResource.getIdleId(), true));
		}
		ignoreDefaultCombo = false;
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
	
	protected void configureTableMap(List<LifecycleItemResource> tableItems, BuildingLifecycleResource buildingLifecycleResource){

		if(buildingLifecycleResource==null) return;
		
		List<LifecycleItemResource> resourceList = buildingLifecycleResource.getLifecycleItems();
		
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
		
		Object[] data = new Object[]{
				
				Resource.getItemInternalNameById(resource.getId()),
				resource.getAmount(),
				imageResource.getIdleNameFromId(resource.getIdleId()),
			};

			hook.addDataRowToTable(data, row);
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == destructionDelete){

			deleteResourcesFromTable(tableItems, selectedRow, hook);

		}else if(button == destructionAdd){

			String warning = addResourceToTableAndMap(itemCombo, amountSpinner, overrideIdleCombo, 
					tableItems, hook);
			
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == destructionEdit){

			editResourceFromTable(itemCombo, amountSpinner, overrideIdleCombo, 
					tableItems, selectedRow);
			
		}
	}
	
	protected void deleteResourcesFromTable(List<LifecycleItemResource> tableItems, int selectedRow, ITableUpdateHook hook){
		
		if(selectedRow > -1){
			
			tableItems.remove(selectedRow);
			hook.removeRow(selectedRow);
			setDirtyStateAndConfigure(true);
		}
	}
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	protected String addResourceToTableAndMap(JComboBox<String> itemCombo, 
			JSpinner amountSpinner, JComboBox<String> idleCombo, 
			List<LifecycleItemResource> tableItems, ITableUpdateHook hook){
		
		ItemResource item = items.get(itemCombo.getSelectedIndex());
		
		int i = 0;
		int row = -1;
		
		System.out.println("table items "+tableItems.size());
		
		for (LifecycleItemResource lifecycleItemResource : tableItems) {
			
			if(lifecycleItemResource.getId() == item.getId()){
				if(addOverwriteConfirmation()){
					row = i;
					break;
				}else{
					return null;
				}
			}
			i++;
		}
		
		LifecycleItemResource resource = new DestructionItemResource();
		
		resource.setId(item.getId());
		int amount = (int)amountSpinner.getValue();
		resource.setAmount((short)amount);
		
		int idleId = idleCombo.getSelectedIndex()-1;
		if(idleId > -1) idleId = imageResource.getIdleByIndex(idleId).getId();		
		resource.setIdleId(idleId);
		
		if(row > -1){
			tableItems.remove(row);
			tableItems.add(row, resource);
			
		}else{
			tableItems.add(resource);
		}
		
		addRowFromResource(resource, hook, row);
		setDirtyStateAndConfigure(true);
		return null;
	}
	
	protected void editResourceFromTable(JComboBox<String> itemCombo, JSpinner amountSpinner, JComboBox<String> idleCombo, 
			List<LifecycleItemResource> tableItems, int selectedRow){
		
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

		}
	}

	@Override
	protected void passedBundle(Map<String, Object> properties) {

		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
		
		destructionResource = imageResource.getDestructionResource();

	}

	@Override
	protected void saveData() {
		
		if(destructionResource==null){
			destructionResource = new BuildingLifecycleResource();
			imageResource.setDestructionResource(destructionResource);
		}
		
		int idleId = defaultIdleCombo.getSelectedIndex()-1;
		if(idleId > -1) idleId = imageResource.getIdleByIndex(idleId).getId();		
		destructionResource.setIdleId(idleId);
		
		revertTableItems = copyTableItems(tableItems);
		destructionResource.setLifecycleItems(copyTableItems(tableItems));
		
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
		if(defaultIdleCombo.getSelectedIndex()==0) return "A default idle must be selected";
		return null;
	}

	@Override
	protected void newButtonClicked() {
		
		if(defaultIdleCombo.getSelectedIndex() > 0){
			defaultIdleCombo.setSelectedIndex(0);
			setDirtyStateAndConfigure(true);
		}
		
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(destructionResource!=null){
			defaultIdleCombo.setSelectedIndex(imageResource.getIdleIndexFromId(destructionResource.getIdleId(), true));
		}else{
			defaultIdleCombo.setSelectedIndex(0);
		}
		
		if(revertTableItems==null){
			tableItems = new ArrayList<LifecycleItemResource>();
			configureTableMap(tableItems, destructionResource);
		}
		else tableItems = copyTableItems(revertTableItems);

		displayTable(hook, tableItems);
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

		tableItems = new ArrayList<LifecycleItemResource>();
		saveData();
        displayTable(hook, tableItems);
		
		return enableButton;
	}



	@Override
	protected String getHelpText() {
		return new StringBuilder("Destruction Requirements.\n\n")
						 .append("The destruction requirements allow you to configure everything that is returned to the warehouse after a completed building is destroyed.\n\n")
						 .append("All idle sets that have been configured in the 'Idle Image' section will appear in the Default Idle and the Idle Override dropdown combo boxes.\n")
						 .append("The Default Idle box must have a idle set selected, this will be used whenever a Idle Override is not set.\n\n")
						 .append("Select a warehouse item/worker from the Item dropdoen combo box, then select how many to return in the Amount box.\n")
						 .append("If you want the default Idle to be overridden, select an idle from the Idle Override dropdown combo box, or select <none selected> to use the default idle.\n")
						 .append("When you are happy with your selection (Item, amount and idle override), click the Add button to add it to the table.\n\n")
						 .append("You can edit or remove table entries by selecting a row and then using the Edit or Remove buttons.").toString();
	}

}
