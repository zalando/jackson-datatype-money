package org.zalando.jackson.datatype.money;

import org.apiguardian.api.API;

import javax.money.MonetaryAmount;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface AmountWriter<T> {

    Class<T> getType();

    T write(MonetaryAmount amount);

}
