package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        long time = System.currentTimeMillis();
        HashSet<String> hashSet = new HashSet<>();
        for (String str : args) {
            if (!str.equals("-Xmx1G")) {
                try (
                        BufferedReader br = new BufferedReader(new FileReader(str))) {
                    String line = br.readLine();
                    while (line != null) {
                        hashSet.add(line);
                        line = br.readLine();
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        List<String> lines = new ArrayList<>(hashSet);

        ListCreator listCreator = new ListCreator();
        List<List<String>> result = listCreator.findAndGroupLines(lines);

        System.out.println("file size = " + lines.size());
        System.out.println("list group size = " + result.size());
        createFile(result);
        System.out.println(System.currentTimeMillis() - time);

    }


    public static void createFile(List<List<String>> collection) {
        try (
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("text.txt"))) {
            for (int i = 1; i < collection.size() +1; i++) {
                bufferedWriter.append("Группа " + i);
                bufferedWriter.newLine();

                for (String line :
                        collection.get(i - 1)) {
                    bufferedWriter.append(line + ";");
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }


}