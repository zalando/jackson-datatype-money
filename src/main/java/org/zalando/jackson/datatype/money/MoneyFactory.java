package org.zalando.jackson.datatype.money;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.NumberValue;

final class MoneyFactory implements MonetaryAmountFactory<Money> {

    @Override
    public Money create(final NumberValue amount, final CurrencyUnit currency) {
        return Money.of(amount, currency);
    }

}
