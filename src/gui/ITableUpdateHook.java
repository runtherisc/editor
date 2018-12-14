package gui;

public interface ITableUpdateHook {

	//selected = -1 is a new row at the bottom of the table else the selected row will be replaced (if must exist!)
	void addDataRowToTable(Object[] objects, int selectedRow);
	
	void removeRow(int selectedRow);
	
	void insertRow(Object[] objects, int selectedRow);
	
	int getMatchingRowId(Object entry, int column);
	
	boolean doesObjectMatch(Object entry, int column, int row);
	
	Object[] getSelectedRow(int selectedRow);
	
	void clearTable();
}
