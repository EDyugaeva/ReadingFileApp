package org.example;

import java.io.*;
import java.util.*;

import static java.util.Comparator.comparing;

public class ListCreator {

    public void divideFileIntoGroups(String path) {
        HashSet<String> hashSet = readFile(path);//читаем файл
        List<List<String>> listOfLists = findAndGroupLines(new ArrayList<>(hashSet));//делим его на группы
        createFile(listOfLists);

        printResults(listOfLists);


    }

    private void printResults(List<List<String>> listOfLists) {
        int size = listOfLists.size();
        System.out.println("Количество групп = " + size);
        for (int i = 1; i < size + 1; i++) {
            System.out.println("Группа " + i);
            for (String lines :
                    listOfLists.get(i - 1)) {
                System.out.println(lines);
            }
            System.out.println();
        }
    }

    private HashSet<String> readFile(String path) {
        HashSet<String> set = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                set.add(line);
                line = br.readLine();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return set;
    }


    private List<List<String>> findAndGroupLines(List<String> lines) {
        if (lines == null)
            return Collections.emptyList();

        List<List<String>> linesGroups = new ArrayList<>(); //список групп, каждый элемент вида "номер группы - список строк группы"
        if (lines.size() < 2) {
            linesGroups.add(lines);
            return linesGroups;
        }

        List<Map<String, Integer>> columns = new ArrayList<>(); // список столбцов, каждый столбец - мапа с парами "элемент строки/столбца-номер группы"
        Map<Integer, Integer> unitedGroups = new HashMap<>(); //мэп с парами "номер некоторой группы - номер группы, с которой надо объединить данную"

        for (String line : lines) {
            String[] lineElements = line.split(";");
            TreeSet<Integer> groupsWithSameElems = new TreeSet<>(); //список групп, имеющих совпадающие элементы
            List<NewElements> newElements = new ArrayList<>(); //список элементов, которых нет в мапах столбцов
            boolean correctLine = true;

            for (int elmentIndex = 0; elmentIndex < lineElements.length; elmentIndex++) {
                String currentLineElement = lineElements[elmentIndex];

                if (columns.size() == elmentIndex)
                    columns.add(new HashMap<>());


                if ("\"\"".equals(currentLineElement)) {
                    continue;
                }

                if ((currentLineElement.length() > 2 && currentLineElement.substring(1, currentLineElement.length() - 1).contains("\""))) {
                    correctLine = false;
                }

                Map<String, Integer> currCol = columns.get(elmentIndex); //получаем мап с соответствием уникальных элементов и номеров групп
                Integer elemGrNum = currCol.get(currentLineElement);//получаем номер группы
                if (elemGrNum != null) {
                    while (unitedGroups.containsKey(elemGrNum)) // если группа с таким номером объединена с другой,
                        elemGrNum = unitedGroups.get(elemGrNum); //то сохраняем номер группы, с которой была объединена данная
                    groupsWithSameElems.add(elemGrNum);
                } else {
                    newElements.add(new NewElements(currentLineElement, elmentIndex));//Если такой группы нет, создаем новый элемент
                }
            }
            if (correctLine) {

                int groupNumber;
                if (groupsWithSameElems.isEmpty()) {
                    linesGroups.add(new ArrayList<>());
                    groupNumber = linesGroups.size() - 1;
                } else {
                    groupNumber = groupsWithSameElems.first();
                }
                for (NewElements newLineElement : newElements) { //добавляет данные о несгруппированных элементах
                    columns.get(newLineElement.getPosition()).put(newLineElement.getString(), groupNumber);
                }
                for (int matchedGrNum : groupsWithSameElems) { //перебираем все группы с таким же элементом
                    if (matchedGrNum != groupNumber) {
                        unitedGroups.put(matchedGrNum, groupNumber); //сохраняем инф-цию об объединённых группах
                        linesGroups.get(groupNumber).addAll(linesGroups.get(matchedGrNum)); //объединяем группы
                        linesGroups.set(matchedGrNum, null); //помечаем группу с текущим номер, как несуществующую
                    }
                }
                linesGroups.get(groupNumber).add(line);
            }
        }

        return sortList(linesGroups);

    }

    private static List<List<String>> sortList(List<List<String>> list) {
        list.removeAll(Collections.singleton(null)); //Удаляем пустые группы
            list.removeIf(l -> l.size() == 1);
            list.sort(comparing(List::size));
            Collections.reverse(list);

        return list;
    }

    private static void createFile(List<List<String>> collection) {
        try (
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("result.txt"))) {
            for (int i = 1; i < collection.size() + 1; i++) {
                bufferedWriter.append("Группа " + i);
                bufferedWriter.newLine();
                for (String line :
                        collection.get(i - 1)) {
                    bufferedWriter.append(line).append(";");
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
