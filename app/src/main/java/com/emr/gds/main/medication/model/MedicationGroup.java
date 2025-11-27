package com.emr.gds.main.medication.model;

import java.util.List;

public record MedicationGroup(String title, List<MedicationItem> medications) {}
