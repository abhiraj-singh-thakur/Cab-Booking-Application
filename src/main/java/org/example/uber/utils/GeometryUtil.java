package org.example.uber.utils;

import org.example.uber.dto.PointDTO;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    public static Point createPoint(PointDTO pointDto) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(pointDto.getCoordinates()[0],
                pointDto.getCoordinates()[1]
        );
        return geometryFactory.createPoint(coordinate);
    }
}
