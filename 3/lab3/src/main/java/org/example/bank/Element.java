package org.example.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Element {
    private String name;
    private double tnext;
    private double delayMean, delayDev;
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

    public Element(double delay, ChooseRouteBy chooseRouteBy) {
        name = "anonymus";
        tnext = 0.0;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state = 0;
        id = nextId;
        nextId++;
        name = "element" + id;

        this.chooseRouteBy = chooseRouteBy;
        this.nextElements = new ArrayList<>();
        if (chooseRouteBy.equals(ChooseRouteBy.PROBABILITY)) {
            this.nextElementsProbabilities = new ArrayList<>();
        } else if (chooseRouteBy.equals(ChooseRouteBy.PRIORITY)) {
            this.nextProcessesPriorities = new TreeMap<>();
        }
    }

    public Element(String nameOfElement, double delay) {
        name = nameOfElement;
        tnext = 0.0;
        delayMean = delay;
        distribution = "exp";
        tcurr = tnext;
        state = 0;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public double getDelay() {
        double delay = getDelayMean();
        if ("exp".equalsIgnoreCase(getDistribution())) {
            delay = FunRand.Exp(getDelayMean());
        } else {
            if ("norm".equalsIgnoreCase(getDistribution())) {
                delay = FunRand.Norm(getDelayMean(),
                        getDelayDev());
            } else {
                if ("unif".equalsIgnoreCase(getDistribution())) {
                    delay = FunRand.Unif(getDelayMean(),
                            getDelayDev());
                } else {
                    if ("".equalsIgnoreCase(getDistribution()))
                        delay = getDelayMean();
                }
            }
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
        return tnext;
    }

    public void setTnext(double tnext) {
        this.tnext = tnext;
    }

    public double getDelayMean() {
        return delayMean;
    }

    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
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
                " tnext= " + tnext);
    }

}