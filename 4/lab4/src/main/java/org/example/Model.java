package org.example;

import java.util.ArrayList;

public class Model {
    private final ArrayList<Element> list;
    double tnext, tcurr;
    Element event;
    int eventCount;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            eventCount++;

            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e;
                }
            }
//            System.out.println("\nIt's time for event in " +
//                    event.getName() +
//                    ", time = " + tnext);
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            event.outAct();
            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    eventCount++;

                    e.outAct();
                }
            }
//            printInfo();
        }
        System.out.println("Event count: " + eventCount);
//        printResult();
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (Element e : list) {
            e.printResult();
            if (e instanceof Process p) {
                System.out.println("Process: " + p.getName());
                System.out.println("mean length of queue = " +
                        p.getMeanQueue() / tcurr
                        + "\nmean load of process = " +
                        p.getMeanLoad() / tcurr
                        + "\nfailure probability = " +
                        p.getFailure() / (double) (p.getFailure() + p.getQuantity()));
                System.out.println("\n");
            }
        }
    }
}
