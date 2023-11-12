package org.example.hospital.processes;

import org.example.hospital.ChooseRouteBy;
import org.example.hospital.Dispose;
import org.example.hospital.Element;
import org.example.hospital.Process;
import org.example.hospital.models.Patient;

import java.util.HashMap;
import java.util.Map;

public class Lab extends Process {
    private Map<Integer, Element> nextElementByType;
    private double totalTimeBetweenEntrances, entranceTime;

    public Lab(double delay, int workersCount, ChooseRouteBy chooseRouteBy) {
        super(delay, workersCount, chooseRouteBy);
        this.nextElementByType = new HashMap<>();
        this.entranceTime = 0.0;
        totalTimeBetweenEntrances = 0.0;
    }

    public void inAct(Patient patient) {
        if (entranceTime != 0.0) {
            totalTimeBetweenEntrances += super.getTcurr() - entranceTime;
        }
        entranceTime = super.getTcurr();

        super.inAct(patient);
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
        Element nextElement = nextElementByType.get(patient.getType());

        if (patient.getType() == 2) {
            patient.setType(1);
        }

        if (nextElement instanceof Process process) {
            process.inAct(patient);
        } else if (nextElement instanceof Dispose dispose) {
            dispose.inAct(patient);
        }
    }

    public void addElementByType(int type, Element element) {
        nextElementByType.put(type, element);
    }

    public double getTotalTimeBetweenEntrances() {
        return totalTimeBetweenEntrances;
    }
}
