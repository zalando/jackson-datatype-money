package org.zalando.jackson.datatype.money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;

public interface MonetaryAmountFactory<M extends MonetaryAmount> {

    M create(NumberValue amount, CurrencyUnit currency);

}
