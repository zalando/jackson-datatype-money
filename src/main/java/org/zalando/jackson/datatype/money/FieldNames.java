package org.zalando.jackson.datatype.money;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@AllArgsConstructor(staticName = "valueOf")
@Getter
final class FieldNames {

    static final FieldNames DEFAULT = FieldNames.valueOf("amount", "currency", "formatted");

    @With
    private final String amount;

    @With
    private final String currency;

    @With
    private final String formatted;

    static FieldNames defaults() {
        return DEFAULT;
    }

}
