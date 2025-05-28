package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apiguardian.api.API;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.io.IOException;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * @deprecated This module is deprecated. Please use
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/blob/2.x/javax-money/src/main/java/com/fasterxml/jackson/datatype/javax/money/CurrencyUnitDeserializer.java">CurrencyUnitDeserializer</a> instead.
 */
@Deprecated
@API(status = DEPRECATED)
public final class CurrencyUnitDeserializer extends JsonDeserializer<CurrencyUnit> {

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
