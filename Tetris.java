import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Tetris extends JFrame{
    private static KeyboardPanel keyboardPanel = new KeyboardPanel();

    public Tetris() throws InterruptedException {
        keyboardPanel.setBackground(Color.ORANGE);
        //keyboardPanel.setVisible(true);
        keyboardPanel.setFocusable(true);
    }

    public static void main(String[] args) throws InterruptedException {
        Tetris frame = new Tetris();
        frame.setTitle( "Tetris");
        frame.setSize(450,540);
        frame.setBackground(Color.ORANGE);
        frame.setLocationRelativeTo( null);
        frame.setDefaultCloseOperation(JFrame. EXIT_ON_CLOSE);
        frame.setVisible( true);
        frame.add(keyboardPanel);
        keyboardPanel.start();
    }

    static class KeyboardPanel extends JPanel{
        private int squareSize = 20,baseX = 0,baseY = 0;
        private static int x = 15,y = 25;
        private static int empty = 0,map = 1,wall = 2,square = 3;
        private static int arr[][] = new int[x][y];
        private boolean flag = false;
        private Block block = null;
        private static int speed = 500,tempSpeed = 100,standardSpeed = 500,beforeSpeed = standardSpeed;
        private static int reduceNum = 1,reduceLines = 1;
        public static int score = 0;
        private static boolean needRepaint = true;

        static {
            for (int i=0;i<x;i++){
                arr[i][y-1] = wall;
            }
            for (int i=0;i<y;i++){
                arr[0][i] = wall;
                arr[x-1][i] = wall;
            }
        }

        public KeyboardPanel(){
            this.addKeyListener(keyAdapter);
        }

        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Font font = new Font(Font.SERIF,Font.BOLD,30);
            g.setFont(font);
            paintMap(g);
        }

        void start() throws InterruptedException {
            if (block == null){
                block = new Block();
            }
            repaint();
            while (!flag){
                block.autoDown();
                Thread.sleep(speed);
                repaint();
            }
        }

        KeyAdapter keyAdapter = new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_DOWN: block.fastDown(); break;
                    case KeyEvent.VK_UP: block.rotate(); break;
                    case KeyEvent.VK_LEFT: block.moveLeft(); break;
                    case KeyEvent.VK_RIGHT: block.moveRight(); break;
                    case KeyEvent.VK_Z: block.speedUp(); break;
                    case KeyEvent.VK_X: block.speedDown(); break;
                    case KeyEvent.VK_P: block.pause(); break;
                    default:
                }
                if (needRepaint) repaint();
            }

            public void keyReleased(KeyEvent e){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_DOWN: speed = standardSpeed; break;
                    case KeyEvent.VK_P: block.resume(); break;
                    default:
                }
                repaint();
            }
        };

        int reduce(){
            int emptyNum = 0;int base = 0;
            for (int i=y-2;i>1;i--){
                emptyNum = 0;
                for (int j=0;j<x;j++){
                    if (arr[j][i] == empty){
                        emptyNum++;
                    }
                    if (emptyNum>reduceNum) break;
                }
                if (emptyNum>reduceNum) continue;
                for (int j=0;j<x;j++){
                    if (arr[j][i-1] == empty){
                        emptyNum++;
                    }
                    if (emptyNum>reduceNum) break;
                }
                if (emptyNum>reduceNum) continue;
                for (int j=0;j<x;j++){
                    if (arr[j][i-2] == empty){
                        emptyNum++;
                    }
                    if (emptyNum>reduceNum) break;
                }
                base = i;
                if (emptyNum<=reduceNum || emptyNum == x) break;
            }
            if (emptyNum<=reduceNum){
                for (int i=base-3;i>0;i--){
                    for (int j=0;j<x;j++){
                        arr[j][i+3] = arr[j][i];
                    }
                }
                score += 10-emptyNum;
            }
            return emptyNum;
        }

        private void paintMap(Graphics g){
            for (int i=0;i<y;i++){
                for (int j=0;j<x;j++){
                    switch (arr[j][i]){
                        case 0:
                            g.setColor(Color.WHITE);
                            break;
                        case 1:
                            g.setColor(Color.ORANGE);
                            break;
                        case 2:
                            g.setColor(Color.BLACK);
                            break;
                        case 3:
                            g.setColor(Color.BLACK.YELLOW);
                            break;
                        default:
                    }
                    g.fillRect(j*squareSize,i*squareSize,squareSize,squareSize);
                }
            }
            g.drawString("score:"+score,310,100);
        }

        private static void print(){
            for (int i=0;i<y;i++){
                for (int j=0;j<x;j++){
                    System.out.print(arr[j][i]+" ");
                }
                System.out.println();
            }
            System.out.println();
        }

        protected class Block{
            private byte type; private byte status = 1;
            private int x1=4,y1=2, x2=5,y2=2, x3=4,y3=y1+1, x4=5,y4=1;

            Block(){
                if(arr[x1][y1+1] == map||arr[x2][y2+1] == map||arr[x3][y3+1] == map||arr[x4][y4+1] == map
                        ||arr[x1][y1+1] == wall||arr[x2][y2+1] == wall||arr[x3][y3+1] == wall||arr[x4][y4+1] == wall){
                    flag = true;
                }else {
                    this.genRandomBlock();
                    arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = square;
                }
            }

            void genRandomBlock(){
                type = (byte) (Math.random()*4);
                int offset = 2;
                switch (type){
                    case 1:
                        x1=4;y1=0; x2=4;y2=1; x3=5;y3=1; x4=5;y4=2;
                        break;
                    case 2:
                        x1=3;y1=0; x2=4;y2=0; x3=5;y3=0; x4=6;y4=0;
                        break;
                    case 3:
                        x1=4;y1=0; x2=4;y2=1; x3=4;y3=2; x4=5;y4=2;
                        break;
                    default:
                        x1=4;y1=0; x2=5;y2=0; x3=4;y3=1; x4=5;y4=1;
                }
                y1 = y1+offset; y2 = y2+offset; y3 = y3+offset; y4 = y4+offset;
            }

            void moveLeft(){
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = empty;
                if(arr[x1-1][y1] == wall||arr[x2-1][y2] == wall||arr[x3-1][y3] == wall||arr[x4-1][y4] == wall
                        ||arr[x1-1][y1] == map||arr[x2-1][y2] == map||arr[x3-1][y3] == map||arr[x4-1][y4] == map){
                    needRepaint = false;
                    return;
                }
                --x1;--x2; --x3;--x4;
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = square;
            }

            void moveRight(){
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = empty;
                if(arr[x1+1][y1] == wall||arr[x2+1][y2] == wall||arr[x3+1][y3] == wall||arr[x4+1][y4] == wall
                        ||arr[x1+1][y1] == map||arr[x2+1][y2] == map||arr[x3+1][y3] == map||arr[x4+1][y4] == map){
                    needRepaint = false;
                    return;
                }
                ++x1;++x2; ++x3;++x4;
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = square;
            }

            void speedUp(){
                needRepaint = true;
                speed = speed<=100?100:speed-100;
            }

            void speedDown(){
                needRepaint = true;
                speed = speed>=1000?1000:speed+100;
            }

            void fastDown(){
                needRepaint = true;
                tempSpeed = speed;
                speed = 100;
            }

            void pause(){
                speed = 999999999;
            }

            void resume(){
                speed = standardSpeed;
            }

            void rotate(){
                needRepaint = true;
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = empty;
                switch (type){
                    case 1:  //Z
                        if (status == 1){
                            x1=x2+1;y1=y2; x2=x2;y2=y2; x3=x2;y3=y2+1; x4=x2-1;y4=y3;
                            this.status = 2;
                        }else if (status == 2){
                            x1=x2;y1=y2+1; x2=x2;y2=y2; x3=x2-1;y3=y2; x4=x3;y4=y3-1;
                            this.status = 3;
                        }else if (status == 3){
                            x1=x2-1;y1=y2; x2=x2;y2=y2; x3=x2;y3=y2-1; x4=x2+1;y4=y2-1;
                            this.status = 4;
                        }else {
                            x1=x2;y1=y2-1; x2=x2;y2=y2; x3=x2+1;y3=y2; x4=x2+1;y4=y2+1;
                            this.status = 1;
                        }
                        break;
                    case 2: //一
                        if (status == 1){
                            x1=x3;y1=y3-2; x2=x1;y2=y1+1; x3=x1;y3=y1+2; x4=x1;y4=y1+3;
                            this.status = 2;
                        }else {
                            x1=x3-2;y1=y3; x2=x1+1;y2=y1; x3=x1+2;y3=y1; x4=x1+3;y4=y1;
                            this.status = 1;
                        }
                        break;
                    case 3:  //L
                        if (status == 1){
                            x1=x4;y1=y2; x2=x2;y2=y2; x3=x1-2;y3=y1; x4=x3;y4=y3+1;
                            this.status = 2;
                        }else if (status == 2){
                            x1=x2;y1=y2+1; x2=x2;y2=y2; x3=x2;y3=y2-1; x4=x3-1;y4=y3;
                            this.status = 3;
                        }else if (status == 3){
                            x1=x2-1;y1=y2; x2=x2;y2=y2; x3=x2+1;y3=y2; x4=x3;y4=y3-1;
                            this.status = 4;
                        }else {
                            x1=x2;y1=y2-1; x2=x2;y2=y2; x3=x2;y3=y2+1; x4=x2+1;y4=y3;
                            this.status = 1;
                        }
                        break;
                    default:
                }
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = square;
            }

            void autoDown(){
                if (flag){
                    return;
                }
                if(arr[x1][y1+1] == map||arr[x2][y2+1] == map||arr[x3][y3+1] == map||arr[x4][y4+1] == map
                        ||arr[x1][y1+1] == wall||arr[x2][y2+1] == wall||arr[x3][y3+1] == wall||arr[x4][y4+1] == wall){
                    this.addToMap();
                    block = new Block();
                    return;
                }else {
                    arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = empty;
                    ++y1;++y2; ++y3;++y4;
                }
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = square;
            }

            void addToMap(){
                arr[x1][y1] = arr[x2][y2] = arr[x3][y3] = arr[x4][y4] = map;
                reduce();
            }
        }
    }
}
