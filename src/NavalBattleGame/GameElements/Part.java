package NavalBattleGame.GameElements;

public class Part {

    private boolean isDestroyed = false;

    public boolean getIsDestroyed() {
        return isDestroyed;
    }

    public void GetDamage() {
        this.isDestroyed = true;
    }

}
