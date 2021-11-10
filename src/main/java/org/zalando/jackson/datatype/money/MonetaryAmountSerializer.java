package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.annotation.Nullable;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import java.io.IOException;
import java.util.Locale;

final class MonetaryAmountSerializer extends StdSerializer<MonetaryAmount> {

    private final FieldNames names;
    private final AmountWriter<?> writer;
    private final MonetaryAmountFormatFactory factory;

    MonetaryAmountSerializer(final FieldNames names, final AmountWriter<?> writer,
            final MonetaryAmountFormatFactory factory) {
        super(MonetaryAmount.class);
        this.writer = writer;
        this.factory = factory;
        this.names = names;
    }

    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper wrapper, final JavaType hint)
            throws JsonMappingException {

        @Nullable final JsonObjectFormatVisitor visitor = wrapper.expectObjectFormat(hint);

        if (visitor == null) {
            return;
        }

        final SerializerProvider provider = wrapper.getProvider();

        visitor.property(names.getAmount(),
                provider.findValueSerializer(writer.getType()),
                provider.constructType(writer.getType()));

        visitor.property(names.getCurrency(),
                provider.findValueSerializer(CurrencyUnit.class),
                provider.constructType(CurrencyUnit.class));

        visitor.optionalProperty(names.getFormatted(),
                provider.findValueSerializer(String.class),
                provider.constructType(String.class));
    }

    @Override
    public void serializeWithType(final MonetaryAmount value, final JsonGenerator generator,
            final SerializerProvider provider, final TypeSerializer serializer) throws IOException {

        // effectively assuming no type information at all
        serialize(value, generator, provider);
    }

    @Override
    public void serialize(final MonetaryAmount value, final JsonGenerator json, final SerializerProvider provider)
            throws IOException {

        final CurrencyUnit currency = value.getCurrency();
        @Nullable final String formatted = format(value, provider);

        json.writeStartObject();
        {
            provider.defaultSerializeField(names.getAmount(), writer.write(value), json);
            provider.defaultSerializeField(names.getCurrency(), currency, json);

            if (formatted != null) {
                json.writeStringField(names.getFormatted(), formatted);
            }
        }
        json.writeEndObject();
    }

    @Nullable
    private String format(final MonetaryAmount value, final SerializerProvider provider) {
        final Locale locale = provider.getConfig().getLocale();
        final MonetaryAmountFormat format = factory.create(locale);
        return format == null ? null : format.format(value);
    }

}
