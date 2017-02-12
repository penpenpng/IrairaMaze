
public class Test {
    public static void main(String[] args){
        Maze m = new Maze(15, 15);
        m.generate(30);
        m.printMaze();
    }
}