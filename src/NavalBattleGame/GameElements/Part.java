package NavalBattleGame.GameElements;

public class Part {

    private boolean destroyed = false;

    public boolean isDestroyed() {
        return destroyed;
    }

    public void getDamage() {
        this.destroyed = true;
    }

}
