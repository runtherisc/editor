package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class HelpMenu {

	public HelpMenu(String formName, String text){
		
		this.formName = formName;
		if(text==null) this.text = "Documentation is still pending for this form";
		else this.text = text;
		initFrame();
	}
	
	private String formName, text;
	private JFrame frame;
	
	private void initFrame(){
		
		frame = new JFrame(getFrameTitle());
        frame.setLayout(new GridBagLayout());	
        
        JTextArea helpBox = new JTextArea();
        helpBox.setEditable(false);
        helpBox.setLineWrap(true);
        helpBox.setWrapStyleWord(true);
        helpBox.setText(text);
        helpBox.setCaretPosition(0);
        JScrollPane scrollpane = new JScrollPane(helpBox);
        scrollpane.setPreferredSize(new Dimension(600, 200));
        scrollpane.setMinimumSize(new Dimension(300, 100));
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        GridBagConstraints gbc = new GridBagConstraints();	
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        frame.add(scrollpane, gbc);
        
        JPanel panel = new JPanel();
        JButton closeButton = new JButton();
        closeButton.setText("Close");
        closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				frame.dispose();
				
			}
		});
        panel.add(closeButton);
        
        gbc = new GridBagConstraints();	
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(panel, gbc);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private String getFrameTitle(){
		
		return "Help for "+formName;
	}
}
