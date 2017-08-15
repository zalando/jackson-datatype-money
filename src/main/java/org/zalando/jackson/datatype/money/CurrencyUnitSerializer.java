package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.money.CurrencyUnit;
import java.io.IOException;

final class CurrencyUnitSerializer extends JsonSerializer<CurrencyUnit> {

    @Override
    public void serialize(final CurrencyUnit value, final JsonGenerator generator, final SerializerProvider serializers)
            throws IOException {
        generator.writeString(value.getCurrencyCode());
    }

}
