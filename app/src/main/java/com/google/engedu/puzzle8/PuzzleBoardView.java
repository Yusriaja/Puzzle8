package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {

            for(int i=1;i<=30;i++)
            {
                int max_index =(puzzleBoard.neighbours().size())-1;
                int min_index=0;
                int random_index=min_index+(int)( Math.random()*((max_index-min_index)+1) );
                Log.e("Rohan","random:"+random_index );
                Log.e("Rohan","size:"+max_index );
                PuzzleBoard shuffled_board  = puzzleBoard.neighbours().get(random_index);
                puzzleBoard = shuffled_board;
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve()
    {
        PriorityQueue<PuzzleBoard> boardQueue = new PriorityQueue<>(100, new Comparator<PuzzleBoard>() {
            @Override
            public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
                if(lhs.priority() <= rhs.priority())
                    return -1;
                else
                return 1;
            }
        });

        puzzleBoard.steps=0;
        puzzleBoard.previousBoard=null;
        boardQueue.add(puzzleBoard);


        while(!boardQueue.isEmpty())
        {

            PuzzleBoard board_lowest_priority = boardQueue.poll();
            if(!board_lowest_priority.resolved())
            {
                for(int i=0;i<board_lowest_priority.neighbours().size();i++)
                {
                    if(board_lowest_priority.getPreviousBoard()!=null && !board_lowest_priority.getPreviousBoard().equals(board_lowest_priority))
                    {
                        boardQueue.add(board_lowest_priority.neighbours().get(i));
                    }
                    else
                    {
                        boardQueue.add(board_lowest_priority.neighbours().get(i));
                    }
                }
            }

            else
            {
                ArrayList<PuzzleBoard> boards_leading_to_soln= new ArrayList<>();
                PuzzleBoard prev_board = board_lowest_priority.getPreviousBoard();
                while(prev_board!=null)
                {
                    boards_leading_to_soln.add(prev_board);
                    prev_board = prev_board.getPreviousBoard();
                }
                Collections.reverse(boards_leading_to_soln);
                animation=(ArrayList<PuzzleBoard>)boards_leading_to_soln.clone();
                break;

            }
        }

        invalidate();
    }
}
