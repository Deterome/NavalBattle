package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameUsers.NavalBattleAI;
import NavalBattleGame.GameUsers.PlayerAction;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGame.ToolsForGame.JsonParser;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleButton;
import NavalBattleGameViewer.UI.ConsoleUI.FieldPrinter;
import NavalBattleGameViewer.UI.ConsoleUI.PrintConstructor;
import NavalBattleGameViewer.UI.Printable;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Optional;

enum MatchMenuElements {
    AIHelpButton
}

public class ConsoleMatchMenu extends ConsoleCanvas<MatchMenuElements> implements Printable, InputListener {

    public ConsoleMatchMenu(NavalBattleGame game, int width, int height) {
        super(width, height);

        this.game = game;

        initializeElements();
    }

    private void initializeElements() {
        var aiHelpButton = new ConsoleButton("AI help [help]", 10,1);
        aiHelpButton.addListener(() -> {
            var player =  game.getCurrentRound().findPlayerByUser(game.getUser());

            var coordinates = NavalBattleAI.analyseFieldAndGetAttackCoords(game.getCurrentRound().findNextPlayerToAct().getField());
            game.getCurrentRound().makeAction(player, PlayerAction.Attack, coordinates.getKey(), coordinates.getValue());
        });
        aiHelpButton.setPosition(90, 0);
        UIElementsMap.put(MatchMenuElements.AIHelpButton, aiHelpButton);
        focusableElementsMap.put(MatchMenuElements.AIHelpButton, aiHelpButton);
    }

    @Override
    public void onInput(String enteredText) {
        if (game.getCurrentRound().findPlayerByUser(game.getUser()) != null) {
            switch (enteredText) {
                case "help" -> pressButton(MatchMenuElements.AIHelpButton);
                default -> {
                    var player =  game.getCurrentRound().findPlayerByUser(game.getUser());

                    try {
                        Optional<String> coordinatesJsonStr = Optional.ofNullable(JsonParser.createJsonStringFromCoordinatesString(enteredText));
                        coordinatesJsonStr.ifPresent(coordsJsonStr -> {
                            try {
                                var coordinates = JsonParser.makeCoordinatesFromJsonString(coordsJsonStr);
                                game.getCurrentRound().makeAction(player, PlayerAction.Attack, coordinates.getKey(), coordinates.getValue());
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    @Override
    public String getPrintableString() {
        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(canvasSize.x, canvasSize.y);
        printConstructor.putTextInPosition(super.getPrintableString(), 0, 0);

        addFieldToPrint(printConstructor);

        return printConstructor.getPrint();
    }

    private void addFieldToPrint(PrintConstructor printConstructor) {
        var players = game.getCurrentRound().createPlayersList();
        int printXPos = 5;
        int printXOffset = 45;
        for (var player: players) {
            String playerActing = "";
            if (game.getCurrentRound().findActingPlayer() == player) {
                playerActing += "acting-> ";
            }
            printConstructor.putTextInPosition(playerActing + player.getNickname(), printXPos + 15 - playerActing.length(), 2);
            if (game.getCurrentRound().findPlayerByUser(game.getUser()) != null) {
                printConstructor.putTextInPosition(FieldPrinter.getFieldPrint(player.getField(), game.getCurrentRound().findPlayerByUser(game.getUser()) != player), printXPos, 3);

            } else {
                printConstructor.putTextInPosition(FieldPrinter.getFieldPrint(player.getField()), printXPos, 3);
            }
            printConstructor.putTextInPosition("Remaining ships in field: " + player.countRemainingShips(),printXPos + 5, 22);
            printXPos += printXOffset;
        }
    }
    NavalBattleGame game;
}
