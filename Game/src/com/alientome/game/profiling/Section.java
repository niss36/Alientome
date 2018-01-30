package com.alientome.game.profiling;

public class Section {

    private final ExecutionTimeDataCruncher dataCruncher = new ExecutionTimeDataCruncher();
    private long lastTimestamp;
    private boolean isOpen;

    void start() {

        lastTimestamp = System.nanoTime();
        isOpen = true;
    }

    void end() {

        if (isOpen) {

            long elapsed = System.nanoTime() - lastTimestamp;
            dataCruncher.addEntry(elapsed);
            isOpen = false;
        }
    }

    String dumpSectionData() {

        dataCruncher.compute();
        String data = dataCruncher.toString();

        dataCruncher.reset();

        return data;
    }
}
