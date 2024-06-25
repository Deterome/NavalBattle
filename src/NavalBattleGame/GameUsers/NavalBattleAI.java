package NavalBattleGame.GameUsers;

import NavalBattleGame.GameElements.SeaCellInfo;
import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
import NavalBattleGame.GameElements.GameEnums.ShipOrientation;

import java.util.*;

enum CheckDirection {
    Horizontal,
    Vertical
}

public class NavalBattleAI {

    static public void automaticPlacementOfShipsToField(SeaField field, HashMap<Integer, ArrayList<Ship>> ships) {
        if (ships == null) return;

        ArrayList<Integer> rows = field.makeRowsKeysList();
        ArrayList<Character> cols = field.makeColsKeysList();

        for (var entry : ships.entrySet()) {
            for (Ship ship: entry.getValue()) {
                boolean shipPlaced = false;
                HashSet<SeaCellInfo> checkedCells = new HashSet<>();
                while (!shipPlaced) {
                    int row = rows.get(random.nextInt(rows.size()));
                    char col = cols.get(random.nextInt(cols.size()));
                    SeaCellInfo cell = field.getSeaTable().get(row).get(col);
                    checkedCells.add(cell);
                    // если ИИ тупанул и расположил корабли так, что больше ничего не может вставить, то заставляем его повторить заполнение
                    if (checkedCells.size() == field.getHeight() * field.getWidth()) {
                        field.clearField();
                        automaticPlacementOfShipsToField(field, ships);
                        return;
                    }
                    ShipOrientation orientation = random.nextBoolean() ? ShipOrientation.Right : ShipOrientation.Down;
                    if (field.tryPlaceShipInCells(ship, orientation, row, col)) {
                        shipPlaced = true;
                    }
                }
            }
        }
    }


    static public Map.Entry<Integer, Character> analyseFieldAndGetAttackCoords(SeaField field) {
        var hitedShipCoords = findCoordsOfHitedShipInField(field);
        if (hitedShipCoords == null) {
            return findCellInWichCanBeLocatedShip(field);
        }
        return findCoordsToAttackHitedShipAtCoords(field, hitedShipCoords);
    }

    static private Map.Entry<Integer, Character> findCoordsOfHitedShipInField(SeaField field) {
        HashMap<Integer, HashMap<Character, SeaCellInfo>> seaTable = field.getSeaTable();

        for (var rowEntry: seaTable.entrySet()) {
            for (var colEntry: rowEntry.getValue().entrySet()) {
                if (colEntry.getValue().isShelled() &&
                        colEntry.getValue().hasShip() &&
                        !colEntry.getValue().getShip().checkIsShipDestroyed()) {
                    return new AbstractMap.SimpleEntry<>(rowEntry.getKey(), colEntry.getKey());
                }
            }
        }

        return null;
    }

