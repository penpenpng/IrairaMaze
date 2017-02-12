import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;


abstract public class GameObject {
    abstract public void draw(Graphics g);
    public void react(Game game, int mouseX, int mouseY) {}
    public void update(int msPerFrame) {}
}

class FieldBounds extends GameObject {
    final private int width, height;
    public FieldBounds(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(0, 0, width-1, height-1);
    }

    public void react(Game game, int mouseX, int mouseY) {
        if (mouseX < 0 || width < mouseX || mouseY < 0 || height < mouseY){
            game.gameOver();
        }
    }
}

class WallBlock extends GameObject {
    final private int x, y, scale;
    public WallBlock(int row, int column, int scale) {
        this.scale = scale;
        x = (column-1)*scale;
        y = (row-1)*scale;
    }

    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(x, y, scale, scale);
    }

    public void react(Game game, int mouseX, int mouseY) {
        if (x < mouseX && mouseX < x+scale && y < mouseY && mouseY < y+scale){
            //gameOver
            game.moveCursorPosition(-1, -1);
        }
    }
}

class MovingWallBlock extends GameObject {
    final private int defx, defy, scale;
    private int x, y;
    final int direction;
    private int displacement;
    public MovingWallBlock(int row, int column, int scale) {
        this.scale = scale;
        defx = x = (column-1)*scale;
        defy = y = (row-1)*scale;
        direction = (new Random()).nextInt(4);
        displacement = Math.random() < 0.5 ? 0 : scale;
    }

    public void update(int msPerFrame) {
        displacement += 1;
        displacement %= 2*scale;

        int diff = displacement < scale ? displacement : 2*scale-displacement;
        switch (direction) {
            case 0: x = defx + diff; break;
            case 1: x = defx - diff; break;
            case 2: y = defy + diff; break;
            case 3: y = defy - diff; break;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(x, y, scale, scale);
    }

    public void react(Game game, int mouseX, int mouseY) {
        if (x < mouseX && mouseX < x+scale && y < mouseY && mouseY < y+scale){
            game.gameOver();
        }
    }
}

class StartMarker extends GameObject {
    final private int x, y, scale;
    public StartMarker(int row, int column, int scale) {
        this.scale = scale;
        x = (column-1)*scale;
        y = (row-1)*scale;
    }

    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(x, y, scale, scale);
    }

    public void react(Game game, int mouseX, int mouseY) {
        double x2 = mouseX-(x+(double)scale/2);
        x2 *= x2;
        double y2 = mouseY-(y+(double)scale/2);
        y2 *= y2;
        double s2 = (double)scale/2;
        s2 *= s2;
        if (x2 + y2 < s2){
            game.gameStart();
        }
    }
}

class GoalMarker extends GameObject {
    final private int x, y, scale;
    public GoalMarker(int row, int column, int scale) {
        this.scale = scale;
        x = (column-1)*scale;
        y = (row-1)*scale;
    }

    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(x, y, scale, scale);
    }

    public void react(Game game, int mouseX, int mouseY) {
        double x2 = mouseX-(x+(double)scale/2);
        x2 *= x2;
        double y2 = mouseY-(y+(double)scale/2);
        y2 *= y2;
        double s2 = (double)scale/2;
        s2 *= s2;
        if (x2 + y2 < s2){
            game.gameClear();
        }
    }
}

class Score extends GameObject {
    private Game game;
    private int width, height;
    public Score (Game game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        Font font = new Font("Arial", Font.BOLD, 120);
        g.setFont(font);
        g.setColor(Color.blue);
        g.drawString(String.valueOf(game.score), 0, height - 30);
    }    
}

class GameOverEffect extends GameObject {
    final private int width, height;
    private float opacity = 1.0f;

    public GameOverEffect(int width, int height) {
        this.width = width;
        this.height = height;        
    }

    public void draw(Graphics g) {
        g.setColor(new Color(1.0f, 0f, 0f, opacity));
        g.fillRect(0, 0, width, height);
    }

    public void update(int msPerFrame) {
        if (opacity > 0) opacity -= 0.1f;
    }
}