/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package astar.pcg;

import astar.plugin.ILevelGenerator;
import astar.aes.Tools;
import astar.aes.World;

/**
 * Generates a random level using a series of connected rooms.
 * @author Martin Wells
 */

public class Wells implements ILevelGenerator
{
   private static final int LEFT_DIR = 0;
   private static final int RIGHT_DIR = 1;
   private static final int UP_DIR = 2;
   private static final int DOWN_DIR = 3;

   private int width;
   private int height;
   private char[][] tileMap;
   private int[] roomX;
   private int[] roomY;
   private int[] roomW;
   private int[] roomH;

   private int roomCount;
   private int playerStartX, playerStartY;
   private int gatewayX, gatewayY;
   private int seed = 0;

   public Wells()
   {
       this.seed = 0;
   }
   
   @Override
   public void init(int seed) {
       this.seed = seed;
   }

   private void clear()
   {
      for (int ty = 0; ty < height; ty++)
         for (int tx = 0; tx < width; tx++)
            tileMap[ty][tx] = World.NO_TILE;
   }

   public int getPlayerStartX()
   {
      return playerStartX;
   }

   public int getPlayerStartY()
   {
      return playerStartY;
   }
   
   public int getGatewayX() {
	   return gatewayX;
   }
   
   public int getGatewayY() {
	   return gatewayY;
   }
   
   public char[][] getTileMap() {
	   return tileMap;
   }
   
