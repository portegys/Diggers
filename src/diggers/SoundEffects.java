// See: http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html

package diggers;

import java.io.*;

import javax.sound.sampled.*;

/**
 * This enum encapsulates all the sound effects of a game, so as to separate the sound playing
 * codes from the game codes.
 * 1. Define all your sound effect names and the associated wave file.
 * 2. To play a specific sound, simply invoke SoundEffects.SOUND_NAME.play().
 * 3. You might optionally invoke the static method SoundEffects.init() to pre-load all the
 *    sound files, so that the play is not paused while loading the file for the first time.
 * 4. You can use the static variable SoundEffects.volume to mute the sound.
 */
public enum SoundEffects
{
   STRIKE("bhit2.wav"),      // strike block
   BREAK("boom.wav");       // break block

   // Nested class for specifying volume
   public static enum Volume
   {
      MUTE, LOW, MEDIUM, HIGH
   }

   public static Volume volume = Volume.LOW;

   // Each sound effect has its own clip, loaded with its own sound file.
   private Clip clip;

   // Constructor to construct each element of the enum with its own sound file.
   SoundEffects(String soundFileName)
   {
      try {
         // Set up an audio input stream piped from the sound file.
         InputStream      stream           = getClass().getResourceAsStream(soundFileName);
         if (stream == null)
         {
        	 stream = new FileInputStream("resource/" + soundFileName);
         }         
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);

         // Get a clip resource.
         clip = AudioSystem.getClip();

         // Open audio clip and load samples from the audio input stream.
         clip.open(audioInputStream);
      }
      catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      catch (LineUnavailableException e) {
         e.printStackTrace();
      }
   }

   // Play or Re-play the sound effect from the beginning, by rewinding.
   public void play()
   {
      if (volume != Volume.MUTE)
      {
         if (clip.isRunning())
         {
            clip.stop();           // Stop the player if it is still running
         }
         clip.setFramePosition(0); // rewind to the beginning
         clip.start();             // Start playing
      }
   }


   // Optional static method to pre-load all the sound files.
   static void init()
   {
      values(); // calls the constructor for all the elements
   }
}
