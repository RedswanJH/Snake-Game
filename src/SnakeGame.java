import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SnakeGame extends GameEngine {
    public static void main(String[] args) {
        createGame(new SnakeGame());
    }

    // Variables for preparing images
    Image imgHead;
    Image imgBody;
    Image imgApple;
    Image imgPoison;
    Image imgHeart;
    Image imgHead2;


    // Game menu control
    boolean gameMenu = true;
    boolean showHelp = false;
    boolean isGameOver = false;

    //choosing PVP (two player) mode
    boolean isTwoPlayer = false;

    // ======================== Draw the game grid =========================
    public void drawGrid() {
        changeColor(64, 64, 64);
        for (int i = 0; i <= 500; i = i + 20) {
            drawLine(i, 20, i, 500);
        }
        for (int j = 0; j <= 500; j = j + 20) {
            drawLine(0, j, 500, j);
        }
    }

    // ========================= Player 1  ========================
    ArrayList<Point> snakeBody = new ArrayList<>();
    int snakeLength1 = 3;
    // health points for P1
    int healthPoints1 = 4;
    int direction = 3;

    // ========================= Player 2 ========================
    ArrayList<Point> snakeBody2 = new ArrayList<>();
    int direction2 = 2; // Default to the left
    int snakeLength2 = 3;
    int healthPoints2 = 4;
    double timer = 0.0;

    // Update logic
    public void update(double dt) {
        if (isGameOver || gameMenu) return;
        timer += dt;
        if (timer >= 0.25) {
            // move snake ： Encapsulate the original logic
            moveSnake(snakeBody, direction);

            // if choosing PVP mode, move P2
            if (isTwoPlayer) {
                moveSnake(snakeBody2, direction2);
            }

            // Collision detection
            Point head1 = snakeBody.getFirst();

            // The snake 1 bumps into the wall or hits itself.
            if (head1.x < 0 || head1.x >= 500 || head1.y < 20 || head1.y >= 500 || checkHitSelf(snakeBody)) {
                isGameOver = true;
            }

            if (isTwoPlayer) {
                Point head2 = snakeBody2.getFirst();
                //The snake 2 bumps into the wall or hits itself.
                if (head2.x < 0 || head2.x >= 500 || head2.y < 20 || head2.y >= 500 || checkHitSelf(snakeBody2)) {
                    isGameOver = true;
                }
                // Collision detection with each other
                if (checkHitOther(head1, snakeBody2) || checkHitOther(head2, snakeBody)) {
                    isGameOver = true;
                }
            }

            // logic for eating apple
            if (head1.x == applePositionX && head1.y == applePositionY) {
                if(snakeLength1 < 20) {
                    snakeBody.add(new Point(snakeBody.getLast()));
                }
                snakeLength1++;
                randomApple();
            }
            
            if (head1.x == poisonX && head1.y == poisonY) {
                healthPoints1 -= 1;
                randomPoison();
            }

            if (isTwoPlayer) {
                Point head2 = snakeBody2.getFirst();
                if (head2.x == applePositionX && head2.y == applePositionY) {
                    if (snakeLength1 < 20) {
                        snakeBody2.add(new Point(snakeBody2.getLast()));
                    }
                    snakeLength2++;
                    randomApple();
                }


                // logic for eating poison
                if (head1.x == poisonX && head1.y == poisonY) {
                    healthPoints1 -= 1;
                    randomPoison();
                }
                if (head2.x == poisonX && head2.y == poisonY) {
                    healthPoints2 -= 1;
                    randomPoison();
                }
            }

                if (healthPoints1 <= 0)
                    isGameOver = true;
                if (healthPoints2 <= 0)
                    isGameOver = true;

            
            timer = 0;
        }
    }

    // Movement logic
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

    // =====================================Logic for Item=========================================
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

    // ===================================== Parts for draw =========================================
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
                // draw snake 1
                for (int i = 0; i < snakeBody.size(); i++) {
                    drawImage(i == 0 ? imgHead : imgBody, snakeBody.get(i).x, snakeBody.get(i).y, 19, 19);
                }
                // draw snake 2
                if (isTwoPlayer) {
                    for (int i = 0; i < snakeBody2.size(); i++) {
                        // Snake 2: The body is the same, and the head can be blue.
                        drawImage(i == 0 ? imgHead2 : imgBody, snakeBody2.get(i).x, snakeBody2.get(i).y, 19, 19);
                    }
                }
                drawImage(imgApple, applePositionX, applePositionY, 20, 20);
                drawImage(imgPoison, poisonX, poisonY, 25, 25);

                // UI
                //PVP
                if(isTwoPlayer) {
                    changeColor(white);
                    drawText(10, 17, "P1: " + snakeLength1, "Consolas", 12);
                    drawText(50, 17, "P2: " + snakeLength2, "Consolas", 12);
                    drawText(250, 17, "P1 Health: " + healthPoints1, "Consolas", 12);
                    drawText(350, 17, "P2 Health: " + healthPoints2, "Consolas", 12);
                    //one player
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
    void drawMenuScreen() {
        changeColor(white);
        drawBoldText(140, 180, "SNAKE GAME", "Helvetica", 40);
        drawText(120, 250, "1 - Single Player", "Times", 24);
        drawText(120, 290, "2 - Two Players", "Times", 24);
        drawText(120, 330, "3 - Help", "Times", 24);
        drawText(120, 370, "4 - Exit", "Times", 24);
    }

    void drawHelpScreen() {
        // 1. 背景：深灰色渐变感
        changeColor(30, 30, 30);
        drawSolidRectangle(0, 0, 500, 500);

        // 2. 标题
        changeColor(255, 215, 0); // 金色
        drawBoldText(160, 60, "HOW TO PLAY", "Helvetica", 32);
        drawLine(150, 75, 350, 75);

        // 3. 玩家操作区 (Player Controls)
        // P1
        changeColor(50, 255, 50); // 亮绿色
        drawBoldText(60, 120, "Player 1 (Single/PVP):", "Arial", 18);
        changeColor(white);
        drawText(80, 150, "Movement:  [ Arrow Keys ]", "Consolas", 16);

        // P2
        changeColor(50, 200, 255); // 亮蓝色
        drawBoldText(60, 190, "Player 2 (PVP Mode Only):", "Arial", 18);
        changeColor(white);
        drawText(80, 220, "Movement:  [ W, A, S, D ]", "Consolas", 16);

        // 4. 游戏规则 (Game Rules)
        changeColor(255, 255, 100); // 浅黄色
        drawBoldText(60, 270, "Rules:", "Arial", 18);
        changeColor(220, 220, 220);
        drawText(80, 300, "* Eat Apples to grow and score points.", "Arial", 14);
        drawText(80, 325, "* Avoid hitting walls or your own body.", "Arial", 14);
        drawText(80, 350, "* In PVP: Don't collide with the other snake.", "Arial", 14);
        drawText(80, 375, "* Max snake length is 20 segments.", "Arial", 14);

        // 5. 物品说明 (Item Legends)
        changeColor(255, 100, 100); // 亮红色
        drawBoldText(60, 420, "Items:", "Arial", 18);

        // 小图标演示 (提示玩家哪个是好哪个是坏)
        drawImage(imgApple, 140, 405, 20, 20);
        changeColor(white);
        drawText(170, 420, "Apple (+Length)", "Arial", 14);

        drawImage(imgPoison, 290, 405, 23, 23);
        changeColor(white);
        drawText(320, 420, "Poison (-1 Health)", "Arial", 14);

        // 6. 返回提示
        changeColor(200, 200, 200);
        drawBoldText(110, 480, "Press [ SPACE ] to return to Menu", "Helvetica", 18);
    }

    public void init() {
        imgApple = loadImage("resources/apple.png");
        imgBody = loadImage("resources/dot.png");
        imgHead = loadImage("resources/head.png");
        imgPoison = loadImage("resources/apple_eaten.png");
        imgHeart = loadImage("resources/heart.png");
        imgHead2 = loadImage("resources/blue_dot.png"); // 也可以换个别的图

        // Initialization
        snakeBody.clear();
        snakeBody.add(new Point(100, 100));
        snakeBody.add(new Point(80, 100));
        snakeBody.add(new Point(60, 100));
        direction = 3;

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
            // P1 Arrow Keys
            if (key == KeyEvent.VK_UP && direction != 1)
                direction = 0;
            if (key == KeyEvent.VK_DOWN && direction != 0)
                direction = 1;
            if (key == KeyEvent.VK_LEFT && direction != 3)
                direction = 2;
            if (key == KeyEvent.VK_RIGHT && direction != 2)
                direction = 3;

            // P2 WASD
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