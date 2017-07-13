package com.ng.techhouse.tinggqr.util;

import java.util.regex.Pattern;

/**
 * Created by rabiu on 07/02/2017.
 */

public enum CardTypes {

    UNKNOWN,
    VISA("^4[0-9]{12}(?:[0-9]{3})?$"),
    MASTERCARD("^5[1-5][0-9]{14}$"),
    AMERICAN_EXPRESS("^3[47][0-9]{13}$"),
    DINERS_CLUB("^3(?:0[0-5]|[68][0-9])[0-9]{11}$"),
    DISCOVER("^6(?:011|5[0-9]{2})[0-9]{12}$"),
    JCB("^(?:2131|1800|35\\d{3})\\d{11}$");

    private Pattern pattern;

    CardTypes() {
        this.pattern = null;
    }

    CardTypes(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public static CardTypes detect(String cardNumber) {

        for (CardTypes cardTypes : CardTypes.values()) {
            if (null == cardTypes.pattern) continue;
            if (cardTypes.pattern.matcher(cardNumber).matches()) return cardTypes;
        }

        return UNKNOWN;
    }


}
