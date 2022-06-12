// Digger player.

package diggers;

import javax.media.opengl.GL2;

public class Digger
{
   // Position and direction.
   Spacial spacial;

   // Motion types.
   enum MOTION_TYPE
   {
      NONE,
      MOVE_FORWARD,
      SWIVEL_FORWARD,
      STRIKE_BLOCK,
      PITCH_UP,
      PITCH_DOWN,
      YAW_RIGHT,
      YAW_LEFT,
      ROLL_RIGHT,
      ROLL_LEFT
   };

   MOTION_TYPE motion;

   // Movement amounts.
   float distance, distance2;
   float angle;

   // Distance movement delta.
   static final float DISTANCE_DELTA = Block.BLOCK_SIZE * 0.25f;

   // Rotation delta degrees.
   static final float ANGLE_DELTA = 5.0f;

   // Constructor.
   public Digger()
   {
      spacial  = new Spacial();
      motion   = MOTION_TYPE.NONE;
      distance = distance2 = 0.0f;
      angle    = 0.0f;
   }


   // Get spacial.
   Spacial getSpacial() { return(spacial); }

   // Get and set position.
   float getX() { return(spacial.getX()); }
   float getY() { return(spacial.getY()); }
   float getZ() { return(spacial.getZ()); }
   float[] getPosition() { return(spacial.getPosition()); }
   void setX(float x) { spacial.setX(x); }
   void setY(float y) { spacial.setY(y); }
   void setZ(float z) { spacial.setZ(z); }
   void setPosition(float[] position) { spacial.setPosition(position); }

   // Move forward.
   void moveForward(float distance)
   {
      float[] forward  = spacial.getForward();
      float[] position = spacial.getPosition();
      Spacial.vscale(forward, distance);
      position = Spacial.vadd(position, forward);
      spacial.setPosition(position);
   }


   // Change direction.
   void pitchUp(float angle) { spacial.pitch(angle); }
   void pitchDown(float angle) { spacial.pitch(-angle); }
   void yawRight(float angle) { spacial.yaw(angle); }
   void yawLeft(float angle) { spacial.yaw(-angle); }
   void rollRight(float angle) { spacial.roll(angle); }
   void rollLeft(float angle) { spacial.roll(-angle); }

   // Get direction vectors.
   float[] getForward() { return(spacial.getForward()); }
   float[] getRight() { return(spacial.getRight()); }
   float[] getUp() { return(spacial.getUp()); }

   // Move depends on block world configuration.
   void move(Block[][][] blockWorld)
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      // Check for block ahead.
      float[] forward = spacial.getForward();
      Spacial.vscale(forward, Block.BLOCK_SIZE);
      float[] position = spacial.getPosition();
      float[] ahead    = Spacial.vadd(position, forward);
      if (findBlock(ahead, blockWorld) != null) { return; }

