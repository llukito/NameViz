
/*
 * File: NameSurferGraph.java
 * ---------------------------
 * This class represents the canvas on which the graph of
 * names is drawn. This class is responsible for updating
 * (redrawing) the graphs whenever the list of entries changes or the window is resized.
 */

import acm.graphics.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.awt.*;

public class NameSurferGraph extends GCanvas implements NameSurferConstants, ComponentListener {

	/* Instance variables */
	// this arrayList will store entries
	private List<NameSurferEntry> entries = new ArrayList<>();

	/**
	 * Creates a new NameSurferGraph object that displays the data.
	 */
	public NameSurferGraph() {
		addComponentListener(this);
	}

	/**
	 * Clears the list of name surfer entries stored inside this class.
	 */
	public void clear() {
		// along with canvas, arrayList should be cleared too
		entries.clear();
		update();
	}

	/* Method: addEntry(entry) */
	/**
	 * Adds a new NameSurferEntry to the list of entries on the display. Note
	 * that this method does not actually draw the graph, but simply stores the
	 * entry; the graph is drawn by calling update.
	 */
	public void addEntry(NameSurferEntry entry) {
		entries.add(entry);
		update();
	}

	/**
	 * Updates the display image by deleting all the graphical objects from the
	 * canvas and then reassembling the display according to the list of
	 * entries. Your application must call update after calling either clear or
	 * addEntry; update is also called whenever the size of the canvas changes.
	 */
	public void update() {
		removeAll();
		drawOutline();
		drawEntries();
	}

	/*
	 * That's how initial graph will look alike
	 */
	private void drawOutline() {
		drawHorizontalLines();
		verticalLinesAndLabels();
	}

	private void drawHorizontalLines() {
		drawHorizontalLine(GRAPH_MARGIN_SIZE); // top
		drawHorizontalLine(getHeight() - GRAPH_MARGIN_SIZE); // bottom
	}

	private void drawHorizontalLine(int y) {
		double x1 = 0;
		double y1 = y;
		double x2 = getWidth();
		double y2 = y1;
		add(new GLine(x1, y1, x2, y2));
	}

	/*
	 * This method draws vertical lines and also displays labels of year on
	 * canvas
	 */
	private void verticalLinesAndLabels() {
		double xOffest = getWidth() / NDECADES;
		for (int i = 0; i < NDECADES; i++) {
			String year = String.valueOf(START_DECADE + 10 * i);
			double x = xOffest * i; // x coordinate
			addYear(year, x + COORDINATE_SENSITIVITY);
			addVerticalLine(x);
		}
	}

	private void addYear(String year, double x) {
		GLabel label = new GLabel(year);
		add(label, x, getHeight() - COORDINATE_SENSITIVITY);
	}

	private void addVerticalLine(double x) {
		double x1 = x;
		double y1 = 0;
		double x2 = x1;
		double y2 = getHeight();
		add(new GLine(x1, y1, x2, y2));
	}

	/*
	 * First for loop iterates through names that are displayed on canvas
	 */
	private void drawEntries() {
		for (int j = 0; j < entries.size(); j++) {
			NameSurferEntry entry = entries.get(j);
			drawEntry(entry, j);
		}
	}

	/*
	 * This inner loop will iterate through decades to draw GLabels and simply
	 * make a graph with ranks
	 */
	private void drawEntry(NameSurferEntry entry, int j) {
		// Ratio for scaling rank to y coordinate
		double ratio = (getHeight() - 2 * GRAPH_MARGIN_SIZE) / (double) MAX_RANK;
		Color color = chooseColor(j);
		double decadeWidth = getWidth() / NDECADES;
		for (int i = 0; i < NDECADES; i++) {
			int startDecade = i;
			int endDecade = i + 1;
			int startRank = entry.getRank(startDecade);
			int endRank = entry.getRank(endDecade);
			double x1 = i * decadeWidth;
			double y1 = (startRank == 0) ? getHeight() - GRAPH_MARGIN_SIZE : GRAPH_MARGIN_SIZE + ratio * startRank;
			double x2 = (i + 1) * decadeWidth;
			double y2 = (endRank == 0) ? getHeight() - GRAPH_MARGIN_SIZE : GRAPH_MARGIN_SIZE + ratio * endRank;
			String name = (startRank == 0) ? entry.getName() + "*" : entry.getName() + " " + startRank;
			GLine line = new GLine(x1, y1, x2, y2);
			line.setColor(color);
			if (i != NDECADES - 1) { // we don't need last line to be drawn
				add(line);
			}
			GLabel label = new GLabel(name, x1 + COORDINATE_SENSITIVITY, y1);
			label.setColor(color);
			add(label);
		}
	}

	/*
	 * These four colors will be chosen one-by-one. Task said last color to be
	 * yellow, but it is hardly seen on canvas, so we make it orange
	 */
	private Color chooseColor(int j) {
		if (j % 4 == 0) {
			return Color.BLACK;
		} else if (j % 4 == 1) {
			return Color.RED;
		} else if (j % 4 == 2) {
			return Color.BLUE;
		} else { // when j%4==3
			return Color.ORANGE;
		}
	}

	/* Implementation of the ComponentListener interface */
	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		update();
	}

	public void componentShown(ComponentEvent e) {
	}
}
