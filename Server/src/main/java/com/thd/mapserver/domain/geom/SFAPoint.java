package com.thd.mapserver.domain.geom;

import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SFAPoint extends SFAGeometry {
	private static final String TYPENAME_POINT = "Point";
	private static final double NULL_ORDINATE = Double.NaN;
	private static final double EPSILON = 1E-7;

	private final Coordinate coordinate;

	public SFAPoint(double x, double y) {
		this(x, y, NULL_ORDINATE);
	}

	public SFAPoint(double x, double y, double z) {
		super(4326);
		if(z == 0){
			this.coordinate = new Coordinate(x,y);
		} else {
			this.coordinate = new Coordinate(x,y,z);
		}
	}
	public SFAPoint(Coordinate coordinate, int srid){
		super(srid);
		this.coordinate = coordinate;
	}
	public SFAPoint(Coordinate coordinate){
		super();
		this.coordinate = coordinate;
	}

	public Coordinate getCoordinate(){
		return this.coordinate;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof SFAPoint)) {
			return false;
		}

		final SFAPoint otherSFAPoint = (SFAPoint) other;
		return Math.abs(coordinate.getX() - otherSFAPoint.coordinate.getX()) < EPSILON && Math.abs(coordinate.getY() - otherSFAPoint.coordinate.getY()) < EPSILON
				&& (Math.abs(coordinate.getZ() - otherSFAPoint.coordinate.getZ()) < EPSILON || Double.isNaN(coordinate.getZ()) && Double.isNaN(otherSFAPoint.coordinate.getZ()));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(coordinate.getX()).append(coordinate.getY()).append(coordinate.getZ()).toHashCode();
	}

	@Override
	public String asText() {
		return String.format("POINT(%s)", GeometryHelper.convertCoordinatesToWkt(coordinate));
	}

	@Override
	public String geometryType() {
		return TYPENAME_POINT;
	}

}
