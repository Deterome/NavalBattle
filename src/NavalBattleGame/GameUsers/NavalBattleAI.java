package NavalBattleGame.GameUsers;

import NavalBattleGame.GameElements.SeaCellInfo;
import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
import NavalBattleGame.GameEnums.ShipOrientation;

import java.util.*;

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
//        if (hitedShipCoords == null) {
        if (true) {
            return getRandomNotShelledCellInField(field);
        }
        return getCoordsToAttackHitedShipAtCoords(field, hitedShipCoords);
    }

    static private Map.Entry<Integer, Character> findCoordsOfHitedShipInField(SeaField field) {
        HashMap<Integer, HashMap<Character, SeaCellInfo>> seaTable = field.getSeaTable();

        for (var rowEntry: seaTable.entrySet()) {
            for (var colEntry: rowEntry.getValue().entrySet()) {
                if (colEntry.getValue().getShip() != null && colEntry.getValue().getShip().isHited() && !colEntry.getValue().getShip().isDestroyed()) {
                    return new AbstractMap.SimpleEntry<>(rowEntry.getKey(), colEntry.getKey());
                }
            }
        }

        return null;
    }

    static private Map.Entry<Integer, Character> getCoordsToAttackHitedShipAtCoords(SeaField field, Map.Entry<Integer, Character> hitedShipCoords) {

        return null;
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
