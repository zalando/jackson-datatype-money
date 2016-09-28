package org.zalando.jackson.datatype.money;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Wither;

@AllArgsConstructor(staticName = "valueOf")
@Getter
public final class FieldNames {

    private static final FieldNames DEFAULT = FieldNames.valueOf("amount", "currency", "formatted");

    @Wither
    private final String amount;

    @Wither
    private final String currency;

    @Wither
    private final String formatted;

    public static FieldNames defaults() {
        return DEFAULT;
    }

}
