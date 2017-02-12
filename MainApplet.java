import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class MainApplet extends Applet {
    public void init() {
        setLayout(null);
        setBackground(Color.white);

        GameCanvas canvas = new GameCanvas();
        canvas.setBounds(10,10,500,500);
        add(canvas);
        canvas.start();
    }
}

class GameCanvas extends Canvas implements Runnable {
    static final private int msPerFrame = 30;
    private Thread thread;
    private Image buffer;
    private Graphics bufferg;
    private Game game;    

    public GameCanvas(){
        thread = new Thread(this);
    }

    public void start() {
        //after start() called, getSize gets enabled
        Dimension d = getSize();
        buffer = createImage(d.width, d.height);
        game = new Game(d.width, d.height, 30);
        addMouseMotionListener(new MouseMotionAdapterForGame(game));

        //game start
        game.loadGame();
        thread.start();
    }

    public void run() {
        try {
            while (true) {
                repaint();
                game.update(msPerFrame);
                thread.sleep(msPerFrame);
            }
        }
        catch(Exception e){}
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if(bufferg == null) bufferg = buffer.getGraphics();
        
        //clear buffer
        Dimension d = getSize();
        bufferg.setColor(Color.white);
        bufferg.fillRect(0, 0, d.width, d.height);
        
        //draw game scene
        game.draw(bufferg);
        g.drawImage(buffer, 0, 0, this);
    }
}

class MouseMotionAdapterForGame extends MouseAdapter {
    private Game game;

    public MouseMotionAdapterForGame(Game game) {
        this.game = game;
    }

    public void mouseMoved(MouseEvent e) {
        game.moveCursorPosition(e.getX(), e.getY());
    }

    public void mouseExited(MouseEvent e){
        //gameOver
        //you must call moveCursorPosition(), because now cursor is out of the canvas
        game.moveCursorPosition(-1, -1);
    } 
}
