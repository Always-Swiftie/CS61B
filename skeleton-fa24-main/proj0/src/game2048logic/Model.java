package game2048logic;

import game2048rendering.Board;
import game2048rendering.Side;
import game2048rendering.Tile;

import java.util.Formatter;
import java.util.Random;


/** The state of a game of 2048.
 *  @author P. N. Hilfinger + Josh Hug
 */
public class Model {
    /** Current contents of the board. */
    private final Board board;
    /** Current score. */
    private int score;

    /* Coordinate System: column x, row y of the board (where x = 0,
     * y = 0 is the lower-left corner of the board) will correspond
     * to board.tile(x, y).  Be careful!
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = 0;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (x, y) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score) {
        board = new Board(rawValues);
        this.score = score;
    }

    /** Return the current Tile at (x, y), where 0 <= x < size(),
     *  0 <= y < size(). Returns null if there is no tile there.
     *  Used for testing. */
    public Tile tile(int x, int y) {
        return board.tile(x, y);
    }

    /** Return the number of squares on one side of the board. */
    public int size() {
        return board.size();
    }

    /** Return the current score. */
    public int score() {
        return score;
    }


    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        board.clear();
    }


    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        return maxTileExists() || !atLeastOneMoveExists();
    }

    /** Returns this Model's board. */
    public Board getBoard() {
        return board;
    }

    /** Returns true if at least one space on the board is empty.
     *  Empty spaces are stored as null.
     * */
    public boolean emptySpaceExists() {
        // TODO: Task 1. Fill in this function.
        //IDEA:遍历整个方格，只要有一个方格获取到的tile值为null就返回真，如果遍历完整个方格都没找到就为假
        //方格是一个board.size*size的正方形，左下角为原点，往北走y越大，往东走x越大
        for (int x=0;x<board.size();x++){
            for(int y=0;y<board.size();y++){
                if(board.tile(x,y)==null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public boolean maxTileExists() {
        // TODO: Task 2. Fill in this function.
        for(int x=0;x<board.size();x++){
            for (int y=0;y<board.size();y++){
                if(board.tile(x,y)!=null){
                    if(board.tile(x,y).value()==MAX_PIECE){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public boolean atLeastOneMoveExists() {
        // TODO: Task 3. Fill in this function.
        //1.先看看方格里面有没有空位
        if(emptySpaceExists()){
            return true;//如果还存在空位，直接返回真
        }
        //2.如果没有空位，先看看也没有2048出现
        if(maxTileExists()){
            return true;
        }
        //3.如果没有空位，就要看看有没有可以合并的任意两个瓦片
        for(int x=0;x<board.size();x++){
            for (int y=0;y<board.size();y++){
                //遍历方格中每个瓦片，对于每个瓦片都遍历它的相邻瓦片看看能不能合并
                //(x+1,y),(x,y+1),(x,y-1),(x-1,y)四个点需要看，其他不要
                if(x+1<board.size()&&board.tile(x+1,y).value()==board.tile(x,y).value()){
                        return true;
                }
                if(y+1< board.size()&&board.tile(x,y+1).value()==board.tile(x,y).value()){
                        return true;
                }
                if(y-1>=0&&board.tile(x,y-1).value()==board.tile(x,y).value()){
                        return true;
                }
                if(x-1>=0&&board.tile(x-1,y).value()==board.tile(x,y).value()){
                        return true;
                }
            }
        }
        return false;
    }
    /**
     * Moves the tile at position (x, y) as far up as possible.
     *
     * Rules for Tilt:
     * 1. If two Tiles are adjacent in the direction of motion (ignoring empty space)
     *    and have the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     */
    public void moveTileUpAsFarAsPossible(int x, int y) {
        Tile currTile = board.tile(x, y);
        int myValue = currTile.value();
        int targetY = y;//该方法用于将当前坐标的瓦片尽可能向上移动
                        //如果瓦片上方无法合并或者为null,那么找到最靠上方的位置，move方法会新建一个瓦片，传入当前瓦片的值
        //要找到正确的targetY
        int tempY=y+1;
        while (tempY<board.size()){
            //case1:当前格子为null
            if(board.tile(x,tempY)==null){
                targetY=tempY;//先把target更新
                tempY++;
            }else{
                //case2:当前格子不为null
                if(tempY!=y&&board.tile(x,tempY)!=null){
                    //a.如果值和myvalue不相等，那么不能合并，不更新target,退出循环
                    if(board.tile(x,tempY).value()!=myValue){
                        break;
                    }else if(board.tile(x,tempY).value()==myValue&&!board.tile(x,tempY).wasMerged()){//b.如果值等于myvalue，并且该瓦片没有被合并过那么可以合并，更新target为temp,同时因为执行了合并操作，tempY处的瓦片需要被标记为已合并
                        targetY=tempY;
                        this.score+=2*board.tile(x,tempY).value();
                        break;
                    }else if(board.tile(x,tempY).value()==myValue&&board.tile(x,tempY).wasMerged()){//c.值相等，但是已经合并过，不更新targetY
                        break;
                    }
                }
            }
        }
        board.move(x,targetY,currTile);

        // TODO: Tasks 5, 6, and 10. Fill in this function.
    }

    /** Handles the movements of the tilt in column x of board B
     * by moving every tile in the column as far up as possible.
     * The viewing perspective has already been set,
     * so we are tilting the tiles in this column up.
     * */
    public void tiltColumn(int x) {
        // TODO: Task 7. Fill in this function.
        //对一列上的所有非null瓦片都执行moveTileUp...方法
        for (int y= board.size()-1;y>=0;y--){
            if(board.tile(x,y)!=null){
                moveTileUpAsFarAsPossible(x,y);
            }
        }
    }

    public void tilt(Side side) {
        //Task8-tilt up
        switch (side){
            case NORTH -> {
                for(int x=0;x< board.size();x++){
                    for (int y= board.size()-1;y>=0;y--){
                        if(board.tile(x,y)!=null){
                            moveTileUpAsFarAsPossible(x,y);
                        }
                    }
                }
                break;
            }
            case WEST -> {
                board.setViewingPerspective(Side.WEST);
                for(int x=0;x< board.size();x++){
                    for (int y= board.size()-1;y>=0;y--){
                        if(board.tile(x,y)!=null){
                            moveTileUpAsFarAsPossible(x,y);
                        }
                    }
                }
            }
            case EAST -> {
                board.setViewingPerspective(Side.EAST);
                for(int x=0;x< board.size();x++){
                    for (int y= board.size()-1;y>=0;y--){
                        if(board.tile(x,y)!=null){
                            moveTileUpAsFarAsPossible(x,y);
                        }
                    }
                }
            }
            case SOUTH -> {
                board.setViewingPerspective(Side.SOUTH);
                for(int x=0;x< board.size();x++){
                    for (int y= board.size()-1;y>=0;y--){
                        if(board.tile(x,y)!=null){
                            moveTileUpAsFarAsPossible(x,y);
                        }
                    }
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);
        // TODO: Tasks 8 and 9. Fill in this function.
    }

    /** Tilts every column of the board toward SIDE.
     */
    public void tiltWrapper(Side side) {
        board.resetMerged();
        tilt(side);
    }


    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int y = size() - 1; y >= 0; y -= 1) {
            for (int x = 0; x < size(); x += 1) {
                if (tile(x, y) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(x, y).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (game is %s) %n", score(), over);
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Model m) && this.toString().equals(m.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
