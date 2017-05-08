package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import java.util.Currency;

public final class MoneyModule extends Module {

    private final JsonSerializer<NumberValue> numberValueSerializer;
    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final MonetaryAmountFormatFactory formatFactory;
    private final FieldNames names;

    @SuppressWarnings("deprecation")
    public MoneyModule() {
        this(new MoneyFactory());
    }

    @Override
    public String getModuleName() {
        return MoneyModule.class.getSimpleName();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Version version() {
        final ClassLoader loader = MoneyModule.class.getClassLoader();
        return VersionUtil.mavenVersionFor(loader, "org.zalando", "jackson-datatype-money");
    }

    @Override
    public void setupModule(final SetupContext context) {
        final SimpleSerializers serializers = new SimpleSerializers();

        serializers.addSerializer(Currency.class, new CurrencySerializer());
        serializers.addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        serializers.addSerializer(NumberValue.class, numberValueSerializer);
        serializers.addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer(formatFactory, names));

        context.addSerializers(serializers);

        final SimpleDeserializers deserializers = new SimpleDeserializers();

        deserializers.addDeserializer(Currency.class, new CurrencyDeserializer());
        deserializers.addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        deserializers.addDeserializer(NumberValue.class, new DecimalNumberValueDeserializer());
        deserializers.addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory, names));

        deserializers.addDeserializer(Money.class, new MonetaryAmountDeserializer<>(new MoneyFactory(), names));
        deserializers.addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(new FastMoneyFactory(), names));
        deserializers.addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(new RoundedMoneyFactory(), names));

        context.addDeserializers(deserializers);
    }

    /**
     * @param factory the amount factory used for deserialization of monetary amounts
     * @deprecated as of 0.11.0 in favor of {@link #withAmountFactory(MonetaryAmountFactory)}
     */
    @Deprecated
    @SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
    public MoneyModule(final MonetaryAmountFactory<? extends MonetaryAmount> factory) {
        this(factory, new NoopMonetaryAmountFormatFactory());
    }

    /**
     * @param factory the amount factory used for formatting of monetary amounts
     * @deprecated as of 0.11.0 in favor of {@link #withFormatFactory(MonetaryAmountFormatFactory)}
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public MoneyModule(final MonetaryAmountFormatFactory factory) {
        this(new MoneyFactory(), factory);
    }

    /**
     * @param amountFactory the amount factory used for deserialization of monetary amounts
     * @param formatFactory the amount factory used for formatting of monetary amounts
     * @deprecated as of 0.11.0 in favor of {@link #withAmountFactory(MonetaryAmountFactory)} and
     * {@link #withFormatFactory(MonetaryAmountFormatFactory)}
     */
    @Deprecated
    public MoneyModule(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final MonetaryAmountFormatFactory formatFactory) {
        this(new DecimalNumberValueSerializer(), amountFactory, formatFactory, FieldNames.defaults());
    }

    private MoneyModule(final JsonSerializer<NumberValue> numberValueSerializer,
            final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final MonetaryAmountFormatFactory formatFactory, final FieldNames names) {

        this.numberValueSerializer = numberValueSerializer;
        this.amountFactory = amountFactory;
        this.formatFactory = formatFactory;
        this.names = names;
    }

    public MoneyModule withAmountFactory(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }

    public MoneyModule withFormatFactory(final MonetaryAmountFormatFactory formatFactory) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }

    public MoneyModule withFieldNames(final FieldNames names) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }

    public MoneyModule withNumberValueSerializer(final JsonSerializer<NumberValue> numberValueSerializer) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }
}
