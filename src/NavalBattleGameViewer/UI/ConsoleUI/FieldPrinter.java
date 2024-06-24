package NavalBattleGameViewer.UI.ConsoleUI;

import NavalBattleGame.GameElements.SeaCellInfo;
import NavalBattleGame.GameElements.SeaField;

import java.util.ArrayList;

public class FieldPrinter {

    public static String getFieldPrint(SeaField field) {
        return getFieldPrint(field, false);
    }

    public static String getFieldPrint(SeaField field, boolean hideShips) {


        var colsKeys = field.makeColsKeysList();
        var rowsKeys = field.makeRowsKeysList();
        StringBuilder fieldStr = new StringBuilder();

        int maxDigitsCountOfRowKey = rowsKeys.getLast().toString().length();

        fieldStr.append(" ".repeat(maxDigitsCountOfRowKey));
        fieldStr.append(getColsKeysString(colsKeys));
        fieldStr.append('\n');
        for (var rowKey: rowsKeys) {
            fieldStr.append(" ".repeat(maxDigitsCountOfRowKey - rowKey.toString().length()));
            fieldStr.append(rowKey);
            for (var colKey: colsKeys) {
                SeaCellInfo cellInfo = field.getSeaTable().get(rowKey).get(colKey);
                if (cellInfo.getShip() == null) {
                    if (cellInfo.isShelled()) {
                        fieldStr.append(String.valueOf(SHELLED_CELL_SYMBOL).repeat(CELL_WIDTH));
                    } else {
                        if ((colKey - colsKeys.getFirst()) % 2 == 0) {
                            fieldStr.append(String.valueOf(FIRST_WAVE_SYMBOL).repeat(CELL_WIDTH));
                        } else {
                            fieldStr.append(String.valueOf(SECOND_WAVE_SYMBOL).repeat(CELL_WIDTH));
                        }
                    }
                } else {
                    if (cellInfo.getShip().checkIsShipDestroyed()) {
                        fieldStr.append(String.valueOf(DESTROYED_SHIP_SYMBOL).repeat(CELL_WIDTH));
                    } else {
                        if (cellInfo.getShip().partAtId(cellInfo.getShipPartId()).isDestroyed()) {
                            fieldStr.append(String.valueOf(DESTROYED_SHIP_PART_SYMBOL).repeat(CELL_WIDTH));
                        } else {
                            if (!hideShips) {
                                fieldStr.append(String.valueOf(SHIP_PART_SYMBOL).repeat(CELL_WIDTH));
                            } else {
                                if ((colKey - colsKeys.getFirst()) % 2 == 0) {
                                    fieldStr.append(String.valueOf(FIRST_WAVE_SYMBOL).repeat(CELL_WIDTH));
                                } else {
                                    fieldStr.append(String.valueOf(SECOND_WAVE_SYMBOL).repeat(CELL_WIDTH));
                                }
                            }
                        }
                    }
                }
            }
            fieldStr.append(rowKey);
            fieldStr.append('\n');
        }
        fieldStr.append(" ".repeat(maxDigitsCountOfRowKey));
        fieldStr.append(getColsKeysString(colsKeys));

        return fieldStr.toString();
    }

    static private String getColsKeysString(ArrayList<Character> colsKeys) {
        StringBuilder rowKeysStr = new StringBuilder();

        for (var colKey: colsKeys) {
            rowKeysStr.append(" ".repeat((int)Math.floor((double)(CELL_WIDTH-1)/2)));
            rowKeysStr.append(colKey);
            rowKeysStr.append(" ".repeat((int)Math.ceil((double)(CELL_WIDTH-1)/2)));
        }

        return rowKeysStr.toString();
    }

    private static final int CELL_WIDTH = 2;

    private static final char FIRST_WAVE_SYMBOL = '_';
    private static final char SECOND_WAVE_SYMBOL = '-';
    private static final char SHIP_PART_SYMBOL = '#';
    private static final char DESTROYED_SHIP_PART_SYMBOL = '/';
    private static final char DESTROYED_SHIP_SYMBOL = 'X';
    private static final char SHELLED_CELL_SYMBOL = 'u';
}
