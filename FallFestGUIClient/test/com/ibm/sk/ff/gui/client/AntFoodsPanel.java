package com.ibm.sk.ff.gui.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ibm.sk.ff.gui.common.objects.gui.GAntFoodObject;

public class AntFoodsPanel extends JPanel {
	
	private static final long serialVersionUID = -8780098469519172008L;
	
	private DefaultListModel<GAntFoodObject> listModel = new DefaultListModel<>();
	private JList<GAntFoodObject> list = new JList<>(listModel);
	
	private AntFoodView antfoodView = new AntFoodView();
	
	private JPanel panel_buttons = new JPanel();
	private JButton button_set = new JButton("Set");
	private JButton button_remove = new JButton("Remove");
	
	private GUIFacade facade;
	
	public AntFoodsPanel(GUIFacade facade) {
		this.facade = facade;
		
		setLayout(new BorderLayout());
		
		setEvents();
		
		panel_buttons.setLayout(new FlowLayout());
		panel_buttons.add(button_set);
		panel_buttons.add(button_remove);
		
		list.setPreferredSize(new Dimension(70, 200));
		
		add(list, BorderLayout.WEST);
		add(antfoodView, BorderLayout.CENTER);
		add(panel_buttons, BorderLayout.SOUTH);
	}
	
	private void setEvents() {
		button_set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GAntFoodObject gad = antfoodView.createAntFoodObject();
				
				if (listModel.contains(gad)) {
					listModel.set(listModel.indexOf(gad), gad);
				} else {
					listModel.addElement(gad);
					facade.join(gad.getAnt(), gad.getFood());
				}
				
				facade.set(gad.getAnt());
			}
		});
		
		button_remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GAntFoodObject gad = listModel.getElementAt(list.getSelectedIndex());
				listModel.removeElement(gad);
				facade.remove(gad);
				antfoodView.clear();
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selected = list.getSelectedIndex();
				if (selected > 0 && selected < listModel.size()) {
					antfoodView.setAntFoodObject(listModel.getElementAt(selected));
				}
			}
		});
	}

}
