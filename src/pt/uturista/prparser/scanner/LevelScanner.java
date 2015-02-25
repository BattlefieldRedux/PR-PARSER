package pt.uturista.prparser.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import pt.uturista.log.Log;
import pt.uturista.prspy.model.Asset;
import pt.uturista.prspy.model.Layout;
import pt.uturista.prspy.model.Map;
import pt.uturista.prspy.model.Vehicle;

public class LevelScanner {
	private static final String TAG = "LevelScanner";
	private HashMap<String, Vehicle> vehicleLibrary;
	private HashMap<String, Map> levelLibrary;
	private String tempFolder;

	public LevelScanner(HashMap<String, Vehicle> library, String tempFolder) {
		this.vehicleLibrary = library;
		this.levelLibrary = new HashMap<>();
		this.tempFolder = tempFolder;
	}

	public HashMap<String, Map> buildLibrary(File levelsRoot) {

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

					try {
						// Initiate ZipFile object with the path/name of the zip
						// file.
						ZipFile zipFile = new ZipFile(file);

						// Extracts all files to the path specified
						zipFile.extractAll(tempFolder);
						readLevelLayouts(builder);
						Map map = builder.build();
						levelLibrary.put(map.getKey(), map);
					} catch (ZipException e) {
						Log.e(TAG, e.toString());
					}
				}
			}
		}

		return levelLibrary;
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

	static class LayoutScanner {
		private static final String TAG = "LayoutScanner";
		final private HashMap<String, Vehicle> vehicleLibrary;

		private LayoutScanner(HashMap<String, Vehicle> vehicleLibrary) {
			this.vehicleLibrary = vehicleLibrary;
		}

		public static LayoutScanner getInstance(
				HashMap<String, Vehicle> vehicleLibrary) {
			return new LayoutScanner(vehicleLibrary);
		}

		public Layout scan(File file) {

			final File gameSize = file.getParentFile();
			final File gameMode = gameSize.getParentFile();

			Layout.Builder layoutBuilder = new Layout.Builder();
			layoutBuilder.setGameMode(gameMode.getName());
			layoutBuilder.setGameSize(gameSize.getName());

			Log.d(TAG, "scan(" + gameMode.getName() + "/" + gameSize.getName()
					+ ")");

			if (gameMode.getName().equals("gpm_coop")) {
				Log.d(TAG, "GameMode is Coop");
				return null;
			}
			boolean implicitFactions = true;
			try (BufferedReader bf = new BufferedReader(new FileReader(file));) {

				String line;
				Asset.Builder assetBuilder = null;
				String[] token = null;

				while ((line = bf.readLine()) != null) {
					Log.i(TAG, line);

					if (line.startsWith("ObjectTemplate.create ObjectSpawner")
							|| line.isEmpty()) {

						if (assetBuilder != null) {
							final Asset asset = assetBuilder.build();
							if (asset != null) {
								Log.d(TAG, "layoutBuilder.addAsset("
										+ asset.getVehicle().getName() + ","
										+ asset.getTeam() + ")");
								layoutBuilder.addAsset(asset, asset.getTeam());
							} else {
								Log.e(TAG, "Asset not valid");
							}
						}
						assetBuilder = new Asset.Builder();

						/*
						 * ObjectTemplate.team <int>
						 * 
						 * This is an alternative way to define which team the
						 * spawner belongs to w/o need of flags. The team
						 * numbers are same as above. You can also use
						 */
					} else if (line.startsWith("ObjectTemplate.team ")) {
						token = line.split(" ");
						// assetBuilder.setTeam(Integer.parseInt(token[1]));

					} else if (line.startsWith("ObjectTemplate.minSpawnDelay")) {

						token = line.split(" ");
						int delay = Integer.parseInt(token[1]);
						Log.d(TAG, "minDelay: " + delay);
						assetBuilder.setMinDelay(delay);

					} else if (line.startsWith("ObjectTemplate.maxSpawnDelay")) {
						token = line.split(" ");
						int delay = Integer.parseInt(token[1]);
						Log.d(TAG, "maxDelay: " + delay);
						assetBuilder.setMaxDelay(delay);

					} else if (line
							.startsWith("ObjectTemplate.spawnDelayAtStart")) {
						token = line.split(" ");

						Log.d(TAG, "startDelay: " + token[1]);
						if (token[1].equals("1"))
							assetBuilder.setStartDelay(true);
						/*
						 * ObjectTemplate.setObjectTemplate <int> <string>
						 * 
						 * This defines what should spawn when the flag that
						 * this spawner belongs to it capped by a certain team.
						 * The integers are 0, 1 and 2. 0 is neutral, 1 opfor
						 * and 2 blufor. <string> is the template name of the
						 * vehicle to spawn
						 */
					} else if (line
							.startsWith("ObjectTemplate.setObjectTemplate")) {
						parseAssetName(line, assetBuilder);

					} else if (line.startsWith("rem")) {
						continue;
					} else if (line.startsWith("run")) {// eg: run
						Log.d(TAG, line); // ../../../Init_canada.con
						token = line.split(" ");
						File initFile = getInitFile(token[1], file);
						implicitFactions = !parseFactions(initFile,
								layoutBuilder);

					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (implicitFactions) {
				File initFile = getInitFile("../../../init.con", file);
				parseFactions(initFile, layoutBuilder);
			}

			return layoutBuilder.build();
		}

		private File getInitFile(String path, File file) {
			Log.d(TAG, "getInitFile(" + path + ")");

			String[] tokens = path.split("/");
			File rootFile = file.getParentFile();
			String initPath = null;
			for (String token : tokens) {
				if (token.equals("..")) {
					rootFile = rootFile.getParentFile();
				} else if (token.endsWith(".con")) {
					initPath = token;
				}
			}

			if (rootFile == null) {
				Log.e(TAG, "getInitFile.rootFile is NULL");
				return null;
			}
			if (initPath == null) {
				Log.e(TAG, "getInitFile.initPath is NULL");
				return null;
			}

			File initFile = null;
			for (File configFiles : rootFile.listFiles()) {
				if (configFiles.getName().equalsIgnoreCase(initPath)) {
					initFile = configFiles;
					break;
				}
			}
			if (initFile == null) {
				Log.e(TAG, "getInitFile.initFile is NULL");
			}

			return initFile;
		}

		private boolean parseFactions(File file, Layout.Builder builder) {

			if (file == null) {
				return false;
			}
			Log.d(TAG, "parseFactions(" + file.getName() + ")");

			try (BufferedReader bf = new BufferedReader(new FileReader(file));) {

				String line;

				while ((line = bf.readLine()) != null) {
					if (line.startsWith("run ../../Factions/faction_init.con")) {

						String[] subline = line.split(" ");
						String[] factionClean = subline[3].replace("\"", "")
								.split("_");

						builder.setOpFaction(factionClean[0]);
						Log.d(TAG, "OPFACTION: " + factionClean[0]);
						line = bf.readLine();
						subline = line.split(" ");
						factionClean = subline[3].replace("\"", "").split("_");

						builder.setBluFaction(factionClean[0]);
						Log.d(TAG, "BLUFACTION: " + factionClean[0]);
						return true;
					}
				}
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
			return false;
		}

		private void parseAssetName(String line, Asset.Builder assetBuilder) {
			Log.d(TAG, "parseAssetName()");
			String[] token = line.split(" ");

			String vehicleName = "";

			if (token.length > 2)
				vehicleName = token[2];

			assetBuilder.setTeam(Integer.parseInt(token[1]), false);
			Log.d(TAG, "[" + vehicleName + "] TEAM: " + token[1]);

			if (vehicleName.startsWith("artillery")
					|| vehicleName.startsWith("mortar")) {
				assetBuilder.setVehicle(Vehicle.ARTILLERY);
			} else {

				final Vehicle vehicle = getVehicle(vehicleName);

				if (vehicle != null) {
					assetBuilder.setVehicle(vehicle);
				}

			}

		}

		private Vehicle getVehicle(String key) {
			return this.vehicleLibrary.get(key);
		}

	}

}
