package pt.uturista.prparser.model;

import java.io.Serializable;

import pt.uturista.log.Log;

public class Asset implements Serializable {
	final private int minDelay;
	final private int maxDelay;
	final private boolean startDelay;
	final private int team;
	final private Vehicle vehicle;
	private int quantity = 1;

	public void addAssetCount() {
		this.quantity++;
	}

	private Asset(Builder builder) {
		this.minDelay = builder.minDelay;
		this.maxDelay = builder.maxDelay;
		this.startDelay = builder.startDelay;
		this.team = builder.team;
		this.vehicle = builder.vehicle;
	}

	public int getTeam() {
		return this.team;
	}

	public int getMinDelay() {
		return minDelay;
	}

	public int getMaxDelay() {
		return maxDelay;
	}

	public boolean hasStartDelay() {
		return startDelay;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public static class Builder {
		private final static String TAG = "Asset.Builder";
		int minDelay;
		int maxDelay;
		boolean startDelay;
		int team = 0;
		Vehicle vehicle = null;

		public Asset build() {
			if (vehicle == null) {
				// Log.e(TAG, "Vehicle IS NOT SET");
				return null;
			}

			if (team == 0) {
				Log.e(TAG, "[" + vehicle.getName() + "] TEAM IS NOT SET");
				return null;
			}
			return new Asset(this);
		}

		public void setMinDelay(int minDelay) {
			this.minDelay = minDelay;

		}

		public void setMaxDelay(int maxDelay) {
			this.maxDelay = maxDelay;

		}

		public void setStartDelay(boolean startDelay) {
			this.startDelay = startDelay;

		}

		public void setTeam(int team) {
			this.team = team;

		}

		public void setVehicle(Vehicle vehicle) {
			this.vehicle = vehicle;

		}

		public void setTeam(int team, boolean overwrite) {
			if (this.team == 0 || overwrite)
				this.team = team;

		}
	}
}
