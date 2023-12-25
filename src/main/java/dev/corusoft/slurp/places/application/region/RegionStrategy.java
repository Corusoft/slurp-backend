package dev.corusoft.slurp.places.application.region;

import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public interface RegionStrategy {

    /**
     * Genera una región a partir de dos ubicaciones y el radio de cada ubicación.
     * @param point1 Ubicación del primer punto
     * @param radius1 Radio alrededor del primer punto
     * @param point2 Ubicación del segundo punto
     * @param radius2 Radio alrededor del segundo punto
     * @return Región generada a partir de ambos puntos y sus respectivos radios
     */
    PolygonsSet generateRegion(Vector2D point1, float radius1, Vector2D point2, float radius2);

    boolean checkRegionContainsPoint(PolygonsSet region, Vector2D point);
}
