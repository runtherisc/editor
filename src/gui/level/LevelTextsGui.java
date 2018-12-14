package gui.level;

import java.util.Map;

import javax.swing.JFrame;

import data.map.resources.InfoResource;
import data.map.resources.LevelResource;
import gui.InfoGui;
import gui.PropertyKeys;

public class LevelTextsGui extends InfoGui {

	public LevelTextsGui(String title, JFrame parent) {
		super(title, parent, false);
	}
	
	private InfoResource infoResource;
	private String name = null;

	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		LevelResource levelResource = (LevelResource) properties.get(PropertyKeys.LEVEL_RESOURCE);
		
		name = (String) properties.get(PropertyKeys.LEVEL_RESOURCE_NAME);
		
		if(name!=null && name.trim().length()>0){
	
			infoResource = levelResource.getInfoResource();
			
			copyTextMap();
		}
	}
	
	@Override
	protected String getFrameTitle(){
		
		return getTitle() + " for "+ name;
	}


	@Override
	protected InfoResource getInfoResource() {
		
		System.out.println("items size " + infoResource.getTextMap().size());
		
		return infoResource;
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Level Texts\n\n")
	   			 .append("This is where you can get the level a title.  This will be shown to the player on the main menu.\n\n")
	   			 .append("You must first set a title in the default locale.\n")
	   			 .append("Once the default locale has been set, you can then add title for any other locale.\n")
	   			 .append("Locale set must conform to ISO-639 (eg: en) and ISO-3166 (eg: en-GB).\n")
	   			 .append("When the locale and title have been set, use the Add button to add them to the table before saving.\n")
	   			 .append("You can Edit/Delete previously added values using the Edit and Delete buttons, after clicking the required row in the table.\n").toString();
	}



}
