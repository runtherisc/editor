package gui.building;

import java.util.Map;

import javax.swing.JFrame;

import data.map.resources.BuildingResource;
import data.map.resources.InfoResource;
import gui.InfoGui;
import gui.PropertyKeys;

public class BuildingTextsGui extends InfoGui {

	public BuildingTextsGui(String title, JFrame parent) {
		super(title, parent, true);
	}
	
	private InfoResource infoResource;
	private String name = null;

	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		BuildingResource buildingResource = (BuildingResource) properties.get(PropertyKeys.BUILDING_RESOURCE);
		
		name = (String) properties.get(PropertyKeys.BUILDING_RESOURCE_NAME);
		
		if(name!=null && name.trim().length()>0){
	
			infoResource = buildingResource.getInfoResource();
			
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
		return new StringBuilder("Building Texts\n\n")
	   			 .append("When a player selects a building or a warehouse to be place, some text is displayed that you can define here. If you wish to format the text with line breaks, you can by using html line breaks, eg <br>\n\n")
	   			 .append("You must first set a title and description in the default locale.\n")
	   			 .append("Once the default locale has been set, you can then add title and description for any other locale.\n")
	   			 .append("Locale set must conform to ISO-639 (eg: en) and ISO-3166 (eg: en-GB).\n")
	   			 .append("You cannot set a title without a description nor a description without a title.\n")
	   			 .append("When all fields (locale, title and description) have been set, use the Add button to add them to the table before saving.\n")
	   			 .append("You can Edit/Delete previously added values using the Edit and Delete buttons, after clicking the required row in the table.\n").toString();
	}



}
