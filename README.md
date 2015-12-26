# Jackson Datatype Money

[Jackson](http://jackson.codehaus.org) module (jar)
to support JSON serialization and deserialization of
[javax.money](https://github.com/JavaMoney/jsr354-api) data types.

[![Build Status](https://img.shields.io/travis/zalando/jackson-datatype-money.svg)](https://travis-ci.org/zalando/jackson-datatype-money)
[![Coverage Status](https://img.shields.io/coveralls/zalando/jackson-datatype-money.svg)](https://coveralls.io/r/zalando/jackson-datatype-money)
[![Release](https://img.shields.io/github/release/zalando/jackson-datatype-money.svg)](https://github.com/zalando/jackson-datatype-money/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/jackson-datatype-money.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/jackson-datatype-money)

## Dependency

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>jackson-datatype-money</artifactId>
    <version>${jackson-datatype-money.version}</version>
</dependency>
```

This module has no direct dependency on Java Money, but rather requires that clients pick one in their project directly. This allows this module to be compatible with the official version as well as the backport of Java Money. Pick one of the following dependency sets:

### Java 7

```xml
<dependency>
    <groupId>javax.money</groupId>
    <artifactId>money-api-bp</artifactId>
    <version>${java-money.version}</version>
</dependency>
<dependency>
    <groupId>org.javamoney</groupId>
    <artifactId>moneta-bp</artifactId>
    <version>${java-money.version}</version>
</dependency>
```

### Java 8

```xml
<dependency>
    <groupId>javax.money</groupId>
    <artifactId>money-api</artifactId>
    <version>${java-money.version}</version>
</dependency>
<dependency>
    <groupId>org.javamoney</groupId>
    <artifactId>moneta</artifactId>
    <version>${java-money.version}</version>
</dependency>
```

## Usage

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule());
```

Or alternatively you can use the SPI capabilities:

```java
ObjectMapper mapper = new ObjectMapper()
    .findAndRegisterModules();
```

## Supported Types
The module supports de/serialization of the following types:

### [`java.util.Currency`](https://docs.oracle.com/javase/8/docs/api/java/util/Currency.html)

`Currency.getInstance("EUR")` produces an [ISO-4217](http://en.wikipedia.org/wiki/ISO_4217) currency code, e.g. `"EUR"`.

### [`javax.money.CurrencyUnit`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/CurrencyUnit.java)

`Monetary.getCurrency("EUR")` produces an [ISO-4217](http://en.wikipedia.org/wiki/ISO_4217) currency code, e.g. `"EUR"`.

### [`javax.money.MonetaryAmount`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/MonetaryAmount.java)

By default the `MoneyModule` will use `org.javamoney.moneta.Money` as an implementation for `javax.money.MonetaryAmount` when deserializing money values. If you need a different implementation you can pass in an implementation of `MonetaryAmountFactory` to the `MoneyModule`:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule(new FastMoneyFactory()));
```

| `MonetaryAmount` Implementation     | Factory                                                                                                                         |
|-------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| `org.javamoney.moneta.FastMoney`    | [`org.zalando.jackson.datatype.money.FastMoneyFactory`](src/main/java/org/zalando/jackson/datatype/money/FastMoneyFactory.java) |
| `org.javamoney.moneta.Money`        | [`org.zalando.jackson.datatype.money.MoneyFactory`](src/main/java/org/zalando/jackson/datatype/money/MoneyFactory.java)         |
| `org.javamoney.moneta.RoundedMoney` | n/a                                                                                                                             |

In case you're using Java 8, you can also just pass in a method reference:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule(FastMoney::of));
```

`Money.of(29.95, "EUR")` produces the following structure:

```json
{
  "amount": 29.95, 
  "currency": "EUR"
}
```

## Formatting

Formatting of monetary amounts is **disabled by default**. To enable it you have to pass in a `MonetaryAmountFormatFactory` implementation to the `MoneyModule`:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule(new DefaultMonetaryAmountFormatFactory()));
```

The `DefaultMonetaryAmountFormatFactory` delegates directly to `MonetaryFormats.getAmountFormat(Locale, String...)`.

Formatting only affects the serialization and can be customized based on the *current* locale, as defined by the [`SerializationConfig`](http://wiki.fasterxml.com/SerializationConfig). This allows to implement RESTful API endpoints that format monetary amounts based on the `Accept-Language` header.

The first example serializes a monetary amount using the `de_DE` locale:

```java
ObjectWriter writer = mapper.writer().with(Locale.GERMANY);
writer.writeValueAsString(Money.of(29.95 "EUR");
```

```json
{
  "amount": 29.95, 
  "currency": "EUR",
  "formatted": "29,95 EUR"
}
```

The following examples uses `en_US`:

```java
ObjectWriter writer = mapper.writer().with(Locale.US);
writer.writeValueAsString(Money.of(29.95 "USD");
```

```json
{
  "amount": 29.95, 
  "currency": "USD",
  "formatted": "USD29.95"
}
```

## License

Copyright [2015] Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
