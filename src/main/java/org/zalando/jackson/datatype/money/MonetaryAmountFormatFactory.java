package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.annotation.Nullable;
import javax.money.format.MonetaryAmountFormat;
import java.util.Locale;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface MonetaryAmountFormatFactory {

    MonetaryAmountFormatFactory NONE = locale -> null;

    @Nullable
    MonetaryAmountFormat create(final Locale defaultLocale);

}
