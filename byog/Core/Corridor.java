package byog.Core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Corridor implements Serializable {

    Position reference;
    int corridorLength; //How many units long the corridor is.
    Direction direction; //direction in which the corridor is heading

    public Corridor(Corridor oldCorridor, IntRange length,
                    int gridWidth, int gridHeight, MapGenerator m1, Random randomizer) {

        int ranNumber = RandomUtils.uniform(randomizer, 0, 4);
        direction = Direction.values()[ranNumber];


        Direction oppositeDirection = Direction.values()[(ranNumber + 2) % 4];

        //give a direction that doesnt go back
        if (oldCorridor.direction == oppositeDirection) {
            int directionInt = ranNumber;
            directionInt++;
            directionInt = directionInt % 4;
            direction = Direction.values()[directionInt];
        }

        corridorLength = length.getRandom();
        int maxLength = length.mMax;

        int startXPos = 0;
        int startYPos = 0;

        switch (oldCorridor.direction) {
            case NORTH:
                startXPos = oldCorridor.reference.x;
                startYPos = oldCorridor.getEndPositionY();
                break;

            case EAST:
                startXPos = oldCorridor.getEndPositionX();
                startYPos = oldCorridor.reference.y;
                break;

            case SOUTH:
                startXPos = oldCorridor.reference.x;
                startYPos = oldCorridor.getEndPositionY();
                break;

            case WEST:
                startXPos = oldCorridor.getEndPositionX();
                startYPos = oldCorridor.reference.y;
                break;
            default:
                break;
        }

        int newxPos = clamper(startXPos, 0, gridWidth - 1);
        int newyPos = clamper(startYPos, 0, gridHeight - 1);

        switch (direction) {
            case NORTH:
                maxLength = gridHeight - newyPos - 1;
                break;

            case EAST:
                maxLength = gridWidth - newxPos - 1;
                break;

            case SOUTH:
                maxLength = newyPos - 1;
                break;

            case WEST:
                maxLength = newxPos - 1;
                break;
            default:
                break;
        }
        if (corridorLength > maxLength) {
            corridorLength = maxLength;
        }
        reference = new Position(newxPos, newyPos);
    }

    //first parameter room is the room in this corridor come out of
    public Corridor(Room room, IntRange length, IntRange roomWidth, IntRange roomHeight,
                    int gridHeight, boolean firstCorridor, MapGenerator m1, Random randomizer) {
        //set a random dirrection
        Random rand = new Random(m1.SEED);

        int ranNumber = RandomUtils.uniform(randomizer, 0, 4);
        direction = Direction.values()[ranNumber];
        int gridWidth = MapGenerator.WIDTH;


        //get the index of direction that of the previous room entering corridor

        int index = Arrays.asList(Direction.values()).indexOf(room.enteringCorridor);

        //get the opposite direction
        Direction oppositeDirection = Direction.values()[(index + 2) % 4];


        if (!firstCorridor && direction == oppositeDirection) {
            int directionInt = ranNumber;
            directionInt++;
            directionInt = directionInt % 4;
            direction = Direction.values()[directionInt];

        }

        corridorLength = length.getRandom();

        int maxLength = length.mMax;

        int startXPos = 0;
        int startYPos = 0;

        //direction of the new corridor
        switch (direction) {
            case NORTH:
                startXPos = RandomUtils.uniform(randomizer, room.reference.x, room.reference.x
                        + room.roomWidth);

                //always start from the same
                startYPos = room.reference.y + room.roomHeight;
                maxLength = gridHeight - startYPos - roomHeight.mMin;
                break;
            case EAST:
                startXPos = room.reference.x + room.roomWidth;
                startYPos = RandomUtils.uniform(randomizer, room.reference.y, room.reference.y
                        + room.roomHeight);
                maxLength = gridWidth - startXPos - roomWidth.mMin;
                break;
            case SOUTH:
                startXPos = RandomUtils.uniform(randomizer, room.reference.x, room.reference.x
                        + room.roomWidth);
                startYPos = room.reference.y;
                maxLength = startYPos - roomHeight.mMin;
                break;
            case WEST:
                startXPos = room.reference.x;
                startYPos = RandomUtils.uniform(randomizer, room.reference.y, room.reference.y
                        + room.roomHeight);
                maxLength = startXPos - roomWidth.mMin;
                break;
            default:
                break;
        }
        if (corridorLength > maxLength) {
            corridorLength = maxLength;
        }
        reference = new Position(startXPos, startYPos);

        int newxPos = clamper(startXPos, 0, gridWidth - 1);
        int newyPos = clamper(startYPos, 0, gridHeight - 1);

        reference = new Position(newxPos, newyPos);


    }

    //get End position of corridor based on starting position and ending
    public int getEndPositionX() {

        //x does not change
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return reference.x;
        }

        //x changes
        if (direction == Direction.EAST) {
            return reference.x + corridorLength - 1;
        }
        return reference.x - corridorLength + 1;
    }

    public int getEndPositionY() {

        //height does not change
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return reference.y;
        }

        //height changes
        if (direction == Direction.NORTH) {
            return reference.y + corridorLength - 1;
        }
        return reference.y - corridorLength + 1;
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
