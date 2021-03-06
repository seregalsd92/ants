package com.ibm.sk.ff.gui.simple;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ibm.sk.dto.qualification.QualificationCandidate;
import com.ibm.sk.dto.qualification.QualificationTable;

public class MenuQualificationTableCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	private QualificationTable qualification;

	MenuQualificationTableCellRenderer(QualificationTable qualification) {
		this.qualification = qualification == null ? new QualificationTable() : qualification;
	}
	
	@Override
	public int getHorizontalAlignment() {
        return JLabel.CENTER;
    }

	 @Override
     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

         //Cells are by default rendered as a JLabel.
         JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

         //Get the status for the current row.
         QualificationCandidate candidate = qualification.getCandidate(row);
         if (candidate == null) {
        	 l.setBackground(Color.WHITE);
         } else {
        	 l.setBackground(candidate.isQualified() ? new Color(200, 221, 242) : new Color(238, 238, 238));
         }

         //Return the JLabel which renders the cell.
         return l;
     }
}
