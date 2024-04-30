import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.awt.event.*;
import javax.sound.sampled.*;

/**
 * Tetris - A single player game about stacking and clearing tetraminos
 * @author Eric
 * @version June 2022
 */
public class Tetris{
    //Graphics Variables
    public static JFrame gameWindow;
    JPanel panel;
    JButton playButton;
    JButton controls;
    JButton highScore;
    JLabel tetris;
    JLabel select;
    GraphicsPanel canvas;
    JLabel scoreDisplay;
    Font font = new Font("Monospaced", Font.PLAIN, 40);
    Font bigFont = new Font("Monospaced", Font.BOLD, 60);
    Font smallFont = new Font("Monospaced", Font.BOLD, 20);
    Font tetrisFont = new Font("Monospaced", Font.PLAIN, 60);
    Font buttonFont = new Font("Monospaced", Font.PLAIN, 20);
    DirectionKeyListener keyListener;
    
    //Game Variables
    public static int timerTime = Const.BASETIME;
    public static boolean hardDrop = false;
    public static int score = 0;
    public static boolean play = true;
    public static boolean button = true;
    public static int held = -1;
    public static boolean switched = false;
    public static boolean hold = false;
    public static boolean dead = false;
    public static int highScoreNum;
    public static String highScorer;
    public static int softDrop = 1;
    public static int[][][] heldOffset = new int[4][4][2];
    
    //Sound Variables
    Sound music = new Sound(".\\Sound\\am321.wav");
    Sound tap = new Sound(".\\Sound\\tap.wav");
    Sound shuffle = new Sound(".\\Sound\\shuffle.wav");
    Sound hit = new Sound(".\\Sound\\hit.wav");
    Sound lose = new Sound(".\\Sound\\lose.wav");

