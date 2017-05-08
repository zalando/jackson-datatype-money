package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.javamoney.moneta.spi.DefaultNumberValue;

import javax.money.NumberValue;
import java.io.IOException;
import java.math.BigDecimal;

public final class DecimalNumberValueDeserializer extends JsonDeserializer<NumberValue> {

    @Override
    public NumberValue deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final BigDecimal decimal = context.readValue(parser, BigDecimal.class);
        return DefaultNumberValue.of(decimal);
    }

}
