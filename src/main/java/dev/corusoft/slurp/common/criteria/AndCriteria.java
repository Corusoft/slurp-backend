package dev.corusoft.slurp.common.criteria;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AndCriteria<T> implements Criteria<T> {
    private Criteria<T> currentCriteria;
    private Criteria<T> otherCriteria;


    @Override
    public List<T> meetsCriteria(List<T> items) {
        List<T> itemsMeetingCurrentCriteria = currentCriteria.meetsCriteria(items);

        return otherCriteria.meetsCriteria(itemsMeetingCurrentCriteria);
    }
}
