package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;

import static java.lang.String.format;

public final class MonetaryAmountDeserializer<M extends MonetaryAmount> extends JsonDeserializer<M> {

    private final MonetaryAmountFactory<M> factory;
    private final FieldNames names;

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
                amount = parser.getDecimalValue();
            } else if (field.equals(names.getCurrency())) {
                currency = context.readValue(parser, CurrencyUnit.class);
            }
        }

        if (amount == null) {
            throw new JsonParseException(parser, format("Missing '%s' property", names.getAmount()));
        }

        if (currency == null) {
            throw new JsonParseException(parser, format("Missing '%s' property", names.getCurrency()));
        }

        return factory.create(amount, currency);
    }

}