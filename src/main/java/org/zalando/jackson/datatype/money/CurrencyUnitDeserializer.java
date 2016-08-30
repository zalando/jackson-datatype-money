package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.io.IOException;

public final class CurrencyUnitDeserializer extends JsonDeserializer<CurrencyUnit> {

    @Override
    public CurrencyUnit deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String currencyCode = parser.getValueAsString();
        return Monetary.getCurrency(currencyCode);
    }

}
