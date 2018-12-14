package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import data.map.resources.Resource;
import data.map.resources.ResourceParser;
import data.map.resources.ResourceWriter;
import game.ImageHelper;

public abstract class ChildBaseGui extends BaseGui implements FocusListener{
	
//	private int parentState;
	private JButton helpButton, newButton, saveButton, closeButton, revertButton, deleteButton;
	private JTextField infoTextField;
	
	private int maxTableRows = 15; //scroll bar will kick in after this many rows
	
	private JSlider slider = null;

	private boolean formComplete;
	private boolean suppressGlobalComponents;
	private BaseGui parentGui;
	
	//ignore listeners etc if we are still populating the form
	private boolean formReady;
	
	private int maxImagesOnPanel = 8;
	private JSlider imageSlider;
	private boolean disableImageSlider;
	
	private Map<JTextField, Boolean> textFieldDirtyStates;
	
	private long lastWarningTimeStamp;

	public ChildBaseGui(String title, JFrame parent) {
		super(title, parent);

	}
	

//	public void setParentState(int state){
//		
//		parentState = state;
//	}

	@Override
	public void setDirtyStateAndConfigure(boolean dirtyState, boolean enableRevert) {
		
		System.out.println("setDirtyStateAndConfigure dirtyChildren "+hasDirtyChildren());

		if(enableRevert) super.setDirtyStateAndConfigure(dirtyState, enableRevert);
		else updateDisplayTitleForDirty();


		if(dirtyState){
			saveButton.setEnabled(true);
			if(!hasDirtyChildren() || enableRevert) revertButton.setEnabled(true);
		}else{
			if(!hasDirtyChildren()) saveButton.setEnabled(false);
			revertButton.setEnabled(false);
			clearWarning();
		}
		
	}
	
	
	
	protected boolean isSuppressGlobalComponents() {
		return suppressGlobalComponents;
	}

	//addToPassedProperties(PropertyKeys.SUPPRESS_GLOBAL_COMPONENTS, true); before opening child to set this
	protected void setSuppressGlobalComponents(boolean suppressGlobalComponents) {

		this.suppressGlobalComponents = suppressGlobalComponents;
	}

	@Override
	public void setNewItem(boolean newItem) {
		super.setNewItem(newItem);
		if(slider!=null)((CustomSlider)slider).setReseting(true);
	}

	public void drawGui(){
		
		textFieldDirtyStates = new HashMap<JTextField, Boolean>();
		
		frame = new JFrame(getFrameTitle());
        frame.setLayout(new GridBagLayout());	
        
        int gridy = addComponents(frame);
        
        if(slider!=null){
        	
        	addSlider(gridy, slider, frame);
            
            gridy++;
        }
        
        if(!isSuppressGlobalComponents()){
        
	        //info box
	        infoTextField = new JTextField();
	        infoTextField.setForeground(Color.RED);
	        infoTextField.enableInputMethods(false);
	        infoTextField.setEditable(false);
	        infoTextField.setHorizontalAlignment(JLabel.CENTER);
	        
	        frame.add(infoTextField, getSidePaddedGridBagConstraints(0, gridy));
	        
	        gridy++;
	        
	        JPanel panel = new JPanel();
	        
	        helpButton = new JButton("HELP");
	        
	        helpButton.addActionListener(this);
	        helpButton.addFocusListener(this);
	        helpButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
	        addMouseEnterClearWarning(helpButton);
	        panel.add(helpButton);
	        
	        newButton = new JButton("NEW");
	        
	        newButton.addActionListener(this);
	        newButton.addFocusListener(this);
	        newButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
	        if(isNewItem() && isCanWriteXml()) newButton.setEnabled(false);
	        addMouseEnterClearWarning(newButton);
	        panel.add(newButton);
	        
	        deleteButton = new JButton("DELETE");
	        
	        deleteButton.addActionListener(this);
	        deleteButton.addFocusListener(this);
	        deleteButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
	        if(isNewItem() && isCanWriteXml()) deleteButton.setEnabled(false);
	        addMouseEnterClearWarning(deleteButton);
	        panel.add(deleteButton);
	        
	        String saveButtonText;
	        if(isCanWriteXml()){
	        	saveButtonText = "SAVE TO XML";
	        }else{
	        	saveButtonText = "SAVE";
	        }
	        
	        saveButton = new JButton(saveButtonText);
	        
	        saveButton.addActionListener(this);
	        saveButton.addFocusListener(this);
	        saveButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
	        if(!isDirtyState()) saveButton.setEnabled(false);
	        addMouseEnterClearWarning(saveButton);
	        panel.add(saveButton);
	        
	        revertButton = new JButton("REVERT");
	        
	        revertButton.addActionListener(this);
	        revertButton.addFocusListener(this);
	        revertButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
	        if(!isDirtyState()) revertButton.setEnabled(false);
	        addMouseEnterClearWarning(revertButton);
	        panel.add(revertButton);
	        
	        closeButton = new JButton("CLOSE");
	        
	        closeButton.addActionListener(this);
	        closeButton.addFocusListener(this);
	        closeButton.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

	        addMouseEnterClearWarning(closeButton);
	        panel.add(closeButton);
	        
	        frame.add(panel, getNoPaddingGridBagConstraints(0, gridy));
        }
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        setupCloseListener(frame);
        
        //after pack, update components
        if(slider!=null && !isNewItem()){

        	formReady = false;
			sliderAllComponents(((CustomSlider) slider).getCustomValue());
			formReady = true;
        }
        
        postDrawGui();
		
	}
	
