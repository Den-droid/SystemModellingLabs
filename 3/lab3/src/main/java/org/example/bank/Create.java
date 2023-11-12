package org.example.bank;

import java.util.Iterator;
import java.util.Map;

public class Create extends Element {
    public Create(double delay, ChooseRouteBy chooseRouteBy) {
        super(delay, chooseRouteBy);
        super.setTnext(0.1);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());

        Element nextElement = null;
        switch (chooseRouteBy) {
            case PROBABILITY -> nextElement = chooseNextElementByProbability();
            case PRIORITY -> nextElement = chooseNextElementByPriority();
        }

        if (nextElement != null)
            nextElement.inAct();
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
                if (current.getQueue() < queue) {
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
