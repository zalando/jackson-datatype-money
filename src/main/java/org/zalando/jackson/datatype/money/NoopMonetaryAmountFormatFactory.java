package org.zalando.jackson.datatype.money;

import javax.money.format.MonetaryAmountFormat;
import java.util.Locale;

final class NoopMonetaryAmountFormatFactory implements MonetaryAmountFormatFactory {

    @Override
    public MonetaryAmountFormat create(final Locale defaultLocale) {
        return null;
    }
    
}
