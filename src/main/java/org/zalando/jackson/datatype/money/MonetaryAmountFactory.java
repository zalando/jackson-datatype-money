package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import java.math.BigDecimal;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface MonetaryAmountFactory<M extends MonetaryAmount> {

    M create(BigDecimal amount, CurrencyUnit currency);

}
