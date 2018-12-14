package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigIO {
	
	private static Properties prop;
	private static String CONFIG_FILENAME = "config.properties";
	public static String LAST_SAVED_KEY = "last.saved";
	public static String LAST_IMAGE_LOAD_KEY = "last.image.dir";
	
	static{
		
		prop = new Properties();
		
		if(new File(CONFIG_FILENAME).exists()){
			
			InputStream input = null;

			try {
	
				input = new FileInputStream(CONFIG_FILENAME);
	
				prop.load(input);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
	
	public static String getProperty(String key){
		
		return prop.getProperty(key);
	}
	
	public static void updateProperty(String key, String value){
		
		prop.put(key, value);
		saveProperties(prop);
	}
	
	private static void saveProperties(Properties prop){
		
		OutputStream output = null;
		
		try {

			output = new FileOutputStream(CONFIG_FILENAME);

			prop.store(output, null);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
