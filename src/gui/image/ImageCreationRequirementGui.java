package gui.image;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.BuildingCreationResource;
import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.Resource;
import game.ImageHelper;
import game.ImportHelper;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.PropertyKeys;

public class ImageCreationRequirementGui extends ChildBaseGui {

	private String creationInnerFolder = "creation/";// get this from creation?
	private String newEntryText = "Editing New Entry";
	private String addFinalizeButtonText = "Add Final Requirement";
	private String updateFinalizeButtonText = "Update Final Requirement";
	
	private ImageResource imageResource;
	private int maxFrame;
	private int minFrame = 0;
	private int selectedRow = -1;
	private JTable table;
	private ITableUpdateHook hook;
	private JComboBox<String> idleCombo, destructionCombo;
	
	private List<BuildingCreationResource> tableItems, revertTableItems;//index matches table row
	
	private JSpinner spinner;
	private JButton finalizeButton, previewButton;
	private JLabel rowLabel, previewLabel, imageLabel;
	
	private JButton addReq, editReq, deleteReq, itemReq;
	
	private String itemReqStr= "Configure Required Items";
	
	private boolean creationImagesSavePending;
	private Coords span;
	
	private int editingRow = -1;
	
	private List<Integer> importComboIds;
	JComboBox<String> importCombo;
	JButton importButton;

	public ImageCreationRequirementGui(String title, JFrame parent) {
		super(title, parent);
	}

