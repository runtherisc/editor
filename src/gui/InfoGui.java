package gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import data.map.resources.InfoResource;
import data.map.resources.Resource;
import data.map.resources.ResourceConstants;

public abstract class InfoGui extends ChildBaseGui {

	public InfoGui(String title, JFrame parent, boolean requiresDescription) {
		super(title, parent);
		this.requiresDescription = requiresDescription;
	}
	
	private HashMap<String, String> localeTextMap;
	
	private JTextField titleTxt, descriptionTxt, localeTxt;
	private JButton addTexts, editTexts, deleteTexts;
	private JTable table;
	private ITableUpdateHook hook;
	private int selectedRow = -1;
	private FocusListener localeFocusListener;
	private boolean requiresDescription;

	@Override
	protected String validatePreSaveDataAndReturnIssues() {

		return null;
	}

	@Override
	protected int addComponents(JFrame frame) {
		
      	JPanel panel = new JPanel();
      	
      	localeTxt =addLabelAndTextFieldToPanel(panel, "Locale Code", 0, 0, 4, true);
      	localeTxt.addFocusListener(this);
      	
      	titleTxt = addLabelAndTextFieldToPanel(panel, "Title", 2, 0, 22, true);
      	titleTxt.addFocusListener(this);
      	
      	frame.add(panel, getRightPaddedGridBagConstraints(0, 0));
      	
      	JPanel panel2 = new JPanel();
      	
      	descriptionTxt = addLabelAndTextFieldToPanel(panel2, "Description", 0, 0, 30, requiresDescription);
      	descriptionTxt.addFocusListener(this);

        frame.add(panel2, getRightPaddedGridBagConstraints(0, 1));
		
		JPanel panel3 = new JPanel();
      	
      	addTexts = new JButton("Add");
      	addTexts.addActionListener(this);
      	addTexts.addFocusListener(this);
      	panel3.add(addTexts, getRightPaddedGridBagConstraints(0, 0));
      	
      	editTexts = new JButton("Edit");
      	editTexts.addActionListener(this);
      	editTexts.setEnabled(false);
      	editTexts.addFocusListener(this);
      	panel3.add(editTexts, getRightPaddedGridBagConstraints(1, 0));
      	
      	deleteTexts = new JButton("Remove");
      	deleteTexts.addActionListener(this);
      	deleteTexts.setEnabled(false);
      	deleteTexts.addFocusListener(this);
      	panel3.add(deleteTexts, getRightPaddedGridBagConstraints(2, 0));

		frame.add(panel3, getRightPaddedGridBagConstraints(0, 2));
		
		Object[] columnNames;
      	int[] sizes; 
      	
      	if(requiresDescription){
      		columnNames = new Object[]{ "Locale", "Title", "Description"};
          	sizes = new int[]{50, 150, 300}; 
      	}else{
      		columnNames =  new Object[]{ "Locale", "Title"};
          	sizes = new int[]{50, 300}; 
      	}
		
      	table = new JTable(createTableListSelectionModel(columnNames));
      	
      	configueTable(table, sizes, 0, 3, null);

		hook = addHookToTable(table);
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				selectedRow = table.getSelectedRow();
				
				if(selectedRow==-1){
					
					editTexts.setEnabled(false);
					deleteTexts.setEnabled(false);
					
				}else{
					
					editTexts.setEnabled(true);

					if(hook.doesObjectMatch(Resource.getDefaultLocale(), 0, selectedRow)){
						
						deleteTexts.setEnabled(false);
					}else{
						deleteTexts.setEnabled(true);
					}
				}
			}
        	
        });
        
        initFields();

		return 4;
	}
	
	@Override
	protected void postDrawGui(){
		
		if(localeTextMap==null || localeTextMap.isEmpty()){
			
			titleTxt.grabFocus();
		}
	}
	
	protected void initFields(){
		
		if(localeTextMap==null || localeTextMap.isEmpty()){

			localeTxt.setText(Resource.getDefaultLocale());
			
			localeTxt.addFocusListener(getLocaleFocusListener());
			
			localeTxt.setEditable(false);
			
			hook.clearTable();
			
		}else{
			
			//store differently to the game to save this mess?
			HashMap<String, String> titles = new HashMap<String, String>();
			HashMap<String, String> descriptions = new HashMap<String, String>();
			
			getInfoResource().populateHashMapHelpers(titles, descriptions);
			
			hook.clearTable();
			
			for(String locale : titles.keySet()){
				
//				if(descriptions.get(locale)!=null){
					
					if(requiresDescription){
						hook.addDataRowToTable(new Object[]{locale,  titles.get(locale), descriptions.get(locale)}, -1);
					}else{
						hook.addDataRowToTable(new Object[]{locale, titles.get(locale)}, -1);
					}
//				}
			}
		}
	}
	
	//TODO update this using validation helper
	protected boolean updateTableAndHashmap(){
		
		int rowToUpdate = -1; //add at the bottom (default)
		
		String localeStr = localeTxt.getText();
		
		if(!validateLocale(localeStr)){
			displayWarning("Locale must conform to ISO-639 (eg: en) and ISO-3166 (eg: en-GB)");
			return false;
		
		}else localeStr = nicelyFormattedLocale(localeStr);
	
		
		//existing check
		if((rowToUpdate = hook.getMatchingRowId(localeStr, 0))>-1 && !confirmOverwrite()){
			
			return false;
		}
		

		String titleStr = titleTxt.getText();
		
		if(!validateTitle(titleStr)){
			displayWarning("Title cannot be empty");
			return false;
		}

		
		String descriptionStr = null;
		
		if(requiresDescription){
		
			descriptionStr = descriptionTxt.getText();
		
			if(!validateDescription(descriptionStr)){
				displayWarning("Description cannot be empty");
				return false;
			}
		}
		
		if(localeTextMap==null || localeTextMap.isEmpty()){
			
			localeTxt.setEditable(true);
			localeTxt.removeFocusListener(getLocaleFocusListener());
		}
		
		//save as you go
		addText(localeStr, titleStr, descriptionStr);

		if(requiresDescription){
			hook.addDataRowToTable(new Object[]{localeStr, titleStr, descriptionStr}, rowToUpdate);
		}else{
			hook.addDataRowToTable(new Object[]{localeStr, titleStr}, rowToUpdate);
		}
		
		localeTxt.setText("");
		titleTxt.setText("");
		descriptionTxt.setText("");

		return true;

		
	}
	
	private boolean confirmOverwrite(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing locale?", "Locale already exists", JOptionPane.YES_NO_OPTION);
		
		return dialogResult == JOptionPane.YES_OPTION;
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == deleteTexts){
			
			Object[] objects = hook.getSelectedRow(selectedRow);
			removeText((String)objects[0]);
			hook.removeRow(selectedRow);
			
		}else if(button == addTexts){
			
			updateTableAndHashmap();

		}else if(button == editTexts){
			
			Object[] objects = hook.getSelectedRow(selectedRow);
			localeTxt.setText((String)objects[0]);
			titleTxt.setText((String)objects[1]);
			if(requiresDescription){
				descriptionTxt.setText((String)objects[2]);
			}

		}
		
	}
	
	protected FocusListener getLocaleFocusListener(){
		
		if(localeFocusListener==null){
		
			localeFocusListener = new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					
					clearWarning();
					
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					
					displayWarning("default locale must be set first");
				}
			};
		}
		
		return localeFocusListener;
	}
	
	
    
    protected void addText(String locale, String title, String description) {

    	localeTextMap.put(ResourceConstants.INFO_TYPE_TITLE + locale.trim(), title.trim());
    	if(description!=null)localeTextMap.put(ResourceConstants.INFO_TYPE_DESCRIPTION + locale.trim(), description.trim());
    	
		setDirtyStateAndConfigure(true);

    }
    
    protected void removeText(String locale) {
		
    	localeTextMap.remove(ResourceConstants.INFO_TYPE_TITLE + locale);
    	localeTextMap.remove(ResourceConstants.INFO_TYPE_DESCRIPTION + locale);
    	
		setDirtyStateAndConfigure(true);

	}
    
    protected void copyTextMap(){
    	
    	localeTextMap = new HashMap<String, String>(getInfoResource().getTextMap());
    }
    
    protected DefaultTableModel createTableListSelectionModel(Object[] columnNames){
    	
    	return new DefaultTableModel(columnNames, 0){

    		private static final long serialVersionUID = 3963911428426862252L;
    		
    		@Override
			public boolean isCellEditable(int row, int column) {
	        	  
				if (column==0 && getValueAt(row, column).equals(Resource.getDefaultLocale())) {
					
				    return false;
				}
				return true;
			}

			@Override
			public void setValueAt(Object aValue, int row, int column) {

				Object oldValue = getValueAt(row, column);
				
				Object[] objects = hook.getSelectedRow(row);
				
				boolean pass;
				
				switch(column){
				
				case 0:
					pass = validateLocale((String)aValue);
					aValue = nicelyFormattedLocale((String)aValue);
					objects[0] = aValue;
					removeText((String) oldValue);
					break;
				case 1:
					pass = validateTitle((String)aValue);
					objects[1] = aValue;
					break;
				case 2:
					pass = validateDescription((String)aValue);
					objects[2] = aValue;
					break;
				default:
					pass = false;
				}

				if(pass){
					if(requiresDescription){
						addText((String)objects[0], (String)objects[1], (String)objects[2]);
					}else{
						addText((String)objects[0], (String)objects[1], null);
					}
				}else{
					aValue = oldValue;
				}
				
				super.setValueAt(aValue, row, column);
			}
    		
    	};
    }
	
	@Override
	protected void saveData() {
		
		setPendingWriteXml(true);
		
		getInfoResource().setTextMap(localeTextMap);
		setDirtyStateAndConfigure(false);
		setDeleteButtonEnablement(true);
		
	}
	
	protected boolean validateLocale(String localeStr){
		
		if(localeStr != null){ 
			
			localeStr = localeStr.trim();
			Pattern pattern = Pattern.compile("^[a-zA-Z]{2}([-\\_][a-zA-Z]{2})?$");
		    if (pattern.matcher(localeStr).matches()) {
		        
		    	return true;
		    }
			
		}
		
		return false;
	}
	
	//because, that's why
	private String nicelyFormattedLocale(String localeStr){
		
		//validate first	
    	String locale = localeStr.substring(0, 2).toLowerCase();
    	
    	StringBuilder sb = new StringBuilder(locale);
    	
    	if(localeStr.length()>2){
    		
    		sb.append("-");
    		sb.append(localeStr.substring(3, 5).toUpperCase());
    	}
    	return sb.toString();
	}
	
	protected boolean validateTitle(String titleStr){
		
		if(titleStr ==null || titleStr.trim().length() == 0){

			return false;
			
		}
		return true;
	}
	
	protected boolean validateDescription(String descriptionStr){
		
//		if(descriptionStr ==null || descriptionStr.trim().length() == 0){
//	
//			return false;
//			
//		}
		
		return true;
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		
		if(table.isEditing()) table.getCellEditor().stopCellEditing();
	}
	
	@Override
	protected boolean additionalCloseConfirmation(){
		
		if(anyFieldsHaveAValue()){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Entered values have not been added, close anyway?", "Fields not empty", JOptionPane.YES_NO_OPTION);
			
			return dialogResult == JOptionPane.YES_OPTION;
			
		}
		
		return true;
	}
	
	@Override
	protected void newButtonClicked() {
		
		if(anyFieldsHaveAValue()){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all fields?", "Fields not empty", JOptionPane.YES_NO_OPTION);
			
			if(dialogResult == JOptionPane.YES_OPTION){
				
				if(localeTxt.isEditable()) localeTxt.setText("");
				titleTxt.setText("");
				descriptionTxt.setText("");
			}
		}
		
	}
	
	protected boolean anyFieldsHaveAValue(){
		
		return (localeTxt.getText()!=null && !localeTxt.getText().isEmpty()) ||
				(titleTxt.getText()!=null && !titleTxt.getText().isEmpty()) ||
				(descriptionTxt.getText()!=null && !descriptionTxt.getText().isEmpty());
	}
	
	@Override
	protected void revertUnsavedChanges(int pos) {
		
		copyTextMap();
		initFields();
		
		if(table.getRowCount() > 0){

			localeTxt.setEditable(true);
			localeTxt.setText("");
			localeTxt.removeFocusListener(getLocaleFocusListener());
		
		}else{
			titleTxt.grabFocus();
		}
	}
	
	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to clear ALL items from the table?", "Clear the table", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}
	
	@Override
	protected boolean deleteActions() {
			
		localeTextMap = new HashMap<String, String>();
		initFields();
		hook.clearTable();
		saveData();
		return true;
		
	}

	protected abstract InfoResource getInfoResource();
}
