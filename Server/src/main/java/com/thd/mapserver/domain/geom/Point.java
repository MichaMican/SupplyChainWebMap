package com.thd.mapserver.domain.geom;

import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Point extends Geometry {
	private static final String TYPENAME_POINT = "Point";
	private static final double NULL_ORDINATE = Double.NaN;
	private static final double EPSILON = 1E-7;

	private final Coordinate coordinate;

	public Point(double x, double y, int srid) {
		this(x, y, NULL_ORDINATE, srid);
	}

	public Point(double x, double y, double z, int srid) {
		super(srid);
		this.coordinate = new Coordinate(x,y,z);
	}

	public Point(Coordinate coordinate, int srid){
		super(srid);
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

		if (!(other instanceof Point)) {
			return false;
		}

		final Point otherPoint = (Point) other;
		return Math.abs(coordinate.getX() - otherPoint.coordinate.getX()) < EPSILON && Math.abs(coordinate.getY() - otherPoint.coordinate.getY()) < EPSILON
				&& (Math.abs(coordinate.getZ() - otherPoint.coordinate.getZ()) < EPSILON || Double.isNaN(coordinate.getZ()) && Double.isNaN(otherPoint.coordinate.getZ()));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(coordinate.getX()).append(coordinate.getY()).append(coordinate.getZ()).toHashCode();
	}

	@Override
	public String asText() {
		return String.format("%s(%s)", TYPENAME_POINT.toUpperCase(), GeometryHelper.convertCoordinatesToWkt(this.coordinate));
	}

	@Override
	public String geometryType() {
		return TYPENAME_POINT;
	}

}
