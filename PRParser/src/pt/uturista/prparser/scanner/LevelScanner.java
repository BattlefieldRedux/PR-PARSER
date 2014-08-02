package pt.uturista.prparser.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import pt.uturista.log.Log;
import pt.uturista.prparser.scanner.VehicleScanner.VehicleLibrary;
import pt.uturista.prspy.model.Layout;
import pt.uturista.prspy.model.Map;
import pt.uturista.zip.ZipExtrator;

public class LevelScanner {
	private static final String TAG = "LevelScanner";
	private VehicleLibrary vehicleLibrary;
	private HashSet<Map> levelLibrary;
	private String tempFolder;

	public LevelScanner(VehicleLibrary library, String tempFolder) {
		this.vehicleLibrary = library;
		this.levelLibrary = new HashSet<>();
		this.tempFolder = tempFolder;
	}

	public LevelLibrary buildLibrary(File levelsRoot) {

		Map.Builder builder = null;

		for (File levelFolder : levelsRoot.listFiles()) {// Level Folders
			builder = new Map.Builder();
			builder.setKey(levelFolder.getName());
			for (File file : levelFolder.listFiles()) {// Config Files
				if (file.getName().equalsIgnoreCase("INFO")) {
					for (File files : file.listFiles()) {
						if (files.getName().toLowerCase().endsWith(".desc")) {
							builder.setName(parseLevelName(files));
						}
					}

				} else if (file.getName().equals("server.zip")) {

					if (ZipExtrator.extract(file).to(tempFolder)) {


						readLevelLayouts(builder);
						addToLibrary(builder.build());
					}
				}
			}
		}

		LevelLibrary finalLibrary = new LevelLibrary(levelLibrary);
		return finalLibrary;
	}

	private boolean addToLibrary(Map level) {
		return this.levelLibrary.add(level);
	}

	private String parseLevelName(File files) {

		String factionClean = null;
		try (BufferedReader bf = new BufferedReader(new FileReader(files));) {

			String line;

			while ((line = bf.readLine()) != null) {

				if (line.endsWith("</name>")) {
					factionClean = line.replace("<name>", "");
					factionClean = factionClean.replace("</name>", "");
					factionClean = factionClean.trim();
					System.out.println("parseMapName " + factionClean);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return factionClean;
	}

	private void readLevelLayouts(Map.Builder builder) {
		File leverServerRoot = new File(tempFolder);
		LayoutScanner layoutscanner = LayoutScanner.getInstance(vehicleLibrary);

		for (File file : leverServerRoot.listFiles()) {
			if (file.getName().equalsIgnoreCase("gamemodes")) {
				for (File gamemodes : file.listFiles()) {
					for (File gameSizes : gamemodes.listFiles()) {
						for (File configFiles : gameSizes.listFiles()) {
							if (configFiles.getName().equalsIgnoreCase(
									"gameplayobjects.con")) {
								Layout layout = layoutscanner.scan(configFiles);

								if (layout != null)
									builder.addLayout(layout);
							}
						}
					}
				}
			} else if (file.getName().equals("heightdata.con")) {
				builder.setSize(readHeightData(file));
			}
		}
	}

	private int readHeightData(File file) {
		try (BufferedReader bf = new BufferedReader(new FileReader(file));) {

			String line;

			while ((line = bf.readLine()) != null) {
				if (line.startsWith("heightmapcluster.setHeightmapSize")) {
					bf.close();
					String[] subline = line.split(" ");
					return Integer.parseInt(subline[1].substring(0, 1));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// =========================== LIBRARY ===============================

	public static class LevelLibrary implements Serializable {

		final private HashSet<Map> library;

		private LevelLibrary(HashSet<Map> library) {
			this.library = library;
		}

		public ArrayList<Map> toArray() {

			ArrayList<Map> arrayLibrary = new ArrayList<Map>(library);

			Collections.sort(arrayLibrary, new Comparator<Map>() {

				@Override
				public int compare(Map o1, Map o2) {
					return o1.getName().compareTo(o2.getName());
				}

			});

			return arrayLibrary;
		}

	}

}
