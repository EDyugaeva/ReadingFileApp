package org.example;

public class Main {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        ListCreator listCreator = new ListCreator();

        for (String str : args) {
            if (!str.equals("-Xmx1G")) {
                listCreator.divideFileIntoGroups(str);
            }
        }
        System.out.println("Время выполнения программы = " + (System.currentTimeMillis() - time) + " мс");

    }


}