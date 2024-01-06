package dev.corusoft.slurp.places.application.region;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum RectangleCornerNames {
    UP_LEFT,
    UP_RIGHT("NE"),
    DOWN_RIGHT,
    DOWN_LEFT("SW");

    private String alias;

    public boolean hasAlias() {
        return this.alias != null;
    }

}
