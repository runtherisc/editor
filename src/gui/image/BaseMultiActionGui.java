package gui.image;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.ImageResourceActions;
import data.map.resources.MultiImageResourceAction;
import data.map.resources.Resource;
import game.ImageHelper;
import gui.ITableUpdateHook;
import gui.ValidationHelper;

public abstract class BaseMultiActionGui extends BaseImageActionGui{
	
	private JTextField nameTxt;
	private JSpinner skipSpin;

	private int sequence = -1;
	private String sequenceStr = "sequence";
	
	private JTable table;
	private ITableUpdateHook hook;
	private int selectedRow = -1;
	
	private JButton addTexts, editTexts, deleteTexts;
	
	private List<MultiImageResourceAction> tableItems;//index matches table row
	private List<MultiImageResourceAction> revertTableItems;
	

	public BaseMultiActionGui(String title, JFrame parent) {
		super(title, parent);
		
	}

	@Override
	protected int addComponents(final JFrame frame) {
		
		JPanel panel = new JPanel();
		
		nameTxt = addLabelAndTextFieldToPanel(panel, "Name", 0, 0, 10, true);
		
		if(addSkipToForm()){
			skipSpin = addLabelAndNumberSpinnerToPanel(panel, "Skip", 2, 0, 20, 1);
			skipSpin.setValue(1);
//			skipSpin.addChangeListener(new ChangeListener() {
//				
//				@Override
//				public void stateChanged(ChangeEvent e) {
//					
//					if(isFormReady())  setDirtyStateAndConfigure(true);
//				}
//			});
		}
		
		frame.add(panel, getAllPaddingGridBagConstraints(0, 0));
		
        imageButtons = new ArrayList<JButton>();
		frame.add(imagePanel = getMultiImageSelection(null, imageButtons, enableImageSlider()), getRightPaddedGridBagConstraints(0, 1));
	
		panel = new JPanel();
      	
      	addTexts = new JButton("Add");
      	addTexts.addActionListener(this);
      	addTexts.addFocusListener(this);
      	panel.add(addTexts);
      	
      	editTexts = new JButton("Edit");
      	editTexts.addActionListener(this);
      	editTexts.setEnabled(false);
      	editTexts.addFocusListener(this);
      	panel.add(editTexts);
      	
      	deleteTexts = new JButton("Remove");
      	deleteTexts.addActionListener(this);
      	deleteTexts.setEnabled(false);
      	deleteTexts.addFocusListener(this);
      	panel.add(deleteTexts);
      	
      	addAdditionalTableButtons(panel);

		frame.add(panel, getRightPaddedGridBagConstraints(0, 2));
		
		table = createTable(getTableColumnNames(), getTableColumnSizes(), 0, 3);
		hook = addHookToTable(table);
        
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				selectedRow = table.getSelectedRow();
//				addToPassedProperties(PropertyKeys.REQUIREMENT_SELECTED_ROW, selectedRow);
				
				if(selectedRow==-1){
					
					editTexts.setEnabled(false);
					deleteTexts.setEnabled(false);
					
				}else{
					
					editTexts.setEnabled(true);
					deleteTexts.setEnabled(true);
				}
				
				configureAdditionalButtons(selectedRow!=-1);
				
			}
        	
        	
        });
        
    	sequence = -1;
        
        configureTableMap();
        displayTable();
        
        clearImageOrder();
        
        if(enableImageSlider()) initImageSliderFromPanel(imagePanel);

        return 4;
	}
	
	protected void addAdditionalTableButtons(JPanel panel) {}
	
	protected void configureAdditionalButtons(boolean isRowSelected) {}

	protected void postDrawGui(){
		
		initFields();
		
		for(MultiImageResourceAction resource : getResources()){
			
			if(checkForUsage(resource)!=null){
				setDeleteButtonEnablement(false);
				break;
			}
		}
	}
	
	
	protected void configureTableMap(){
		
		List<MultiImageResourceAction> resourceList = getResources();	
		
		tableItems = new ArrayList<MultiImageResourceAction>();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(MultiImageResourceAction resource : resourceList){
				
				String name = resource.getInternalName();
				if(name.trim().length()==0) name = String.valueOf(resource.getId());
				
				//temp
				String seq = getInnerFolders(resource.getDirectory());
				System.out.println(seq+" configure table: "+(getImageOrder(seq)==null));
				System.out.println(seq+" images: "+(resource.getTotalNumberImages()));
				
				tableItems.add(resource.copy());

			}
		}
	}
	protected void displayTable(){
		
		hook.clearTable();

		for (MultiImageResourceAction resource : tableItems) {
			
			addRowFromResource(resource, -1, hook, sequenceStr);
		}
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == deleteTexts){

			deleteResourcesFromTable();

		}else if(button == addTexts){
			
			System.out.println("add clicked");
			String warning = addResourceToTableAndList();
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == editTexts){
			
			System.out.println("edit on row "+selectedRow);
			editResourceFromTable();
		}
		
		
	}
	
	protected boolean deleteResourcesFromTable(){
		
		String warning = checkForUsage(tableItems.get(selectedRow));
		if(warning!=null){
			
			displayWarning(warning);
			return false;
		}
		
		System.out.println("sequence:"+sequence+" selected row:"+selectedRow);
		
		//empty list will clean up any images on parent save
		addImageOrder(getInnerFolders(tableItems.get(selectedRow).getDirectory()), new ArrayList<String>());
		addImageTotal(getInnerFolders(tableItems.get(selectedRow).getDirectory()), 0);
		
		tableItems.remove(selectedRow);
		
		hook.removeRow(selectedRow);
		
		setPendingImageSaveAndConfigure(true);
		
//		sequence = -1;
		
		return true;
	}
	
	//override me when needed
	protected String checkForUsage(MultiImageResourceAction action){
		
		return null;
	}
	
	protected void editResourceFromTable(){

		MultiImageResourceAction resource = tableItems.get(selectedRow);
		
		nameTxt.setText(resource.getInternalName());
		if(addSkipToForm()){
			skipSpin.setValue((int)resource.getSkip());
		}
		sequence = resource.getDirectory();
		
		String innerFolder = getInnerFolders();
		
		List<String> imageOrder = getImageOrder(innerFolder);
		
		int total = resource.getTotalNumberImages();
		
		if(imageOrder==null){		

			imageOrder = new ArrayList<String>();
			
			for (int i = 0; i < total; i++) {
				imageOrder.add(String.valueOf(i));
			}
			
			addImageOrder(getInnerFolders(), imageOrder);
			addImageTotal(getInnerFolders(), total);
			
			if(getRevertMap().containsKey(innerFolder)){
				
				ImageHelper.copyFromRevertToTemp(total, innerFolder, imageOrder, frame);
			}else{

				ImageHelper.copyPngFromReourceToTemp(null, total, null, -1, imageResource.getDirectory(), getInnerFolders(), frame);
			}

		}
		
		List<Integer> seqs = new ArrayList<Integer>();
		
		for (MultiImageResourceAction resourceTableItem : tableItems) {

			seqs.add(resourceTableItem.getDirectory());
		}

		sequence = Resource.findUnusedId(seqs);

		ImageHelper.duplicateFilesInTemp(total, innerFolder, getInnerFolders(sequence), frame);
		addImageOrder(getInnerFolders(sequence), duplicateImageOrder(imageOrder));
		addImageTotal(getInnerFolders(sequence), getImageTotal(innerFolder));
		
		
		zeroImageSliderValue();
		displayImages(0);

//		initImageSliderFromPanel();
		
	}
	
	protected String addResourceToTableAndList(){
		
		ValidationHelper validationHelper = new ValidationHelper();
		
		if(!validationHelper.validateString("Name", nameTxt.getText()))
			return validationHelper.getWarning();
		String name = validationHelper.getStringResult();
		
		int skip = 0;
		
		if(addSkipToForm()){

			skip = (int) skipSpin.getValue();
		}
		
		MultiImageResourceAction resource = getResourceBySequence();
		MultiImageResourceAction resourceToAdd = null;
		
		String innerFolder = getInnerFolders(resource.getDirectory());
		
		System.out.println("Inner folder on add to table"+innerFolder);
		
		int total = getImageTotal(innerFolder);
		
		String warning = checkImageSizeRequirements(total);
		
		if(warning!=null) return warning;

		int row = hook.getMatchingRowId(name, 0);
		
		if(row > -1){
			if(!confirmOverwrite()) return null;
			
		}
			
		boolean nameExists = false;
		
		int id = tableItems==null || tableItems.isEmpty() ? 1 : tableItems.get(tableItems.size()-1).getId()+1;
		int seq = tableItems==null || tableItems.isEmpty() ? 1 : tableItems.get(tableItems.size()-1).getDirectory()+1;
		
		for (MultiImageResourceAction item : tableItems) {
			
			//user renamed/created idle to a name that matches an existing name
			if(item.getInternalName().equals(name)){
				
				nameExists = true;
				resourceToAdd = resource.copy();
				id = item.getId();
				seq = item.getDirectory();
				
//				resourceToAdd.setId(item.getId());
//				addImageOrder(getInnerFolders(item.getDirectory()), duplicateImageOrder(getImageOrder(getInnerFolders(resourceToAdd.getDirectory()))));
//				addImageTotal(getInnerFolders(item.getDirectory()), getImageTotal(getInnerFolders(resourceToAdd.getDirectory())));
//				ImageHelper.duplicateFilesInTemp(total, getInnerFolders(resourceToAdd.getDirectory()), getInnerFolders(item.getDirectory()), frame);
//				resourceToAdd.setSequence(item.getDirectory());
				break;
			}
		}
		
		//straight edited idle with no name change or new entry
		if(!nameExists) resourceToAdd = resource;
		
		resourceToAdd.setId(id);
		addImageOrder(getInnerFolders(seq), duplicateImageOrder(getImageOrder(getInnerFolders(resourceToAdd.getDirectory()))));
		addImageTotal(getInnerFolders(seq), getImageTotal(getInnerFolders(resourceToAdd.getDirectory())));
		ImageHelper.duplicateFilesInTemp(total, getInnerFolders(resourceToAdd.getDirectory()), getInnerFolders(seq), frame);
		resourceToAdd.setSequence(seq);
		
		resourceToAdd.setInternalName(name);
		resourceToAdd.setSkip(skip);
		resourceToAdd.setTotalNumberImages(total);

		if(row == -1){
			tableItems.add(resourceToAdd);
		}else{
			tableItems.set(row, resourceToAdd);
		}
		
		addRowFromResource(resourceToAdd, row, hook, sequenceStr);
		
		sequence = -1;
	
		setPendingImageSaveAndConfigure(true);
		
//		initFields();
		clearFields();
		displayImages(0);
		
		return null;
	}
	
	protected String checkImageSizeRequirements(int total){

		if(total==0) return "You must select at least one image";
		
		return null;
	}
	
	protected int getSequence(){
		
		if(sequence==-1){
			
			MultiImageResourceAction resource = (MultiImageResourceAction) getResourceBySequence();
			
			sequence = resource.getDirectory();
			
			if(getImageOrder(getInnerFolders())==null){
				
				clearFields();
			}
			
		}
		
		return sequence;
	}
	
	protected void clearFields(){
		
		addImageOrder(getInnerFolders(), new ArrayList<String>());
		addImageTotal(getInnerFolders(), 0);
		nameTxt.setText("");
		if(addSkipToForm()) skipSpin.setValue(1);
	}
	
	protected MultiImageResourceAction getResourceFromTable(String innerFolder){
		
		int seq = getSequenceFromInnerFolder(innerFolder);
		
		for (MultiImageResourceAction resource : tableItems) {
			
			if(seq == resource.getDirectory()) return resource;
		}
		
		return null;
	}
	
	protected MultiImageResourceAction getResourceBySequence(){
		
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> seq = new ArrayList<Integer>();
		
		for (MultiImageResourceAction resource : tableItems) {
			
			if(sequence == resource.getDirectory()) return resource;
			ids.add(resource.getId());
			seq.add(resource.getDirectory());
		}
		
		//create a new one if it does not exist in the table
		MultiImageResourceAction resource = new MultiImageResourceAction();
		resource.setSequence(sequence == -1 ? Resource.findUnusedId(seq) : sequence);
		resource.setId(Resource.findUnusedId(ids));
		return resource;
	}

	
	private boolean confirmOverwrite(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Name already exists", JOptionPane.YES_NO_OPTION);
		
		return dialogResult == JOptionPane.YES_OPTION;
	}

