package dev.corusoft.slurp.places.application.criteria;

import dev.corusoft.slurp.common.exception.InvalidArgumentException;
import dev.corusoft.slurp.common.exception.MissingMandatoryValueException;

public class PlacesCriteriaValidator {
    public static Double MAX_LATITUDE_VALUE = 90.0;
    public static Double MAX_LONGITUDE_VALUE = 180.0;
    private static final String className = PlacesCriteria.class.getSimpleName();

    public static void validate(PlacesCriteria instance) throws MissingMandatoryValueException, InvalidArgumentException {
        if (instance == null) throw new MissingMandatoryValueException("placesCriteria");
        validateCoordinates(instance.getLatitude(), instance.getLongitude());
        validateRadius(instance.getRadius());
    }

    /* HELPER METHODS */
    private static void validateCoordinates(Double latitude, Double longitude) throws MissingMandatoryValueException, InvalidArgumentException {
        if (latitude == null) throw new MissingMandatoryValueException("latitude");
        if (longitude == null) throw new MissingMandatoryValueException("longitude");

        boolean isLatitudeInsideRange = Math.abs(latitude) <= MAX_LATITUDE_VALUE;
        if (!isLatitudeInsideRange)
            throw new InvalidArgumentException(className, "latitude", latitude);

        boolean isLongitudeInsideRange = Math.abs(longitude) <= MAX_LONGITUDE_VALUE;
        if (!isLongitudeInsideRange)
            throw new InvalidArgumentException(className, "longitude", longitude);
    }

    private static void validateRadius(Integer radius) throws MissingMandatoryValueException, InvalidArgumentException {
        if (radius == null) throw new MissingMandatoryValueException("radius");

        boolean isPositive = radius > 0;

        if (!isPositive)
            throw new InvalidArgumentException(className, "radius", radius);
    }


}
