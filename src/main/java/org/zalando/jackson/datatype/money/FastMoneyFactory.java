package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.FastMoney;

import javax.money.CurrencyUnit;
import javax.money.NumberValue;
import java.math.BigDecimal;

public final class FastMoneyFactory implements MonetaryAmountFactory<FastMoney> {

    @Override
    public FastMoney create(final NumberValue amount, final CurrencyUnit currency) {
        return FastMoney.of(amount, currency);
    }

}
