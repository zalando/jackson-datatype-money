package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.apiguardian.api.API;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.MonetaryRounding;
import javax.money.format.MonetaryFormats;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @deprecated This module is deprecated. Please use
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/tree/2.x/moneta">MonetaMoneyModule</a> instead.
 */
@API(status = DEPRECATED)
public final class MoneyModule extends Module {

    private final AmountWriter<?> writer;
    private final FieldNames names;
    private final MonetaryAmountFormatFactory formatFactory;
    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final MonetaryAmountFactory<FastMoney> fastMoneyFactory;
    private final MonetaryAmountFactory<Money> moneyFactory;
    private final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory;

    public MoneyModule() {
        this(new DecimalAmountWriter(), FieldNames.defaults(), MonetaryAmountFormatFactory.NONE,
                Money::of, FastMoney::of, Money::of, RoundedMoney::of);
    }

    private MoneyModule(final AmountWriter<?> writer,
            final FieldNames names,
            final MonetaryAmountFormatFactory formatFactory,
            final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final MonetaryAmountFactory<FastMoney> fastMoneyFactory,
            final MonetaryAmountFactory<Money> moneyFactory,
            final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory) {

        this.writer = writer;
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
        serializers.addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer(names, writer, formatFactory));
        context.addSerializers(serializers);

        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        deserializers.addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory, names));
        // for reading into concrete implementation types
        deserializers.addDeserializer(Money.class, new MonetaryAmountDeserializer<>(moneyFactory, names));
        deserializers.addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(fastMoneyFactory, names));
        deserializers.addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(roundedMoneyFactory, names));
        context.addDeserializers(deserializers);
    }

    public MoneyModule withDecimalNumbers() {
        return withNumbers(new DecimalAmountWriter());
    }

    public MoneyModule withQuotedDecimalNumbers() {
        return withNumbers(new QuotedDecimalAmountWriter());
    }

    @API(status = DEPRECATED)
    public MoneyModule withNumbers(final AmountWriter<?> writer) {
        return new MoneyModule(writer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    /**
     * @see FastMoney
     * @return new {@link MoneyModule} using {@link FastMoney}
     */
    public MoneyModule withFastMoney() {
        return withMonetaryAmount(fastMoneyFactory);
    }

    /**
     * @see Money
     * @return new {@link MoneyModule} using {@link Money}
     */
    public MoneyModule withMoney() {
        return withMonetaryAmount(moneyFactory);
    }

    /**
     * @see RoundedMoney
     * @return new {@link MoneyModule} using {@link RoundedMoney}
     */
    public MoneyModule withRoundedMoney() {
        return withMonetaryAmount(roundedMoneyFactory);
    }

    /**
     * @see RoundedMoney
     * @param rounding the rounding operator
     * @return new {@link MoneyModule} using {@link RoundedMoney} with the given {@link MonetaryRounding}
     */
    public MoneyModule withRoundedMoney(final MonetaryOperator rounding) {
        final MonetaryAmountFactory<RoundedMoney> factory = (amount, currency) ->
                RoundedMoney.of(amount, currency, rounding);

        return new MoneyModule(writer, names, formatFactory, factory,
                fastMoneyFactory, moneyFactory, factory);
    }

    public MoneyModule withMonetaryAmount(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory) {
        return new MoneyModule(writer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MoneyModule withoutFormatting() {
        return withFormatting(MonetaryAmountFormatFactory.NONE);
    }

    public MoneyModule withDefaultFormatting() {
        return withFormatting(MonetaryFormats::getAmountFormat);
    }

    public MoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new MoneyModule(writer, names, formatFactory, amountFactory,
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
        return new MoneyModule(writer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

}
