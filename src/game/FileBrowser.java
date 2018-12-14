package game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import gui.EditorGeneral;

public class FileBrowser {
		
	private String endsWith;

	
	public List<File> browserMultiPngDiag(String path){

		return browseDiag(path, ".png", true);
	}
	
	public File browserSinglePngDiag(String path){
		
		List<File> files = browseDiag(path, ".png", false);
		
		if(files!=null && !files.isEmpty()) return files.get(0);
		
		return null;
	}
	
	public List<File> browseDiag(String path, String endsWith, boolean multiSelection){

		if(path==null) path="";
		this.endsWith = endsWith;
		JFileChooser fileChooser = new JFileChooser(new File(path));

		fileChooser.setFileFilter(new MyFilter());
		fileChooser.setMultiSelectionEnabled(multiSelection);
		fileChooser.showOpenDialog(fileChooser);

		File[] selectedFiles;
		if(multiSelection) selectedFiles = fileChooser.getSelectedFiles();
		else selectedFiles = new File[]{fileChooser.getSelectedFile()};
		
		
		List<File> selectedFilesList = new ArrayList<File>();
		
		for (int i = 0; i < selectedFiles.length; i++) {
			
			if(selectedFiles[i]!=null && selectedFiles[i].getAbsolutePath().endsWith(endsWith)){
				
				selectedFilesList.add(selectedFiles[i]);
			}
		}
		
		if(!selectedFilesList.isEmpty()){

			ConfigIO.updateProperty(ConfigIO.LAST_IMAGE_LOAD_KEY, selectedFilesList.get(0).getParent());
		}
		
		return selectedFilesList;
	}
	
	class MyFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File file) {
	    	
	    	if(file.getAbsolutePath().startsWith(EditorGeneral.getWorkFolderPath())){
	    		return false;
	    	}
	    	
	        String filename = file.getName();
	        return (endsWith(filename, endsWith, true) || file.isDirectory());
	    }
	    public String getDescription() {
	        return endsWith;
	    }
	    
		protected boolean endsWith(String str, String suffix, boolean ignoreCase) {
		    if (str == null || suffix == null) {
		        return (str == null && suffix == null);
		    }
		    if (suffix.length() > str.length()) {
		        return false;
		    }
		    int strOffset = str.length() - suffix.length();
		    return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
		}
	}

}
