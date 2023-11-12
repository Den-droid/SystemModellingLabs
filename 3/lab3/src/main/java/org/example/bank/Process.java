package org.example.bank;

import java.util.*;

public class Process extends Element {
    private int queue, maxqueue, failure;
    private int workersCount;
    private PriorityQueue<Double> workersTimeNext;
    private double meanQueue, meanLoad;
    private double waitStart, waitTime;
    private int swapThreshold, swapsCount;
    private List<Process> otherLanes;

    public Process(double delay, int workersCount, ChooseRouteBy chooseRouteBy, int swapThreshold) {
        super(delay, chooseRouteBy);
        super.setTnext(Double.MAX_VALUE);
        this.workersCount = workersCount;
        this.workersTimeNext = new PriorityQueue<>();
        this.otherLanes = new ArrayList<>();
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        meanLoad = 0.0;
        waitTime = 0.0;
        waitStart = 0.0;
        this.swapThreshold = swapThreshold;
        swapsCount = 0;
    }

    @Override
    public void inAct() {
        if (super.getState() == 0) {
            waitTime += super.getTcurr() - waitStart;
        }

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

        Element nextElement = null;
        switch (chooseRouteBy) {
            case PROBABILITY -> nextElement = chooseNextElementByProbability();
            case PRIORITY -> nextElement = chooseNextElementByPriority();
        }

        if (super.getState() == 0) {
            waitStart = super.getTcurr();
        }

        for (Process otherLane : otherLanes) {
            otherLane.checkForOtherLanesQueues();
        }

        if (nextElement != null)
            nextElement.inAct();
    }

    private void checkForOtherLanesQueues() {
        for (Process otherLane : otherLanes) {
            if (this.getQueue() - otherLane.getQueue() >= swapThreshold) {
                this.setQueue(this.getQueue() - 1);
                otherLane.setQueue(otherLane.getQueue() + 1);

                swapsCount++;
                break;
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
        System.out.println("failure = " + this.getFailure() + "; queue = " + this.getQueue());
    }

    @Override
    public void doStatistics(double delta) {
        meanQueue += queue * delta;
        meanLoad += super.getState() * delta;
    }

    public int getSwapsCount() {
        return swapsCount;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public void addOtherLanes(Process process) {
        otherLanes.add(process);
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