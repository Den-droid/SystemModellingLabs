package org.example;

import java.util.*;

public class Element {
    private String name;
    private double delayMean, delayDev, k;
    private String distribution;
    private int quantity;
    private double tcurr;
    private int state;
    private static int nextId = 0;
    private int id;
    protected ChooseRouteBy chooseRouteBy;
    protected List<Element> nextElements;
    protected List<Double> nextElementsProbabilities;
    protected Map<Integer, Process> nextProcessesPriorities;
    protected int workersCount;
    protected PriorityQueue<Double> workersTimeNext;

    public Element(double delay, int workersCount, ChooseRouteBy chooseRouteBy) {
        delayMean = delay;
        distribution = "";
        tcurr = 0.0;
        state = 0;
        id = nextId;
        nextId++;
        name = "element" + id;

        this.workersCount = workersCount;
        this.workersTimeNext = new PriorityQueue<>();
        this.chooseRouteBy = chooseRouteBy;
        this.nextElements = new ArrayList<>();
        if (chooseRouteBy.equals(ChooseRouteBy.PROBABILITY)) {
            this.nextElementsProbabilities = new ArrayList<>();
        } else if (chooseRouteBy.equals(ChooseRouteBy.PRIORITY)) {
            this.nextProcessesPriorities = new TreeMap<>();
        }
    }

    public double getDelay() {
        double delay = getDelayMean();

        switch (getDistribution().toLowerCase()) {
            case "exp" -> delay = FunRand.Exp(getDelayMean());
            case "norm" -> delay = FunRand.Norm(getDelayMean(), getDelayDev());
            case "unif" -> delay = FunRand.Unif(getDelayMean(), getDelayDev());
            case "erlang" -> delay = FunRand.Erlang(getDelayMean(), getK());
        }

        return delay;
    }

    public double getDelayDev() {
        return delayDev;
    }

    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTcurr() {
        return tcurr;
    }

    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getTnext() {
        return workersTimeNext.isEmpty() ? Double.MAX_VALUE : workersTimeNext.peek();
    }

    public double getDelayMean() {
        return delayMean;
    }

    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkersCount() {
        return workersCount;
    }

    public void setWorkersCount(int workersCount) {
        this.workersCount = workersCount;
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

    protected void removeTimeNext() {
        if (!this.workersTimeNext.isEmpty()) {
            workersTimeNext.poll();
        }
    }

    protected void addTimeNext(double value) {
        this.workersTimeNext.add(value);
    }

    public void inAct() {
    }

    public void outAct() {
        quantity++;
    }

    public void doStatistics(double delta) {
    }

    public void printResult() {
        System.out.println(getName() + " quantity = " + quantity);
    }

    public void printInfo() {
        System.out.println(getName() + " state= " + state +
                " quantity = " + quantity +
                " tnext= " + getTnext());
    }

    protected void increaseQuantity() {
        quantity++;
    }
}