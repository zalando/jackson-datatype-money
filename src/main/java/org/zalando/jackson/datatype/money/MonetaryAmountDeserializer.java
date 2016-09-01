package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.money.MonetaryAmount;
import java.io.IOException;

public final class MonetaryAmountDeserializer extends JsonDeserializer<MonetaryAmount> {

    private final MonetaryAmountFactory factory;

    /**
     * @deprecated use {@link #MonetaryAmountDeserializer(MonetaryAmountFactory)} with {@link MoneyFactory}
     */
    @Deprecated
    public MonetaryAmountDeserializer() {
        this(new MoneyFactory());
    }

    public MonetaryAmountDeserializer(final MonetaryAmountFactory factory) {
        this.factory = factory;
    }

    @Override
    public MonetaryAmount deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final MoneyNode node = context.readValue(parser, MoneyNode.class);
        return factory.create(node.getAmount(), node.getCurrency());
    }

}