    static private Map.Entry<Integer, Character> findCoordsToAttackHitedShipAtCoords(SeaField field, Map.Entry<Integer, Character> hitedShipCoords) {
        HashMap<CheckDirection, int[][]> moveDirections = new HashMap<>();
        int[][] horizontalDirections = {{1, 0},{-1,0}};
        moveDirections.put(CheckDirection.Horizontal, horizontalDirections);
        int[][] verticalDirections = {{0, 1},{0, -1}};

        int[] stepsVariants = {1, -1};
        ArrayList<Integer> randomStepsVariantsList = new ArrayList<>();
        int randomIdOfStep = random.nextInt(stepsVariants.length);
        randomStepsVariantsList.add(stepsVariants[randomIdOfStep]);
        randomStepsVariantsList.add(stepsVariants[1 - randomIdOfStep]);

        moveDirections.put(CheckDirection.Vertical, verticalDirections);
        var seaTable = field.getSeaTable();
        // ищем соседние задетые части
        for (var moveDirectionEntry: moveDirections.entrySet()) {
            for (var moveDirection: moveDirectionEntry.getValue()) {
                if (seaTable.get(hitedShipCoords.getKey() + moveDirection[1]) != null &&
                        seaTable.get(hitedShipCoords.getKey() + moveDirection[1]).get((char)(hitedShipCoords.getValue() + moveDirection[0])) != null &&
                        seaTable.get(hitedShipCoords.getKey() + moveDirection[1]).get((char)(hitedShipCoords.getValue() + moveDirection[0])).hasShip()) {
                    // теперь мы точно знаем расположение корабля и возвращаем координаты по нужной оси
                    switch (moveDirectionEntry.getKey()) {
                        case Horizontal -> {
                            for (var horizontalMoveDirection: randomStepsVariantsList) {
                                char currCol = (char)(hitedShipCoords.getValue() + horizontalMoveDirection);
                                while (seaTable.get(hitedShipCoords.getKey()).get(currCol) != null &&
                                        seaTable.get(hitedShipCoords.getKey()).get(currCol).isShelled() &&
                                        seaTable.get(hitedShipCoords.getKey()).get(currCol).hasShip()) {
                                    currCol = (char)(currCol + horizontalMoveDirection);
                                }
                                if (seaTable.get(hitedShipCoords.getKey()).get(currCol) != null &&
                                        !seaTable.get(hitedShipCoords.getKey()).get(currCol).isShelled()) {
                                    return new AbstractMap.SimpleEntry<>(hitedShipCoords.getKey(), currCol);
                                }
                            }
                        }
                        case Vertical -> {
                            for (var verticalMoveDirection: randomStepsVariantsList) {
                                var currRow = hitedShipCoords.getKey()+ verticalMoveDirection;
                                while (seaTable.get(currRow) != null &&
                                        seaTable.get(currRow).get(hitedShipCoords.getValue()).isShelled() &&
                                        seaTable.get(currRow).get(hitedShipCoords.getValue()).hasShip()) {
                                    currRow += verticalMoveDirection;
                                }
                                if (seaTable.get(currRow) != null &&
                                        !seaTable.get(currRow).get(hitedShipCoords.getValue()).isShelled()) {
                                    return new AbstractMap.SimpleEntry<>(currRow, hitedShipCoords.getValue());
                                }
                            }
                        }
                    }
                }
            }
        }
        // мы не знаем расположение корабля, поэтому возвращаем близлежащую нетронутую координату
        for (var moveDirectionEntry: moveDirections.entrySet()) {
            for (var moveDirection: moveDirectionEntry.getValue()) {
                if (seaTable.get(hitedShipCoords.getKey() + moveDirection[1]) != null &&
                        seaTable.get(hitedShipCoords.getKey() + moveDirection[1]).get((char)(hitedShipCoords.getValue() + moveDirection[0])) != null &&
                        !seaTable.get(hitedShipCoords.getKey() + moveDirection[1]).get((char)(hitedShipCoords.getValue() + moveDirection[0])).isShelled()) {
                    return new AbstractMap.SimpleEntry<>(hitedShipCoords.getKey() + moveDirection[1], (char)(hitedShipCoords.getValue() + moveDirection[0]));
                }
            }
        }

        return findCellInWichCanBeLocatedShip(field);
    }

    static private Map.Entry<Integer, Character> findCellInWichCanBeLocatedShip(SeaField field) {
        if (field == null) return null;

        var listOfNotShelledCells = field.makeListOfNotShelledCells();
        var countOfShipsWithSameSize = makeMapOfCountOfShipsWithSameSize(field.makeShipsListFromField());
        while (!listOfNotShelledCells.isEmpty()) {
            var notShelledCell = listOfNotShelledCells.get(random.nextInt(listOfNotShelledCells.size()));
            listOfNotShelledCells.remove(notShelledCell);
            for (var shipSize: countOfShipsWithSameSize.keySet()) {
                if (checkIfShipWithSizeCanBeInCell(field, notShelledCell.getKey(), notShelledCell.getValue(), shipSize)) {
                    return notShelledCell;
                }
            }
        }
        return findRandomNotShelledCellInField(field);
    }

    static private Map.Entry<Integer, Character> findRandomNotShelledCellInField(SeaField field) {
        if (field == null) return null;

        var listOfNotShelledCells = field.makeListOfNotShelledCells();

        return listOfNotShelledCells.get(random.nextInt(listOfNotShelledCells.size()));
    }

    static private boolean checkIfShipWithSizeCanBeInCell (SeaField field, int row, char col, int sizeOfShip) {
        if (field == null) return false;
        var seaTable = field.getSeaTable();

        int[] steps = {1, -1};
        for (var direction: CheckDirection.values()) {
            int remainingCells = sizeOfShip-1;
            for (var step: steps) {
                int currentRow = row;
                char currentCol = col;
                while (seaTable.get(currentRow) != null &&
                        seaTable.get(currentRow).get(currentCol) != null &&
                        !seaTable.get(currentRow).get(currentCol).isShelled()
                ) {
                    if (currentRow != row && currentCol != col) {
                        remainingCells--;
                        if (remainingCells == 0) return true;
                    }

                    switch (direction) {
                        case Horizontal -> currentCol = (char)(currentCol + step);
                        case Vertical -> currentRow = currentRow + step;
                    }
                }
            }
        }
        return false;
    }

    static private HashMap<Integer, Integer> makeMapOfCountOfShipsWithSameSize(ArrayList<Ship> shipsList) {
        HashMap<Integer, Integer> countOfShipsWithSameSize = new HashMap<>();

        for (var ship: shipsList) {
            var shipSize = ship.countShipSize();
            if (!countOfShipsWithSameSize.containsKey(shipSize)) {
                countOfShipsWithSameSize.put(shipSize, 0);
            }
            countOfShipsWithSameSize.put(shipSize, countOfShipsWithSameSize.get(shipSize) + 1);
        }

        return countOfShipsWithSameSize;
    }


    static private Random random = new Random();
}
