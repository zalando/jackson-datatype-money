package org.zalando.jackson.datatype.money;

import javax.money.MonetaryAmount;

final class QuotedDecimalAmountWriter implements AmountWriter<String> {

    private final DecimalAmountWriter delegate = new DecimalAmountWriter();

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String write(final MonetaryAmount amount) {
        return delegate.write(amount).toPlainString();
    }

}
