package com.reactive.currency.exchange.entity;

public class FromToRate {
    private String from;
    private String to;
    private Double rate;

    public FromToRate(String from, String to, Double rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "FromToRate{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", rate=" + rate +
                '}';
    }
}
