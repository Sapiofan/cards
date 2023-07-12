package com.sapiofan.cards.services;

public enum Period {
    TWENTY_MINUTES(1200), HOUR(3600), NINE_HOURS(32400), DAY(86400), THREE_DAYS(259200),
    WEEK(604800), MONTH(2419200), TWO_MONTH(4838400), HALF_YEAR(14515200);

    private final int seconds;

    Period(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }
}
