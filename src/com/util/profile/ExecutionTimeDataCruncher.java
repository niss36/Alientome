package com.util.profile;

class ExecutionTimeDataCruncher {

    private static final int MILLIS_TO_NANOS_RATIO = 1_000_000;

    private int entries;
    private long currentMin = Long.MAX_VALUE;
    private long currentMax = Long.MIN_VALUE;
    private double currentAverage;
    private double average;
    private double minimum;
    private double maximum;

    void addEntry(long value) {

        entries++;
        if (value < currentMin) currentMin = value;
        if (value > currentMax) currentMax = value;
        currentAverage += (value - currentAverage) / entries;
    }

    void compute() {

        average = currentAverage / MILLIS_TO_NANOS_RATIO;
        minimum = (double) currentMin / MILLIS_TO_NANOS_RATIO;
        maximum = (double) currentMax / MILLIS_TO_NANOS_RATIO;
    }

    void reset() {

        entries = 0;
        currentMin = Long.MAX_VALUE;
        currentMax = Long.MIN_VALUE;
        currentAverage = 0;
        average = 0;
        minimum = 0;
        maximum = 0;
    }

    @Override
    public String toString() {
        return entries == 0 ? "No data" :
                String.format("%s Entries; Average=%sms, Minimum=%sms, Maximum=%sms", entries, average, minimum, maximum);
    }
}
