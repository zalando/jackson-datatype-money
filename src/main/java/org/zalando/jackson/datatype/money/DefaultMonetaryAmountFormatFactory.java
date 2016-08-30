package org.zalando.jackson.datatype.money;

import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

/**
 * @see MonetaryFormats#getAmountFormat(Locale, String...) 
 */
public final class DefaultMonetaryAmountFormatFactory implements MonetaryAmountFormatFactory {

    @Override
    public MonetaryAmountFormat create(Locale locale) {
        return MonetaryFormats.getAmountFormat(locale);
    }
    
}
