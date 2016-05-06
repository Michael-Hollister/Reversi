package edu.utdallas.msh150130.cs2336.reversi.model;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.stream.IntStream;

/**
 * Implements game logic and contains game data
 */
public final class Model extends Observable{
    private PlayerSetup playerSetup;
    private Board gameBoard;
    private ComputerDifficulty computerDifficulty;
    private Disc.Color playerTurn = Disc.Color.BLACK;

    /**
     * A pair of iterators that will iterate along a specified direction
     */
    private final class DirectionIteratorPair{
        private final Iterator<Integer> rowIterator;
        private final Iterator<Integer> columnIterator;

        /**
         * Constructs an iterator pair
         * @param row starting row, exclusive
         * @param column starting column, exclusive
         * @param direction direction to iterate along
         * @throws IllegalArgumentException if {@code direction} is null
         */
        DirectionIteratorPair(final int row, final int column, final Direction direction){
            // The iterators iterate sequentially, over the same value, or reverse sequentially. The
            // function used to reverse the iterator uses the formula: to - i + from  - 1.
            switch(direction){
                case N:
                    rowIterator = IntStream.range(0, row).map(value -> row - value - 1).iterator();
                    columnIterator = IntStream.range(0, row).map(value -> column).iterator();
                    break;
                case S:
                    rowIterator = IntStream.range(row + 1, gameBoard.getBoardSize()).iterator();
                    columnIterator = IntStream.range(row + 1, gameBoard.getBoardSize()).map(value -> column).iterator();
                    break;
                case E:
                    rowIterator = IntStream.range(column + 1, gameBoard.getBoardSize()).map(value -> row).iterator();
                    columnIterator = IntStream.range(column + 1, gameBoard.getBoardSize()).iterator();
                    break;
                case W:
                    rowIterator = IntStream.range(0, column).map(value -> row).iterator();
                    columnIterator = IntStream.range(0, column).map(value -> column - value - 1).iterator();
                    break;
                case NE:
                    rowIterator = IntStream.range(0, row).map(value -> row - value - 1).iterator();
                    columnIterator = IntStream.range(column + 1, gameBoard.getBoardSize()).iterator();
                    break;
                case NW:
                    rowIterator = IntStream.range(0, row).map(value -> row - value - 1).iterator();
                    columnIterator = IntStream.range(0, column).map(value -> column - value - 1).iterator();
                    break;
                case SE:
                    rowIterator = IntStream.range(row + 1, gameBoard.getBoardSize()).iterator();
                    columnIterator = IntStream.range(column + 1, gameBoard.getBoardSize()).iterator();
                    break;
                case SW:
                    rowIterator = IntStream.range(row + 1, gameBoard.getBoardSize()).iterator();
                    columnIterator = IntStream.range(0, column).map(value -> column - value - 1).iterator();
                    break;
                default:
                    rowIterator = null;
                    columnIterator = null;
                    throw new IllegalArgumentException("direction should not be null");
            }
        }

        Iterator<Integer> getRowIterator(){
            return rowIterator;
        }
        Iterator<Integer> getColumnIterator(){
            return columnIterator;
        }
    }

    /**
     * Compass directions used to validate moves
     */
    private enum Direction{
        N, S, E, W, NE, NW, SE, SW
    }

    /**
     * Constructs a {@code Model} that will contain game data
     * @param boardSize size of the n x n board to play on
     * @param playerSetup type of player setup
     * @param difficulty difficulty of computer players
     * @throws IllegalArgumentException if {@code playerSetup} or {@code difficulty} is null
     */
    public Model(final int boardSize, final PlayerSetup playerSetup, final ComputerDifficulty difficulty){
        if(boardSize < 3){
            throw new IllegalArgumentException("boardSize should be greater than 2");
        }
        if(playerSetup == null){
            throw new IllegalArgumentException("playerSetup should not be null");
        }
        if(difficulty == null){
            throw new IllegalArgumentException("difficulty should not be null");
        }

        this.gameBoard = new Board(boardSize);
        this.playerSetup = playerSetup;
        this.computerDifficulty = difficulty;
    }


    //region Getters and Setters
    public PlayerSetup getPlayerSetup(){
        return playerSetup;
    }
    public Board getGameBoard(){
        return gameBoard;
    }
    public ComputerDifficulty getComputerDifficulty(){
        return computerDifficulty;
    }
    public Disc.Color getPlayerTurn() { return playerTurn; }

