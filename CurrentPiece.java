import java.util.Arrays;
import java.awt.*;

//Current Piece Class
public class CurrentPiece{
    //Position of center piece, piece type, orientation, offsets, filereader and array with tiles of current piece
    private int x;
    private int y;
    private int type;
    private int orientation;
    private int[][][] offsets = new int[4][4][2];
    private FileReader reader = new FileReader();
    private Tile[] pieces = new Tile[4];
    
    //Constructor
    public CurrentPiece(){
        //Sets starting position and orientation as well as a randomly generated type
        this.orientation = 0;
        this.x = 4;
        this.type = (int)Math.round((Math.random()*6));
        if(this.type <= 1){
            this.y = 0;
        }else{
            this.y = 1;
        }
    }
    //Constructor (Predetermined)
    public CurrentPiece(int type){
        //Sets same values except with a predetermined type
        this.orientation = 0;
        this.x = 4;
        this.type = type;
        if(this.type <= 1){
            this.y = 0;
        }else{
            this.y = 1;
        }
    }
    //Initialize current piece
    public void initialize() throws Exception{
        //Sets offsets and fills array with tiles
        this.offsets = reader.getOffset(this.type);
        for(int piece = 0; piece < 4; piece++){
            this.pieces[piece] = new Tile(this.x + this.offsets[this.orientation][piece][1],this.y + this.offsets[this.orientation][piece][0],this.type);
        }
    }
    //X Position Getter
    public int getX(){
        return this.x;
    }
    //Y Position Getter
    public int getY(){
        return this.y;
    }
    //Type Getter
    public int getType(){
        return this.type;
    }
    //Piece Array Getter
    public Tile[] getPieces(){
        return this.pieces;
    }
    //Move Current Piece Down
    public boolean moveDown(GameBoard gameBoard){
        boolean collide = false;
        //Checks for collision
        for(Tile i: this.pieces){
            if(i.moveDown(gameBoard)){
                collide = true;
            }
        }
        //Moves Down
        this.y = this.y + 1;
        //If it collides, move up
        if(collide){
            this.y = this.y - 1;
            for(Tile i:this.pieces){
                i.moveUp();
            }
            return true;
        }
        return false;
    }
    //Moves current piece right
    public void moveRight(GameBoard gameBoard){
        //Checks for collision if all tiles move right
        boolean collide = false;
        for(Tile i: this.pieces){
            if(i.moveRight(gameBoard)){
                collide = true;
            }
        }
        //Moves left if it collides
        this.x = this.x + 1;
        if(collide){
            this.x = this.x - 1;
            for(Tile i:this.pieces){
                i.moveLeft(gameBoard);
            }
        }
    }
    //Moves current piece left
    public void moveLeft(GameBoard gameBoard){
        //Moves all tiles left and checks for collision
        boolean collide = false;
        for(Tile i: this.pieces){
            if(i.moveLeft(gameBoard)){
                collide = true;
            }
        }
        //Moves tiles back right if it collides
        this.x = this.x - 1;
        if(collide){
            this.x = this.x + 1;
            for(Tile i:this.pieces){
                i.moveRight(gameBoard);
            }
        }
    }
    //Rotates Current Piece
    public boolean rotate(boolean clockwise, GameBoard gBoard){
        //Checks if its the o piece
        if(this.type == 1){
            //If it is, there is no need to rotate
            return true;
        }
        //If its clockwise, the orientation is increased (or reset if its at the final one)
        if(clockwise){
            if(this.orientation >= 3){
                this.orientation = 0;
            }else{
                this.orientation = this.orientation + 1;
            }
        }else{
            // if its counter clockwise, decrease the orientation (or go to the highest if its at the lowest)
            if(this.orientation <= 0){
                this.orientation = 3;
            }else{
                this.orientation = this.orientation - 1;
            }
        }
        //Checks if rotating it will go out of bounds
        boolean outOfBounds = false;
        for(int piece = 0; piece < 4; piece++){
            Tile tile = new Tile(this.x + this.offsets[this.orientation][piece][1],this.y + this.offsets[this.orientation][piece][0],this.type);
            if(tile.outOfBounds(gBoard)){
                outOfBounds = true;
            }
        }
        //If it doesn't, rotate it
        if(!outOfBounds){
            for(int piece = 0; piece < 4; piece++){
                this.pieces[piece] = new Tile(this.x + this.offsets[this.orientation][piece][1],this.y + this.offsets[this.orientation][piece][0],this.type);
            }
        }
        return false;
    }
    //Checks if current piece is out of bounds
    public boolean outOfBounds(GameBoard gBoard){
        boolean outOfBounds = false;
        //Checks all tiles to see if it is out of bounds and returns true if one is
        for(Tile tile:this.pieces){
            if(tile.outOfBounds(gBoard)){
                outOfBounds = true;
            }
        }
        return outOfBounds;
    }
    //Draws current piece
    public void draw(Graphics g){
        for(int i = 0; i < 4;i++){
            if(this.pieces[i] != null){
                this.pieces[i].draw(g);
            }
        }
    }
}