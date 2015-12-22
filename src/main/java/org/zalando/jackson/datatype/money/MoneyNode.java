package org.zalando.jackson.datatype.money;

/*
 * ⁣​
 * jackson-datatype-money
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;

final class MoneyNode {

    private final BigDecimal amount;

    private final CurrencyUnit currency;

    private final String formattedValue;

    @JsonCreator
    MoneyNode(@JsonProperty("amount") final BigDecimal amount,
            @JsonProperty("currency") final CurrencyUnit currency,
              @JsonProperty("formattedValue") final String formattedValue) {
        this.amount = amount;
        this.currency = currency;
        this.formattedValue = formattedValue;
    }

    @JsonGetter("amount")
    BigDecimal getAmount() {
        return amount;
    }

    @JsonGetter("currency")
    CurrencyUnit getCurrency() {
        return currency;
    }

    @JsonGetter("formattedValue")
    String getFormattedValue() { return formattedValue; }
}
