package gui.image;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.map.resources.Coords;
import data.map.resources.ImageResource;
import game.ImageHelper;
import gui.ChildBaseGui;
import gui.PropertyKeys;

public class ImageWalkoverGui extends ChildBaseGui {

	public ImageWalkoverGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	protected ImageResource imageResource;
	private Coords span;
	private JButton toggle;
	private boolean toggleState;
	private Coords hotspot;
	private boolean[][] walkoverGrid;
	private BufferedImage[][] imageGrid;
	private JLabel[][] labelGrid;
	private String walkoverText = "Mode walkover: Click on panels, red panels cannot be walked over";
	private String hotspotText = "Mode entry/exit: Click on a panel, blue panel is the entry/exit";
	
	
	@Override
	protected int addComponents(JFrame frame) {
	
		JPanel panel = new JPanel(new GridBagLayout());
		
		imageGrid = ImageHelper.getImageGridFromStatic(span);
		labelGrid = new JLabel[span.x][span.y];
		
		toggleState = imageResource.getMovement()!=null && imageResource.getMovement().getSkip() > 0;
		
		//grid is set but span has changed
		if(imageResource.getWalkover()!=null && 
				(imageResource.getWalkover().length!=span.x() || 
				imageResource.getWalkover()[0].length!=span.y())){
	
			imageResource.setWalkover(new boolean[span.x][span.y]);
			imageResource.nullHotspots();
			if(toggleState) imageResource.fillWalkoverGrid(true);
			setFormComplete(false);
		}
		
		walkoverGrid = (imageResource.getWalkover()!=null ? copyArray(imageResource.getWalkover())  : new boolean[span.x][span.y]);

		hotspot = imageResource.getFirstHotspot();
		
		
		
		for (int i = 0; i < span.x; i++) {
			for (int j = 0; j < span.y; j++) {

		        final JLabel imageLabel = new JLabel();
		        labelGrid[i][j] = imageLabel;
		        final int labelX = i;
		        final int labelY = j;
		        colourPanel(imageLabel, labelX, labelY);
		        //maybe add a image to a button instead?
		        imageLabel.addMouseListener(new MouseListener() {

					public void mouseReleased(MouseEvent e) {}
					@Override
					public void mousePressed(MouseEvent e) {
						
						Image result;
						
						if(toggleState){
							
							if(labelX > 0 && labelX < span.x-1 && labelY > 0 && labelY < span.y-1){
								displayWarning("exit/entry must be on the edge");
								return;
							}
							clearWarning();
							setDirtyStateAndConfigure(true);
							walkoverGrid[labelX][labelY] = true;
							int hotX = -1;
							int hotY = -1;
							
							//clear hotspot label if set
							if(hotspot.x > -1){
								hotX = span.x-hotspot.x-1;
								hotY = span.y-hotspot.y-1;	
								labelGrid[hotX][hotY].setIcon(new ImageIcon(imageGrid[hotX][hotY]));
								labelGrid[hotX][hotY].setBorder(BorderFactory.createLineBorder(Color.GRAY));
							}
							
							//set hotspot label
							if(hotX==-1 || hotX!=labelX || hotY!=labelY){

					        	result = ImageHelper.addBlueMaskToImage(imageGrid[labelX][labelY]);
					        	imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
					        	hotspot = new Coords(span.x-labelX-1, span.y-labelY-1);
							}else{
								hotspot = new Coords(-1, -1);
								return;
							}
				        	
						}else{
							
							if(labelX == span.x-hotspot.x-1 && labelY == span.y-hotspot.y-1){
								displayWarning("exit/entry must always be walked over");
								return;
							}
							clearWarning();
							setDirtyStateAndConfigure(true);
							
							walkoverGrid[labelX][labelY] = !walkoverGrid[labelX][labelY];
										
							if(!walkoverGrid[labelX][labelY]){
					        	result = ImageHelper.addRedMaskToImage(imageGrid[labelX][labelY]);
					        	imageLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
					        }else{
					        	System.out.println("setting grey");
					        	result = imageGrid[labelX][labelY];
					        	imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
					        }
								
						}
						imageLabel.setIcon(new ImageIcon(result));
					}
					public void mouseExited(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}		
					//doesn't register a quick click
					public void mouseClicked(MouseEvent e) {}
				});
		        panel.add(imageLabel, getNoPaddingGridBagConstraints(i, j));
			}
			
		} 
        
        frame.add(panel, getAllPaddingGridBagConstraints(0, 0));
        toggle = new JButton(walkoverText);
        if(toggleState) toggle.setText(hotspotText);
        toggle.addActionListener(this);
        frame.add(toggle, getAllPaddingGridBagConstraints(0, 1));
		
		return 2;
	}
	
