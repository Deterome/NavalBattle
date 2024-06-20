package NavalBattleGameViewer.UI.ConsoleUI;

import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.Printable;
import NavalBattleGameViewer.UI.UIElement;

import java.util.ArrayList;

public abstract class ConsoleCanvas<E extends Enum<E>> extends Canvas<E> implements Printable {


    public ConsoleCanvas(int width, int height) {
        super(width, height);
    }

    @Override
    public String getPrintableString() {
        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(this.canvasSize.x, this.canvasSize.y);
        ArrayList<UIElement> elementsList = getUIElementsList();
        for (UIElement uiElement : elementsList) {
            if (uiElement instanceof Printable) {
                var elementPos = uiElement.getPosition();
                printConstructor.putTextInPosition(((Printable) uiElement).getPrintableString(), elementPos.x, elementPos.y);
            }
        }
        return printConstructor.getPrint();
    }
}
