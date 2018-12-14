package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import game.ImageHelper;

public abstract class BaseGui implements ActionListener{
	
	private String title;
	protected JFrame frame;
	protected JFrame parentFrame;
	protected Map<JButton, ChildBaseGui> buttonToGuiMap= new HashMap<JButton, ChildBaseGui>(); 
	private Map<String, Object> propertiesToPass;
	private Map<String, List<String>> imageOrderMap = new HashMap<String, List<String>>();
	private Map<String, Integer> imageTotalsMap = new HashMap<String, Integer>();
	private boolean canWriteXml;
	private boolean pendingWriteXml;
	private boolean dirtyState;
	private boolean newItem;
	private boolean pendingImageSave;
	private boolean dirtyChildren;
	private Map<String, Integer> revertMap = new HashMap<String, Integer>();

	//main form
	public BaseGui(String title){
		this.title = title;
		initFrame();
	}
	
	public BaseGui(String title, JFrame parent) {
		
		this.title = title;
		this.parentFrame = parent;
	}
	
	private void initFrame(){
		
		frame = new JFrame(getFrameTitle());
        frame.setLayout(new GridBagLayout());	
        
        addComponents(frame);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        setupCloseListener(frame);
	}
	
	
	protected void setupCloseListener(final JFrame frame){

        frame.addWindowListener(new WindowListener() {
			
        	public void windowClosed(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				
				if(parentFrame!=null){
				
					if((anythingDirty() && confirmDirtyClose()) || (!anythingDirty() && additionalCloseConfirmation())){
						frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						closeDown();
					}else{
						frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					}
				
		        }else{
		        	if(EditorGeneral.getWorkFolderPath()!=null) ImageHelper.deleteTempFolder();
		        	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        }
				
			}

		});

	}
	


	
	protected boolean anythingDirty(){
		
		return isDirtyState() || hasDirtyChildren();
	}
	
	protected boolean confirmDirtyClose(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Close without saving?", "Changes will be lost", JOptionPane.YES_NO_OPTION);
		
		boolean confirmDirtyClose = dialogResult == JOptionPane.YES_OPTION;
		
		if(confirmDirtyClose){
			if(canWriteXml){
				reloadXml();
			}else{
				setPendingWriteXml(false);
				setDirtyChildren(false);
				clearAllChildrenPendingWriteXml(buttonToGuiMap.values());
				revertUnsavedChanges(-1);
			}
			
		}
		
		return confirmDirtyClose;
	}
	
	//override must return false to stop user closing
	protected boolean additionalCloseConfirmation(){
		
		return true;
	}
	
	protected void setFrame(JFrame frame){
		
		this.frame = frame;
	}
	
	protected String getTitle(){
		
		return title;
	}
	
	protected void setTitle(String title){
		
		this.title = title;
	}
	
	protected String getFrameTitle(){
		
		return title;
	}
	
	protected boolean isDirtyState() {
		return dirtyState;
	}
	
	public boolean isNewItem() {
		return newItem;
	}

	public void setNewItem(boolean newItem) {
		
		this.newItem = newItem;
	}
	
	public void setDirtyStateAndConfigure(boolean dirtyState) {

		setDirtyStateAndConfigure(dirtyState, true);
	}

	public void setDirtyStateAndConfigure(boolean dirtyState, boolean enableRevert) {
		
//		if(dirtyState) throw new RuntimeException("here");
		
		//main menus will never be set to dirty
		if(parentFrame != null) this.dirtyState = dirtyState;
		updateDisplayTitleForDirty();
	}
	
	public boolean hasDirtyChildren(){
		
		return dirtyChildren;
	}
	
	public void setDirtyChildren(boolean dirtyChildren){
		
		System.out.println("dirtyChildren "+dirtyChildren);
		this.dirtyChildren = dirtyChildren;
	}
	
	public boolean isPendingImageSave() {
		return pendingImageSave;
	}
	
	public void setPendingImageSave(boolean pendingImageSave) {

		this.pendingImageSave = pendingImageSave;
	}

	public void setPendingImageSaveAndConfigure(boolean pendingImageSave) {

		setPendingImageSave(pendingImageSave);
		setDirtyStateAndConfigure(pendingImageSave);
	}
	
