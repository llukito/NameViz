
/*
 * File: NameSurferGraphExtension.java
 * ---------------------------
 * This class represents the canvas on which the graph of
 * names is drawn. This class is responsible for updating
 * (redrawing) the graphs whenever the list of entries changes or the window is resized.
 */

import acm.graphics.*;
import acm.io.IOConsole;
import acm.util.RandomGenerator;

import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.awt.*;

public class NameSurferGraphExtension extends GCanvas implements NameSurferConstants, ComponentListener {

	/* Instance variables */
	// this arrayList will store entries
	private List<NameSurferEntry> entries = new ArrayList<>();
	// this HashMap remembers all GLines used to make graphs for users
	private Map<String, ArrayList<GLine>> linesOfGraph = new HashMap<>();
	// this HashMap remembers all GLabels used to make graphs for users
	private Map<String, ArrayList<GLabel>> labelsOfGraph = new HashMap<>();
	// this arrayList remembers all names that are displayed on canvas
	private List<String> names = new ArrayList<>();
	// this is canvas of bar chart
	private GCanvas barChartCanvas = new GCanvas();
	// this is canvas for year chart
	private GCanvas yearChartCanvas = new GCanvas();
	// will use random for charts, so it can be pretty :)
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/**
	 * Creates a new NameSurferGraph object that displays the data.
	 */
	public NameSurferGraphExtension() {
		addComponentListener(this);
	}

	/*
	 * (about the random Color used in the method) if user enters same name,
	 * everything will still be drawn but with different color(I could do the
	 * same for this chart as I did to name graph, when you enter same name it
	 * tells you that name is already on canvas). But I won't do it here, cause
	 * randomColor could be smth user won't like. So user can enter the same
	 * name and color will change.
	 */
	public void drawYearChart(String year) {
		remove(barChartCanvas); // if it was displayed before yearChartCanvas
		yearChartCanvas.setBounds(0, 0, 2 * getWidth(), 2 * getHeight());
		add(yearChartCanvas);
		yearChartCanvas.removeAll(); // if smth was drawn before it
		drawOutline(yearChartCanvas);
		Color color = rgen.nextColor();
		displayYear(year, color);
		drawYearBar(year, color);
	}

	private void displayYear(String year, Color color) {
		GLabel label = new GLabel(year);
		Font font = new Font("Serif", Font.BOLD, 1400 / 45);
		label.setFont(font);
		label.setColor(color);
		yearChartCanvas.add(label, (getWidth() - label.getWidth()) / 2, getHeight() - COORDINATE_SENSITIVITY);
	}

	/*
	 * It looks like a drawChar method which is for names
	 */
	private void drawYearBar(String yaer, Color color) {
		NameSurferDataBase base = new NameSurferDataBase(NAMES_DATA_FILE);
		Map<String, Integer> map = base.mapOfYears.get(yaer);
		List<Integer> list = new ArrayList<>();
		for (String name : map.keySet()) {
			list.add(map.get(name));
		}
		Collections.sort(list);
		drawIt(map, list, color);
	}

	private void drawIt(Map<String, Integer> map, List<Integer> list, Color color) {
		int xCoordinate = 0;
		// listOfNames makes sure same name is not used more that once
		List<String> listOfNames = new ArrayList<>();
		// rankIndex will be the variable which will get ranks from sorted list
		// from lowest to higher, so chart can be symmetric
		int rankIndex = 0;
		for (int j = 0; j < 11; j++) { // 11 names should be displayed
			for (String name : map.keySet()) { // searches name with specific rank
				if (list.get(rankIndex) == map.get(name) && !listOfNames.contains(name)) {
					listOfNames.add(name);
					int rank = map.get(name);
					addRectangle(yearChartCanvas, rank, color, xCoordinate);
					addName(name, color, xCoordinate);
					addRankLabel(rank, color, xCoordinate);
					xCoordinate += getWidth() / NDECADES;
					break;
				}
			}
			rankIndex++; // move to next rank
		}
	}

