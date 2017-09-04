package org.zalando.jackson.datatype.money;

import javax.money.MonetaryAmount;

final class QuotedDecimalAmountWriter implements AmountWriter {

    private final DecimalAmountWriter delegate = new DecimalAmountWriter();

    @Override
    public String write(final MonetaryAmount amount) {
        return delegate.write(amount).toPlainString();
    }

}
