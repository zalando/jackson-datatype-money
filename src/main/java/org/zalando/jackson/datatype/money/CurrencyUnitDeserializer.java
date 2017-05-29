package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.io.IOException;

final class CurrencyUnitDeserializer extends JsonDeserializer<CurrencyUnit> {

    @Override
    public Object deserializeWithType(final JsonParser parser, final DeserializationContext context,
            final TypeDeserializer deserializer) throws IOException {

        // effectively assuming no type information at all
        return deserialize(parser, context);
    }

    @Override
    public CurrencyUnit deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String currencyCode = parser.getValueAsString();
        return Monetary.getCurrency(currencyCode);
    }

}
