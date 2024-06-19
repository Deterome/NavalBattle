package NavalBattleGameViewer.UI.ConsoleUI.UItemplates;

import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;

enum IntroElements {
    IntroText
}

public class ConsoleIntro extends Canvas<IntroElements> {

    public ConsoleIntro() {
        initializeUIElements();
    }
    private void initializeUIElements() {
        var introText = new ConsoleTextBlock("NAVAL BATTLE GAME", 50, 20);
        introText.setPosition(50,12);
        this.UIElementsMap.put(IntroElements.IntroText, introText);

    }

}
