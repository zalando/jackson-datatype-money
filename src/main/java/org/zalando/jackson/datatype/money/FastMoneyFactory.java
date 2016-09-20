package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.FastMoney;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;

public final class FastMoneyFactory implements MonetaryAmountFactory<FastMoney> {

    @Override
    public FastMoney create(final BigDecimal amount, final CurrencyUnit currency) {
        return FastMoney.of(amount, currency);
    }

}
