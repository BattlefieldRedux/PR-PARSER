package pt.uturista.prparser.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;



import pt.uturista.log.Log;
import pt.uturista.prspy.model.Vehicle;

public class VehicleScanner {
	public final static String TAG = "[VehicleScanner]";
	public final static String DEFAULT_ENCODING = "UTF-8";
	private static String encoding = DEFAULT_ENCODING;

	public static void setEnconding(String encoding) {
		if (encoding != null)
			VehicleScanner.encoding = encoding;
	}

	public static VehicleLibrary buildLibrary(File unzippedVehicleRoot) {
		HashMap<String, Vehicle> library = new HashMap<>();

		for (File vehicleRoot : unzippedVehicleRoot.listFiles()) {
			for (File vehicleTypes : vehicleRoot.listFiles()) {
				for (File vehicleFolder : vehicleTypes.listFiles()) {
					for (File vehicleConfig : vehicleFolder.listFiles()) {
						if (vehicleConfig.getName().endsWith("tweak")) {
							Vehicle vehicle = scan(vehicleConfig);

							if (vehicle != null) {
								System.out.println(TAG + " library.put("
										+ vehicle.getKey() + ", "
										+ vehicle.getName());
								library.put(vehicle.getKey(), vehicle);
							}

						}
					}
				}
			}
		}
		VehicleLibrary vlibrary = new VehicleLibrary(library);
		return vlibrary;
	}

