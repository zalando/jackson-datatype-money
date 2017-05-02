package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import javax.annotation.Nullable;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.String.format;

public final class MonetaryAmountDeserializer<M extends MonetaryAmount> extends JsonDeserializer<M> {

    private final MonetaryAmountFactory<M> factory;
    private final FieldNames names;

    /**
     *
     * @param factory the amount factory used for deserialization of monetary amounts
     * @deprecated as of 0.11.0 in favor of {@link #MonetaryAmountDeserializer(MonetaryAmountFactory, FieldNames)}
     */
    @Deprecated
    public MonetaryAmountDeserializer(final MonetaryAmountFactory<M> factory) {
        this(factory, FieldNames.defaults());
    }

    public MonetaryAmountDeserializer(final MonetaryAmountFactory<M> factory, final FieldNames names) {
        this.factory = factory;
        this.names = names;
    }

    @Override
    public M deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        BigDecimal amount = null;
        CurrencyUnit currency = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();

            parser.nextToken();

            if (field.equals(names.getAmount())) {
                amount = context.readValue(parser, BigDecimal.class);
            } else if (field.equals(names.getCurrency())) {
                currency = context.readValue(parser, CurrencyUnit.class);
            } else if (field.equals(names.getFormatted())) {
                //noinspection UnnecessaryContinue
                continue;
            } else if (context.isEnabled(FAIL_ON_UNKNOWN_PROPERTIES)) {
                throw UnrecognizedPropertyException.from(parser, MonetaryAmount.class, field,
                        Arrays.<Object>asList(names.getAmount(), names.getCurrency(), names.getFormatted()));
            } else {
                parser.skipChildren();
            }
        }

        checkPresent(parser, amount, names.getAmount());
        checkPresent(parser, currency, names.getCurrency());

        return factory.create(amount, currency);
    }

    private void checkPresent(final JsonParser parser, @Nullable final Object value, final String name)
            throws JsonParseException {
        if (value == null) {
            throw new JsonParseException(parser, format("Missing property: '%s'", name));
        }
    }

}