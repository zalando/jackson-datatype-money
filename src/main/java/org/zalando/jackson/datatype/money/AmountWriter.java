package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.money.MonetaryAmount;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @deprecated This module is deprecated. Please use
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/blob/2.x/javax-money/src/main/java/com/fasterxml/jackson/datatype/javax/money/AmountWriter.java">AmountWriter</a> instead.
 */
@Deprecated
@API(status = DEPRECATED)
public interface AmountWriter<T> {

    Class<T> getType();

    T write(MonetaryAmount amount);

}
