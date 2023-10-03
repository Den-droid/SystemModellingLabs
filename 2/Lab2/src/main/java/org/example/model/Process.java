package org.example.model;

import java.util.*;

public class Process extends Element {
    private int queue, maxqueue, failure;
    private int workersCount;
    private PriorityQueue<Double> workersTimeNext;
    private ChooseRouteBy chooseRouteBy;
    private List<Element> nextElements;
    private List<Double> nextElementsProbabilities;
    private Map<Integer, Process> nextProcessesPriorities;
    private double meanQueue;
    private double meanLoad;

    public Process(double delay, int workersCount, ChooseRouteBy chooseRouteBy) {
        super(delay);
        super.setTnext(Double.MAX_VALUE);
        this.workersCount = workersCount;
        this.workersTimeNext = new PriorityQueue<>();
        this.nextElements = new ArrayList<>();
        this.chooseRouteBy = chooseRouteBy;
        if (chooseRouteBy.equals(ChooseRouteBy.PROBABILITY)) {
            this.nextElementsProbabilities = new ArrayList<>();
        } else if (chooseRouteBy.equals(ChooseRouteBy.PRIORITY)) {
            this.nextProcessesPriorities = new TreeMap<>();
        }
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        meanLoad = 0.0;
    }

    @Override
    public void inAct() {
        if (super.getState() < getWorkersCount()) {
            super.setState(super.getState() + 1);
            addTimeNext(super.getTcurr() + super.getDelay());
            setTnext();
        } else {
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
            } else {
                failure++;
            }
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setState(super.getState() - 1);
        removeTimeNext();
        setTnext();

        if (getQueue() > 0) {
            setQueue(getQueue() - 1);
            super.setState(super.getState() + 1);
            addTimeNext(super.getTcurr() + super.getDelay());
            setTnext();
        }

        switch (chooseRouteBy) {
            case PROBABILITY -> {
                Element element = chooseNextElementByProbability();
                if (element != null)
                    element.inAct();
            }
            case PRIORITY -> {
                Process process = chooseNextElementByPriority();
                if (process != null)
                    process.inAct();
            }
        }
    }

    public int getFailure() {
        return failure;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
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

    public int getWorkersCount() {
        return workersCount;
    }

    public void setWorkersCount(int workersCount) {
        this.workersCount = workersCount;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("failure = " + this.getFailure());
    }

    @Override
    public void doStatistics(double delta) {
        meanQueue += queue * delta;
        meanLoad += super.getState() * delta;
    }

    public void setTnext() {
        if (!this.workersTimeNext.isEmpty()) {
            double timeNext = this.workersTimeNext.peek();
            super.setTnext(timeNext);
        } else {
            super.setTnext(Double.MAX_VALUE);
        }
    }

    public void removeTimeNext() {
        if (!this.workersTimeNext.isEmpty()) {
            workersTimeNext.poll();
        }
    }

    public void addTimeNext(double value) {
        this.workersTimeNext.add(value);
    }

    public void addNextElement(Element element, double probability) {
        if (chooseRouteBy.equals(ChooseRouteBy.PROBABILITY)) {
            nextElements.add(element);
            nextElementsProbabilities.add(probability);
        }
    }

    public void addNextElement(Process process, int priority) {
        if (chooseRouteBy.equals(ChooseRouteBy.PRIORITY)) {
            nextElements.add(process);
            nextProcessesPriorities.put(priority, process);
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
            nextElement.inAct();
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