	/*
	 * This is a column, which is made by the rank (This method is used for two
	 * charts)
	 */
	private void addRectangle(GCanvas canvas, int rank, Color color, int xCoordinate) {
		double ratio = (getHeight() - 2 * GRAPH_MARGIN_SIZE) / (double) MAX_RANK;
		double decadeWidth = -2 * COORDINATE_SENSITIVITY + getWidth() / NDECADES;
		double height = ratio * (MAX_RANK - rank);
		if (rank != 0) { // this is applicable for name Chart
			GRect rect = new GRect(decadeWidth, height);
			rect.setFilled(true);
			rect.setColor(color);
			canvas.add(rect, COORDINATE_SENSITIVITY + xCoordinate, getHeight() - GRAPH_MARGIN_SIZE - height);
		}
	}

	/*
	 * Name will be written on top of columns
	 */
	private void addName(String name, Color color, int xCoordinate) {
		GLabel label = new GLabel(name);
		label.setFont("Arial-Bold-15");
		label.setColor(color);
		int x = xCoordinate + 6 * COORDINATE_SENSITIVITY;
		// makes sure names fit in top rectangles
		if (name.length() > 9) {
			label.setFont("Arial-Bold-12");
			x = xCoordinate + 2 * COORDINATE_SENSITIVITY;
		} else if (name.length() > 7) {
			x = xCoordinate + 3 * COORDINATE_SENSITIVITY;
		}
		yearChartCanvas.add(label, x, -COORDINATE_SENSITIVITY + GRAPH_MARGIN_SIZE);
	}

	/*
	 * Adds rank on columns
	 */
	private void addRankLabel(int rank, Color color, int xCoordinate) {
		GLabel label = new GLabel("#" + rank);
		label.setFont("Arial-Bold-25");
		if (calculateLuminance(color) < 128) {
			label.setColor(Color.WHITE);
		} else {
			label.setColor(Color.BLACK);
		}
		yearChartCanvas.add(label, xCoordinate + 6 * COORDINATE_SENSITIVITY, getHeight() / 2);
	}

	/*
	 * If columns color is dark, color of rank will be white and vice versa
	 */
	private int calculateLuminance(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		// luminance formula
		return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
	}

	/*
	 * Users have buttons in north part of window. when they enter name, they
	 * can see the bar chart of ranks and years
	 */
	public void createChart(NameSurferEntry entry) {
		remove(yearChartCanvas); // if it was drawn before chartCanvas
		barChartCanvas.setBounds(0, 0, 2 * getWidth(), 2 * getHeight());
		add(barChartCanvas);
		barChartCanvas.removeAll();
		drawOutline(barChartCanvas);
		drawChart(entry);
	}

	/*
	 * It looks like a drawEntry method, but output is more beautiful :) P.S
	 * (about the random Color) Same thing happens to this chart as to year
	 * chart
	 */
	private void drawChart(NameSurferEntry entry) {
		Color color = rgen.nextColor();
		int xCoordinate = 0;
		for (int i = 0; i < NDECADES; i++) {
			int rank = entry.getRank(i);
			addRectangle(barChartCanvas, rank, color, xCoordinate);
			GLabel label = new GLabel("" + rank);
			label.setFont("Arial-Bold-24");
			label.setColor(color);
			barChartCanvas.add(label, xCoordinate + 6 * COORDINATE_SENSITIVITY,
					-COORDINATE_SENSITIVITY + GRAPH_MARGIN_SIZE);
			xCoordinate += getWidth() / NDECADES;
		}
	}

	public void removeCharts() {
		remove(barChartCanvas);
		remove(yearChartCanvas);
	}

