package NavalBattleGameViewer;

import NavalBattleGame.GameEnums.GameState;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.ConsoleUI.UItemplates.ConsoleIntro;
import NavalBattleGameViewer.UI.ConsoleUI.UItemplates.ConsoleJoinMenu;
import NavalBattleGameViewer.UI.ConsoleUI.UItemplates.ConsolePlayerSetupMenu;
import NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView.ConsoleRoundViewer;
import NavalBattleGameViewer.UI.Printable;
import NavalBattleGameViewer.UI.ConsoleUI.UItemplates.ConsoleMainMenu;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class NavalBattleGameConsoleViewer {

    public NavalBattleGameConsoleViewer(NavalBattleGame game, int width, int height) {
        this.game = game;

        this.views.put(GameState.MainMenu, new ConsoleMainMenu(game, width, height));
        this.views.put(GameState.SetPlayerName, new ConsolePlayerSetupMenu(game, width, height));
        this.views.put(GameState.Intro, new ConsoleIntro(width, height));
        this.views.put(GameState.Round, new ConsoleRoundViewer(game, width, height));
        this.views.put(GameState.JoinToRoundMenu, new ConsoleJoinMenu(game, width, height));

        viewerSize.x = width;
        viewerSize.y = height;

        try {
            initTerminal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clearScreen();
    }

    public void closeViewer() {
        for (var view: views.values()) {
            if (view instanceof Canvas<?>)  {
                for (var uiElement: ((Canvas<?>)view).getUIElementsList()) {
                    uiElement.stopStateMachine();
                }
            }
        }
    }

    public void processGame() {
        drawThread.start();
        while (game.getCurrentState() != GameState.Exit) {
                processInput();
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

        drawThread = new Thread(() -> {
            while (!drawThread.isInterrupted() && game.getCurrentState() != GameState.Exit) {
                processDraw();
            }
        });
    }

    private void closeTerminal() throws IOException {
        this.terminal.close();
    }

    private void redisplay() {
        setTerminalCarriagePosition(0,0);
        outputInConsole();
    }

    private void processDraw() {
        if (drawing) {
            var currentView = views.get(game.getCurrentState());
            if (currentView == null) {
                outputText = "There is no view with current state";
                redisplay();
                return;
            }
            outputText = currentView.getPrintableString();
            redisplay();
        }
    }

//    private String processInput() {
//        setTerminalCarriagePosition(0, viewerSize.y - 1);
//        return lineReader.readLine(">> ");
//    }

    private void processInput() {
        if (views.get(game.getCurrentState()) instanceof InputListener) {
            if (drawing) {
                lineReader.readLine();
            } else {
                setTerminalCarriagePosition(0, viewerSize.y - 1);
                String input = lineReader.readLine(">> ");

                var currentView = views.get(game.getCurrentState());
                if (input != null && currentView instanceof InputListener) {
                    ((InputListener) currentView).onInput(input);
                }
            }
            drawing = !drawing;
        }
    }

    void clearScreen() {
        terminal.writer().print("\033[2J");
    }

    void outputInConsole() {
        terminal.writer().print(outputText);
    }

    void setTerminalCarriagePosition(int x, int y) {
        terminal.writer().printf("\033[%d;%dH", y, x);
    }

    Thread drawThread;
    boolean drawing = true;

    String outputText = "";
    Coord2D viewerSize = new Coord2D();

    NavalBattleGame game;
    HashMap<GameState, Printable> views = new HashMap<>();

    Terminal terminal;
    LineReader lineReader;

    ArrayList<java.util.EventListener> listeners = new ArrayList<>();

}