    public void setPlayerSetup(PlayerSetup value){
        if(value == null){
            throw new IllegalArgumentException("value should not be null");
        }

        playerSetup = value;
    }

    public void setComputerDifficulty(ComputerDifficulty value){
        if(value == null){
            throw new IllegalArgumentException("value should not be null");
        }

        computerDifficulty = value;
    }

    //endregion

    /**
     * Returns an index from a list of optimal moves that a player can make
     * @param possibleMoves list of possible valid moves
     * @return an index of a move that yields the most discs captured
     */
    private int getOptimalMove(ArrayList<InputLocation> possibleMoves){
        assert possibleMoves != null : "possibleMoves should not be null";

        final Board currentBoard = getGameBoard();
        int maxScore = 0;
        int maxScoreIndex = 0;

        // Find the highest yielding move by placing discs and calculating
        for(int index = 0; index < possibleMoves.size(); index++){
            gameBoard = currentBoard.clone();
            placeDisc(possibleMoves.get(index).getRow(),possibleMoves.get(index).getColumn(),false);

            if(getPlayerScore(getPlayerTurn()) > maxScore){
                maxScore = getPlayerScore(getPlayerTurn());
                maxScoreIndex = index;
            }
        }

        gameBoard = currentBoard;
        return maxScoreIndex;
    }

    /**
     * Returns a list of directions where a valid capture can be performed
     * @param row row of placement disc
     * @param column column of placement disc
     * @param placementDiscColor color of placement disc
     * @return a list of directions
     */
    private ArrayList<Direction> validPathDirections(final int row, final int column, final Disc.Color placementDiscColor){
        assert getGameBoard().isLocationOnBoard(row,column) : "row, column combination is not located on the board";
        assert placementDiscColor != null : "placementDiscColor should not be null";

        ArrayList<Direction> validDirections = new ArrayList<>();

        for(Direction direction : Direction.values()){
            if(isPathValid(row,column,placementDiscColor,direction)){
                validDirections.add(direction);
            }
        }

        return validDirections;
    }

    /**
     * Determines if a path has an opponents disc between to discs of the player
     * @param row to start from
     * @param column to end from
     * @param placementDiscColor disc color of the current player
     * @param direction direction to validate
     * @return true if there is an opponent's disc between to discs of the same color
     */
    private boolean isPathValid(final int row, final int column, final Disc.Color placementDiscColor,
                                final Direction direction){
        assert placementDiscColor != null : "placementDiscColor should not be null";
        assert direction != null : "direction should not be null";

        DirectionIteratorPair pair = new DirectionIteratorPair(row,column,direction);
        Iterator<Integer> rowIterator = pair.getRowIterator();
        Iterator<Integer> columnIterator = pair.getColumnIterator();

        boolean isOpponentDiscBetween = false;
        Disc.Color opponentDiscColor = Disc.Color.getOppositeColor(placementDiscColor);

        while(rowIterator.hasNext() && columnIterator.hasNext()){
            Disc disc = gameBoard.getDiscAtLocation(rowIterator.next(),columnIterator.next());

            if(disc == null || (!isOpponentDiscBetween && disc.getColor() == placementDiscColor)){
                break;
            }
            else if(!isOpponentDiscBetween && disc.getColor() == opponentDiscColor){
                isOpponentDiscBetween = true;
            }
            else if(isOpponentDiscBetween && disc.getColor() == placementDiscColor){
                return true;
            }
        }

        return false;
    }


