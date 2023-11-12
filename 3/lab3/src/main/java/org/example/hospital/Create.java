package org.example.hospital;

import org.example.hospital.models.Patient;

import java.util.Iterator;
import java.util.Map;

public class Create extends Element {
    public Create(double delay, ChooseRouteBy chooseRouteBy) {
        super(delay, 1, chooseRouteBy);
        super.addTimeNext(0.0);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.removeTimeNext();
        super.addTimeNext(super.getTcurr() + super.getDelay());

        Patient newPatient = new Patient(Math.random(), super.getTcurr());

        Element nextElement = null;
        switch (super.chooseRouteBy) {
            case PROBABILITY -> nextElement = chooseNextElementByProbability();
            case PRIORITY -> nextElement = chooseNextElementByPriority();
        }

        if (nextElement != null) {
            if (nextElement instanceof Process process) {
                process.inAct(newPatient);
            }
        }
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
