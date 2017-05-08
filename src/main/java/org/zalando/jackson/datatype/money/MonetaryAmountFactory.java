package org.zalando.jackson.datatype.money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public interface MonetaryAmountFactory<M extends MonetaryAmount> {

    M create(BigDecimal amount, CurrencyUnit currency);

}
