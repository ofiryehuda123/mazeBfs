import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class Maze extends JFrame {
    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;


    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }

        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       //this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }


    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_BRUTE_FORCE:
                    break;
                case Definitions.ALGORITHM_DFS:
                    break;
                case Definitions.ALGORITHM_BFS:
                    result = startBfs(new int[]{0,0});
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");
        }).start();
    }


    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean startBfs(int[] start) {
        boolean result = false;
        Queue<int[]> queue = new LinkedList();
        setSquareAsVisited(start[0], start[1],true);
        queue.add(start);
        while (!queue.isEmpty()) {
            int[] currentNode = queue.remove();
            setSquareAsVisited(currentNode[0], currentNode[1], true);
            if(currentNode[0] == values.length - 1 && currentNode[1] == values[0].length - 1) {
                result = true;
                break;
            }
            for (int[] currentNeighbor : getNeighborsBfs(currentNode)) {
                if (!isVisited(currentNeighbor)) {
                    setSquareAsVisited(currentNeighbor[0], currentNeighbor[1], true);
                    queue.add(currentNeighbor);

                }
            }
        }
        return result;
    }


    private boolean isVisited(int[] node){
        return this.visited[node[0]][node[1]];
    }


    private ArrayList<int[]> getNeighborsBfs(int[] currentNode){
        ArrayList<int[]> neighbors = new ArrayList<>();
        for(int i = currentNode[0] - 1; i <= currentNode[0]+ 1; i++){
            for(int j = currentNode[1] -1; j <= currentNode[1] + 1; j++){
                if(
                        (currentNode[0] != i || currentNode[1] != j)
                                &&
                                ( i >= 0 && j >= 0 )
                                &&
                                (i < values[1].length)
                                &&
                                (j < values[0].length )
                ){
                    if(this.values[i][j] == Definitions.EMPTY){
                        neighbors.add(new int[]{i,j});
                    }
                }
            }
        }
        return neighbors;
    }
}