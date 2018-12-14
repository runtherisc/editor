package gui.mapobject;

import java.util.Map;

import javax.swing.JFrame;

import data.map.resources.InfoResource;
import data.map.resources.MapItemResource;
import gui.InfoGui;
import gui.PropertyKeys;

public class MapObjectTextsGui extends InfoGui {

	public MapObjectTextsGui(String title, JFrame parent) {
		super(title, parent, true);
	}
	
	private InfoResource infoResource;
	private String name = null;

	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		MapItemResource mapItemResource = (MapItemResource) properties.get(PropertyKeys.MAP_OBJECT_RESOURCE);
		
		name = (String) properties.get(PropertyKeys.MAP_OBJECT_RESOURCE_NAME);
		
		if(name!=null && name.trim().length()>0){
	
			infoResource = mapItemResource.getInfoResource();
			
			copyTextMap();
		}
	}
	
	@Override
	protected String getFrameTitle(){
		
		return getTitle() + " for " + name;
	}


	@Override
	protected InfoResource getInfoResource() {
		
		System.out.println("items size " + infoResource.getTextMap().size());
		
		return infoResource;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Localized Map Item Texts\n\n")
			   			 .append("When a player click on a map item, a title and description is displayed to give the player information about the map item.\n")
			   			 .append("You must first set a title and description in the default locale.\n")
			   			 .append("Once the default locale has been set, you can then add title and description for any other locale.\n")
			   			 .append("Locale set must conform to ISO-639 (eg: en) and ISO-3166 (eg: en-GB).\n")
			   			 .append("You cannot set a title without a description nor a description without a title.\n")
			   			 .append("When all fields (locale, title and description) have been set, use the Add button to add them to the table before saving.\n")
			   			 .append("You can Edit/Delete previously added values using the Edit and Delete buttons, after clicking the required row in the table.\n").toString();
	}



}
