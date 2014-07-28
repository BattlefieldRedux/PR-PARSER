package pt.uturista.prparser.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Level implements Serializable {

	private final String name;
	private final List<Layout> layouts;

	private Level(Builder builder) {
		this.name = builder.name;
		this.layouts = builder.layouts;
	}

	public String getName() {
		return name;
	}

	public List<Layout> getLayouts() {
		List<Layout> newLayouts = new ArrayList<Layout>(layouts);
		return newLayouts;
	}

	public static class Builder {
		private String name;
		private List<Layout> layouts = new ArrayList<Layout>();

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder addLayout(Layout layout) {
			layouts.add(layout);
			return this;
		}

		public Level build() {
			return new Level(this);
		}

	}
}
