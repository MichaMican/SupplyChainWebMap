package com.thd.mapserver.domain.geom;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class SFAGeometry {
	protected final int srid;

	public SFAGeometry(int srid) {
		this.srid = srid;
	}
	public SFAGeometry() {
		this.srid = 4326;
	}

	public int srid() {
		return this.srid;
	}

	public abstract String asText();

	public abstract String geometryType();

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof SFAGeometry)) {
			return false;
		}

		return srid == ((SFAGeometry) other).srid;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(srid).toHashCode();
	}
}
