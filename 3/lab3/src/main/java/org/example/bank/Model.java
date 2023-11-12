package org.example.bank;

import java.util.ArrayList;

public class Model {
    private final ArrayList<Element> list;
    double tnext, tcurr;
    int event;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e.getId();
                }
            }
            System.out.println("\nIt's time for event in " +
                    list.get(event).getName() +
                    ", time = " + tnext);
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            list.get(event).outAct();
            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    e.outAct();
                }
            }
            printInfo();
        }
        printResult();
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult() {
        int totalQuantity = 0;
        double totalClientsInBank = 0, totalWaitTime = 0;

        System.out.println("\n-------------RESULTS-------------");
        for (Element e : list) {
            if (e instanceof Process p) {
                totalClientsInBank += p.getMeanLoad() + p.getMeanQueue();
                totalWaitTime += p.getWaitTime();
                totalQuantity += p.getQuantity();

                System.out.println("Process: " + p.getName());
                System.out.println("1. Average load of cashier: " + (p.getMeanLoad() / tcurr));
                System.out.println("5. Average queue of cashier: " + (p.getMeanQueue() / tcurr));
                System.out.println("6. Average failures: " + (p.getFailure() / (double) (p.getQuantity() + p.getFailure())));
                System.out.println("7. Swaps count: " + p.getSwapsCount());
                System.out.println("\n");
            }
        }

        System.out.println("2. Average amount of clients in bank: " + totalClientsInBank / tcurr);
        System.out.println("3. Average out time in bank: " + (totalWaitTime / totalQuantity));
        System.out.println("4. Average client time in bank: " + (totalClientsInBank / totalQuantity));
    }
}
