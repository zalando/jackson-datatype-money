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
    private final FieldNames names;

    public MonetaryAmountSerializer() {
        this(new NoopMonetaryAmountFormatFactory());
    }

    public MonetaryAmountSerializer(final MonetaryAmountFormatFactory factory) {
        this(factory, FieldNames.defaults());
    }

    public MonetaryAmountSerializer(final MonetaryAmountFormatFactory factory, final FieldNames names) {
        this.factory = factory;
        this.names = names;
    }

    @Override
    public void serialize(final MonetaryAmount value, final JsonGenerator generator, final SerializerProvider provider)
            throws IOException {

        final BigDecimal amount = value.getNumber().numberValueExact(BigDecimal.class);
        final CurrencyUnit currency = value.getCurrency();
        @Nullable final String formatted = format(value, provider);

        generator.writeStartObject();
        {
            generator.writeNumberField(names.getAmount(), amount);
            generator.writeObjectField(names.getCurrency(), currency);

            if (formatted != null) {
                generator.writeStringField(names.getFormatted(), formatted);
            }
        }
        generator.writeEndObject();
    }

    @Nullable
    private String format(final MonetaryAmount value, final SerializerProvider provider) {
        final Locale locale = provider.getConfig().getLocale();
        final MonetaryAmountFormat format = factory.create(locale);
        return format == null ? null : format.format(value);
    }

}