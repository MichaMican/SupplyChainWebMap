package com.thd.mapserver.helper;

import com.thd.mapserver.domain.geom.SFAPoint;
import com.thd.mapserver.models.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GeometryHelper {

	private GeometryHelper() {
	}

	public static String convertCoordinatesToWkt(SFAPoint point){
		return convertCoordinatesToWkt(point.getCoordinate());
	}

	public static String convertCoordinatesToWkt(Coordinate coordinate) {
		return Double.isNaN(coordinate.getZ()) ? String.format(Locale.US, "%.7f %.7f", coordinate.getX(), coordinate.getY())
				: String.format(Locale.US, "%.7f %.7f %.7f", coordinate.getX(), coordinate.getY(), coordinate.getZ());
	}

	public static List<SFAPoint> convertCoordinateListToPointList(List<Coordinate> coordinates){
		List<SFAPoint> returnList = new ArrayList<>();

		for (var coordinate : coordinates) {
			returnList.add(new SFAPoint(coordinate));
		}

		return returnList;
	}
}
