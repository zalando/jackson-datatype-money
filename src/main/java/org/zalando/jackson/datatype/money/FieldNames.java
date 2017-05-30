package org.zalando.jackson.datatype.money;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Wither;

@AllArgsConstructor(staticName = "valueOf")
@Getter
final class FieldNames {

    static final FieldNames DEFAULT = FieldNames.valueOf("amount", "currency", "formatted");

    @Wither
    private final String amount;

    @Wither
    private final String currency;

    @Wither
    private final String formatted;

    static FieldNames defaults() {
        return DEFAULT;
    }

}
