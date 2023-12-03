package dev.corusoft.slurp.places.application;


import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public interface GeometryService {
// TODO esto no es un service, debe ser un patrón factoría para poder fabricar
//  circulos, rectángulos, polígonos, segmentos entre dos puntos, etc

    // TODO análogamente, usar patrón estrategia para poder intercambiar el algoritmo
    //  que se usa para calcular la región de candidatos (según rectángulo, según óvalo, etc)

    PolygonsSet generateCandidatesRegion(Vector2D user1, Vector2D user2);
}
