package pt.uturista.prparser.writer;

import java.util.HashMap;

import pt.uturista.prspy.model.Map;
import pt.uturista.prspy.model.Vehicle;


public interface PRWriter {
	public void write(HashMap<String, Vehicle> vehicles, HashMap<String, Map> level);
}
