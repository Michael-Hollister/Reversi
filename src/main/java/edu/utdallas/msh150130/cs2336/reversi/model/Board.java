package edu.utdallas.msh150130.cs2336.reversi.model;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

/**
 * Represents a n x n board of discs
 */
public final class Board implements Cloneable {
    private final Disc[][] board;

    /**
     * Constructs a board with in the standard disc configuration
     * @param boardSize size of the board to construct
     */
    public Board(final int boardSize){
        if(boardSize < 3){
            throw new IllegalArgumentException("boardSize should be greater than 2");
        }

        board = new Disc[boardSize][boardSize];

        // For odd numbered boards, the initial positions of the discs cannot be in the exact middle. Since odd numbered
        // game boards were stated as valid on the eLearning thread, the location for the starting pieces on odd
        // will be numbered boards will be closer to the upper left.
        int middle = boardSize / 2;
        board[middle - 1][middle - 1] = new Disc(Disc.Color.WHITE);
        board[middle - 1][middle] = new Disc(Disc.Color.BLACK);
        board[middle][middle - 1] = new Disc(Disc.Color.BLACK);
        board[middle][middle] = new Disc(Disc.Color.WHITE);
    }

    /**
     * Constructs a board with the given state passed in as an argument
     * @param board state of the board to construct
     */
    public Board(final Disc[][] board){
        if(board == null){
            throw new IllegalArgumentException("board should not be null");
        }
        if(board.length < 3){
            throw new IllegalArgumentException("board length should be greater than 2");
        }
        if(board.length != board[0].length){
            throw new IllegalArgumentException("board is not a n x n array");
        }

        this.board = board.clone();
    }


    /**
     * Replaces the disc at the specified row, column
     * @param row board row
     * @param column board column
     * @param disc the instance of the disc to replace. Specify null to remove the disc at the location.
     */
    public void setDiscAtLocation(final int row, final int column, final Disc disc){
        if(!isLocationOnBoard(row,column)){
            throw new IllegalArgumentException("the specified row, column combination is not located on the board");
        }

        board[row][column] = disc;
    }

    /**
     * Returns the disc instance at a location on the board
     * @param row board row
     * @param column board column
     * @return the instance of the disc at the location. Returns null if no disc is placed at the specified row, column.
     */
    public Disc getDiscAtLocation(final int row, final int column){
        return board[row][column];
    }

    public boolean isLocationOnBoard(final int row, final int column){
        return (row >= 0 && row < getBoardSize()) && (column >= 0 && column < getBoardSize());
    }

    public int getBoardSize(){
        return board.length;
    }

    /**
     * Returns a deep copy of this board object
     * @return a deep copy of this board object
     */
    @Override
    public Board clone(){
        Disc[][] copy = new Disc[getBoardSize()][getBoardSize()];

        for(int row = 0; row < getBoardSize(); row++){
            for(int column = 0; column < getBoardSize(); column++){
                copy[row][column] = getDiscAtLocation(row,column);
            }
        }

        return new Board(copy);
    }
}
