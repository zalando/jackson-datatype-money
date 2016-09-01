package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.FastMoney;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public final class FastMoneyFactory implements MonetaryAmountFactory {

    @Override
    public MonetaryAmount create(final BigDecimal amount, final CurrencyUnit currency) {
        return FastMoney.of(amount, currency);
    }

}
