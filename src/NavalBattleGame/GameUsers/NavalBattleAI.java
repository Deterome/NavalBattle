package NavalBattleGame.GameUsers;

import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
import NavalBattleGame.GameEnums.ShipOrientation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NavalBattleAI {

    static public void automaticPlacementOfShipsToField(SeaField field, HashMap<Integer, ArrayList<Ship>> ships) {
        ArrayList<Integer> rows = field.getRowsKeys();
        ArrayList<Character> cols = field.getColsKeys();

        for (var entry : ships.entrySet()) {
            for (Ship ship: entry.getValue()) {
                boolean shipPlaced = false;
                while (!shipPlaced) {
                    int row = rows.get(random.nextInt(rows.size()));
                    char col = cols.get(random.nextInt(cols.size()));
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
