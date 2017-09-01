package org.zalando.jackson.datatype.money;

import javax.money.MonetaryAmount;

interface AmountWriter {

    Object write(final MonetaryAmount amount);

}
