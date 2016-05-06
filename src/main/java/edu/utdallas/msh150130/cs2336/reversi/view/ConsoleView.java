package edu.utdallas.msh150130.cs2336.reversi.view;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.model.Disc;
import edu.utdallas.msh150130.cs2336.reversi.model.Model;

import java.util.Observable;
import java.util.Scanner;

/**
 * Console front end for the Reversi game
 */
public final class ConsoleView extends View {
    private final String inputDescriptionMessage;

    /**
     * Constructs the {@code ConsoleView} that will receive updates from the model
     * @param modelToObserve the model to receive updates from
     * @throws IllegalArgumentException if {@code modelToObserve} is null
     */
    public ConsoleView(final Model modelToObserve){
        super(modelToObserve);

        inputDescriptionMessage = "Enter an ordered pair in the form (row,column) where 0 <= row < " +
                model.getGameBoard().getBoardSize() + " and 0 <= column < " + model.getGameBoard().getBoardSize() + ":";

        System.out.println("----------------------------------------");
        System.out.println("Reversi");
        System.out.println();
        System.out.println("Game Settings:");
        System.out.println("  Board Size: " + model.getGameBoard().getBoardSize());
        System.out.println("  Player Setup: " + model.getPlayerSetup().getFriendlyName());
        System.out.println("  Computer Difficulty: " + model.getComputerDifficulty().getFriendlyName());
        System.out.println("----------------------------------------");

        update(null,null);
    }

    /**
     * Outputs a message to the user in the console
     *
     * @param message string to display
     */
    public void displayMessage(final String message) {
        System.out.println(message);
    }

    /**
     * Waits for input from the user
     *
     * @return a string location on the board to place a new disc
     */

    public String waitForUserInput() {
        System.out.println("Player " + model.getPlayerTurn().getFriendlyName() + " " + inputDescriptionMessage);
        final Scanner input = new Scanner(System.in);

        return input.nextLine().trim();
    }

    /**
     * Prints the game board and current player score.
     *
     * @param o   not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        System.out.println();

        // Column numbers
        StringBuilder columnBorder = new StringBuilder();
        for(int col = 0; col < model.getGameBoard().getBoardSize(); col++){
            System.out.print(col);
            columnBorder.append("-");
        }
        System.out.print('\n');
        System.out.println(columnBorder.toString() + "--");

        for(int row = 0; row < model.getGameBoard().getBoardSize(); row++){
            for(int col = 0; col < model.getGameBoard().getBoardSize(); col++){
                if(model.getGameBoard().getDiscAtLocation(row,col) == null){
                    System.out.print('_');
                }
                else if(model.getGameBoard().getDiscAtLocation(row,col).getColor() == Disc.Color.BLACK){
                    System.out.print('B');
                }
                else if(model.getGameBoard().getDiscAtLocation(row,col).getColor() == Disc.Color.WHITE){
                    System.out.print('W');
                }
            }

            System.out.print(" | " + row + '\n');
        }

        System.out.println();
        System.out.println("Score: Black: " + model.getPlayerScore(Disc.Color.BLACK) + ", White: " +
                model.getPlayerScore(Disc.Color.WHITE));
    }

}
