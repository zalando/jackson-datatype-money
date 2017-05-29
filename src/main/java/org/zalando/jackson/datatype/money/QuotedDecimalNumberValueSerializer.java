package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import javax.money.NumberValue;

final class QuotedDecimalNumberValueSerializer extends JsonSerializer<NumberValue> {

    @Override
    public void serialize(final NumberValue value, final JsonGenerator generator, final SerializerProvider serializers) throws IOException {
        final BigDecimal amount = value.numberValueExact(BigDecimal.class);
        generator.writeString(amount.toString());
    }

}
