package org.example.bank;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Create create = new Create(0.5, ChooseRouteBy.PRIORITY);
        create.setName("Create");
        create.setDistribution("exp");

        Process lane1 = new Process(1, 1, ChooseRouteBy.PRIORITY, 2);
        lane1.setMaxqueue(3);
        lane1.setName("Lane 1");
        lane1.setDistribution("norm");
        lane1.setDelayDev(0.3);

        Process lane2 = new Process(1, 1, ChooseRouteBy.PRIORITY, 2);
        lane2.setMaxqueue(3);
        lane2.setName("Lane 2");
        lane2.setDistribution("norm");
        lane2.setDelayDev(0.3);

        lane1.addOtherLanes(lane2);
        lane2.addOtherLanes(lane1);

        create.addNextElement(lane1, 1);
        create.addNextElement(lane2, 2);

        ArrayList<Element> elementList = new ArrayList<>();
        elementList.add(create);
        elementList.add(lane1);
        elementList.add(lane2);

        lane1.inAct();
        lane1.inAct();
        lane1.inAct();
        lane1.setDistribution("exp");
        lane1.setDelayMean(0.3);

        lane2.inAct();
        lane2.inAct();
        lane2.inAct();
        lane2.setDistribution("exp");
        lane2.setDelayMean(0.3);

        Model model = new Model(elementList);
        model.simulate(1000.0);
    }
}
