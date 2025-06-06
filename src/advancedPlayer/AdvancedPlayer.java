package advancedPlayer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AdvancedPlayer {

    private Player player;
    private Thread playerThread;
    private String currentFilePath;
    private boolean isPlaying;

    public AdvancedPlayer(){
        isPlaying=false;
    }

    public void play(String filePath){
        if(isPlaying){
            stop(); // stop current song if playing
        }
        this.currentFilePath = filePath;
        try{
            FileInputStream fis = new FileInputStream(filePath);
            player = new Player(fis); // create a new player instance

            playerThread = new Thread(()->{
               try{
                   System.out.println("Playing: "+currentFilePath);
                   isPlaying=true;
                   player.play(); // this blocks the thread not the main app
                   isPlaying=false; // set to false when playback finishes
                   System.out.println("Finished playing: "+currentFilePath);
               } catch(javazoom.jl.decoder.JavaLayerException e){
                   System.out.println("Error Playing Audio! ");
                   e.printStackTrace();
                   isPlaying = false;
               }
            });
            playerThread.start(); // start the playback in a new thread
        } catch (FileNotFoundException | JavaLayerException e) {
            System.err.println("Error: MP3 file not found at " + filePath);
            e.printStackTrace();
            isPlaying = false;
        }
    }

    // stop method
    public void stop(){
        if(player!=null){
            player.close(); // stops the playback and closes the stream.
            player=null; // clears the player instance
            isPlaying = false;
            System.out.println("Stopped Playing!");
            // You might want to interrupt the thread as well if it's still alive
            if (playerThread != null && playerThread.isAlive()) {
                playerThread.interrupt(); // Attempt to interrupt the thread
            }
        }
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public static void main(String[] args ) throws InterruptedException {
        AdvancedPlayer myPlayer = new AdvancedPlayer();
        String file1 = "/Users/habibullahfedaee/Music/03_Dunya_Guzaran.mp3"; // change it to your file1 path
        String file2 = "/Users/habibullahfedaee/Music/05_Qataghani_(Rubab).mp3"; // change it to your file2 path
        // Create a Path object
        Path path1 = Paths.get(file1);
        Path path2 = Paths.get(file2);

        // Get the filename using getFileName()
        String file1name = path1.getFileName().toString(); // getFileName() returns a Path, so convert to String
        String file2name = path1.getFileName().toString(); // getFileName() returns a Path, so convert to String

        System.out.println("Starting playback of  "+file1name);
        myPlayer.play(file1);
        // simulate some user interface delay like 5sec
        Thread.sleep(10000); //play for 5 seconds

        // stopping play
        if(myPlayer.isPlaying){
            myPlayer.stop();
            System.out.println("Stopping current song!");
        }
        Thread.sleep(2000); //small delay

        System.out.println("Starting playback of  "+file2name);
        myPlayer.play(file2);

        // let file2 start playing for a while befor stopping
        Thread.sleep(10000);
        if (myPlayer.isPlaying()) {
            myPlayer.stop();
        }
        System.out.println("Application finished.");

    }
}
