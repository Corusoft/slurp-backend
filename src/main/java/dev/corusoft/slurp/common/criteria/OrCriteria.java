package dev.corusoft.slurp.common.criteria;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class OrCriteria<T> implements Criteria<T> {
    private Criteria<T> currentCriteria;
    private Criteria<T> otherCriteria;

    @Override
    public List<T> meetsCriteria(List<T> items) {
        List<T> itemsMeetingCurrentCriteria = currentCriteria.meetsCriteria(items);
        List<T> itemsMeetingOtherCriteria = otherCriteria.meetsCriteria(items);

        for (T item : itemsMeetingOtherCriteria) {
            // Si los items ya filtrados no contienen a los items filtrados actualmente, añadirlos
            if (!itemsMeetingCurrentCriteria.contains(item)) {
                itemsMeetingCurrentCriteria.add(item);
            }
        }

        return itemsMeetingCurrentCriteria;
    }
}