	private void addMouseEnterClearWarning(JButton button){
		
		button.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}		
			@Override
			public void mousePressed(MouseEvent e) {}	
			@Override
			public void mouseExited(MouseEvent e) {}			
			@Override
			public void mouseEntered(MouseEvent e) {clearWarning();}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
	}
	
	protected void postDrawGui(){}
	
	protected void addSlider(int gridy, final JSlider slider, JFrame frame){
		
//		if(slider.getMouseListeners()==null || slider.getMouseListeners().length==0){
		
			slider.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {}
				
				@Override
				public void mouseExited(MouseEvent e) {
	
					slider.setEnabled(true);
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
		
					String warnings = validatePreSaveDataAndReturnIssues();
					
					if(warnings!=null){
						
						slider.setEnabled(false);
						displayWarning(warnings);
					
					}else clearWarning();
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					
//					System.out.println("MOUSE CLICKED!!");
//					System.out.println("value "+slider.getValue());
				}
			});
//		}
		
//		if(slider.getFocusListeners()==null || slider.getFocusListeners().length==0){
		
			slider.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					
					slider.setEnabled(true);
				}
				
				@Override
				public void focusGained(FocusEvent e) {
	
					String warnings = validatePreSaveDataAndReturnIssues();
					
					if(warnings!=null){
						
						slider.setEnabled(false);
						displayWarning(warnings);
					
					}else clearWarning();
				}
			});
//		}
		
		if(slider.getChangeListeners()==null || slider.getChangeListeners().length==0){
		
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					
				    CustomSlider slider = (CustomSlider) evt.getSource();
				    
				    int value = slider.getCustomValue();
				    
				    if(!slider.getValueIsAdjusting()){
				    	
				    	if(slider.isReseting()){
				    		
				    		slider.setReseting(false);
				    	}else{
				    		
				    		if(isNewItem()){
				    			
				    			if(slider.getValue()!=slider.getMaximum()){
						    		saveChangesPromptOnSlider(slider);
				    			}
				    			
				    		}else{//edited item
				    
							    if(isDirtyState() || hasDirtyChildren()){
							    	
							    	saveChangesPromptOnSlider(slider);//no longer dirty if saved..
				
							    }
							    
								if(!isDirtyState() && !hasDirtyChildren()){
									
									slider.setLast();
									formReady=false;
								    sliderAllComponents(value);
								    formReady=true;
							    }
				    		}
				    	}
				    }else{
				    	//being dragged
					    if(!isDirtyState() && !hasDirtyChildren()){
					    	
						    sliderUpdateLightComponents(value);
					    }
				    }
				}
			});
			
//			((CustomSlider)slider).setReseting(false);
		}else{
			slider.setValue(slider.getMinimum());
		}
  
		frame.add(slider, getSidePaddedGridBagConstraints(0, gridy));
		
