package dev.corusoft.slurp.places.application.region;

import dev.corusoft.slurp.places.application.utils.PolarAngleComparator;
import lombok.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;

import static java.lang.Math.*;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class MinimunEnclosingRectangle {
    @Getter
    private Vector2D westernPoint;
    @Getter
    private double leftRadius;
    @Getter
    private Vector2D easternPoint;
    @Getter
    private double rightRadius;
    @Getter
    private double height;
    @Getter
    private double inclination;
    private Vector2D topRightCorner;
    private Vector2D topLeftCorner;
    private Vector2D bottomLeftCorner;
    private Vector2D bottomRightCorner;

    public MinimunEnclosingRectangle(Vector2D westernPoint, double leftRadius, Vector2D easternPoint, double rightRadius) {
        this.westernPoint = westernPoint;
        this.leftRadius = leftRadius;
        this.easternPoint = easternPoint;
        this.rightRadius = rightRadius;
        this.height = 2 * Math.max(leftRadius, rightRadius);

        this.inclination = calculateInclination();
        calculateBounds();
    }

    public Map<RectangleCornerNames, Vector2D> getCorners() {
        Map<RectangleCornerNames, Vector2D> cornersMap = new EnumMap<>(RectangleCornerNames.class);
        cornersMap.put(RectangleCornerNames.UP_RIGHT, topRightCorner);
        cornersMap.put(RectangleCornerNames.UP_LEFT, topLeftCorner);
        cornersMap.put(RectangleCornerNames.DOWN_LEFT, bottomLeftCorner);
        cornersMap.put(RectangleCornerNames.DOWN_RIGHT, bottomRightCorner);

        return cornersMap;
    }

    public List<Vector2D> toClockwiseOrderedPoints() {
        List<Vector2D> points = Arrays.asList(topRightCorner, topLeftCorner, bottomLeftCorner, bottomRightCorner);
        PolarAngleComparator comparator = new PolarAngleComparator();

        return points.stream()
                .sorted(comparator)
                .toList();
    }

    private double calculateInclination() {
        double slopeY = easternPoint.getY() - westernPoint.getY();
        double slopeX = easternPoint.getX() - westernPoint.getX();

        return Math.atan2(slopeY, slopeX);
    }

    private void calculateBounds() {
        // Vertice superior derecho (NE)
        double topRightCornerX = easternPoint.getX() + rightRadius * sqrt(2) * cos((PI / 4) + inclination);
        double topRightCornerY = easternPoint.getY() + (height / 2) * sqrt(2) * sin((PI / 4) + inclination);
        this.topRightCorner = new Vector2D(topRightCornerX, topRightCornerY);

        double topLeftCornerX = easternPoint.getX() + rightRadius * sqrt(2) * cos(((3 * PI) / 4) + inclination);
        double topLeftCornerY = easternPoint.getY() + (height / 2) * sqrt(2) * sin(((3 * PI) / 4) + inclination);
        this.topLeftCorner = new Vector2D(topLeftCornerX, topLeftCornerY);

        double bottomLeftCornerX = easternPoint.getX() + rightRadius * sqrt(2) * cos(((5 * PI) / 4) + inclination);
        double bottomLeftCornerY = easternPoint.getY() + (height / 2) * sqrt(2) * sin(((5 * PI) / 4) + inclination);
        this.bottomLeftCorner = new Vector2D(bottomLeftCornerX, bottomLeftCornerY);

        double bottomRightCornerX = easternPoint.getX() + rightRadius * sqrt(2) * cos(((7 * PI) / 4) + inclination);
        double bottomRightCornerY = easternPoint.getY() + (height / 2) * sqrt(2) * sin(((7 * PI) / 4) + inclination);
        this.bottomRightCorner = new Vector2D(bottomRightCornerX, bottomRightCornerY);
    }


}
