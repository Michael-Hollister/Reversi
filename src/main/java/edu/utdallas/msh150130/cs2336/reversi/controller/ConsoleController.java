package edu.utdallas.msh150130.cs2336.reversi.controller;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.model.Disc;
import edu.utdallas.msh150130.cs2336.reversi.model.InputLocation;
import edu.utdallas.msh150130.cs2336.reversi.model.Model;
import edu.utdallas.msh150130.cs2336.reversi.view.ConsoleView;

/**
 * Handles console input from the user
 */
public final class ConsoleController extends Controller {
    /**
     * View that the controller can access
     */
    final ConsoleView view;

    /**
     * Constructs a {@code ConsoleController} that handles game input
     * @param modelToControl model that the controller is communicating with
     * @throws IllegalArgumentException if {@code modelToControl} is null
     */
    public ConsoleController(final Model modelToControl){
        super(modelToControl);

        view = new ConsoleView(modelToControl);
    }

    /**
     * Attempts to convert a string into an input location
     * @param input string from the user in the form (n,m) or n, m
     * @return returns a location if the row and column is a parsable integer. Otherwise returns null.
     */
    private InputLocation parseInput(String input){
        assert input != null : "input should not be null";

        input = input.replaceAll(" ","");
        input = input.replaceAll("\\(","");
        input = input.replaceAll("\\)","");

        try{
            final int row = Integer.parseInt(input.substring(0,input.indexOf(',')));
            final int column = Integer.parseInt(input.substring(input.indexOf(',') + 1));

            return new InputLocation(row,column);
        }
        catch (NumberFormatException e){
            return null;
        }
    }

    /**
     * Determines if the user input is a valid game move
     * @param input string in the form (n,m) or n, m
     * @return true if the input is parsable and a valid game move
     */
    private boolean isValidInput(final String input){
        assert input != null : "input should not be null";

        // Allows (n,m) or n, m
        if(input.matches("\\(?\\s*\\d+\\s*,\\s*\\d+\\s*\\)?")){
            InputLocation returnLocation = parseInput(input);

            return (returnLocation != null) && (model.isValidMove(returnLocation.getRow(),
                    returnLocation.getColumn(),model.getPlayerTurn()));
        }

        return false;
    }

    /**
     * Passes a message along to the appropriate view to inform the user
     * @param message string to display
     */
    @Override
    void notifyView(final String message) {
        view.displayMessage(message);
    }

    /**
     * Passes a message along to the appropriate view to inform the user about a player made move
     * @param row of the new move
     * @param column of the new move
     */
    @Override
    void notifyViewMoveMessage(final int row, final int column) {
        notifyView("Player " + model.getPlayerTurn().getFriendlyName() + " move at (" + row + ", " + column + ")");
    }

    /**
     * Allows subclasses to process input appropriately according to the currently used view
     * @return a valid location to place a disc for the current players turn
     */
    @Override
    InputLocation receiveUserInput() {
        while(true){
            String input = view.waitForUserInput();

            if(isValidInput(input)){
                return parseInput(input);
            }

            view.displayMessage("That is not a valid location.");
        }
    }

    /**
     * Defines custom game termination behavior according to the currently used view
     */
    @Override
    void gameOver() {
        view.displayMessage("Game over! Final score: Black: " + model.getPlayerScore(Disc.Color.BLACK) + ", White: " +
                model.getPlayerScore(Disc.Color.WHITE));

    }

}
