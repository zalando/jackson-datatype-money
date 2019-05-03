# Representing Money in JSON

> A large proportion of the computers in this world manipulate money, so it's always puzzled me that money isn't actually a first class data type in any mainstream programming language.
>
> [Martin Fowler](https://martinfowler.com/eaaCatalog/money.html)

Unfortunately JSON is no different. This document tries to change that by proposing and comparing different styles to represent money, some inspired by external sources and some based on our own experience.

## ⚠️ Monetary amounts ≠ floats

Before we dive into details, always keep the following in mind. However you desire to format money in JSON, nothing changes the fact that you should...

> **Never hold monetary values [..] in a float variable.** Floating point is not suitable for this work, and you must use either [fixed-point](#fixed-point) or [decimal](#decimal) values.
>  
> [Coinkite: Common Terms and Data Objects](https://web.archive.org/web/20150924073850/https://docs.coinkite.com/api/common.html)

## Styles

We identified the following styles that all of different advantages and disadvantages that are discussed in their respective section.

| Style                              | Expressive | Arithmetic | Pitfalls / Misuses |
|------------------------------------|------------|------------|--------------------|
| [Decimal](#decimal)                | ✔          | ✔          | Precision          |
| [Quoted Decimal](#quoted-decimal)  | ✔          | ✘          | Parsing            |
| [Fixed Point](#fixed-point)        | ✘          | ✔          | Mixed scales       |
| [Mixed](#mixed)                    | ✘          | ✔          | Consistency        |

### Decimal

The most straightforward way to represent a monetary amount would be a base-10 decimal number:

```json
{
  "amount": 49.95,
  "currency": "EUR"
}
```

It's expressive, readable and allows arithmetic operations. The downside is that most [JSON decoders will treat it as a floating point](https://tools.ietf.org/html/rfc7159#section-6) number which is very much undesirable.

Most programming languages have support for arbitrary-precision [decimals](#decimal-implementations) and JSON decoders that can be configured to use them. In general it can be considered to be a problem of the implementation, not the format itself.

### Quoted Decimal

Same as  [Decimal](#decimal) but quoted so your JSON decoder treats it as a string:

```json
{
  "amount": "49.95",
  "currency": "EUR"
}
```

It solves the precision problem of decimals on the expense of performing arithmetic operations on it. It also requires a two-phase parsing, i.e. parsing the JSON text into a data structure and then parsing quoted amounts into decimals.

### Fixed Point

> A value of a fixed-point data type is essentially an integer that is scaled by an implicit specific factor determined by the type.
>
> [Wikipedia: Fixed-point arithmetic](https://en.wikipedia.org/wiki/Fixed-point_arithmetic)

```json
{
  "amount": 4995,
  "currency": "EUR"
}
```

The implicit scaling factor is defined as (0.1 raised to the power of) the currency's [default number of fraction digits](http://www.localeplanet.com/icu/currency.html).
 
In rare cases one might need a higher precision, e.g. to have sub-cent. In this case the scale can be defined explicitly:

```json
{
  "amount": 499599,
  "currency": "EUR",
  "scale": 4
}
```

The downside with fixed-point amounts is that reading them is a bit harder and arithmetic with mixed scale amounts can be tricky and error-prone. 

### Mixed

As a way to counter all negative aspects of the styles above one idea would be to have a single object that contains all of the formats:

```json
{
  "decimal": 49.95,
  "quoted_decimal": "49.95",
  "fixed_point": 4995,
  "scale": 2,
  "currency": "EUR"
}
```

Decoders can choose the representation that fits them the best. Encoders on the other hand have the harder task by providing all of them and making sure that all values are in fact consistent.

## Decimal Implementations

| Language   | Implementation                                                                              |
|------------|---------------------------------------------------------------------------------------------|
| C#         | [decimal](https://msdn.microsoft.com/en-us/library/364x0z75.aspx)                           |
| Java       | [java.math.BigDecimal](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html) |
| JavaScript | [decimal.js](https://github.com/MikeMcl/decimal.js/)                                        |
| Python     | [decimal.Decimal](https://docs.python.org/2/library/decimal.html)                           |

## Credits and References

- [Coinkite: Currency Amounts](https://web.archive.org/web/20150924073850/https://docs.coinkite.com/api/common.html#currency-amounts)
- [Culttt: How to handle money and currency in web applications](http://culttt.com/2014/05/28/handle-money-currency-web-applications/)
- [Currency codes - ISO 4217](https://www.iso.org/iso-4217-currency-codes.html)
- [LocalePlanet: ICU Currencies](http://www.localeplanet.com/icu/currency.html)
- [RFC 7159:  The JavaScript Object Notation (JSON) Data Interchange Format](https://tools.ietf.org/html/rfc7159#section-6)
- [Stackoverflow: What is the standard for formatting currency values in JSON?](http://stackoverflow.com/questions/30249406/what-is-the-standard-for-formatting-currency-values-in-json)
- [Stackoverflow: Why not use Double or Float to represent currency?](http://stackoverflow.com/questions/3730019/why-not-use-double-or-float-to-represent-currency/3730040#3730040)
- [TechEmpower: Mangling JSON numbers](https://www.techempower.com/blog/2016/07/05/mangling-json-numbers/)
- [Wikipedia: Fixed-point arithmetic](https://en.wikipedia.org/wiki/Fixed-point_arithmetic)
