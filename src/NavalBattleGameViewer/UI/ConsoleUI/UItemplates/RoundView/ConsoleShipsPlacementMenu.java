package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameEnums.ShipPlacement;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;
import NavalBattleGameViewer.UI.ConsoleUI.FieldPrinter;
import NavalBattleGameViewer.UI.ConsoleUI.PrintConstructor;
import NavalBattleGameViewer.UI.Printable;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum ShipsPlacementMenuElements {
    InfoText
}

public class ConsoleShipsPlacementMenu extends ConsoleCanvas<ShipsPlacementMenuElements> implements Printable, InputListener {

    public ConsoleShipsPlacementMenu(NavalBattleGame game, int width, int height) {
        super(width, height);

        this.game = game;

        initializeElements();
    }

    private void initializeElements() {
        var infoText = new ConsoleTextBlock("To place the ship in a cell, enter the coordinates with the combined letter and cell number\nCoordinates of the ship starts at symbol 'O'\nInput example: A4", 10, 1);
        infoText.setPosition(0,0);
        this.UIElementsMap.put(ShipsPlacementMenuElements.InfoText, infoText);
    }

    @Override
    public String getPrintableString() {
        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(canvasSize.x, canvasSize.y);
        printConstructor.putTextInPosition(super.getPrintableString(), 0, 0);

        addFieldToPrint(printConstructor);
        addAvailableShipsToPrint(printConstructor);

        return printConstructor.getPrint();
    }

    @Override
    public void onInput(String enteredText) {
        if ("rot".equals(enteredText)) {
            currentShipPlacements = currentShipPlacements == ShipPlacement.Horizontal ?
                    ShipPlacement.Vertical : ShipPlacement.Horizontal;
        } else {
            var player =  game.getCurrentRound().getPlayerByUser(game.getUser());

            var coordinates = Optional.ofNullable(parseCoordinates(enteredText));
            coordinates.ifPresent(coordinate -> {
                player.getField().placeShipInCells(player.pickFirstAvailableShip(), currentShipPlacements,coordinate.getKey(), coordinate.getValue());
            });
        }
    }

    private Map.Entry<Integer, Character> parseCoordinates(String coordinateString) {
        Pattern pattern = Pattern.compile("([A-Z])([0-9]+)");
        Matcher matcher = pattern.matcher(coordinateString);
        if (matcher.find()) {
            var field = game.getCurrentRound().getPlayerByUser(game.getUser()).getField();
            Map.Entry<Integer, Character> coordinates = new AbstractMap.SimpleEntry<>(Integer.parseInt(matcher.group(2)), matcher.group(1).charAt(0));
            var colsKeys = field.getColsKeys();
            var rowsKeys = field.getRowsKeys();
            if (colsKeys.contains(coordinates.getValue()) && rowsKeys.contains(coordinates.getKey())) {
                return coordinates;
            }
        }
        return null;
    }

    private void addFieldToPrint(PrintConstructor printConstructor) {
        var field = game.getCurrentRound().getPlayerByUser(game.getUser()).getField();

        printConstructor.putTextInPosition(FieldPrinter.getFieldPrint(field), 41, 2);
    }

    private void addAvailableShipsToPrint(PrintConstructor printConstructor) {
        var player =  game.getCurrentRound().getPlayerByUser(game.getUser());
        var availableShips = player.getAvailableShips();
        final char SHIP_PART_SYMBOL = '#';
        final char MAIN_SHIP_PART_SYMBOl = 'O';

        StringBuilder shipsList = new StringBuilder();
        for (var entry: availableShips.entrySet()) {
            StringBuilder shipStr = new StringBuilder();
            var parts = entry.getValue().getFirst().getParts();
            for (var part: parts) {
                shipStr.append(SHIP_PART_SYMBOL);
            }
            shipStr.append(" - ");
            shipStr.append(entry.getValue().size());

            shipsList.append(shipStr);
            shipsList.append(String.valueOf('\n').repeat(2));
        }

        printConstructor.putTextInPosition(shipsList.toString(), 10, 6);

        printConstructor.putTextInPosition("Enter coordinates or rotate ship [rot]", 41, 20);


        var pickedShipParts = player.getFirstAvailableShip().getParts();
        StringBuilder pickedShipStr = new StringBuilder();

        for (int partId = 0; partId < pickedShipParts.size(); partId++) {
            if (partId == 0) {
                pickedShipStr.append(MAIN_SHIP_PART_SYMBOl);
            } else {
                pickedShipStr.append(SHIP_PART_SYMBOL);
            }
            if (currentShipPlacements.equals(ShipPlacement.Vertical)) {
                pickedShipStr.append('\n');
            }
        }

        printConstructor.putTextInPosition(pickedShipStr.toString(), 57, 21);
    }

    NavalBattleGame game;

    ShipPlacement currentShipPlacements = ShipPlacement.Horizontal;
}
