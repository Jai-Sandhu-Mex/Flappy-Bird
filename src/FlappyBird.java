import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;


import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener,  KeyListener {

    private static final FlappyBird.Pipe Pipe = null;
    int boardwidth = 360;
    int boardheight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
     
    //
    int birdx= boardwidth/8;
    int birdy= boardheight/2;
    int birdY = birdy; 
    int birdwidth=34;
    int birdheight=24;
    
    class Bird{
        int x= birdx;
        int y= birdy;
        int width= birdwidth;
        int height = birdheight;
        Image img;
        Bird(Image img){
            this.img=img;
        }
    }

    int pipeX= boardwidth;
    int pipeY=0;
    int pipeWidth=64;
    int pipeHeight=512;
    
    class Pipe{
        int x= pipeX;
        int y = pipeY;
        int height=pipeHeight;
        int width= pipeWidth;
        Image img;
        Boolean passed= false;
        
 
        Pipe(Image img){
         this.img=img;
        }
     }


    Bird bird;
    int velocityX=-4;
    int velocityY=0;
    int gravity=1;

    ArrayList<Pipe>pipes;
    Random random = new Random();


    Timer gameloop;
    Timer PlacePipesTimer;
    boolean gameover=false;
    double score=0;
     
    FlappyBird() {
        setPreferredSize(new Dimension(boardwidth, boardheight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird=new Bird(birdImg);
        pipes= new ArrayList<Pipe>();

        PlacePipesTimer= new Timer(1500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        PlacePipes();
        
    } 
        });
        PlacePipesTimer.start();
        

        gameloop=new Timer(1000/60,this);
        gameloop.start();

    }

    public void PlacePipes(){

          int randomPipeY= (int) (pipeY-pipeHeight/4 - Math.random()*(pipeHeight/2));
          int openingSpace=boardheight/4;
          Pipe topPipe = new Pipe(topPipeImg);
          topPipe.y= randomPipeY;
          pipes.add(topPipe); 

          Pipe bottomPipe = new Pipe(bottomPipeImg);
          bottomPipe.y=topPipe.y+ pipeHeight+ openingSpace;
          pipes.add(bottomPipe);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        System.out.println("draw");
        g.drawImage(backgroundImg, 0, 0, boardwidth, boardheight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
    
        for(int i=0; i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y , pipe.width, pipe.height, null);  // Corrected dimensions
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if (gameover) {
            // Draw "game over" along with the score
            g.drawString("game over: " + (int) score, 10, 35);
        } else {
            // Draw just the score when the game is ongoing
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // Apply gravity and move the bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);  // Prevent bird from going off-screen at the top
    
        // Move all pipes to the left
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x>pipe.x+pipe.width){
                pipe.passed=true;
                score+=0.5;
            }
            // Check for collision with each pipe
            if (collision(bird, pipe)) {
                gameover = true;
                break;  // Stop checking if a collision is detected
            }
        }
        // Check if bird falls below the screen (bottom out of bounds)
        if (bird.y > boardheight) {
            gameover = true;
        }
    }

    public boolean collision(Bird a , Pipe b){
    return a.x<b.x+b.width &&
           a.x+a.width > b.x &&
           a.y < b.y + b.height &&
           a.y + a.height > b.y;
}
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameover){
            PlacePipesTimer.stop();
            gameloop.stop();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
      
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            velocityY=-9;

            if (gameover) {
                bird.y = birdY;  
                velocityY = 0;    
                pipes.clear();    
                score = 0;       
                gameover = false;
                gameloop.start();         
                PlacePipesTimer.start();  
                System.out.println("Game Restarted!");
            }
        }
    }
}
