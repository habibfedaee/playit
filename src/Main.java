import javazoom.jl.player.Player;
import java.io.FileInputStream;

import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) {

         String filePath = "/Users/habibullahfedaee/Music/03_Dunya_Guzaran.mp3"; // change this to the actual file you want to play

        try{
            FileInputStream fis = new FileInputStream(filePath);
            Player player = new Player(fis);
            player.play();
            System.out.println("Finished Playing.");
        } catch (javazoom.jl.decoder.JavaLayerException e){
            System.out.println("Error! MP3 file not found at: "+filePath);
            e.printStackTrace();
        } catch (java.io.FileNotFoundException e){
            System.out.println("Error, Cannot find the audio file! " +filePath);
            e.printStackTrace();
        }
    }
}