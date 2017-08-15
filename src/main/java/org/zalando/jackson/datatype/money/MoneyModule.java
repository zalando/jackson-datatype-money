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

    private final JsonSerializer<? super NumberValue> numberValueSerializer;
    private final FieldNames names;
    private final MonetaryAmountFormatFactory formatFactory;
    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final FastMoneyFactory fastMoneyFactory;
    private final MoneyFactory moneyFactory;
    private final RoundedMoneyFactory roundedMoneyFactory;

    public MoneyModule() {
        this(new DecimalNumberValueSerializer(), FieldNames.defaults(), new NoopMonetaryAmountFormatFactory(),
                new MoneyFactory(), new FastMoneyFactory(), new MoneyFactory(),
                new RoundedMoneyFactory(Monetary.getDefaultRounding()));
    }

    private MoneyModule(final JsonSerializer<? super NumberValue> numberValueSerializer,
            final FieldNames names,
            final MonetaryAmountFormatFactory formatFactory,
            final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final FastMoneyFactory fastMoneyFactory,
            final MoneyFactory moneyFactory,
            final RoundedMoneyFactory roundedMoneyFactory) {

        this.numberValueSerializer = numberValueSerializer;
        this.names = names;
        this.formatFactory = formatFactory;
        this.amountFactory = amountFactory;
        this.fastMoneyFactory = fastMoneyFactory;
        this.moneyFactory = moneyFactory;
        this.roundedMoneyFactory = roundedMoneyFactory;
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
        deserializers.addDeserializer(Money.class, new MonetaryAmountDeserializer<>(moneyFactory, names));
        deserializers.addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(fastMoneyFactory, names));
        deserializers.addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(roundedMoneyFactory, names));

        context.addDeserializers(deserializers);
    }

    public MoneyModule withDecimalNumbers() {
        return withNumbers(new DecimalNumberValueSerializer());
    }

    public MoneyModule withQuotedDecimalNumbers() {
        return withNumbers(new QuotedDecimalNumberValueSerializer());
    }

    public MoneyModule withNumbers(final JsonSerializer<? super NumberValue> numberValueSerializer) {
        return new MoneyModule(numberValueSerializer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    /**
     * @see FastMoney
     * @return
     */
    public MoneyModule withFastMoney() {
        return withMonetaryAmount(fastMoneyFactory);
    }

    /**
     * @see Money
     * @return
     */
    public MoneyModule withMoney() {
        return withMonetaryAmount(moneyFactory);
    }

    /**
     * @see RoundedMoney
     * @return
     */
    public MoneyModule withRoundedMoney() {
        return withMonetaryAmount(roundedMoneyFactory);
    }

    /**
     * @see RoundedMoney
     * @return
     */
    public MoneyModule withRoundedMoney(final MonetaryOperator rounding) {
        final RoundedMoneyFactory factory = new RoundedMoneyFactory(rounding);
        return new MoneyModule(numberValueSerializer, names, formatFactory, factory,
                fastMoneyFactory, moneyFactory, factory);
    }

    public MoneyModule withMonetaryAmount(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory) {
        return new MoneyModule(numberValueSerializer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MoneyModule withoutFormatting() {
        return withFormatting(new NoopMonetaryAmountFormatFactory());
    }

    public MoneyModule withDefaultFormatting() {
        return withFormatting(new DefaultMonetaryAmountFormatFactory());
    }

    public MoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new MoneyModule(numberValueSerializer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MoneyModule withAmountFieldName(final String name) {
        return withFieldNames(names.withAmount(name));
    }

    public MoneyModule withCurrencyFieldName(final String name) {
        return withFieldNames(names.withCurrency(name));
    }

    public MoneyModule withFormattedFieldName(final String name) {
        return withFieldNames(names.withFormatted(name));
    }

    private MoneyModule withFieldNames(final FieldNames names) {
        return new MoneyModule(numberValueSerializer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

}
