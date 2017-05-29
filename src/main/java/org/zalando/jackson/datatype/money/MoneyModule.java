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
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.NumberValue;

public final class MoneyModule extends Module {

    private final JsonSerializer<NumberValue> numberValueSerializer;
    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final MonetaryAmountFormatFactory formatFactory;
    private final FieldNames names;

    public MoneyModule() {
        this(new DecimalNumberValueSerializer(), new MoneyFactory(), new NoopMonetaryAmountFormatFactory(),
                FieldNames.defaults());
    }

    private MoneyModule(final JsonSerializer<NumberValue> numberValueSerializer,
            final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final MonetaryAmountFormatFactory formatFactory, final FieldNames names) {

        this.numberValueSerializer = numberValueSerializer;
        this.amountFactory = amountFactory;
        this.formatFactory = formatFactory;
        this.names = names;
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

        serializers.addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        serializers.addSerializer(NumberValue.class, numberValueSerializer);
        serializers.addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer(formatFactory, names));

        context.addSerializers(serializers);

        final SimpleDeserializers deserializers = new SimpleDeserializers();

        deserializers.addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        deserializers.addDeserializer(NumberValue.class, new DecimalNumberValueDeserializer());
        deserializers.addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory, names));

        // for reading into concrete implementation types
        deserializers.addDeserializer(Money.class, new MonetaryAmountDeserializer<>(new MoneyFactory(), names));
        deserializers.addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(new FastMoneyFactory(), names));
        deserializers.addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(new RoundedMoneyFactory(Monetary.getDefaultRounding()), names));

        context.addDeserializers(deserializers);
    }

    public MoneyModule withDecimalNumbers() {
        return withNumbers(new DecimalNumberValueSerializer());
    }

    public MoneyModule withQuotedDecimalNumbers() {
        return withNumbers(new QuotedDecimalNumberValueSerializer());
    }

    public MoneyModule withNumbers(final JsonSerializer<NumberValue> numberValueSerializer) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }

    /**
     * @see FastMoney
     * @return
     */
    public MoneyModule withFastMoney() {
        return withMonetaryAmount(new FastMoneyFactory());
    }

    /**
     * @see Money
     * @return
     */
    public MoneyModule withMoney() {
        return withMonetaryAmount(new MoneyFactory());
    }

    /**
     * @see RoundedMoney
     * @return
     */
    public MoneyModule withRoundedMoney() {
        return withRoundedMoney(Monetary.getDefaultRounding());
    }

    /**
     * @see RoundedMoney
     * @return
     */
    public MoneyModule withRoundedMoney(final MonetaryOperator rounding) {
        return withMonetaryAmount(new RoundedMoneyFactory(rounding));
    }

    public MoneyModule withMonetaryAmount(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }

    public MoneyModule withoutFormatting() {
        return withFormatting(new NoopMonetaryAmountFormatFactory());
    }

    public MoneyModule withDefaultFormatting() {
        return withFormatting(new DefaultMonetaryAmountFormatFactory());
    }

    public MoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }

    public MoneyModule withFieldNames(final FieldNames names) {
        return new MoneyModule(numberValueSerializer, amountFactory, formatFactory, names);
    }

}
