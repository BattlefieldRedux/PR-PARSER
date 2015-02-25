package pt.uturista.prparser.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import pt.uturista.log.Log;
import pt.uturista.prspy.model.Vehicle;

public class VehicleScanner {
	public final static String TAG = "VehicleScanner";

	public Vehicle scan(File file) {
		return scan(file, "UTF-8");
	}

	public HashMap<String, Vehicle> buildLibrary(File vehicleroot) {
		HashMap<String, Vehicle> vlibrary = new HashMap<>();
		for (File vehicleRoot : vehicleroot.listFiles()) {
			for (File vehicleTypes : vehicleRoot.listFiles()) {
				for (File vehicleFolder : vehicleTypes.listFiles()) {
					for (File vehicleConfig : vehicleFolder.listFiles()) {
						if (vehicleConfig.getName().endsWith("tweak")) {
							Vehicle vehicle = this.scan(vehicleConfig);

							if (vehicle != null) {
								Log.d(TAG, " library.put(" + vehicle.getKey()
										+ ", " + vehicle.getName());
								vlibrary.put(vehicle.getKey(), vehicle);
							} else {
								Log.d(TAG, "vehicle not valid");
							}

						}
					}
				}
			}
		}

		return vlibrary;

	}

	private static Vehicle scan(File file, String encoding) {
		Vehicle.Builder builder = new Vehicle.Builder();
		try (Reader reader = new InputStreamReader(new FileInputStream(file),
				encoding); BufferedReader bf = new BufferedReader(reader);) {

			String line;

			String key = FilenameUtils.getBaseName(file.getName());
			builder.setKey(key);

			while ((line = bf.readLine()) != null) {

				if (line.startsWith("rem"))
					continue;

				if (line.startsWith("ObjectTemplate.weaponHud.hudName ")) {
					String weaponName = line.replace(
							"ObjectTemplate.weaponHud.hudName ", "");
					weaponName = weaponName.replace("\"", "");

					Log.i(TAG, "Scaned weapon: " + weaponName);
				} else if (line
						.startsWith("ObjectTemplate.vehicleHud.hudName ")) {
					// Remove the initial caracters
					String hudName = line.replace(
							"ObjectTemplate.vehicleHud.hudName ", "");

					// Remove additional caracters
					hudName = hudName.replace("\"", "");

					// Set Name
					builder.setName(hudName);

					// It appears that this is used for every seat
					builder.addSeat();
				} else if (line
						.startsWith("ObjectTemplate.vehicleHud.miniMapIcon ")) {

					String[] token;
					token = line.split(" ");
					token[1] = token[1].toLowerCase();
					builder.setIcon(FilenameUtils.getBaseName(token[1]));
				} else if (line.startsWith("ObjectTemplate.setEngineType ")) {

					String[] token;
					token = line.split(" ");
					builder.addEngineType(token[1]);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return builder.build();
	}
}
