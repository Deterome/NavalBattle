package NavalBattleGameViewer;

import NavalBattleGame.GameEnums.GameState;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.ConsoleUI.UItemplates.ConsoleIntro;
import NavalBattleGameViewer.UI.Printable;
import NavalBattleGameViewer.UI.ConsoleUI.UItemplates.ConsoleMainMenu;
import NavalBattleGameViewer.UI.UIElement;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NavalBattleGameConsoleViewer {

    public NavalBattleGameConsoleViewer(NavalBattleGame game, int width, int height) {
        this.game = game;

        this.views.put(GameState.MainMenu, new ConsoleMainMenu(game));
        this.views.put(GameState.Intro, new ConsoleIntro());

        viewerSize.x = width;
        viewerSize.y = height;

        try {
            initTerminal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clearOutputBuffer();
        clearScreen();
    }

    public void closeViewer() {
        for (var view: views.values()) {
            for (var uiElement: view.getUIElementsList()) {
                uiElement.StopStateMachine();
            }
        }
    }

    public void processGame() {
        while (game.getCurrentState() != GameState.Exit) {
            var currentView = views.get(game.getCurrentState());
            if (currentView == null) {
                addTextToBuffer("There is no view with current state");
                redisplay();
                return;
            }

            ArrayList<UIElement> elementsList = currentView.getUIElementsList();
            for (UIElement uiElement : elementsList) {
                if (uiElement instanceof Printable) {
                    var elementPos = uiElement.getPosition();
                    setBufferCarriagePosition(elementPos.x , elementPos.y);

                    addTextToBuffer(((Printable) uiElement).getPrintableStringOfElement());
                }
            }
            redisplay();

            if (currentView instanceof InputListener) {
                ((InputListener) currentView).onInput(processInput());
            }
        }

        try {
            closeTerminal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initTerminal() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        terminal.flush();

        lineReader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    private void closeTerminal() throws IOException {
        this.terminal.close();
    }

    private void redisplay() {
        setTerminalCarriagePosition(0,0);
        outputInConsole();
        clearOutputBuffer();
    }

    private String processInput() {
        return lineReader.readLine();
    }

    void clearScreen() {
        terminal.writer().print("\033[2J");
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
    HashMap<GameState, Canvas<?>> views = new HashMap<>();

    Terminal terminal;
    LineReader lineReader;

    ArrayList<java.util.EventListener> listeners = new ArrayList<>();

}
