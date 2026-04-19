import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SnakeGame extends GameEngine{
    public static void main(String[] args) {
        createGame(new SnakeGame());
    }

    //准备图片的变量
    Image imgHead;
    Image imgBody;
    Image imgApple;
    Image imgPoison;
    Image imgHeart;


    //生命值
    int healthPoints = 4;

    //游戏菜单选择 true菜单模式 false游戏模式
    boolean gameMenu = true;
    //菜单选择 0: “开始” 1: “帮助” 2: “退出”

    //HELP页面的开关
    boolean showHelp = false; // 帮助页面开关

    //判断游戏结束
    boolean isGameOver = false;

    //========================画网格=========================
    public void drawGrid(){
        changeColor(64,64,64);
        for(int i = 0 ; i <= 500 ;i = i+20){
            drawLine(i, 20, i, 500);
        }
        for(int j = 0 ; j <= 500 ;j = j+20){
            drawLine(0, j, 500, j);
        }

    }

    //=========================贪吃蛇========================
    //准备贪吃蛇的身体 ArrayList
    //ArrayList<Point>存的是（x,y）
    ArrayList<Point> snakeBody = new ArrayList<>();

    //贪吃蛇初始长度 身体长度为1
    int snakeLength = 3;
    int direction = 3; // 0:上 1:下 2:左 3:右
    //添加时间控制
    //update(double dt) 是每秒钟跑 60 次, 蛇 1 秒移动 60 格
    double timer = 0.0;


    //画蛇
    public void drawSnake(){
        for (int i = 0; i < snakeBody.size(); i++) {
            Point p = snakeBody.get(i);
            if (i == 0) {
                drawImage(imgHead, p.x , p.y ,19,19);
            }else{
                drawImage(imgBody, p.x , p.y ,19 ,19);
            }
        }
    }

    //更新蛇的位置
    public void update(double dt){
        if(isGameOver) return;
        timer += dt;
        if(timer >= 0.25){
            //贪吃蛇前移逻辑，从后往前数，头在[0],尾巴在[i-1]
            for(int i = snakeBody.size()-1; i > 0; i--){
                snakeBody.get(i).x = snakeBody.get(i-1).x;
                snakeBody.get(i).y = snakeBody.get(i-1).y;
            }
            //更新蛇头的位置
            Point head = snakeBody.getFirst();
            // 0:上 1:下 2:左 3:右
            switch(direction){
                case 0: head.y = head.y - 20;break;
                case 1: head.y = head.y + 20;break;
                case 2: head.x = head.x - 20;break;
                case 3: head.x = head.x + 20;break;
            }

            //碰撞检测
            if (head.x < 0 || head.x >= 500 || head.y < 20 || head.y >= 500) {
                isGameOver = true;
            }

            //碰撞自己身体检测
            for(int i = snakeBody.size()-1; i > 0; i--){
                if(head.x == snakeBody.get(i).x && head.y == snakeBody.get(i).y){
                    isGameOver = true;
                    break;
                }
            }

            //吃到苹果
            if(head.x == applePositionX && head.y == applePositionY ){
                //从数组最后新加一个，相当于新加一个尾巴，复制原来尾巴的位置
                snakeBody.add(new Point(snakeBody.getLast()));
                snakeLength ++ ;
                randomApple();
            }

            //吃到毒苹果
            if(head.x == poisonX && head.y == poisonY){
                healthPoints -= 1;
                randomPoison();
            }
            //当生命值将为0 游戏结束
            if(healthPoints <= 0){
                isGameOver = true;
            }
            timer = 0;
        }
    }


    //=====================================苹果=========================================
    int applePositionX, applePositionY;

    //苹果的随机位置
    public void randomApple(){
        boolean isOnItem;
        do {
            applePositionX = rand(25) * 20;
            applePositionY = (rand(23) + 2) * 20;
            isOnItem = false;
            for(Point p : snakeBody){
                if(p.x == applePositionX && p.y == applePositionY){
                    isOnItem = true;
                    break;
                }
            }
        }while(isOnItem);
    }

    //画苹果
    public void drawApple(){
            drawImage(imgApple, applePositionX, applePositionY,20,20);
    }


    /// 毒苹果///
    int poisonX, poisonY;
    public void randomPoison(){
        boolean isOnItem;
        do {
            poisonX = rand(25) * 20;
            poisonY = (rand(23) + 2) * 20;
            isOnItem = false;
            for(Point p : snakeBody){
                if(p.x == poisonX && p.y == poisonY){
                    isOnItem = true;
                    break;
                }
            }

            if(poisonX == applePositionX && poisonY == applePositionY){
                isOnItem = true;
            }

        }while(isOnItem);
    }

    //画毒苹果
    public void drawPoison(){
        drawImage(imgPoison, poisonX, poisonY,25,25);
    }



    public void paintComponent() {
        //设置背景
        changeColor(black);
        drawSolidRectangle(0, 0, 500, 500);

        if (showHelp) {
            changeColor(white);
            drawBoldText(100, 150, "HELP", "Helvetica", 36);       // 大号标题
            drawText(80, 210, "Use UP/DOWN/LEFT/RIGHT to move", "Times", 20);
            drawText(80, 250, "Eat apples to grow longer", "Times", 20);
            drawText(80, 290, "Don't hit walls or your body", "Times", 20);
            drawText(80, 380, "Press SPACE back to MENU", "Times", 22);
            return;
        }

        if (gameMenu) {
            changeColor(white);
            drawBoldText(140, 200, "MENU", "Helvetica", 40);
            drawText(120, 250, "1 - Start Game", "Times", 24);
            drawText(120, 300, "2 - Help", "Times", 24);
            drawText(120, 350, "3 - Exit Game", "Times", 24);// 菜单大标题
        } else {
            if (!isGameOver) {
                //画网格
                drawGrid();
                //Paint the Snake body
                drawSnake();
                //Paint the apple
                drawApple();
                //Paint the poison
                drawPoison();
                //画得分
                changeColor(white);
                drawText(10, 17, "Length of body: " + snakeLength, "Consolas",12);
                //画生命值
                changeColor(white);
                drawText(340, 17, "Health points: ", "Consolas",12);
                for(int i = 0; i < healthPoints; i++){
                    drawImage(imgHeart,(420 + i *20),0,23,23);
                }
            } else {
                changeColor(255, 80, 80);
                drawBoldText(140, 250, "GAME OVER");

                changeColor(200, 200, 200);
                drawText(120, 300, "Press SPACE to return to menu", "Consolas",20);

            }
        }
    }



    //构造函数：程序启动就执行这里
    public void init(){
        imgApple = loadImage("out/production/resources/apple.png");
        imgBody = loadImage("out/production/resources/dot.png");
        imgHead = loadImage("out/production/resources/head.png");
        imgPoison = loadImage("out/production/resources/apple_eaten.png");
        imgHeart = loadImage("out/production/resources/heart.png");
        // 初始化蛇：起始长度为 3 [cite: 8]
        // 假设每个格子大小是 20 像素
        snakeBody.clear();
        snakeBody.add(new Point(100, 100));
        snakeBody.add(new Point(80,100));
        snakeBody.add(new Point(60,100));

        healthPoints = 4;

        randomApple();
        randomPoison();
    }


    //键盘设置
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();

        if (showHelp) {
            if (key == KeyEvent.VK_SPACE) {
                showHelp = false;
            }
            return;
        }
            if(gameMenu){
            if(key == KeyEvent.VK_1){
                gameMenu = false;
                isGameOver = false;
                init();
                timer = 0;
                direction = 3;
            }
            if(key == KeyEvent.VK_2){
                showHelp = true;
            }
            if(key == KeyEvent.VK_3){
                //退出游戏
                System.exit(0);
            }
            return;
        }

        if(isGameOver && key == KeyEvent.VK_SPACE){
            gameMenu = true;
            isGameOver = false;
            return;
        }

        if(!isGameOver){
        // 0:上 1:下 2:左 3:右
        //向上走
        if(key == KeyEvent.VK_UP && direction != 1){
            direction = 0;
        }
        //向下走
        if(key == KeyEvent.VK_DOWN && direction != 0){
            direction = 1;
        }
        //向左走
        if(key == KeyEvent.VK_LEFT && direction != 3){
            direction = 2;
        }
        //向右走
        if(key == KeyEvent.VK_RIGHT && direction != 2) {
            direction = 3;
        }
        }
    }

}
