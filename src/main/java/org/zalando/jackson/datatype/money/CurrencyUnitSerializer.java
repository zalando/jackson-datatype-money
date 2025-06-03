package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apiguardian.api.API;

import javax.money.CurrencyUnit;
import java.io.IOException;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * @deprecated This module is deprecated. Please use
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/blob/2.x/javax-money/src/main/java/com/fasterxml/jackson/datatype/javax/money/CurrencyUnitSerializer.java">CurrencyUnitSerializer</a> instead.
 */
@Deprecated
@API(status = DEPRECATED)
public final class CurrencyUnitSerializer extends StdSerializer<CurrencyUnit> {

    CurrencyUnitSerializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public void serialize(final CurrencyUnit value, final JsonGenerator generator, final SerializerProvider serializers)
            throws IOException {
        generator.writeString(value.getCurrencyCode());
    }

    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType hint)
            throws JsonMappingException {
        visitor.expectStringFormat(hint);
    }

}
