package gui;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import data.map.resources.BuildingActionResource;
import data.map.resources.BuildingResource;
import data.map.resources.InfoResource;
import data.map.resources.ItemResource;
import data.map.resources.LevelResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import data.map.resources.ResourceParser;
import data.map.resources.ResourceWriter;
import game.DirBrowser;
import game.ImageHelper;

public class EntryMenuGui extends BaseGui {
	
	private JTextField infoTextField, versionTxt, localeTxt, codenameTxt;
	private JTextFieldWithDocListener browseBox;
	private JButton browseButton, startButton, helpButton;
	private static boolean clearFields = false;
	private boolean newProject;
	

	public EntryMenuGui() {
		super("Global Information");
	}

	@Override
	protected int addComponents(JFrame frame) {
		        
        JPanel panel = new JPanel(new GridBagLayout());
        
	  	browseBox = new JTextFieldWithDocListener(40){
	
			private static final long serialVersionUID = 1L;

			@Override
			public void didFieldChange(boolean changed) {

				String browseStr = browseBox.getText();
				Resource.init();//clear old data
		        if(attemptXmlLoad(browseStr)){
					EditorGeneral.setWorkFolder(browseStr);
		        }
		        fillForm(new File(browseStr));

			}

			@Override
			public void doImageRefresh() {
				//do nothing
			}
 
      	};
        panel.add(browseBox, getSidePaddedGridBagConstraints(0, 0));

        browseButton = new JButton("browse...");
        panel.add(browseButton, getSidePaddedGridBagConstraints(1, 0));
        
        browseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				File selectedDir = new DirBrowser().dirSelectorDiag(EditorGeneral.getWorkFolderPath(), "browse to an existing project or an empty directory");
				if(selectedDir!=null){
					String path = selectedDir.getAbsolutePath();
					browseBox.setText(path);
					
				}else System.out.println("dir was null");
			}
		});
        
        frame.add(panel, getAllPaddingGridBagConstraints(0, 0));
        
        JPanel panel2 = new JPanel(new GridBagLayout());
        
        codenameTxt = addLabelAndTextFieldToPanel(panel2, "Game Title", 0, 0, 15, true);
        versionTxt = addLabelAndTextFieldToPanel(panel2, "Version", 2, 0, 2, true);
        localeTxt = addLabelAndTextFieldToPanel(panel2, "Default Locale", 4, 0, 2, true);
        
        
        helpButton = new JButton("View Help");
        panel2.add(helpButton, getSidePaddedGridBagConstraints(6, 0));
        helpButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        helpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				new HelpMenu(getTitle(), getHelpText());
			}
        });

        
        frame.add(panel2, getAllPaddingGridBagConstraints(0, 1));
        
        JPanel panel3 = new JPanel(new GridBagLayout());
        		
        infoTextField = new JTextField(40);
        infoTextField.setForeground(Color.RED);
        infoTextField.enableInputMethods(false);
        infoTextField.setEditable(false);
        infoTextField.setHorizontalAlignment(JLabel.CENTER);
        
        panel3.add(infoTextField, getSidePaddedGridBagConstraints(0, 0));
        
        startButton = new JButton("Start");
        panel3.add(startButton, getSidePaddedGridBagConstraints(1, 0));
        startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(validatePath() && validateTextFields()){
				
					int major = getMajorFromVersion();
					int minor = getMinorFromVersion();
					
					boolean allGo = true;

					if(major < Resource.getMajorVersion() || minor < Resource.getMinorVersion()){
						allGo = confirmLowerVersion();
					}else if(major > Resource.getMajorVersion() || !isProjectComplete()){
						EditorGeneral.setMajorAndMinorFlag(true);
					}else if(minor > Resource.getMinorVersion()){
						EditorGeneral.setMinorFlag(true);
					}

					if(allGo){
					
						System.out.println("GO GO GO!!!");
						
						File file = new File(EditorGeneral.getWorkFolder(), ResourceWriter.RESOURCE_FILENAME);
	//					if(!file.exists()){
	//						System.out.println("creating xml for new project");
							
						Resource.setCodename(codenameTxt.getText());
						Resource.setMinorVersion(minor);
						Resource.setMajorVersion(major);
						Resource.setDefaultLocale(localeTxt.getText());
						Resource.setXmlstatus(EditorGeneral.getCompletionState());
						
						new ResourceWriter().writeXml(file);
	//					}
						
						
						EditorGeneral.saveWorkFolder();
						
						ImageHelper.deleteTempFolder();//? keep?
						
						closeGui();
						new MainMenuGui();
					}
				}
				
			}
		});
        
        frame.add(panel3, getAllPaddingGridBagConstraints(0, 2));
        
        if(EditorGeneral.getWorkFolderPath()==null || EditorGeneral.getWorkFolderPath().trim().length()==0){
        	infoTextField.setText("Browse to an empty folder or an existing little hoarders project");
        }else{
        	browseBox.setText(EditorGeneral.getWorkFolderPath());
        }

		return 3;
	}
	
	private boolean isProjectComplete(){
		
		return Resource.getNumberOfLevelRes() > 0;
	}
	
	private boolean confirmLowerVersion(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Version number is less than previous version, are you sure you want that?", "Decrease Version Number", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			EditorGeneral.setMajorAndMinorFlag(true);
			return true;
		}
		
		return false;
	}
	
	private boolean validatePath(){
		
		String path = browseBox.getText();

		if(path!=null && path.length() > 0){
			
			File file = new File(path);
			
			if(new File(file, ResourceWriter.RESOURCE_FILENAME).exists() ||
					(file.isDirectory() && file.list().length == 0)) return true;
			
		}
		
		return false;
	}
	
	private void fillForm(File path){

        if(newProject){
        	
        	if(clearFields){

            	versionTxt.setText("");
            	localeTxt.setText("");
            	codenameTxt.setText("");
            	clearFields = false;
            	
        	}
        	
        	if(path==null || !path.isDirectory() || path.list().length > 0){
        		infoTextField.setText("Browse to an empty folder or an existing little hoarders project");
        		
        	}else{
        	
        		validateTextFields();
        		EditorGeneral.setWorkFolder(path.getAbsolutePath());
        	}

        }else{
        	infoTextField.setText(EditorGeneral.getCompletionText());
        	versionTxt.setText(String.valueOf(Resource.getVersion()));
        	localeTxt.setText(Resource.getDefaultLocale());
        	codenameTxt.setText(Resource.getCodename());
        	clearFields = true;//the xml set the fields, browsing to a new project should clear them
        }
	}
	
	private boolean validateTextFields(){
		
		if(codenameTxt.getText().trim().length()==0){
			infoTextField.setText("Enter a descriptive name");
			return false;
		}else if(versionTxt.getText()=="" || !isAFloat(versionTxt.getText())){
			infoTextField.setText("Enter a valid version number (eg 1.0)");
			return false;
		}else if(!validateLocale(localeTxt.getText())){
			infoTextField.setText("Enter a valid locale (eg en)");
			return false;
		}
		String localeCheck = checkTextsForLocale(localeTxt.getText());
		if(localeCheck!=null){
			infoTextField.setText(localeCheck);
			return false;
		}
		
		if(newProject){
			infoTextField.setText("Press Start when you are happy!");
			EditorGeneral.setMajorAndMinorFlag(true);
		}else{
			infoTextField.setText(EditorGeneral.getCompletionText());	
			EditorGeneral.setMajorAndMinorFlag(false);
		}
		return true;
	}
	
	protected boolean validateLocale(String localeStr){
		
		if(localeStr != null){ 
			
			localeStr = localeStr.trim();
			Pattern pattern = Pattern.compile("^[a-zA-Z]{2}([-\\_][a-zA-Z]{2})?$");
		    if (pattern.matcher(localeStr).matches()) {
		        
		    	return true;
		    }
			
		}
		
		return false;
	}
	
	private boolean isAFloat(String number){
		
		try{
			
			Float.parseFloat(number);
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	private boolean attemptXmlLoad(String path){
		
		boolean success = false;
		
		if(path!=null && new File(path).isDirectory()){
			
			if(!path.endsWith("/")) path = path +"/";

			//restore
	    	try {
				new ResourceParser().parseDocument(path + ResourceWriter.RESOURCE_FILENAME);
				success = true;
				
			//TODO improve exception catching
			} catch (SAXException | ParserConfigurationException | IOException e) {
				
				System.out.println("failed to load last saved: "+e.getMessage());
			}
		    	
		}
		
		newProject = !success;
		
		return success;
	}
	
	@Override
	protected JTextField addLabelAndTextFieldToPanel(JPanel panel, String name, int gridx, int gridy, int textSpan, boolean isEnabled){
        
        JLabel nameLab = new JLabel(name);
        nameLab.setEnabled(isEnabled);
        panel.add(nameLab, getSidePaddedGridBagConstraints(gridx, gridy));

      	JTextField nameTxt = new JTextFieldWithDocListener(textSpan){
	
			private static final long serialVersionUID = 1L;

			@Override
			public void didFieldChange(boolean changed) {
				
				validateTextFields();
			}

			@Override
			public void doImageRefresh() {
				//do nothing
			}
 
      	};
      	
      	nameTxt.setEnabled(isEnabled);
        panel.add(nameTxt, getSidePaddedGridBagConstraints(gridx + 1, gridy));
        
        return nameTxt;
	}
	
	protected String checkTextsForLocale(String locale){
		
		List<ItemResource> itemList = Resource.getItemResourceList();
		if(itemList!=null && !itemList.isEmpty()){
			for (ItemResource itemResource : itemList) {
				
				if(!checkInfoContainsLocale(locale, itemResource.getInfoResource())){
					return "Text for locale '"+locale+"' not set on item: "+itemResource.getName();
				}
			}
		}
		
		List<MapItemResource> mapItemList = Resource.getMapItemResourceList();
		if(mapItemList!=null && !mapItemList.isEmpty()){
			for (MapItemResource mapItemResource : mapItemList) {
				
				if(!checkInfoContainsLocale(locale, mapItemResource.getInfoResource())){
					return "Text for locale '"+locale+"' not set on map item: "+mapItemResource.getName();
				}
			}
		}
		
		List<BuildingResource> buildingsList = Resource.getBuildingResourceList();
		if(buildingsList!=null && !buildingsList.isEmpty()){
			for (BuildingResource buildingResource : buildingsList) {
				
				if(!checkInfoContainsLocale(locale, buildingResource.getInfoResource())){
					return "Text for locale '"+locale+"' not set on building: "+buildingResource.getName();
				}
				
				List<BuildingActionResource> buildingActionList = buildingResource.getBuildingActionList();
				if(buildingActionList!=null && !buildingActionList.isEmpty()){
					for (BuildingActionResource buildingActionResource : buildingActionList) {
						
						if(!checkInfoContainsLocale(locale, buildingActionResource.getInfoResource())){
							return "Text for locale '"+locale+"' not set on building action: "+buildingActionResource.getTitle();
						}
					}
				}
			}
		}
		
		List<LevelResource> levelList = Resource.getLevelResourceList();
		if(levelList!=null && !levelList.isEmpty()){
			for (LevelResource levelResource : levelList) {
				
				if(!checkInfoContainsLocale(locale, levelResource.getInfoResource())){
					return "Text for locale '"+locale+"' not set on level: "+levelResource.getInternalTitle();
				}
			}
		}
		

		
		return null;
	}
	
	private boolean checkInfoContainsLocale(String locale, InfoResource infoResource){
		
		List<String> localeKeys = infoResource.getLocaleKeys();
		//assume optional text and text has not been set eg a worker working
		if(localeKeys.isEmpty()) return true;
		for (String key : localeKeys) {
			//assumes if one is set then all is set when needed
			if(key.endsWith(locale)) return true;
		}
		
		return false;
	}

	@Override
	protected void saveData() {

	}
	
	private void closeGui(){
		
		frame.dispose();
	}

	@Override
	protected void dirtyButtonUpdate() {

		
	}


	protected int getMajorFromVersion(){
		
		String version = versionTxt.getText();
		int periodPos = version.indexOf(".");
		if(periodPos==-1) return Integer.parseInt(version);
		return Integer.parseInt(version.substring(0, periodPos));
	}
	
	protected int getMinorFromVersion(){
		
		String version = versionTxt.getText();
		int periodPos = version.indexOf(".");
		if(periodPos==-1) return 0;
		return Integer.parseInt(version.substring(periodPos+1));
	}
	
	protected String getHelpText(){
		
		return new StringBuilder("Before you can start creating a Little Hoarders project, you must browse to an empty folder using the browse... button.\n")
						 .append("If you have already started a project, then you will need to browse to the parent folder that holds the resource.xml, Images and Data folders.\n")
						 .append("Your project will only be playable on a device after at least one level has been added, The info box will state the completeness of the project.\n\n")
						 .append("The Game Title will be dispalyed to the user after they have downloaded your project, along with the path they downloaded it from.\n\n")
						 .append("The version is important for the game to know if there are any updates for your project.\n")
						 .append("The version number should be increased before you reupload your project to your webspace or the game will not download any changes you have made.\n")
						 .append("The version number consists of a major number (before the period) and a minor version (after the period).\n")
						 .append("Increasing the minor version will cause the game to reload the resources.xml and any additional assets you have added to your project.\n")
						 .append("Increasing the major version will cause the game to remove all assets and redownload everything.  You should only need to increase the major version if you have made changes to assets that has previously be uploaded\n\n")
						 .append("The Default Locale is what language the text will be dispalyed in.  If the device is set to a locale that you have not supplied text for, then the game will display the texts you have set for your default locale.\n")
						 .append("You must supply all mandatory texts in the default locale.  Any additional texts you set in other locales, will be used if the device is set to that locale.\n\n")
						 .append("For further help, please watch my YouTube video:\n")
						 .append("https://youtu.be/ds5CTBsEWqM").toString();
	}
	


}
