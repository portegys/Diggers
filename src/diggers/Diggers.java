/**
 * Diggers game.
 *
 * @author Tom Portegys
 */

package diggers;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.awt.TextRenderer;

public class Diggers extends JFrame
implements GLEventListener, KeyListener
{
   /** Serial version UID. */
   static final long serialVersionUID = 1L;

   /** Display dimensions. */
   static final int WIDTH  = 750;
   static final int HEIGHT = 750;

   /** Display canvas. */
   GLCanvas canvas;

   /** The frames per second setting. */
   static int FPS = 60;

   /** The OpenGL animator. */
   FPSAnimator animator;

   /** Block world dimension. */
   static final int BLOCK_WORLD_DIMENSION = 5;

   /** Block world. */
   Block[][][] blockWorld;

   /** Block textures. */
   Texture[] blockTextures;

   /** Digger player. */
   Digger digger;

   /** Camera. */
   Camera camera;

   // Light settings.
   float SHINE_ALL_DIRECTIONS = 1.0f;
   float[] lightPos           = { -30.0f, 0.0f, 0.0f, SHINE_ALL_DIRECTIONS };
   float[] lightColorAmbient  = { 0.2f, 0.2f, 0.2f, 1.0f };
   float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1.0f };

   /** Text drawing. */
   TextRenderer textRenderer;

   // Modes.
   enum Mode
   {
      RUN_MODE, HELP_MODE
   }
   Mode mode;

   /**
    * Constructor.
    */
   public Diggers()
   {
      setTitle("Diggers");
      setSize(WIDTH, HEIGHT);
      canvas = new GLCanvas(createGLCapabilities());
      canvas.addGLEventListener(this);
      getContentPane().add(canvas, BorderLayout.CENTER);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      canvas.requestFocus();
   }


   /**
    * Set GL capabilities.
    */
   static GLCapabilities createGLCapabilities()
   {
      GLProfile      profile      = GLProfile.get(GLProfile.GL2);
      GLCapabilities capabilities = new GLCapabilities(profile);

      capabilities.setRedBits(8);
      capabilities.setBlueBits(8);
      capabilities.setGreenBits(8);
      capabilities.setAlphaBits(8);
      return(capabilities);
   }


   /**
    * Initialize.
    */
   public void init(GLAutoDrawable drawable)
   {
      final GL2 gl = (GL2)drawable.getGL();

      // Enable z- (depth) buffer for hidden surface removal.
      gl.glEnable(GL2.GL_DEPTH_TEST);
      gl.glDepthFunc(GL2.GL_LEQUAL);

      // Enable smooth shading.
      gl.glShadeModel(GL2.GL_SMOOTH);

      // Define clear color.
      gl.glClearColor(0f, 0f, 0f, 0f);

      // Perspective hint.
      gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

      // Load block textures.
      blockTextures = new Texture[3];
      try {
         InputStream stream = getClass().getResourceAsStream("stone-25.jpg");
         if (stream == null)
         {
        	 stream = new FileInputStream("resource/stone-25.jpg");
         }
         GLProfile profile = GLProfile.getDefault();
         TextureData data   = TextureIO.newTextureData(profile, stream, false, "jpg");
         blockTextures[0] = TextureIO.newTexture(data);
         stream           = getClass().getResourceAsStream("stone-13.jpg");
         if (stream == null)
         {
        	 stream = new FileInputStream("resource/stone-13.jpg");
         }
         data             = TextureIO.newTextureData(profile, stream, false, "jpg");
         blockTextures[1] = TextureIO.newTexture(data);
         stream           = getClass().getResourceAsStream("stone-53.jpg");
         if (stream == null)
         {
        	 stream = new FileInputStream("resource/stone-53.jpg");
         }
         data             = TextureIO.newTextureData(profile, stream, false, "jpg");
         blockTextures[2] = TextureIO.newTexture(data);
      }
      catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      // Create block world.
      blockWorld = new Block[BLOCK_WORLD_DIMENSION][BLOCK_WORLD_DIMENSION][BLOCK_WORLD_DIMENSION];
      for (int x = 0; x < BLOCK_WORLD_DIMENSION; x++)
      {
         for (int y = 0; y < BLOCK_WORLD_DIMENSION; y++)
         {
            for (int z = 0; z < BLOCK_WORLD_DIMENSION; z++)
            {
               blockWorld[x][y][z] = new Block(x, y, z, blockTextures);
            }
         }
      }

      // Create digger on surface of block world.
      digger = new Digger();
      digger.setX(((float)BLOCK_WORLD_DIMENSION * Block.BLOCK_SIZE) * 0.5f);
      digger.setY(((float)BLOCK_WORLD_DIMENSION * Block.BLOCK_SIZE) * 0.5f);
      if ((BLOCK_WORLD_DIMENSION % 2) == 0)
      {
         digger.setX(digger.getX() + (Block.BLOCK_SIZE * 0.5f));
         digger.setY(digger.getY() + (Block.BLOCK_SIZE * 0.5f));
      }
      digger.setZ(((float)BLOCK_WORLD_DIMENSION * Block.BLOCK_SIZE) +
                  (Block.BLOCK_SIZE * 0.5f));
      digger.pitchDown();

      // Create camera.
      camera = new Camera(canvas);

      // Add key listener to canvas.
      canvas.addKeyListener(this);

      // Create text renderer.
      textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));

      // Initialize sound effects.
      SoundEffects.init();
      SoundEffects.volume = SoundEffects.Volume.LOW;

      // Run mode.
      mode = Mode.RUN_MODE;

      // Start animator.
      animator = new FPSAnimator(canvas, FPS);
      animator.start();
   }


   /**
    * Display.
    */
   public void display(GLAutoDrawable drawable)
   {
      if (!animator.isAnimating())
      {
         return;
      }
      final GL2 gl = (GL2)drawable.getGL();

      // Clear screen.
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

      // Update digger.
      digger.update();

      // Update camera.
      camera.synch(digger.getSpacial());
      camera.view(gl);

      // Set up lighting.
      gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
      gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
      gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);
      gl.glEnable(GL2.GL_LIGHT1);
      gl.glEnable(GL2.GL_LIGHTING);

      // Draw block world.
      for (int x = 0; x < BLOCK_WORLD_DIMENSION; x++)
      {
         for (int y = 0; y < BLOCK_WORLD_DIMENSION; y++)
         {
            for (int z = 0; z < BLOCK_WORLD_DIMENSION; z++)
            {
               if (blockWorld[x][y][z] != null)
               {
                  blockWorld[x][y][z].draw(gl);
               }
            }
         }
      }

      // Draw digger.
      digger.draw(gl);

      // Show help.
      help(drawable);
   }


   // Show help.
   void help(GLAutoDrawable drawable)
   {
      String      text;
      Rectangle2D bounds;
      int         offset;

      textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
      textRenderer.setColor(Color.white);
      if (mode == Mode.RUN_MODE)
      {
         text   = "h for help";
         bounds = textRenderer.getBounds(text);
         offset = (int)bounds.getHeight() + 1;
         textRenderer.draw(text, 1, drawable.getHeight() - offset);
      }
      else
      {
         text   = "Space : move forward";
         bounds = textRenderer.getBounds(text);
         offset = (int)bounds.getHeight() + 1;
         textRenderer.draw(text, 1, drawable.getHeight() - offset);
         text    = "Enter : strike block";
         bounds  = textRenderer.getBounds(text);
         offset += (int)bounds.getHeight() + 1;
         textRenderer.draw(text, 1, drawable.getHeight() - offset);
         text    = "Arrow up/down : pitch up/down";
         bounds  = textRenderer.getBounds(text);
         offset += (int)bounds.getHeight() + 1;
         textRenderer.draw(text, 1, drawable.getHeight() - offset);
         text    = "Arrow left/right : yaw left/right";
         bounds  = textRenderer.getBounds(text);
         offset += (int)bounds.getHeight() + 1;
         textRenderer.draw(text, 1, drawable.getHeight() - offset);
         text    = "Escape : quit";
         bounds  = textRenderer.getBounds(text);
         offset += (int)bounds.getHeight() + 1;
         textRenderer.draw(text, 1, drawable.getHeight() - offset);
      }
      textRenderer.endRendering();
   }


   /**
    * Resize the screen.
    */
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
   {
      final GL2 gl = (GL2)drawable.getGL();

      gl.glViewport(0, 0, width, height);
   }


   /**
    * Changing devices is not supported.
    */
   public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
   {
      throw new UnsupportedOperationException("Changing display is not supported.");
   }


   /**
    * Key pressed event.
    * @param e KeyEvent.
    */
   public void keyPressed(KeyEvent e)
   {
      int key = e.getKeyCode();

      // Check for help event.
      if ((key == KeyEvent.VK_H) && (mode == Mode.RUN_MODE))
      {
         mode = Mode.HELP_MODE;
         return;
      }
      if (mode == Mode.HELP_MODE)
      {
         mode = Mode.RUN_MODE;
         return;
      }

      switch (key)
      {
      case KeyEvent.VK_SPACE:
         // Block world configuration determines move action.
         digger.move(blockWorld);
         break;

      case KeyEvent.VK_ENTER:
         // Block world configuration determines strike action.
         digger.strike(blockWorld);
         break;

      case KeyEvent.VK_UP:
         digger.pitchUp();
         break;

      case KeyEvent.VK_DOWN:
         digger.pitchDown();
         break;

      case KeyEvent.VK_RIGHT:
         digger.yawRight();
         break;

      case KeyEvent.VK_LEFT:
         digger.yawLeft();
         break;

      case KeyEvent.VK_ESCAPE:
         System.exit(0);
      }
   }


   /**
    * Key released event.
    * @param e KeyEvent.
    */
   public void keyReleased(KeyEvent e) {}


   /**
    * Key typed event.
    * @param e KeyEvent.
    */
   public void keyTyped(KeyEvent e) {}

   /**
    * Dispose.
    */
   public void dispose(GLAutoDrawable drawable) {}

   /**
    * Main.
    */
   public final static void main(String[] args)
   {
      Diggers diggers = new Diggers();
   }
}
