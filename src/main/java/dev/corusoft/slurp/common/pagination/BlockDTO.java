package dev.corusoft.slurp.common.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
public class BlockDTO<T> {
    @NotNull
    private List<T> items;

    @Accessors(fluent = true)
    @JsonProperty("hasMoreItems")
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
}
