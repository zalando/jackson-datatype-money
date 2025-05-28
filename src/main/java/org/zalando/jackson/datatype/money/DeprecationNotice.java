package org.zalando.jackson.datatype.money;

/**
 * This class exists purely to trigger compiler warnings when the library is used.
 * @deprecated This library is deprecated. Please migrate to
 * <a href="https://github.com/FasterXML/jackson-datatypes-misc/tree/2.x/moneta">MonetaMoneyModule</a> instead.
 */
@Deprecated
public class DeprecationNotice {
    static {
        System.err.println("WARNING: jackson-datatype-money is deprecated. " +
                "Please migrate to MonetaMoneyModule from jackson-datatypes-misc.");
    }
}