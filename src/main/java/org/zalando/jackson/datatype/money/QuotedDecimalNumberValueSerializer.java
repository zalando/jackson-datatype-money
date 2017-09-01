package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.money.NumberValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;

final class QuotedDecimalNumberValueSerializer extends JsonSerializer<NumberValue> {

    @Override
    public void serialize(final NumberValue value, final JsonGenerator generator, final SerializerProvider serializers) throws IOException {
        final BigDecimal amount = value.numberValueExact(BigDecimal.class);
        generator.writeString(toString(generator, amount));
    }

    private String toString(final JsonGenerator generator, final BigDecimal amount) {
        return generator.isEnabled(WRITE_BIGDECIMAL_AS_PLAIN) ? amount.toPlainString() : amount.toString();
    }

}
