package org.example.model;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Create create = new Create(1);
        create.setName("Create");
        create.setDistribution("exp");

        Process process1 = new Process(1, 1, ChooseRouteBy.PRIORITY);
        process1.setMaxqueue(5);
        process1.setName("Process 1");
        process1.setDistribution("exp");

        Process process2 = new Process(1, 1, ChooseRouteBy.PRIORITY);
        process2.setMaxqueue(5);
        process2.setName("Process 2");
        process2.setDistribution("exp");

        Process process3 = new Process(1, 1, ChooseRouteBy.PROBABILITY);
        process3.setMaxqueue(5);
        process3.setName("Process 3");
        process3.setDistribution("exp");

        create.setNextElement(process1);

        process1.addNextElement(process2, 1);
        process1.addNextElement(process3, 2);
        process2.addNextElement(process3, 2);
        process2.addNextElement(process1, 1);

        ArrayList<Element> elementList = new ArrayList<>();
        elementList.add(create);
        elementList.add(process1);
        elementList.add(process2);
        elementList.add(process3);

        Model model = new Model(elementList);
        model.simulate(1000.0);
    }
}
