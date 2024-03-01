package dev.corusoft.slurp;

import com.google.maps.model.LatLng;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class TestConstants {
    public final static String DEFAULT_NAME = "Name";
    public final static String DEFAULT_SURNAME = "Surname";
    public final static LocalDate DEFAULT_BIRTHDATE = LocalDate.of(2005, 10, 9);
    public final static String DEFAULT_NICKNAME = "Foo";
    public final static String DEFAULT_PASSWORD = "Bar";
    public final static String DEFAULT_EMAIL_DOMAIN = "slurp.dev";
    public final static String DEFAULT_PHONE_NUMBER = "987654321";
    public final static String LOCALDATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public final static String LOCALDATETIME_ISO_FORMAT = DateTimeFormat.ISO.DATE_TIME.name();
    public final static String LOCALDATE_ISO_FORMAT = DateTimeFormat.ISO.DATE.name();
    public final static String DATETIME_FORMAT = "dd-MM-yyyy";

    public final static int DEFAULT_SEARCH_PERIMETER_RADIUS = 200;
    public final static double MADRID_KILOMETRIC_POINT_0_LATITUDE = 40.4169473;
    public final static double MADRID_KILOMETRIC_POINT_0_LONGITUDE = -3.7035285;
    public final static LatLng MADRID_LOCATION = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
}
