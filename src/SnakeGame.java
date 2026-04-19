import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SnakeGame extends GameEngine {
    public static void main(String[] args) {
        createGame(new SnakeGame());
    }

    // 准备图片的变量
    Image imgHead;
    Image imgBody;
    Image imgApple;
    Image imgPoison;
    Image imgHeart;
    Image imgHead2;


    // 游戏菜单控制
    boolean gameMenu = true;
    boolean showHelp = false;
    boolean isGameOver = false;

    //双人模式开关
    boolean isTwoPlayer = false;

    // ======================== 画网格 =========================
    public void drawGrid() {
        changeColor(64, 64, 64);
        for (int i = 0; i <= 500; i = i + 20) {
            drawLine(i, 20, i, 500);
        }
        for (int j = 0; j <= 500; j = j + 20) {
            drawLine(0, j, 500, j);
        }
    }

    // ========================= 贪吃蛇 1  ========================
    ArrayList<Point> snakeBody = new ArrayList<>();
    int snakeLength1 = 3;
    // 生命值
    int healthPoints1 = 4;
    int direction = 3;

    // ========================= 贪吃蛇 2 ========================
    ArrayList<Point> snakeBody2 = new ArrayList<>();
    int direction2 = 2; // 默认向左，避免撞车
    int snakeLength2 = 3;
    int healthPoints2 = 4;
    double timer = 0.0;

    // 更新逻辑
    public void update(double dt) {
        if (isGameOver || gameMenu) return;
        timer += dt;
        if (timer >= 0.25) {
            // 1. 移动蛇 1 (你的原始逻辑封装)
            moveSnake(snakeBody, direction);

            // 2. 如果是双人模式，移动蛇 2
            if (isTwoPlayer) {
                moveSnake(snakeBody2, direction2);
            }

            // --- 碰撞检测区 ---
            Point head1 = snakeBody.getFirst();
            Point head2 = snakeBody2.getFirst();
            // 蛇 1 撞墙或撞自己
            if (head1.x < 0 || head1.x >= 500 || head1.y < 20 || head1.y >= 500 || checkHitSelf(snakeBody)) {
                isGameOver = true;
            }

            if (isTwoPlayer) {
                // 蛇 2 撞墙或撞自己
                if (head2.x < 0 || head2.x >= 500 || head2.y < 20 || head2.y >= 500 || checkHitSelf(snakeBody2)) {
                    isGameOver = true;
                }
                // 互相碰撞检测
                if (checkHitOther(head1, snakeBody2) || checkHitOther(head2, snakeBody)) {
                    isGameOver = true;
                }
            }

            // --- 吃苹果逻辑 ---
            if (head1.x == applePositionX && head1.y == applePositionY) {
                snakeBody.add(new Point(snakeBody.getLast()));
                snakeLength1++;
                randomApple();
            }
            if (isTwoPlayer) {
                if (head2.x == applePositionX && head2.y == applePositionY) {
                    snakeBody2.add(new Point(snakeBody2.getLast()));
                    randomApple();
                }
            }

            // --- 毒苹果逻辑 ---
            if (head1.x == poisonX && head1.y == poisonY) {
                healthPoints1 -= 1;
                randomPoison();
            }
            if (head2.x == poisonX && head2.y == poisonY) {
                healthPoints2 -= 1;
                randomPoison();
            }

            if (healthPoints1 <= 0)
                isGameOver = true;
            if (healthPoints2 <= 0)
                isGameOver = true;

            timer = 0;
        }
    }

    // 辅助方法：移动逻辑
    void moveSnake(ArrayList<Point> body, int dir) {
        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).x = body.get(i - 1).x;
            body.get(i).y = body.get(i - 1).y;
        }
        Point head = body.getFirst();
        switch (dir) {
            case 0: head.y -= 20; break;
            case 1: head.y += 20; break;
            case 2: head.x -= 20; break;
            case 3: head.x += 20; break;
        }
    }

    boolean checkHitSelf(ArrayList<Point> body) {
        Point head = body.getFirst();
        for (int i = 1; i < body.size(); i++) {
            if (head.x == body.get(i).x && head.y == body.get(i).y) return true;
        }
        return false;
    }

    boolean checkHitOther(Point head, ArrayList<Point> otherBody) {
        for (Point p : otherBody) {
            if (head.x == p.x && head.y == p.y) return true;
        }
        return false;
    }

    // =====================================物品逻辑 =========================================
    int applePositionX, applePositionY;
    public void randomApple() {
        boolean isOnItem;
        do {
            applePositionX = rand(25) * 20;
            applePositionY = (rand(23) + 2) * 20;
            isOnItem = false;
            for (Point p : snakeBody) {
                if (p.x == applePositionX && p.y == applePositionY) isOnItem = true;
            }
            if (isTwoPlayer) {
                for (Point p : snakeBody2) {
                    if (p.x == applePositionX && p.y == applePositionY) isOnItem = true;
                }
            }
        } while (isOnItem);
    }

    int poisonX, poisonY;
    public void randomPoison() {
        boolean isOnItem;
        do {
            poisonX = rand(25) * 20;
            poisonY = (rand(23) + 2) * 20;
            isOnItem = false;
            for (Point p : snakeBody) {
                if (p.x == poisonX && p.y == poisonY) isOnItem = true;
            }
            if (poisonX == applePositionX && poisonY == applePositionY) isOnItem = true;
        } while (isOnItem);
    }

    // ===================================== 绘画部分 =========================================
    public void paintComponent() {
        changeColor(black);
        drawSolidRectangle(0, 0, 500, 500);

        if (showHelp) {
            drawHelpScreen();
            return;
        }

        if (gameMenu) {
            drawMenuScreen();
        } else {
            if (!isGameOver) {
                drawGrid();
                // 画蛇 1
                for (int i = 0; i < snakeBody.size(); i++) {
                    drawImage(i == 0 ? imgHead : imgBody, snakeBody.get(i).x, snakeBody.get(i).y, 19, 19);
                }
                // 画蛇 2
                if (isTwoPlayer) {
                    for (int i = 0; i < snakeBody2.size(); i++) {
                        // 蛇 2 身体用一样的，头可以用个不一样的颜色
                        drawImage(i == 0 ? imgHead2 : imgBody, snakeBody2.get(i).x, snakeBody2.get(i).y, 19, 19);
                    }
                }
                drawImage(imgApple, applePositionX, applePositionY, 20, 20);
                drawImage(imgPoison, poisonX, poisonY, 25, 25);

                // UI 状态栏
                //两人游戏
                if(isTwoPlayer) {
                    changeColor(white);
                    drawText(10, 17, "P1: " + snakeLength1, "Consolas", 12);
                    drawText(50, 17, "P2: " + snakeLength2, "Consolas", 12);
                    drawText(250, 17, "P1 Health: " + healthPoints1, "Consolas", 12);
                    drawText(350, 17, "P2 Health: " + healthPoints2, "Consolas", 12);
                    //一人游戏
                }else {
                    changeColor(white);
                    drawText(10, 17, "P1: " + snakeLength1, "Consolas", 12);
                    drawText(340, 17, "Health points: ", "Consolas",12);
                    for(int i = 0; i < healthPoints1; i++){
                        drawImage(imgHeart,(420 + i *20),0,23,23);
                    }
                }
            } else {
                changeColor(255, 80, 80);
                drawBoldText(140, 250, "GAME OVER");
                changeColor(200, 200, 200);
                drawText(120, 300, "Press SPACE to return to menu", "Consolas",20);

            }
        }
    }
