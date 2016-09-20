package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;

public final class MoneyFactory implements MonetaryAmountFactory<Money> {

    @Override
    public Money create(final BigDecimal amount, final CurrencyUnit currency) {
        return Money.of(amount, currency);
    }

}
