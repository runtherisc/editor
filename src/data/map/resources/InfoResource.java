package data.map.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class InfoResource {
	
    private HashMap<String, String> textMap = new HashMap<String, String>();

    public String getText(String type) {

        String locale = Locale.getDefault().getLanguage();

        String text = textMap.get(type + locale);
//        if(Constants.DEBUG) Log.d(Constants.GENERAL_TAG, "getting " + type + locale + " got "+text);

        if(text==null){
            text = textMap.get(type + Resource.getDefaultLocale());
//            if(Constants.DEBUG) Log.d(Constants.GENERAL_TAG, "getting " + type +  Resource.getDefaultLocale() + " got "+text);
        }

        if(text==null){

//            text = locale + " unknown "+type;
        }

        return text;
    }

    protected void addText(String type, String locale, String text) {

//        if(Constants.DEBUG) Log.w(Constants.GENERAL_TAG, type + locale + " adding "+text);
        textMap.put(type + locale, text);
    }
    
    public List<String> getLocaleKeys(){
    	
    	return new ArrayList<String>(textMap.keySet());
    }

	public HashMap<String, String> getTextMap() {

		return textMap;
	}

	public void setTextMap(HashMap<String, String> textMap) {

		this.textMap = new HashMap<String, String>(textMap);

	}
	
	public boolean hasTexts(){
		
		return !textMap.isEmpty();
	}
	
	public void populateHashMapHelpers(HashMap<String, String> titles, HashMap<String, String> descriptions){
		
		for (String string : textMap.keySet()) {
			
			int titleIndex = string.indexOf(ResourceConstants.INFO_TYPE_TITLE);
			if(titleIndex>-1){
				
				titles.put(string.substring(ResourceConstants.INFO_TYPE_TITLE.length()), textMap.get(string));
				
			}else{
				
				int descriptionIndex = string.indexOf(ResourceConstants.INFO_TYPE_DESCRIPTION);
				
				if(descriptionIndex>-1){
					
					descriptions.put(string.substring(ResourceConstants.INFO_TYPE_DESCRIPTION.length()), textMap.get(string));
					
				}else System.out.println("unknown type "+string);
			}
		}
	}
    
    

}
