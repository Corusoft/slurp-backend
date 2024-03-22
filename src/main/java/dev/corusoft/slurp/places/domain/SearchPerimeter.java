package dev.corusoft.slurp.places.domain;

import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Pair;

@Getter
public class SearchPerimeter {
    private final double radius;
    private final Vector2D point;

    public SearchPerimeter(double x, double y, double radius) {
        this.radius = radius;
        this.point = new Vector2D(x, y);
    }

    public static Pair<SearchPerimeter, SearchPerimeter> calculateEasternAndWesternPoints(SearchPerimeter range1, SearchPerimeter range2) {
        double point1X = range1.getXCoordinate();
        double point2X = range2.getXCoordinate();
        int longitudeComparation = Double.compare(point1X, point2X);
        boolean isPoint1LeftMost = longitudeComparation < 0;

        return (isPoint1LeftMost) ? new Pair<>(range1, range2) : new Pair<>(range2, range1);
    }

    public double getXCoordinate() {
        return this.point.getX();
    }

    public double getYCoordinate() {
        return this.point.getY();
    }
}
