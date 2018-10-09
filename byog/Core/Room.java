package byog.Core;


import java.io.Serializable;
import java.util.Random;

public class Room implements Serializable {
    Direction enteringCorridor; //the direction of corricdot that is entering this room
    Position reference;
    int roomWidth;
    int roomHeight;


    //used for the first room. It does not have a Corridor parameter
    public Room(IntRange widthRange, IntRange heightRange, int columns,
                int rows, MapGenerator m1, Random randomizer) {

        roomWidth = widthRange.getRandom();
        roomHeight = heightRange.getRandom();

        int xPos = (int) Math.round(columns / 2 - roomWidth / 2);
        int yPos = (int) Math.round(rows / 2 - roomHeight / 2);

        xPos = RandomUtils.uniform(randomizer, xPos - (int) columns / 3, xPos + (int) columns / 3);
        yPos = RandomUtils.uniform(randomizer, yPos - (int) rows / 3, yPos + (int) rows / 3);

        reference = new Position(xPos, yPos);
    }

    //incom
    public Room(IntRange widthRange, IntRange heightRange, int gridWidth,
                int gridHeight, Corridor corridor, MapGenerator m1, Random randomizer) {
        enteringCorridor = corridor.direction;

        roomWidth = widthRange.getRandom();
        roomHeight = heightRange.getRandom();


        int xPos = 0;
        int yPos = 0;

        switch (corridor.direction) {
            case NORTH:
                roomHeight = clamper(roomHeight, 1, gridHeight - corridor.getEndPositionY());
                yPos = corridor.getEndPositionY();
                xPos = RandomUtils.uniform(randomizer, corridor.getEndPositionX()
                        - roomWidth + 1, corridor.getEndPositionX());
                xPos = clamper(xPos, 0, gridWidth - roomWidth);
                break;
            case EAST:
                roomWidth = clamper(roomWidth, 1, gridWidth - corridor.getEndPositionX());
                xPos = corridor.getEndPositionX();
                yPos = RandomUtils.uniform(randomizer, corridor.getEndPositionY()
                        - roomHeight + 1, corridor.getEndPositionY());
                yPos = clamper(yPos, 0, gridHeight - roomHeight);
                break;
            case SOUTH:
                roomHeight = clamper(roomHeight, 1, corridor.getEndPositionY());
                yPos = corridor.getEndPositionY() - roomHeight + 1;
                xPos = RandomUtils.uniform(randomizer, corridor.getEndPositionX()
                        - roomWidth + 1, corridor.getEndPositionX());
                xPos = clamper(xPos, 0, gridWidth - roomWidth);
                break;
            case WEST:
                roomWidth = clamper(roomWidth, 1, corridor.getEndPositionX());
                xPos = corridor.getEndPositionX() - roomWidth + 1;
                yPos = RandomUtils.uniform(randomizer, corridor.getEndPositionY()
                        - roomHeight + 1, corridor.getEndPositionY());
                yPos = clamper(yPos, 0, gridHeight - roomHeight);
                break;
            default:
                break;
        }

        roomWidth = clamper(roomWidth, 0, widthRange.mMax);
        roomHeight = clamper(roomHeight, 0, heightRange.mMax);

        int newxPos = clamper(xPos, 1, gridWidth - roomWidth - 1);
        int newyPos = clamper(yPos, 1, gridHeight - roomHeight - 1);

        reference = new Position(newxPos, newyPos);
    }

    private int clamper(int x, int min, int max) {

        if (max < min) {
            max = min;
        }

        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        }
        return x;
    }


}



