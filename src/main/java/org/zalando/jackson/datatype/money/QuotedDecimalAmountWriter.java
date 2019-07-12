package org.zalando.jackson.datatype.money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

final class QuotedDecimalAmountWriter implements AmountWriter<String> {

    private final AmountWriter<BigDecimal> delegate = new DecimalAmountWriter();

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String write(final MonetaryAmount amount) {
        return delegate.write(amount).toPlainString();
    }

}
