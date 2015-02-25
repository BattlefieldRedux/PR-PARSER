package pt.uturista.prparser;

import java.io.File;
import java.util.HashMap;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import pt.uturista.log.Log;
import pt.uturista.prparser.scanner.LevelScanner;
import pt.uturista.prparser.scanner.VehicleScanner;
import pt.uturista.prspy.model.Map;
import pt.uturista.prspy.model.Vehicle;

public class PrParser {
	static final String EXTRACT_PATH = "C:\\Users\\Vasco\\Desktop\\PRtemp";
	static final String MOD_PATH = "C:\\Program Files (x86)\\Origin Games\\Battlefield 2 Complete Collection\\mods\\pr";
	static final String VEHICLE_PATH = "\\content\\objects_vehicles_server.zip";
	static final String LEVEL_PATH = "\\levels";
	static final String TAG = "PrParser";

	public static void main(String[] args) {
		Log.info(false);
		Log.debug(false);
		Log.error(false);

		final File vehiclesRootFile = new File(MOD_PATH + VEHICLE_PATH);

		Log.p("==================================================");
		Log.p("===============  Extracting files  ===============");
		Log.p("==================================================");
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(vehiclesRootFile);

			// Extracts all files to the path specified
			zipFile.extractAll(EXTRACT_PATH);

			Log.p("==================================================");
			Log.p("==========  Building vehicle library  ============");
			Log.p("==================================================");

			final File unzippedRoot = new File(EXTRACT_PATH);
			VehicleScanner vehicleScanner = new VehicleScanner();
			HashMap<String, Vehicle> vehicleLibrary = vehicleScanner
					.buildLibrary(unzippedRoot);

			Log.p("==================================================");
			Log.p("===========  Building level library  =============");
			Log.p("==================================================");

			final File levelsRoot = new File(MOD_PATH + LEVEL_PATH);
			LevelScanner levelScanner = new LevelScanner(vehicleLibrary,
					EXTRACT_PATH);
			HashMap<String, Map> levelLibrary = levelScanner
					.buildLibrary(levelsRoot);

			Log.p("==================================================");
			Log.p("=============  Writing information  ==============");
			Log.p("==================================================");

			pt.uturista.prparser.writer.PRWriter json = new pt.uturista.prparser.writer.Json();

			json.write(vehicleLibrary, levelLibrary);
		} catch (ZipException e) {
			Log.e(TAG, e.toString());
		}
	}

}
