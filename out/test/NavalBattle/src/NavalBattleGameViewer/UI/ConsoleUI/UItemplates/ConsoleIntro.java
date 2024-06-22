package NavalBattleGameViewer.UI.ConsoleUI.UItemplates;

import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;

enum IntroElements {
    IntroText
}

public class ConsoleIntro extends ConsoleCanvas<IntroElements> {

    public ConsoleIntro(int width, int height) {
        super(width, height);
        initializeUIElements();
    }
    private void initializeUIElements() {
        var introText = new ConsoleTextBlock("NAVAL BATTLE GAME", 10, 1);
        introText.setPosition(50,12);
        this.UIElementsMap.put(IntroElements.IntroText, introText);
    }

}
