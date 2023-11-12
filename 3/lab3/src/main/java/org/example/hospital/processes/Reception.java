package org.example.hospital.processes;

import org.example.hospital.ChooseRouteBy;
import org.example.hospital.Element;
import org.example.hospital.Process;
import org.example.hospital.models.Patient;

import java.util.HashMap;
import java.util.Map;

public class Reception extends Process {
    private Map<Integer, Element> nextElementsByType;
    private Map<Integer, Double> delaysByPatientType;

    public Reception(int workersCount, ChooseRouteBy chooseRouteBy) {
        super(0, workersCount, chooseRouteBy);
        this.nextElementsByType = new HashMap<>();
        this.delaysByPatientType = new HashMap<>();
    }

    public void inAct(Patient patient) {
        if (super.getState() < getWorkersCount()) {
            super.setState(super.getState() + 1);

            nextPatients.add(patient);
            int type = patient.getType();

            addTimeNext(super.getTcurr() + getDelay(type));
        } else {
            if (getQueue() < getMaxqueue()) {
                patientsQueue.add(patient);
            } else {
                setFailure(getFailure() + 1);
            }
        }
    }

    @Override
    public void outAct() {
        super.increaseQuantity();
        super.setState(super.getState() - 1);
        removeTimeNext();

        if (getQueue() > 0) {
            Patient patient = patientsQueue.poll();
            nextPatients.add(patient);

            super.setState(super.getState() + 1);
            addTimeNext(super.getTcurr() + super.getDelay());
        }

        Patient patient = super.pollPatient();
        Element nextElement = nextElementsByType.get(patient.getType());

        if (nextElement instanceof Process process) {
            process.inAct(patient);
        }
    }

    public void addElementByType(int type, Element element) {
        nextElementsByType.put(type, element);
    }

    public void addDelayByType(int type, double delay) {
        delaysByPatientType.put(type, delay);
    }

    public double getDelay(int type) {
        return delaysByPatientType.get(type);
    }
}
