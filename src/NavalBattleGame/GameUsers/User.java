package NavalBattleGame.GameUsers;

public class User {

    public User() {
        this.name = "user";
    }

    public User(String name) {
        this.name = name;
    }

    public User(User user) {
        this.name = user.name;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }


    String name;
    //ArrayList<RoundInformation> statistic;

}
