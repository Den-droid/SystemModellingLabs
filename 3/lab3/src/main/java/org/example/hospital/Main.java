package org.example.hospital;

import org.example.hospital.processes.Lab;
import org.example.hospital.processes.Reception;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Create create = new Create(15, ChooseRouteBy.PROBABILITY);
        create.setName("Create");
        create.setDistribution("exp");

        Reception reception = new Reception(2, ChooseRouteBy.TYPED);
        reception.setName("Reception");
        reception.setMaxqueue(Integer.MAX_VALUE);
        reception.setDistribution("exp");

        Process goToWard = new Process(3, 3, ChooseRouteBy.PROBABILITY);
        goToWard.setName("Go To Ward");
        goToWard.setMaxqueue(Integer.MAX_VALUE);
        goToWard.setDistribution("unif");
        goToWard.setDelayDev(8);

        Process goToLabRegistry = new Process(2, Integer.MAX_VALUE, ChooseRouteBy.PROBABILITY);
        goToLabRegistry.setName("Go To Lab Registry");
        goToLabRegistry.setMaxqueue(Integer.MAX_VALUE);
        goToLabRegistry.setDistribution("unif");
        goToLabRegistry.setDelayDev(5);

        Process labRegistry = new Process(4.5, 1, ChooseRouteBy.PROBABILITY);
        labRegistry.setName("Lab Registry");
        labRegistry.setMaxqueue(Integer.MAX_VALUE);
        labRegistry.setDistribution("erlang");
        labRegistry.setK(3);

        Lab lab = new Lab(4, 2, ChooseRouteBy.TYPED);
        lab.setName("Lab");
        lab.setMaxqueue(Integer.MAX_VALUE);
        lab.setDistribution("erlang");
        lab.setK(2);

        Process goToReception = new Process(2, Integer.MAX_VALUE, ChooseRouteBy.PROBABILITY);
        goToReception.setName("Go To Reception");
        goToReception.setMaxqueue(Integer.MAX_VALUE);
        goToReception.setDistribution("unif");
        goToReception.setDelayDev(5);

        Dispose dispose = new Dispose();
        dispose.setName("Dispose");


        create.addNextElement(reception, 1.0);

        reception.addDelayByType(1, 15);
        reception.addDelayByType(2, 40);
        reception.addDelayByType(3, 30);

        reception.addElementByType(1, goToWard);
        reception.addElementByType(2, goToLabRegistry);
        reception.addElementByType(3, goToLabRegistry);

        goToWard.addNextElement(dispose, 1.0);

        goToLabRegistry.addNextElement(labRegistry, 1.0);

        labRegistry.addNextElement(lab, 1.0);

        lab.addElementByType(2, goToReception);
        lab.addElementByType(3, dispose);

        goToReception.addNextElement(reception, 1.0);


        ArrayList<Element> elements = new ArrayList<>();
        elements.add(create);
        elements.add(reception);
        elements.add(goToWard);
        elements.add(goToLabRegistry);
        elements.add(labRegistry);
        elements.add(lab);
        elements.add(goToReception);
        elements.add(dispose);

        Model model = new Model(elements);
        model.simulate(10000.0);

        for (Element e : elements) {
            if (e instanceof Dispose d) {
                System.out.println("Average time of patient in hospital: " +
                        (d.getTotalPatientsTime() / d.getQuantity()));
            } else if (e instanceof Lab lab1) {
                System.out.println("Average interval between arrival to the lab: " +
                        (lab1.getTotalTimeBetweenEntrances() / lab1.getQuantity()));
            }
        }
    }
}
