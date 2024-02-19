package dev.corusoft.slurp.common.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
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
    @Accessors(fluent = true)
    @JsonProperty("hasMoreItems")
    private boolean hasMoreItems;

    private String nextPageToken;

    /**
     * Cantidad de elementos contenidos
     */
    private int itemsCount;

    public Block(List<T> items, boolean hasMoreItems) {
        this.items = items;
        this.hasMoreItems = hasMoreItems;
        this.itemsCount = items.size();
    }

    public Block(List<T> items, String nextPageToken) {
        this.items = items;
        this.itemsCount = items.size();
        this.hasMoreItems = nextPageToken != null;
        this.nextPageToken = nextPageToken;
    }

    public static <T> Block<T> emptyBlock() {
        return new Block<>(new ArrayList<>(0), false);
    }
}
