package gui;

import java.util.Collection;
import java.util.regex.Pattern;

public class ValidationHelper {
	
	private String warning;
	
	private int intResult;
	private String stringResult;
	
	
	//TODO too many overloaded methods?
	public boolean validateInt(String identifier, String value){
		
		return validateInt(identifier, value, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, true);
	}
	
	public boolean validateInt(String identifier, String value, boolean forceValue){
		
		return validateInt(identifier, value, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, forceValue);
	}
	
	public boolean validateInt(String identifier, String value, int unsetDefault){
		
		return validateInt(identifier, value, Integer.MIN_VALUE, Integer.MAX_VALUE, unsetDefault, true);
	}
	
	public boolean validateInt(String identifier, String value, int unsetDefault, boolean forceValue){
		
		return validateInt(identifier, value, Integer.MIN_VALUE, Integer.MAX_VALUE, unsetDefault, forceValue);
	}
	
	public boolean validateInt(String identifier, String value, int minValue, int maxValue, int unsetDefault){
		
		return validateInt(identifier, value, minValue, maxValue, unsetDefault, true);
	}
	
	public boolean validateInt(String identifier, String value, int minValue, int maxValue, boolean forceValue){
		
		return validateInt(identifier, value, minValue, maxValue, 0, forceValue);
	}
	
	public boolean validateInt(String identifier, String value, int minValue, int maxValue, int unsetDefault, boolean forceValue){
		
		intResult = unsetDefault;
		warning = null;
		
		if(forceValue && (value==null || value.trim().length()==0)){
			warning = identifier + " cannot be empty";
			
		}else if(value!=null && value.trim().length() > 0){
			int intValue;
			try{
				intValue = Integer.valueOf(value);
				if(intValue < minValue || intValue > maxValue){
					warning = identifier + " must be between "+minValue+" & "+maxValue;
				}else{
					intResult = intValue;
				}
			}catch(Exception e){
				warning =  identifier + " must be a number";
			}
		}

		return warning==null;
		
	}
	
	public boolean validateFileName(String identifier, String value){
		
		warning = null;
		
		if(validateString(identifier, value, 50)){
			
			Pattern pattern = Pattern.compile("^[^\\/:*?<>|]+$");
		    if(!pattern.matcher(value).matches()) {
		        
		    	warning = "name contains invalid filename characters";
		    }
		    
		    return warning==null;
		}
		
		return false;
	}
	
	public boolean validateString(String identifier, String value){
		
		return validateString(identifier, value, Integer.MAX_VALUE);
	}

	public boolean validateString(String identifier, String value, int length){
		
		warning = null;
		stringResult = null;
		
		if(value==null || value.trim().length()==0){
			
			warning = identifier +" cannot be empty";	
		}else{
			value = value.trim();
			if(value.length() > length){
				
				warning = identifier +" too long";
			}else{
				
				stringResult = value;
			}
		}
		
		return warning == null;
	}
	
	public boolean validateCollectionIsEmpty(String identifier, Collection<? extends Object> value){
		
		warning = null;
		if(value==null || value.isEmpty()){
			
			warning = identifier + " must be present";
		}
		
		return warning == null;
	}
	
	public boolean validateObjectIsNotNull(String identifier, Object value){
		
		warning = null;
		if(value==null){
			
			warning = identifier + " must be present";
		}
		
		return warning == null;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public int getIntResult() {
		return intResult;
	}

	public void setIntResult(int intResult) {
		this.intResult = intResult;
	}

	public String getStringResult() {
		return stringResult;
	}

	public void setStringResult(String stringResult) {
		this.stringResult = stringResult;
	}
	
	

}
