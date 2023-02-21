package org.example;

import java.util.*;

import static java.util.Comparator.comparing;

public class ListCreator {


    public List<List<String>> findAndGroupLines(List<String> lines) {
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

            for (int elmentIndex = 0; elmentIndex < lineElements.length; elmentIndex++) {
                String currentLineElement = lineElements[elmentIndex];

                if (columns.size() == elmentIndex)
                    columns.add(new HashMap<>());

                if ("\"\"".equals(currentLineElement)) {
                    continue;
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
        linesGroups.removeAll(Collections.singleton(null));
        linesGroups.removeAll(Collections.emptyList());
//        linesGroups.removeIf(list -> list.size() == 1);
        linesGroups.sort(comparing(List::size));
        Collections.reverse(linesGroups);
        return linesGroups;

    }


}
