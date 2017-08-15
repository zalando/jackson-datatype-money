package org.zalando.jackson.datatype.money;

import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

/**
 * @see MonetaryFormats#getAmountFormat(Locale, String...) 
 */
final class DefaultMonetaryAmountFormatFactory implements MonetaryAmountFormatFactory {

    @Override
    public MonetaryAmountFormat create(final Locale locale) {
        return MonetaryFormats.getAmountFormat(locale);
    }
    
}