	protected void updateDisplayTitleForDirty(){
		
		String state = "";
		
		System.out.println("isDirtyState() " + isDirtyState());
		System.out.println("isPendingImageSave() " + isPendingImageSave());
		System.out.println("hasDirtyChildren() " + hasDirtyChildren());
		
		if(isDirtyState() || hasDirtyChildren()) state = "*";
		
		frame.setTitle(getFrameTitle()+state);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() instanceof JButton){
			
			JButton button = (JButton) e.getSource();
			
			ChildBaseGui baseGui = buttonToGuiMap.get(button);
			
			if(baseGui!=null){
				
				String preOpenWarning = addBundlesOrReturnWarning(baseGui.getClass().getName());
				
				if(preOpenWarning==null){
				
					openChildGui(baseGui);
					
				}else{
					
					JOptionPane.showMessageDialog(null, preOpenWarning, "Additional Requirements Needed", JOptionPane.WARNING_MESSAGE);
				}
				
			}else{
				otherActions(button, frame);
			}
		}

	}
	
	protected String addBundlesOrReturnWarning(String childName){
		
		//override and add bundles, return issue in override
		return null;
	}
	
	protected void openChildGui(ChildBaseGui childGui){
		
		System.out.println("open gui called");

		childGui.passedBundle(propertiesToPass);

//			saveData();
		frame.setEnabled(false);

		if(propertiesToPass!=null && propertiesToPass.containsKey(PropertyKeys.SUPPRESS_GLOBAL_COMPONENTS))
			childGui.setSuppressGlobalComponents((boolean)propertiesToPass.get(PropertyKeys.SUPPRESS_GLOBAL_COMPONENTS));		
		
		if(!childGui.isCanWriteXml()) childGui.setNewItem(isNewItem());
		childGui.drawGui();
		childGui.setParentGui(this);

	}
	
	protected void addGuiButtonAndListener(ChildBaseGui baseGui, JButton button){
		
        button.addActionListener(this);
        
        buttonToGuiMap.put(button, baseGui);
	}
	
	protected JButton createGuiButtonAndListener(ChildBaseGui baseGui, String description, int items){
        
        String label;
        if(items==0) label = new StringBuilder("Add New ").append(description).toString();
        else label = new StringBuilder("Edit ").append(description).append(" (").append(items).append(")").toString();
		
        final JButton button = new JButton(label);
        
        button.addActionListener(this);
        
        buttonToGuiMap.put(button, baseGui);
        
        return button;
		
	}
	
	protected void updateButtonLabelWithItems(JButton button, String description, int items){
		
        String label;
        if(items==0) label = new StringBuilder("Add New ").append(description).toString();
        else label = new StringBuilder("Edit ").append(description).append(" (").append(items).append(")").toString();
        button.setText(label);
	}
	
	protected void updateButtonLabelWithState(JButton button, String description, boolean isDirty){
		
		if(isDirty){
			button.setText(description + " *");
		}else{
			button.setText(description);
		}
	}
	
	protected JTextField addLabelAndTextFieldToPanel(JPanel panel, String name, int gridx, int gridy, int textSpan, boolean isEnabled){
        
        JLabel nameLab = new JLabel(name);
        nameLab.setEnabled(isEnabled);
        panel.add(nameLab, getRightPaddedGridBagConstraints(gridx, gridy));

      	JTextField nameTxt = new JTextField(textSpan);
      	
      	nameTxt.setEnabled(isEnabled);
        panel.add(nameTxt, getRightPaddedGridBagConstraints(gridx + 1, gridy));
        
        return nameTxt;
	}
	
	protected JButton addHorizontalButtonAndLabelToPanel(JPanel panel, int gridx, int gridy, String buttonName, String labelText){
        
        JButton button = new JButton(buttonName);
        panel.add(button, getRightPaddedGridBagConstraints(gridx, gridy));
        
        JLabel label = new JLabel(labelText);
        panel.add(label, getRightPaddedGridBagConstraints(gridx + 1, gridy));
        
        return button;
	}
	
	protected JButton addImageSelection(JPanel panel, int gridx, int gridy, String title){
        
        JLabel label = new JLabel(title);
        panel.add(label, getImageLeftPaddedGridBagConstraints(gridx, gridy));
        
        JLabel imageLabel = new JLabel();
        Image image = new BufferedImage(ImageHelper.DEFAULT_SIZE,ImageHelper.DEFAULT_SIZE,BufferedImage.TYPE_INT_RGB);
        imageLabel.setIcon(new ImageIcon(image));
        panel.add(imageLabel, getImageLeftPaddedGridBagConstraints(gridx, gridy + 1));
        
        JButton button = new JButton("Add");
        button.setPreferredSize(new Dimension(78, 25));
        button.addActionListener(this);
        panel.add(button, getSideAndTopPaddingGridBagConstraints(gridx, gridy + 2));
        
        return button;
        
	}
	
	protected JSpinner addLabelAndNumberSpinnerToPanel(JPanel panel, String name, int gridx, int gridy, int maxValue, int minValue){
        
        JLabel nameLab = new JLabel(name);
        panel.add(nameLab, getRightPaddedGridBagConstraints(gridx, gridy));
		
		SpinnerModel model1 = new SpinnerNumberModel(maxValue, minValue, maxValue, 1);
		
	    final JSpinner spinner1 = new JSpinner(model1);
        
        panel.add(spinner1, getRightPaddedGridBagConstraints(gridx + 1, gridy));
        
        return spinner1;
	}
	
	protected JComboBox<String> addLabelAndComboToPanel(JPanel panel, String labelName, int gridx, int gridy, String[] comboItems){
		
        JLabel nameLab = new JLabel(labelName);
        panel.add(nameLab, getRightPaddedGridBagConstraints(gridx, gridy));
		
        JComboBox<String> combo = new JComboBox<String>(comboItems);
        
        panel.add(combo, getRightPaddedGridBagConstraints(gridx + 1, gridy));
        
        return combo;
	}
	
	protected JCheckBox addLabelAndCheckBoxToPanel(JPanel panel, String labelName, int gridx, int gridy){
		
        JLabel nameLab = new JLabel(labelName);
        panel.add(nameLab, getRightPaddedGridBagConstraints(gridx, gridy));
		
        JCheckBox check = new JCheckBox();
        
        panel.add(check, getRightPaddedGridBagConstraints(gridx + 1, gridy));
        
        return check;
	}
	
	protected void addToPassedProperties(String key, Object value){
		
		if(propertiesToPass==null) propertiesToPass = new HashMap<String, Object>();
		
		propertiesToPass.put(key, value);
	}
	
	protected void removeAPassedProperty(String key){
		
		if(propertiesToPass!=null) propertiesToPass.remove(key);
	}
	
	protected void clearPassedProperties(){
		
		propertiesToPass = null;
	}
	
	//inserts: top, left, bottom, right
	
	public GridBagConstraints getSidePaddedGridBagConstraints(int column, int row){
		
		return getGridBagConstraints(GridBagConstraints.HORIZONTAL, -1, column, row, new Insets(0, 5, 0, 5), 1, 0);
	}
	
	public GridBagConstraints getRightPaddedGridBagConstraints(int column, int row){
		
		return getGridBagConstraints(GridBagConstraints.HORIZONTAL, -1, column, row, new Insets(0, 0, 0, 5), 1, 0);
	}
	
	public GridBagConstraints getImageLeftPaddedGridBagConstraints(int column, int row){
		
		return getGridBagConstraints(GridBagConstraints.HORIZONTAL, -1, column, row, new Insets(0, 12, 0, 0), 1, 0);
	}
	
	public GridBagConstraints getNoPaddingGridBagConstraints(int column, int row){
		
		return getGridBagConstraints(GridBagConstraints.HORIZONTAL, -1, column, row, new Insets(0, 0, 0, 0), 0, 0);
	}
	
	public GridBagConstraints getAllPaddingGridBagConstraints(int column, int row){
		
		return getGridBagConstraints(GridBagConstraints.HORIZONTAL, -1, column, row, new Insets(5, 5, 5, 5), 1, 1);
	}
	
	public GridBagConstraints getSlightPaddingGridBagConstraints(int column, int row){
		
		return getGridBagConstraints(GridBagConstraints.HORIZONTAL, -1, column, row, new Insets(2, 2, 2, 2), 1, 1);
	}
	
	public GridBagConstraints getSideAndTopPaddingGridBagConstraints(int column, int row){
		
		return getGridBagConstraints(GridBagConstraints.HORIZONTAL, -1, column, row, new Insets(5, 5, 0, 5), 0, 0);
	}
	
	
	public GridBagConstraints getGridBagConstraints(int fill, int width, int column, int row, Insets inserts, int wieghtx, int wieghty){
		
        GridBagConstraints gbc = new GridBagConstraints();		

        gbc.fill = fill;
        if(width>-1) gbc.gridwidth = width;
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.insets = inserts;
        gbc.weightx = wieghtx;
        gbc.weighty = wieghty;
        
        return gbc;
	}
	
	public boolean isPendingWriteXml() {
		return pendingWriteXml;
	}

	public void setPendingWriteXml(boolean pendingWriteXml) {
		this.pendingWriteXml = pendingWriteXml;
	}
	
	public boolean isCanWriteXml() {
		return canWriteXml;
	}

	public void setCanWriteXml(boolean canWriteXml) {
		this.canWriteXml = canWriteXml;
	}

	public int getRevertAmount(String innerFolder) {
		if(innerFolder == null || innerFolder == "") innerFolder = "*";
		return revertMap.containsKey(innerFolder) ? revertMap.get(innerFolder) : 0;
	}

	public void setRevertAmount(String innerFolder, int revertAmount) {
		if(innerFolder == null || innerFolder == "") innerFolder = "*";
		revertMap.put(innerFolder, revertAmount);
	}
	