    //Classes For The Current Piece And The Game Board
    public static GameBoard gameBoard = new GameBoard();
    public static CurrentPiece currentPiece = new CurrentPiece();
//------------------------------------------------------------------------------
    Tetris(){
        //Set Game Window
        gameWindow = new JFrame("Game Window");
        gameWindow.setSize(Const.WIDTH, Const.HEIGHT);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Create JPanel and Add to Game Window
        panel = new JPanel();
        panel.setLayout(new GridLayout());
        panel.setBackground(Color.black);
        gameWindow.add(panel);
            
        //Add Tetris Title
        tetris = new JLabel("Tetris");
        tetris.setFont(tetrisFont);
        tetris.setHorizontalAlignment(JLabel.CENTER);
        tetris.setVerticalAlignment(JLabel.TOP);
        tetris.setForeground(Color.white);
        panel.add(tetris);
        
        //Add Buttons
        playButton = new JButton("Play");
        playButton.setActionCommand("play");
        controls = new JButton("Controls");
        controls.setActionCommand("controls");
        highScore = new JButton("High Score");
        highScore.setActionCommand("highScore");
        // Add a listener to the button (makes the button active)
        playButton.addActionListener(new ButtonListener());
        controls.addActionListener(new ButtonListener());  
        highScore.addActionListener(new ButtonListener());
        playButton.setFont(buttonFont);
        controls.setFont(buttonFont);
        highScore.setFont(buttonFont);
        panel.add(playButton);
        panel.add(controls);
        panel.add(highScore);
        
        //Create Game Panel
        canvas = new GraphicsPanel();
        
        //Add Keylisteners and Line Listeners to Sounds
        keyListener = new DirectionKeyListener();
        canvas.addKeyListener(keyListener);
        tap.addLineListener(new SoundListener(tap));
        shuffle.addLineListener(new SoundListener(shuffle));
        hit.addLineListener(new SoundListener(hit));
        lose.addLineListener(new SoundListener(lose));
        gameWindow.setVisible(true);
    }
//------------------------------------------------------------------------------
    public void runGameLoop() throws Exception{
        //Get High Score And Name
        FileReader reader = new FileReader();
        highScoreNum = reader.getHighScore();
        highScorer = reader.getHighScorer();
        while (play == true){
            //Initialize piece and set timers to 0
            currentPiece.initialize();
            int timer = 0;
            int timerTimer = 0;
            while(button == true){
                //1ms Wait
                try {Thread.sleep(Const.FRAME_PERIOD);} catch(Exception e){}
                if(button == false){
                    //Wait for start button to be pressed
                    System.out.println("Started!");
                }
            }
            //Start and loop the music
            music.start();
            music.loop();
            while (dead == false) {
                //Repaint the game Window
                gameWindow.repaint();
                //1ms Wait
                try {Thread.sleep(Const.FRAME_PERIOD);} catch(Exception e){}
                //Increase Timer
                timer++;
                //Checks if Player wants to hold a block
                if(hold == true){
                    //Checks if the held block is empty
                    if(held == -1){
                        //Holds block and creates new block to be used
                        held = currentPiece.getType();
                        currentPiece = new CurrentPiece();
                        currentPiece.initialize();
                    }else{
                        //Checks if the block has already been switched this turn
                        if(!switched){
                            //If not, switches held block with current piece
                            int current = currentPiece.getType();
                            currentPiece = new CurrentPiece(held);
                            currentPiece.initialize();
                            held = current;
                        }
                    }
                    //Sets switched to true to indicate already switched this turn and resets hold variable.
                    //Also gets new offsets to display the held block.
                    switched = true;
                    hold = false;
                    heldOffset = reader.getOffset(held);
                }
                //Checks if player wants to hard drop a block.
                if(hardDrop == true){
                    //Checks if block has been dropped
                    boolean dropped = false;
                    while(dropped != true){
                        //Drops block until collision and places it
                        if(currentPiece.moveDown(gameBoard)){
                            dropped = true;
                            hit.start();
                            pieceTransfer();
                        }
                    }
                    //Resets hard drop variable
                    hardDrop = false;
                }
                //Checks if 500ms have passed to speed up game
                if(timerTimer >= 500){
                    //Speeds up game and resets timer
                    timerTime--;
                    timerTimer = 0;
                }else{
                    //Increases timer
                    timerTimer++;
                }
                //Checks if it is time for block to naturally fall
                if(timer >= (timerTime/softDrop)){
                    //Moves piece down and places it if it hits the ground.
                    if(currentPiece.moveDown(gameBoard)){
                        hit.start();
                        pieceTransfer();
                    }
                    //Resets Timer
                    timer = 0;
                }
            }
            //Checks if the player has died
            if (dead){
                //Stops music and starts lose music
                music.stop();
                music.flush();
                music.setFramePosition(0);
                lose.start();
                //Displays game over message
                JOptionPane.showMessageDialog(null,"Game Over!");
                //Returns to main menu
                gameWindow.remove(canvas);
                gameWindow.add(panel);
                gameWindow.repaint();
                //Checks if new high score was achieved and retrieves user data to input new high score
                if(score > highScoreNum){
                    JOptionPane.showMessageDialog(null,"NEW HIGH SCORE");
                    String name = JOptionPane.showInputDialog("What is your name?");
                    reader.setHighScore(name,score);
                }
                //Sets play game to false in order to restart
                play = false;
            }
            gameWindow.repaint();
        }
    }
//------------------------------------------------------------------------------
    //KeyListener
    public class DirectionKeyListener implements KeyListener{
        public void keyPressed(KeyEvent e){
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT){
                //Moves Piece Left and Plays Sound
                currentPiece.moveLeft(gameBoard);
                if(tap.isRunning()){
                    tap.stop();
                    tap.flush();
                    tap.setFramePosition(0);
                }
                tap.start();
            } else if (key == KeyEvent.VK_RIGHT){
                //Moves Piece Right and Plays Sound
                currentPiece.moveRight(gameBoard);
                if(tap.isRunning()){
                    tap.stop();
                    tap.flush();
                    tap.setFramePosition(0);
                }
                tap.start();
            } else if (key == KeyEvent.VK_UP){
                //Rotates Piece and Plays Sound
                currentPiece.rotate(true, gameBoard);
                if(shuffle.isRunning()){
                    shuffle.stop();
                    shuffle.flush();
                    shuffle.setFramePosition(0);
                }
                shuffle.start();
            }
            if (key == KeyEvent.VK_DOWN){
                //Sets the softdrop divider to 10 in order to divide the fall time by 10
                softDrop = 10;
            }
            if(key == KeyEvent.VK_Z){
                //Rotates Piece and Plays Sound
                currentPiece.rotate(false, gameBoard);
                if(shuffle.isRunning()){
                    shuffle.stop();
                    shuffle.flush();
                    shuffle.setFramePosition(0);
                }
                shuffle.start();
            }
            if(key == KeyEvent.VK_SPACE){
                //Sets hard drop to true in order to hard drop the piece and plays sound
                hardDrop = true;
                if(hit.isRunning()){
                    hit.stop();
                    hit.flush();
                    hit.setFramePosition(0);
                }
                hit.start();
            }
            if(key == KeyEvent.VK_SHIFT){
                //Sets hold to true to hold a piece
                hold = true;
            }
        }
        public void keyReleased(KeyEvent e){
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_DOWN){
                //Sets the softdrop multiplier to 1 to return the timer to normal once key has been released
                softDrop = 1;;
            }
        }
        public void keyTyped(KeyEvent e){
        }
    }
