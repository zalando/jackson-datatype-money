package org.zalando.jackson.datatype.money;

/*
 * ⁣​
 * Jackson-datatype-Money
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import org.javamoney.moneta.RoundedMoney;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import java.math.BigDecimal;

public final class RoundedMoneyFactory implements MonetaryAmountFactory {
    
    private final MonetaryOperator rounding;

    public RoundedMoneyFactory() {
        this(Monetary.getDefaultRounding());
    }

    public RoundedMoneyFactory(MonetaryOperator rounding) {
        this.rounding = rounding;
    }

    @Override
    public MonetaryAmount create(final BigDecimal amount, final CurrencyUnit currency) {
        return RoundedMoney.of(amount, currency, rounding);
    }

}
