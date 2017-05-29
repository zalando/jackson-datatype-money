package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.RoundedMoney;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryOperator;
import javax.money.NumberValue;
import java.math.BigDecimal;

final class RoundedMoneyFactory implements MonetaryAmountFactory<RoundedMoney> {
    
    private final MonetaryOperator rounding;

    RoundedMoneyFactory(final MonetaryOperator rounding) {
        this.rounding = rounding;
    }

    @Override
    public RoundedMoney create(final NumberValue amount, final CurrencyUnit currency) {
        return RoundedMoney.of(amount, currency, rounding);
    }

}