	public static Vehicle scan(File file) {
		Vehicle.Builder builder = new Vehicle.Builder();
		try (Reader reader = new InputStreamReader(new FileInputStream(file),
				encoding); BufferedReader bf = new BufferedReader(reader);) {

			String line;

			String key = file.getName().replace(".tweak", "");
			builder.setKey(key);

			while ((line = bf.readLine()) != null) {

				if (line.startsWith("rem"))
					continue;

				if (line.startsWith("ObjectTemplate.vehicleHud.hudName ")) {
					// Remove the initial caracters
					String hudName = line.replace(
							"ObjectTemplate.vehicleHud.hudName ", "");

					// Remove additional caracters
					hudName = hudName.replace("\"", "");
					hudName = hudName.replace("?", "");

					// Set Name
					builder.setName(hudName);

					// It appears that this is used for every seat
					builder.addSeat();
				} else if (line
						.startsWith("ObjectTemplate.vehicleHud.miniMapIcon ")) {

					String[] token;
					token = line.split(" ");
					token[1] = token[1].toLowerCase();
					int icon = 0;
					if (token[1].contains("mini_tank_heavy")) {
						icon = pt.uturista.prspy.R.drawable.mini_tank_heavy;
					} else if (token[1].contains("mini_adv_heavy")) {
						icon = pt.uturista.prspy.R.drawable.mini_adv_heavy;
					} else if (token[1].contains("mini_adv_light")) {
						icon = pt.uturista.prspy.R.drawable.mini_adv_light;
					} else if (token[1].contains("mini_adv_medium")) {
						icon = pt.uturista.prspy.R.drawable.mini_adv_medium;
					} else if (token[1].contains("mini_apc_aavp7")) {
						icon = pt.uturista.prspy.R.drawable.mini_apc_aavp7;
					} else if (token[1].contains("mini_apc_heavy")) {
						icon = pt.uturista.prspy.R.drawable.mini_apc_heavy;
					} else if (token[1].contains("mini_apc_light")) {
						icon = pt.uturista.prspy.R.drawable.mini_apc_light;
					} else if (token[1].contains("mini_apc_logistics")) {
						icon = pt.uturista.prspy.R.drawable.mini_apc_logistics;
					} else if (token[1].contains("mini_apc_medium")) {
						icon = pt.uturista.prspy.R.drawable.mini_apc_medium;
					} else if (token[1].contains("mini_atgmvehicle")) {
						icon = pt.uturista.prspy.R.drawable.mini_atgmvehicle;
					} else if (token[1].contains("mini_atv")) {
						icon = pt.uturista.prspy.R.drawable.mini_atv;
					} else if (token[1].contains("mini_boat")) {
						icon = pt.uturista.prspy.R.drawable.mini_boat;
					} else if (token[1].contains("mini_boat_heavy")) {
						icon = pt.uturista.prspy.R.drawable.mini_boat_heavy;
					} else if (token[1].contains("mini_boat_medium")) {
						icon = pt.uturista.prspy.R.drawable.mini_boat_medium;
					} else if (token[1].contains("mini_bombcar")) {
						icon = pt.uturista.prspy.R.drawable.mini_bombcar;
					} else if (token[1].contains("mini_bombtruck")) {
						icon = pt.uturista.prspy.R.drawable.mini_bombtruck;
					} else if (token[1].contains("mini_chinook")) {
						icon = pt.uturista.prspy.R.drawable.mini_chinook;
					} else if (token[1].contains("mini_civicar")) {
						icon = pt.uturista.prspy.R.drawable.mini_civicar;
					} else if (token[1].contains("mini_dirtbike")) {
						icon = pt.uturista.prspy.R.drawable.mini_dirtbike;
					} else if (token[1].contains("mini_forklift")) {
						icon = pt.uturista.prspy.R.drawable.mini_forklift;
					} else if (token[1].contains("mini_harrier")) {
						icon = pt.uturista.prspy.R.drawable.mini_harrier;
					} else if (token[1].contains("mini_heavyhelo")) {
						icon = pt.uturista.prspy.R.drawable.mini_heavyhelo;
					} else if (token[1].contains("mini_jeep")) {
						icon = pt.uturista.prspy.R.drawable.mini_jeep;
					} else if (token[1].contains("mini_jeep2")) {
						icon = pt.uturista.prspy.R.drawable.mini_jeep2;
					} else if (token[1].contains("mini_jeep3")) {
						icon = pt.uturista.prspy.R.drawable.mini_jeep3;
					} else if (token[1].contains("mini_jet_figherbomber")) {
						icon = pt.uturista.prspy.R.drawable.mini_jet_figherbomber;
					} else if (token[1].contains("mini_jetattack")) {
						icon = pt.uturista.prspy.R.drawable.mini_jetattack;
					} else if (token[1].contains("mini_lightattackhelo")) {
						icon = pt.uturista.prspy.R.drawable.mini_lightattackhelo;
					} else if (token[1].contains("mini_lighthelo")) {
						icon = pt.uturista.prspy.R.drawable.mini_lighthelo;
					} else if (token[1].contains("mini_logisticstruck")) {
						icon = pt.uturista.prspy.R.drawable.mini_logisticstruck;
					} else if (token[1].contains("mini_medattackhelo")) {
						icon = pt.uturista.prspy.R.drawable.mini_medattackhelo;
					} else if (token[1].contains("mini_mv22")) {
						icon = pt.uturista.prspy.R.drawable.mini_mv22;
					} else if (token[1].contains("mini_plane")) {
						icon = pt.uturista.prspy.R.drawable.mini_plane;
					} else if (token[1].contains("mini_rib")) {
						icon = pt.uturista.prspy.R.drawable.mini_rib;
					} else if (token[1].contains("mini_sampan")) {
						icon = pt.uturista.prspy.R.drawable.mini_sampan;
					} else if (token[1].contains("mini_scimitar")) {
						icon = pt.uturista.prspy.R.drawable.mini_scimitar;
					} else if (token[1].contains("mini_scout_light")) {
						icon = pt.uturista.prspy.R.drawable.mini_scout_light;
					} else if (token[1].contains("mini_scouthelo")) {
						icon = pt.uturista.prspy.R.drawable.mini_scouthelo;
					} else if (token[1].contains("mini_semitruck")) {
						icon = pt.uturista.prspy.R.drawable.mini_semitruck;
					} else if (token[1].contains("mini_supporttruck")) {
						icon = pt.uturista.prspy.R.drawable.mini_supporttruck;
					} else if (token[1].contains("mini_tank_medium")) {
						icon = pt.uturista.prspy.R.drawable.mini_tank_medium;
					} else if (token[1].contains("mini_towjeep")) {
						icon = pt.uturista.prspy.R.drawable.mini_towjeep;
					} else if (token[1].contains("mini_transport_heli")) {
						icon = pt.uturista.prspy.R.drawable.mini_transport_heli;
					} else if (token[1].contains("mini_jet")) {
						icon = pt.uturista.prspy.R.drawable.mini_jet;
					} else if (token[1].contains("mini_attack_heli")) {
						icon = pt.uturista.prspy.R.drawable.mini_attack_heli;

					} else {
						System.err.println("[" + file.getName()
								+ "] ICON NOT FOUND:" + token[1]);
					}

					builder.setIcon(icon);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return builder.build();
	}

	// =============== LIBRARY WRAPPER =============

	public static class VehicleLibrary implements Serializable {
		private static final long serialVersionUID = 1L;
		final private HashMap<String, Vehicle> library;

		private VehicleLibrary(HashMap<String, Vehicle> library) {
			this.library = library;
		}

		public HashMap<String, Vehicle> getSet() {
			HashMap<String, Vehicle> newSet = new HashMap<>(library);
			return newSet;
		}

		public boolean containsKey(String arg0) {
			return library.containsKey(arg0);
		}

		public boolean containsValue(String value) {
			return library.containsValue(value);
		}

		public Vehicle get(String key) {
			return library.get(key);
		}
	}
}
