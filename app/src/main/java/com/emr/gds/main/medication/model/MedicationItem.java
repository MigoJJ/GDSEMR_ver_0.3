package com.emr.gds.main.medication.model;

public class MedicationItem {
    private String text;

    public MedicationItem(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
