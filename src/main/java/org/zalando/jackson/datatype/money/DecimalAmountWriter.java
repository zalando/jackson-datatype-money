package org.zalando.jackson.datatype.money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.math.RoundingMode;

final class DecimalAmountWriter implements AmountWriter {

    @Override
    public BigDecimal write(final MonetaryAmount amount) {
        final BigDecimal decimal = amount.getNumber().numberValueExact(BigDecimal.class);
        final int defaultFractionDigits = amount.getCurrency().getDefaultFractionDigits();
        final int scale = Math.max(decimal.scale(), defaultFractionDigits);

        return decimal.setScale(scale, RoundingMode.UNNECESSARY);
    }

}
