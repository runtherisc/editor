package gui.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.Resource;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;

public abstract class BaseMapAmountsGui extends ChildBaseGui {

	public BaseMapAmountsGui(String title, JFrame parent) {
		super(title, parent);

	}
	
	private JSpinner amountSpinner;
	private JButton updateRow;
	
	private JTable table;
	private ITableUpdateHook hook;	
	
	private int selectedRow = -1;

	private List<Integer> items = new ArrayList<Integer>();
	private List<Short> amounts = new ArrayList<Short>();
	
	@Override
	protected int addComponents(JFrame frame) {
		
		Object[] names = new Object[]{"Item", "Max", "Amount"};
		int[] sizes = new int[]{200, 100, 100}; 

		JPanel panel = new JPanel();
		
		amountSpinner = addLabelAndNumberSpinnerToPanel(panel, "Selected Row", 0, 0, 999, 0);
		amountSpinner.setValue(0);
		amountSpinner.setEnabled(false);
		
		updateRow = addTableButton(panel, "update", false);
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
      	table = createTable(names, sizes, 0, 1);
      	hook = addHookToTable(table);
      	
    	addListenerToTable(table, updateRow, amountSpinner);    
    	
    	displayTable(hook);
		
		return 2;
	}
	
	protected void addListenerToTable(final JTable table, final JButton update, final JSpinner amountSpinner){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {

				
				selectedRow = table.getSelectedRow();
				
				SpinnerNumberModel spinnerModel = (SpinnerNumberModel) amountSpinner.getModel();
				
				if(selectedRow==-1){
					
					update.setEnabled(false);
					amountSpinner.setEnabled(false);
					amountSpinner.setValue(0);
					spinnerModel.setMaximum(0);
					setNewButtonEnablement(false);
					
				}else{
	
					update.setEnabled(true);
					amountSpinner.setEnabled(true);
					amountSpinner.setValue((int)amounts.get(selectedRow));
					spinnerModel.setMaximum(getMaxAmountForItem(items.get(selectedRow)));//improve this?
					setNewButtonEnablement(false);
				}
				
			}
        	
        	
        });
        
	}
	
	protected void displayTable(ITableUpdateHook hook){
		
		hook.clearTable();
		
		items = new ArrayList<Integer>();
		amounts = new ArrayList<Short>();

		for (int itemId : getAllItems()) {
			
			short amount = getAmountForItem(itemId);
			
			items.add(itemId);
			
			amounts.add(amount);
			
			addRowFromResource(itemId, amount, hook, -1);
		}
	}
	
	protected void addRowFromResource(int itemId, int amount, ITableUpdateHook hook, int row){
		
		Object[] data = new Object[]{
			
			Resource.getItemInternalNameById(itemId),
			getMaxAmountForItem(itemId),
			amount
		};

		hook.addDataRowToTable(data, row);
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame){
		
		super.otherActions(button, frame);
		
		if(button == updateRow && selectedRow > -1){
			
			setDirtyStateAndConfigure(true);
			
			int amount = (Integer)amountSpinner.getValue();
			
			amounts.set(selectedRow, (short) amount);
			
			addRowFromResource(items.get(selectedRow), amount, hook, selectedRow);
		}
		
	}
	
	@Override
	protected void saveData() {
		
		for (int i = 0; i < amounts.size(); i++) {
			
			addItemAndAmount(items.get(i), amounts.get(i));
		}

	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		displayTable(hook);

	}

	@Override
	protected void newButtonClicked() {
		
		amountSpinner.setValue(0);

	}
	
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all amounts and save?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}

	@Override
	protected boolean deleteActions() {
		
		for (int i = 0; i < amounts.size(); i++) {
			
			amounts.set(i, (short)0);
			addRowFromResource(items.get(i), 0, hook, i);
		}
		
		return false;
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		//nothing to do
		return null;
	}


	protected abstract Set<Integer> getAllItems();

	protected abstract int getMaxAmountForItem(int itemId);

	protected abstract short getAmountForItem(int itemId);
	
	protected abstract void addItemAndAmount(int item, short amount);

}
