package dev.corusoft.slurp;

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
}
