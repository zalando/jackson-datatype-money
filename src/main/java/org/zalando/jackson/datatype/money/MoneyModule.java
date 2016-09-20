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

    public MoneyModule() {
        this(new MoneyFactory());
    }

    public MoneyModule(final MonetaryAmountFactory<? extends MonetaryAmount> factory) {
        this(factory, new NoopMonetaryAmountFormatFactory());
    }
    
    public MoneyModule(final MonetaryAmountFormatFactory factory) {
        this(new MoneyFactory(), factory);
    }

    public MoneyModule(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
            final MonetaryAmountFormatFactory formatFactory) {
        super(MoneyModule.class.getSimpleName(),
                getVersion());
        
        addSerializer(Currency.class, new CurrencySerializer());
        addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer(formatFactory));
        
        addDeserializer(Currency.class, new CurrencyDeserializer());
        addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory));

        addDeserializer(Money.class, new MonetaryAmountDeserializer<>(new MoneyFactory()));
        addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(new FastMoneyFactory()));
        addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(new RoundedMoneyFactory()));
    }

    @SuppressWarnings("deprecation")
    private static Version getVersion() {
        return mavenVersionFor(MoneyModule.class.getClassLoader(), "org.zalando", "jackson-datatype-money");
    }

}