      // Move if have footing on block.
      float[] down = spacial.getUp();
      Spacial.vscale(down, -Block.BLOCK_SIZE);
      float[] footing    = Spacial.vadd(position, down);
      int[] footingBlock = findBlock(footing, blockWorld);
      if (footingBlock != null)
      {
         // Check for landing block.
         float[] landing    = Spacial.vadd(ahead, down);
         int[] landingBlock = findBlock(landing, blockWorld);
         if (landingBlock == null)
         {
            // Swivel around corner.
            motion   = MOTION_TYPE.SWIVEL_FORWARD;
            distance = distance2 = Block.BLOCK_SIZE;
            angle    = 90.0f;
         }
         else
         {
            motion   = MOTION_TYPE.MOVE_FORWARD;
            distance = Block.BLOCK_SIZE;
         }
      }
   }


   // Strike block depends on block world configuration.
   void strike(Block[][][] blockWorld)
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      // Check for block ahead to strike.
      float[] forward = spacial.getForward();
      Spacial.vscale(forward, Block.BLOCK_SIZE);
      float[] position  = spacial.getPosition();
      float[] ahead     = Spacial.vadd(position, forward);
      int[] strikeBlock = findBlock(ahead, blockWorld);
      if (strikeBlock == null) { return; }

      // Strike block.
      if (blockWorld[strikeBlock[0]][strikeBlock[1]][strikeBlock[2]].strike())
      {
         // Destroy block and move ahead.
         blockWorld[strikeBlock[0]][strikeBlock[1]][strikeBlock[2]] = null;
         motion   = MOTION_TYPE.MOVE_FORWARD;
         distance = Block.BLOCK_SIZE;
         SoundEffects.BREAK.play();
      }
      else
      {
         motion   = MOTION_TYPE.STRIKE_BLOCK;
         distance = distance2 = Block.BLOCK_SIZE * 0.25f;
         SoundEffects.STRIKE.play();
      }
   }


   // Change direction.
   void pitchUp()
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      motion = MOTION_TYPE.PITCH_UP;
      angle  = 90.0f;
   }


   void pitchDown()
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      motion = MOTION_TYPE.PITCH_DOWN;
      angle  = 90.0f;
   }


   void yawRight()
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      motion = MOTION_TYPE.YAW_RIGHT;
      angle  = 90.0f;
   }


   void yawLeft()
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      motion = MOTION_TYPE.YAW_LEFT;
      angle  = 90.0f;
   }


   void rollRight()
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      motion = MOTION_TYPE.ROLL_RIGHT;
      angle  = 90.0f;
   }


   void rollLeft()
   {
      // Finish previous motion.
      while (motion != MOTION_TYPE.NONE)
      {
         update();
      }

      motion = MOTION_TYPE.ROLL_LEFT;
      angle  = 90.0f;
   }


   // Find block at given position.
   int[] findBlock(float[] position, Block[][][] blockWorld)
   {
      int[] block = new int[3];
      for (int i = 0; i < 3; i++)
      {
         if ((position[i] < 0.0f) ||
             (position[i] >= (float)Diggers.BLOCK_WORLD_DIMENSION)) { return(null); }
         block[i] = (int)(position[i] / Block.BLOCK_SIZE);
      }
      if (blockWorld[block[0]][block[1]][block[2]] == null)
      {
         return(null);
      }
      else
      {
         return(block);
      }
   }


   // Update.
   void update()
   {
      switch (motion)
      {
      case NONE:
         break;

      case MOVE_FORWARD:
         if (distance >= DISTANCE_DELTA)
         {
            moveForward(DISTANCE_DELTA);
            distance -= DISTANCE_DELTA;
         }
         else if (distance > 0.0f)
         {
            moveForward(distance);
            distance = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case SWIVEL_FORWARD:
         if (distance >= DISTANCE_DELTA)
         {
            moveForward(DISTANCE_DELTA);
            distance -= DISTANCE_DELTA;
         }
         else if (distance > 0.0f)
         {
            moveForward(distance);
            distance = 0.0f;
         }
         else if (angle >= ANGLE_DELTA)
         {
            pitchDown(ANGLE_DELTA);
            angle -= ANGLE_DELTA;
         }
         else if (angle > 0.0f)
         {
            pitchDown(angle);
            angle = 0.0f;
         }
         else if (distance2 >= DISTANCE_DELTA)
         {
            moveForward(DISTANCE_DELTA);
            distance2 -= DISTANCE_DELTA;
         }
         else if (distance2 > 0.0f)
         {
            moveForward(distance2);
            distance2 = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case STRIKE_BLOCK:
         if (distance >= DISTANCE_DELTA)
         {
            moveForward(DISTANCE_DELTA);
            distance -= DISTANCE_DELTA;
         }
         else if (distance > 0.0f)
         {
            moveForward(distance);
            distance = 0.0f;
         }
         else if (distance2 >= DISTANCE_DELTA)
         {
            moveForward(-DISTANCE_DELTA);
            distance2 -= DISTANCE_DELTA;
         }
         else if (distance2 > 0.0f)
         {
            moveForward(-distance2);
            distance2 = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case PITCH_UP:
         if (angle >= ANGLE_DELTA)
         {
            pitchUp(ANGLE_DELTA);
            angle -= ANGLE_DELTA;
         }
         else if (angle > 0.0f)
         {
            pitchUp(angle);
            angle = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case PITCH_DOWN:
         if (angle >= ANGLE_DELTA)
         {
            pitchDown(ANGLE_DELTA);
            angle -= ANGLE_DELTA;
         }
         else if (angle > 0.0f)
         {
            pitchDown(angle);
            angle = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case YAW_RIGHT:
         if (angle >= ANGLE_DELTA)
         {
            yawRight(ANGLE_DELTA);
            angle -= ANGLE_DELTA;
         }
         else if (angle > 0.0f)
         {
            yawRight(angle);
            angle = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case YAW_LEFT:
         if (angle >= ANGLE_DELTA)
         {
            yawLeft(ANGLE_DELTA);
            angle -= ANGLE_DELTA;
         }
         else if (angle > 0.0f)
         {
            yawLeft(angle);
            angle = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case ROLL_RIGHT:
         if (angle >= ANGLE_DELTA)
         {
            rollRight(ANGLE_DELTA);
            angle -= ANGLE_DELTA;
         }
         else if (angle > 0.0f)
         {
            rollRight(angle);
            angle = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;

      case ROLL_LEFT:
         if (angle >= ANGLE_DELTA)
         {
            rollLeft(ANGLE_DELTA);
            angle -= ANGLE_DELTA;
         }
         else if (angle > 0.0f)
         {
            rollLeft(angle);
            angle = 0.0f;
         }
         else
         {
            motion = MOTION_TYPE.NONE;
         }
         break;
      }
   }


   // Draw.
   void draw(GL2 gl) { }
}
