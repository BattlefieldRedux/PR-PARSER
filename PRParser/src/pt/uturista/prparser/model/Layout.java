package pt.uturista.prparser.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import pt.uturista.log.Log;

public class Layout implements Serializable {
	final public static int BLUFOR = 2;
	final public static int OPFOR = 1;
	final private GameMode gameMode;
	final private GameSize gameSize;
	final private String OPFaction;
	final private String BLUFaction;
	final private HashMap<String, Asset> bluforAssets;
	final private HashMap<String, Asset> opforAssets;

	private Layout(Builder builder) {
		this.gameMode = builder.gameMode;
		this.gameSize = builder.gameSize;
		this.bluforAssets = builder.bluforAssets;
		this.opforAssets = builder.opforAssets;
		this.OPFaction = builder.OPFaction;
		this.BLUFaction = builder.BLUFaction;
	}

	public static enum GameMode {
		gpm_insurgency, gpm_skirmish, gpm_coop, gpm_cq, gpm_cnc, gpm_vehicles
	}

	public static enum GameSize {
		infantry, alternative, standard, large
	}

	public static class Builder {
		private static final String TAG = "Layout.Builder";
		private GameMode gameMode = null;
		private GameSize gameSize = null;
		private HashMap<String, Asset> bluforAssets = new HashMap<>();
		private HashMap<String, Asset> opforAssets = new HashMap<>();
		private String OPFaction = null;
		private String BLUFaction = null;

		public Builder setOpFaction(String name) {
			this.OPFaction = name;
			return this;
		}

		public Builder setBluFaction(String name) {
			this.BLUFaction = name;
			return this;
		}

		public Builder setGameMode(String gameMode) {
			this.gameMode = GameMode.valueOf(gameMode);
			return this;
		}

		public Builder setGameSize(String gameSize) {

			if (gameSize.equals("16")) {
				this.gameSize = GameSize.infantry;
			} else if (gameSize.equals("32")) {
				this.gameSize = GameSize.alternative;
			} else if (gameSize.equals("64")) {
				this.gameSize = GameSize.standard;
			} else if (gameSize.equals("128")) {
				this.gameSize = GameSize.large;
			}
			return this;
		}

		public Builder addAsset(Asset asset, int side) {
			Log.d(TAG, "addAsset(" + side + ")");

			if (side == OPFOR) {
				Asset elemnt = opforAssets.get(asset.getVehicle().getKey());
				if (elemnt == null) {
					opforAssets.put(asset.getVehicle().getKey(), asset);
				} else {
					elemnt.addAssetCount();
				}

			} else if (side == BLUFOR) {
				Asset elemnt = bluforAssets.get(asset.getVehicle().getKey());
				if (elemnt == null) {
					bluforAssets.put(asset.getVehicle().getKey(), asset);
				} else {
					elemnt.addAssetCount();
				}
			} else {
				Log.e(TAG, "addAsset: no Side detected:"
						+ asset.getVehicle().getName());
			}
			return this;

		}

		public Layout build() {

			if (gameMode == null)
				Log.e(TAG, "gameMode IS NULL");
			if (gameSize == null)
				Log.e(TAG, "gameSize IS NULL");
			if (OPFaction == null)
				Log.e(TAG, "OPFaction IS NULL");
			if (BLUFaction == null)
				Log.e(TAG, "BLUFaction IS NULL");

			return new Layout(this);
		}

	}

}
