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

    //判断游戏结束
    boolean isGameOver = false;

    //============贪吃蛇===============
    //准备贪吃蛇的身体 ArrayList
    //ArrayList<Point>存的是（x,y）
    ArrayList<Point> snakeBody = new ArrayList<>();

    //贪吃蛇初始长度 身体长度为1
    int snakeLength = 3;
    int direction = 3; // 0:上 1:下 2:左 3:右

    //画蛇
    public void drawSnake(){
        for (int i = 0; i < snakeBody.size(); i++) {
            Point p = snakeBody.get(i);
            if (i == 0) {
                drawImage(imgHead, p.x , p.y ,20,20);
            }else{
                drawImage(imgBody, p.x , p.y ,20 ,20);
            }
        }
    }

    //更新蛇的位置
    public void update(double dt){

    }


    //================苹果=================
    int applePositionX, applePositionY;

    //苹果的随机位置
    public void randomApple(){
        boolean isOnSnake;
        do {
            applePositionX = rand(25) * 20;
            applePositionY = rand(25) * 20;
            isOnSnake = false;
            for(Point p : snakeBody){
                if(p.x == applePositionX && p.y == applePositionY){
                    isOnSnake = true;
                    break;
                }
            }
        }while(isOnSnake);
    }

    //画苹果
    public void drawApple(){
            drawImage(imgApple, applePositionX, applePositionY,20,20);
    }




    public void paintComponent() {
        //设置背景
        changeColor(black);
        drawSolidRectangle(0, 0, 500, 500);

        if(!isGameOver){
            //Paint the Snake body
            drawSnake();
            //Paint the apple
            drawApple();
        }else{
            changeColor(white);
            drawText(150,250,"GAME OVER");
        }
    }



    //构造函数：程序启动就执行这里
    public void init(){
        imgApple = loadImage("out/production/resources/apple.png");
        imgHead = loadImage("out/production/resources/dot.png");
        imgBody = loadImage("out/production/resources/head.png");
        // 初始化蛇：起始长度为 3 [cite: 8]
        // 假设每个格子大小是 20 像素
        snakeBody.clear();
        snakeBody.add(new Point(100, 100));
        snakeBody.add(new Point(80,100));
        snakeBody.add(new Point(60,100));

        randomApple();
    }


    //键盘设置
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
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
        if(key == KeyEvent.VK_RIGHT && direction != 2){
            direction = 3;
        }
    }













}
