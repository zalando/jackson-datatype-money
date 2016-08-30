package org.zalando.jackson.datatype.money;

import javax.annotation.Nullable;
import javax.money.format.MonetaryAmountFormat;
import java.util.Locale;

public interface MonetaryAmountFormatFactory {

    @Nullable
    MonetaryAmountFormat create(final Locale locale);

}
