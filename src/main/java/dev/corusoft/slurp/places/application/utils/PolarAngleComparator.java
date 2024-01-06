package dev.corusoft.slurp.places.application.utils;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Comparator;

public class PolarAngleComparator implements Comparator<Vector2D> {
    private static final Vector2D ORIGIN = new Vector2D(0, 0);

    private static double calculateAngle(Vector2D point) {
        return Vector2D.angle(ORIGIN, point);
    }

    private static double calculateDistance(Vector2D point) {
        // Calcular distancia cuadrática es más eficiente que calcular distancia real
        // porque elevar al cuadrado es computacionalmente más eficiente que una raíz cuadrada
        return Vector2D.distanceSq(ORIGIN, point);
    }

    @Override
    public int compare(Vector2D o1, Vector2D o2) {

        // Ángulos de los puntos respecto al origen
        double angleO1 = calculateAngle(o1);
        double angleO2 = calculateAngle(o2);

        int angleComparison = Double.compare(angleO1, angleO2);
        if (angleComparison != 0) return angleComparison;

        // Ángulos son iguales, comparar por distancia
        double distance1 = calculateDistance(o1);
        double distance2 = calculateDistance(o2);

        return Double.compare(distance1, distance2);
    }
}
