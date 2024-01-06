package dev.corusoft.slurp.places.application.region;

import dev.corusoft.slurp.places.domain.SearchPerimeter;
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
    PolygonsSet generateRegionOfCandidates(SearchPerimeter range1, SearchPerimeter range2);

    /**
     * Comprueba si el punto dado está contenido en la región recibida
     *
     * @param region Región a observar
     * @param point  Punto a comparar
     * @return Booleano indicando si el punto está dentro de la región
     */
    boolean checkRegionContainsCandidate(PolygonsSet region, Vector2D point);
}
