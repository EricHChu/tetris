import java.util.Arrays;
import java.awt.*;

//Tile class
public class Tile{
    //Contains position and color
    private int x;
    private int y;
    private int color;
    
    //Constructor
    public Tile(int x, int y, int color){
        this.x = x;
        this.y = y;
        this.color = color;
    }
    //X Position Getter
    public int getX(){
        return this.x;
    }
    //Y Position Getter
    public int getY(){
        return this.y;
    }
    //Color Getter
    public int getColor(){
        return this.color;
    }
    //Checks if the tile is out of bounds or colliding with another block on the game board
    public boolean outOfBounds(GameBoard gBoard){
        if(this.x < 0 || this.x >= Const.BOARDX || this.y < 0 || this.y >= Const.BOARDY || gBoard.getGameBoard()[this.y][this.x] != null){
            return true;
        }
        return false;
    }
    //Moves block down and checks if it collides
    public boolean moveDown(GameBoard gBoard){
        this.y = this.y + 1;
        if(this.y >= Const.BOARDY){
            return true;
        }else if(gBoard.getGameBoard()[this.y][this.x] != null){
            return true;
        }
        return false;
    }
    //Moves block up
    public void moveUp(){
        this.y = this.y -1;
    }
    //Moves block right and checks if it collides
    public boolean moveRight(GameBoard gameBoard){
        this.x = this.x + 1;
        if(x >= Const.BOARDX){
            return true;
        }else if(gameBoard.getGameBoard()[y][x] != null){
            return true;
        }
        return false;
    }
    //Moves block left and checks if it collides
    public boolean moveLeft(GameBoard gameBoard){
        this.x = this.x - 1;
        if(x < 0){
            return true;
        }else if(gameBoard.getGameBoard()[y][x] != null){
            return true;
        }
        return false;
    }
    //Draws the tile
    public void draw(Graphics g){
        g.setColor(Const.COLORS[this.color]);
        g.fillRect((Const.WIDTH/2) - ((Const.BOARDX*Const.SQUARE_SIZE)/2) + this.x * Const.SQUARE_SIZE,
                   this.y *Const.SQUARE_SIZE,Const.SQUARE_SIZE,Const.SQUARE_SIZE);
    }
}