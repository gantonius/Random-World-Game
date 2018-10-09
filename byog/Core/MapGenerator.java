package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Random;

public class MapGenerator implements Serializable {
    static final int WIDTH = 90;
    private static final int HEIGHT = 45;
    // The range of widths rooms can have.

    ArrayList<Room> rooms;
    ArrayList<Corridor> corridors;
    TETile[][] world;
    Position posToCheck;

    Position pacmanPos;

    IntRange roomWidth;
    IntRange roomHeight;
    IntRange corridorLength;

    Random randomizer;



    long SEED;

    public MapGenerator(long sd) {
        SEED = sd;
        roomWidth = new IntRange(5, 8, this);
        // The range of heights rooms can have.
        roomHeight = new IntRange(5, 8, this);
        corridorLength = new IntRange(10, 12, this);
        rooms = new ArrayList<Room>();
        corridors = new ArrayList<Corridor>();
        randomizer = new Random(sd);
    }



    private void setTilesValuesForRooms() {
        for (int i = 0; i < rooms.size(); i++) {
            Room currentRoom = rooms.get(i);

            //gor each room go through its width
            for (int j = 0; j < currentRoom.roomWidth; j++) {
                int xCoord = currentRoom.reference.x + j;

                // For each horizontal tile, go up vertically through the room's height.
                for (int k = 0; k < currentRoom.roomHeight; k++) {
                    int yCoord = currentRoom.reference.y + k;

                    // The coordinates in the jagged array are based on the room's
                    // position and it's width and height.
                    /*System.out.println(xCoord + " ," + yCoord + "," + currentRoom.reference.y
                            + "," + currentRoom.roomHeight);*/
                    world[xCoord][yCoord] = Tileset.FLOOR;
                }
            }
        }
    }




    private void setTilesValuesForCorridors() {
        for (int i = 0; i < corridors.size(); i++) {
            Corridor currentCorridor = corridors.get(i);

            for (int j = 0; j < currentCorridor.corridorLength; j++) {
                int xCoord = currentCorridor.reference.x;
                int yCoord = currentCorridor.reference.y;

                switch (currentCorridor.direction) {
                    case NORTH:
                        yCoord += j;
                        break;
                    case EAST:
                        xCoord += j;
                        break;
                    case SOUTH:
                        yCoord -= j;
                        break;
                    case WEST:
                        xCoord -= j;
                        break;
                    default:
                        break;
                }
                world[xCoord][yCoord] = Tileset.FLOOR;
            }
        }
    }

