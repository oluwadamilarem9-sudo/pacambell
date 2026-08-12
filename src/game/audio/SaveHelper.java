package game.audio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SaveHelper
{
    private static final String SAVE_FILE = "highscore.txt";

    public static int loadHighScore()
    {
        File file = new File(SAVE_FILE);
        if(!file.exists())
            return 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line = reader.readLine();
            return line == null ? 0 : Integer.parseInt(line.trim());
        }
        catch (IOException | NumberFormatException e)
        {
            return 0;
        }
    }

    public static void saveHighScore(int highScore)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE)))
        {
            writer.write(Integer.toString(highScore));
        }
        catch (IOException ignored)
        {
        }
    }
}
