package gui.level;

import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import data.LevelDataIO;
import data.map.resources.BuildingResource;
import data.map.resources.LevelResource;
import data.map.resources.Resource;
import game.ConfigIO;
import game.FileBrowser;
import game.ImageHelper;
import game.MapEditor;
import gui.ChildBaseGui;
import gui.EditorGeneral;
import gui.ITableUpdateHook;
import gui.PropertyKeys;
import gui.ValidationHelper;

public class LevelGui extends ChildBaseGui implements IDirtyMap{

	public LevelGui(String title, JFrame parent, boolean write) {
		super(title, parent);
//		setupSlider(Resource.getAllLevelResourceIds());
		if(write)setCanWriteXml(true);
	}
	
	private JTable table;
	private ITableUpdateHook hook;	
	private JButton browseButton, selectedRowButton, levelBuildingsButton, deleteMap;
	private JTextField nameTxt, mapImageTxt;
	private JButton addButton, editButton, deleteButton, mapButton, targetButton, localizedTextButton;
	private int selectedRow = -1;
	private List<LevelResource> tableItems;//no need for a revert list as we will be writing the xml (so a reload)
//	private JLabel rowLabel;
//	private String rowLabelText = "Editing Level ";
//	private String rowLabelNewText = "Editing New Entry";
	int currentId;//increases for every new entry regardless of how many items are in the table
	int editingLevel;
	
	private List<String> selectedPopupItems = new ArrayList<String>();
	private List<Integer> selectedPopupIds = new ArrayList<Integer>();
	boolean menuShowing;
	boolean menuVisable;
	private JPopupMenu menu;
	private String buildingNoneText = "Nothing Selected";
	
	private JSpinner levelSpinner, gridXSpinner, gridYSpinner;
	private SpinnerNumberModel spinnerModel;
	
