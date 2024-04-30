import java.io.File; 
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.Clip; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.LineListener; 
// possible exceptions 
import java.io.IOException; 
import javax.sound.sampled.UnsupportedAudioFileException; 
import javax.sound.sampled.LineUnavailableException;

public class Sound{ 
    Clip sound; 
//------------------------------------------------------------------------------          
    Sound(String soundName){ 
        try { 
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(soundName)); 
            this.sound = AudioSystem.getClip(); 
            this.sound.open(audioStream); 
        }  
        catch (IOException ex){System.out.println("File not found!");} 
        catch (UnsupportedAudioFileException ex){System.out.println("Unsupported file!");}    
        catch (LineUnavailableException ex){System.out.println("Audio feed already in use!");} 
    } 
//------------------------------------------------------------------------------          
    public void start(){ 
        this.sound.start(); 
    } 
    public void stop(){ 
        this.sound.stop(); 
    } 
    public void flush(){ 
        this.sound.flush(); 
    } 
    public void setFramePosition(int frames){ 
        this.sound.setFramePosition(frames); 
    }     
    public void addLineListener(LineListener listener){ 
        this.sound.addLineListener(listener); 
    }
    public boolean isRunning(){ 
        return this.sound.isRunning(); 
    }
    public void loop(){
        this.sound.loop(Clip.LOOP_CONTINUOUSLY);
    }
}