//		if(!((CustomSlider)slider).isReseting()) sliderAllComponents(((CustomSlider) slider).getCustomValue());
	}
	
	private void saveChangesPromptOnSlider(CustomSlider slider){
		
		//TODO This causes a pointer bug on a Mac, need to find a fix
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Save Changes before editing another?", "Changes not saved", JOptionPane.YES_NO_CANCEL_OPTION);
		
		if(dialogResult==JOptionPane.YES_OPTION){

			boolean callAllComponets = isNewItem();
			saveDataProcess();		
			if(callAllComponets){
	        	formReady = false;
				sliderAllComponents(((CustomSlider) slider).getCustomValue());
				formReady = true;
			}

		
		}else if(dialogResult==JOptionPane.NO_OPTION){
			
			if(isNewItem()){
				slider.setLast();
				deleteDataProcess();
				slider.restoreLastPosition();
				setNewItem(false);
			}else{
				setPendingImageSaveAndConfigure(false);
				revertUnsavedChanges(slider.getCustomValue());
				if(isCanWriteXml() && hasDirtyChildren()){
					reloadXml();
					dirtyButtonUpdate();
					setPendingImageSaveAndConfigure(false);
				}
			}
			
		}else{//cancel
			
			if(isNewItem()){
				slider.setMaximumPosition();
			}else{
				slider.restoreLastPosition();
			}
		}
	}
	
	@Override
	protected void closeDown(){
		
		boolean reloadXml = (isCanWriteXml() && hasDirtyChildren());

		if(isCanWriteXml()) setPendingImageSaveAndConfigure(false);
		else setDirtyStateAndConfigure(false);	
		
		preClosingCleanup();
		
		if(parentFrame!=null){
			parentFrame.setEnabled(true);					
//			parentFrame.setDefaultCloseOperation(parentState);
			
			if(parentGui!=null){
				if(isPendingWriteXml()) parentGui.setPendingWriteXml(true);
				parentGui.dirtyButtonUpdate();
				parentGui.childClosing();
				if(isPendingWriteXml()){
					parentGui.setDirtyStateAndConfigure(true, false);
				}
//				if(isPendingWriteXml() && parentGui.isCanWriteXml()){
//					parentGui.setDirtyStateAndConfigure(true, false);
//				}
				
			}else{
				System.out.println("child form has no parent set");
			}
		}else{
			System.out.println("DELETING TEMP and then exit");
			ImageHelper.deleteTempFolder();
			System.exit(0);
		}
		
		if(isNewItem() && isCanWriteXml()){
			deleteDataProcess();
		}
		frame.dispose();
		//clear all unsaved changes including children
		if(reloadXml) reloadXml();
	}
	
	//override for cleanup on a form
	protected void preClosingCleanup(){}
	
	@Override
	protected void dirtyButtonUpdate() {
		
		revertButton.setEnabled(false);
		if(slider!=null) ((CustomSlider)slider).setReseting(false);
		
	}
	
	@Override
	protected void childClosing(){
		
		revertButton.setEnabled(false);
		if(slider!=null) ((CustomSlider)slider).setReseting(false);
	}

	
	protected JPanel getMultiImageSelection(String[] titles, List<JButton> imageButtons, boolean addSlider){
		
		int loopend = (titles==null ? maxImagesOnPanel : titles.length);
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		JPanel panel2 = new JPanel(new GridBagLayout());
		
		for (int i = 0; i < loopend; i++) {
			
			imageButtons.add(addImageSelection(panel2, i, 0, (titles==null ? "image " + i : titles[i])));
		}
		
		panel.add(panel2, getSidePaddedGridBagConstraints(0, 0)); 
		
		if(addSlider){
		
			JSlider imageSlider = new JSlider();
			imageSlider.setMinorTickSpacing(4);
			imageSlider.setMajorTickSpacing(4);//needed for spacing
			imageSlider.setPaintLabels(true);
			imageSlider.setPaintTicks(true);
			imageSlider.setSnapToTicks(true);
			
			panel.add(imageSlider, getSidePaddedGridBagConstraints(0, 1)); 
		}
		
		return panel;
	}
	

	
	protected JTextField addLabelAndTextFieldToPanelWithListener(JPanel panel, String name, int gridx, int gridy, int textSpan, boolean isEnabled, final boolean isImageRelated){
		
        JLabel nameLab = new JLabel(name);
        nameLab.setEnabled(isEnabled);
        panel.add(nameLab, getRightPaddedGridBagConstraints(gridx, gridy));
      	
      	JTextField nameTxt = new JTextFieldWithDocListener(textSpan){
	
			private static final long serialVersionUID = 1L;

			@Override
			public void didFieldChange(boolean changed) {
				
				textFieldDirtyStates.put(this, changed);
				
				if((slider==null || !slider.getValueIsAdjusting()) && formReady && changed!=isDirtyState()){

					setDirtyStateAndConfigure(anyDirtyTextFieldsOrImages());						
				}				
			}
			
			@Override
			public void doImageRefresh(){
				if(isImageRelated && !slider.getValueIsAdjusting() && formReady) refreshImages();
			}
 
      	};
      	
      	nameTxt.setEnabled(isEnabled);
        panel.add(nameTxt, getRightPaddedGridBagConstraints(gridx + 1, gridy));
        
        return nameTxt;
	}
	
	private boolean anyDirtyTextFieldsOrImages(){

//		for (JTextField field : textFieldDirtyStates.keySet()) {System.out.println(field.getText() +" : "+ textFieldDirtyStates.get(field));}
		
		if(isPendingImageSave()) return true;
			
		for (boolean dirty : textFieldDirtyStates.values()) {
			
			if(dirty) return true;
		}
		
		return false;
	}
	
	protected void setupSlider(List<Integer> values){
		if(values.isEmpty()){
			values.add(1);
			CustomSlider slider = new CustomSlider(values);
			slider.setReseting(true);
			setSlider(slider);
		}else{
			setSlider(new CustomSlider(values));
		}
	}
	
	protected void setSlider(JSlider slider){
		
		this.slider = slider;
	}
	
	protected void updateSliderPositionById(Integer id){
		
		((CustomSlider)slider).updateSliderPositionById(id);
	}
	
