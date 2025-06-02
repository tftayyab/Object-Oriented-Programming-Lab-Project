import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;

public class GUI extends JFrame implements ActionListener, ChangeListener {
    
    int index = 0;
    boolean shuffle = false;
    boolean paused = true;
    String MusicFolder = "E:/music";
    Audio song;
    Read read;
    String songAddress;
    File file;
    
    Timer sliderTimer;
    Timer waveTimer;
    
    JPanel mainPanel;
    JPanel controlPanel;
    JPanel titlePanel;
    WavePanel wavePanel;
    JLabel songTitleLabel;
    JLabel timeLabel;
    ImageIcon image;
    
    JButton stopStartButton;
    JButton nextButton;
    JButton previousButton;
    JButton shuffleButton;
    
    JSlider musicSlider;
    
    public GUI() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        read = new Read(MusicFolder);
        songAddress = read.getFileName(index);
        file = new File(songAddress);
        song = new Audio(file);
        
        setTitle("Music Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        
        // Load icon
        image = new ImageIcon("C:\\Users\\Muhammad Tayyab\\eclipse-workspace\\OPP Project\\src\\icon.jpg");
        setIconImage(image.getImage());
        
        // Create main panel with padding
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Song title at top
        titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        songTitleLabel = new JLabel(file.getName());
        songTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(songTitleLabel);
        
        // Wave visualization panel
        wavePanel = new WavePanel();
        wavePanel.setPreferredSize(new Dimension(460, 180));
        wavePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Slider panel with time display
        JPanel sliderPanel = new JPanel(new BorderLayout(0, 5));
        
        musicSlider = new JSlider(0, song.songLengthInMs(), 0);
        musicSlider.setPaintTicks(true);
        musicSlider.setPaintLabels(false);
        musicSlider.setMajorTickSpacing(song.songLengthInMs() / 5);
        
        timeLabel = new JLabel("0:00 / " + formatTime(song.songLengthInMs()));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        sliderPanel.add(musicSlider, BorderLayout.CENTER);
        sliderPanel.add(timeLabel, BorderLayout.SOUTH);
        
        // Control buttons with custom styling
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // Create main control panel for play, prev, next buttons
        JPanel mainControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        mainControlPanel.setPreferredSize(new Dimension(300, 50));
        
        // Create and style buttons
        previousButton = createStyledButton("Previous", "PREV", new Color(70, 130, 180));
        stopStartButton = createStyledButton("Play/Stop", "PLAY", new Color(46, 139, 87));
        nextButton = createStyledButton("Next", "NEXT", new Color(70, 130, 180));
        shuffleButton = createStyledButton("Shuffle", "SHFL", new Color(148, 0, 211));
        
        // Set sizes
        previousButton.setPreferredSize(new Dimension(80, 40));
        stopStartButton.setPreferredSize(new Dimension(80, 40));
        nextButton.setPreferredSize(new Dimension(80, 40));
        shuffleButton.setPreferredSize(new Dimension(45, 30));
        
        // Add main control buttons to centered panel
        mainControlPanel.add(previousButton);
        mainControlPanel.add(stopStartButton);
        mainControlPanel.add(nextButton);
        
        // Add both panels to control panel
        controlPanel.add(mainControlPanel);
        controlPanel.add(shuffleButton);
        
        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(wavePanel, BorderLayout.CENTER);
        mainPanel.add(sliderPanel, BorderLayout.SOUTH);
        
        // Add panels to frame
        add(mainPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        setupEventListeners();
        setupTimers();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void songEnds(String current, String total) {
    	if (current.equalsIgnoreCase(total)) {
    		System.out.println("Next song loop is working");
        	nextButton.doClick();
        }
		
	}

	private JButton createStyledButton(String name, String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setName(name);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setOpaque(true);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        
        button.addActionListener(this);
        return button;
    }
    
    private void setupEventListeners() {
        musicSlider.addChangeListener(this);
        
        musicSlider.addChangeListener(e -> {
            if (!musicSlider.getValueIsAdjusting()) {
                int valueInMs = musicSlider.getValue();
                if (Math.abs(song.songCurrentTimeInMs() - valueInMs) > 50) {
                    song.jumpToMs(valueInMs);
                    updateTimeLabel();
                }
            }
        });
    }
    
    private void setupTimers() {
        sliderTimer = new Timer(200, e -> {
            if (!musicSlider.getValueIsAdjusting()) {
                musicSlider.setValue(song.songCurrentTimeInMs());
                updateTimeLabel();
            }
        });
        sliderTimer.start();
        
        
        
        waveTimer = new Timer(50, e -> {
            if (paused) {
                wavePanel.setVolume(0);
            } else {
                float volume = song.getVolumeLevel();
                wavePanel.setVolume(volume);
            }
            wavePanel.repaint();
        });
        waveTimer.start();
    }
    
    private void updateTimeLabel() {
        int current = song.songCurrentTimeInMs();
        int total = song.songLengthInMs();
        timeLabel.setText(formatTime(current) + " / " + formatTime(total));
        songEnds(formatTime(current),formatTime(total));
    }
    
    private String formatTime(int timeInMs) {
        int seconds = timeInMs / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
    
    private void updateSongTitle() {
        songTitleLabel.setText(file.getName());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stopStartButton) {
            if (paused) {
                song.startSong();
                stopStartButton.setText("PAUSE");
                paused = false;
            } else {
                song.stopSong();
                stopStartButton.setText("PLAY");
                paused = true;
            }
        } else if (e.getSource() == nextButton) {
            try {
                index = song.nextSong(index, read.getNumberOfFiles(), shuffle);
                songAddress = read.getFileName(index);
                file = new File(songAddress);
                song = new Audio(file);
                musicSlider.setMaximum(song.songLengthInMs());
                musicSlider.setValue(0);
                updateSongTitle();
                song.startSong();
                stopStartButton.setText("PAUSE");
                paused = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == previousButton) {
            try {
                index = song.previousSong(index, read.getNumberOfFiles());
                songAddress = read.getFileName(index);
                file = new File(songAddress);
                song = new Audio(file);
                musicSlider.setMaximum(song.songLengthInMs());
                musicSlider.setValue(0);
                updateSongTitle();
                song.startSong();
                stopStartButton.setText("PAUSE");
                paused = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == shuffleButton) {
            shuffle = !shuffle;
            if (shuffle) {
                shuffleButton.setBackground(new Color(138, 43, 226));
            } else {
                shuffleButton.setBackground(new Color(148, 0, 211));
            }
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
    }
}