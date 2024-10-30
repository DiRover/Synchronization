package di_rover;

import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {

        ThreadGroup group = getThreadGroup();

        group.interrupt();

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

    private static ThreadGroup getThreadGroup() {
        ThreadGroup group = new ThreadGroup("Group");

        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(group, () -> {
                String route = generateRoute("RLRFR", 100);
                int repeats = countRInRoute(route);

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(repeats, 1, Integer::sum);
                }
            });

            thread.start();
        }
        return group;
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