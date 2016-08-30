package org.zalando.jackson.datatype.money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public interface MonetaryAmountFactory {

    MonetaryAmount create(final BigDecimal amount, final CurrencyUnit currency);

}
