package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Game implements Serializable {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 90;
    public static final int HEIGHT = 45;
    TETile[][] tile;




    TERenderer ter = new TERenderer();

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        drawLayout();
        String checkString = "";
        long seedNum;
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            checkString = String.valueOf(key);
            if (checkString.equals("q")) {
                System.exit(0);
            }
            if (checkString.equals("n")) {
                break;
            }
            if (checkString.equals("l")) {
                break;
            }
        }
        if (checkString.equals("n")) {
            seedNum = getSeed();
            MapGenerator map = new MapGenerator(seedNum);
            ter.initialize(WIDTH, HEIGHT + 2, 0, 2);
            TETile[][] worldState = map.drawWholeWorld();
            allRenderer(worldState);
            String prev = "";
            String temp = "";
            while (true) {
                if (StdDraw.isMousePressed()) {
                    Position clickedPos = new Position((int) StdDraw.mouseX(),
                            (int) StdDraw.mouseY() - 2);
                    if (map.checkTeleport(clickedPos)) {
                        map.setPacman(clickedPos.x, clickedPos.y);
                    }
                    continue;
                }
                if (!StdDraw.isMousePressed()) {
                    allRenderer(worldState);
                }
                if (!StdDraw.hasNextKeyTyped()) {
                    continue;
                }
                char key = StdDraw.nextKeyTyped();
                checkString = String.valueOf(key);
                temp = prev;
                prev = checkString;
                if (temp.equals(":") && (prev.equals("q")  || (prev.equals("Q")))) {
                    saveWorld(new AllDataNeeded(seedNum, map.world, map.pacmanPos));
                    break;
                }
                if (checkString.equals("w")) {
                    map.setPacman(map.pacmanPos.x, map.pacmanPos.y + 1);
                }
                if (checkString.equals("s")) {
                    map.setPacman(map.pacmanPos.x, map.pacmanPos.y - 1);
                }
                if (checkString.equals("a")) {
                    map.setPacman(map.pacmanPos.x - 1, map.pacmanPos.y);
                }
                if (checkString.equals("d")) {
                    map.setPacman(map.pacmanPos.x + 1, map.pacmanPos.y);
                }
                allRenderer(worldState);
            }
        }
        helperKeyboard(checkString);
        System.exit(0);
    }


    public void helperKeyboard(String checkString) {
        if (checkString.equals("l")) {
            AllDataNeeded allData = loadWorld();
            TETile[][] worldState;
            long seedNum = allData.seed;
            MapGenerator m1 = new MapGenerator(seedNum);
            m1.world = allData.tile;
            m1.pacmanPos = allData.pacmanPos;
            ter.initialize(WIDTH, HEIGHT + 2, 0, 2);
            worldState = allData.tile;
            allRenderer(worldState);
            String prev = "";
            String temp = "";
            while (true) {
                if (StdDraw.isMousePressed()) {
                    Position clickedPos = new Position((int) StdDraw.mouseX(),
                            (int) StdDraw.mouseY() - 2);
                    if (m1.checkTeleport(clickedPos)) {
                        Position originalPos = new Position(m1.pacmanPos.x, m1.pacmanPos.y);
                        m1.world[clickedPos.x][clickedPos.y] = Tileset.PLAYER;
                        m1.pacmanPos = new Position(clickedPos.x, clickedPos.y);
                        m1.world[originalPos.x][originalPos.y] = Tileset.FLOOR;
                    }
                    continue;
                }
                if (!StdDraw.isMousePressed()) {
                    allRenderer(worldState);
                }
                if (!StdDraw.hasNextKeyTyped()) {
                    continue;
                }
                char key = StdDraw.nextKeyTyped();
                checkString = String.valueOf(key);
                temp = prev;
                prev = checkString;
                if (temp.equals(":") && (prev.equals("q"))) {
                    saveWorld(new AllDataNeeded(seedNum, m1.world, m1.pacmanPos));
                    break;
                }
                if (checkString.equals("w") && m1.checkMovement("w", m1.pacmanPos)) {
                    Position originalPos = new Position(m1.pacmanPos.x, m1.pacmanPos.y);
                    m1.world[m1.pacmanPos.x][m1.pacmanPos.y + 1] = Tileset.PLAYER;
                    m1.pacmanPos = new Position(m1.pacmanPos.x, m1.pacmanPos.y + 1);
                    m1.world[originalPos.x][originalPos.y] = Tileset.FLOOR;
                }
                if (checkString.equals("s") && m1.checkMovement("s", m1.pacmanPos)) {
                    Position originalPos = new Position(m1.pacmanPos.x, m1.pacmanPos.y);
                    m1.world[m1.pacmanPos.x][m1.pacmanPos.y - 1] = Tileset.PLAYER;
                    m1.pacmanPos = new Position(m1.pacmanPos.x, m1.pacmanPos.y - 1);
                    m1.world[originalPos.x][originalPos.y] = Tileset.FLOOR;
                }
                if (checkString.equals("a") && m1.checkMovement("a", m1.pacmanPos)) {
                    Position originalPos = new Position(m1.pacmanPos.x, m1.pacmanPos.y);
                    m1.world[m1.pacmanPos.x - 1][m1.pacmanPos.y] = Tileset.PLAYER;
                    m1.pacmanPos = new Position(m1.pacmanPos.x - 1, m1.pacmanPos.y);
                    m1.world[originalPos.x][originalPos.y] = Tileset.FLOOR;
                }
                if (checkString.equals("d") && m1.checkMovement("d", m1.pacmanPos)) {
                    Position originalPos = new Position(m1.pacmanPos.x, m1.pacmanPos.y);
                    m1.world[m1.pacmanPos.x + 1][m1.pacmanPos.y] = Tileset.PLAYER;
                    m1.pacmanPos = new Position(m1.pacmanPos.x + 1, m1.pacmanPos.y);
                    m1.world[originalPos.x][originalPos.y] = Tileset.FLOOR;
                }
                allRenderer(worldState);
            }
        }
    }



    public void allRenderer(TETile[][] worldState) {


        StdDraw.clear(Color.black);

        ter.renderFrame(worldState);


        Font smallFont = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.WHITE);

        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();

        String description = "None";

        int xint = (int) x;
        int yint = (int) y - 2;


        if ((xint < WIDTH - 1 && xint >= 0) && (yint < HEIGHT - 1 && yint >= 0)) {
            description = worldState[xint][yint].description();
        }

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date today = Calendar.getInstance().getTime();

        String reportDate = df.format(today);

        StdDraw.text(5, 1, "Tile type:  " + description);
        StdDraw.text(40, 1, reportDate);

        StdDraw.enableDoubleBuffering();
        StdDraw.show();



    }


    // command line argument constructor
    public Game() {
    }


    // play with keyboard constructor
    public Game(boolean something) {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);


        //set the scale for x and y
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);

        //clear the screen wuth black
        StdDraw.clear(Color.BLACK);

    }
    public void drawLayout() {
        int midWidth = WIDTH / 2;
        int midHeight = HEIGHT / 2;

        //clear the damn screen with color back
        //StdDraw.clear();
        StdDraw.clear(Color.black);


        Font bigFont = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, midHeight + 10, "CS61B :  The Game");


        Font smallFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);

        StdDraw.text(midWidth, midHeight + 2, "New Game (N)");
        StdDraw.text(midWidth, midHeight + 1, "Load Game (L)");
        StdDraw.text(midWidth, midHeight, "Quit (Q)");




    }

    public long getSeed() {
        int midWidth = WIDTH / 2;
        int midHeight = HEIGHT / 2;

        StdDraw.clear(Color.black);

        Font smallFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);

        StdDraw.text(midWidth, midHeight, "Please Enter seed: ");

        String stringDisplay = "";
        String checkString = "";

        while (!checkString.equals("s")) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            checkString = String.valueOf(key);
            stringDisplay += checkString;
            seedRenderer(stringDisplay);

        }

        stringDisplay = stringDisplay.substring(0, stringDisplay.length() - 1);

        System.out.println(stringDisplay);

        return Long.parseLong(stringDisplay);



    }
    public void seedRenderer(String stringDisplay) {
        int midWidth = WIDTH / 2;
        int midHeight = HEIGHT / 2;

        //StdDraw.clear();
        StdDraw.clear(Color.black);

        Font smallFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);

        StdDraw.text(midWidth, midHeight, "Please Enter seed: ");

        StdDraw.text(midWidth, midHeight - 1, stringDisplay);
    }






    public class AllDataNeeded implements Serializable {
        final long seed;
        final TETile[][] tile;
        Position pacmanPos;



        public AllDataNeeded(long seed, TETile[][] tile, Position pacmanPos) {
            this.seed = seed;
            this.tile = tile;
            this.pacmanPos = pacmanPos;
        }
    }


    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        String lowercaseInput = input.toLowerCase(), getDir = "", seedObtained = "";
        boolean firstS = false, toSave = false;
        if (lowercaseInput.charAt(0) == 'n') {
            for (int i = 1; i < lowercaseInput.length(); i++) {
                String c = lowercaseInput.substring(i, i + 1);
                if (c.equals("s") && !firstS) {
                    firstS = true;
                    continue;
                }
                if (!firstS) {
                    seedObtained += String.valueOf(c);
                }
                if (firstS) {
                    getDir += String.valueOf(c);
                }
            }
            if (getDir.length() >= 3) {
                if (getDir.substring(getDir.length() - 2, getDir.length()).equals(":q")) {
                    toSave = true;
                    getDir = getDir.substring(0, getDir.length() - 2);
                }
            }
            MapGenerator m1 = new MapGenerator(Long.valueOf(seedObtained));
            m1.drawWholeWorld();
            Position posOrig = m1.pacmanPos;
            m1.world[posOrig.x][posOrig.y] = Tileset.FLOOR;
            for (int j = 0; j < getDir.length(); j++) {
                String dir = getDir.substring(j, j + 1);
                if (m1.checkMovement(dir, m1.pacmanPos)) {
                    m1.movePacman(dir, m1.pacmanPos);
                }
            }
            m1.setPacman(m1.pacmanPos.x, m1.pacmanPos.y);
            if (toSave) {
                saveWorld(new AllDataNeeded(m1.SEED, m1.world, m1.pacmanPos));
            }
            return m1.world;
        }
        if (lowercaseInput.charAt(0) == 'l') {
            AllDataNeeded allData = loadWorld();
            TETile[][] w = allData.tile;
            Position initialPos = allData.pacmanPos;
            long seed = allData.seed;
            MapGenerator mapGenerated = new MapGenerator(seed);
            mapGenerated.pacmanPos = initialPos;
            mapGenerated.world = w;
            for (int i = 1; i < lowercaseInput.length(); i++) {
                String c = lowercaseInput.substring(i, i + 1);
                getDir += String.valueOf(c);
            }
            if (getDir.length() >= 3) {
                if (getDir.substring(getDir.length() - 2, getDir.length()).equals(":q")) {
                    toSave = true;
                    getDir = getDir.substring(0, getDir.length() - 2);
                }
            }
            Position oldPacmanPosBeforeMove;
            for (int j = 0; j < getDir.length(); j++) {
                String dir = getDir.substring(j, j + 1);
                if (mapGenerated.checkMovement(dir, mapGenerated.pacmanPos)) {
                    oldPacmanPosBeforeMove = new Position(mapGenerated.pacmanPos.x,
                            mapGenerated.pacmanPos.y);
                    mapGenerated.movePacman(dir, mapGenerated.pacmanPos);
                    mapGenerated.world[oldPacmanPosBeforeMove.x][oldPacmanPosBeforeMove.y]
                            = Tileset.FLOOR;
                }
            }
            mapGenerated.world[initialPos.x][initialPos.y] = Tileset.FLOOR;
            Position newPosition = new Position(mapGenerated.pacmanPos.x, mapGenerated.pacmanPos.y);
            mapGenerated.setPacman(newPosition.x, newPosition.y);
            mapGenerated.world[newPosition.x][newPosition.y] = Tileset.PLAYER;
            if (toSave) {
                saveWorld(new AllDataNeeded(seed, mapGenerated.world, newPosition));
            }
            return mapGenerated.world;
        }
        return null;
    }


    private void saveWorld(AllDataNeeded allDataNeeded) {
        File f = new File("./world.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(allDataNeeded);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }



    private AllDataNeeded loadWorld() {
        File f = new File("./world.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);

                AllDataNeeded allDataNeeded = (AllDataNeeded) os.readObject();
                os.close();
                return allDataNeeded;

            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        /* In the case no World has been saved yet, we return a new one. */
//        //return new MapGenerator();
        return null;
    }


}
