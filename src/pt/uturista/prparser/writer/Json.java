package pt.uturista.prparser.writer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import pt.uturista.log.Log;
import pt.uturista.prspy.model.Map;
import pt.uturista.prspy.model.Vehicle;

import com.google.gson.Gson;

public class Json implements PRWriter {

	@Override
	public void write(HashMap<String, Vehicle> vehicles,
			HashMap<String, Map> level) {
		Gson gson = new Gson();

		ArrayList<Map> output = new ArrayList<Map>(level.values());
		output.sort(new Comparator<Map>() {

			@Override
			public int compare(Map arg0, Map arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}

		});
		String json = gson.toJson(output);

		final String finalJson = json.replace("?", " ");
		Log.p(finalJson);
	}

}
