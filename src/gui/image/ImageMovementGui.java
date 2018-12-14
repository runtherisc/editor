package gui.image;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.MovementImageResource;
import game.ImageHelper;
import gui.ChildBaseGui;
import gui.ITableUpdateHook;
import gui.ImageSliderHook;
import gui.PropertyKeys;

public class ImageMovementGui extends ChildBaseGui implements ImageSliderHook{
	
	private ImageResource imageResource;
	private Coords span;
	private JSpinner skipSpin;

	private List<JButton> imageButtons;
	private JPanel imagePanel;
	
	private JTable table;
	private ITableUpdateHook hook;

	private String movement_warning = "Movement must have all directions set: 8 images in total (clear all by using DELETE)";
	
	private JButton addTexts, deleteTexts;
	private JComboBox<String> combo;
	private boolean[] pendingSaves = new boolean[5];
//	private boolean[] editedRow = new boolean[5];
	private boolean changesPendingAdd = false;
	private int selectedRow = 0;
	private boolean ignoreComboListener = false;
	private List<String> currentImageOrderCopy;
	
	private String movement_str = "Movement";
	private String up_str = "Up Sequence";
	private String right_str = "Right Sequence";
	private String down_str = "Down Sequence";
	private String left_str= "Left Sequence";
		
	//array must match combo order, also save must be updated if altered
	private String[] selectedFolders = new String[]{
		"movement/",
		"movement/up/",
		"movement/right/",
		"movement/down/",	
		"movement/left/"	
	};
	
	private String selectedFolder= selectedFolders[0];
	
	private String mandatory_str = "Mandatory";
	private String optional_str = "Optional";
	
	private String[] movementFilenames = new String[]{"10", "11", "20", "21", "30", "31", "40", "41"};
	
	private String[] movementTitles = new String[]{
			"Up 1",
			"Up 2",
			"Right 1",
			"Right 2",
			"Down 1",
			"Down 2",
			"Left 1",
			"Left 2"
		};
	
	
	

	public ImageMovementGui(String title, JFrame parent) {
		super(title, parent);
		
	}

	@Override
	protected int addComponents(final JFrame frame) {

		Object[] columnNames = {"Action", "Images", "Priority", "status"};
		int[] sizes = new int[]{100, 100, 100, 100}; 
		
		JPanel panel = new JPanel();
		
		combo = new JComboBox<String>();

		//must match selected folders array
		combo.addItem(movement_str);
		combo.addItem(up_str);
		combo.addItem(right_str);
		combo.addItem(down_str);
		combo.addItem(left_str);
		
		combo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if (e.getStateChange() == ItemEvent.SELECTED) {
					
					System.out.println("combo listerner fired");
					
					if(!ignoreComboListener){
	
						if(getImageOrder(selectedFolders[0])!=null && getImageTotal(selectedFolders[0])==8){
					
							String item = (String)e.getItem();
							clearWarning();
							postComboSelection(item);
						
						}else{
							
							displayWarning("Please set Movement images first");		
							ignoreComboListener = true;
							selectedRow = 0;
							selectedFolder = selectedFolders[0];
							addTexts.setText("Add "+movement_str);
							deleteTexts.setText("Clear "+movement_str);
							combo.setSelectedIndex(0);
							table.getSelectionModel().setLeadSelectionIndex(selectedRow);
						}
						
					}else{
						System.out.println("ignoring combo listener");
						ignoreComboListener = false;
					}

				}
				
			}
		});
		
		panel.add(combo);
		
