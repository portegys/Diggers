// Spacial properties.

package diggers;

public class Spacial
{
   // Position.
   float x, y, z;

   // Rotation.
   float[] quaternion;
   float[][]     rotationMatrix;

   // Constructor.
   public Spacial()
   {
      x              = y = z = 0.0f;
      quaternion     = newQuaternion();
      rotationMatrix = new float[4][4];
      buildRotationMatrix();
   }


   // Get and set position.
   float getX() { return(x); }
   float getY() { return(y); }
   float getZ() { return(z); }
   float[] getPosition()
   {
      float[] position = new float[3];
      position[0]      = x;
      position[1]      = y;
      position[2]      = z;
      return(position);
   }


   void setX(float x) { this.x = x; }
   void setY(float y) { this.y = y; }
   void setZ(float z) { this.z = z; }
   void setPosition(float[] position)
   {
      x = position[0];
      y = position[1];
      z = position[2];
   }


   // Get direction vectors.
   float[] getForward()
   {
      float[] forward = new float[3];
      forward[0]      = rotationMatrix[2][0];
      forward[1]      = rotationMatrix[2][1];
      forward[2]      = rotationMatrix[2][2];
      return(forward);
   }


   float[] getRight()
   {
      float[] right = new float[3];
      right[0]      = rotationMatrix[0][0];
      right[1]      = rotationMatrix[0][1];
      right[2]      = rotationMatrix[0][2];
      return(right);
   }


   float[] getUp()
   {
      float[] up = new float[3];
      up[0]      = rotationMatrix[1][0];
      up[1]      = rotationMatrix[1][1];
      up[2]      = rotationMatrix[1][2];
      return(up);
   }


   // Change direction.
   void pitch(float angle)
   {
      float[] axis = new float[3];
      axis[0]      = 1.0f;
      axis[1]      = 0.0f;
      axis[2]      = 0.0f;
      rotate(angle, axis);
   }


   void yaw(float angle)
   {
      float[] axis = new float[3];
      axis[0]      = 0.0f;
      axis[1]      = 1.0f;
      axis[2]      = 0.0f;
      rotate(angle, axis);
   }


   void roll(float angle)
   {
      float[] axis = new float[3];
      axis[0]      = 0.0f;
      axis[1]      = 0.0f;
      axis[2]      = 1.0f;
      rotate(angle, axis);
   }


   // Rotate.
   // Angle is in degrees.
   void rotate(float angle, float[] axis)
   {
      float[] q1, q2;

      q1         = createQuaternion(DegreesToRadians(angle), axis);
      q2         = quaternion;
      quaternion = multiplyQuaternions(q2, q1);
      buildRotationMatrix();
   }


   // Create quaternion.
   static float[] newQuaternion()
   {
      float[] q = new float[4];
      q[0]      = 0.0f;
      q[1]      = 0.0f;
      q[2]      = 0.0f;
      q[3]      = 1.0f;
      return(q);
   }


   // Clear quaternion.
   static void clearQuaternion(float[] q)
   {
      q[0] = 0.0f;
      q[1] = 0.0f;
      q[2] = 0.0f;
      q[3] = 1.0f;
   }


   // Vector methods.
   static void vzero(float[] v)
   {
      v[0] = 0.0f;
      v[1] = 0.0f;
      v[2] = 0.0f;
   }


   static void vset(float[] v, float x, float y, float z)
   {
      v[0] = x;
      v[1] = y;
      v[2] = z;
   }


   static void vcopy(float[] src, float[] dest)
   {
      for (int i = 0; i < 3; i++)
      {
         dest[i] = src[i];
      }
   }


   static float[] vclone(float[] src)
   {
      float[] dest = new float[3];
      for (int i = 0; i < 3; i++)
      {
         dest[i] = src[i];
      }
      return(dest);
   }


