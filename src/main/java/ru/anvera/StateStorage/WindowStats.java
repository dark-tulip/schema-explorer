package ru.anvera.StateStorage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WindowStats {

    private double[] stats;

    public WindowStats() {
        this.stats = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    }

    public WindowStats(double[] stats) {
        this.stats = stats;
    }

    public void setStats(double[] stats) {
        this.stats = stats;
    }

    public double[] getStats() { return stats; }

    @Override
    public String toString() {
        return String.format("WindowStats{sum=%f, count=%f, mean=%.2f, stdDev=%.2f}", getStats()[0], getStats()[1],
                getStats()[3], getStats()[4]);
    }
}