	@Override
	protected int addComponents(JFrame frame) {
		
		JPanel topPanel = new JPanel(new GridBagLayout());
		
		JPanel panel2 = new JPanel(new GridBagLayout());
	
		JPanel panel = new JPanel();
		
		String[] comboItems = imageResource.getIdleNames(false);
		
		idleCombo = addLabelAndComboToPanel(panel, "Idle Seq", 0, 0, comboItems);
		
		spinner = addLabelAndNumberSpinnerToPanel(panel, "From Frame", 0, 0, maxFrame, minFrame);
		spinner.setValue(minFrame);
		
		previewButton = new JButton("Preview:");
		previewButton.addActionListener(this);
		previewButton.addFocusListener(this);
      	panel.add(previewButton);
		
		panel2.add(panel, getSidePaddedGridBagConstraints(0, 0));
		
		panel = new JPanel();
		
		destructionCombo = addLabelAndComboToPanel(panel, "Destruction Seq", 0, 0, comboItems);

		finalizeButton = new JButton(addFinalizeButtonText);
		finalizeButton.addActionListener(this);
		finalizeButton.addFocusListener(this);
      	panel.add(finalizeButton);

      	panel2.add(panel, getSidePaddedGridBagConstraints(0, 1));
      	
      	topPanel.add(panel2, getNoPaddingGridBagConstraints(0, 0));
      	
      	
      	JPanel imagePanel = new JPanel(new GridBagLayout());
      	
      	previewLabel = new JLabel("preview");
        imagePanel.add(previewLabel, getImageLeftPaddedGridBagConstraints(0, 0));
        
        imageLabel = new JLabel();
        Image image = new BufferedImage(ImageHelper.DEFAULT_SIZE,ImageHelper.DEFAULT_SIZE,BufferedImage.TYPE_INT_RGB);
        imageLabel.setIcon(new ImageIcon(image));
        imagePanel.add(imageLabel, getImageLeftPaddedGridBagConstraints(0, 1));
      	

      	topPanel.add(imagePanel, getNoPaddingGridBagConstraints(1, 0));
        
      	frame.add(topPanel, getNoPaddingGridBagConstraints(0, 0));
      	
		JPanel panel3 = new JPanel();
		
		rowLabel = new JLabel(newEntryText);
		rowLabel.setForeground(Color.BLUE);
		
		panel3.add(rowLabel);
      	
      	addReq = new JButton("Add");
      	addReq.addActionListener(this);
      	addReq.addFocusListener(this);
      	panel3.add(addReq);
      	
      	editReq = new JButton("Edit");
      	editReq.addActionListener(this);
      	editReq.setEnabled(false);
      	editReq.addFocusListener(this);
      	panel3.add(editReq);
      	
      	deleteReq = new JButton("Remove");
      	deleteReq.addActionListener(this);
      	deleteReq.setEnabled(false);
      	deleteReq.addFocusListener(this);
      	panel3.add(deleteReq);
      	
      	itemReq = new JButton(itemReqStr);
      	addGuiButtonAndListener(new ImageCreationItemGui("Requirement Items", frame), itemReq);
      	panel3.add(itemReq);

		frame.add(panel3, getRightPaddedGridBagConstraints(0, 2));
		
		Object[] columnNames = new Object[]{"Row", "Idle Sequence", "Destruction Sequence", "Frames", "Creation items", "Destruction items"};
      	int[] sizes = new int[]{40, 150, 150, 80, 110, 110}; 
		
		table = createTable(columnNames, sizes, 0, 3);
		hook = addHookToTable(table);
        
        ListSelectionModel selectionModel = table.getSelectionModel();
        
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				selectedRow = table.getSelectedRow();
//				addToPassedProperties(PropertyKeys.REQUIREMENT_SELECTED_ROW, selectedRow);
				
				if(selectedRow==-1){
					
					editReq.setEnabled(false);
					deleteReq.setEnabled(false);
					itemReq.setEnabled(false);
					
				}else{
	
					deleteReq.setEnabled(true);
					itemReq.setEnabled(true);
					if(tableItems.get(selectedRow).getEndFrame()==-2){
						editReq.setEnabled(false);
					}else{
						editReq.setEnabled(true);
					}
				}
				
			}
        	
        	
        });
        
        frame.add(new JSeparator(), getSidePaddedGridBagConstraints(0, 4));
        
        String[] importItems = configureImageResourceComboItems();
        
        panel = new JPanel();
        
		importCombo = addLabelAndComboToPanel(panel, "Import from another image resource", 0, 0, importItems);
		
		importButton = new JButton("Import Now");
		importButton.addActionListener(this);
		importButton.addFocusListener(this);
      	panel.add(importButton);
      	
      	if(importItems.length==0){
      		
      		importCombo.setEnabled(false);
      		importButton.setEnabled(false);
      	}
      	
      	frame.add(panel, getSidePaddedGridBagConstraints(0, 5));
        
        configureTableMap();
        displayTable(false);
        
        return 6;

	}
	
	protected String[] configureImageResourceComboItems(){
		
		List<ImageResource> allBuildingImageResouirces = Resource.getBuildingImageResourceList();

		List<String> names = new ArrayList<String>();
		importComboIds = new ArrayList<Integer>();

		for (ImageResource resource : allBuildingImageResouirces) {
			
			if(resource.getId() != imageResource.getId() && resource.getBuildingCreationList() != null && resource.getBuildingCreationList().size() > 1){
				
				names.add(resource.getNameFromDir());
				importComboIds.add(resource.getId());
			}			
		}
		
		return names.toArray(new String[]{});
		
	}
	
	@Override
	protected void postDrawGui(){
		
		//copy in case of unsaved changes in the temp folder
		if(creationImagesSavePending){
			
			ImageHelper.copyFromRevertToTemp(maxFrame+1, creationInnerFolder, null, frame);
		}else{
			
			ImageHelper.copyPngFromReourceToTemp(null, maxFrame+1, null, -1, imageResource.getDirectory(), creationInnerFolder, frame);
		}
		
	}
	

	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		Object max = properties.get(PropertyKeys.REQUIREMENT_MAX_FRAME);

		if(max!=null && max instanceof Integer) maxFrame = (int)max -1; else System.out.println("max not set correctly");

		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
		
		creationImagesSavePending = (boolean) properties.get(PropertyKeys.IMAGE_CREATION_SAVE_PENDING);
		
		span = (Coords) properties.get(PropertyKeys.IMAGE_RESOURCE_SPAN);
		
	}
	
	protected void configureTableMap(){
		
		List<BuildingCreationResource> resourceList = imageResource.getBuildingCreationList();	
		
		tableItems = new ArrayList<BuildingCreationResource>();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(BuildingCreationResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
	}
	
	protected void displayTable(boolean overwrite){
		
		if(!overwrite) hook.clearTable();
		
		int startFrame = 0;
		
		int loop = 0;

		for (BuildingCreationResource resource : tableItems) {
			
			int row = overwrite ? loop : -1;
			
			addRowFromResource(resource, row, startFrame, hook);
			
			startFrame = resource.getEndFrame()+1;
			
			loop++;
		}
	}
	
	protected void addRowFromResource(BuildingCreationResource resource, int row, int startFrame, ITableUpdateHook hook) {
		
		int idle = resource.getIdleId();
		String idleName = "<none selected>";
		if(idle > -1) idleName = imageResource.getIdleNameFromId(idle);
		
		int destructionIdle = resource.getDestructionIdleId();
		String destructionIdleName = "<none selected>";
		if(destructionIdle > -1) destructionIdleName = imageResource.getIdleNameFromId(destructionIdle);
		
		String endFrameStr;
		if(resource.getEndFrame() > -1){
			endFrameStr = startFrame + " - " + resource.getEndFrame();
		}else{
			endFrameStr = "complete";
//			setWidgetEnablement(false);
			finalizeButton.setText(updateFinalizeButtonText);
		}
		
		int rowNumber;
		
		if(row==-1) rowNumber = table.getRowCount() + 1;
		else rowNumber = row + 1;	
		
		Object[] data = new Object[]{
				
			rowNumber,
			idleName,
			destructionIdleName,
			endFrameStr,
			resource.getNumberOfCreationItems(),
			resource.getNumberOfDestructionItems()
		};

		hook.addDataRowToTable(data, row);
		
		editingRow = -1;
		rowLabel.setText(newEntryText);
		
	}
	
	protected boolean addOverwriteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Overwrite Entry", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}

	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == deleteReq){

			deleteResourcesFromTable(selectedRow);

		}else if(button == addReq){
			
			System.out.println("add clicked");
			String warning = addResourceToTableAndMap();
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}else if(button == editReq){
			
			System.out.println("edit on row "+selectedRow);
			editResourceFromTable();
			
		}else if(button == finalizeButton){
			
			System.out.println("finalize pressed");
			finalizeRequirements();
		
		}else if(button == previewButton){
			
			String frameToPreview = (String) String.valueOf(spinner.getValue());
			
			ImageHelper.displaySingleImageOnLabelFromTemp(imageLabel, creationInnerFolder, frameToPreview, span);
			
			previewLabel.setText("image:"+frameToPreview);
		
		}else if(button == importButton){
			
			if(confirmImport()) importRequirement();
		}
	}
	
	protected boolean confirmImport(){
		
		String importItem = (String)importCombo.getSelectedItem();
		return JOptionPane.showConfirmDialog(null, "import requirement from "+importItem+"? (Requirements may be adjusted to match)", "Import Requirements?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

	}
	
	protected void importRequirement(){
		
		int id = importComboIds.get(importCombo.getSelectedIndex());
		ImageResource buidlingResourceSource = Resource.getBuildingImageResourceById(id);
		List<BuildingCreationResource> sourceList = buidlingResourceSource.getBuildingCreationList();
		tableItems = new ArrayList<BuildingCreationResource>();
		for (BuildingCreationResource buildingCreationResource : sourceList) {
			tableItems.add(buildingCreationResource.copy());
		}
		ImportHelper.adjustBuildingCreationRequirementEndframe(tableItems, maxFrame);
		ImportHelper.clearBuildingCreationRequirementIdles(tableItems, imageResource.getIdles(), imageResource.getIdleId());
		setDirtyStateAndConfigure(true);
		displayTable(false);
		
	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		if(childName.equals(ImageCreationItemGui.class.getName()) && selectedRow > -1){

			addToPassedProperties(PropertyKeys.IMAGE_CREATION_REQUIREMENT, tableItems.get(selectedRow));
			addToPassedProperties(PropertyKeys.IMAGE_RESOURCE, imageResource);
		
		}else return "Select the row you wish to add item requirements to";
		
		return null;
	}
	
	protected void finalizeRequirements(){
		
		BuildingCreationResource resource;
		
		int idleId = imageResource.getIdleByIndex(idleCombo.getSelectedIndex()).getId();
		int destructionId = imageResource.getIdleByIndex(destructionCombo.getSelectedIndex()).getId();
		
		if(tableItems.isEmpty() || tableItems.get(tableItems.size()-1).getEndFrame()!=-2){
			
			resource = new BuildingCreationResource();
			tableItems.add(resource);
			

			Object[] data = new Object[]{
					
				tableItems.size(),
				idleId,
				destructionId,
				0,
				resource.getNumberOfCreationItems(),
				resource.getNumberOfDestructionItems()
			};
			
			hook.addDataRowToTable(data, -1);
			
		}else{
			
			resource = tableItems.get(tableItems.size()-1);
		}
		
		resource.setEndFrame(-2);
		resource.setIdleId(idleId);
		resource.setDestructionIdleId(destructionId);
		
		setDirtyStateAndConfigure(true);
		
		displayTable(true);
	}
	
	protected void editResourceFromTable(){

		editingRow = selectedRow;
		
		BuildingCreationResource resource = tableItems.get(selectedRow);
		
//		setWidgetEnablement(true);
		
		destructionCombo.setSelectedIndex(imageResource.getIdleIndexFromId(resource.getDestructionIdleId(), false));
		idleCombo.setSelectedIndex(imageResource.getIdleIndexFromId(resource.getIdleId(), false));
		
		if(selectedRow==0) spinner.setValue(0);
		else spinner.setValue(tableItems.get(selectedRow-1).getEndFrame()+1);
		
		rowLabel.setText("Editing Row "+(editingRow+1));
		
	}

	protected void deleteResourcesFromTable(int rowToDelete){
		
		int oldEndFrame = tableItems.get(rowToDelete).getEndFrame();
		
		System.out.println("old end frame "+oldEndFrame);
	
		tableItems.remove(rowToDelete);
		
		hook.removeRow(rowToDelete);
		
		if(tableItems.size() > 0){
		
			int lastEndFrame = tableItems.get(tableItems.size()-1).getEndFrame();
	
			if(lastEndFrame!=-2) finalizeButton.setText(addFinalizeButtonText);			
			else finalizeButton.setText(updateFinalizeButtonText);
			
			if(lastEndFrame!=maxFrame){
				
				ListIterator<BuildingCreationResource> listItr = tableItems.listIterator(tableItems.size());

				//reverse iteration :|
				while(listItr.hasPrevious()) {
					
					BuildingCreationResource buildingCreationResource = listItr.previous();
					
					if(buildingCreationResource.getEndFrame()!=-2 && buildingCreationResource.getEndFrame() < oldEndFrame){
						
						buildingCreationResource.setEndFrame(oldEndFrame);
						break;
					}
				}
			}
			setDirtyStateAndConfigure(true);
			displayTable(true);
		}
	}
	
	
	protected String addResourceToTableAndMap(){
		
		int spinnerValue = (int)spinner.getValue();
		
		//check for existing
		boolean editNext = spinnerValue == 0;
		for (BuildingCreationResource buildingCreationResource : tableItems) {
			
			if(editNext){
				if(addOverwriteConfirmation()){
					buildingCreationResource.setIdleId(imageResource.getIdleByIndex(idleCombo.getSelectedIndex()).getId());
					buildingCreationResource.setDestructionIdleId(imageResource.getIdleByIndex(destructionCombo.getSelectedIndex()).getId());
					displayTable(true);
					setDirtyStateAndConfigure(true);
				}
				return null;
			}
			
			if(buildingCreationResource.getEndFrame() == spinnerValue-1){
				editNext = true;
			}
		}
		
		setDirtyStateAndConfigure(true);
		
		BuildingCreationResource resource;
		
		if(editingRow > 0){
			resource = tableItems.get(editingRow);
			deleteResourcesFromTable(editingRow);
		}else{
			resource = new BuildingCreationResource();
		}
		
		resource.setIdleId(imageResource.getIdleByIndex(idleCombo.getSelectedIndex()).getId());
		resource.setDestructionIdleId(imageResource.getIdleByIndex(destructionCombo.getSelectedIndex()).getId());

		if(tableItems.isEmpty() || (tableItems.size() == 1 && tableItems.get(0).getEndFrame() == 0)){
			
			if(spinnerValue!=0){

				BuildingCreationResource firstResource = new BuildingCreationResource();
				
				firstResource.setEndFrame(spinnerValue-1);
				firstResource.setIdleId(imageResource.getIdleByIndex(0).getId());
				firstResource.setDestructionIdleId(imageResource.getIdleByIndex(0).getId());
				
				addRowFromResource(firstResource, -1, 0, hook);//doesn't matter, just need to add a row
			
				tableItems.add(0, firstResource);
			}
			
			resource.setEndFrame(maxFrame);
			
			addRowFromResource(resource, -1, spinnerValue, hook);//doesn't matter, just need to add a row
			
			if(tableItems.isEmpty() || (tableItems.size() == 1 && tableItems.get(0).getEndFrame() == 0)){
				tableItems.add(0, resource);
			}else{
				tableItems.add(1, resource);
			}
			
		}else{

			int rowcount = 0;
			
//			boolean added = false;
			
			for (BuildingCreationResource buildingCreationResource : tableItems) {
				
				if(spinnerValue <= buildingCreationResource.getEndFrame()){
					
					resource.setEndFrame(buildingCreationResource.getEndFrame());
					
					buildingCreationResource.setEndFrame(spinnerValue-1);

					tableItems.add(rowcount+1, resource);
					
					tempUpdateRow(rowcount, resource);
					
					break;
				}
				rowcount++;
			}

			
		}
		
		displayTable(true);
		
		return null;
	}
	
	//adds a placeholder only!
	private void tempUpdateRow(int rowcount, BuildingCreationResource resource){
		
		Object[] data = new Object[]{
				
			rowcount+1,
			idleCombo.getSelectedItem(),
			destructionCombo.getSelectedItem(),
			resource.getEndFrame(),
			resource.getNumberOfCreationItems(),
			resource.getNumberOfDestructionItems()
		};
		
		hook.insertRow(data, rowcount);
	}

	@Override
	protected void saveData() {
		
		revertTableItems = copyTableItems(tableItems);
		imageResource.setBuildingCreation(copyTableItems(tableItems));
		
	}
	
	protected List<BuildingCreationResource> copyTableItems(List<BuildingCreationResource> sourceList){
		
		ArrayList<BuildingCreationResource> tableItemsCopy = new ArrayList<BuildingCreationResource>();
		
		for (BuildingCreationResource resource : sourceList) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {

		return null;
	}

	@Override
	protected void newButtonClicked() {
	
		editingRow = -1;
		rowLabel.setText(newEntryText);
		
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null) tableItems = copyTableItems(imageResource.getBuildingCreationList());
		else tableItems = copyTableItems(revertTableItems);
		displayTable(false);
	}
	
	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Remove ALL requirements from the table?", "Clear Table", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}
	
	@Override
	protected boolean deleteActions() {
		boolean enableButton = true;
		
		tableItems = new ArrayList<BuildingCreationResource>();
		saveData();
		displayTable(false);
		finalizeButton.setText(addFinalizeButtonText);	
		
		return enableButton;
	}
	
	@Override
	protected void dirtyButtonUpdate() {
		
		super.dirtyButtonUpdate();
		
		boolean childDirty = isChildGuiXmlWritePending(itemReq);
		
		if(childDirty) setDirtyChildren(true);
		
		updateButtonLabelWithState(itemReq, itemReqStr, childDirty);
		
		displayTable(true);

	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Creation Requirements\n")
						 .append("The creation requirements allows you to configue everything that is required for building a building.\n")
						 .append("Although you do not need to configure any images in this section, you will be referencing the creation images that have already been set, and also any idle sequence sets that have been created.\n\n")
						 .append("All idle sets that have been configured in the 'Idle Image' section will appear in the Idle Req and the Destruction Req dropdown combo boxes.\n\n")
						 .append("From the Idle Req, select the idle set that will be displayed when the player first places the building.  You will also need to select the destruction Req that will be displayed if the player destroys the buidling at this stage in construction.\n")
						 .append("You will not need to change the From Frame when it is the first entry in the table, as you must always have the first entry in the table starting from frame 0, If the frame selection form is increased when there is not an enrty starting from 0, one will be added for you, using the first value in the idle Req and destruction Req combo as the values.\n")
						 .append("Next select the next starting frame, this could be, for example; a builder starting to build the frame of the building.\n")
						 .append("(Optional:)To help you find the right constrution frame, select the frame you think is the correct frame and click the Preview: button, this will display the image of the frame that is in the 'From Frame' box\n")
						 .append("When you are happy, select the idle req needed (in our example, this would be a image of the ground fully leveled), and select the destruction req (note: you can reference the same idle set multiple times) and then click the Add button.\n")
						 .append("Repeat this for any other breaks in construction when idle images are needed.\n")
						 .append("Finally, select an idle req and a destruction req needed for when the building has been completed, but this time select 'Update Final Requirement'\n")
						 .append("Update final requirement will put in a complete in the frames box, this allows you to configure construction requirements after the building has completed (eg, returning a builder, sending out a worker to the building from a warehouse).\n\n")
						 .append("Next you will need to configure items and workers going to and from the building during construction.  This is done on the 'Configure Required Items' form.\n")
						 .append("First select to row you wish to configure the Items and Workers that will be going to and from the building and then click the 'Configure Item Requirements' button\n")
						 .append("The items configured on that form will need to be completed in the game before construction will be allowed to continue.\n\n")
						 .append("(Optional:)If you have configured contruction requirements on another building image set that are very simular, you can import these to save time.\n")
						 .append("Care should be taken, for example, adding idle image sets in the same order so the ids match will help, but this can be edited after the import.\n")
						 .append("Importing will replace any rows that have already been added to the table.\n")
						 .append("To Import, select the image set you are importing from and then click the Import Now button").toString();
	}


}
