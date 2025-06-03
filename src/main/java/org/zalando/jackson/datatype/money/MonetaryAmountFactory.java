package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * @deprecated This module is deprecated. Please use
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/blob/2.x/javax-money/src/main/java/com/fasterxml/jackson/datatype/javax/money/MonetaryAmountFactory.java">MonetaryAmountFactory</a> instead.
 */
@Deprecated
@API(status = DEPRECATED)
@FunctionalInterface
public interface MonetaryAmountFactory<M extends MonetaryAmount> {

    M create(BigDecimal amount, CurrencyUnit currency);

}
