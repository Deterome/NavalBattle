package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameEnums.ShipOrientation;
import NavalBattleGame.GameUsers.CoordinatesParser;
import NavalBattleGame.GameUsers.NavalBattleAI;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;
import NavalBattleGameViewer.UI.ConsoleUI.FieldPrinter;
import NavalBattleGameViewer.UI.ConsoleUI.PrintConstructor;
import NavalBattleGameViewer.UI.Printable;

import java.util.Optional;

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
        shipsPlaced = game.getCurrentRound().getPlayerByUser(game.getUser()).getAvailableShips() == null;

        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(canvasSize.x, canvasSize.y);
        printConstructor.putTextInPosition(super.getPrintableString(), 0, 0);

        addFieldToPrint(printConstructor);
        if (!shipsPlaced) {
            addAvailableShipsToPrint(printConstructor);
        } else {
            addReadyButtonToPrint(printConstructor);
        }

        return printConstructor.getPrint();
    }

    public void addReadyButtonToPrint(PrintConstructor printConstructor) {
        var currentRound = game.getCurrentRound();
        if (!currentRound.isPlayerReady(currentRound.getPlayerByUser(game.getUser()))) {
            printConstructor.putTextInPosition("Ready [ready]", 57, 23 );
        }
    }

    @Override
    public void onInput(String enteredText) {
        if (shipsPlaced) {
            if ("ready".equals(enteredText)) {
                game.getCurrentRound().setPlayerReadiness(game.getCurrentRound().getPlayerByUser(game.getUser()), true);
                if (game.isConnectedToRoundServer()) {
                    game.getConnectionToRound().notifyServerOfReadiness();
                }
            }
        } else {
            if ("rot".equals(enteredText)) {
                currentShipOrientation = currentShipOrientation.nextOrientation();
            } else if ("auto".equals(enteredText)) {
                var player =  game.getCurrentRound().getPlayerByUser(game.getUser());
                NavalBattleAI.automaticPlacementOfShipsToField(player.getField(), player.getAvailableShips());
                player.setAvailableShips(null);
            } else {
                var player =  game.getCurrentRound().getPlayerByUser(game.getUser());

                var coordinates = Optional.ofNullable(CoordinatesParser.getCoordinatesByString(enteredText));
                coordinates.ifPresent(coordinate -> {
                    if (player.getField().tryPlaceShipInCells(player.getFirstAvailableShip(), currentShipOrientation,coordinate.getKey(), coordinate.getValue())) {
                        player.pickFirstAvailableShip();
                    }
                });
            }
        }
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

        printConstructor.putTextInPosition("Enter coordinates or rotate ship [rot]. Automatic placement [auto]", 37, 20);

        var pickedShipParts = player.getFirstAvailableShip().getParts();
        StringBuilder pickedShipStr = new StringBuilder();

        for (int partId = 0; partId < pickedShipParts.size(); partId++) {
            if (partId != 0) {
                if (currentShipOrientation.equals(ShipOrientation.Up) ||
                        currentShipOrientation.equals(ShipOrientation.Down)) {
                    pickedShipStr.append('\n');
                }
            }
            pickedShipStr.append(SHIP_PART_SYMBOL);
        }
        if (currentShipOrientation.equals(ShipOrientation.Right) ||
                currentShipOrientation.equals(ShipOrientation.Down)) {
            pickedShipStr.setCharAt(0, MAIN_SHIP_PART_SYMBOl);
        } else {
            pickedShipStr.setCharAt(pickedShipStr.length()-1, MAIN_SHIP_PART_SYMBOl);
        }

        printConstructor.putTextInPosition(pickedShipStr.toString(), 57, 21);
    }
    NavalBattleGame game;

    boolean shipsPlaced = false;

    ShipOrientation currentShipOrientation = ShipOrientation.Right;
}
