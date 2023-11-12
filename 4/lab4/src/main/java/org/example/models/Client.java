package org.example.models;

public class Client {
    private final double timeStart;
    private double timeEnd;

    public Client(double timeStart) {
        this.timeStart = timeStart;
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
