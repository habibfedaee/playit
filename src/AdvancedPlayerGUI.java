// imports
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File; // For JFileChooser

public class AdvancedPlayerGUI extends JFrame{

    // buttons (file chooser, player, stop)
    // labels ( statusLabell, fileNameLabel, ...)
    private JTextField filePathField;
    private JButton playButton;
    private JButton stopButton;
    private JButton browseButton;
    private JLabel statusLabel;

    private Player player;
    private Thread playerThread;
    private String currentFilePath;
    private boolean isPlaying;

    // constructor
    public AdvancedPlayerGUI(){
        super("Simple MP3 Player");
        isPlaying = false;
        setupUI();
        setupListeners();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // center the window
        setVisible(true);
    }

    // setup UI
    private void setupUI(){
        setLayout(new BorderLayout(10, 10)); // Add some padding

        // top panel for file path and browser button
        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        filePathField = new JTextField("path to your file"); // default file path
        browseButton = new JButton("Browse");
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);
        add(filePanel, BorderLayout.NORTH);

        // center panel for play controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        playButton = new JButton("Play");
        stopButton = new JButton(("Stop"));
        stopButton.setEnabled(false); // disable stop initially
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.CENTER);

        //Bottom Panel for status label
        statusLabel = new JLabel("Reader", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setupListeners(){
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("Select an MP3 file");
                // optional, set a default directory
                fileChooser.setCurrentDirectory(new File("/users/habibullahfedaee/music/"));

                int result = fileChooser.showOpenDialog(AdvancedPlayerGUI.this);
                if(result==JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                    if(selectedFile !=null && selectedFile.getName().toLowerCase().endsWith(".mp3")){
                        filePathField.setText(selectedFile.getAbsolutePath());
                        statusLabel.setText("File Selected: "+selectedFile.getName());
                    } else {
                        statusLabel.setText("Please select a valid MP3 file.");
                    }
                }
            }
        });
        // play action listener
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                if(filePath==null || filePath.trim().isEmpty() || !new File(filePath).exists()){
                    statusLabel.setText("Please select a valid MP3 file.");
                    return;
                }
                play(filePath);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
    }

    // --- MP3 Playback Logic (adapted from AdvancedMp3Player) ---
    public void play(String filePath) {
        if (isPlaying) {
            stop(); // Stop current song if playing
        }

        this.currentFilePath = filePath;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            player = new Player(fis); // Create a new player instance

            playerThread = new Thread(() -> {
                try {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Playing: " + new File(currentFilePath).getName());
                        playButton.setEnabled(false);
                        stopButton.setEnabled(true);
                        isPlaying = true;
                    });

                    player.play(); // This blocks the thread

                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Finished playing: " + new File(currentFilePath).getName());
                        playButton.setEnabled(true);
                        stopButton.setEnabled(false);
                        isPlaying = false;
                    });
                } catch (JavaLayerException ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error playing: " + ex.getMessage());
                        playButton.setEnabled(true);
                        stopButton.setEnabled(false);
                        isPlaying = false;
                    });
                    System.err.println("Error playing MP3:");
                    ex.printStackTrace();
                } finally {
                    // Ensure player is closed even if an error occurs
                    if (player != null) {
                        player.close();
                    }
                }
            });
            playerThread.start(); // Start the playback in a new thread

        } catch (FileNotFoundException e) {
            statusLabel.setText("Error: MP3 file not found!");
            System.err.println("Error: MP3 file not found at " + filePath);
            e.printStackTrace();
            isPlaying = false;
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (player != null) {
            player.close(); // Stops playback and closes the stream
            player = null; // Clear the player instance
            isPlaying = false;
            if (playerThread != null && playerThread.isAlive()) {
                playerThread.interrupt(); // Attempt to interrupt the thread
            }
            statusLabel.setText("Stopped.");
            playButton.setEnabled(true);
            stopButton.setEnabled(false);
            System.out.println("Stopped playing.");
        }
    }

    public static void main(String[] args){
        // ensure GUI updates are done on the event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdvancedPlayerGUI();
            }
        });
    }

}
