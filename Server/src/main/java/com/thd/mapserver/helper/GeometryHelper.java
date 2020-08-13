package com.thd.mapserver.helper;

import com.thd.mapserver.domain.geom.Point;
import com.thd.mapserver.models.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GeometryHelper {

	private GeometryHelper() {
	}

	public static String convertCoordinatesToWkt(Coordinate coordinate) {
		return Double.isNaN(coordinate.getZ()) ? String.format(Locale.US, "%.7f %.7f", coordinate.getX(), coordinate.getY())
				: String.format(Locale.US, "%.7f %.7f %.7f", coordinate.getX(), coordinate.getY(), coordinate.getZ());
	}

	public static List<Point> convertCoordinateListToPointList(List<Coordinate> coordinates){
		List<Point> returnList = new ArrayList<>();

		for (var coordinate : coordinates) {
			returnList.add(new Point(coordinate));
		}

		return returnList;
	}
}
