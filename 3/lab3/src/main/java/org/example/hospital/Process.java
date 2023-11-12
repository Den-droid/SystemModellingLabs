package org.example.hospital;

import org.example.hospital.models.Patient;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Process extends Element {
    private int maxqueue, failure;
    protected Queue<Patient> patientsQueue;
    protected Queue<Patient> nextPatients;
    private double meanQueue, meanLoad;

    public Process(double delay, int workersCount, ChooseRouteBy chooseRouteBy) {
        super(delay, workersCount, chooseRouteBy);
        this.patientsQueue = new LinkedList<>();
        this.nextPatients = new LinkedList<>();

        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        meanLoad = 0.0;
    }

    public void inAct(Patient patient) {
        if (super.getState() < getWorkersCount()) {
            super.setState(super.getState() + 1);
            nextPatients.add(patient);
            addTimeNext(super.getTcurr() + super.getDelay());
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
        super.outAct();
        super.setState(super.getState() - 1);
        removeTimeNext();

        if (getQueue() > 0) {
            Patient patient = patientsQueue.poll();
            nextPatients.add(patient);

            super.setState(super.getState() + 1);
            addTimeNext(super.getTcurr() + super.getDelay());
        }

        Element nextElement = null;
        switch (chooseRouteBy) {
            case PROBABILITY -> nextElement = chooseNextElementByProbability();
            case PRIORITY -> nextElement = chooseNextElementByPriority();
        }

        if (nextElement != null) {
            if (nextElement instanceof Process process) {
                process.inAct(nextPatients.poll());
            } else if (nextElement instanceof Dispose dispose) {
                dispose.inAct(nextPatients.poll());
            }
        }
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getQueue() {
        return patientsQueue.size();
    }

    public int getMaxqueue() {
        return maxqueue;
    }

    public void setMaxqueue(int maxqueue) {
        this.maxqueue = maxqueue;
    }

    public double getMeanLoad() {
        return meanLoad;
    }

    public double getMeanQueue() {
        return meanQueue;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("failure = " + this.getFailure() + "; queue = " + this.getQueue());
    }

    @Override
    public void doStatistics(double delta) {
        meanQueue += getQueue() * delta;
        meanLoad += super.getState() * delta;
    }

    public Patient pollPatient() {
        return nextPatients.poll();
    }

    private Process chooseNextElementByPriority() {
        if (!nextElements.isEmpty()) {
            Iterator<Map.Entry<Integer, Process>> iterator =
                    nextProcessesPriorities.entrySet().iterator();
            Map.Entry<Integer, Process> keyValue = iterator.next();
            int queue = keyValue.getValue().getQueue();
            Process nextElement = keyValue.getValue();
            while (iterator.hasNext()) {
                Process current = iterator.next().getValue();
                if (current.getQueue() < queue && current.getQueue() < current.getMaxqueue()) {
                    queue = current.getQueue();
                    nextElement = current;
                }
            }
            return nextElement;
        }
        return null;
    }

    private Element chooseNextElementByProbability() {
        if (!nextElements.isEmpty()) {
            double random = Math.random();
            double nextProbability = 0.0;
            for (int i = 0; i < nextElements.size(); i++) {
                if (random < nextProbability + nextElementsProbabilities.get(i)) {
                    return nextElements.get(i);
                }
                nextProbability += nextElementsProbabilities.get(i);
            }
        }
        return null;
    }
}