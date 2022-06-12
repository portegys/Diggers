// Camera.

package diggers;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.awt.GLCanvas;

public class Camera
{
   // Game canvas.
   GLCanvas canvas;

   // GL unit.
   GLU glu;

   // Position and direction.
   Spacial spacial;

   // Constructor.
   public Camera(GLCanvas canvas)
   {
      this.canvas = canvas;
      glu         = new GLU();
      spacial     = new Spacial();
   }


   // Synch to given spacial.
   void synch(Spacial spacial)
   {
      this.spacial.copy(spacial);
   }


   // Set view.
   void view(GL2 gl)
   {
      float[] eye = spacial.getPosition();
      float[] at  = Spacial.vadd(eye, spacial.getForward());
      float[] up  = spacial.getUp();

      // Change to projection matrix.
      gl.glMatrixMode(GL2.GL_PROJECTION);
      gl.glLoadIdentity();

      // Set perspective.
      float widthHeightRatio = (float)canvas.getWidth() / (float)canvas.getHeight();
      glu.gluPerspective(45.0f, widthHeightRatio,
                         Block.BLOCK_SIZE * 0.1f, (float)Diggers.BLOCK_WORLD_DIMENSION * Block.BLOCK_SIZE * 2.0f);

      // Set view.
      glu.gluLookAt(eye[0], eye[1], eye[2], at[0], at[1], at[2], up[0], up[1], up[2]);

      // Change back to model view matrix.
      gl.glMatrixMode(GL2.GL_MODELVIEW);
      gl.glLoadIdentity();
   }
}
