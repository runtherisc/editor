package gui.image;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.LevelDataIO;
import data.map.resources.BuildingResource;
import data.map.resources.ImageResource;
import data.map.resources.Resource;

public class BuildingImageGui extends BaseImageGui {

	public BuildingImageGui(String title, JFrame parent) {
		super(title, parent);
	}

	private String creationStr = "Creation Req";
	private String destructionStr = "Destruction Req";
	
	@Override
	protected int addComponents(JFrame frame) {
		
		JPanel masterPanel = new JPanel(new GridBagLayout());
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		addGenericImageConponents(panel);
		
		JPanel customPanel = new JPanel(new GridBagLayout());
		
      	JButton creationReqButton = new JButton(creationStr);
      	
      	addGuiButtonAndListener(new ImageCreationRequirementGui("Creation Requirements", frame), creationReqButton);
      	
      	childrenButtons.put(creationStr, creationReqButton);
    	
      	customPanel.add(creationReqButton, getSlightPaddingGridBagConstraints(0, 0));
      	
      	JButton destructionReqButton = new JButton(destructionStr);
      	
      	addGuiButtonAndListener(new ImageDestructionRequirementGui("Destruction Requirements", frame), destructionReqButton);
      	
      	childrenButtons.put(destructionStr, destructionReqButton);
    	
      	customPanel.add(destructionReqButton, getSlightPaddingGridBagConstraints(1, 0));
      	
      	panel.add(customPanel, getSlightPaddingGridBagConstraints(0, 3));
		
		masterPanel.add(panel, getNoPaddingGridBagConstraints(0, 0));
      	
		addStaticConponetsToPanel(masterPanel);
      	
      	frame.add(masterPanel, getAllPaddingGridBagConstraints(0, 0));
      	
      	
      	return 1;

	}
	
	
	
	@Override
	protected void saveData() {
		
		super.saveData();
		
		imageResource.setMapObject(false);
		
		if(isNewItem()) Resource.addBuildingImageResource(imageResource);
		
		super.postSaveData();
		
	}
	
	@Override
	protected String addBundlesOrReturnWarning(String childName){
		
		String issues = super.addBundlesOrReturnWarning(childName);
		
		if(issues!=null) return issues;
		
		if(childName.equals(ImageCreationGui.class.getName())){
			
			if(imageResource.getBuildingCreationList()!=null && !imageResource.getBuildingCreationList().isEmpty()){
				
				JOptionPane.showMessageDialog(null, "Creation Requirements may change depending on the number of images after adjustments", "Creation Requirements may change", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		return null;
	}

	@Override
	protected void initLightFields(){
		
		super.initLightFields();
	}

	@Override
	protected List<Integer> getAllImageIds() {
		return Resource.getAllBuildingImageResourceIds();
	}
	
	@Override
	protected List<ImageResource> getImageResourceList() {
		return Resource.getBuildingImageResourceList();
	}
	
	@Override
	protected ImageResource getFirstImageResource() {
		return Resource.getBuildingImageResource(0);
	}

	@Override
	protected ImageResource getImageResourceById(int id) {
		return Resource.getBuildingImageResourceById(id);
	}

	@Override
	protected int getNumberOfImageResources() {
		
		return Resource.getNumberOfBuildingImageRes();
	}

	@Override
	protected void removeImageResourceAndGetNext(ImageResource imageResource) {
		
		Resource.removeBuildingImageResource(imageResource);
	}

	@Override
	protected String preDeleteChecks(){
		
		int id = imageResource.getId();
		
		List<BuildingResource> buildings = Resource.getBuildingResourceList();
		
		if(buildings!=null && !buildings.isEmpty()){
			
			for (BuildingResource buildingResource : buildings) {
				
				if(buildingResource.getImageResourceId()==id){
					if(buildingResource.isWarehouse()){
						return "image resource is in used with warehouse: "+buildingResource.getName();
					}else{
						return "image resource is in used with building: "+buildingResource.getName();
					}
				}
			}
		}
		
		
		return null;
	}
	
	protected String getOnMapWarning(){
		
		if(onMapWarning==null){
			
			onMapWarning = LevelDataIO.checkBuildingImageResourceUsedInMap(imageResource.getId());
			if(onMapWarning == null) onMapWarning = "";//stop re-check
		}
		
		return onMapWarning;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Building Images\n")
				 .append("All images that are required to display a building are configured here, to be referenced by a building or possibly multiple buildings.\n\n")
				 .append("Name Textbox (Mandatory)\n")
				 .append("The name field is only used in the editor to identify a image set and is not used in the game.\n\n")
				 .append("Span X and Span Y (Mandatory)\n")
				 .append("Span X and Span Y are used to set the size of the images for the building image set.\n")
				 .append("On a device that is a normal screensize, a span of 1 will be equal to 16 pixels.  This will be resized based on screensize.\n")
				 .append("The size of the image span can be between 1 and 9, all images are resized to conform to the span regardless of their original size.\n\n")
				 .append("Static Image (Mandatory)\n")
				 .append("The static image is typically used during map editing and when the player is selecting a building to place.\n")
				 .append("You can add a static image using the Add button below the preview image box after it's span has been set.  Once added, it can be cleared using the same button\n\n")
				 .append("Idle Images (Mandatory)\n")
				 .append("Idle images are displayed when the building is not being interacted with, for example; when the worker is not working from home (eg a sawmill cutting wood) or during creation when it is waiting for resources to be delivered.\n\n")
				 .append("Walkover Grid (Mandatory)\n")
				 .append("The walkover grid allows you to set which areas the building is allow to be walked over by a worker.\n\n")
				 .append("Creation (Optional)\n")
				 .append("Creation allows you to set what images are used during the buildings creation\n\n")
				 .append("Destruction (Optional)\n")
				 .append("Destruction allows you to set what images are used during the buildings destruction\n\n")
				 .append("Action (Optional)\n")
				 .append("You can have multiple actions against a map item, for example; a sawmill cutting wood, these are referenced by the building\n\n")
				 .append("Creation Req (Optional)\n")
				 .append("Although these are not image requirements, Creation requirements are set here so you don't have to set them against the building.  You need to select which idle and items are required by the building during creation at which frame.\n\n")
				 .append("Destruction Req (Optional)\n")
				 .append("Here you can set which items are returned after destruction and which idles are used.").toString();
	}



}
