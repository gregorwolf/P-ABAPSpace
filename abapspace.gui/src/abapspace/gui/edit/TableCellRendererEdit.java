/*
 * MIT License
 *
 * Copyright (c) 2018 mnemotron
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package abapspace.gui.edit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class TableCellRendererEdit extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -1254525574172848599L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component locComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		switch (column) {
		case TableModelEdit.COLUMN_INDEX_REPLACEMENT: // replacement}
			JTextField locTxfReplacement = new JTextField();
			locTxfReplacement.setText((String) value);
			locTxfReplacement.setBackground(this.getColorCheckMaxLength(table, row));
			locTxfReplacement.setBorder(null);
			Font locFont1 = locTxfReplacement.getFont();
			Font locNewFont1 = new Font(locFont1.getFontName(), locFont1.getStyle(), 16);
			locTxfReplacement.setFont(locNewFont1);
			locComponent = locTxfReplacement;
			break;
		case TableModelEdit.COLUMN_INDEX_LENGTH: // length
			JTextField locTxfLength = new JTextField();
			locTxfLength.setText((new Integer((int) value)).toString());
			locTxfLength.setEditable(false);
			locTxfLength.setBackground(this.getColorCheckMaxLength(table, row));
			locTxfLength.setBorder(null);
			locTxfLength.setHorizontalAlignment(JTextField.CENTER);
			Font locFont3 = locTxfLength.getFont();
			Font locNewFont3 = new Font(locFont3.getFontName(), locFont3.getStyle(), 16);
			locTxfLength.setFont(locNewFont3);
			locComponent = locTxfLength;
			break;
		}

		return locComponent;
	}

	private Color getColorCheckMaxLength(JTable table, int row) {
		TableModel locTableModel = table.getModel();

		Integer locMaxLength = (Integer) locTableModel.getValueAt(row, TableModelEdit.COLUMN_INDEX_MAX_LENGTH); // get max length
		Integer locLength = (Integer) locTableModel.getValueAt(row, TableModelEdit.COLUMN_INDEX_LENGTH); // get length

		if (locLength > locMaxLength) {
			return new Color(255, 205, 205);
		} else {
			return new Color(213, 255, 233);
		}
	}
}