	protected void postDrawGui(){
		
		setDeleteButtonEnablement(false);

	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		imageResource = (ImageResource) properties.get(PropertyKeys.IMAGE_RESOURCE);
		
		span = (Coords) properties.get(PropertyKeys.IMAGE_RESOURCE_SPAN);

	}


	@Override
	protected void otherActions(JButton button, JFrame frame) {
		super.otherActions(button, frame);
		if(button.equals(toggle)){
			
			clearWarning();
			if(imageResource.getMovement()!=null && imageResource.getMovement().getSkip() > 0){
				displayWarning("Items that move do not need the walkover grid set");
			}else{
				toggleState = !toggleState;
				if(toggleState) toggle.setText(hotspotText);
				else toggle.setText(walkoverText);
			}
		}
	}
	
	protected void clearGrid(){
		
		walkoverGrid = new boolean[span.x][span.y];
		hotspot = new Coords(-1, -1);
		redrawGrid(true);
	}
	
	protected void redrawGrid(boolean clearingGrid){
		
		for (int i = 0; i < span.x; i++) {
			for (int j = 0; j < span.y; j++) {
				
				if(clearingGrid) walkoverGrid[i][j] = true;

				colourPanel(labelGrid[i][j], i, j);
			}
		}
	}
	
	private void colourPanel(JLabel imageLabel, int labelX, int labelY){
		
        Image result;
        if(hotspot!=null && hotspot.x==span.x-labelX-1 && hotspot.y==span.y-labelY-1){
        	result = ImageHelper.addBlueMaskToImage(imageGrid[labelX][labelY]);
        	imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        }else if(!walkoverGrid[labelX][labelY]){
        	result = ImageHelper.addRedMaskToImage(imageGrid[labelX][labelY]);
        	imageLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        }else{
        	result = imageGrid[labelX][labelY];
        	imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        imageLabel.setIcon(new ImageIcon(result));
	}

	@Override
	protected void revertUnsavedChanges(int pos){
		
		walkoverGrid = imageResource.getWalkover()!=null ? copyArray(imageResource.getWalkover())  : new boolean[span.x][span.y];
		hotspot = imageResource.getFirstHotspot();
		redrawGrid(false);
	}

	@Override
	protected void newButtonClicked() {
		
		int dialogResult = JOptionPane.showConfirmDialog(null, "Clear all selected panels?", "Clear Grid", JOptionPane.YES_NO_OPTION);
		
		if(dialogResult == JOptionPane.YES_OPTION){
			
			clearGrid();
		}

	}

	@Override
	protected boolean deleteActions() {
		// not used
		return false;
	}

	@Override
	protected String validatePreSaveDataAndReturnIssues() {
		
		if(hotspot.x == -1){
			return "select 'Mode entry/exit' then click an edge panel";
		}
		return null;
	}

	@Override
	protected void saveData() {
		
		imageResource.setWalkover(copyArray(walkoverGrid));
		imageResource.setFirstHotSpot(hotspot);

	}
	
	//yuck
	private boolean[][] copyArray(boolean[][] source){
		
		boolean[][] dest = new boolean[span.x][span.y];
		
		for (int i = 0; i < span.x; i++) {
			for (int j = 0; j < span.y; j++) {
				
				dest[i][j] = source[i][j];
			}
		}
		
		return dest;
	}

	@Override
	protected String getHelpText() {
		
		return new StringBuilder("Walkover Grid\n\n")
						 .append("There are two modes on the walkover grid; walkover and entry/exit.\n")
						 .append("These can be toggled between using the mode button below the grid.\n\n\n")
						 .append("Walkover Mode\n\n")
						 .append("When the grid is in walkover mode, it allows you to set which areas can be walked upon by workers and moving map items (eg deers).\n\n")
						 .append("If you can click on any square, it will toggle it between being red and clear.\n")
						 .append("When they are red they cannot be walked over.\n\n")
						 .append("It is worth noting that a worker's 'feet' is the bottom right square, it is this square that is not allowed on squares marked in red on the walkover grid.\n\n")
						 .append("Think about which squares to mark in red, for example, marking only the base of a tree as non-walkover will mean that a worker can walk behind it* as the worker will be drawn first and then the tree will be drawn on top of it. (*unless it is a map item that has been marked as flat)\n\n")
						 .append("Moving Map Items do not need a walkover grid set, as they can always be walked over.\n\n\n")
						 .append("Entry/Exit Mode\n\n")
						 .append("When the grid is in Entry/Exit mode, it allows you to set the square that workers will walk to.\n\n")
						 .append("This square is marked in blue and there can only be one set.  This will be also be a square that can be walked over\n")
						 .append("The Entry/Exit square must be an edge square.").toString();
	}

}
