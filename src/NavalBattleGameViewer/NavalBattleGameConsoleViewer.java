package NavalBattleGameViewer;

import NavalBattleGame.GameEnums.GameState;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.UI.UIelements.Button;
import NavalBattleGameViewer.UI.UIelements.TextBlock;
import NavalBattleGameViewer.UI.UIstate;
import NavalBattleGameViewer.UI.UItemplates.MainMenu;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;

public class NavalBattleGameConsoleViewer {

    public NavalBattleGameConsoleViewer(NavalBattleGame game, int width, int height) {
        this.game = game;
        mainMenu = new MainMenu(game);
        viewerSize.x = width;
        viewerSize.y = height;

        try {
            terminal = TerminalBuilder.builder().system(true).build();
            terminal.puts(InfoCmp.Capability.cursor_invisible);
            terminal.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clearOutputBuffer();
    }

    public void viewGame() {
        while (game.getCurrentState() != GameState.Exit) {

            if (game.getCurrentState() == GameState.MainMenu) {
                for (var uiElement : mainMenu.getUIelements()) {
                    var elementPos = uiElement.getPosition();
                    setBufferCarriagePosition(elementPos.x , elementPos.y);

                    if (uiElement instanceof Button) {
                        if (uiElement.getCurrentState() == UIstate.Hover) {
                            setBufferCarriagePosition(elementPos.x - 2, elementPos.y);
                            addTextToBuffer("# ");
                        }
                        addTextToBuffer(((Button) uiElement).getButtonText());
                    } else if (uiElement instanceof TextBlock) {
                        addTextToBuffer(((TextBlock) uiElement).getText());
                    }
                }
            }

            setTerminalCarriagePosition(0,0);
            outputInConsole();
            clearOutputBuffer();
        }

    }

    void outputInConsole() {
        terminal.writer().print(outputBuffer);
    }

    void setTerminalCarriagePosition(int x, int y) {
        terminal.writer().printf("\033[%d;%dH", x, y);
    }

    void addTextToBuffer(String text) {
        for (int charId = 0; charId < text.length(); charId++) {
            this.outputBuffer.setCharAt((viewerSize.x + 1) * bufferCarriagePos.y + bufferCarriagePos.x, text.charAt(charId));
            bufferCarriagePos.x++;
        }
    }

    void setBufferCarriagePosition(int x, int y) {
        this.bufferCarriagePos.x = x;
        this.bufferCarriagePos.y = y;
    }

    void clearOutputBuffer() {
        outputBuffer.delete(0, outputBuffer.length());
        outputBuffer.insert(0, (" ".repeat(viewerSize.x) + "\n").repeat(viewerSize.y));
    }

    StringBuffer outputBuffer = new StringBuffer();
    Coord2D bufferCarriagePos = new Coord2D();
    Coord2D viewerSize = new Coord2D();

    NavalBattleGame game;
    MainMenu mainMenu;

    Terminal terminal;
}