//drawText(340, 17, "Health points: ", "Consolas",12);
    void drawMenuScreen() {
        changeColor(white);
        drawBoldText(140, 180, "SNAKE GAME", "Helvetica", 40);
        drawText(120, 250, "1 - Single Player", "Times", 24);
        drawText(120, 290, "2 - Two Players", "Times", 24);
        drawText(120, 330, "3 - Help", "Times", 24);
        drawText(120, 370, "4 - Exit", "Times", 24);
    }

    void drawHelpScreen() {
        changeColor(white);
        drawBoldText(100, 150, "HELP", "Helvetica", 36);
        drawText(80, 210, "P1: Arrow Keys", "Times", 20);
        drawText(80, 240, "P2: W A S D", "Times", 20);
        drawText(80, 280, "Eat apples, avoid poison & walls", "Times", 20);
        drawText(80, 380, "Press SPACE back to MENU", "Times", 22);
    }

    public void init() {
        imgApple = loadImage("out/production/resources/apple.png");
        imgBody = loadImage("out/production/resources/dot.png");
        imgHead = loadImage("out/production/resources/head.png");
        imgPoison = loadImage("out/production/resources/apple_eaten.png");
        imgHeart = loadImage("out/production/resources/heart.png");
        imgHead2 = loadImage("out/production/resources/blue_dot.png"); // 也可以换个别的图

        // 初始化蛇 1
        snakeBody.clear();
        snakeBody.add(new Point(100, 100));
        snakeBody.add(new Point(80, 100));
        snakeBody.add(new Point(60, 100));
        direction = 3;

        // 初始化蛇 2
        if (isTwoPlayer) {
            snakeBody2.clear();
            snakeBody2.add(new Point(400, 400));
            snakeBody2.add(new Point(420, 400));
            snakeBody2.add(new Point(440, 400));
            direction2 = 2;
        }

        healthPoints1 = 4;
        healthPoints2 = 4;
        snakeLength1 = 3;
        snakeLength2 = 3;
        randomApple();
        randomPoison();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (showHelp && key == KeyEvent.VK_SPACE) {
            showHelp = false;
            return;
        }

        if (gameMenu) {
            if (key == KeyEvent.VK_1) {
                isTwoPlayer = false;
                gameMenu = false; init();
            }
            if (key == KeyEvent.VK_2) {
                isTwoPlayer = true;
                gameMenu = false; init();
            }
            if (key == KeyEvent.VK_3) {
                showHelp = true;
            }
            if (key == KeyEvent.VK_4) {
                System.exit(0);
            }
            return;
        }

        if (isGameOver && key == KeyEvent.VK_SPACE) {
            gameMenu = true;
            isGameOver = false;
            return;
        }

        if (!isGameOver) {
            // P1 控制 (Arrow Keys)
            if (key == KeyEvent.VK_UP && direction != 1)
                direction = 0;
            if (key == KeyEvent.VK_DOWN && direction != 0)
                direction = 1;
            if (key == KeyEvent.VK_LEFT && direction != 3)
                direction = 2;
            if (key == KeyEvent.VK_RIGHT && direction != 2)
                direction = 3;

            // P2 控制 (WASD)
            if (isTwoPlayer) {
                if (key == KeyEvent.VK_W && direction2 != 1)
                    direction2 = 0;
                if (key == KeyEvent.VK_S && direction2 != 0)
                    direction2 = 1;
                if (key == KeyEvent.VK_A && direction2 != 3)
                    direction2 = 2;
                if (key == KeyEvent.VK_D && direction2 != 2)
                    direction2 = 3;
            }
        }
    }
}