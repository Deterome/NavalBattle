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

    static private Random random = new Random();
}
