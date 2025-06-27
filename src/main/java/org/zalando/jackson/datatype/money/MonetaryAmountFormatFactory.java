package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.annotation.Nullable;
import javax.money.format.MonetaryAmountFormat;
import java.util.Locale;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * @deprecated This module is deprecated. Please use
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/blob/2.x/javax-money/src/main/java/com/fasterxml/jackson/datatype/javax/money/MonetaryAmountFormatFactory.java">MonetaryAmountFormatFactory</a> instead.
 */
@Deprecated
@API(status = DEPRECATED)
@FunctionalInterface
public interface MonetaryAmountFormatFactory {

    MonetaryAmountFormatFactory NONE = locale -> null;

    @Nullable
    MonetaryAmountFormat create(final Locale defaultLocale);

}