    /**
     * Places a disc on the board, which also captures opponents pieces while optionally notifying the view
     * @param row row to place the disc
     * @param column column to place the disc
     * @param notifyObservers notifies observers about the state of the board
     */
    private void placeDisc(final int row, final int column, final boolean notifyObservers){
        ArrayList<Direction> validDirections = validPathDirections(row,column,getPlayerTurn());
        getGameBoard().setDiscAtLocation(row,column,new Disc(getPlayerTurn()));

        Disc.Color opponentDiscColor = Disc.Color.getOppositeColor(getPlayerTurn());
        for(Direction direction : validDirections){
            DirectionIteratorPair pair = new DirectionIteratorPair(row, column, direction);
            Iterator<Integer> rowIterator = pair.getRowIterator();
            Iterator<Integer> columnIterator = pair.getColumnIterator();

            while(rowIterator.hasNext() && columnIterator.hasNext()){
                int rowIndex = rowIterator.next();
                int columnIndex = columnIterator.next();

                // Path iterators go from a path next to the disc to the end of the board, not unitl the next
                // colored disc
                if(getGameBoard().getDiscAtLocation(rowIndex,columnIndex).getColor() == getPlayerTurn()){
                    break;
                }

                if(getGameBoard().getDiscAtLocation(rowIndex,columnIndex) != null &&
                        getGameBoard().getDiscAtLocation(rowIndex,columnIndex).getColor() == opponentDiscColor){
                    getGameBoard().setDiscAtLocation(rowIndex,columnIndex,new Disc(getPlayerTurn()));
                }
            }

        }

        // False when calculating scores values from computer moves..
        if(notifyObservers){
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Returns an input location from a list of valid moves
     * @return an input location
     */
    public InputLocation receiveComputerInput(){
        ArrayList<InputLocation> validLocations = new ArrayList<>();

        for(int row = 0; row < getGameBoard().getBoardSize(); row++){
            for(int column = 0; column < getGameBoard().getBoardSize(); column++){
                if(isValidMove(row,column,playerTurn)){
                    validLocations.add(new InputLocation(row,column));
                }
            }
        }

        int moveChoice = 0;

        switch(getComputerDifficulty()){
            case EASY:
                moveChoice = (int)(Math.random() * validLocations.size());
                break;
            case MEDIUM:
                if(Math.random() > 0.5){
                    moveChoice = getOptimalMove(validLocations);
                }
                else{
                    moveChoice = (int)(Math.random() * validLocations.size());
                }
                break;
            case HARD:
                moveChoice = getOptimalMove(validLocations);
                break;
        }

        return validLocations.get(moveChoice);
    }


    /**
     * Allows the controller to change player turn
     */
    public void changeTurn(){
        playerTurn = Disc.Color.getOppositeColor(getPlayerTurn());
    }

    /**
     * Determines of the location for the specified color is a valid move
     * @param row board row
     * @param column board column
     * @param discColor color of disc to check
     * @return true if the location is on the board, not place on top of another disc, and can capture at least one disc.
     * @throws IllegalArgumentException if {@code discColor} is null
     */
    public boolean isValidMove(final int row, final int column, final Disc.Color discColor){
        if(discColor == null){
            throw new IllegalArgumentException("discColor should not be null");
        }

        if(!getGameBoard().isLocationOnBoard(row,column) || getGameBoard().getDiscAtLocation(row,column) != null){
            return false;
        }

        for(Direction direction : Direction.values()){
            if(isPathValid(row,column,discColor,direction)){
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if a valid move exists on the board for the player
     * @return true if a move exists for the specified player
     * @throws IllegalArgumentException if {@code discColor} is null
     */
    public boolean validMoveExists(final Disc.Color playerColor){
        if(playerColor == null){
            throw new IllegalArgumentException("discColor should not be null");
        }

        for(int row = 0; row < getGameBoard().getBoardSize(); row++){
            for(int column = 0; column < getGameBoard().getBoardSize(); column++){
                if(isValidMove(row,column,playerColor)){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the count of the discs of the specified color on the game board
     * @param playerColor color to determine score
     * @return the number of discs on the game board
     * @throws IllegalArgumentException if {@code discColor} is null
     */
    public int getPlayerScore(final Disc.Color playerColor){
        if(playerColor == null){
            throw new IllegalArgumentException("playerColor should not be null");
        }
        int returnScore = 0;

        for(int row = 0; row < getGameBoard().getBoardSize(); row++){
            for(int column = 0; column < getGameBoard().getBoardSize(); column++){
                Disc disc = getGameBoard().getDiscAtLocation(row,column);

                if(disc != null && disc.getColor() == playerColor){
                    returnScore++;
                }
            }
        }

        return returnScore;
    }

    /**
     * Places a disc on the board, which also captures opponents picees and updates the view
     * @param row row to place the disc
     * @param column column to place the disc
     */
    public void placeDisc(final int row, final int column){
        placeDisc(row,column,true);
    }
}
