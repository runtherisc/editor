package gui.image;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.map.resources.BuildingCreationResource;
import data.map.resources.BuildingLifecycleResource;
import data.map.resources.CreationItemResource;
import data.map.resources.DestructionItemResource;
import data.map.resources.LifecycleItemResource;
import data.map.resources.MultiImageResourceAction;
import data.map.resources.Resource;
import gui.ITableUpdateHook;

public class ImageMultiIdleGui extends BaseMultiActionGui {
	
	private int currentMainIdle = -1;
	private int revertMainIdle = -2;
	private JButton setMainButton;

	public ImageMultiIdleGui(String title, JFrame parent) {
		super(title, parent);

	}


	@Override
	protected boolean addSkipToForm() {

		return false;
	}

	@Override
	protected String checkForUsage(MultiImageResourceAction action){
		
		int id = action.getId();
		
		//will be null for map items anyway
		if(imageResource.getBuildingCreationList()!=null){

			int row = 1;
			
			for (BuildingCreationResource resource : imageResource.getBuildingCreationList()) {
				
				if(resource.getIdleId()==id) return "idle used for 'Idle Sequence' on row "+row+" of Creation Requirements";
				if(resource.getDestructionIdleId()==id) return "idle used for 'Destruction Sequence' on row "+row+" of Creation Requirements";	
				for (LifecycleItemResource lifecycleItemResource : resource.getLifecycleItems()) {
					if(lifecycleItemResource.getIdleId()==id && lifecycleItemResource instanceof CreationItemResource) 
						return "idle used on row "+row+" creation item requirements ("+Resource.getItemInternalNameById(lifecycleItemResource.getId())+")";
					if(lifecycleItemResource.getIdleId()==id && lifecycleItemResource instanceof DestructionItemResource) 
						return "idle used on row "+row+" destruction item requirements ("+Resource.getItemInternalNameById(lifecycleItemResource.getId())+")";
				}
				
				row ++;
			}
		}
		
		BuildingLifecycleResource destResource = imageResource.getDestructionResource();
		
		if(destResource != null){
			
			if(destResource.getIdleId()==id) return "idle used for 'Idle Sequence' on Destruction Requirements";
			for (LifecycleItemResource lifecycleItemResource : destResource.getLifecycleItems()) {
				if(lifecycleItemResource.getIdleId()==id) 
					return "idle used on destruction item requirements ("+Resource.getItemInternalNameById(lifecycleItemResource.getId())+")";
			}
		}
		
		return null;
	}

	@Override
	protected void addAdditionalTableButtons(JPanel panel) {

		super.addAdditionalTableButtons(panel);
		
		setMainButton = new JButton("Set as Main Idle");
		setMainButton.addActionListener(this);
		setMainButton.setEnabled(false);
		setMainButton.addFocusListener(this);
      	panel.add(setMainButton);
	}

	@Override
	protected void configureAdditionalButtons(boolean isRowSelected) {
		
		super.configureAdditionalButtons(isRowSelected);
		
		setMainButton.setEnabled(isRowSelected);
	}
	
	@Override
	protected void otherActions(JButton button, JFrame frame) {

		super.otherActions(button, frame);
		
		if(button == setMainButton){
			
			List<MultiImageResourceAction> itemTable = getTable();
			
			int oldRow = -1;

			if(currentMainIdle > -1){
			
				for (int i = 0; i < itemTable.size(); i++) {
					
					if(itemTable.get(i).getId() == currentMainIdle){
						
						oldRow = i;
						break;
					}
				}
				
			}
			
			int newRow = getSelectedRow();
			
			currentMainIdle = itemTable.get(newRow).getId();
			
			if(oldRow!=newRow) setDirtyStateAndConfigure(true);
			
			if(oldRow > -1) addRowFromResource(itemTable.get(oldRow), oldRow);
			addRowFromResource(itemTable.get(newRow), newRow);

		}
		
	}

	@Override
	protected void postDrawGui() {

		super.postDrawGui();
		
		currentMainIdle = imageResource.getIdleId();
		
		revertMainIdle = -2;
		
		for(MultiImageResourceAction resource : getResources()){
			
			if(checkForUsage(resource)!=null){
				setDeleteButtonEnablement(false);
				break;
			}
		}
	}


	@Override
	protected String getInnerPathPrefix(){
		
		return "idle/sequence";
	}
	
	@Override
	protected List<MultiImageResourceAction> getResources(){
		
		return imageResource.getIdles();
	}


	@Override
	protected void setMultiResource(List<MultiImageResourceAction> resources) {
		
		imageResource.setIdles(resources);
		
	}
	
