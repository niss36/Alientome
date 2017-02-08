package com.util.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ExecutionTimeProfiler {

    public static final ExecutionTimeProfiler theProfiler = new ExecutionTimeProfiler();

    private final Map<String, Section> sectionMap = new HashMap<>(16);

    public void startSection(String sectionName) {

        Section section = sectionMap.computeIfAbsent(sectionName, k -> new Section());
        section.start();
    }

    public void endSection(String sectionName) {

        Section section = sectionMap.get(sectionName);
        section.end();
    }

    public void dumpProfileData() {

        StringBuilder builder = new StringBuilder("Section data :\n");

        Map<String, Section> sortedMap = new TreeMap<>(sectionMap);

        for (Map.Entry<String, Section> sectionEntry : sortedMap.entrySet()) {

            String sectionName = sectionEntry.getKey();
            String data = sectionEntry.getValue().dumpSectionData();

            int nestLevel = sectionName.length() - sectionName.replace("/", "").length(); // Count occurrences of '/'
            if (nestLevel > 0) sectionName = sectionName.substring(sectionName.lastIndexOf('/') + 1);

            for (int i = -1; i < nestLevel; i++) builder.append('\t');

            builder.append(sectionName).append(" : ").append(data).append('\n');
        }

        System.out.println(builder);
    }
}