	private boolean editingExistingMap = false;
	
	
	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		int i = 1;
		for (LevelResource levelResource : tableItems) {
			if(!LevelDataIO.doesTempLevelExist(levelResource.getId())) return "level " + i + " must have a map configured";
			if(levelResource.getTargets()==null || levelResource.getTargets().isEmpty()) return "level " + i + " does not have any targets set";
			if((levelResource.getInfoResource()==null || levelResource.getInfoResource().getTextMap().isEmpty()))
				return "at least one localized text must be set on level "+i;
			i++;
		}
		return null;
	}

	@Override
	protected int addComponents(JFrame frame) {
		
		Object[] names = new Object[]{"Level", "Name", "Map Image", "Texts", "Target", "Map Configured"};
		int[] sizes = new int[]{50, 250, 200, 50, 50, 100}; 
		
		JPanel panel = new JPanel();
		
		levelSpinner = addLabelAndNumberSpinnerToPanel(panel, "Level", 0, 0, 99, 1);
		
		spinnerModel = (SpinnerNumberModel) levelSpinner.getModel();
		
		panel.add(levelSpinner);
		
		nameTxt = addLabelAndTextFieldToPanelWithListener(panel, "Name", 0, 0, 20, true, false);
		
		gridXSpinner = addLabelAndNumberSpinnerToPanel(panel, "Grid X", 0,  0, 100, 50);
		panel.add(gridXSpinner);
		
		gridYSpinner = addLabelAndNumberSpinnerToPanel(panel, "Grid Y", 0,  0, 100, 25);
		panel.add(gridYSpinner);

		frame.add(panel, getRightPaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
		mapImageTxt = addLabelAndTextFieldToPanelWithListener(panel, "Map Image", 0, 0, 15, false, false);
		
		browseButton = addTableButton(panel, "Browse", true);
		selectedRowButton = addTableButton(panel, "Copy Selected Row", false);
		
		frame.add(panel, getAllPaddingGridBagConstraints(0, 1));
		
        panel = new JPanel();

		menu = new JPopupMenu();
		menu.addPopupMenuListener(new PopupMenuListener() {			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {menuVisable = true;}			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {menuVisable = false;}			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		
      	levelBuildingsButton = new JButton(buildingNoneText);
      	levelBuildingsButton.addActionListener(this);
      	levelBuildingsButton.addFocusListener(this);
      	
      	panel.add(new JLabel("Level Buildings"));
      	
      	panel.add(levelBuildingsButton);
      	
        frame.add(panel, getRightPaddedGridBagConstraints(0, 2));
        
        initMenu();

		panel = new JPanel();
		
      	addButton = addTableButton(panel, "Add", true);
      	editButton = addTableButton(panel, "Edit", false);
      	deleteButton = addTableButton(panel, "Remove", false);
      	
//      	frame.add(panel, getSidePaddedGridBagConstraints(0, 3));
//      	
//      	panel = new JPanel();
      	
      	localizedTextButton = new JButton("Localized Texts");
      	addGuiButtonAndListener(new LevelTextsGui("Level text", frame), localizedTextButton);
    	panel.add(localizedTextButton);
		localizedTextButton.setEnabled(false);
		
      	mapButton = addTableButton(panel, "Edit Map", false);
      	
      	deleteMap = addTableButton(panel, "Delete Map", false);
      	
      	targetButton = new JButton("Targets");
      	addGuiButtonAndListener(new LevelTargetsGui("Level Targets", frame), targetButton);
    	panel.add(targetButton);
    	targetButton.setEnabled(false);
		
		
		frame.add(panel, getSidePaddedGridBagConstraints(0, 3));
		
      	table = createTable(names, sizes, 0, 4);
      	hook = addHookToTable(table);
      	
      	addListenerToTable(table, editButton, deleteButton, mapButton, targetButton, localizedTextButton, selectedRowButton, deleteMap);      
      	
      	initFromResource();
        
//        rowLabel.setText(rowLabelNewText);
        
		return 5;
	}
	
	@Override
	protected void postDrawGui() {
		super.postDrawGui();
      	setDeleteButtonEnablement(false);
	}
	
	protected void initFromResource(){
		
		tableItems = new ArrayList<LevelResource>();
		
    	for (LevelResource levelResource : Resource.getLevelResourceList()) {
			
    		tableItems.add(levelResource.copy());
		}

		String[] filenames = getRequiredMapImageFilenamesFromResource();
		ImageHelper.copyPngFromReourceToTemp(filenames, filenames.length, null , -1, Resource.getMapPath(), "", frame);	
		LevelDataIO.copyDataToTemp();
        
        displayTable(hook, tableItems);
        
        currentId = tableItems.size()+1;
        
        spinnerModel.setMaximum(currentId);
        spinnerModel.setValue(currentId);
        
        nameTxt.grabFocus();
 
        editingLevel = -1;
        
        clearFields();
	}
	
	protected void initMenu(){
		
//		setFormReady(false);
		
		menu.removeAll();
		selectedPopupItems = new ArrayList<String>();
		selectedPopupIds = new ArrayList<Integer>();
		
		List<BuildingResource> buildings = Resource.getBuildingResourceList();
		
		setupPopupGridLayout(menu, buildings.size());
		
		for (BuildingResource buildingResource : buildings) {
			
			createMenuPopupItem(buildingResource.getName(), buildingResource.getId(), menu);
		}
		
//		setFormReady(true);
		
	}
	
	protected void createMenuPopupItem(final String name, final int id, JPopupMenu menu){
		
//		final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);
//		menuItem.setUI(new StayOpenCheckBoxMenuItemUI());//fails on a mac (all checked)
		final JCheckBox menuItem = new JCheckBox(name);

		menuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			
				if(menuItem.isSelected()){
					
					selectedPopupItems.add(name);
					selectedPopupIds.add(id);
				}else{
					
					selectedPopupItems.remove(name);
					selectedPopupIds.remove((Integer)id);
				}
				updateButtonText();
			}
		});
		
		menu.add(menuItem);
	}
	
	protected void configureMenuPopupItem(List<Integer> selectedIds){
		
//		setFormReady(false);

		int i = 0;
		List<BuildingResource> buildings = Resource.getBuildingResourceList();
		for (BuildingResource buildingResource : buildings) {
			
			JCheckBox checkBox = (JCheckBox)menu.getComponent(i);
			checkBox.setSelected(selectedIds.contains(buildingResource.getId()));
			i++;
		}
//		setFormReady(true);
	}
	
	protected void updateButtonText(){
		
//		if(isFormReady()) setDirtyStateAndConfigure(true);
		
		StringBuilder sb = new StringBuilder();
		for (String name : selectedPopupItems) {
			if(sb.length() > 0) sb.append(", ");
			if(sb.length() > 50){
				sb.append(" ...");
				break;
			}
			sb.append(name);

		}
		if(sb.length()==0) sb.append(buildingNoneText);
		levelBuildingsButton.setText(sb.toString());
	}

	protected void displayTable(ITableUpdateHook hook, List<LevelResource> tableItems){
		
		hook.clearTable();
		
		int level = 0;

		for (LevelResource resource : tableItems) {
			
			level++;
			
//			System.out.println("level "+level+" id "+resource.getId());
			
			addRowFromResource(resource, hook, -1, Integer.toString(level));
		}
	}
	
	protected void addRowFromResource(LevelResource resource, ITableUpdateHook hook, int row, String levelText){
		
//		Object[] names = new Object[]{"Level", "Name", "Map Image", "Items"};
		
		StringBuilder mapCellSB = new StringBuilder(Boolean.toString(LevelDataIO.doesTempLevelExist(resource.getId())));
		mapCellSB.append(" ").append(resource.getGridX()).append(" X ").append(resource.getGridY());
		
		
		Object[] data = new Object[]{
			
			levelText, //+"["+resource.getId()+"]",
			resource.getInternalTitle(),
			getFileNameFromPath(resource.getMapPath()),
			resource.getInfoResource().getTextMap().size(),
			resource.getTargets().size(),
			mapCellSB.toString()
		};

		hook.addDataRowToTable(data, row);
	}
	
	protected String getFileNameFromPath(String path){
		
		return path.substring(path.lastIndexOf("/")+1);
	}
	
	protected void addListenerToTable(final JTable table, final JButton edit, final JButton delete, 
			final JButton map, final JButton target, final JButton localizedTextButton, final JButton selectedRowButton, final JButton deleteMap){
		
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {

				
				selectedRow = table.getSelectedRow();
				
				if(selectedRow==-1){
					
					edit.setEnabled(false);
					delete.setEnabled(false);
					map.setEnabled(false);
					target.setEnabled(false);
					localizedTextButton.setEnabled(false);
					selectedRowButton.setEnabled(false);
					deleteMap.setEnabled(false);
					
				}else{
	
					edit.setEnabled(true);
					delete.setEnabled(true);
					map.setEnabled(true);
					target.setEnabled(true);
					localizedTextButton.setEnabled(true);
					selectedRowButton.setEnabled(true);
					if(LevelDataIO.doesTempLevelExist(tableItems.get(selectedRow).getId())){
						deleteMap.setEnabled(true);
					}else{
						deleteMap.setEnabled(false);
					}
				}
				
			}
        	
        	
        });
        
	}
	
	
	@Override
	protected void otherActions(JButton button, JFrame frame){
		
		super.otherActions(button, frame);
		
		if(button == mapButton){
			
			if(selectedRow > -1){
			
				System.out.println("level editor");
				
				//assumes that the existing level has been previously saved
				if(LevelDataIO.doesTempLevelExist(selectedRow+1)){
					editingExistingMap = true;
				}
				
				LevelResource resource = tableItems.get(selectedRow);
			
				String mapImageFilename = getFileNameFromPath(resource.getMapPath());

				new MapEditor(mapImageFilename, resource.getId(), "Level "+(selectedRow+1), frame, resource.getGridX(), resource.getGridY(), this);//assumes that level increment by 1
				
			}
		}else if(button == deleteButton){

			deleteResourcesFromTable();

		}else if(button == addButton){

			String warning = addResourceToTableAndList();
			
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == editButton){

			editResourceFromTable();
			
		}else if(button == deleteMap){
			
			if(selectedRow > -1){
				LevelResource resource = tableItems.get(selectedRow);
				LevelDataIO.deleteLevelFromTemp(resource.getId());
				Object[] objects = hook.getSelectedRow(selectedRow);
				addRowFromResource(resource, hook, selectedRow, objects[0].toString());
			}
			
		}else if(button == selectedRowButton){
			
			if(selectedRow > -1){
				mapImageTxt.setText(getFileNameFromPath(tableItems.get(selectedRow).getMapPath()));
			}
		}else if(button==levelBuildingsButton){
			
			if(!menuShowing) menu.show(levelBuildingsButton, 0, levelBuildingsButton.getHeight());

	    	menuShowing = menuVisable;

		}else if(button==browseButton){
			
			File file = new FileBrowser().browserSinglePngDiag(ConfigIO.getProperty(ConfigIO.LAST_IMAGE_LOAD_KEY));
			if(fileOverwriteCheck(file)){
				if(file!=null){
					
					ImageHelper.addSingleFileToTemp(file);
					String filename = file.getName();
					int ext = filename.lastIndexOf(".");
					if(ext > -1) filename = filename.substring(0, ext);
					mapImageTxt.setText(filename);
				}
			}
		}
	}
	
	protected boolean fileOverwriteCheck(File file){
		
		if(ImageHelper.doesFileExistInTemp(file)){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "File already exists in project with the name, Overwrite existing file?", "Confirm file overwrite", JOptionPane.YES_NO_OPTION);
			if(dialogResult != JOptionPane.YES_OPTION){

				return false;
			}
		}
		
		return true;
	}
	
	protected void clearFields(){
		
		nameTxt.setText("");
		gridXSpinner.setValue(75);
		gridYSpinner.setValue(75);
		gridXSpinner.setEnabled(true);
		gridYSpinner.setEnabled(true);
		mapImageTxt.setText("");
		selectedPopupItems = new ArrayList<String>();
		selectedPopupIds = new ArrayList<Integer>();
		configureMenuPopupItem(selectedPopupIds);
	}
	
	protected void deleteResourcesFromTable(){
		
		if(selectedRow > -1){
			
			tableItems.remove(selectedRow);
//			hook.removeRow(selectedRow);
			setDirtyStateAndConfigure(true);
			
			displayTable(hook, tableItems);
			
	        spinnerModel.setMaximum(tableItems.size()+1);	            
	        spinnerModel.setValue(tableItems.size()+1);         
	        nameTxt.grabFocus();

		}
	}
	
	protected String addResourceToTableAndList(){
		
		ValidationHelper validationHelper = new ValidationHelper();

		if(!validationHelper.validateString("Name", nameTxt.getText()))
			return validationHelper.getWarning();

		String title = validationHelper.getStringResult();
		
		
		if(!validationHelper.validateString("Map Path", mapImageTxt.getText()))
			return validationHelper.getWarning();
		
		String mapName = validationHelper.getStringResult();
		
		if(selectedPopupIds.size()==0) return "Please select some Level Buildings that the player can create";
	
		LevelResource levelResource;
    	

		int selectedLevel = (int)levelSpinner.getValue();
		
		boolean isInsert = false;
		boolean update = false;
		
		if(selectedLevel <= tableItems.size()){
			
			int response = promptUserUpdateOrInsert(selectedLevel);
			
			//Cancelled
			if(response < 1) return null;

			if(response == 1) isInsert = true;
			
			if(response == 2) update = true;
		}
		
		Set<Integer> imageIds = new HashSet<Integer>();
		Set<Integer> workerIds = new HashSet<Integer>();
		
		if(editingLevel > -1){
			
			if(editingLevel+1 == selectedLevel && update){
				
				levelResource =  tableItems.get(editingLevel);
				
				System.out.println("editing same source with id "+levelResource.getId());
			}else{
				
				LevelResource sourceResource = tableItems.get(editingLevel);
				
				//clear editing
				addRowFromResource(sourceResource, hook, editingLevel, Integer.toString(editingLevel+1));
				
				levelResource = getNewLevelResourceWithId();
				
				System.out.println("editing new source with id "+levelResource.getId());
				
				if(LevelDataIO.doesTempLevelExist(sourceResource.getId())){
					
					LevelDataIO.duplicateJsonInTemp(sourceResource.getId(), levelResource.getId());
					


				}

				levelResource.setTargets(sourceResource.copyTargets());
			}
			
		}else{
			
			levelResource = getNewLevelResourceWithId();
			
			System.out.println("new source with id "+levelResource.getId());
		}
		
		levelResource.setJson(LevelDataIO.getJsonFilePath(levelResource.getId()));
		
		LevelDataIO.populateIdsFromMap(levelResource.getId(), imageIds, workerIds);
		
		LevelDataIO.populateIdsFromBuildingResourceIds(new HashSet<Integer>(selectedPopupIds), imageIds, workerIds);

		levelResource.setWorkers(intSetToArray(workerIds));
		levelResource.setImages(intSetToArray(imageIds));
		
		levelResource.setBuildings(copyIdList(selectedPopupIds));
		
		levelResource.setInternalTitle(title);
		
		levelResource.setGridX((int)gridXSpinner.getValue());
		levelResource.setGridY((int)gridYSpinner.getValue());
		
		levelResource.setMapPath(Resource.getMapPath()+mapName);
		
		if(selectedLevel > tableItems.size()){
			tableItems.add(levelResource);
		}else{
			if(!isInsert)tableItems.remove(selectedLevel-1);
			tableItems.add(selectedLevel-1, levelResource);
		}
		
		if(isInsert){
			displayTable(hook, tableItems);
		}else{
			addRowFromResource(levelResource, hook, update ? selectedLevel-1 : -1, Integer.toString(selectedLevel));
		}
		
		clearFields();
		
		setDirtyStateAndConfigure(true);

        if(!update){

	        spinnerModel.setMaximum(tableItems.size()+1);	            
        }
        spinnerModel.setValue(tableItems.size()+1); 
        
        nameTxt.grabFocus();
        
        editingLevel = -1;
		
		return null;
	}
	
	private int[] intSetToArray(Set<Integer> set){
		
		int[] array = new int[set.size()];
		int j = 0;
		for (int i : set) {
			array[j] = i;
			j++;
		}
		
		return array;
	}
	
	private List<Integer> copyIdList(List<Integer> ids){
		
		List<Integer> copy = new ArrayList<>();
		for (int integer : ids) {
			copy.add(integer);
		}
		return copy;
	}
	
	private LevelResource getNewLevelResourceWithId(){
		
		LevelResource levelResource = new LevelResource();
		levelResource.setId(currentId);
		currentId++;
		return levelResource;
	}
	
	protected int promptUserUpdateOrInsert(int level){
		
		Object[] options = {"Cancel", "Insert", "Update"};
		
		return JOptionPane.showOptionDialog(frame, "Insert Above Or Update Level "+level+"?", "Level Exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

	}
	
	protected void editResourceFromTable(){
		
		if(selectedRow > -1){
			int selectedRow = this.selectedRow;
			if(editingLevel > -1){
				LevelResource resource = tableItems.get(editingLevel);
				addRowFromResource(resource, hook, editingLevel, Integer.toString(editingLevel+1));
			}
			
			editingLevel = selectedRow;
			LevelResource resource = tableItems.get(selectedRow);
			nameTxt.setText(resource.getInternalTitle());
			nameTxt.setCaretPosition(0);
			gridXSpinner.setValue(resource.getGridX());
			gridYSpinner.setValue(resource.getGridY());
			mapImageTxt.setText(getFileNameFromPath(resource.getMapPath()));
			mapImageTxt.setCaretPosition(0);
			levelSpinner.setValue(editingLevel+1);
			
			if(LevelDataIO.doesTempLevelExist(resource.getId())){
				gridXSpinner.setEnabled(false);
				gridYSpinner.setEnabled(false);
			}else{
				gridXSpinner.setEnabled(true);
				gridYSpinner.setEnabled(true);
			}
			
			configureMenuPopupItem(resource.getBuildings());
			
			addRowFromResource(resource, hook, selectedRow, (editingLevel+1)+" (editing)");

		}
	}
	

	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		if(selectedRow > -1){

			LevelResource resource = tableItems.get(selectedRow);
			
			addToPassedProperties(PropertyKeys.LEVEL_RESOURCE, resource);

			addToPassedProperties(PropertyKeys.LEVEL_RESOURCE_NAME, resource.getInternalTitle());

		}else return "Row must be selected from the table first";
		
		return null;
	}

	@Override
	protected void saveData() {
		
		Resource.replaceLevelResources(tableItems);
		
		LevelDataIO.copyTempToData(tableItems);
		
		Set<String> filenames = new HashSet<String>();
		
		for (LevelResource levelResource : tableItems) {
			
			filenames.add(getFileNameFromPath(levelResource.getMapPath()));
		}

		ImageHelper.copyMapsFromTempToMapsFolder(filenames);
		
		updateButtonLabelWithState(mapButton, "Edit Map", false);
		
		Resource.setXmlstatus(EditorGeneral.getCompletionState());

	}

	@Override
	protected void newButtonClicked() {
		clearFields();
		
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		initFromResource();
		
	}
	

	@Override
	protected boolean deleteActions() {
		boolean enableButton = true;
		return enableButton;
	}
	
	public void mapUpdated(){
		
		System.out.println("dirty map!!!");
		if(editingExistingMap){
			setNewItem(false);
			setPendingImageSaveAndConfigure(true);
		}else{
			setDirtyStateAndConfigure(true);
		}
		updateButtonLabelWithState(mapButton, "Edit Map", true);
		
		LevelResource levelResource = tableItems.get(selectedRow);
		
		Set<Integer> imageIds = new HashSet<Integer>();
		Set<Integer> workerIds = new HashSet<Integer>();
		
		LevelDataIO.populateIdsFromMap(levelResource.getId(), imageIds, workerIds);
		
		LevelDataIO.populateIdsFromBuildingResourceIds(new HashSet<Integer>(levelResource.getBuildings()), imageIds, workerIds);

		levelResource.setWorkers(intSetToArray(workerIds));
		levelResource.setImages(intSetToArray(imageIds));
		
		displayTable(hook, tableItems);
	}

	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(targetButton);		
		if(childDirty) setDirtyChildren(true);		
		updateButtonLabelWithState(targetButton, "Targets", childDirty);
		
		childDirty = isChildGuiXmlWritePending(localizedTextButton);		
		if(childDirty) setDirtyChildren(true);		
		updateButtonLabelWithState(localizedTextButton, "Localized Texts", childDirty);
		
		displayTable(hook, tableItems);

	}



	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
	}
	
	protected String[] getRequiredMapImageFilenamesFromResource(){
		
		Set<String> mapImages =  new HashSet<String>();
		
		for (LevelResource levelResource : tableItems) {
			
			mapImages.add(getFileNameFromPath(levelResource.getMapPath())+".png");
		}
		
		return mapImages.toArray(new String[]{});
	}

	@Override
	public void focusLost(FocusEvent e) {

		super.focusLost(e);
		//solves tabbing and clicking other widgets, but not clicking form itself
		if(e.getComponent()==levelBuildingsButton) menuShowing = menuVisable;
	}
	
	@Override
	protected String getHelpText() {
		return new StringBuilder("The final step is to create levels for the player to play.  Each level will require a map that must contain a warehouse and a set of target items that the player will need to collect/create.\n\n")
						 .append("The Level box allows you to set the level you are working on, by defualt the value will be the next new level, but you can decrease this to insert a level or update an existing level.\n\n")
						 .append("The Name textbox is only used by the editor to help you identify the level.\n\n")
						 .append("Grid X and Grid Y is the total size of the map, the minimum Grid X is 50 and the minimum Grid Y is 25, this will show a map that fills the screen without any scrolling.  The unit of the Grid is the same as the Span that was set against the images.  The maximum size is Grid X and a Grid Y of 150.\n\n")
						 .append("The Map Image is the image that is used as a background on the map (avoid using an image with transparent pixels).  This will be resized to fit the Grid X and Grid Y that was set.\n")
						 .append("The map image will normally be quite large, so be careful that the image size does not become too excessive, try using something like photoshop's 'save for web' option and reduce the number of colours to help reduce the file size.\n")
						 .append("Once you have created your map image, you can use the Browse button which will open a dialog to allow you to select it.\n\n")
						 .append("You can also reuse your map images and help reduce the amount of downloading a player may have to do, by selecting the row on the table that a map image was previously used and clicking the Copy Selected Row button.\n\n")
						 .append("Level Buildings allows you to select the buildings a player is allowed to place for the level (this does not need to include the buildings you plan to place yourself on the map).  You must select at least one building for the player to be able to place.\n\n")
						 .append("When you are happy with your selection, use the Add button to add it to the table.\n\n")
						 .append("You can use the Edit button to edit the level and retain the map and target that have been created.  The Remove button will remove that level (Level numbers will be adjust when removing or inserting a level row).\n\n")
						 .append("After you have added the level requirements to the table, you can create localize text for the level name by selecting the row and clicking the Localized Text button.\n\n")
						 .append("You can create a map for that level by selecting the row and clicking the Edit Map button\n\n")
						 .append("If you want to delete a map without removing the level, select the row and use the Delete Map button.\n\n")
						 .append("The target items that are required to complete the level can be set by selecting the row and clicking the Targets button.").toString();
	}
	
}
