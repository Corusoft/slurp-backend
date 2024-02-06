package dev.corusoft.slurp.common.criteria;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class NotCriteria<T> implements Criteria<T> {
    private Criteria<T> criteria;

    @Override
    public List<T> meetsCriteria(List<T> items) {
        List<T> meetingCriteriaItems = criteria.meetsCriteria(items);

        for (T item : meetingCriteriaItems) {
            // Retirar de los elementos recibidos aquellos que cumplen el criterio
            items.remove(item);
        }

        return items;
    }
}
