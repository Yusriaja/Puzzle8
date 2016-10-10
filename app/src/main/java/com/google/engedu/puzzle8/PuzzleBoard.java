package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.NavigableMap;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    int steps;
    PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {

        tiles = new ArrayList<PuzzleTile>();
        Bitmap scaled_bitmap = bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
        int count=-1;
        for(int i=0;i<NUM_TILES;i++)
        {
            for(int j=0;j<NUM_TILES;j++)
            {
                if(i==NUM_TILES-1 && j==NUM_TILES-1)
                    tiles.add(null);

                else
                {
                    Bitmap b = bitmap.createBitmap(scaled_bitmap, j*(parentWidth/NUM_TILES), i*(parentWidth/NUM_TILES),parentWidth/NUM_TILES,parentWidth/NUM_TILES);
                    PuzzleTile tile=new PuzzleTile(b,++count);
                    tiles.add(tile);
                }

            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        previousBoard = otherBoard;
        steps= otherBoard.steps;
    }

    public PuzzleBoard getPreviousBoard() { return previousBoard;}

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {

        ArrayList<PuzzleBoard> new_boards = new ArrayList<>();
        PuzzleBoard existing_puzzle_board=this;

        for(int i=0;i<NUM_TILES*NUM_TILES;i++)
        {
            if(tiles.get(i)==null)
            {
                int tileX = i / NUM_TILES;
                int tileY = i % NUM_TILES;


                for (int[] delta : NEIGHBOUR_COORDS)
                {
                    int nullX = tileX + delta[0];
                    int nullY = tileY + delta[1];

                    PuzzleBoard new_board = null;
                    if(nullX>=0 && nullX<NUM_TILES && nullY>=0 && nullY<NUM_TILES)
                    {
                        new_board= new PuzzleBoard(existing_puzzle_board);
                        new_board.swapTiles(nullX*NUM_TILES+nullY,tileX*NUM_TILES+tileY);
                        new_boards.add(new_board);
                    }
                }
                break;
            }
        }
        return new_boards;
    }

    public int priority() {

        int manhattan_distance=0;
        for(int i=0;i<NUM_TILES;i++)
        {
            if(tiles.get(i)!=null)
            {
                int curr_X=i/NUM_TILES;
                int curr_Y=i%NUM_TILES;
                int tile_no = tiles.get(i).getNumber();
                int correct_pos_X=tile_no/NUM_TILES;
                int correct_pos_Y=tile_no%NUM_TILES;
                int distance = Math.abs(curr_X-correct_pos_X)+Math.abs(curr_Y-correct_pos_Y);
                manhattan_distance+=distance;
            }

        }
        return manhattan_distance+steps;
    }

}
