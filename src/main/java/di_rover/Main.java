package di_rover;

import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        ThreadGroup group = new ThreadGroup("Group");

        Thread maxCheckThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Map.Entry<Integer, Integer> mostFrequentEntry = sizeToFreq.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .orElse(null);

                    if (mostFrequentEntry != null) {
                        System.out.println("Current max " + mostFrequentEntry.getKey() +
                                " (встретилось " + mostFrequentEntry.getValue() + " раз)");
                    }

                }
            }
        });

        maxCheckThread.start();

        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(group, () -> {
                String route = generateRoute("RLRFR", 100);
                int repeats = countRInRoute(route);

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(repeats, 1, Integer::sum);
                    sizeToFreq.notify();
                }

            });

            thread.start();
        }


        maxCheckThread.interrupt();
        group.interrupt();

        maxCheckThread.join();

        Map.Entry<Integer, Integer> mostFrequentEntry = sizeToFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (mostFrequentEntry != null) {
            System.out.println("Самое частое количество повторений " + mostFrequentEntry.getKey() +
                    " (встретилось " + mostFrequentEntry.getValue() + " раз)");
            System.out.println("Другие размеры:");
            sizeToFreq.forEach((key, value) -> {
                if (!key.equals(mostFrequentEntry.getKey())) {
                    System.out.println("- " + key + " (" + value + " раз)");
                }
            });
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int countRInRoute(String route) {
        return (int) route.chars().filter(ch -> ch == 'R').count();
    }
}