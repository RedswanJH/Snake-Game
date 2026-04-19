import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SnakeGamePVP extends GameEngine {
    public static void main(String[] args) {
        createGame(new SnakeGamePVP());
    }

    // 图片变量
    Image imgHead1, imgHead2, imgBody1, imgBody2, imgApple;

    // 游戏状态
    boolean isGameOver = false;
    String winner = "";

    // 蛇1 (方向键控制)
    ArrayList<Point> snake1 = new ArrayList<>();
    int dir1 = 3; // 0:上 1:下 2:左 3:右

    // 蛇2 (WASD控制)
    ArrayList<Point> snake2 = new ArrayList<>();
    int dir2 = 2; // 默认向左，防止开局对撞

    double timer = 0.0;
    int appleX, appleY;

    // 初始化：设置两条蛇的起始位置
    public void init() {
        // 加载图片 (你可以沿用之前的图片，或者为蛇2换个颜色)
        imgApple = loadImage("out/production/resources/apple.png");
        imgHead1 = loadImage("out/production/resources/head.png");
        imgBody1 = loadImage("out/production/resources/dot.png");
        // 如果有不同颜色的图可以换上，没有就先用一样的
        imgHead2 = imgHead1;
        imgBody2 = imgBody1;

        // 蛇1：左侧出发
        snake1.clear();
        snake1.add(new Point(100, 200));
        snake1.add(new Point(80, 200));
        snake1.add(new Point(60, 200));
        dir1 = 3;

        // 蛇2：右侧出发
        snake2.clear();
        snake2.add(new Point(400, 300));
        snake2.add(new Point(420, 300));
        snake2.add(new Point(440, 300));
        dir2 = 2;

        isGameOver = false;
        randomApple();
    }

    public void update(double dt) {
        if (isGameOver) return;
        timer += dt;

        if (timer >= 0.20) { // 双人模式稍微快一点更好玩
            // 1. 移动身体
            moveBody(snake1);
            moveBody(snake2);

            // 2. 移动头部
            moveHead(snake1, dir1);
            moveHead(snake2, dir2);

            // 3. 碰撞检测
            checkCollisions();

            // 4. 吃苹果检测
            checkEat(snake1);
            checkEat(snake2);

            timer = 0;
        }
    }

    // 抽取的通用逻辑：身体跟着头走
    void moveBody(ArrayList<Point> snake) {
        for (int i = snake.size() - 1; i > 0; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }
    }

    // 抽取的通用逻辑：头按方向走
    void moveHead(ArrayList<Point> snake, int dir) {
        Point head = snake.get(0);
        if (dir == 0) head.y -= 20;
        if (dir == 1) head.y += 20;
        if (dir == 2) head.x -= 20;
        if (dir == 3) head.x += 20;
    }

    void checkCollisions() {
        Point h1 = snake1.get(0);
        Point h2 = snake2.get(0);

        // 蛇1的死亡判定 (撞墙、撞自己、撞蛇2身体)
        if (h1.x < 0 || h1.x >= 500 || h1.y < 0 || h1.y >= 500 || hitSelf(snake1) || hitOther(h1, snake2)) {
            isGameOver = true;
            winner = "Player 2 (WASD) Wins!";
        }

        // 蛇2的死亡判定 (撞墙、撞自己、撞蛇1身体)
        if (h2.x < 0 || h2.x >= 500 || h2.y < 0 || h2.y >= 500 || hitSelf(snake2) || hitOther(h2, snake1)) {
            isGameOver = true;
            winner = "Player 1 (Arrows) Wins!";
        }
    }

    boolean hitSelf(ArrayList<Point> snake) {
        Point head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (head.x == snake.get(i).x && head.y == snake.get(i).y) return true;
        }
        return false;
    }

    boolean hitOther(Point head, ArrayList<Point> otherSnake) {
        for (Point p : otherSnake) {
            if (head.x == p.x && head.y == p.y) return true;
        }
        return false;
    }

    void checkEat(ArrayList<Point> snake) {
        Point head = snake.get(0);
        if (head.x == appleX && head.y == appleY) {
            snake.add(new Point(snake.get(snake.size() - 1)));
            randomApple();
        }
    }

    public void randomApple() {
        appleX = rand(25) * 20;
        appleY = rand(25) * 20;
    }

    public void paintComponent() {
        changeColor(black);
        drawSolidRectangle(0, 0, 500, 500);

        if (!isGameOver) {
            // 画蛇1
            for (int i = 0; i < snake1.size(); i++) {
                drawImage(i == 0 ? imgHead1 : imgBody1, snake1.get(i).x, snake1.get(i).y, 19, 19);
            }
            // 画蛇2
            for (int i = 0; i < snake2.size(); i++) {
                drawImage(i == 0 ? imgHead2 : imgBody2, snake2.get(i).x, snake2.get(i).y, 19, 19);
            }
            // 画苹果
            drawImage(imgApple, appleX, appleY, 20, 20);
        } else {
            changeColor(white);
            drawBoldText(120, 250, winner);
            drawText(150, 300, "Press SPACE to Restart");
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // 蛇1控制：方向键
        if (key == KeyEvent.VK_UP && dir1 != 1) dir1 = 0;
        if (key == KeyEvent.VK_DOWN && dir1 != 0) dir1 = 1;
        if (key == KeyEvent.VK_LEFT && dir1 != 3) dir1 = 2;
        if (key == KeyEvent.VK_RIGHT && dir1 != 2) dir1 = 3;

        // 蛇2控制：WASD
        if (key == KeyEvent.VK_W && dir2 != 1) dir2 = 0;
        if (key == KeyEvent.VK_S && dir2 != 0) dir2 = 1;
        if (key == KeyEvent.VK_A && dir2 != 3) dir2 = 2;
        if (key == KeyEvent.VK_D && dir2 != 2) dir2 = 3;

        if (isGameOver && key == KeyEvent.VK_SPACE) {
            init();
        }
    }
}