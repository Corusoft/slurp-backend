package dev.corusoft.slurp.places.application.commands;

/**
 * Command interface for Google Places API requests
 *
 * @param <T> Expected result type
 */
public interface PlacesApiCommand<T> {
    T execute();
}