//	protected void updateSliderToPositionZero(){
//		
//		((CustomSlider)slider).updateSliderToPositionZero();
//	}
	
	protected void updateSlider(List<Integer> values, boolean setLastId){
		
		((CustomSlider)slider).updateCustomSlider(values, setLastId);
	}
	
	//light weight eg text views (excluding images)
	protected void sliderUpdateLightComponents(int pos){
//		childClosedRefresh();
	}
	
	//update all other components after calling update light
	protected void sliderAllComponents(int pos){
		sliderUpdateLightComponents(pos);
		((CustomSlider)slider).setReseting(false);
		clearWarning();
		textFieldDirtyStates = new HashMap<JTextField, Boolean>();
	}
	
	protected void setMaxTableRows(int rows){
		
		maxTableRows = rows;
	}
	
	protected JTable createTable(Object[] columnNames, int[] sizes, int column, int row){
		
		return createTable(columnNames, sizes, column, row, null);
	}
	
	protected JTable createTable(Object[] columnNames, int[] sizes, int column, int row, String labelName){
		
		JTable table = new JTable(new DefaultTableModel(columnNames, 0){
			
			private static final long serialVersionUID = -189302917815606522L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		});
		
		configueTable(table, sizes, column, row, labelName);
		
		return table;
	}
	
	protected void configueTable(JTable table, int[] sizes, int column, int row, String labalName){
		
		if(sizes!=null){
			for (int i = 0; i < sizes.length; i++) {
				
				table.getColumnModel().getColumn(i).setPreferredWidth(sizes[i]);
			}
		}
		
		if(table.getRowCount() < maxTableRows)
			table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		JScrollPane scrollPane = new JScrollPane(table);		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		int tableRow = 0;
		
		if(labalName!=null){
			
			panel.add(new JLabel(labalName), getNoPaddingGridBagConstraints(0, 0));
			tableRow = 1;
		}

		panel.add(scrollPane, getNoPaddingGridBagConstraints(0, tableRow));
		
        GridBagConstraints gbc = new GridBagConstraints();		

        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.gridwidth = 3;
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 50, 5, 50);
        gbc.weightx = 1;
		
		frame.add(panel, gbc);
	}
	
	protected JButton addTableButton(JPanel panel, String text, boolean enable){
		
		JButton button = new JButton(text);
		button.addActionListener(this);
		button.addFocusListener(this);
		button.setEnabled(enable);
      	panel.add(button);

      	return button;
	}
	
	protected ITableUpdateHook addHookToTable(final JTable table){
		
		ITableUpdateHook hook = new ITableUpdateHook() {

			@Override
			public void addDataRowToTable(Object[] objects, int tableRow) {
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
//				System.out.println("tableRow" + tableRow);
				
				if(tableRow == -1){
					model.addRow(objects);
				}else{
					model.removeRow(tableRow);
					model.insertRow(tableRow, objects);
				}
				
				if(table.getRowCount() <= maxTableRows)
					table.setPreferredScrollableViewportSize(table.getPreferredSize());
				
				frame.pack();//resize window
				frame.setLocationRelativeTo(null);
				
			}

			@Override
			public void removeRow(int selectedRow) {
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				if(selectedRow > -1){
				
					model.removeRow(selectedRow);
					
					if(table.getRowCount() <= maxTableRows)
						table.setPreferredScrollableViewportSize(table.getPreferredSize());
					
					frame.pack();//resize window
					frame.setLocationRelativeTo(null);
				}
				
			}
			
			@Override
			public void insertRow(Object[] objects, int tableRow) {
				
				if(tableRow > -1){
				
					DefaultTableModel model = (DefaultTableModel) table.getModel();

					model.insertRow(tableRow, objects);
	
					if(table.getRowCount() <= maxTableRows)
						table.setPreferredScrollableViewportSize(table.getPreferredSize());
					
					frame.pack();//resize window
					frame.setLocationRelativeTo(null);
				}
				
			}

			@Override
			public int getMatchingRowId(Object entry, int column) {
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				for (int i = 0; i < model.getRowCount(); i++) {
					if(entry.equals(model.getValueAt(i, column))) return i;
				}   
				
				return -1;
			}

			@Override
			public Object[] getSelectedRow(int selectedRow) {
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				if(selectedRow<0 || selectedRow > model.getRowCount()) return null;
				
				int columns = model.getColumnCount();
				
				Object[] objects = new Object[columns];
				
				for (int i = 0; i < columns; i++) {
					
					objects[i] = model.getValueAt(selectedRow, i);
				}
				
				return objects;
			}

			@Override
			public boolean doesObjectMatch(Object entry, int column, int row) {
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				return entry.equals(model.getValueAt(row, column));
			}
			
			@Override
			public void clearTable() {
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				model.setRowCount(0);
				
				if(table.getRowCount() <= maxTableRows)
					table.setPreferredScrollableViewportSize(table.getPreferredSize());
				
				frame.pack();//resize window
				frame.setLocationRelativeTo(null);
			}
			
		};
		
		return hook;
	}
	
