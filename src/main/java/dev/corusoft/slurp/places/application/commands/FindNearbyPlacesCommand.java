package dev.corusoft.slurp.places.application.commands;

import com.google.maps.PlacesApi;
import dev.corusoft.slurp.common.criteria.Criteria;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class FindNearbyPlacesCommand<T> implements PlacesApiCommand<T> {
    /**
     * Criterios de búsqueda
     */
    private final Criteria<List<T>> criteria;

    // TODO configurar esto para que sea un bean, o para que ya tenga el contexto configurado al arrancar
    private PlacesApi placesApi;


    public FindNearbyPlacesCommand(Criteria<List<T>> criteria) {
        this.criteria = criteria;
    }

    @Override
    public T execute() {
        // TODO hacer que este Command llame bien a la API de Places con los parámetros
        // TODO seguir programando para que se llame al comando en vez de directamente a la API
        // TODO lanzar tests de cobertura para ver si mi código tiene coverage, incluyendo command, y se hace correctamente llamada a api. Seguir haciendo tests


        return null;
    }
}
