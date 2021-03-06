package com.ibm.sk.ff.gui.simple;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ibm.sk.dto.matchmaking.StartGameData;
import com.ibm.sk.ff.gui.common.events.GuiEvent;
import com.ibm.sk.ff.gui.common.events.GuiEventListener;
import com.ibm.sk.ff.gui.common.mapper.Mapper;

/**
 * <h2>Listener for the {@link Menu}</h2>
 */
public class MenuListener implements ChangeListener, ActionListener, ListSelectionListener {

	private JFrame mainContainer = null;
	private GuiEventListener listener = null;
	private JTabbedPane tabbedPane = null;
	private JButton buttonStart = null;
	private JRadioButton radioQualification = null;
	private JRadioButton radioTournamentSemiFinals = null;
	private JScrollPane scrollPaneQualification;
	private JScrollPane scrollPaneTournament;
	private JList<String> firstListOfAnthills = null;
	private JList<String> secondListOfAnthills = null;
	private JList<String> thirdListOfAnthills = null;
	private JList<String> replays = null;
	private JCheckBox runInBackgroundCheckbox = null;

	public MenuListener(JFrame mainContainer, GuiEventListener listener, JTabbedPane tabbedPane,
			JList<String> firstListOfAnthills, JList<String> secondListOfAnthills, JList<String> thirdListOfAnthills,
			JList<String> replays, JButton buttonStart, JRadioButton radioQualification, JRadioButton radioTournamentSemiFinals,
			JScrollPane scrollPaneQualification, JScrollPane scrollPaneTournament, JCheckBox runInBackgroundCheckbox) {
		this.tabbedPane = tabbedPane;
		this.buttonStart = buttonStart;
		this.firstListOfAnthills = firstListOfAnthills;
		this.secondListOfAnthills = secondListOfAnthills;
		this.thirdListOfAnthills = thirdListOfAnthills;
		this.replays = replays;
		this.radioQualification = radioQualification;
		this.radioTournamentSemiFinals = radioTournamentSemiFinals;
		this.scrollPaneQualification = scrollPaneQualification;
		this.scrollPaneTournament = scrollPaneTournament;
		this.listener = listener;
		this.runInBackgroundCheckbox = runInBackgroundCheckbox;

		tabbedPane.addChangeListener(this);
		firstListOfAnthills.addListSelectionListener(this);
		secondListOfAnthills.addListSelectionListener(this);
		thirdListOfAnthills.addListSelectionListener(this);
		replays.addListSelectionListener(this);
		buttonStart.addActionListener(this);
		radioQualification.addActionListener(this);
		radioTournamentSemiFinals.addActionListener(this);
	}

