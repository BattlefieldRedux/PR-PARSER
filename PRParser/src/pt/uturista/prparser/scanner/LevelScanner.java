package pt.uturista.prparser.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import com.google.gson.Gson;

import pt.uturista.log.Log;
import pt.uturista.prparser.model.Layout;
import pt.uturista.prparser.model.Level;
import pt.uturista.prparser.scanner.VehicleScanner.VehicleLibrary;
import pt.uturista.utils.ZipExtrator;

public class LevelScanner {
	private VehicleLibrary vehicleLibrary;
	private HashSet<Level> levelLibrary;
	private String tempFolder;

	public LevelScanner(VehicleLibrary library, String tempFolder) {
		this.vehicleLibrary = library;
		this.levelLibrary = new HashSet<>();
		this.tempFolder = tempFolder;
	}

	public LevelLibrary buildLibrary(File levelsRoot) {

		Level.Builder builder =null;

		for (File levelFolder : levelsRoot.listFiles()) {// Level Folders
			builder =  new Level.Builder(); 
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

	private boolean addToLibrary(Level level) {
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

	private void readLevelLayouts(Level.Builder builder) {
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
			}
		}
	}

	// =========================== LIBRARY ===============================

	public static class LevelLibrary implements Serializable{
	
		final private HashSet<Level> library;

		private LevelLibrary(HashSet<Level> library) {
			this.library = library;
		}

	}

}