//		skipSpin = addLabelAndTextFieldToPanel(panel, "Skip", 0, 0, 2, true);
		
		skipSpin = addLabelAndNumberSpinnerToPanel(panel, "Skip", 0, 0, 20, 1);

		skipSpin.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(isFormReady())  setDirtyStateAndConfigure(true);
			}
		});

		frame.add(panel, getAllPaddingGridBagConstraints(0, 0));
		
        imageButtons = new ArrayList<JButton>();
		frame.add(imagePanel = getMultiImageSelection(null, imageButtons, true), getRightPaddedGridBagConstraints(0, 1));
		
		panel = new JPanel();
      	
      	addTexts = new JButton("Add");
      	addTexts.addActionListener(this);
      	addTexts.addFocusListener(this);
      	panel.add(addTexts);

      	deleteTexts = new JButton("Clear");
      	deleteTexts.addActionListener(this);
      	deleteTexts.addFocusListener(this);
      	panel.add(deleteTexts);

		frame.add(panel, getRightPaddedGridBagConstraints(0, 2));
		
		table = createTable(columnNames, sizes, 0, 4);
		hook = addHookToTable(table);
		
        ListSelectionModel selectionModel = table.getSelectionModel();
               
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int selectedRow = table.getSelectedRow();
				
				if(selectedRow > -1 && selectedRow < 5){
					
					combo.setSelectedIndex(selectedRow);
				}
				
			}
        	
        	
        });
 
        pendingSaves = new boolean[5];
        changesPendingAdd = false;
        
        clearImageOrder();
        initTable(imageResource.getMovement());
        initImageSliderFromPanel(imagePanel);
		
        return 5;
	}
	
	
	@Override
	protected void imageSliderUpdated(int value){
		
		System.out.println("imageSliderUpdated called");
		
		if(!isDisableImageSlider()){
			
			ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(getSelectedFolder()), getImageTotal(getSelectedFolder()), Integer.MAX_VALUE, value, getSelectedFolder());		
		}
	}
	
	private void postComboSelection(String selection){
		
		if(changesPendingAdd){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Add changes to table before changing?", "Add To Table", JOptionPane.YES_NO_CANCEL_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
	
				String warning = addResourceToTable(true);
				if(warning!=null){
					displayWarning(warning);
					cancelComboChange();
					return;
				}
				
			}else if(dialogResult == JOptionPane.CANCEL_OPTION){
				
				cancelComboChange();
				return;
				
			}else{// NO
				
				if(currentImageOrderCopy!=null){
					
					List<String> imageOrder = new ArrayList<String>();
					for (String string : currentImageOrderCopy) {
						
						imageOrder.add(string);
					}
					addImageOrder(selectedFolders[selectedRow], imageOrder);
					addImageTotal(selectedFolders[selectedRow], imageOrder.size());
					changesPendingAdd = false;
					updateRowStatus(selectedRow, hook.getSelectedRow(selectedRow));
					
				}else System.out.println("oops, current image order was null, should of been set");
			}
		}
		
		selectedRow = combo.getSelectedIndex();
		
		addTexts.setText("Add "+selection);
		deleteTexts.setText("Clear "+selection);
	
		table.getSelectionModel().setLeadSelectionIndex(selectedRow);
		
		initFields();

	}
	
	private void cancelComboChange(){
		
		System.out.println("selectedRow: "+selectedRow);
		ignoreComboListener = true;
		table.getSelectionModel().setLeadSelectionIndex(selectedRow);
		combo.setSelectedIndex(selectedRow);	
	}
	
	protected void postDrawGui(){
		
		if(isRevertMapSet()) revertUnsavedChanges(-1);

//		postComboSelection(movement_str);
		initFields();

		
	}
	
	protected void initFields() {
		
		setFormReady(false);
		
		if(imageResource.getMovement()!=null) skipSpin.setValue((int)imageResource.getMovement().getSkip());
		else skipSpin.setValue(1);
		
		zeroImageSliderValue();
		displayImages(0);
		
		setFormReady(true);

	}
	
	protected void initTable(MovementImageResource movement){
		
		System.out.println("is movement null?"+movement==null);
		
		addRowToTable(movement_str, movement==null ? 0 : 8, mandatory_str, "", table.getRowCount() > 0 ? 0 : -1);
		addRowToTable(up_str, movement==null ? 0 : movement.getUp(), optional_str, "", table.getRowCount() > 1 ? 1 : -1);
		addRowToTable(right_str, movement==null ? 0 : movement.getRight(), optional_str, "", table.getRowCount() > 2 ? 2 : -1);
		addRowToTable(down_str, movement==null ? 0 : movement.getDown(), optional_str, "", table.getRowCount() > 3 ? 3 : -1);
		addRowToTable(left_str, movement==null ? 0 : movement.getLeft(), optional_str, "", table.getRowCount() > 4 ? 4 : -1);
		
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
		
		span = (Coords) properties.get(PropertyKeys.IMAGE_RESOURCE_SPAN);

	}
	
	protected void displayImages(int offset){
		
		int total;
		String[] labelNames = null;
		int comboSelection = combo.getSelectedIndex();
		
		setSelectedFolder(comboSelection);
		
//		if(getImageOrder(getSelectedFolder())==null){
			
		List<String> imageOrder = getImageOrder(getSelectedFolder());
		
		MovementImageResource movementResource = imageResource.getMovement();
		
		int totalImages = movementResourceTotalFromSelectionArrayPosition(comboSelection, movementResource);
		String[] filenames = null;
		
		if(comboSelection == 0){
			
			total = 8;
			filenames = movementFilenames;
			labelNames = movementTitles;
			enableImageSlider(false);
		}else{
			
			total = Integer.MAX_VALUE;
			enableImageSlider(true);
		}
			
		if(imageOrder==null){
			
			imageOrder = new ArrayList<String>();
			
			if(imageResource.getDirectory()!=null){

				ImageHelper.copyPngFromReourceToTemp(filenames, totalImages, null , -1, imageResource.getDirectory(), getSelectedFolder(), frame);
				
				for (int i = 0; i < totalImages; i++) {
					if(filenames!=null) imageOrder.add(filenames[i]);
					else imageOrder.add(String.valueOf(i));
				}
			}
			
			addImageOrder(getSelectedFolder(), imageOrder);
			addImageTotal(getSelectedFolder(), imageOrder.size());
			
		}
		
		currentImageOrderCopy = new ArrayList<String>();
		for (String string : imageOrder) {
			
			currentImageOrderCopy.add(string);
		}
		
		setDisableImageSlider(true);

		ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(getSelectedFolder()), getImageTotal(getSelectedFolder()), total, offset, getSelectedFolder(), labelNames);

		setDisableImageSlider(false);
	}
	
	private void addRowToTable(String action, int images, String priority, String status, int row){
		
		Object[] data = new Object[]{
				action,
				images,	
				priority,
				status
			};

			hook.addDataRowToTable(data, row);
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);

		
		int buttonPos = imageButtons.indexOf(button);
		
		if(buttonPos > -1){
			
			//add button
			int oldAmount = buttonPos + getImageSliderValue() == getImageTotal(getSelectedFolder()) ? getImageTotal(getSelectedFolder()) : -1;
			
			//other buttons that are not blank
			boolean validButton = buttonPos + getImageSliderValue() < getImageTotal(getSelectedFolder());
			
			int total = (getSelectedFolder()==selectedFolders[0] ? 8 : Integer.MAX_VALUE);
			
			ImageHelper.imageButtonClicked(buttonPos, getImageSliderValue(), getImageOrder(getSelectedFolder()), getImageTotal(getSelectedFolder()), span, imagePanel, null, total, getSelectedFolder(), this, frame);
			
			//add was pressed, but was it cancelled?
			if(oldAmount > -1) validButton = getImageTotal(getSelectedFolder()) != oldAmount;
			
			
			
			if(validButton){
				
				int index = combo.getSelectedIndex();
				changesPendingAdd = true;
				updateRowStatus(index, hook.getSelectedRow(index));
			}
			
		}else if(button == deleteTexts){

			deleteResourcesFromTable();

		}else if(button == addTexts){
			
			System.out.println("add clicked");
			String warning = addResourceToTable(false);
			if(warning!=null) displayWarning(warning);
			else System.out.println("no warning");

		}
		
		
	}
	
	protected void deleteResourcesFromTable(){
		
		int index = combo.getSelectedIndex();
		
		Object[] objects = hook.getSelectedRow(index);
		objects[1] = 0;
		hook.addDataRowToTable(objects, index);
		
		List<String> imageOrder = new ArrayList<String>();
		addImageOrder(getSelectedFolder(), imageOrder);
		addImageTotal(getSelectedFolder(), imageOrder.size());
		
		displayImages(0);
		
		setPendingImageSaveAndConfigure(true);

	}
	
	protected String addResourceToTable(boolean supressConfirmation){
		
		Object[] object = hook.getSelectedRow(selectedRow);
		
		if(getSelectedFolder()==selectedFolders[0] && getImageTotal(getSelectedFolder())!=8)
			return movement_warning;
		
		//already exists confirmation
		if((Integer)object[1] !=0 && !supressConfirmation && !confirmOverwrite()) return null;

		pendingSaves[selectedRow]=true;
		changesPendingAdd = false;
		
		List<String> imageOrder = getImageOrder(getSelectedFolder());
		
		object[1] = getImageTotal(getSelectedFolder());
		
		updateRowStatus(selectedRow, object);
		
		setPendingImageSaveAndConfigure(true);
		
		currentImageOrderCopy = new ArrayList<String>();
		for (String string : imageOrder) {
			
			currentImageOrderCopy.add(string);
		}
		
		return null;
	}
	
	protected void updateRowStatus(int index, Object[] object){
		
		StringBuilder status = new StringBuilder();
		
		if(pendingSaves[index]) status.append("*Not Saved");
		
		if(changesPendingAdd){
			if(status.length()!=0) status.append(", ");
			status.append("Add Pending");
		}
		
		object[3] = status.toString();
		
		hook.addDataRowToTable(object, index);
	}
	
	protected void clearFields(){
		
//		addImageOrder(getInnerFolders(), new ArrayList<String>());
//		nameTxt.setText("");
		setFormReady(false);
		skipSpin.setValue(1);
		setFormReady(true);
	}
	
	
	private boolean confirmOverwrite(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Replace existing entry?", "Name already exists", JOptionPane.YES_NO_OPTION);
		
		return dialogResult == JOptionPane.YES_OPTION;
	}

	@Override
	protected void saveData() {
		
		MovementImageResource movementResource = imageResource.getMovement();
		
		if(movementResource==null) movementResource = new MovementImageResource();

//		ValidationHelper validationHelper = new ValidationHelper();
//		validationHelper.validateInt("Skip", skipSpin.getText(), 1, 10, false);
		
		movementResource.setSkip((int)skipSpin.getValue());
		
		if(isPendingImageSave()){
			
			for (int i = 0; i < selectedFolders.length; i++) {
				
				List<String> imageOrder = getImageOrder(selectedFolders[i]);
				
				int total = 0;
				
				if(imageOrder !=null){

					total = getImageTotal(selectedFolders[i]);
		
					if(total > 0) ImageHelper.copyFromTempToRevert(total, selectedFolders[i], imageOrder, frame);
	
					//dependent on the selectedFolders array
					switch(i){
						case 1:
							movementResource.setUp(total);
							break;
						case 2:
							movementResource.setRight(total);
							break;
						case 3:
							movementResource.setDown(total);
							break;
						case 4:
							movementResource.setLeft(total);
							break;
					}
				}else{

					total = movementResourceTotalFromSelectionArrayPosition(i, movementResource);

					if(total > 0) ImageHelper.copyPngFromReourceToRevert(null, total, null, -1, imageResource.getDirectory(), selectedFolders[i], frame);
				}
				System.out.println("setting revert for "+selectedFolder+" total images:"+total);
				setRevertAmount(selectedFolders[i], total);
				
			}

		}
		
		pendingSaves = new boolean[5];
		selectedRow = 0;
		
		imageResource.setMovement(movementResource);
		
		initTable(movementResource);
	}
	
	private int movementResourceTotalFromSelectionArrayPosition(int pos, MovementImageResource movementResource){
		
		
		if(movementResource != null){
		
			switch(pos){
				case 0: 
					return 8;
				case 1:
					return movementResource.getUp();
				case 2:
					return movementResource.getRight();
				case 3:
					return movementResource.getDown();
				case 4:
					return movementResource.getLeft();
				default:
					System.out.println("unknown item with pos "+pos);
			}
		}

		return 0;
	}

	
	@Override
	protected void revertUnsavedChanges(int pos) {
		
		for (int i=0; i<selectedFolders.length; i++) {
			
			int total = getRevertAmount(selectedFolders[i]);
			
			boolean fromResource = false;
			
			System.out.println(selectedFolders[i]+" revert total "+total);
		
			if(total == 0){
				
				MovementImageResource movementResource = imageResource.getMovement();
				fromResource = true;
				total = movementResourceTotalFromSelectionArrayPosition(i, movementResource);
			}
			
			if(fromResource){
				
				addImageOrder(selectedFolders[i], null);
				addImageTotal(selectedFolders[i], 0);
				
			}else{
				ImageHelper.copyFromRevertToTemp(total, selectedFolders[i], null, frame);
				
				ArrayList<String> imageOrder = new ArrayList<String>();
				
				for (int j = 0; j < total; j++) {
					imageOrder.add(String.valueOf(j));
				}
				
				addImageOrder(selectedFolders[i], imageOrder);
				addImageTotal(selectedFolders[i], imageOrder.size());
			}
		}
		
		pendingSaves = new boolean[5];
		
		if(selectedRow > 0){
			ignoreComboListener = true;
			selectedRow = 0;
			selectedFolder = selectedFolders[0];
			addTexts.setText("Add "+movement_str);
			deleteTexts.setText("Clear "+movement_str);
			combo.setSelectedIndex(0);
			table.getSelectionModel().setLeadSelectionIndex(selectedRow);
		}

		initTable(imageResource.getMovement());
		initFields();
		displayImages(0);
		
	}


	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
