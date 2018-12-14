package gui;

import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSlider;

public class CustomSlider extends JSlider{
	
	private int last = 0;
	
	private boolean reseting;

	private static final long serialVersionUID = -7611581056138082432L;
	
	private List<Integer> values;

    public CustomSlider(List<Integer> values){
    	
    	super(0, values.size()-1, 0);
    	this.values = values; 
    	init();
    
    }
    
    private void init(){
    	
    	Hashtable<Integer, JLabel> labels = new Hashtable<>();
    	
    	//do not show more than 10 labels (sometimes 11 as last one is always shown) and attempt to space then evenly
    	float step = values.size()<10 ? 1.0f : (float)values.size()/10; 
    	
        for(float i = 0; values.size() > i; i = i + step){
        	
        	int pos = (int)Math.floor(i);
        	
        	labels.put(pos, new JLabel(Integer.toString(values.get(pos))));

        }
        //always do the last (even when it already exists :| )
        labels.put(values.size()-1, new JLabel(Integer.toString(values.get(values.size()-1))));
        
        setLabelTable(labels);      
        setPaintTicks(true);
        setPaintLabels(true);
        setSnapToTicks(true);
        setMajorTickSpacing(1);
    }
    
    public List<Integer> getValues(){
    	
    	return values;
    }
    
    public int getCustomValue(){
    	
    	return values.get(super.getValue());
  
    }
    
    public void setLast(){
    	
    	last = super.getValue();
    	System.out.println("Slider: setting last "+last);
    }
    
    public void restoreLastPosition(){
    	
    	setReseting(true);
    	super.setValue(last);
    	System.out.println("Slider: restoring last "+last);
    }
    
    public void setMaximumPosition(){
    	
    	setReseting(true);
    	super.setValue(getMaximum());
    }
    
    public boolean isReseting() {
		return reseting;
	}

	public void setReseting(boolean reseting) {
		System.out.println("resetting "+reseting);
		this.reseting = reseting;
	}


	public void updateCustomSlider(List<Integer> values, boolean setLastId){
    	
    	this.values = values;
		setMaximum(values.size()-1);
		setLabelTable(null);
		init();
		if(setLastId){
			last = super.getValue();
			
			super.setValue(getMaximum());	
//			setReseting(true);
		}
    }
	
	public boolean updateSliderPositionById(Integer id){
		
		int index = values.lastIndexOf(id);
		
		if(index==-1) return false;
		
		super.setValue(index);
		
		return true;
	}
	
}