//	protected void addTextfieldToChangeHelper(JTextField textField){
//		
//		new TextFieldWithDocListener(textField){
//
//			@Override
//			public void didFieldChange(boolean changed) {
//				
//				setDirtyState(changed);
//			}
//			
//		};
//	}
	
	@Override
	public void focusGained(FocusEvent e) {

		
	}
	
	@Override
	public void focusLost(FocusEvent e) {

		clearWarning();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() instanceof JButton){
		
			JButton button = (JButton) e.getSource();
		
			ChildBaseGui baseGui = buttonToGuiMap.get(button);
			
			String warnings = null;
			
			if(baseGui!=null){
				
				String preOpenWarning = addBundlesOrReturnWarning(baseGui.getClass().getName());
				
				if(preOpenWarning==null){
				
					clearWarning();
					openChildGui(baseGui);
					
				}else{
					
					displayWarning(preOpenWarning);
				}
				
			}else if(button == helpButton){
				
				new HelpMenu(getTitle(), getHelpText());
			
			}else if(button == newButton){
								
				if(slider!=null){
					
					setNewButtonEnablement(false);

					if(isDirtyState() || hasDirtyChildren()){
					
						warnings = validatePreSaveDataAndReturnIssues();
						
						if(warnings==null){
							
							int dialogResult = JOptionPane.showConfirmDialog(null, "Save Changes before opening new?", "Changes not saved", JOptionPane.YES_NO_OPTION);
							
							if(dialogResult == JOptionPane.YES_OPTION){
		
								saveDataProcess();
								
							}
						}
					}
					if(isCanWriteXml() && hasDirtyChildren()){
						reloadXml();
						dirtyButtonUpdate();
						setPendingImageSaveAndConfigure(false);
					}
				}

				newButtonClicked();
			
			}else if(button == saveButton){
				
				warnings = validatePreSaveDataAndReturnIssues();
				
				if(warnings==null){
					
					saveDataProcess();
					if(slider!=null)((CustomSlider)slider).setReseting(false);
				
				}
				
			}else if(button == revertButton){
				
				int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all unsaved changes?", "Revert Changes", JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
				
					int pos=-1;
					if(slider!=null) pos = ((CustomSlider)slider).getCustomValue();
//					setPendingImageSaveAndConfigure(false);
					setFormReady(false);
					revertUnsavedChanges(pos);
					setFormReady(true);
					setPendingImageSaveAndConfigure(false);
					
				}
				
			}else if(button == deleteButton){
				
				String usageIssues = preDeleteChecks();
				
				if(usageIssues==null){
					deleteConfirmation();
				}else{
					displayWarning(usageIssues);
				}
			
			}else if(button == closeButton){
							
				if((anythingDirty() && confirmDirtyClose()) || (!anythingDirty() && additionalCloseConfirmation())){
						
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					closeDown();
					
				}
				
			}else {
				
				otherActions(button, frame);
			}
			
			if(warnings!=null){

				displayWarning(warnings);
				System.out.println(warnings);
			}
		}
		
	}
	
	protected String preDeleteChecks(){
		
		return null;
	}
	
	
	protected void reloadXml(){
		
		setPendingWriteXml(false);
		setDirtyChildren(false);
		clearAllChildrenPendingWriteXml(buttonToGuiMap.values());
		//reload to re-init all objects :|						
		try {
			String path = EditorGeneral.getWorkFolderPath();
			if(!path.endsWith("/")) path = path +"/";
			new ResourceParser().parseDocument(path + ResourceWriter.RESOURCE_FILENAME);
		} catch (SAXException | ParserConfigurationException | IOException e1) {
			e1.printStackTrace();
		}
	}
	
	protected void setNewButtonEnablement(boolean isEnabled){
		
		newButton.setEnabled(isEnabled);
	}
	
	protected void setDeleteButtonEnablement(boolean isEnabled){
		
		deleteButton.setEnabled(isEnabled);
	}

	private void saveDataProcess(){
		
		if(isCanWriteXml()){
			if(!EditorGeneral.isMajorincreased() && isPendingImageSave() && !isNewItem()){
				
				increaseMajorConfirmation();
				EditorGeneral.setMajorAndMinorFlag(true);
			}else if(!EditorGeneral.isMinorIncreased()){
				
				increaseMinorConfirmation();
				EditorGeneral.setMinorFlag(true);
			}
		}
		
		saveData();
		clearWarning();
		checkAndWriteXml();
		setFormComplete(true);
	}
	
	protected void increaseMinorConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Minor changes made, increase minor version number?", "Increase Minor Version", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			Resource.increaseMinorVersion();
		}
	}
	
	protected void increaseMajorConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Major changes made, increase major version number?", "Increase Major Version", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			Resource.increaseMajorVersion();
		}
	}
	
	protected void postSaveData(){
	
		setDeleteButtonEnablement(true);
		setNewButtonEnablement(true);
		setNewItem(false);
	}
	
	protected void deleteConfirmation(){
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Really Delete?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){

			deleteConfirmationAccepted();
		}
	}
	
	protected void deleteConfirmationAccepted(){
		
		setPendingImageSaveAndConfigure(false);
		
		//disable buttons is we just cleared the fields
		boolean enableButton = deleteDataProcess();
		deleteButton.setEnabled(enableButton);
		newButton.setEnabled(enableButton);

	}
	
	private boolean deleteDataProcess(){
		
		boolean enableDelete = deleteActions();
		
		checkAndWriteXml();
		
		return enableDelete;
	}

	private void checkAndWriteXml(){

		if(isCanWriteXml()){
			setPendingWriteXml(false);
			new ResourceWriter().writeXml(new File(EditorGeneral.getWorkFolder(), ResourceWriter.RESOURCE_FILENAME));
			ImageHelper.deleteRevertFolder();
		}else{
			setPendingWriteXml(true);
		}
		setDirtyChildren(false);
		clearAllChildrenPendingWriteXml(buttonToGuiMap.values());
		dirtyButtonUpdate();
		setPendingImageSaveAndConfigure(false);
	}
	
	
	protected void displayWarning(String warning){
		
		lastWarningTimeStamp = System.currentTimeMillis();
		infoTextField.setText(warning);
	}
	
	protected void clearWarning(){
		
		if(System.currentTimeMillis() - lastWarningTimeStamp > 2000)
			infoTextField.setText("");
	}
	
	protected boolean areAllChildrenComplete(){
		
		for (Iterator<ChildBaseGui> iterator = buttonToGuiMap.values().iterator(); iterator.hasNext();) {
			
			ChildBaseGui baseGui = (ChildBaseGui) iterator.next();
			
			if(!baseGui.formComplete) return false;
		}
		
		return true;
	}
	

