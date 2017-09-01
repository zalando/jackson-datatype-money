package org.zalando.jackson.datatype.money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.text.DecimalFormat;

final class QuotedDecimalAmountWriter implements AmountWriter {

    @Override
    public String write(final MonetaryAmount amount) {
        final BigDecimal decimal = amount.getNumber().numberValueExact(BigDecimal.class);
        final int defaultFractionDigits = amount.getCurrency().getDefaultFractionDigits();
        final int scale = Math.max(decimal.scale(), defaultFractionDigits);

        final DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(defaultFractionDigits);
        format.setMaximumFractionDigits(scale);
        format.setGroupingUsed(false);

        return format.format(decimal);
    }

}