	/**
	 * Clears the list of name surfer entries stored inside this class.
	 */
	public void clear() {
		// along with canvas, data structures should be cleared too
		entries.clear();
		names.clear();
		linesOfGraph.clear();
		labelsOfGraph.clear();
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
		drawOutline(this);
		drawEntries();
	}

	/*
	 * That's how initial graph will look alike
	 */
	private void drawOutline(GCanvas canvas) {
		drawHorizontalLines(canvas);
		verticalLinesAndLabels(canvas);
	}

	private void drawHorizontalLines(GCanvas canvas) {
		drawHorizontalLine(canvas, GRAPH_MARGIN_SIZE); // top
		drawHorizontalLine(canvas, getHeight() - GRAPH_MARGIN_SIZE); // bottom
	}

	private void drawHorizontalLine(GCanvas canvas, int y) {
		double x1 = 0;
		double y1 = y;
		double x2 = getWidth();
		double y2 = y1;
		canvas.add(new GLine(x1, y1, x2, y2));
	}

	/*
	 * This method draws vertical lines and also displays labels of year on
	 * canvas
	 */
	private void verticalLinesAndLabels(GCanvas canvas) {
		double xOffest = getWidth() / NDECADES;
		for (int i = 0; i < NDECADES; i++) {
			String year = String.valueOf(START_DECADE + 10 * i);
			double x = xOffest * i; // x coordinate
			if (canvas == this || canvas == barChartCanvas) {
				addYear(year, x + COORDINATE_SENSITIVITY, canvas);
			}
			addVerticalLine(x, canvas);
		}
	}

	private void addYear(String year, double x, GCanvas canvas) {
		GLabel label = new GLabel(year);
		// makes label change its size regarding canvas dimensions
		Font font = new Font("Dialog", Font.BOLD, (getHeight() + getWidth()) / 110);
		label.setFont(font);
		canvas.add(label, x, getHeight() - COORDINATE_SENSITIVITY);
	}

