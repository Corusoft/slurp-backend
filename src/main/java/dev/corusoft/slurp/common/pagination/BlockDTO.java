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

    private String nextPageToken;

    public BlockDTO(List<T> items, String nextPageToken) {
        this.items = items;
        this.nextPageToken = nextPageToken;
        this.hasMoreItems = nextPageToken != null;
        this.itemsCount = items.size();
    }

    public BlockDTO(Block<T> block) {
        this.items = block.getItems();
        this.hasMoreItems = block.hasMoreItems();
        this.itemsCount = block.getItemsCount();
        this.nextPageToken = block.getNextPageToken();
    }
}
