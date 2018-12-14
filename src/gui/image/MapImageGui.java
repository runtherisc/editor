package gui.image;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.LevelDataIO;
import data.map.resources.ImageResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;

public class MapImageGui extends BaseImageGui {

	public MapImageGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	private String movementStr = "Movement";

	@Override
	protected int addComponents(JFrame frame) {
		
		JPanel masterPanel = new JPanel(new GridBagLayout());
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		addGenericImageConponents(panel);
		
      	JButton button = new JButton(movementStr);
    	
      	addGuiButtonAndListener(new ImageMovementGui("Movement Images", frame), button);
      	
      	childrenButtons.put(movementStr, button);
      	
      	panel.add(button, getSlightPaddingGridBagConstraints(0, 3));
		
		masterPanel.add(panel, getNoPaddingGridBagConstraints(0, 0));
      	
		addStaticConponetsToPanel(masterPanel);
      	
      	frame.add(masterPanel, getAllPaddingGridBagConstraints(0, 0));
      	
      	
      	return 1;
	}
	
	@Override
	protected String checkIfItemChanges(int id){
		
		if(!isNewItem()){
			List<MapItemResource> mapItems = Resource.getMapItemResourceList();
			if(mapItems!=null && !mapItems.isEmpty()){
				
				for (MapItemResource mapItemResource : mapItems) {
					
					List<MapItemActionResource> actions = mapItemResource.getMapItemActionList();
					
					for (MapItemActionResource mapItemActionResource : actions) {
					
						if((mapItemResource.getImageResourceId() == id && mapItemActionResource.getMapitem() > -1)
								|| imageIdMatchActionMapitem(mapItemActionResource.getMapitem(), id)){
							
							return "Used in '"+mapItemResource.getName()+"' on it's action: "+mapItemActionResource.getInternalName();
						}
					
					}
				}
			}
		}
		return "";
	};
	
	private boolean imageIdMatchActionMapitem(int mapItemId, int id){
		
		if(mapItemId==-1) return false;
		return Resource.getMapItemResourceById(mapItemId).getImageResourceId() == id;
	}
	
	@Override
	protected void addIdleGui(JButton idleButton){
		
		addGuiButtonAndListener(new ImageSingleIdleGui("Idle Images", frame), idleButton);
	}
	
	@Override
	protected void saveData() {
		
		super.saveData();
		
		imageResource.setMapObject(true);
		
		if(isNewItem()) Resource.addMapImageResource(imageResource);
		
		super.postSaveData();
		
	}
	
	@Override
	protected void initLightFields(){
		
		super.initLightFields();
	}
	
	@Override
	protected List<Integer> getAllImageIds() {
		return Resource.getAllMapImageResourceIds();
	}
	
	@Override
	protected List<ImageResource> getImageResourceList() {
		return Resource.getMapImageResourceList();
	}

	@Override
	protected ImageResource getFirstImageResource() {
		return Resource.getMapImageResource(0);
	}
	
	@Override
	protected ImageResource getImageResourceById(int id) {
		return Resource.getMapImageResourceById(id);
	}
	
	@Override
	protected int getNumberOfImageResources() {
		
		return Resource.getNumberOfMapImageRes();
	}
	
	@Override
	protected void removeImageResourceAndGetNext(ImageResource imageResource) {
		
		Resource.removeMapImageResource(imageResource);
	}

	@Override
	protected String preDeleteChecks(){
		
		int id = imageResource.getId();
		
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		
		if(mapItems!=null && !mapItems.isEmpty()){
			
			for (MapItemResource mapItemResource : mapItems) {
				
				if(mapItemResource.getImageResourceId()==id)
					return "image resource is in use with map item "+mapItemResource.getName();
			}
		}
		
		
		return null;
	}
	
	protected String getOnMapWarning(){
		
		if(onMapWarning==null){
			
			onMapWarning = LevelDataIO.checkMapItemImageResourceIdsUsed(imageResource.getId());
			if(onMapWarning == null) onMapWarning = "";//stop re-check
		}
		
		return onMapWarning;
	}

	@Override
	protected String getHelpText() {
		
		return new StringBuilder("Map Images\n")
						 .append("All images that are required to display a map item are configured here, to be referenced by map item(s) and possibly by buildings that interact with them.\n\n")
						 .append("Name Textbox (Mandatory)\n")
						 .append("The name field is only used in the editor to identify a image set and is not used in the game.\n\n")
						 .append("Span X and Span Y (Mandatory)\n")
						 .append("Span X and Span Y are used to set the size of the images for a map image set.\n")
						 .append("On a device that is a normal screensize, a span of 1 will be equal to 16 pixels.  This will be resized based on screensize.\n")
						 .append("The size of the image span can be between 1 and 9, all images are resized to conform to the span regardless of their original size.\n\n")
						 .append("Static Image (Mandatory)\n")
						 .append("The static image is typically used during map editing.\n")
						 .append("You can add a static image using the Add button below the preview image box after it's span has been set.  Once added, it can be cleared using the same button\n\n")
						 .append("Idle Images (Mandatory)\n")
						 .append("Idle images are displayed when the map item is not being interacted with, created or destroyed\n\n")
						 .append("Walkover Grid (Mandatory)\n")
						 .append("The walkover grid allows you to set which areas the map item is allow to be walked over by a worker.\n\n")
						 .append("Creation (Optional)\n")
						 .append("Creation allows you to set what images are used during the map items creation (eg tree growing)\n\n")
						 .append("Destruction (Optional)\n")
						 .append("Destruction allows you to set what images are used during the map items destruction (eg a tree stump)\n\n")
						 .append("Action (Optional)\n")
						 .append("You can have multiple actions against a map item, for example; images showing a tree being planted or a tree being cut down, etc\n\n")
						 .append("Movement (Optional)\n")
						 .append("Movement allows you to set what images are used for map item that randomly moves about, eg a deer\n\n").toString();
	}
}
