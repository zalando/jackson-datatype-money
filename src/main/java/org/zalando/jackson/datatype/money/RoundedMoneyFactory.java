package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.RoundedMoney;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import java.math.BigDecimal;

public final class RoundedMoneyFactory implements MonetaryAmountFactory {
    
    private final MonetaryOperator rounding;

    public RoundedMoneyFactory() {
        this(Monetary.getDefaultRounding());
    }

    public RoundedMoneyFactory(final MonetaryOperator rounding) {
        this.rounding = rounding;
    }

    @Override
    public MonetaryAmount create(final BigDecimal amount, final CurrencyUnit currency) {
        return RoundedMoney.of(amount, currency, rounding);
    }

}
