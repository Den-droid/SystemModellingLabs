package org.example.hospital.models;

public class Patient {
    private int type;
    private final double timeStart;
    private double timeEnd;

    public Patient(double typeProbability, double timeStart) {
        if (typeProbability < 0.5)
            this.type = 1;
        else if (typeProbability < 0.6)
            this.type = 2;
        else
            this.type = 3;

        this.timeStart = timeStart;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }
}
