package org.example.hospital;

import org.example.hospital.models.Patient;

public class Dispose extends Element {
    double totalPatientsTime;
    private Patient nextPatient;

    public Dispose() {
        super(0, Integer.MAX_VALUE, ChooseRouteBy.PROBABILITY);
    }

    public void inAct(Patient patient) {
        nextPatient = patient;

        outAct();
    }

    @Override
    public void outAct() {
        super.increaseQuantity();

        totalPatientsTime += getTcurr() - nextPatient.getTimeStart();
    }

    public double getTotalPatientsTime() {
        return totalPatientsTime;
    }
}
