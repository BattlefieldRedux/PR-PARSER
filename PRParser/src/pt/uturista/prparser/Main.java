package pt.uturista.prparser;

import java.io.File;
import java.util.ArrayList;

import pt.uturista.log.Log;
import pt.uturista.prparser.scanner.LevelScanner;
import pt.uturista.prparser.scanner.LevelScanner.LevelLibrary;
import pt.uturista.prparser.scanner.VehicleScanner;
import pt.uturista.prparser.scanner.VehicleScanner.VehicleLibrary;
import pt.uturista.prspy.model.Map;
import pt.uturista.zip.ZipExtrator;

import com.google.gson.Gson;

public class Main {

	public static void main(String[] args) {
		Log.info(false);
		Log.debug(false);
		Log.error(true);

		final String levelsPath = "C:\\Program Files (x86)\\Origin Games\\Battlefield 2 Complete Collection\\mods\\pr\\levels";
		final String vehiclesPath = "C:\\Program Files (x86)\\Origin Games\\Battlefield 2 Complete Collection\\mods\\pr\\content\\objects_vehicles_server.zip";
		final String tempFolderPath = "C:\\Users\\Vascko\\Desktop\\PRtemp";

		final File vehiclesRootFile = new File(vehiclesPath);
		if (!ZipExtrator.extract(vehiclesRootFile).to(tempFolderPath))
			return;

		final File unzippedRoot = new File(tempFolderPath);
		VehicleLibrary vehicleLibrary = VehicleScanner
				.buildLibrary(unzippedRoot);

		System.out
				.println("\n\n=========================\n=========================\n\n");
		final File levelsRoot = new File(levelsPath);
		LevelScanner levelScanner = new LevelScanner(vehicleLibrary,
				tempFolderPath);
		LevelLibrary levelLibrary = levelScanner.buildLibrary(levelsRoot);

		Gson gson = new Gson();

		ArrayList<Map> output = levelLibrary.toArray();
		String json = gson.toJson(output);

		final String finalJson = json.replace("?", " ");
		Log.p(finalJson);
	}

}
