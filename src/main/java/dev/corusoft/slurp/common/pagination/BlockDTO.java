package dev.corusoft.slurp.common.pagination;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BlockDTO<T> {
    @NotNull
    private List<T> items;

    @NotNull
    private boolean hasMoreItems;

    @NotNull
    @PositiveOrZero
    private int itemsCount;

    public BlockDTO(List<T> items, boolean hasMoreItems) {
        this.items = items;
        this.hasMoreItems = hasMoreItems;
        this.itemsCount = items.size();
    }
}
