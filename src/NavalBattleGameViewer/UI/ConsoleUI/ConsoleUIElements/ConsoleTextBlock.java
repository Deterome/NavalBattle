package NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements;

import NavalBattleGameViewer.UI.Printable;
import NavalBattleGameViewer.UI.UIelements.TextBlock;

public class ConsoleTextBlock extends TextBlock implements Printable {

    public ConsoleTextBlock(String text, int width, int height) {
        super(text, width, height);
    }

    @Override
    public String getPrintableStringOfElement() {
        return this.getText();
    }
}
