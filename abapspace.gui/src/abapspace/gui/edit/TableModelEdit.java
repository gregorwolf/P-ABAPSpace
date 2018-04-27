package abapspace.gui.edit;

import javax.swing.table.AbstractTableModel;

public class TableModelEdit extends AbstractTableModel {

	private static final long serialVersionUID = -9070204988905662706L;

	private String[] colNames;
	private Object[][] data;

	public TableModelEdit(String[] colNames, Object[][] data) {
		this.colNames = colNames;
		this.data = data;
	}

	@Override
	public int getRowCount() {
		return this.data.length;
	}

	@Override
	public String getColumnName(int col) {
		return colNames[col];
	}

	@Override
	public int getColumnCount() {
		return this.colNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.data[rowIndex][columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col != 1) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

}
