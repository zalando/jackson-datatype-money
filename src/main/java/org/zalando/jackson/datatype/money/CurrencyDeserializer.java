package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Currency;

public final class CurrencyDeserializer extends JsonDeserializer<Currency> {

    @Override
    public Currency deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String currencyCode = parser.getValueAsString();
        return Currency.getInstance(currencyCode);
    }

}
