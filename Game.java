import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;

public class Game {
    private final int canvasWidth, canvasHeight;
    private final int scale;
    private ArrayList<GameObject> objects;
    public int score;
    private boolean reverseCourse;
    private boolean stageStarted;
    private boolean stageCrashed;
    private boolean crashEffected;
    private boolean stageCleared;
    private boolean stageRestarting;

    public Game(int canvasWidth, int canvasHeight, int scale) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.scale = scale;
        objects = new ArrayList<GameObject>();
        score = 0;
        reverseCourse = false;
        stageStarted = false;
        stageCrashed = false;
        crashEffected = false;
        stageCleared = false;
        stageRestarting = false;
    }

    public void loadGame() {
        if (stageCleared) {
            score++;
            reverseCourse = !reverseCourse;
        }
        else {
            score = 0;
        }
        stageStarted = false;
        stageCrashed = false;
        crashEffected = false;
        stageCleared = false;
        stageRestarting = false;
        if (objects.size() > 0) objects.clear();

        Maze maze = new Maze(canvasWidth/scale, canvasHeight/scale);
        maze.generate(40);
        // maze.printMaze();
        for (int col = 1; col <= maze.getColSize(); col++) {
            for (int row = 1; row <= maze.getRowSize(); row++) {
                if (maze.getCell(col, row) == Maze.WALL) {
                    objects.add(new WallBlock(col, row, scale));
                    if (Math.random() < 0.5 && 1 < col && col < maze.getColSize() && 1 < row && row < maze.getRowSize()) {
                        objects.add(new MovingWallBlock(col, row, scale));
                    }
                }
            }
        }

        objects.add(new FieldBounds(canvasWidth/scale*scale, canvasHeight/scale*scale));
        objects.add(new Score(this, canvasWidth, canvasHeight));
        if (reverseCourse){
            objects.add(new StartMarker(1, 1, scale));
            objects.add(new GoalMarker(maze.getColSize(), maze.getRowSize(), scale));
        } else {
            objects.add(new StartMarker(maze.getColSize(), maze.getRowSize(), scale));
            objects.add(new GoalMarker(1, 1, scale));
        }
    }

    public void update(int msPerFrame) {
        for (GameObject obj : objects) {
            obj.update(msPerFrame);
            if (stageCleared) break;
        }
    }

    public void gameStart() {
        stageStarted = true;
        if (stageCrashed) {
            stageRestarting = true;
        }
    }

    public void gameOver() {
        if (stageStarted && !stageCleared && !stageCrashed) {
            stageCrashed = true;
        }
    }

    public void gameClear() {
        if (stageStarted && !stageCleared && !stageCrashed) {
            stageCleared = true;
        }
    }

    public void moveCursorPosition(int mouseX, int mouseY) {
        try {
            for (GameObject obj : objects) {
                obj.react(this, mouseX, mouseY);
                if (stageCleared) break;
            }
            if (stageCrashed && !crashEffected) {
                objects.add(new GameOverEffect(canvasWidth, canvasHeight));
                crashEffected = true;
            }
            if (stageCleared || stageRestarting) loadGame();
        }
        catch(Exception e){}
    }

    public void draw(Graphics g) {
        for (GameObject obj : objects) {
            obj.draw(g);
        }
    }
}