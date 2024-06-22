package NavalBattleGame.GameElements;

import NavalBattleGame.GameEnums.ShipOrientation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum AttackResult {
    Miss,
    Hit,
    Destroyed
}

public class SeaField {

    public SeaField(int width, int height) {
        this.width = width;
        this.height = height;

        clearField();
    }

    public void clearField() {
        for (int rowNumber = FIRST_NUMBER; rowNumber <= height; rowNumber++) {
            seaTable.put(rowNumber, new HashMap<>());

            for (char colLetter = FIRST_ALPHABET_SYMBOL; colLetter < FIRST_ALPHABET_SYMBOL + width; colLetter++) {
                seaTable.get(rowNumber).put(colLetter, new SeaCellInfo());
            }
        }
    }

    public HashMap<Integer, HashMap<Character, SeaCellInfo>> getSeaTable() {
        return seaTable;
    }

    private HashMap<Integer, HashMap<Character, SeaCellInfo>> seaTable = new HashMap<Integer, HashMap<Character, SeaCellInfo>>();

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

    private boolean isCellExist(int row, char col) {
        return seaTable.containsKey(row) && seaTable.getOrDefault(row, new HashMap<>()).containsKey(col);
    }

    private boolean checkIsCellAvailable(int row, char col) {
        // Проверка существования строки и столбца перед получением значения
        if (!isCellExist(row, col)) {
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

    private void bombCell(int row, char col) {
        if (!isCellExist(row, col)) {
            return;
        }

        for (int checkedRow = row - 1; checkedRow <= row + 1; checkedRow++) {
            for (char checkedCol = (char)(col - 1); checkedCol <= col + 1; checkedCol++) {
                // Получение значения, если ключ существует, иначе вернется null
                SeaCellInfo cell = seaTable.getOrDefault(checkedRow, new HashMap<>()).getOrDefault(checkedCol, null);
                if (cell != null) {
                    cell.attackCell();
                }
            }
        }
    }

    private Map.Entry<Integer, Character> findShipBegin(Ship ship, int partRow, char partCol) {
        if (!seaTable.containsKey(partRow) ||
                !seaTable.getOrDefault(partRow, new HashMap<>()).containsKey(partCol)) {
            return null;
        }

        var currentCell = seaTable.get(partRow).get(partCol);

        for (int xOffsetRatio = -1; xOffsetRatio <= 1; xOffsetRatio += 2) {
            int newRow = partRow + (currentCell.shipPartId * xOffsetRatio);
            if (isCellExist(newRow, partCol)) {
                if (seaTable.get(newRow).get(partCol).ship == ship) {
                    return new AbstractMap.SimpleEntry<>(newRow, partCol);
                }
            }
        }

        for (int yOffsetRatio = -1; yOffsetRatio <= 1; yOffsetRatio += 2) {
            char newCol = (char)(partCol + (currentCell.shipPartId * yOffsetRatio));
            if (isCellExist(partRow, newCol)) {
                if (seaTable.get(partRow).get(newCol).ship == ship) {
                    return new AbstractMap.SimpleEntry<>(partRow, newCol);
                }
            }
        }

        return null;
    }

    private void bombShip(Ship ship, int explosionStartRow, char explosionStartCol) {
        if (!seaTable.containsKey(explosionStartRow) ||
                !seaTable.getOrDefault(explosionStartRow, new HashMap<>()).containsKey(explosionStartCol)) {
            return;
        }

        var shipBegin = findShipBegin(ship, explosionStartRow, explosionStartCol);
        if (shipBegin == null) return;

        int[] steps = {-1, 1};
        if (ship.getShipSize() == 1) {
            bombCell(explosionStartRow, explosionStartCol);
        } else {
            for (var currStep: steps) {
                int newRow = shipBegin.getKey() + currStep;
                if (isCellExist(newRow, explosionStartCol)) {
                    if (seaTable.get(newRow).get(explosionStartCol).ship == ship) {
                        int currRowOffset = 0;
                        for (var part: ship.parts) {
                            bombCell(shipBegin.getKey() + currRowOffset, explosionStartCol);
                            currRowOffset += currStep;
                        }
                        return;
                    }
                }

                char newCol = (char)(shipBegin.getValue() + currStep);
                if (isCellExist(explosionStartRow, newCol)) {
                    if (seaTable.get(explosionStartRow).get(newCol).ship == ship) {
                        int currColOffset = 0;
                        for (var part: ship.parts) {
                            bombCell(explosionStartRow, (char)(shipBegin.getValue() + currColOffset));
                            currColOffset += currStep;
                        }
                        return;
                    }
                }
            }
        }
    }

    public void attackCell(int row, char col) {
        if (!seaTable.containsKey(row) || !seaTable.getOrDefault(row, new HashMap<>()).containsKey(col)) {
            return;
        }

        var currentCell = seaTable.get(row).get(col);
        switch (currentCell.attackCell()) {
            case Destroyed -> {
                bombShip(currentCell.ship, row, col);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private int width;
    private int height;

}