   /**
    * Generates a new level using a room corridor system. The density of the
    * level is dictated by the level integer.
    * @param level A relative level on density for the level (the higher the
    * number the denser the level.
    * @return A new byte array containing the tiles for the newly generated
    * level.
    */
   @Override
   public char[][] generateLevel(int level)   {
              
       Tools.setRandomizer(seed);
       
      // Set the size of the level relative to the level number provided.
      width = 30 + (level * 2);
      height = 30 + (level * 2);
      // Cap the level to a reasonable maximum.
      if (width > 100) width = 100;
      if (height > 100) height = 100;

      // Construct a new tile map based on this size and clear it.
      tileMap = new char[height][width];
      clear();

      // The minRooms is used later to determine when to stop generating new
      // rooms. It's more of a target than an exact number though.
      int totalRooms = 10 + (level * 2);

      //System.out.println("Generating level: " + level + " minRooms: " +
      //                   totalRooms + " width: " + width + " height: " + height);

      // To track all the rooms created in a level we use an array for each
      // room's x, y tile position, the width and height as well as whether the
      // room was a corridor joining two others. This is mostly used by code
      // to determine if there is enough space to place a new room.
      roomX = new int[totalRooms];
      roomY = new int[totalRooms];
      roomW = new int[totalRooms];
      roomH = new int[totalRooms];

      // The minimum size of each room.
      int minRoomHeight = World.TILE_ROOM_MIN_HEIGHT;
      int minRoomWidth = World.TILE_ROOM_MIN_WIDTH;
      int maxRoomHeight = World.TILE_ROOM_MAX_HEIGHT;
      int maxRoomWidth = World.TILE_ROOM_MAX_WIDTH;

      // Pick the size and location of the first room.
      int corner = Tools.getRand(0, 3);
      int roomStartX = 3;
      int roomStartY = 3;

      switch (corner)
      {
         // case 0 is top left (3,3) (which is already initialised)
         case 1: // top right
            roomStartX = width - maxRoomWidth;
            roomStartY = 3;
            break;
         case 2: // bottom right
            roomStartX = width - maxRoomWidth;
            roomStartY = height - maxRoomHeight;
            break;
         case 3: // bottom left
            roomStartX = 3;
            roomStartY = height - maxRoomHeight;
            break;
      }

      // Add the first room to the map.
      addRoom(level, roomStartX, roomStartY,
              Tools.getRand(minRoomWidth, maxRoomWidth),
              Tools.getRand(minRoomHeight, maxRoomHeight));

      // Set the location where the player's ship will start from.
      playerStartX = roomX[0] + (roomW[0] / 2);
      playerStartY = roomY[0] + (roomH[0] / 2);
      tileMap[playerStartY][playerStartX] = World.PLAYER_START_TILE;

      // The addRoom method will fill a room with other objects (such as enemy
      // fighter). This code clears anything that was added to near the player's
      // start point.
      for (int ty = 0; ty < 3; ty++)
         for (int tx = 0; tx < 3; tx++)
            tileMap[playerStartY - 1 + ty][playerStartX - 1 + tx] =
                    World.NO_TILE;

      // Each room added is spaced relative to the previous one so this is
      // an index to keep track of the last full room created (not the last
      // corridor connecting two rooms).
      int lastRoomIndex = 0;

      // Since the code randomly tries different rooms there are cases where
      // a new room is not valid (too big etc). In this case the code will
      // loop around for another go. In order to stop the generation process
      // going for too long the 'tries' counter stops execution after a
      // reasonable number of tries have been executed.
      int tries = 0;

      // Used inside the loop when creating new rooms.
      int newRoomX = 0;
      int newRoomY = 0;

      // Keep tracking of the number of rooms added to the map. Once the room
      // qouta has been reached the level is complete.
      roomCount = 1;

      // As rooms are added they may not fit. If not we try again using a
      // different (random) direction. This array is used to track what
      // directons have previously been tried.
      boolean[] dirsTried = new boolean[4];

      while (roomCount < totalRooms - 1 && tries < 100)
      {
         tries++;

         // Grab the info on the last room created.
         int lastRoomX = roomX[lastRoomIndex];
         int lastRoomY = roomY[lastRoomIndex];
         int lastRoomW = roomW[lastRoomIndex];
         int lastRoomH = roomH[lastRoomIndex];

         // Pick a random size for the new room.
         int newRoomW = Tools.getRand(minRoomWidth, maxRoomWidth);
         int newRoomH = Tools.getRand(minRoomHeight, maxRoomHeight);

         // If the all the previous directions have been tried we reset them
         // and start again.
         if (areAllTrue(dirsTried))
         {
            // reset the tried dirs to have another go
            for (int i = 0; i < 4; i++)
               dirsTried[i] = false;
         }

         // Pick a random dir from the ones that have not previously been tried.
         int dir = getRandDir(dirsTried);

         // Mark this direction as tried.
         dirsTried[dir] = true;

         // Figure the corridor dimensions to connect up this new room.
         int corridorWidth = Tools.getRand(World.TILE_CORRIDOR_MIN, 10);
         int corridorHeight = Tools.getRand(World.TILE_CORRIDOR_MIN, minRoomHeight - 2);
         if (dir == UP_DIR || dir == DOWN_DIR)
         {
            corridorWidth = Tools.getRand(World.TILE_CORRIDOR_MIN, minRoomWidth - 2);
            corridorHeight = Tools.getRand(World.TILE_CORRIDOR_MIN, 10);
         }

         // Positioning of the new room. Location is based on the direction
         // picked (randomly from a list of ones previously tried) plus distance
         // for a coridor to connect it up.

         // If the room is to the left or right.
         if (dir == LEFT_DIR || dir == RIGHT_DIR)
         {
            // First choose a new x position (it's relatively fixed based on the
            // position of the previous room and the width of the corridor
            // (already chosen above).
            if (dir == LEFT_DIR)  // to the left
               newRoomX = lastRoomX - newRoomW - corridorWidth + 2;
            if (dir == RIGHT_DIR) // to the right
               newRoomX = lastRoomX + lastRoomW + corridorWidth - 2;

            // Next determine the vertical position of the new room. This code
            // ensures enough space is left availble to fit in the corridor
            // (positioned on the left or right).
            int lowPoint = Math.max(1, lastRoomY + corridorHeight - newRoomH);
            int highPoint = lastRoomY + lastRoomH - corridorHeight;
            newRoomY = Tools.getRand(lowPoint, highPoint);
         }

         // If the room is above or below.
         if (dir == UP_DIR || dir == DOWN_DIR)
         {
            // First choose a new y position (it's relatively fixed based on the
            // position of the previous room and the height of the corridor
            // (already chosen above).
            if (dir == UP_DIR)
               newRoomY = lastRoomY - corridorHeight - newRoomH + 2;
            if (dir == DOWN_DIR)
               newRoomY = lastRoomY + lastRoomH + corridorHeight - 2;

            // Next determine the horizontal position of the new room. This code
            // ensures enough space is left availble to fit in the corridor
            // (positioned on the above or below).
            int lowPoint = Math.max(1, lastRoomX + corridorWidth - newRoomW);
            int highPoint = lastRoomX + lastRoomW - corridorWidth;
            newRoomX = Tools.getRand(lowPoint, highPoint);
         }

         // Check to see if this new room is within the dimensions of the map.
         if (Tools.isRectWithinRect(0, 0, width - 1, height - 1,
                                    newRoomX, newRoomY, newRoomW, newRoomH))
         {
            // Check the room is not too close (or overlapping) another room.
            if (!isRectNearRoom(newRoomX, newRoomY, newRoomW, newRoomH))
            {
               // Clear to add this room to the map.
               addRoom(level, newRoomX, newRoomY, newRoomW, newRoomH);

               // Add the corridor connecting the new room to the last one.
               int corridorX = 0;
               int corridorY = 0;

               // Connect a new room either to the left or right.
               if (dir == LEFT_DIR || dir == RIGHT_DIR)
               {
                  if (dir == LEFT_DIR)
                     corridorX = lastRoomX - corridorWidth + 1;
                  if (dir == RIGHT_DIR)
                     corridorX = lastRoomX + lastRoomW - 1;

                  corridorY = Tools.getRand(Math.max(lastRoomY, newRoomY),
                                            Math.min(lastRoomY + lastRoomH -
                                                     corridorHeight,
                                                     newRoomY + newRoomH -
                                                     corridorHeight));
               }

               // Connect a new room either above or below.
               if (dir == UP_DIR || dir == DOWN_DIR)
               {
                  if (dir == UP_DIR)
                     corridorY = lastRoomY - corridorHeight + 1;
                  if (dir == DOWN_DIR)
                     corridorY = lastRoomY + lastRoomH - 1;

                  corridorX = Tools.getRand(Math.max(lastRoomX, newRoomX),
                                            Math.min(lastRoomX + lastRoomW -
                                                     corridorWidth,
                                                     newRoomX + newRoomW -
                                                     corridorWidth));
               }

               // Draw the corridor on the tilemap.
               addRoom(level, corridorX, corridorY, corridorWidth,
                       corridorHeight);

               // Set the last room index to be the room we added (step back by
               // an extra one to skip the corridor just added to connect the
               // new room to the last one).
               lastRoomIndex = roomCount - 2;
            }
         }
      }

      // End point is the second last room we added (last one
      // was the corridor to it)
      setEndPoint(roomX[roomCount - 2] + roomW[roomCount - 2] / 2,
                  roomY[roomCount - 2] + roomH[roomCount - 2] / 2);
      
      tileMap[playerStartY][playerStartX] = World.PLAYER_START_TILE;

      //#ifdef debug
      //dump();
      //#endif
      return tileMap;
   }

