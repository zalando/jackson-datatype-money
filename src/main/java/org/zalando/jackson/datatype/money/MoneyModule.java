package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.util.Currency;

import static com.fasterxml.jackson.core.util.VersionUtil.mavenVersionFor;

public final class MoneyModule extends SimpleModule {

    public MoneyModule() {
        this(new MoneyFactory());
    }

    public MoneyModule(final MonetaryAmountFactory factory) {
        this(factory, new NoopMonetaryAmountFormatFactory());
    }
    
    public MoneyModule(final MonetaryAmountFormatFactory factory) {
        this(new MoneyFactory(), factory);
    }

    public MoneyModule(final MonetaryAmountFactory amountFactory, final MonetaryAmountFormatFactory formatFactory) {
        super(MoneyModule.class.getSimpleName(),
                mavenVersionFor(MoneyModule.class.getClassLoader(), "org.zalando", "jackson-datatype-money"));
        
        addSerializer(Currency.class, new CurrencySerializer());
        addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer(formatFactory));
        
        addDeserializer(Currency.class, new CurrencyDeserializer());
        addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer(amountFactory));
    }

}
