package NavalBattleGameViewer.UI;

import NavalBattleGameViewer.Coord2D;
import StateMachine.StateMachine;

public abstract class UIElement extends StateMachine<UIstate, UIevents> {

    @Override
    public void stopStateMachine() {
        this.stopScheduler();
    }

    public UIElement(int width, int height) {
        super(UIstate.Default);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void initTransitionTable() {
        addNewTransitionToTable(UIstate.Default, UIevents.EnteredArea, UIstate.Hover);
        addNewTransitionToTable(UIstate.Hover, UIevents.ExitedArea, UIstate.Default);
        addNewTransitionToTable(UIstate.Hover, UIevents.Pressed, UIstate.Active);
        addNewTransitionToTable(UIstate.Active, UIevents.Released, UIstate.Hover);
    }

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
    }

    public Coord2D getPosition() {
        return position;
    }

    int width;
    int height;

    Coord2D position = new Coord2D();

}
