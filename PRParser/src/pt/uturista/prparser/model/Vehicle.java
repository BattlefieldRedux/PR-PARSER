package pt.uturista.prparser.model;

import java.io.Serializable;

public class Vehicle implements Serializable{
	final static public Vehicle ARTILLERY;
	static {
		Builder builder = new Builder();
		builder.setIcon(pt.uturista.prspy.R.drawable.mini_artillery);
		builder.setName("Area Atack");
		builder.setKey("Area Atack");
		ARTILLERY = builder.build();
	}
	final private String name;
	final private String key;
	final private int icon;
	final private int seats;

	private Vehicle(Builder builder) {
		this.name = builder.name;
		this.key = builder.key;
		this.icon = builder.icon;
		this.seats = builder.seats;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public int getIcon() {
		return icon;
	}

	// ============= HASH CODE AND EQUALS ====================

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		Vehicle other = (Vehicle) obj;

		if (other.getKey() == this.getKey())
			return true;

		return false;

	}

	@Override
	public int hashCode() {
		return this.getKey().hashCode();
	}

	// ==================== Builder =========================
	public static class Builder {
		private String name = null;
		private String key = null;
		private int icon = 0;
		private int seats = 0;

		public Builder addSeat() {
			this.seats++;
			return this;
		}

		public Builder setName(String name) {
			if (this.name == null)
				this.name = name;

			return this;
		}

		public Builder setKey(String key) {
			this.key = key;
			return this;
		}

		public Builder setIcon(int icon) {
			this.icon = icon;
			return this;
		}

		public Vehicle build() {

			if (name == null || key == null)
				return null;

			return new Vehicle(this);
		}

	}
}
