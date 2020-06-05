import java.util.ArrayList;

public class SlotsResult {
    private ArrayList<String> wheels;
    private int winAmount;
    private boolean hasWon;
    private boolean bonus;

    public SlotsResult(ArrayList<String> wheels, int winAmount, boolean hasWon, boolean bonus) {
        this.wheels = wheels;
        this.winAmount = winAmount;
        this.hasWon = hasWon;
        this.bonus = bonus;
    }

    public ArrayList<String> getWheels() {
        return wheels;
    }

    public int getWinAmount() {
        return winAmount;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public boolean isBonus() {
        return bonus;
    }
}
