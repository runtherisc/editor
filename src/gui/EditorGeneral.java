package gui;

import java.io.File;

import data.map.resources.Resource;
import game.ConfigIO;

public class EditorGeneral {

	private static String workFolder;
	private static boolean minorIncreased;
	private static boolean majorincreased;
	
	public static String getWorkFolderPath() {
		return workFolder;
	}
	
	public static File getWorkFolder() {
		
    	File path = null;
    	if(workFolder!=null){
    		path = new File(workFolder);
    	}
		return path;
	}
	
	public static void setWorkFolder(String workFolder) {
		
		if(workFolder!=null) workFolder = workFolder.replace("\\", "/");// :|
		
		EditorGeneral.workFolder = workFolder;
	}

	public static void saveWorkFolder() {

		ConfigIO.updateProperty(ConfigIO.LAST_SAVED_KEY, workFolder);
	}

	public static String getCompletionState(){
		
		String state = "complete"; 
		
		if(Resource.getNumberOfItemRes()==0) state = "empty";
		else if(Resource.getNumberOfBuildingImageRes()==0) state = "items";
		else if(Resource.getNumberOfBuildingRes(true)==0) state = "images";
		//could do a level without map objects hence no else
		else if(Resource.getNumberOfLevelRes()==0) state = "buildings";
		
		return state;
	}
	
	public static boolean isMinorIncreased() {
		return minorIncreased;
	}

	public static void setMinorFlag(boolean minorIncreased) {
		EditorGeneral.minorIncreased = minorIncreased;
	}

	public static boolean isMajorincreased() {
		return majorincreased;
	}

	public static void setMajorAndMinorFlag(boolean majorincreased) {
		EditorGeneral.majorincreased = majorincreased;
		setMinorFlag(majorincreased);
	}

	public static String getCompletionText(){
		
		String state = "Project is completed and is playable"; 
		
		if(Resource.getNumberOfItemRes()==0) state = "Project exists but nothing is defined yet";
		else if(Resource.getNumberOfBuildingImageRes()==0) state = "item(s) have been added to the project";
		else if(Resource.getNumberOfBuildingRes(true)==0) state = "image(s) have been added to the project";
		//could do a level without map objects hence no else
		else if(!levelMapsExist()) state = "building(s) have been added to the project";
		else if(Resource.getNumberOfLevelRes()==0) state = "level map(s) have been added to the project";
		
		return state+ ", press Start to Save and continue";
	}
	
	public static boolean levelMapsExist(){
		
		return true;
	}
}
