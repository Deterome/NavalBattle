package NavalBattleGame.GameUsers;

import NavalBattleGame.GameElements.SeaCellInfo;
import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
import NavalBattleGame.GameEnums.ShipOrientation;

import java.util.*;

enum CheckDirection {
    Horizontal,
    Vertical
}

public class NavalBattleAI {

    static public void automaticPlacementOfShipsToField(SeaField field, HashMap<Integer, ArrayList<Ship>> ships) {
        if (ships == null) return;

        ArrayList<Integer> rows = field.getRowsKeys();
        ArrayList<Character> cols = field.getColsKeys();

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
            return getRandomNotShelledCellInField(field);
        }
        return getCoordsToAttackHitedShipAtCoords(field, hitedShipCoords);
    }

    static private Map.Entry<Integer, Character> findCoordsOfHitedShipInField(SeaField field) {
        HashMap<Integer, HashMap<Character, SeaCellInfo>> seaTable = field.getSeaTable();

        for (var rowEntry: seaTable.entrySet()) {
            for (var colEntry: rowEntry.getValue().entrySet()) {
                if (colEntry.getValue().isShelled() &&
                        colEntry.getValue().hasShip() &&
                        !colEntry.getValue().getShip().isDestroyed()) {
                    return new AbstractMap.SimpleEntry<>(rowEntry.getKey(), colEntry.getKey());
                }
            }
        }

        return null;
    }

    static private Map.Entry<Integer, Character> getCoordsToAttackHitedShipAtCoords(SeaField field, Map.Entry<Integer, Character> hitedShipCoords) {
        HashMap<CheckDirection, int[][]> moveDirections = new HashMap<>();
        int[][] horizontalDirections = {{1, 0},{-1,0}};
        moveDirections.put(CheckDirection.Horizontal, horizontalDirections);
        int[][] verticalDirections = {{0, 1},{0, -1}};
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
                            for (var horizontalMoveDirection: horizontalDirections) {
                                char currCol = (char)(hitedShipCoords.getValue() + horizontalMoveDirection[0]);
                                while (seaTable.get(hitedShipCoords.getKey()).get(currCol) != null &&
                                        seaTable.get(hitedShipCoords.getKey()).get(currCol).isShelled() &&
                                        seaTable.get(hitedShipCoords.getKey()).get(currCol).hasShip()) {
                                    currCol = (char)(currCol + horizontalMoveDirection[0]);
                                }
                                if (seaTable.get(hitedShipCoords.getKey()).get(currCol) != null &&
                                        !seaTable.get(hitedShipCoords.getKey()).get(currCol).isShelled()) {
                                    return new AbstractMap.SimpleEntry<>(hitedShipCoords.getKey(), currCol);
                                }
                            }
                        }
                        case Vertical -> {
                            for (var verticalMoveDirection: verticalDirections) {
                                var currRow = hitedShipCoords.getKey()+ verticalMoveDirection[1];
                                while (seaTable.get(currRow) != null &&
                                        seaTable.get(currRow).get(hitedShipCoords.getValue()).isShelled() &&
                                        seaTable.get(currRow).get(hitedShipCoords.getValue()).hasShip()) {
                                    currRow += verticalMoveDirection[1];
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
        // если AI сойдёт с ума, вернём рандомную координату
        return getRandomNotShelledCellInField(field);
    }

    static private Map.Entry<Integer, Character> getRandomNotShelledCellInField(SeaField field) {
        if (field == null) return null;
        var seaTable = field.getSeaTable();

        ArrayList<Integer> rows = field.getRowsKeys();
        ArrayList<Character> cols = field.getColsKeys();
        int row;
        char col;
        do {
            row = rows.get(random.nextInt(rows.size()));
            col = cols.get(random.nextInt(cols.size()));
        } while (seaTable.get(row).get(col).isShelled());

        return new AbstractMap.SimpleEntry<>(row, col);
    }


    static private Random random = new Random();
}
