
/*
 * File: NameSurferDataBase.java
 * -----------------------------
 * This class keeps track of the complete database of names.
 * The constructor reads in the database from a file, and
 * the only public method makes it possible to look up a
 * name and get back the corresponding NameSurferEntry.
 * Names are matched independent of case, so that "Eric"
 * and "ERIC" are the same names.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NameSurferDataBase implements NameSurferConstants {

	/* Instance variables */
	// this map will store rank data as values with keys of names
	private Map<String, String> mapOfData;
	// this map will store names and ranks (as values) during all years(keys)
	public Map<String, HashMap<String, Integer>> mapOfYears;

	/* Constructor: NameSurferDataBase(filename) */
	/**
	 * Creates a new NameSurferDataBase and initializes it using the data in the
	 * specified file. The constructor throws an error exception if the
	 * requested file does not exist or if an error occurs as the file is being
	 * read.
	 */
	public NameSurferDataBase(String filename) {
		// initialize HashMaps
		mapOfData = new HashMap<>();
		mapOfYears = new HashMap<>();
		// we initialize buf here, so we can close it in "finally"
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(filename));
			while (true) {
				String line = buf.readLine();
				if (line == null) {
					break;
				}
				storeData(line); // for default linear chart
				storeYearData(line); // for year bar chart(extension)
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * This method separates name from line and then stores it in HashMap
	 */
	private void storeData(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		String name = tokenizer.nextToken().toLowerCase();
		mapOfData.put(name, line);
	}

	/* Method: findEntry(name) */
	/**
	 * Returns the NameSurferEntry associated with this name, if one exists. If
	 * the name does not appear in the database, this method returns null.
	 */
	public NameSurferEntry findEntry(String name) {
		if (mapOfData.keySet().contains(name.toLowerCase())) {
			return new NameSurferEntry(mapOfData.get(name.toLowerCase()));
		} else {
			return null;
		}
	}

	/*
	 * This method receives a line for a person. Then it stores ranks for 11
	 * different years
	 */
	private void storeYearData(String line) {
		for (int j = 0; j < NDECADES; j++) {
			int rank = 0; // initialize it
			StringTokenizer tokenizer = new StringTokenizer(line);
			String name = tokenizer.nextToken().toLowerCase();
			rank = rankOfYear(rank, j, tokenizer);
			if (rank == 0) { // people with 0 ranks, won't be at top
				continue;
			}
			String year = START_DECADE + 10 * j + "";
			HashMap<String, Integer> map = mapOfYears.getOrDefault(year, new HashMap<>());
			map.put(name, rank);
			mapOfYears.put(year, map);
		}
		getTopElevenNames();
	}

	/*
	 * This method return ranks associated with year. For example, if year is
	 * 1920, we have to get fourth token of line(first was name, then 1900, 1910
	 * and then 1920). Name is already tokenized, so we make 2 nextToken which
	 * gets us desired rank
	 */
	private int rankOfYear(int rank, int j, StringTokenizer tokenizer) {
		for (int i = 0; i <= j; i++) {
			rank = Integer.parseInt(tokenizer.nextToken());
		}
		return rank;
	}

	/*
	 * Once every name with rank is stored, then we need to go through data and
	 * leave only 11 most popular names during different years
	 */
	private void getTopElevenNames() {
		for (String year : mapOfYears.keySet()) {
			mapOfYears.put(year, getTopElevenNames(year));
		}
	}

	/*
	 * This method goes through HashMap of names and ranks and returns new
	 * HashMap with minimum ranks(because low rank means high popularity (except
	 * 0))
	 */
	private HashMap<String, Integer> getTopElevenNames(String year) {
		HashMap<String, Integer> map = mapOfYears.get(year); // get that big HashMap
		// convert the map to a list of map.Entry which holds keys and values as pairs
		List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
		// Sort the list by values(ranks) in ascending order
		entryList.sort(Map.Entry.comparingByValue());
		// Create a new HashMap to store the 11 lowest entries
		HashMap<String, Integer> topElevenMap = new HashMap<>();
		for (int i = 0; i < 11 && i < entryList.size(); i++) {
			Map.Entry<String, Integer> entry = entryList.get(i);
			topElevenMap.put(entry.getKey(), entry.getValue());
		}
		return topElevenMap;
	}

}