	/**
	 * {@link ChangeEvent} of the {@link #tabbedPane} (Menu: Single Player /
	 * Duel / Tournament / About)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JTabbedPane) {
			if (tabbedPane.getSelectedIndex() == 4) {
				if (buttonStart != null) {
					buttonStart.setEnabled(false);
				}
			} else {
				if (buttonStart != null) {
					buttonStart.setEnabled(true);
				}
			}
		}

	}

	/**
	 * {@link ActionEvent} of the {@link #buttonStart} (Menu: Single Player /
	 * Duel / Tournament / About)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(buttonStart)) {
			switch (tabbedPane.getSelectedIndex()) {
			case 0:
				if (firstListOfAnthills.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(mainContainer, "Please, select 1 anthill.",
							"Can't start a single player game!", JOptionPane.WARNING_MESSAGE);
				} else {
					sendGuiEvent(new GuiEvent(GuiEvent.EventTypes.SINGLE_PLAY_START,
							firstListOfAnthills.getSelectedValue()));
				}
				break;
			case 1:
				if (secondListOfAnthills.getSelectedIndex() == -1 && thirdListOfAnthills.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(mainContainer, "No anthills have been selected.",
							"Can't start a duel!", JOptionPane.WARNING_MESSAGE);
				} else if (secondListOfAnthills.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(mainContainer, "The player 1 has not been selected.",
							"Can't start a duel!", JOptionPane.WARNING_MESSAGE);
				} else if (thirdListOfAnthills.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(mainContainer, "The player 2 has not been selected.",
							"Can't start a duel!", JOptionPane.WARNING_MESSAGE);
				} else {
					sendGuiEvent(
							new GuiEvent(GuiEvent.EventTypes.DOUBLE_PLAY_START, secondListOfAnthills.getSelectedValue()
									+ GuiEvent.HLL_NAMES_SEPARATOR + thirdListOfAnthills.getSelectedValue()));
				}
				break;
			case 2:
				
				StartGameData data = new StartGameData();
				data.setRunInBackground(runInBackgroundCheckbox.isSelected());
				if (radioTournamentSemiFinals.isSelected()) {
					sendGuiEvent(new GuiEvent(GuiEvent.EventTypes.TOURNAMENT_PLAY_START, Mapper.INSTANCE.pojoToJson(data)));
				} else {
					List<String> players = new ArrayList<>();
					ListModel<String> m = firstListOfAnthills.getModel();
					for (int i = 0 ; i < m.getSize(); i++) {
						players.add(m.getElementAt(i));
					}
					data.setPlayers(players);
					sendGuiEvent(new GuiEvent(GuiEvent.EventTypes.QUALIFICATION_START, Mapper.INSTANCE.pojoToJson(data)));
				}
				
				break;
			case 3:
				if (replays.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(mainContainer, "Please, select 1 saved game.",
							"Can't start a replay!", JOptionPane.WARNING_MESSAGE);
				} else {
					sendGuiEvent(new GuiEvent(GuiEvent.EventTypes.REPLAY_SELECTED,
							replays.getSelectedValue()));
				}
				break;
			case 4:
				// About dialog, ignore, no ActionEvent is expected here
				break;
			default:
				// TODO: REPLAY_SELECTED / listener.actionPerformed(new
				// GuiEvent(GuiEvent.EventTypes.REPLAY_SELECTED, ""));
				// TODO: REPLAYS_SELECTED / listener.actionPerformed(new
				// GuiEvent(GuiEvent.EventTypes.REPLAY_SELECTED,
				// combo_replays.getSelectedItem().toString()));
				// TODO: START_REPLAY
				break;
			}
		} else if (e.getSource().equals(radioQualification)) {
			scrollPaneQualification.setVisible(true);
			if (scrollPaneTournament != null) {
				scrollPaneTournament.setVisible(false);
			}
			scrollPaneQualification.getParent().validate();
		} else if (e.getSource().equals(radioTournamentSemiFinals)) {
			scrollPaneQualification.setVisible(false);
			if (scrollPaneTournament != null) {//for initial tournament run, pane is not initialized
				scrollPaneTournament.setVisible(true);
			}
			scrollPaneQualification.getParent().validate();
		}
	}

	/**
	 * {@link ListSelectionEvent} of the {@link #firstListOfAnthills} or
	 * {@link #secondListOfAnthills} or {@link #thirdListOfAnthills} in the menu
	 * "Single Player" / "Duel".
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(firstListOfAnthills)) {
			if (!e.getValueIsAdjusting()) {
				sendGuiEvent(
						new GuiEvent(GuiEvent.EventTypes.PLAYER_1_SELECTED, firstListOfAnthills.getSelectedValue()));
			}
		} else if (e.getSource().equals(secondListOfAnthills)) {
			if (!e.getValueIsAdjusting()) {
				sendGuiEvent(
						new GuiEvent(GuiEvent.EventTypes.PLAYER_1_SELECTED, secondListOfAnthills.getSelectedValue()));
			}
		} else if (e.getSource().equals(thirdListOfAnthills)) {
			if (!e.getValueIsAdjusting()) {
				sendGuiEvent(
						new GuiEvent(GuiEvent.EventTypes.PLAYER_2_SELECTED, thirdListOfAnthills.getSelectedValue()));
			}
		} else if (e.getSource().equals(replays)) {
			if (!e.getValueIsAdjusting()) {
				sendGuiEvent(
						new GuiEvent(GuiEvent.EventTypes.REPLAY_SELECTED, replays.getSelectedValue()));
			}
		}
	}

	protected void sendGuiEvent(GuiEvent guiEvent) {
		if (listener != null) {
			listener.actionPerformed(guiEvent);
		}
	}

}