	private void addVerticalLine(double x, GCanvas canvas) {
		double x1 = x;
		double y1 = 0;
		double x2 = x1;
		double y2 = getHeight();
		canvas.add(new GLine(x1, y1, x2, y2));
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
			double x1 = startDecade * decadeWidth;
			double y1 = (startRank == 0) ? getHeight() - GRAPH_MARGIN_SIZE : GRAPH_MARGIN_SIZE + ratio * startRank;
			double x2 = (endDecade) * decadeWidth;
			double y2 = (endRank == 0) ? getHeight() - GRAPH_MARGIN_SIZE : GRAPH_MARGIN_SIZE + ratio * endRank;
			String name = (startRank == 0) ? entry.getName() + "*" : entry.getName() + " " + startRank;
			addLine(x1, y1, x2, y2, color, startDecade, entry);
			addNameLabel(name, color, x1, y1, startRank, endRank, entry);
		}
		fillListOfNames(entry);
	}

	/*
	 * These four colors will be chosen one-by-one. Task said last color to be
	 * yellow, but it is rarely seen on canvas, so we make it orange
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

	private void addLine(double x1, double y1, double x2, double y2, Color color, int startDecade, NameSurferEntry entry) {
		GLine line = new GLine(x1, y1, x2, y2);
		line.setColor(color);
		if (startDecade != NDECADES - 1) { // we don't need last line to be drawn
			add(line);
			fillMapOfGLines(entry, line);
		}
	}

	/*
	 * Stores all GLines associated with name
	 */
	private void fillMapOfGLines(NameSurferEntry entry, GLine line) {
		String user = entry.getName().toLowerCase();
		ArrayList<GLine> arrayOfGLines = linesOfGraph.getOrDefault(user, new ArrayList<GLine>());
		arrayOfGLines.add(line);
		linesOfGraph.put(user, arrayOfGLines);
	}

	/*
	 * Adds name label next to GLines
	 */
	private void addNameLabel(String name, Color color, double x1, double y1, int startRank, int endRank, NameSurferEntry entry) {
		GLabel label = new GLabel(name);
		label.setColor(color);
		// makes label change its size regarding canvas dimensions
		Font font = new Font("Dialog", Font.PLAIN, (getHeight() + getWidth()) / 110);
		label.setFont(font);
		add(label, x1 + COORDINATE_SENSITIVITY, yForLabel(startRank, endRank, y1, label.getHeight()));
		checkPlacementOfLabel(entry, label); // checks intersections with other labels
		fillMapOfGLabels(entry, label);
	}

	/*
	 * This method has two functions: 1)Computes y Coordinate for label so
	 * GLines won't cover text(name) 2)If window size changes, it still
	 * minimizes crossing with lines
	 */
	private double yForLabel(int startRank, int endRank, double y1, double fontSize) {
		int difference = endRank - startRank;
		double y = 0;
		double adjuster = fontSize / 6; // adjusts y coordinate when window size change
		if (difference > 10) {
			y = y1 - 2 * adjuster;
		} else if (difference < -10) {
			y = y1 + 4 * adjuster;
		} else {
			y = y1;
		}
		return y;
	}

	private void checkPlacementOfLabel(NameSurferEntry entry, GLabel label) {
		if (!names.contains(entry.getName().toLowerCase())) {
			placeLabel(label, labelsOfGraph);
		}
	}

	/*
	 * This method handles crossings of different labels as much as possible.
	 * But when there are many graphs and labels should be close to their
	 * graphs, it becomes impossible.
	 */
	private void placeLabel(GLabel label, Map<String, ArrayList<GLabel>> labelsOfGraph) {
		int dy = 2;
		while (hasCollision(label, labelsOfGraph)) {
			label.move(0, dy);
		}
	}

	private boolean hasCollision(GLabel label, Map<String, ArrayList<GLabel>> labelsOfGraph) {
		GRectangle labelBounds = label.getBounds(); // rectangle GLabel is drawn into
		for (String str : labelsOfGraph.keySet()) {
			for (GLabel labels : labelsOfGraph.get(str)) {
				if (labelBounds.intersects(labels.getBounds())) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Stores all GLabels associated with name
	 */
	private void fillMapOfGLabels(NameSurferEntry entry, GLabel label) {
		String user = entry.getName().toLowerCase();
		ArrayList<GLabel> arrayOfGLabels = labelsOfGraph.getOrDefault(user, new ArrayList<GLabel>());
		arrayOfGLabels.add(label);
		labelsOfGraph.put(user, arrayOfGLabels);
	}

	private void fillListOfNames(NameSurferEntry entry) {
		if (!names.contains(entry.getName().toLowerCase())) {
			names.add(entry.getName().toLowerCase());
		}
	}

	/*
	 * This method is public, so it can be accessed from other classes, such as
	 * NameSurferExtension class, which makes this method remove graph of
	 * specific person
	 */
	public void removeName(String name) {
		removeGLines(name);
		removeGLabels(name);
		removeFromEntriesList(name);
		names.remove(name); // remove from names list
	}

	/*
	 * Removes all GLines associated with that name
	 */
	private void removeGLines(String name) {
		ArrayList<GLine> removableGLines = linesOfGraph.get(name);
		for (GLine line : removableGLines) {
			remove(line);
		}
		linesOfGraph.remove(name);
	}

	/*
	 * Removes all GLabels associated with that name
	 */
	private void removeGLabels(String name) {
		ArrayList<GLabel> removableGLabels = labelsOfGraph.get(name);
		for (GLabel label : removableGLabels) {
			remove(label);
		}
		labelsOfGraph.remove(name);
	}

	/*
	 * Removes entry from entries list, so it won't appear on canvas, after
	 * update() method takes place
	 */
	private void removeFromEntriesList(String name) {
		for (NameSurferEntry entry : entries) {
			if (entry.getName().equalsIgnoreCase(name)) {
				entries.remove(entry);
				break;
			}
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
