package gui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//need to find a better listener for text changing, this is very flaky
public abstract class JTextFieldWithDocListener extends JTextField implements DocumentListener{

	private static final long serialVersionUID = 1563065367789112183L;

	public JTextFieldWithDocListener(int textSpan){

		super(textSpan);
		getDocument().addDocumentListener(this);
	}
	
	private String baseText;
	
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		
//		System.out.println("changedUpdate");
		if(baseText==null) baseText="";
		didFieldChange(!baseText.equals(getText()));
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		
//		System.out.println("insertUpdate");
		if(baseText==null) baseText="";
		didFieldChange(!baseText.equals(getText()));
		doImageRefresh();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		
//		System.out.println("removeUpdate");
		if(baseText==null) baseText="";
		didFieldChange(!baseText.equals(getText()));
		doImageRefresh();
	}
	
	
	
	@Override
	public void setText(String t) {
		
//		System.out.println("setText");
		this.baseText = t;
		super.setText(t);

	}

	//when this is called and is true, getText() may be empty, gets called twice by the same listener method
	public abstract void didFieldChange(boolean changed);
	public abstract void doImageRefresh();

}
