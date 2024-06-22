package NavalBattleGame.GameElements;

import NavalBattleGame.GameEnums.ShipOrientation;

import java.util.ArrayList;
import java.util.Collections;
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

    public boolean tryPlaceShipInCells(Ship ship, ShipOrientation shipOrientation, int row, char col) {
        int colStep = 0, rowStep = 0;
        switch (shipOrientation) {
            case Right -> colStep = 1;
            case Left -> colStep = -1;
            case Up -> rowStep = -1;
            case Down -> rowStep = 1;
        }
        char currCol = col;
        int currRow = row;
        for (int shipPartId = 0; shipPartId < ship.getShipSize(); shipPartId++,
                currCol = (char)(currCol + colStep),
                currRow += rowStep) {
            if (!checkIsCellAvailable(currRow, currCol)) {
                return false;
            }
        }

        currCol = col;
        currRow = row;
        for (int shipPartId = 0; shipPartId < ship.getShipSize(); shipPartId++,
                currCol = (char)(currCol + colStep),
                currRow += rowStep) {
            seaTable.get(currRow).get(currCol).ship = ship;
            seaTable.get(currRow).get(currCol).shipPartId = shipPartId;
        }

        return true;
    }

    private boolean checkIsCellAvailable(int row, char col) {
        // Проверка существования строки и столбца перед получением значения
        if (!seaTable.containsKey(row) || !seaTable.getOrDefault(row, new HashMap<>()).containsKey(col)) {
            return false;
        }

        // Проверка соседних клеток
        for (int checkedRow = row - 1; checkedRow <= row + 1; checkedRow++) {
            for (char checkedCol = (char)(col - 1); checkedCol <= col + 1; checkedCol++) {
                // Получение значения, если ключ существует, иначе вернется null
                SeaCellInfo cell = seaTable.getOrDefault(checkedRow, new HashMap<>()).getOrDefault(checkedCol, null);
                if (cell != null && cell.ship != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void AttackCell(int row, char col) {

        if (checkIsCellAvailable(row, col)) {
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
