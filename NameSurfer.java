
/*
 * File: NameSurfer.java
 * ---------------------
 * When it is finished, this program will implements the viewer for
 * the baby-name database described in the assignment handout.
 */

import acm.io.IODialog;
import acm.program.*;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class NameSurfer extends Program implements NameSurferConstants {

	// instance variables of J Components
	private JTextField textField;
	private JButton graphButton, clearButton;
	// Instance variables of objects of other classes
	private NameSurferDataBase base;
	private NameSurferGraph graph;
	// Instance variable of list, which stores names that have graphs on canvas
	private List<String> namesOnGraph = new ArrayList<>();

	// Constructor of this class
	public NameSurfer() {
		base = new NameSurferDataBase(NAMES_DATA_FILE);
		graph = new NameSurferGraph();
		add(graph);
	}

	/* Method: init() */
	/**
	 * This method has the responsibility for reading in the data base and
	 * initializing the interactors at the bottom of the window.
	 */
	public void init() {
		addJComponents();
		addActionListeners();
	}

	/*
	 * We add J Components on canvas
	 */
	private void addJComponents() {
		add(new JLabel("Name"), SOUTH); // we don't need its instance
		textField = new JTextField(20);
		add(textField, SOUTH);
		graphButton = new JButton("Graph");
		add(graphButton, SOUTH);
		textField.addActionListener(this); // pressing enter will work too
		clearButton = new JButton("Clear");
		add(clearButton, SOUTH);
	}

	/* Method: actionPerformed(e) */
	/**
	 * This class is responsible for detecting when the buttons are clicked, so
	 * you will have to define a method to respond to button actions.
	 */
	public void actionPerformed(ActionEvent e) {
		// Pressing enter or graph button can both work
		if (e.getSource() == graphButton || e.getSource() == textField) {
			displayName();
		} else if (e.getSource() == clearButton) {
			graph.clear();
			namesOnGraph.clear(); // reset list
		}
		textField.setText(""); // we reset textField
	}

	private void displayName() {
		// initialize it for user-related communication
		IODialog dialog = getDialog();
		// I turn name into lowerCase to handle case-sensitivity
		// And I also trim it to avoid unnecessary void spaces
		String name = textField.getText().toLowerCase().trim();
		if (validNameEntered()) { // if name is in base
			displayGraph(name, dialog);
		} else {
			dialog.showErrorMessage("Invalid name");
		}
	}

	private boolean validNameEntered() {
		return base.findEntry(textField.getText().toLowerCase().trim()) != null;
	}

	/*
	 * This method will display graph on canvas, if it is not already displayed.
	 * If it is already there, user will receive specific message
	 */
	private void displayGraph(String name, IODialog dialog) {
		if (namesOnGraph.contains(name)) {
			dialog.println("Name is already on graph");
		} else {
			graph.addEntry(base.findEntry(name));
			namesOnGraph.add(name);
		}
	}
}
