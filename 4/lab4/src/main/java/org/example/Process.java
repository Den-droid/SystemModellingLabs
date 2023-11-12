package org.example;

import org.example.models.Client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Process extends Element {
    private int maxqueue, failure;
    protected Queue<Client> clientsQueue;
    protected Queue<Client> nextClients;
    private double meanQueue, meanLoad;

    public Process(double delay, int workersCount, ChooseRouteBy chooseRouteBy) {
        super(delay, workersCount, chooseRouteBy);
        this.clientsQueue = new LinkedList<>();
        this.nextClients = new LinkedList<>();

        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        meanLoad = 0.0;
    }

    public void inAct(Client client) {
        if (super.getState() < getWorkersCount()) {
            super.setState(super.getState() + 1);
            nextClients.add(client);
            addTimeNext(super.getTcurr() + super.getDelay());
        } else {
            if (getQueue() < getMaxqueue()) {
                clientsQueue.add(client);
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
            Client client = clientsQueue.poll();
            nextClients.add(client);

            super.setState(super.getState() + 1);
            addTimeNext(super.getTcurr() + super.getDelay());
        }

        Element nextElement = null;
        switch (chooseRouteBy) {
            case PROBABILITY -> nextElement = chooseNextElementByProbability();
            case PRIORITY -> nextElement = chooseNextElementByPriority();
        }

        if (nextElement != null) {
            Client client = pollNextClient();
            if (nextElement instanceof Process process) {
                process.inAct(client);
            } else if (nextElement instanceof Dispose dispose) {
                dispose.inAct(client);
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
        return clientsQueue.size();
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

    public Client pollNextClient() {
        return nextClients.poll();
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