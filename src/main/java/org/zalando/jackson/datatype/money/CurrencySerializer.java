package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Currency;

public final class CurrencySerializer extends JsonSerializer<Currency> {

    @Override
    public void serialize(final Currency value, final JsonGenerator generator, final SerializerProvider serializers) throws IOException {
        generator.writeString(value.getCurrencyCode());
    }
    
}