   /**
    * Sets the exit point in the tile map (the gateway tile).
    * @param tileX The x position of the end point.
    * @param tileY The y position of the end point.
    */
   private void setEndPoint(int tileX, int tileY)
   {
      tileMap[tileY][tileX] = World.GATEWAY_TILE;
      gatewayX = tileX;
      gatewayY = tileY;
   }

   /**
    * @param b An array of booleans to test.
    * @return True if all the booleans in the array are true.
    */
   private static boolean areAllTrue(boolean[] b)
   {
      for (int i = 0; i < b.length; i++)
         if (b[i] == false) return false;
      return true;
   }

   /**
    * Based on an array of boolean's this returns a random choice from a limited
    * list of ones that are currently false. The level generator uses this to
    * randomly choose a direction, but only from ones that have not previously
    * been tried (set to true in the array).
    */
   private static int getRandDir(boolean[] dirs)
   {
      // we only do a random test on the number of available dirs, so lets
      // find out how many there are first
      int numDirs = 0;
      for (int i = 0; i < 4; i++)
         if (!dirs[i]) numDirs++;

      if (numDirs == 0) return 0;

      // now pick one at random
      int n = 0;
      if (numDirs > 1)
         n = Tools.getRand(0, numDirs - 1);

      // and return the dir corresponding to the nth result by figuring
      // the array index of the nth true value
      int c = -1;
      int i = 0;
      while (i < dirs.length)
      {
         if (!dirs[i++]) c++;
         if (c == n) return i - 1;
      }

      return 0;
   }