//	@Override
//	protected void passedBundle(Map<String, Object> properties) {
//	
//		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
//		
//		span = (Coords) properties.get(PropertyKeys.IMAGE_RESOURCE_SPAN);
//		
//		System.out.println("span passed "+span.toString());
//
//	}

	@Override
	protected void saveData() {
		
		ArrayList<MultiImageResourceAction> resourceListToSave = new ArrayList<MultiImageResourceAction>();
		
		for (MultiImageResourceAction resourceImageResourceAction : tableItems) {
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>saving skip "+resourceImageResourceAction.getSkip());
			
			resourceListToSave.add(resourceImageResourceAction.copy());
		}

		setMultiResource(resourceListToSave);
		
		if(isPendingImageSave()){
			
			createRevertTableItems();
			
			//make a copy of all entries, in case they get deleted and then reverted
			for (MultiImageResourceAction resource : tableItems) {
				
				String innerFolder = getInnerFolders(resource.getDirectory());
				
				System.out.println("innerfolder: "+innerFolder);
				
				List<String> imageOrder = getImageOrder(innerFolder);
				
				int total;
				
				if(imageOrder!=null){
					
					total = getImageTotal(innerFolder);
					ImageHelper.copyFromTempToRevert(total, innerFolder, imageOrder, frame);
					
				}else{
					total = resource.getTotalNumberImages();
					ImageHelper.copyPngFromReourceToRevert(null, total, null, -1, imageResource.getDirectory(), innerFolder, frame);
				}
				
				setRevertAmount(innerFolder, total);
			}

		}
	}
	
	protected void createRevertTableItems(){
		
		revertTableItems = new ArrayList<MultiImageResourceAction>();
		
		for (MultiImageResourceAction resource : tableItems) {
			
			revertTableItems.add(resource.copy());
			System.out.println("save: adding "+resource.getInternalName()+" id "+resource.getId());
		}
		
	}
	
	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems!=null && !revertTableItems.isEmpty()){
			
			tableItems = new ArrayList<MultiImageResourceAction>();
			
			for (MultiImageResourceAction resource : revertTableItems) {
				
				tableItems.add(resource.copy());

			}
	
			//copy revert images back to temp folder and reinit imageorder
			for (String innerFolder : getRevertMap().keySet()) {
				
				ArrayList<String> imageOrder = new ArrayList<String>();
				
				addImageOrder(innerFolder, imageOrder);
				addImageTotal(innerFolder, 0);
				
				int total = getRevertAmount(innerFolder);
				
				if(total > 0) ImageHelper.copyFromRevertToTemp(total, innerFolder, null, frame);

				for (int i = 0; i < total; i++) {
					imageOrder.add(String.valueOf(i));
				}
			}
			
		}else{
			
			//reset everything back to resource
			configureTableMap();
			
			clearImageOrder();
		}

		displayTable();
		
		sequence = -1;
		clearFields();
		displayImages(0);
		
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		// done on the Add button
		return null;
	}

	@Override
	protected void newButtonClicked() {
		
		if(anyFieldsHaveAValue()){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all fields?", "Fields not empty", JOptionPane.YES_NO_OPTION);
			
			if(dialogResult == JOptionPane.YES_OPTION){
				
				clearFields();
				displayImages(0);
			}
		}
		
	}
	
	protected boolean anyFieldsHaveAValue(){
		
		return (nameTxt.getText()!=null && !nameTxt.getText().isEmpty()) ||
				(getImageOrder(getInnerFolders())!=null && getImageTotal(getInnerFolders()) > 0);
	}
	
	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Remove ALL actions from the table?", "Clear Table", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}

	@Override
	protected boolean deleteActions() {
		//for image cleanup
		for (String innerFolder : getImageOrderKeySet()) {
			addImageOrder(innerFolder, new ArrayList<String>());
			addImageTotal(innerFolder, 0);
		}
		tableItems = new ArrayList<MultiImageResourceAction>();
		initFields();
		hook.clearTable();
		saveData();
		sequence = -1;
		return true;
	}
	
	@Override
	protected boolean isDirtyOnImageChange(){
		
		return false;
	}

	@Override
	protected String getInnerFolders() {

		return getInnerFolders(getSequence());
	}
	
	protected String getInnerFolders(int sequence){
		
		return getInnerPathPrefix()+sequence;
	}
	
	protected int getSequenceFromInnerFolder(String innerFolder){
		
		String seqStr = innerFolder.substring(getInnerPathPrefix().length());
		
		return Integer.parseInt(seqStr);
	}
	
	

	@Override
	protected ImageResourceActions getResource() {
		return null;
	}

	@Override
	protected void setResource(ImageResourceActions resource) {
		// nothing to do here
		
	}
	
	protected List<MultiImageResourceAction> getTable(){
		
		return tableItems;
	}
	
	protected int getSelectedRow(){
		
		return selectedRow;
	}
	
	protected void addRowFromResource(MultiImageResourceAction resource, int row){
		
		addRowFromResource(resource, row, hook, sequenceStr);
	}
	
	protected abstract boolean addSkipToForm();
	
	protected abstract String getInnerPathPrefix();
	
	protected abstract List<MultiImageResourceAction> getResources();
	
	protected abstract void setMultiResource(List<MultiImageResourceAction> resources);
	
	protected abstract Object[] getTableColumnNames();
	
	protected abstract int[] getTableColumnSizes();
	
	protected abstract void addRowFromResource(MultiImageResourceAction resource, int row, ITableUpdateHook hook, String sequenceStr);

}
