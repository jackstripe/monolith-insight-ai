package com.monolithinsight.domain;

import java.util.List;

public record HotspotsReport(
        List<ClassHotspot> hotspots
) {

    public HotspotsReport {
        hotspots = List.copyOf(hotspots);
    }
}