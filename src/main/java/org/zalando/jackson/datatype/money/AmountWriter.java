package org.zalando.jackson.datatype.money;

import javax.money.MonetaryAmount;

interface AmountWriter<T> {

    Class<T> getType();

    T write(final MonetaryAmount amount);

}