	@Override
	protected Object[] getTableColumnNames() {
		
		return new Object[]{"Name", "Images", "Directory", "Main"};
	}


	@Override
	protected int[] getTableColumnSizes() {
		
		return new int[]{200, 50, 200, 50}; 
	}


	@Override
	protected void addRowFromResource(MultiImageResourceAction resource, int row, ITableUpdateHook hook,
			String sequenceStr) {
		
		String mainIdle = "";
		
		System.out.println("currentMainIdle "+currentMainIdle);

		if(currentMainIdle == -1) currentMainIdle = resource.getId();

		if(resource.getId() == currentMainIdle) mainIdle = "true";
		
		System.out.println("resource.getId() "+ resource.getId() + " currentMainIdle "+currentMainIdle);
		
		Object[] data = new Object[]{
				resource.getInternalName(),
				resource.getTotalNumberImages(),
				sequenceStr+resource.getDirectory(),
				mainIdle			
			};

			hook.addDataRowToTable(data, row);
		
	}
	
	@Override
	protected String checkImageSizeRequirements(int total){
		
		if(total!=1 && total!=2 && total!=4 && total!=8){
			return "The number of idle images must be either 1, 2, 4 or 8 (currently "+total+")";
		}
		
		return null;
	}

	@Override
	protected boolean deleteResourcesFromTable() {
		
		List<MultiImageResourceAction> itemTable = getTable();
		int selectedRow = getSelectedRow();
		
		System.out.println("resource.getId() "+ itemTable.get(selectedRow).getId() + " currentMainIdle "+currentMainIdle);
		
		boolean deletingMain =false;
		
		if(itemTable.get(selectedRow).getId() == currentMainIdle){
			
			deletingMain = true;
		}
		
		//call super
		if(super.deleteResourcesFromTable()){		
		
			if(deletingMain){
				if(itemTable.isEmpty()) currentMainIdle = -1;
				else{
					currentMainIdle = itemTable.get(0).getId();
					addRowFromResource(itemTable.get(0), 0);
				}
			}
		}
		
		return false;
	}
	
	@Override
	protected boolean deleteActions() {
		
		currentMainIdle = -1;
		
		return super.deleteActions();
	}

	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertMainIdle == -2){
			currentMainIdle = imageResource.getIdleId();
		}else{
			currentMainIdle = revertMainIdle;
		}

		super.revertUnsavedChanges(pos);
		

	}

	@Override
	protected void saveData() {
		
		imageResource.setIdleId(currentMainIdle);
		revertMainIdle = currentMainIdle; 
		super.saveData();
		
		
	}
	
	protected int getMaxDisplayImages(){
		return 8;
	}
	
	protected boolean enableImageSlider(){
		return false;
	}


	@Override
	protected String getHelpText() {
		return new StringBuilder("Idle Images\n")
						 .append("Idle images are displayed when the building is not being interacted with, for example; when the worker is not working from home (eg a sawmill cutting wood) or during creation when it is waiting for resources to be delivered.\n\n")
						 .append("The Name textbox is a mandatory field, it is only used within the editor but allows you to identify the idle set during Creation Req and Destruction Req.\n\n")
						 .append("The idle image set can be comprised of 1, 2, 4 or 8 images.  These will be used over a 8 game tick period.\n")
						 .append("if 8 images are supplied then each image will be used, 1 for each game tick that passes, once all 8 images have been used then the sequence will start again.\n")
						 .append("if 4 images are supplied then each image will be used for 2 game ticks each, the sequence will start again when 8 game ticks have passed.\n")
						 .append("if 2 images are supplied then both will be held for 4 game ticks each, and a single image will be used for every game tick (and appear static)\n\n")
						 .append("Use the Add button under the imagebox to add either 1,2,4 or 8 images, the order of these can be changed using the Swap button after being added.\n")
						 .append("The Clear button can be use the remove the last image in the sequence.\n\n")
						 .append("When you are happy with your selection, Click the Add button to add a row to the table.\n")
						 .append("You can select a previous addded row and use the Remove button to remove the row or the Edit button to edit a previously added row\n\n")
						 .append("The first row you add will be set to Main, this set of idle images is used after the building has been created, when a worker is not working at the building.\n")
						 .append("A main idle set is mandatory, if you remove a main idle set the next row in the table will become the main.\n")
						 .append("You can set any row to be a main idle by selecting a row on the table and clicking Set as Main Idle.\n")
						 .append("There can only be one main idle set.").toString();
	}
	
}
