## Migrating from zalando/jackson-datatype-money to FasterXML/jackson-datatypes-misc

This document provides guidance on how to migrate from the `zalando/jackson-datatype-money` module to the new
`FasterXML/jackson-datatypes-misc` module.

# Changes in Dependency

Replace the dependency in your `pom.xml` or build configuration from:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>jackson-datatype-money</artifactId>
    <version>1.3.0</version>
</dependency>
```

to:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-moneta</artifactId>
    <version>{{pick-recent}}</version>
</dependency>
```

(This migration guide was validated for the 2.19.0 version of `jackson-datatype-moneta` module)

# Changes in ObjectMapper Configuration

Replace the registration of the `MoneyModule` with the `MonetaMoneyModule`:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.moneta.MonetaMoneyModule;

ObjectMapper mapper = JsonMapper.builder()
        .addModule(new MonetaMoneyModule())
        .build();
```

All the customization options available in the `MonetaMoneyModule` are similar to those in the `MoneyModule`, so you can
continue to use them as before.

| MoneyModule Method                             | MonetaMoneyModule Method                       |
|------------------------------------------------|------------------------------------------------|
| `withDecimalNumbers()`                         | `withDecimalNumbers()`                         |
| `withQuotedDecimalNumbers()`                   | `withQuotedDecimalNumbers()`                   |
| `withNumbers(AmountWriter<?> writer)`          | `withNumbers(AmountWriter<?> writer)`          |
| `withFastMoney()`                              | `withFastMoney()`                              |
| `withMoney()`                                  | `withMoney()`                                  |
| `withRoundedMoney()`                           | `withRoundedMoney()`                           |
| `withRoundedMoney(MonetaryOperator rounding)`  | `withRoundedMoney(MonetaryOperator rounding)`  |
| `withMonetaryAmount(MonetaryAmountFactory<?>)` | `withMonetaryAmount(MonetaryAmountFactory<?>)` |
| `withoutFormatting()`                          | `withoutFormatting()`                          |
| `withDefaultFormatting()`                      | `withDefaultFormatting()`                      |
| `withFormatting(MonetaryAmountFormatFactory)`  | `withFormatting(MonetaryAmountFormatFactory)`  |
| `withAmountFieldName(String name)`             | `withAmountFieldName(String name)`             |
| `withCurrencyFieldName(String name)`           | `withCurrencyFieldName(String name)`           |
| `withFormattedFieldName(String name)`          | `withFormattedFieldName(String name)`          |
| `withFieldNames(FieldNames names)`             | `withFieldNames(FieldNames names)`             |

*Table: Method compatibility between MoneyModule and MonetaMoneyModule.*
