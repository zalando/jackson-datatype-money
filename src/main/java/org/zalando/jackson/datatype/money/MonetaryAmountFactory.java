package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public interface MonetaryAmountFactory<M extends MonetaryAmount> {

    M create(NumberValue amount, CurrencyUnit currency);

}
