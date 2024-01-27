package dev.corusoft.slurp.common.pagination;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block<T> {
    /**
     * Elementos almacenados
     */
    private List<T> items;

    /**
     * Indica si hay más elementos
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean hasMoreItems;

    /**
     * Cantidad de elementos contenidos
     */
    private int itemsCount;


    public Block(List<T> items, boolean hasMoreItems) {
        this.items = items;
        this.hasMoreItems = hasMoreItems;
        this.itemsCount = items.size();
    }

    public boolean hasMoreItems() {
        return this.hasMoreItems;
    }

    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }
}
