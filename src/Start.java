
import game.ConfigIO;
import gui.EditorGeneral;
import gui.EntryMenuGui;

public class Start {

	public static void main(String[] args) {
		
		String lastSaved = ConfigIO.getProperty(ConfigIO.LAST_SAVED_KEY);
		
		EditorGeneral.setWorkFolder(lastSaved);
		
		new EntryMenuGui();
			
			//new set
//			Resource.init();

	}

}
