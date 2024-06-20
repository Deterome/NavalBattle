package NavalBattleGameViewer.UI.ConsoleUI;

import NavalBattleGameViewer.Coord2D;

public class PrintConstructor {

    public PrintConstructor() {
        this.setSize(0, 0);
    }

    public void putTextInPosition(String text, int x, int y) {
        for (int charId = 0; charId < text.length(); charId++) {
            this.printBuffer.setCharAt((printSize.x + 1) * y + x, text.charAt(charId));
            x++;
        }
    }

    public void setSize(int width, int height) {
        this.printSize.x = width;
        this.printSize.y = height;

        clearPrint();
    }

    public String getPrint() {
        return this.printBuffer.toString();
    }

    public void clearPrint() {
        printBuffer.delete(0, printBuffer.length());
        printBuffer.insert(0, (" ".repeat(printSize.x) + "\n").repeat(printSize.y));
    }

    private StringBuffer printBuffer = new StringBuffer();
    private Coord2D printSize = new Coord2D();
    
}
