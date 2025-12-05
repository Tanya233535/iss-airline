package com.example.issairline.service;

public enum MaintenanceType {

    A_CHECK(500, 60),
    B_CHECK(0, 240),
    C_CHECK(5000, 540),
    D_CHECK(0, 3650);

    public final int flightHoursInterval;
    public final int daysInterval;

    MaintenanceType(int hours, int days) {
        this.flightHoursInterval = hours;
        this.daysInterval = days;
    }
}