//	protected boolean isChildCompleteByName(String name){
//		
//		for (Iterator<ChildBaseGui> iterator = buttonToGuiMap.values().iterator(); iterator.hasNext();) {
//			
//			ChildBaseGui baseGui = (ChildBaseGui) iterator.next();
//			
//			if(baseGui.getTitle().equalsIgnoreCase(name)) return baseGui.formComplete;
//		}
//		
//		throw new RuntimeException("no child by the name of "+name);
//	}
	

	protected boolean isFormComplete() {
		return formComplete;
	}

	protected void setFormComplete(boolean formComplete) {
		System.out.println(this.getClass().getName() + " setting complete to "+formComplete);
		this.formComplete = formComplete;
	}
	
	protected void refreshImages(){}
	

	public BaseGui getParentGui() {
		return parentGui;
	}


	public void setParentGui(BaseGui parentGui) {
		this.parentGui = parentGui;
	}


	public boolean isFormReady() {
		return formReady;
	}


	public void setFormReady(boolean formReady) {
		this.formReady = formReady;
	}
	
	protected String addBundlesOrReturnWarning(String childName){
	
		//override and add bundles, return issue in override
		return null;
	}
	
	protected void initImageSliderFromPanel(JPanel imagePanel){
		
		if(imagePanel==null || imagePanel.getComponentCount()==1){
			System.out.println("ImagePanel was NULL or no slider on panel");
			return;
		}
		
		imageSlider = (JSlider) imagePanel.getComponent(1);
		
		imageSlider.setValue(0);
		imageSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent evt) {
				
			    JSlider slider = (JSlider) evt.getSource();
			    
			    int value = slider.getValue();
			    
			    if(!slider.getValueIsAdjusting()){
			    	
			    	imageSliderUpdated(value);
			    	
			    }
				
			}
			
		});
		
	}
	
	protected void imageSliderUpdated(int value){}

	protected int getImageSliderValue(){
		
		if(imageSlider!=null) return imageSlider.getValue();
//		else System.out.println("image slider not init");
		
		return 0;//failed
	}
	
	protected void zeroImageSliderValue(){
		
		if(imageSlider!=null){
			setDisableImageSlider(true);
			imageSlider.setValue(0);
			setDisableImageSlider(false);
		}
		else System.out.println("image slider not init");
	}
	
	protected void enableImageSlider(boolean enabled){
		
		if(imageSlider!=null) imageSlider.setEnabled(enabled);
		else System.out.println("image slider not init");
	}

	public boolean isDisableImageSlider() {
		return disableImageSlider;
	}


	public void setDisableImageSlider(boolean disableImageSlider) {
		this.disableImageSlider = disableImageSlider;
	}


	protected abstract void revertUnsavedChanges(int pos);
	
	protected abstract void newButtonClicked();
	
	protected abstract boolean deleteActions();
	
	protected abstract String validatePreSaveDataAndReturnIssues();
	
	protected abstract void passedBundle(Map<String, Object> properties);
	
	protected abstract String getHelpText();

}