//		ValidationHelper validationHelper = new ValidationHelper();
//		if(!validationHelper.validateInt("Skip", skipSpin.getText(), 1, 10, true))
//			return validationHelper.getWarning();
		
		if(getImageOrder(selectedFolders[0])==null || getImageTotal(selectedFolders[0]) != 8)
			return movement_warning;
		
		Object[] object = hook.getSelectedRow(selectedRow);
		if((Integer)object[1] == 0) return "add changes to the table before saving";
		
		if(changesPendingAdd){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Add changes to table before saving?", "Add To Table", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
	
				return addResourceToTable(true);
				
			}else{

				return "save cancelled";
			}
		}
		
		
		System.out.println("no issues saving...");
		
		return null;
	}

	@Override
	protected void newButtonClicked() {
		
		if(anyFieldsHaveAValue()){
			
			int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all fields?", "Fields not empty", JOptionPane.YES_NO_OPTION);
			
			if(dialogResult == JOptionPane.YES_OPTION){
				
				clearFields();
				addImageOrder(getSelectedFolder(), new ArrayList<String>());
				addImageTotal(getSelectedFolder(), 0);
				
				int comboSelection = combo.getSelectedIndex();
				
				String[] labelNames = comboSelection==0 ? movementTitles : null;
				int total = comboSelection==0 ? 8 : Integer.MAX_VALUE;
				
				setDisableImageSlider(true);
				ImageHelper.displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, getImageOrder(getSelectedFolder()), getImageTotal(getSelectedFolder()), total, 0, getSelectedFolder(), labelNames);
				setDisableImageSlider(false);
				changesPendingAdd = true;
				updateRowStatus(selectedRow, hook.getSelectedRow(selectedRow));
			}
		}
		
	}
	
	protected boolean anyFieldsHaveAValue(){
		
		return (getImageOrder(getSelectedFolder())!=null && getImageTotal(getSelectedFolder()) > 0);
	}
	
	@Override
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Clear ALL actions from the table?", "Clear Table", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}

	@Override
	protected boolean deleteActions() {
		//for image cleanup
		for (String innerFolder : selectedFolders) {
			addImageOrder(innerFolder, new ArrayList<String>());
			addImageTotal(innerFolder, 0);
		}
		currentImageOrderCopy = new ArrayList<String>();
		imageResource.setMovement(null);
//		saveData();
		initFields();
		initTable(imageResource.getMovement());
		combo.setSelectedIndex(0);
		return true;
	}

	public String getSelectedFolder() {
		return selectedFolder;
	}

	public void setSelectedFolder(int index) {
		
		this.selectedFolder = selectedFolders[index];
	}



	@Override
	public void updatedImageTotal(int total) {
		addImageTotal(getSelectedFolder(), total);
		
	}
	
	@Override
	protected String getHelpText() {
		
		return new StringBuilder("Movement Images\n\n")
						 .append("This is where you can add images for a map item's movement (eg.a deer).\n")
						 .append("If movement images exist, then the map item will move, it does not need to be set on the map item itself.\n")
						 .append("The movement ingame is random and can show the map item moving left, right, up or down.  It can also show the map item in a stationary position facing left, right, up or down.\n")
						 .append("If you want you map item to move you must set all eight movement images, which are two images in each direction.\n")
						 .append("The stationary images are optional in all directions and can be as many images are you like, including a different amount of images in each direction.\n\n")
						 .append("Before you are allowed to set the stationary images, you must set the movement images.\n")
						 .append("These must be a total of eight images, two in each direction.  Click the Add button under the image box and select the images that you want.\n")
						 .append("You can reorder them by using the Swap button and remove the left one by clicking on the Clear button.\n")
						 .append("When you are happy with the order, click the Add Movement button to add them to the table.\n")
						 .append("Once they have been added to the table, you will be able to Save your changes.\n")
						 .append("You will also be able to select any of the stationary directions, you can do this by either select the one you want from the dropdown combo box at the top of the screen, or by clicking on the row on the table.\n")
						 .append("After adding stationary images, you can browse them by dragging the slider below the images, if you have more than eight images added.\n")
						 .append("As soon as you make your selection form the dropdown combo or by clicking the table row, your images will be availible for editing.\n")
						 .append("You can remove images from the table and the image boxes by using the Clear <Item> Sequence button (eg Clear Right Sequence)\n\n")
						 .append("The skip allows you to set the number of frames a stationary image is held for, before the next image is shown.\n")
						 .append("This is a global setting that is for all directions, note that the value of skip has no bearing on the movement images.\n").toString();
	}


}
