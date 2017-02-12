import java.util.ArrayList;
import java.util.Random;

public class Maze {
    private ArrayList<ArrayList<Integer>> board;
    final private int xSize, ySize;
    static private final double STRAIGHTNESS = 0.75;
    static public final int OUT_OF_BOARD = -1;
    static public final int UNDEFINED = 0;
    static public final int ROAD = 1;
    static public final int WALL = 2;

    public Maze(int xSize, int ySize){
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public void generate(int complexity) {
        init_board();
        assureReachability();
        for (int i = 0; i < complexity; i++) {
            createBranch();
        }
        fillBlank();
    }

    private void init_board() {
        //create 2d-arraylist
        //you can "get" and "set" an item with getCell() and setCell()
        board = new ArrayList<ArrayList<Integer>>(xSize);
        for (int x = 0; x < xSize+2; x++) {
            ArrayList<Integer> row = new ArrayList<Integer>(ySize);
            for (int y = 0; y < ySize+2 ; y++) {
                row.add(UNDEFINED);
            }
            board.add(row);
        }

        //sentinel method
        for (int x = 0; x < xSize+2; x++) {
            setCell(x, 0, OUT_OF_BOARD);
            setCell(x, ySize+1, OUT_OF_BOARD);
        }
        for (int y = 0; y < ySize+2; y++) {
            setCell(0, y, OUT_OF_BOARD);
            setCell(xSize+1, y, OUT_OF_BOARD);
        }        
    }

    private void assureReachability() {
        boolean up = Math.random() < 0.5;
        int x = 1;
        int y = 1;

        //(1, 1) is the start
        setCell(1, 1, ROAD);

        //while (x, y) reaches the goal (xSize, ySize)
        while (!(x == xSize && y == ySize)) {
            if (x == xSize) {
                y++;
            } else if (y == ySize) {
                x++;
            } else {
                if (up) y++; else x++;
                up = Math.random() < STRAIGHTNESS ? up : !up;
            }
            setCell(x, y, ROAD);
            fillCornerAround(x, y);
        }
    }

    private void fillCornerAround(int x, int y) {
        final int dx[] = {1, 1, 0, -1, -1, -1, 0, 1};
        final int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
        for (int d = 0; d < 8; d++) {
            if (isCorner(x+dx[d], y+dy[d])) {
                setCell(x+dx[d], y+dy[d], WALL);
            }
        }
    }

    private boolean isCorner(int x, int y) {
        if (getCell(x, y) != 0) return false;
        final boolean u = getCell(x, y+1) == ROAD;
        final boolean d = getCell(x, y-1) == ROAD;
        final boolean r = getCell(x+1, y) == ROAD;
        final boolean l = getCell(x-1, y) == ROAD;
        return (u && r) || (u && l) || (d && r) || (d && l);
    }

    private void createBranch() {
        //arraylist of tuple (x, y, dx, dy)
        ArrayList<ArrayList<Integer>> candidates = new ArrayList<ArrayList<Integer>>();
        final int dx4[] = {1, 0, -1, 0};
        final int dy4[] = {0, 1, 0, -1};
        for (int x = 1; x <= xSize ; x++) {
            for (int y = 1; y <= ySize ; y++) {
                for (int d = 0; d < 4; d++) {
                    if (can_dig_to(x, y, dx4[d], dy4[d])) {
                        ArrayList<Integer> c = new ArrayList<Integer>(4);
                        c.add(x); c.add(y); c.add(dx4[d]); c.add(dy4[d]);
                        candidates.add(c);
                    }
                }
            }
        }

        ArrayList<Integer> start_of_branch = candidates.get((new Random()).nextInt(candidates.size()));
        final int start_x = start_of_branch.get(0);
        final int start_y = start_of_branch.get(1);
        int dx = start_of_branch.get(2);
        int dy = start_of_branch.get(3);

        int x = start_x+dx;
        int y = start_y+dy;
        while (getCell(x, y) == UNDEFINED) {
            setCell(x, y, ROAD);
            fillCornerAround(x, y);

            // if digging direction should be changed
            if (Math.random() < 1.0 - STRAIGHTNESS) {
                if (Math.random() < 0.5) {
                    dx = Math.random() < 0.5 ? 1 : -1;
                    dy = 0;
                } else {
                    dx = 0;
                    dy = Math.random() < 0.5 ? 1 : -1;
                }
            }

            //dig
            x += dx;
            y += dy;
        }
    }

    private boolean can_dig_to(int x, int y, int dx, int dy) {
        return getCell(x, y) == ROAD && getCell(x+dx, y+dy) == UNDEFINED;
    }

    private void fillBlank() {
        for (int x = 1; x <= xSize ; x++) {
            for (int y = 1; y <= ySize ; y++) {
                if (getCell(x, y) == UNDEFINED) {
                    setCell(x, y, WALL);
                }
            }
        }        
    }    


    //util*****************************************
    private void setCell(int x, int y, int val) {
        board.get(x).set(y, val);
    }

    public int getCell(int x, int y) {
        return board.get(x).get(y);
    }

    public int getColSize() {
        return xSize;
    }

    public int getRowSize() {
        return ySize;
    }


    //for debug************************************
    public void debugPrint() {
        for (int x = 1; x <= xSize ; x++) {
            for (int y = 1; y <= ySize ; y++) {
                String s = "undefined";
                switch (getCell(x, y)) {
                    case OUT_OF_BOARD: s = "?"; break;
                    case UNDEFINED: s = "."; break;
                    case ROAD: s = "#"; break;
                    case WALL: s = "*"; break;
                }
                System.out.print(s);
            }
            System.out.print("\n");
        }
    }

    public void printMaze() {
        for (int x = 1; x <= xSize ; x++) {
            for (int y = 1; y <= ySize ; y++) {
                System.out.print(getCell(x, y) == ROAD ? "#" : ".");
            }
            System.out.print("\n");
        }
    }
}
