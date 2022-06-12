// Block.

package diggers;

import javax.media.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class Block
{
   // Block size.
   static final float BLOCK_SIZE = 1.0f;

   // Block position in block units.
   int x, y, z;

   // Block vertex.
   class Vertex
   {
      float x, y, z;
   };

   // Block vertices.
   Vertex[] vertices;

   // Textures reflecting digger strikes.
   Texture[] strikeTextures;

   // Digger strike count.
   int strikeCount;

   // Display lists.
   int faceDisplay;
   int edgeDisplay;

   // Constructor.
   public Block(int x, int y, int z, Texture[] strikeTextures)
   {
      this.x   = x;
      this.y   = y;
      this.z   = z;
      vertices = new Vertex[8];
      for (int i = 0; i < 8; i++)
      {
         vertices[i] = new Vertex();
      }
      setBlockVertices(0.0f, (float)BLOCK_SIZE, 0.0f,
                       (float)BLOCK_SIZE, 0.0f, (float)BLOCK_SIZE);
      this.strikeTextures = strikeTextures;
      strikeCount         = 0;
      faceDisplay         = -1;
      edgeDisplay         = -1;
   }


   // Set strike textures.
   void setStrikeTextures(Texture[] strikeTextures)
   {
      this.strikeTextures = strikeTextures;
   }


   // Strike block.
   // Return true for maximum strikes.
   boolean strike()
   {
      if (strikeCount < (strikeTextures.length - 1))
      {
         strikeCount++;
         return(false);
      }
      else
      {
         return(true);
      }
   }


   // Set block vertices.
   void setBlockVertices(float xmin, float xmax,
                         float ymin, float ymax, float zmin, float zmax)
   {
      vertices[0].x = xmax;
      vertices[0].y = ymax;
      vertices[0].z = zmin;

      vertices[1].x = xmax;
      vertices[1].y = ymax;
      vertices[1].z = zmax;

      vertices[2].x = xmax;
      vertices[2].y = ymin;
      vertices[2].z = zmax;

      vertices[3].x = xmax;
      vertices[3].y = ymin;
      vertices[3].z = zmin;

      vertices[4].x = xmin;
      vertices[4].y = ymax;
      vertices[4].z = zmin;

      vertices[5].x = xmin;
      vertices[5].y = ymax;
      vertices[5].z = zmax;

      vertices[6].x = xmin;
      vertices[6].y = ymin;
      vertices[6].z = zmax;

      vertices[7].x = xmin;
      vertices[7].y = ymin;
      vertices[7].z = zmin;
   }


   // Draw block.
   void draw(GL2 gl)
   {
      // Create displays?
      if (faceDisplay == -1)
      {
         createFaceDisplay(gl);
      }
      if (edgeDisplay == -1)
      {
         createEdgeDisplay(gl);
      }

      // Set material properties.
      float[] rgba = { 1.0f, 1.0f, 1.0f };
      gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
      gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
      gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

      // Draw block.
      if (strikeTextures != null)
      {
         strikeTextures[strikeCount].enable(gl);
         strikeTextures[strikeCount].bind(gl);
      }
      gl.glPushMatrix();
      gl.glTranslatef((float)x * BLOCK_SIZE, (float)y * BLOCK_SIZE, (float)z * BLOCK_SIZE);
      gl.glCallList(faceDisplay);
      if (strikeTextures != null)
      {
         strikeTextures[strikeCount].disable(gl);
      }
      gl.glCallList(edgeDisplay);
      gl.glPopMatrix();
   }


   // Create face display.
   void createFaceDisplay(GL2 gl)
   {
      // Get a display list.
      faceDisplay = gl.glGenLists(1);
      gl.glNewList(faceDisplay, GL2.GL_COMPILE);

      // Set the quads, normals, and texture coordinates.
      float x, y, z;
      gl.glBegin(GL2.GL_QUADS);
      x = vertices[0].x;
      y = vertices[0].y;
      z = vertices[0].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[1].x;
      y = vertices[1].y;
      z = vertices[1].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[2].x;
      y = vertices[2].y;
      z = vertices[2].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[3].x;
      y = vertices[3].y;
      z = vertices[3].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_QUADS);
      x = vertices[3].x;
      y = vertices[3].y;
      z = vertices[3].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[2].x;
      y = vertices[2].y;
      z = vertices[2].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[6].x;
      y = vertices[6].y;
      z = vertices[6].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[7].x;
      y = vertices[7].y;
      z = vertices[7].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_QUADS);
      x = vertices[7].x;
      y = vertices[7].y;
      z = vertices[7].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[6].x;
      y = vertices[6].y;
      z = vertices[6].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[5].x;
      y = vertices[5].y;
      z = vertices[5].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[4].x;
      y = vertices[4].y;
      z = vertices[4].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_QUADS);
      x = vertices[0].x;
      y = vertices[0].y;
      z = vertices[0].z;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[4].x;
      y = vertices[4].y;
      z = vertices[4].z;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[5].x;
      z = vertices[5].z;
      y = vertices[5].y;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[1].x;
      y = vertices[1].y;
      z = vertices[1].z;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_QUADS);
      x = vertices[2].x;
      y = vertices[2].y;
      z = vertices[2].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[1].x;
      y = vertices[1].y;
      z = vertices[1].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[5].x;
      y = vertices[5].y;
      z = vertices[5].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[6].x;
      y = vertices[6].y;
      z = vertices[6].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_QUADS);
      x = vertices[7].x;
      y = vertices[7].y;
      z = vertices[7].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[4].x;
      y = vertices[4].y;
      z = vertices[4].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[0].x;
      y = vertices[0].y;
      z = vertices[0].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[3].x;
      y = vertices[3].y;
      z = vertices[3].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();
      gl.glEndList();
   }


   // Create edge display.
   void createEdgeDisplay(GL2 gl)
   {
      // Get a display list.
      edgeDisplay = gl.glGenLists(1);
      gl.glNewList(edgeDisplay, GL2.GL_COMPILE);

      // Set the lines, normals, and texture coordinates.
      float x, y, z;
      gl.glBegin(GL2.GL_LINE_LOOP);
      x = vertices[0].x;
      y = vertices[0].y;
      z = vertices[0].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[1].x;
      y = vertices[1].y;
      z = vertices[1].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[2].x;
      y = vertices[2].y;
      z = vertices[2].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[3].x;
      y = vertices[3].y;
      z = vertices[3].z;
      gl.glNormal3f(1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_LINE_LOOP);
      x = vertices[3].x;
      y = vertices[3].y;
      z = vertices[3].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[2].x;
      y = vertices[2].y;
      z = vertices[2].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[6].x;
      y = vertices[6].y;
      z = vertices[6].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[7].x;
      y = vertices[7].y;
      z = vertices[7].z;
      gl.glNormal3f(0.0f, -1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_LINE_LOOP);
      x = vertices[7].x;
      y = vertices[7].y;
      z = vertices[7].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[6].x;
      y = vertices[6].y;
      z = vertices[6].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[5].x;
      y = vertices[5].y;
      z = vertices[5].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[4].x;
      y = vertices[4].y;
      z = vertices[4].z;
      gl.glNormal3f(-1.0f, 0.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_LINE_LOOP);
      x = vertices[0].x;
      y = vertices[0].y;
      z = vertices[0].z;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[4].x;
      y = vertices[4].y;
      z = vertices[4].z;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[5].x;
      z = vertices[5].z;
      y = vertices[5].y;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[1].x;
      y = vertices[1].y;
      z = vertices[1].z;
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_LINE_LOOP);
      x = vertices[2].x;
      y = vertices[2].y;
      z = vertices[2].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[1].x;
      y = vertices[1].y;
      z = vertices[1].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[5].x;
      y = vertices[5].y;
      z = vertices[5].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[6].x;
      y = vertices[6].y;
      z = vertices[6].z;
      gl.glNormal3f(0.0f, 0.0f, 1.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();

      gl.glBegin(GL2.GL_LINE_LOOP);
      x = vertices[7].x;
      y = vertices[7].y;
      z = vertices[7].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(1.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[4].x;
      y = vertices[4].y;
      z = vertices[4].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[0].x;
      y = vertices[0].y;
      z = vertices[0].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(0.0f, 1.0f);
      gl.glVertex3f(x, y, z);
      x = vertices[3].x;
      y = vertices[3].y;
      z = vertices[3].z;
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(0.0f, 0.0f);
      gl.glVertex3f(x, y, z);
      gl.glEnd();
      gl.glEndList();
   }
}
