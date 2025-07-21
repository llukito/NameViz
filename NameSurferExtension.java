
/*
 * File: NameSurferExtension.java
 * ---------------------
 * When it is finished, this program will implements the viewer for
 * the baby-name database described in the assignment handout.
 */

import acm.io.IODialog;
import acm.program.*;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class NameSurferExtension extends Program implements NameSurferConstants {

	// instance variables of J Components
	private JTextField textField, deleteTextField, chartTextField, yearTextField;
	private JButton graphButton, clearButton, deleteButton, chart, removeChart, yearBut;
	// Instance variables of objects of other classes
	private NameSurferDataBase base;
	private NameSurferGraphExtension graph;
	// Instance variable of list, which stores names that have graphs on canvas
	public List<String> namesOnGraph = new ArrayList<>();
	// IODialog for user-related interactions
	private IODialog dialog = getDialog();

	// Constructor of this class
	public NameSurferExtension() {
		base = new NameSurferDataBase(NAMES_DATA_FILE);
		graph = new NameSurferGraphExtension();
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
		JComponentsForNameInsertion();
		JComponentsForNameDeletion();
		JComponentsForChart();
	}

	private void JComponentsForNameInsertion() {
		add(new JLabel("Name"), SOUTH); // we don't need its instance
		textField = new JTextField(20);
		add(textField, SOUTH);
		textField.addActionListener(this);// pressing enter will work too
		graphButton = new JButton("Graph");
		add(graphButton, SOUTH);
	}

	private void JComponentsForNameDeletion() {
		clearButton = new JButton("Clear"); // this button deletes all names
		add(clearButton, SOUTH);
		add(new JLabel("Delete Name"), SOUTH); // we don't need its instance
		deleteTextField = new JTextField(20);
		add(deleteTextField, SOUTH);
		deleteTextField.addActionListener(this);
		deleteButton = new JButton("Delete"); // this button deletes one name
		add(deleteButton, SOUTH);
	}

	/*
	 * We have two charts in this program. One displays ranks of a person
	 * through years but like a bar chart not our default linear chart. Second
	 * chart is related to years, since it shows top 11 names in entered year
	 */
	private void JComponentsForChart() {
		add(new JLabel("Name for chart"), NORTH);
		chartTextField = new JTextField(10);
		add(chartTextField, NORTH);
		chartTextField.addActionListener(this);
		chart = new JButton("Name Chart");
		add(chart, NORTH);
		add(new JLabel("Year"), NORTH);
		yearTextField = new JTextField(10);
		add(yearTextField, NORTH);
		yearTextField.addActionListener(this);
		yearBut = new JButton("Year Chart");
		add(yearBut, NORTH);
		removeChart = new JButton("Back to Graph"); // just removes charts
		add(removeChart, NORTH);
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
			namesOnGraph.clear();
		} else if (e.getSource() == deleteButton || e.getSource() == deleteTextField) {
			deleteName();
		} else if (e.getSource() == chart || e.getSource() == chartTextField) {
			displayChart();
		} else if (e.getSource() == removeChart) {
			graph.removeCharts();
		} else if (e.getSource() == yearBut || e.getSource() == yearTextField) {
			displayYearChart();
		}
		// we reset textFields
		resetTextFields();
	}

	private void displayName() {
		graph.removeCharts(); // at first we have to be on our main page
		// I turn name into lowerCase to handle case-sensitivity
		// And I also trim it to avoid unnecessary void spaces
		String name = textField.getText().toLowerCase().trim();
		if (validNameEntered(name)) { // if name is in base
			displayGraph(name);
		} else {
			dialog.showErrorMessage("Invalid name");
		}
	}

	private boolean validNameEntered(String name) {
		return base.findEntry(name) != null;
	}

	/*
	 * This method will display graph on canvas, if it is not already displayed.
	 * If it is already there, user will receive specific message
	 */
	private void displayGraph(String name) {
		if (namesOnGraph.contains(name)) {
			dialog.println("Name is already on graph");
		} else {
			graph.addEntry(base.findEntry(name));
			namesOnGraph.add(name);
		}
	}

	/*
	 * Deletes graph of specific name
	 */
	private void deleteName() {
		graph.removeCharts(); // at first we have to be on our main page
		String name = deleteTextField.getText().toLowerCase().trim();
		if (namesOnGraph.contains(name)) { // if name is on canvas
			namesOnGraph.remove(name);
			graph.removeName(name);
		} else {
			dialog.showErrorMessage("Name is not on canvas");
		}
	}

	private void displayChart() {
		// I turn name into lowerCase to handle case-sensitivity
		// And I also trim it to avoid unnecessary void spaces
		String name = chartTextField.getText().toLowerCase().trim();
		if (validNameEntered(name)) { // if name is in base
			graph.createChart(base.findEntry(name));
		} else {
			dialog.showErrorMessage("Invalid name");
		}
	}

	private void displayYearChart() {
		String year = yearTextField.getText().trim();
		if (!year.isEmpty() && isNumber(year) && yearIsValid(year)) {
			graph.drawYearChart(year);
		} else {
			dialog.showErrorMessage("Invalid year");
		}
	}

	/*
	 * String year should be made with only digits
	 */
	private boolean isNumber(String year) {
		for (int i = 0; i < year.length(); i++) {
			char c = year.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Checks if it is in the range of 1900-2000
	 */
	private boolean yearIsValid(String year) {
		int intYear = Integer.parseInt(year);
		for (int i = START_DECADE; i <= 2000; i = i + 10) {
			if (intYear == i) {
				return true;
			}
		}
		return false;
	}

	private void resetTextFields() {
		textField.setText("");
		deleteTextField.setText("");
		chartTextField.setText("");
		yearTextField.setText("");
	}
}
