import java.util.Arrays;
import java.awt.*;

//Game Board Class
public class GameBoard{
    //2D Array of Tiles
    private Tile[][] gameBoard = new Tile[Const.BOARDY][Const.BOARDX];
    
    //Constructor (Creates empty Board)
    public GameBoard(){
        for (int i = 0; i < Const.BOARDY;i++){
            Arrays.fill(this.gameBoard[i], null);
        }
    }
    //Game Board Array Getter
    public Tile[][] getGameBoard(){
        return this.gameBoard;
    }
    //Adds tile into the array
    public void setVal(Tile tile){
        this.gameBoard[tile.getY()][tile.getX()] = tile;
    }
    //Checks for full lines
    public int detectLine(){
        for(int y = 0;y < Const.BOARDY;y++){
            for(int x = 0; x < Const.BOARDX;x++){
                if(this.gameBoard[y][x] != null){
                    if(x == Const.BOARDX - 1){
                        return y;
                    }
                }else{
                    x = Const.BOARDX;
                }
            }
        }
        return -1;
    }
    //Clears line by moving everything down
    public void clearLine(int line){
        for(int y = line; y > -1;y--){
            for(int x = 0; x < Const.BOARDX;x++){
                if(y - 1 == -1){
                    this.gameBoard[y][x] = null;
                }else{
                    if(this.gameBoard[y - 1][x] != null){
                        Tile currentTile = this.gameBoard[y - 1][x];
                        this.gameBoard[y][x] = new Tile(currentTile.getX(),currentTile.getY() + 1,currentTile.getColor());
                    }else{
                       this.gameBoard[y][x] = null; 
                    }
                }
            }
        }
    }
    //Draws game board
    public void draw(Graphics g){
        for(int x = 0; x < Const.BOARDX; x++){
            for(int y = 0; y < Const.BOARDY; y++){
                if(this.gameBoard[y][x] != null){
                    this.gameBoard[y][x].draw(g);
                }
            }
        }
    }
}