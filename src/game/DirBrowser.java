package game;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class DirBrowser {
		
	
	public File dirSelectorDiag(String path, String title){

        JFileChooser fc = new JFileChooser();

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter( new FileFilter(){

            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Any folder";
            }

        });

        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setApproveButtonText("Select");

        

        fc.showSaveDialog(fc);
        
        
		File selectedDir = fc.getSelectedFile();
		
		//too deep?
		if(selectedDir!=null && !selectedDir.isDirectory()) selectedDir = selectedDir.getParentFile();
		
		return selectedDir;
	}


}