    private void generateWalls() {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    changeSurrondings(i, j);
                }
            }
        }
    }

    private void changeSurrondings(int xPos, int yPos) {

        if (world[xPos + 1][yPos] == Tileset.NOTHING) {
            world[xPos + 1][yPos] = Tileset.WALL;
        }
        if (world[xPos][yPos + 1] == Tileset.NOTHING) {
            world[xPos][yPos + 1] = Tileset.WALL;
        }

        if (world[xPos + 1][yPos + 1] == Tileset.NOTHING) {
            world[xPos + 1][yPos + 1] = Tileset.WALL;
        }
        if (world[xPos - 1][yPos - 1] == Tileset.NOTHING) {
            world[xPos - 1][yPos - 1] = Tileset.WALL;
        }
        if (world[xPos - 1][yPos + 1] == Tileset.NOTHING) {
            world[xPos - 1][yPos + 1] = Tileset.WALL;
        }
        if (world[xPos + 1][yPos - 1] == Tileset.NOTHING) {
            world[xPos + 1][yPos - 1] = Tileset.WALL;
        }
        if (world[xPos][yPos - 1] == Tileset.NOTHING) {
            world[xPos][yPos - 1] = Tileset.WALL;
        }
        if (world[xPos - 1][yPos] == Tileset.NOTHING) {
            world[xPos - 1][yPos] = Tileset.WALL;
        }
    }

    public void createRoomsAndCorridors(int numOfRooms) {

        //Generate first room and first corridor
        Room firstRoom = new Room(roomWidth, roomHeight, WIDTH, HEIGHT, this, randomizer);
        this.rooms.add(firstRoom);
        Corridor firstCorridor = new Corridor(firstRoom, corridorLength,
                roomWidth, roomHeight, HEIGHT, true, this, randomizer);
                //roomWidth, roomHeight, WIDTH, HEIGHT, true, this, randomizer);
        corridors.add(firstCorridor);

        for (int i = rooms.size(); i < numOfRooms; i++) {
            if (RandomUtils.uniform(randomizer, 0, 6) < 4) {
                corridors.add(new Corridor(corridors.get(corridors.size() - 1),
                        corridorLength, WIDTH, HEIGHT, this, randomizer));

            }
            rooms.add(new Room(roomWidth, roomHeight, WIDTH, HEIGHT,
                    corridors.get(corridors.size() - 1), this, randomizer));
            corridors.add(new Corridor(rooms.get(rooms.size() - 1), corridorLength,
                    roomWidth, roomHeight, HEIGHT, false, this, randomizer));
        }
    }

    public void startPacman() {


        Random rand = new Random(SEED);
        int x = RandomUtils.uniform(randomizer, 2, WIDTH - 1);
        int y = RandomUtils.uniform(randomizer, 2, HEIGHT - 1);

        while (!world[x][y].equals(Tileset.FLOOR)) {
            x = RandomUtils.uniform(randomizer, 2, WIDTH - 1);
            y = RandomUtils.uniform(randomizer, 2, HEIGHT - 1);
        }
        pacmanPos = new Position(x, y);
        world[x][y] = Tileset.PLAYER;

    }

    public void setPacman(int x, int y) {

        if (world[x][y] == Tileset.FLOOR) {
            world[pacmanPos.x][pacmanPos.y] = Tileset.FLOOR;
            world[x][y] = Tileset.PLAYER;

            pacmanPos = new Position(x, y);
        }
    }

    public void fillinTile() {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] drawWholeWorld() {

        // initialize tiles
        world = new TETile[WIDTH][HEIGHT];
        fillinTile();

        int randomNums = RandomUtils.uniform(randomizer, 20, 60);

        createRoomsAndCorridors(randomNums);
        setTilesValuesForRooms();
        setTilesValuesForCorridors();
        generateWalls();
        startPacman();


        return world;
    }

    public boolean checkTeleport(Position pos) {
        if ((world[pos.x][pos.y].description()).equals(Tileset.FLOOR.description())) {
            return true;
        }
        return false;
    }

    public boolean checkMovement(String toCheck, Position pos) {
        switch (toCheck) {
            case "w":
                posToCheck = new Position(pos.x, pos.y + 1);
                return (world[posToCheck.x][posToCheck.y].description()).
                        equals(Tileset.FLOOR.description());
            case "s":
                posToCheck = new Position(pos.x, pos.y - 1);
                return (world[posToCheck.x][posToCheck.y].description()).
                        equals(Tileset.FLOOR.description());
            case "a":
                posToCheck = new Position(pos.x - 1, pos.y);
                return (world[posToCheck.x][posToCheck.y].description()).
                        equals(Tileset.FLOOR.description());
            case "d":
                posToCheck = new Position(pos.x + 1, pos.y);
                return (world[posToCheck.x][posToCheck.y].description()).
                        equals(Tileset.FLOOR.description());
            default:
                return false;
        }
    }

    public Position movePacman(String dir, Position pos) {
        switch (dir) {
            case "w":
                pacmanPos = new Position(pos.x, pos.y + 1);
                return pacmanPos;
            case "s":
                pacmanPos = new Position(pos.x, pos.y - 1);
                return pacmanPos;
            case "a":
                pacmanPos = new Position(pos.x - 1, pos.y);
                return pacmanPos;
            case "d":
                pacmanPos = new Position(pos.x + 1, pos.y);
                return pacmanPos;
            default:
                return null;
        }
    }

    public static void drawing(TETile[][] worldToDraw) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 2, 0, 2);
        ter.renderFrame(worldToDraw);
    }
}