   /**
    * Checks to see if the supplied rectangle is within 3 tiles of any other
    * room (including corridors).
    * @param tileX The x position of the room to be checked.
    * @param tileY The y position of the room to be checked.
    * @param tilesWide The width of the room to be checked.
    * @param tilesHigh The height of the room to be checked.
    * @return True if the rectangle (room) is within 3 tiles of another.
    */
   private boolean isRectNearRoom(int tileX, int tileY, int tilesWide,
                                        int tilesHigh)
   {
      for (int i = 0; i < roomCount; i++)
      {
         if (Tools.isIntersectingRect(tileX, tileY, tilesWide, tilesHigh,
                                      roomX[i] - 3, roomY[i] - 3,
                                      roomW[i] + 3, roomH[i] + 3))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Adds a room to the level by setting all the bytes in the tilemap to be
    * walls. Note that existing wall tiles will be inverted by this process
    * (except for corners) in order to "carve out" doorways where the walls
    * between rooms and corridors overlap.
    * @param level The level number is a relative density level used to fill
    * the room with objects.
    * @param tileX The x position of the new room.
    * @param tileY The y position of the new room.
    * @param tileWidth The width of the new room.
    * @param tileHeight The height of the new room.
    */
   private void addRoom(int level, int tileX, int tileY, int tileWidth,
                        int tileHeight)
   {
      //System.out.println("adding room: " + roomCount + " max=" + roomW.length +
      // " xy=" + x + ", " + y + " tileMapsize=" + tileMap[0].length + "," +
      // tileMap.length);

      // If it's a corridor we dont just add tiles, we invert in order
      // to cut doorways out of the rooms
      addWallsToMap(tileX, tileY, tileWidth, tileHeight);
      roomW[roomCount] = tileWidth;
      roomH[roomCount] = tileHeight;
      roomX[roomCount] = tileX;
      roomY[roomCount] = tileY;
      roomCount++;
   }

   /**
    * Toggles tiles in the tilemap to either a wall or an empty space (based on
    * what is there already) in an outline rectangle using the supplied bounding
    * coordinates. Note that corners are NOT inverted, they are always set as
    * walls.
    * @param roomTileX The starting x position of the room to create.
    * @param roomTileY The starting y position of the room to create.
    * @param roomTilesWide The width of the room to create.
    * @param roomTilesHigh The height of the room to create.
    */
   private void addWallsToMap(int roomTileX, int roomTileY,
                              int roomTilesWide, int roomTilesHigh)
   {
      // Add the top and bottom line.
      for (int tileX = roomTileX; tileX < roomTileX + roomTilesWide; tileX++)
      {
         // Invert the tiles along the top.
         invertTile(tileX, roomTileY);
         // Invert the tiles along the bottom.
         invertTile(tileX, roomTileY + roomTilesHigh - 1);
      }

      // Left and right side lines.
      for (int tileY = roomTileY + 1; tileY < roomTileY + roomTilesHigh - 1;
           tileY++)
      {
         // Invert the tiles down the left side.
         invertTile(roomTileX, tileY);
         // Invert the tiles down the right side.
         invertTile(roomTileX + roomTilesWide - 1, tileY);
      }

      // Mark corners as walls (not inverted).
      tileMap[roomTileY][roomTileX] = World.WALL_TILE;
      tileMap[roomTileY][roomTileX + roomTilesWide - 1] = World.WALL_TILE;
      tileMap[roomTileY + roomTilesHigh - 1][roomTileX] = World.WALL_TILE;
      tileMap[roomTileY + roomTilesHigh - 1][roomTileX + roomTilesWide - 1] =
              World.WALL_TILE;
   }

   /**
    * Inverts an existing map tile between either empty or a wall tile.
    * @param tileX The x position of the tile to invert.
    * @param tileY The y position of the tile to invert.
    */
   private void invertTile(int tileX, int tileY)
   {
      // Turn an empty tile into a wall or vice versa.
      if (tileMap[tileY][tileX] == World.WALL_TILE)
         tileMap[tileY][tileX] = World.NO_TILE;
      else
         tileMap[tileY][tileX] = World.WALL_TILE;
   }

   public void dump(String path)
   {
      System.out.println("\t          1         2         3         4         5");
      System.out.println("\t012345678901234567890123456789012345678901234567890");

      for (int ty = 0; ty < height; ty++)
      {
         System.out.print(ty + "\t");

         for (int tx = 0; tx < width; tx++)
         {
            if (tileMap[ty][tx] == World.WALL_TILE)
               System.out.print("#");
            else if (tileMap[ty][tx] == World.GATEWAY_TILE)
               System.out.print("$");
            else if (tileMap[ty][tx] == World.DRONE_ACTIVATOR_TILE)
               System.out.print("D");
            else if (tileMap[ty][tx] == World.TURRET_ACTIVATOR_TILE)
               System.out.print("T");
            else if (tileMap[ty][tx] == World.FIGHTER_ACTIVATOR_TILE)
               System.out.print("F");
            else if (playerStartX == tx && playerStartY == ty) {
               System.out.print("X");
            }
            else
               System.out.print(" ");
         }
         System.out.println();
      }

   }
}