   static float vlength(float[] v)
   {
      return((float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
   }


   static void vscale(float[] v, float div)
   {
      v[0] *= div;
      v[1] *= div;
      v[2] *= div;
   }


   static void vnormal(float[] v)
   {
      vscale(v, 1.0f / vlength(v));
   }


   static float[] vadd(float[] src1, float[] src2)
   {
      float[] dest = new float[3];
      dest[0]      = src1[0] + src2[0];
      dest[1]      = src1[1] + src2[1];
      dest[2]      = src1[2] + src2[2];
      return(dest);
   }


   static float[] vsub(float[] src1, float[] src2)
   {
      float[] dest = new float[3];
      dest[0]      = src1[0] - src2[0];
      dest[1]      = src1[1] - src2[1];
      dest[2]      = src1[2] - src2[2];
      return(dest);
   }


   static float[] vcross(float[] v1, float[] v2)
   {
      float[] cross = new float[3];
      cross[0]      = (v1[1] * v2[2]) - (v1[2] * v2[1]);
      cross[1]      = (v1[2] * v2[0]) - (v1[0] * v2[2]);
      cross[2]      = (v1[0] * v2[1]) - (v1[1] * v2[0]);
      return(cross);
   }


   static float vdot(float[] v1, float[] v2)
   {
      return(v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2]);
   }


   static float DegreesToRadians(float degress)
   {
      return(degress * (float)Math.PI / 180.0f);
   }


   static float RadiansToDegrees(float radians)
   {
      return(radians * 180.0f / (float)Math.PI);
   }


   // Add quaternions.
   static float[] addQuaternions(float[] q1, float[] q2)
   {
      float[] t1  = new float[3];
      float[] t2  = new float[3];
      float[] t3  = new float[3];
      float[] sum = newQuaternion();

      vcopy(q1, t1);
      vscale(t1, q2[3]);
      vcopy(q2, t2);
      vscale(t2, q1[3]);
      t3     = vcross(q2, q1);
      sum    = vadd(t1, t2);
      sum    = vadd(t3, sum);
      sum[3] = q1[3] * q2[3] - vdot(q1, q2);
      normalizeQuaternion(sum);
      return(sum);
   }


   // Multiply quaternions.
   static float[] multiplyQuaternions(float[] q1, float[] q2)
   {
      float[] product = newQuaternion();
      product[3]      = q2[3] * q1[3]
                        - q2[0] * q1[0]
                        - q2[1] * q1[1]
                        - q2[2] * q1[2];

      product[0] = q2[3] * q1[0]
                   + q2[0] * q1[3]
                   + q2[1] * q1[2]
                   - q2[2] * q1[1];

      product[1] = q2[3] * q1[1]
                   - q2[0] * q1[2]
                   + q2[1] * q1[3]
                   + q2[2] * q1[0];

      product[2] = q2[3] * q1[2]
                   + q2[0] * q1[1]
                   - q2[1] * q1[0]
                   + q2[2] * q1[3];
      normalizeQuaternion(product);
      return(product);
   }


   /*
    * Quaternions always obey:  a^2 + b^2 + c^2 + d^2 = 1.0
    * If they don't add up to 1.0, dividing by their magnitude will
    * renormalize them.
    *
    * Note: See the following for more information on quaternions:
    *
    * - Shoemake, K., Animating rotation with quaternion curves, Computer
    *   Graphics 19, No 3 (Proc. SIGGRAPH'85), 245-254, 1985.
    * - Pletinckx, D., Quaternion calculus as a basic tool in computer
    *   graphics, The Visual Computer 5, 2-13, 1989.
    */
   static void normalizeQuaternion(float[] q)
   {
      int   i;
      float mag;

      mag = (q[0] * q[0] + q[1] * q[1] +
             q[2] * q[2] + q[3] * q[3]);
      for (i = 0; i < 4; i++)
      {
         q[i] /= mag;
      }
   }


   // Build the rotation matrix from the quaternion.
   void buildRotationMatrix()
   {
      rotationMatrix[0][0] = 1.0f - 2.0f * (quaternion[1] * quaternion[1] + quaternion[2] * quaternion[2]);
      rotationMatrix[0][1] = 2.0f * (quaternion[0] * quaternion[1] - quaternion[2] * quaternion[3]);
      rotationMatrix[0][2] = 2.0f * (quaternion[2] * quaternion[0] + quaternion[1] * quaternion[3]);
      rotationMatrix[0][3] = 0.0f;

      rotationMatrix[1][0] = 2.0f * (quaternion[0] * quaternion[1] + quaternion[2] * quaternion[3]);
      rotationMatrix[1][1] = 1.0f - 2.0f * (quaternion[2] * quaternion[2] + quaternion[0] * quaternion[0]);
      rotationMatrix[1][2] = 2.0f * (quaternion[1] * quaternion[2] - quaternion[0] * quaternion[3]);
      rotationMatrix[1][3] = 0.0f;

      rotationMatrix[2][0] = 2.0f * (quaternion[2] * quaternion[0] - quaternion[1] * quaternion[3]);
      rotationMatrix[2][1] = 2.0f * (quaternion[1] * quaternion[2] + quaternion[0] * quaternion[3]);
      rotationMatrix[2][2] = 1.0f - 2.0f * (quaternion[1] * quaternion[1] + quaternion[0] * quaternion[0]);
      rotationMatrix[2][3] = 0.0f;

      rotationMatrix[3][0] = 0.0f;
      rotationMatrix[3][1] = 0.0f;
      rotationMatrix[3][2] = 0.0f;
      rotationMatrix[3][3] = 1.0f;
   }


   // Create quaternion from axis-angle rotation.
   static float[] createQuaternion(float angle, float[] axis)
   {
      float[] q = newQuaternion();
      vnormal(axis);
      vcopy(axis, q);
      vscale(q, (float)Math.sin(angle / 2.0f));
      q[3] = (float)Math.cos(angle / 2.0f);
      normalizeQuaternion(q);
      return(q);
   }


   // Merge an axis-angle rotation into quaternion.
   static float[] mergeRotation(float angle, float[] axis, float[] q)
   {
      float[] q2 = newQuaternion();

      q2[0] = axis[0] * (float)Math.sin(angle / 2.0f);
      q2[1] = axis[1] * (float)Math.sin(angle / 2.0f);
      q2[2] = axis[2] * (float)Math.sin(angle / 2.0f);
      q2[3] = (float)Math.cos(angle / 2.0f);
      normalizeQuaternion(q2);
      return(multiplyQuaternions(q2, q));
   }


   // Make quaternion from Euler angles.
   static float[] makeQuaternionFromEulerAngles(float pitch, float yaw, float roll)
   {
      // Re-orient internally.
      float iroll  = DegreesToRadians(pitch);
      float ipitch = DegreesToRadians(yaw);
      float iyaw   = DegreesToRadians(roll);

      float cyaw, cpitch, croll, syaw, spitch, sroll;
      float cyawcpitch, syawspitch, cyawspitch, syawcpitch;

      cyaw   = (float)Math.cos(0.5f * iyaw);
      cpitch = (float)Math.cos(0.5f * ipitch);
      croll  = (float)Math.cos(0.5f * iroll);
      syaw   = (float)Math.sin(0.5f * iyaw);
      spitch = (float)Math.sin(0.5f * ipitch);
      sroll  = (float)Math.sin(0.5f * iroll);

      cyawcpitch = cyaw * cpitch;
      syawspitch = syaw * spitch;
      cyawspitch = cyaw * spitch;
      syawcpitch = syaw * cpitch;

      float[] q = newQuaternion();
      q[3]      = (float)(cyawcpitch * croll + syawspitch * sroll);
      q[0]      = (float)(cyawcpitch * sroll - syawspitch * croll);
      q[1]      = (float)(cyawspitch * croll + syawcpitch * sroll);
      q[2]      = (float)(syawcpitch * croll - cyawspitch * sroll);
      normalizeQuaternion(q);
      return(q);
   }


   // Get Euler angles from quaternion.
   static float[] getEulerAngles(float[] q)
   {
      float pitch, yaw, roll;
      float r11, r21, r31, r32, r33, r12, r13;
      float q00, q11, q22, q33;
      float tmp;

      q00 = q[3] * q[3];
      q11 = q[0] * q[0];
      q22 = q[1] * q[1];
      q33 = q[2] * q[2];

      r11 = q00 + q11 - q22 - q33;
      r21 = 2 * (q[0] * q[1] + q[3] * q[2]);
      r31 = 2 * (q[0] * q[2] - q[3] * q[1]);
      r32 = 2 * (q[1] * q[2] + q[3] * q[0]);
      r33 = q00 - q11 - q22 + q33;

      tmp = Math.abs(r31);
      if (tmp > 0.999999f)
      {
         r12 = 2 * (q[0] * q[1] - q[3] * q[2]);
         r13 = 2 * (q[0] * q[2] + q[3] * q[1]);

         pitch = RadiansToDegrees(0.0f);
         yaw   = RadiansToDegrees((float)(-(Math.PI / 2.0f) * r31 / tmp));
         roll  = RadiansToDegrees((float)Math.atan2(-r12, -r31 * r13));
      }
      else
      {
         pitch = RadiansToDegrees((float)Math.atan2(r32, r33));
         yaw   = RadiansToDegrees((float)Math.asin(-r31));
         roll  = RadiansToDegrees((float)Math.atan2(r21, r11));
      }

      float[] angles = new float[3];
      angles[0]      = pitch;
      angles[1]      = yaw;
      angles[2]      = roll;
      return(angles);
   }


   // Import given spacial.
   void copy(Spacial s)
   {
      int i, j;

      x = s.x;
      y = s.y;
      z = s.z;

      for (i = 0; i < 4; i++)
      {
         quaternion[i] = s.quaternion[i];
      }

      for (i = 0; i < 4; i++)
      {
         for (j = 0; j < 4; j++)
         {
            rotationMatrix[i][j] = s.rotationMatrix[i][j];
         }
      }
   }
}
