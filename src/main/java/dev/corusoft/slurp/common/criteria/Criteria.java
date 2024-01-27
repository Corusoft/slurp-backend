package dev.corusoft.slurp.common.criteria;

import java.util.List;

public interface Criteria<T> {
    List<T> meetsCriteria(List<T> items);
}
