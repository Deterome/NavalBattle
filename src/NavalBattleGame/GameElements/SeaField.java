package NavalBattleGame.GameElements;

import NavalBattleGame.GameEnums.ShipPlacement;

import java.util.ArrayList;
import java.util.HashMap;

public class SeaField {

    public SeaField(int seaWidth, int seaHeight) {
        this.seaTable = new HashMap<Integer, HashMap<Character, SeaCellInfo>>();

        for (int rowNumber = FIRST_NUMBER; rowNumber <= seaHeight; rowNumber++) {
            seaTable.put(rowNumber, new HashMap<>());

            for (char colLetter = FIRST_ALPHABET_SYMBOL; colLetter < FIRST_ALPHABET_SYMBOL + seaWidth; colLetter++) {
                seaTable.get(rowNumber).put(colLetter, new SeaCellInfo());
            }
        }
    }

    public HashMap<Integer, HashMap<Character, SeaCellInfo>> getSeaTable() {
        return seaTable;
    }

    private HashMap<Integer, HashMap<Character, SeaCellInfo>> seaTable;

    public void placeShipInCells(Ship ship, ShipPlacement placement, int row, char col) {
        if (isCellExist(row, col)) {
            switch (placement) {
                case Horizontal -> {
                    while (!isCellExist(row, (char)(col + (ship.getShipSize()-1)))) {
                        col--;
                    }
                    for (int shipPartId = 0; shipPartId < ship.getShipSize(); shipPartId++, col++) {
                        seaTable.get(row).get(col).ship = ship;
                        seaTable.get(row).get(col).shipPartId = shipPartId;
                    }
                }
                case Vertical -> {
                    while (!isCellExist(row + (ship.getShipSize()-1), col)) {
                        row--;
                    }
                    for (int shipPartId = 0; shipPartId < ship.getShipSize(); shipPartId++, row++) {
                        seaTable.get(row).get(col).ship = ship;
                        seaTable.get(row).get(col).shipPartId = shipPartId;
                    }
                }
            }
        }

    }

    private boolean isCellExist(int row, char col) {
        return seaTable.containsKey(row) && seaTable.get(row).containsKey(col);
    }

    public void AttackCell(int row, char col) {

        if (isCellExist(row, col)) {
            var currentCell = seaTable.get(row).get(col);
            if (currentCell.ship != null) {
                currentCell.ship.GetDamageAtPart(currentCell.shipPartId);
            }
        }

    }

    public ArrayList<Character> getColsKeys() {
        return new ArrayList<>(seaTable.get(FIRST_NUMBER).keySet());
    }

    public ArrayList<Integer> getRowsKeys() {
        return new ArrayList<>(seaTable.keySet());
    }

    final char FIRST_ALPHABET_SYMBOL = 'A';
    final int FIRST_NUMBER = 1;

    private int width;
    private int height;

}
