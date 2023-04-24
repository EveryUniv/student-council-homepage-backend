package com.dku.council.domain.user.model;

public enum AcademicStatus {
    ATTENDING("재학"),
    GRADUATE("졸업"),
    TAKEOFF("휴학");

    private final String label;

    AcademicStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
