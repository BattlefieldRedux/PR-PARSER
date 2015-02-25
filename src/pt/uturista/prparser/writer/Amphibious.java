package pt.uturista.prparser.writer;

import java.util.HashMap;

import pt.uturista.log.Log;
import pt.uturista.prspy.model.Map;
import pt.uturista.prspy.model.Vehicle;

public class Amphibious implements PRWriter {
	@Override
	public void write(HashMap<String, Vehicle> vehicles,
			HashMap<String, Map> level) {
		for (Vehicle vehicle : vehicles.values()) {
			if (vehicle.isAmphibious())
				Log.p(vehicle.getName());
		}
	}

}
