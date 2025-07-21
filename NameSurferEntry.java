
/*
 * File: NameSurferEntry.java
 * --------------------------
 * This class represents a single entry in the database.  Each
 * NameSurferEntry contains a name and a list giving the popularity
 * of that name for each decade stretching back to 1900.
 */

import java.util.*;

public class NameSurferEntry implements NameSurferConstants {

	/* Instance variables of NameSurferEntry class */
	private String name;
	private int[] ranks;

	/* Constructor: NameSurferEntry(line) */
	/**
	 * Creates a new NameSurferEntry from a data line as it appears in the data
	 * file. Each line begins with the name, which is followed by integers
	 * giving the rank of that name for each decade.
	 */
	public NameSurferEntry(String line) {
		// this will return array of all values
		String[] values = line.split(" ");
		name = values[0]; // array starts with name
		fillRanks(values);
	}

	/*
	 * This method will fill ranks with rest of values, which are 11 numbers
	 */
	private void fillRanks(String[] values) {
		ranks = new int[NDECADES];
		for (int i = 1; i <= NDECADES; i++) {
			ranks[i - 1] = Integer.parseInt(values[i]);
		}
	}

	/* Method: getName() */
	/**
	 * Returns the name associated with this entry.
	 */
	public String getName() {
		return name;
	}

	/* Method: getRank(decade) */
	/**
	 * Returns the rank associated with an entry for a particular decade. The
	 * decade value is an integer indicating how many decades have passed since
	 * the first year in the database, which is given by the constant
	 * START_DECADE. If a name does not appear in a decade, the rank value is 0.
	 */
	public int getRank(int decade) {
		if (decade < 0 || decade >= NDECADES) {
			return 0;
		}
		return ranks[decade];
	}

	/* Method: toString() */
	/**
	 * Returns a string that makes it easy to see the value of a
	 * NameSurferEntry.
	 */
	public String toString() {
		return name + " " + Arrays.toString(ranks);
	}
}
