package legends;

import java.util.ArrayList;
import java.util.Scanner;

// Everything the game prints or reads goes through this class.
public class Console {
    private Scanner scanner = new Scanner(System.in);

    public void println(String text) {
        System.out.println(text);
    }

    public void println() {
        System.out.println();
    }

    // Typing Q at any prompt quits the game.
    public String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            quit();
        }
        String line = scanner.nextLine().trim();
        if (line.equalsIgnoreCase("q")) {
            quit();
        }
        return line;
    }

    // Keeps asking until the player types a number from min to max.
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int number = Integer.parseInt(line);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // not a number, ask again below
            }
            println("  Please enter a number from " + min + " to " + max + " (or Q to quit).");
        }
    }

    // Generic menu: works for a list of anything (items, monsters, ...).
    // Shows the list, asks for a number, and returns the chosen object (null = cancelled).
    public <T> T chooseFrom(String title, ArrayList<T> options) {
        println(title);
        for (int i = 0; i < options.size(); i++) {
            println("  " + (i + 1) + ") " + options.get(i));
        }
        int choice = readInt("> ", 0, options.size());
        if (choice == 0) {
            return null;
        }
        return options.get(choice - 1);
    }

    private void quit() {
        System.out.println();
        System.out.println("Thanks for playing Monsters and Heroes!");
        System.exit(0);
    }
}
