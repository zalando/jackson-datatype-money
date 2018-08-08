package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.money.MonetaryAmount;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

// TODO create interface for BigDecimal with a default implementation for getType() as soon as we drop Java 7 support
@API(status = EXPERIMENTAL)
public interface AmountWriter<T> {

    Class<T> getType();

    T write(MonetaryAmount amount);

}
