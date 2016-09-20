package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.money.MonetaryAmount;
import java.io.IOException;

public final class MonetaryAmountDeserializer<M extends MonetaryAmount> extends JsonDeserializer<M> {

    private final MonetaryAmountFactory<M> factory;

    public MonetaryAmountDeserializer(final MonetaryAmountFactory<M> factory) {
        this.factory = factory;
    }

    @Override
    public M deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final MoneyNode node = context.readValue(parser, MoneyNode.class);
        return factory.create(node.getAmount(), node.getCurrency());
    }

}