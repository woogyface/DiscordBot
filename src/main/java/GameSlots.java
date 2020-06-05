import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class GameSlots {
    //:heart: :bell: :star: :cherries: :watermelon: :strawberry: :tangerine: :grapes: :apple:
    private List<String> emotes = Arrays.asList(
            ":heart:",
            ":bell:",
            ":star:",
            ":cherries:",
            ":watermelon:",
            ":strawberry:",
            ":tangerine:",
            ":grapes:",
            ":apple:"
    );

    private int[] chances = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private int[] winMultiplier = new int[] { 255, 128, 64, 32, 16, 8, 4, 2, 1 };

    public SlotsResult roll(int bet) {
        int numWheels = 5;
        int minWheels = 3;
        //Create Wheel
        ArrayList<String> wheel = new ArrayList<>();
        for(int i = 0; i < chances.length; i++)
        {
            for(int j = 0; j < chances[i]; j++) {
                wheel.add(emotes.get(i));
            }
        }

        //Shuffle Wheel
        Collections.shuffle(wheel);

        Random r = new Random();
        ArrayList<String> wheels = new ArrayList<>();
        for(int i = 0; i < numWheels; i++) {
            wheels.add(wheel.get(r.nextInt(wheel.size())));
        }

        List<String> wheelsSorted = List.copyOf(wheels);
        Collections.sort(wheelsSorted);
        boolean hasWon = false;
        boolean bonus = false;
        String previous = wheelsSorted.get(0);
        int count = 0;
        for(int i = 0; i < wheelsSorted.size(); i++) {
            String check = wheelsSorted.get(i);
            for(int j = 1; j < wheelsSorted.size(); j++) {
                if (wheelsSorted.get(j).equals(check)) {
                    count++;
                    i = j;
                }
            }

            if (count >= minWheels) {
                hasWon = true;
                if (count == numWheels) {
                    bonus = true;
                }
                break;
            }
        }

        /*
        boolean hasWon = false;
        if(index != -1) {
            win = bet * winMultiplier[index];
            hasWon = true;
        }

        boolean bonus = false;
        if(wheels.get(0).equals(wheels.get(1)) && wheels.get(0).equals(wheels.get(2))) {
            win *= 2;
            bonus = true;
        }
        */

        return new SlotsResult(wheels, win, hasWon, bonus);
    }
}
