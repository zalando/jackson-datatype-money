package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.util.Currency;

import static com.fasterxml.jackson.core.util.VersionUtil.mavenVersionFor;

public final class MoneyModule extends SimpleModule {

    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final MonetaryAmountFormatFactory formatFactory;
    private final FieldNames names;

    @SuppressWarnings("deprecation")
    public MoneyModule() {
        this(new MoneyFactory());
    }

    /**
     * @deprecated use {@link #withAmountFactory(MonetaryAmountFactory)}
     * @param factory the amount factory used for deserialization of monetary amounts
     */
    @Deprecated
    public MoneyModule(final MonetaryAmountFactory<? extends MonetaryAmount> factory) {
        this(factory, new NoopMonetaryAmountFormatFactory());
    }

    /**
     * @deprecated use {@link #withFormatFactory(MonetaryAmountFormatFactory)}
     * @param factory the amount factory used for formatting of monetary amounts
     */
    @Deprecated
    public MoneyModule(final MonetaryAmountFormatFactory factory) {
        this(new MoneyFactory(), factory);
    }

    /**
     * @deprecated use {@link #withAmountFactory(MonetaryAmountFactory)} and {@link #withFormatFactory(MonetaryAmountFormatFactory)}
     * @param amountFactory the amount factory used for deserialization of monetary amounts
     * @param formatFactory the amount factory used for formatting of monetary amounts
     */
    @Deprecated
    public MoneyModule(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final MonetaryAmountFormatFactory formatFactory) {
        this(amountFactory, formatFactory, FieldNames.defaults());
    }

    private MoneyModule(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final MonetaryAmountFormatFactory formatFactory, FieldNames names) {
        super(MoneyModule.class.getSimpleName(), getVersion());

        this.amountFactory = amountFactory;
        this.formatFactory = formatFactory;
        this.names = names;

        addSerializer(Currency.class, new CurrencySerializer());
        addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer(formatFactory, names));
        
        addDeserializer(Currency.class, new CurrencyDeserializer());
        addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory, names));

        addDeserializer(Money.class, new MonetaryAmountDeserializer<>(new MoneyFactory(), names));
        addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(new FastMoneyFactory(), names));
        addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(new RoundedMoneyFactory(), names));
    }

    public MoneyModule withAmountFactory(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory) {
        return new MoneyModule(amountFactory, formatFactory, names);
    }

    public MoneyModule withFormatFactory(final MonetaryAmountFormatFactory formatFactory) {
        return new MoneyModule(amountFactory, formatFactory, names);
    }

    public MoneyModule withFieldNames(final FieldNames names) {
        return new MoneyModule(amountFactory, formatFactory, names);
    }

    @SuppressWarnings("deprecation")
    private static Version getVersion() {
        return mavenVersionFor(MoneyModule.class.getClassLoader(), "org.zalando", "jackson-datatype-money");
    }

}
