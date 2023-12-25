package dev.corusoft.slurp.places.application.region;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@NoArgsConstructor
@Setter
@Log4j2
public class MinimumEnclosingRectangleRegionStrategy implements RegionStrategy {
    private RegionStrategies strategy = RegionStrategies.MinimumEnclosingRectangle;


    @Override
    public PolygonsSet generateRegion(Vector2D point1, float radius1, Vector2D point2, float radius2) {
        log.info("Generating region using {} algorythm", this.strategy.name());

        return null;
    }

    @Override
    public boolean checkRegionContainsPoint(PolygonsSet region, Vector2D point) {
        return false;
    }
}
