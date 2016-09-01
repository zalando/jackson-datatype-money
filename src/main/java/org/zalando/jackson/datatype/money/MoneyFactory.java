package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public final class MoneyFactory implements MonetaryAmountFactory {

    @Override
    public MonetaryAmount create(final BigDecimal amount, final CurrencyUnit currency) {
        return Money.of(amount, currency);
    }

}
