package dev.corusoft.slurp.places.application.region;

import dev.corusoft.slurp.places.domain.SearchPerimeter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.Pair;

import java.util.List;

@NoArgsConstructor
@Setter
@Log4j2
public class MinimumEnclosingRectangleRegionStrategy implements RegionStrategy {
    private RegionStrategies strategy = RegionStrategies.MinimumEnclosingRectangle;
    private double BORDER_THICKNESS = 0.01;


    @Override
    public PolygonsSet generateRegionOfCandidates(SearchPerimeter range1, SearchPerimeter range2) {
        log.info("Generating region using {} algorythm", this.strategy.name());
        // Establecer referencias: ver qué usuario está más al Este y más al Oeste
        Pair<SearchPerimeter, SearchPerimeter> orderedByLongitudePoints = SearchPerimeter.calculateEasternAndWesternPoints(range1, range2);
        SearchPerimeter westernPoint = orderedByLongitudePoints.getFirst();
        SearchPerimeter easternPoint = orderedByLongitudePoints.getSecond();

        // Obtener
        MinimunEnclosingRectangle minimunEnclosingRectangle = new MinimunEnclosingRectangle(westernPoint.getPoint(),
                westernPoint.getRadius(),
                easternPoint.getPoint(),
                easternPoint.getRadius()
        );
        List<Vector2D> orderedEdges = minimunEnclosingRectangle.toClockwiseOrderedPoints();

        return new PolygonsSet(BORDER_THICKNESS, orderedEdges.toArray(new Vector2D[0]));
    }

    @Override
    public boolean checkRegionContainsCandidate(PolygonsSet region, Vector2D point) {
        Region.Location location = region.checkPoint(point);
        boolean isInsideRegion = location.equals(Region.Location.INSIDE);
        boolean isInRegionBoundary = location.equals(Region.Location.BOUNDARY);

        return (isInsideRegion || isInRegionBoundary);
    }

}
