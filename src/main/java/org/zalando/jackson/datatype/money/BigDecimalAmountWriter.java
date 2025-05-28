package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import java.math.BigDecimal;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @deprecated This module is deprecated. Please use
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/blob/2.x/javax-money/src/main/java/com/fasterxml/jackson/datatype/javax/money/BigDecimalAmountWriter.java">BigDecimalAmountWriter</a> instead.
 */
@Deprecated
@API(status = DEPRECATED)
public interface BigDecimalAmountWriter extends AmountWriter<BigDecimal> {

    @Override
    default Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

}
