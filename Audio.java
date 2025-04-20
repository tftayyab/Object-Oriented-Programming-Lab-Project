import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.*;

public class Audio {

    File file;

    AudioInputStream audioStream;
    Clip clip;

    public Audio(File song) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.file = song;
        audioStream = AudioSystem.getAudioInputStream(song);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
    }

    protected void startSong() {
        clip.start();
    }

    protected void stopSong() {
        clip.stop();
    }

    protected int nextSong(int index, int numberOfFiles, boolean shuffle) {
        clip.stop();

        if (shuffle) {
            index = this.shuffle();
        } else {
            index++; // move to next song
            if (index >= numberOfFiles) {
                index = 0; // loop to first song
            }
        }

        System.out.println("New index: " + index);
        return index;
    }

    protected int previousSong(int index, int numberOfFiles) {
        clip.stop();

        index--; // move to previous song
        if (index < 0) {
            index = numberOfFiles - 1; // loop to last song
        }

        System.out.println("New index: " + index);
        return index;
    }

    protected void jumpToMs(int ms) {
        clip.setMicrosecondPosition((long) ms * 1000);
    }

    protected int shuffle() {
        Random random = new Random();
        return random.nextInt(5);
    }

    protected int songLengthInMs() {
        return (int) (clip.getMicrosecondLength() / 1000);
    }

    protected int songCurrentTimeInMs() {
        return (int) (clip.getMicrosecondPosition() / 1000);
    }

    protected float getVolumeLevel() {
        Random random = new Random();
        if (clip != null && clip.isOpen()) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = gainControl.getValue(); // decibels
            // Convert dB to a 0-1 range (roughly)
            return random.nextFloat((dB + 80) / 80); // assuming min is -80 dB
        }
        return 0;
    }
}