//	public Map<String, Integer> getRevertMapCopy(){
//		
//		Map<String, Integer> revertCopy = new HashMap<String, Integer>();
//		
//		for (String key : revertMap.keySet()) {
//			
//			revertCopy.put(key, revertMap.get(key));
//		}
//		
//		return revertCopy;
//	}
	
	public boolean isRevertMapSet(){
		
		return !revertMap.isEmpty();
	}
	
	protected Map<String, Integer> getRevertMap(){
		
		return revertMap;
	}
	
	public void clearRevertMap(){
		
		revertMap = new HashMap<String, Integer>();
	}

	public Map<JButton, ChildBaseGui> getButtonToGuiMap() {
		return buttonToGuiMap;
	}

	public void clearAllChildrenPendingWriteXml(Collection<ChildBaseGui> collection){
		
		for (ChildBaseGui childBaseGui : collection) {
			
			System.out.println("clear pending working on "+childBaseGui.getTitle());
			childBaseGui.setPendingWriteXml(false);
			childBaseGui.clearRevertMap();
			Map<JButton, ChildBaseGui> childsMap = childBaseGui.getButtonToGuiMap();
			if(childsMap!=null && !childsMap.isEmpty()){
				clearAllChildrenPendingWriteXml(childsMap.values());//recursive
			}
		}
		
	}
	
	protected boolean isChildGuiXmlWritePending(JButton button){
		
		if(buttonToGuiMap.containsKey(button)){
			
			return buttonToGuiMap.get(button).isPendingWriteXml();
		}
		
		return false;
	}
	
	protected boolean isChildGuiImageSavePending(JButton button){
		
		if(buttonToGuiMap.containsKey(button)){
			
			return buttonToGuiMap.get(button).isRevertMapSet();
		}
		
		return false;
	}
	
	protected boolean isChildFormComplete(JButton button){
		
//		System.out.println(button.getText());
//		System.out.println("----map contains------");
//		for (JButton but : buttonToGuiMap.keySet()) {
//			
//			System.out.println(but.getText());
//		}
		
		if(buttonToGuiMap.containsKey(button)){

			return buttonToGuiMap.get(button).isFormComplete();
		}

		return false;
	}
	
	protected void setChildFormComplete(JButton button, boolean formComplete){
		
		buttonToGuiMap.get(button).setFormComplete(formComplete);
	}
	
	protected Map<String, Integer> getChildsRevertMap(JButton button){
		
		if(buttonToGuiMap.containsKey(button)){
			
			return buttonToGuiMap.get(button).getRevertMap();
		}
		
		return null;
	}
	
	protected void addImageOrder(String key, List<String> imageOrder){
		
//		System.out.println("adding image order for "+key);
		
		if(key == null || key == "") key = "*";
		
		imageOrderMap.put(key, imageOrder);
	}
	
	protected List<String> getImageOrder(String key){
		
//		System.out.println("getting image order for "+key);
		
		if(key == null || key == "") key = "*";
		
		return imageOrderMap.get(key);
	}
	
	protected Set<String> getImageOrderKeySet(){
		
		return imageOrderMap.keySet();
	}
	
	protected void addImageTotal(String key, int total){
		
//		System.out.println("adding image order total "+total+" for "+key);
		
		if(key == null || key == "") key = "*";
		
		imageTotalsMap.put(key, total);
	}
	
	protected int getImageTotal(String key){
		
		
		
		if(key == null || key == "") key = "*";
		
//		System.out.println("getting image order total "+imageTotalsMap.get(key)==null ? 0 : imageTotalsMap.get(key)+" for "+key);
		
		if(imageTotalsMap.get(key)==null) return 0;
		
		return imageTotalsMap.get(key);
	}
	
	protected List<String> duplicateImageOrder(List<String> originalMap){
	
		List<String> copy = new ArrayList<String>();
		
		for (String string : originalMap) {
			
			copy.add(string);
		}
		
		return copy;
	}
	
	protected void clearImageOrder(){
		
		imageOrderMap = new HashMap<String, List<String>>();
		imageTotalsMap = new HashMap<String, Integer>();
	}
	
//	protected Map<String, List<String>> getImageOrderMap(){
//		
//		return imageOrderMap;
//	}
	
	
	//override me hooks
	protected void closeDown(){}
	protected void reloadXml(){}
	protected void childClosing(){}
	protected void otherActions(JButton button, JFrame frame){}
	protected void revertUnsavedChanges(int pos){}
	
	//return the Y level for the save button
	protected abstract int addComponents(JFrame frame);
	
	protected abstract void dirtyButtonUpdate();
	
	protected abstract void saveData();
	
}
