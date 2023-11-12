package org.example;

import org.example.models.Client;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        task1();
//        task2();
    }

    public static void task1() {
        System.out.println("Task 1");
        for (int n = 100; n < 1001; n += 100) {
            ArrayList<Element> elements = generateElements(n);

            Element previousElement = elements.get(0);
            for (int i = 1; i < n + 1; i++) {
                Element currentElement = elements.get(i);

                previousElement.addNextElement(currentElement, 1.0);
                previousElement = currentElement;
            }

            simulate(n, elements);
        }
    }

    public static void task2() {
        System.out.println("Task 2");

        Random random = new Random();
        int randomId;

        for (int n = 100; n < 1001; n += 100) {
            ArrayList<Element> elements = generateElements(n);

            for (int i = 0; i < n + 1; i++) {
                randomId = random.nextInt(0, n) + 1;

                Element nextElement = elements.get(randomId);
                elements.get(i).addNextElement(nextElement, 1.0);
            }

            simulate(n, elements);
        }
    }

    private static void simulate(int n, ArrayList<Element> elements) {
        for (int i = 1; i < n + 1; i++) {
            Process process = (Process) elements.get(i);
            process.inAct(new Client(0.0));
        }

        Model model = new Model(elements);
        long startTime = System.currentTimeMillis();
        model.simulate(1000.0);
        long finishTime = System.currentTimeMillis();

        System.out.println(n + " processes: " + (finishTime - startTime) + "ms\n");
    }

    private static ArrayList<Element> generateElements(int n) {
        Create create = new Create(10, ChooseRouteBy.PROBABILITY);
        create.setName("Create");
        create.setDistribution("exp");

        ArrayList<Element> elements = new ArrayList<>();
        elements.add(create);

        for (int i = 0; i < n; i++) {
            Process process = new Process(10, 1, ChooseRouteBy.PROBABILITY);
            process.setName("Process " + i);
            process.setMaxqueue(Integer.MAX_VALUE);
            process.setDistribution("exp");

            elements.add(process);
        }
        return elements;
    }
}
