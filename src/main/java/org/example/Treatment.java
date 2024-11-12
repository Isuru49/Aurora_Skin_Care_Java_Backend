package org.example;

import java.util.HashMap;

public class Treatment {
    private static final HashMap<String, Double> treatments = new HashMap<>();

    static {
        treatments.put("Acne Treatment", 2750.00);
        treatments.put("Skin Whitening", 7650.00);
        treatments.put("Mole Removal", 3850.00);
        treatments.put("Laser Treatment", 12500.00);
    }

    public static double getTreatmentCost(String treatment) {
        return treatments.getOrDefault(treatment, 0.0);
    }

}
