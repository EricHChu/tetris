import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.util.Arrays;

//File Reader Class
public class FileReader{
    //String Array with piece names
    private final String[] pieces = {"i","o","j","l","s","t","z"};
    //Empty Constructor
    public FileReader(){
    }
    //Offset Grabber
    public int[][][] getOffset(int type) throws Exception{
        //Pulls offsets from file according to piece type
        String fileName = ".\\Offsets\\" + pieces[type] + "Offset.txt";
        File offsetFile = new File(fileName);
        Scanner input = new Scanner(offsetFile);
        int[][][] offsets = new int[4][4][2];
        for(int orientation = 0; orientation < 4;orientation++){
            for(int piece = 0; piece < 4;piece++){
                offsets[orientation][piece][0] = input.nextInt();
                offsets[orientation][piece][1] = input.nextInt();
            }
        }
        return offsets;
    }
    //High Score Grabber
    public int getHighScore()throws Exception{
        File highScoreFile = new File(".\\HighScore\\highScore.txt");
        Scanner input = new Scanner(highScoreFile);
        input.nextLine();
        int highScore = input.nextInt();
        input.close();
        return highScore;
    }
    //High Score Name Grabber
    public String getHighScorer()throws Exception{
        File highScoreFile = new File(".\\HighScore\\highScore.txt");
        Scanner input = new Scanner(highScoreFile);
        String highScorer = input.nextLine();
        input.close();
        return highScorer;
    }
    //High Score Name & Score Setter
    public void setHighScore(String name, int score)throws Exception{
        File highScoreFile = new File(".\\HighScore\\highScore.txt");
        PrintWriter output = new PrintWriter(highScoreFile);
        output.println(name + "\n" + score);
        output.close();
    }
}