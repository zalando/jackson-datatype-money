package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.annotation.Nullable;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

public final class MonetaryAmountSerializer extends JsonSerializer<MonetaryAmount> {

    private final MonetaryAmountFormatFactory factory;

    public MonetaryAmountSerializer() {
        this(new NoopMonetaryAmountFormatFactory());
    }

    public MonetaryAmountSerializer(final MonetaryAmountFormatFactory factory) {
        this.factory = factory;
    }

    @Override
    public void serialize(final MonetaryAmount value, final JsonGenerator generator, final SerializerProvider provider)
            throws IOException {

        final BigDecimal amount = value.getNumber().numberValueExact(BigDecimal.class);
        final CurrencyUnit currency = value.getCurrency();
        @Nullable final String formatted = format(value, provider);

        generator.writeObject(new MoneyNode(amount, currency, formatted));
    }

    @Nullable
    private String format(final MonetaryAmount value, final SerializerProvider provider) {
        final Locale locale = provider.getConfig().getLocale();
        final MonetaryAmountFormat format = factory.create(locale);
        return format == null ? null : format.format(value);
    }

}