//--------------------------------------------------------------------
    public class GraphicsPanel extends JPanel {
        public GraphicsPanel(){
            //Sets the window to focusable so it can take input
            setFocusable(true);
            requestFocusInWindow();
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            //Sets variable with the edge of the play screen
            int edgeOfPlayScreen = (Const.WIDTH/2) - ((Const.BOARDX*Const.SQUARE_SIZE)/2);
            //Fills game screen
            g.setColor(Color.gray);
            g.fillRect(edgeOfPlayScreen,0,Const.BOARDX*Const.SQUARE_SIZE, Const.BOARDY*Const.SQUARE_SIZE);
            //Draws current piece and game board
            currentPiece.draw(g);
            gameBoard.draw(g);
            //Draws score
            g.setColor(Color.white);
            g.setFont(font);
            g.drawString(String.valueOf(score), Const.WIDTH/2 - 15, 100);
            //Draws held piece area
            g.setColor(Color.black);
            g.fillRect(edgeOfPlayScreen + (Const.BOARDX*Const.SQUARE_SIZE),0,Const.SQUARE_SIZE*6,Const.SQUARE_SIZE*6);
            //Draws lines for game screen
            for(int x = 0;x < Const.BOARDX;x++){
                g.drawLine((Const.SQUARE_SIZE*x) + edgeOfPlayScreen,0,(Const.SQUARE_SIZE*x) + edgeOfPlayScreen,Const.BOARDY*Const.SQUARE_SIZE);
            }
            for(int y = 0;y < Const.BOARDY;y++){
                g.drawLine(edgeOfPlayScreen,Const.SQUARE_SIZE*y,edgeOfPlayScreen + (Const.BOARDX*Const.SQUARE_SIZE),Const.SQUARE_SIZE*y);
            }
            //Draws "Held Block"
            g.setColor(Color.white);
            g.setFont(smallFont);
            g.drawString("Held Block",(Const.WIDTH/2) + Const.SQUARE_SIZE*13/2,Const.SQUARE_SIZE/2);
            //Draws the block that is held if there is one
            if(held != -1){
                g.setColor(Const.COLORS[held]);
                for(int piece = 0;piece < 4;piece++){
                    g.fillRect(edgeOfPlayScreen + (Const.SQUARE_SIZE*(Const.BOARDX + 2 + heldOffset[0][piece][1])),
                               Const.SQUARE_SIZE*(3 + heldOffset[0][piece][0]),
                               Const.SQUARE_SIZE,
                               Const.SQUARE_SIZE);
                }
            }
        }
    }
//-------------------------------------------------------------------------------------------
    //Button Listener
    public class ButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //If play buttion is pressed, switch to game window and hide menu
            if("play".equals(e.getActionCommand())){
                gameWindow.remove(panel);
                gameWindow.add(canvas);
                gameWindow.repaint();
                gameWindow.setVisible(true);
                canvas.requestFocusInWindow();
                button = false;
            }else if("controls".equals(e.getActionCommand())){
                //Shows controls pane
                JOptionPane.showMessageDialog(null,"Left Arrow - Move Left \nRight Arrow - Move Right \nDown Arrow - Speed Up Drop \nSpace - Immedietly Drop \nUp Arrow - Rotate Clockwise \nZ - Rotate Counter Clockwise \nLeft Shift - Switch Held Block");
            }else if("highScore".equals(e.getActionCommand())){
                //Shows high score
                JOptionPane.showMessageDialog(null,"High Score: " + highScoreNum + " by " + highScorer);
            }
        }
    }
//-------------------------------------------------------------------------------------------
    //Sound Listener
    public class SoundListener implements LineListener { 
        Sound sound;
        
        SoundListener(Sound sound){
            this.sound = sound;
        }
        public void update(LineEvent event) { 
            if (event.getType() == LineEvent.Type.STOP) { 
                //Flushes queue and resets sound played once over
                sound.flush();                  
                sound.setFramePosition(0);     
            } 
        } 
    }
//------------------------------------------------------------------------------
    public static void main(String[] args) throws Exception{
        //Starts new tetris game
        Tetris game = new Tetris();
        //Loops game until closed
        while(play == true){
           //Game Loop
           game.runGameLoop();
           //Resets Game
           reset();
           //Plays Again
           play = true;
        }
    }
    public static void pieceTransfer()throws Exception{
        //Resets swtich turn to allow for piece holding again
        switched = false;
        //Places current piece into the game board
        for(Tile tile:currentPiece.getPieces()){
            gameBoard.setVal(tile);
        }
        //Creates and initializes new piece
        currentPiece = new CurrentPiece();
        currentPiece.initialize();
        //Detects and clears line
        //Increases score multiplier every time a line is clear (counts multi-line clears)
        int scoreMultiplier = -1;
        while(gameBoard.detectLine() != -1){
            int line = gameBoard.detectLine();
            scoreMultiplier++;
            if(line != -1){
                gameBoard.clearLine(line);
            }
        }
        //Checks if a line was cleared and increases score based on line combo and level
        if(scoreMultiplier > -1){
            score = score + (int)((100*Math.pow(2,scoreMultiplier))*(Const.BASETIME/(float)timerTime));   
        }
        //Checks if spawning area of block is occupied (Tetris death)
        if(currentPiece.outOfBounds(gameBoard)){
            dead = true;
        }
    }
    public static void reset(){
        //Resets all values to starting values
        score = 0;
        play = true;
        held = -1;
        switched = false;
        hold = false;
        dead = false;
        gameBoard = new GameBoard();
        currentPiece = new CurrentPiece();
        button = true;
    }
}