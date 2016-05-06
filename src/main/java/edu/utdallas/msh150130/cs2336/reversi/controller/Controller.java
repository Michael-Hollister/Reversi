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
import edu.utdallas.msh150130.cs2336.reversi.model.PlayerSetup;

/**
 * Handles game input and high level control flow
 */
public abstract class Controller {
    /**
     * Model that the controller is communicating with
     */
    Model model;

    /**
     * Constructs a {@code Controller} that handles game input and high level control flow
     * @param modelToControl model that the controller is communicating with
     * @throws IllegalArgumentException if {@code modelToControl} is null
     */
    Controller(Model modelToControl){
        setGameModel(modelToControl);
    }

    /**
     * Sets the controller to control a new model
     * @param modelToControl model that the controller is communicating with
     * @throws IllegalArgumentException if {@code modelToControl} is null
     */
    void setGameModel(final Model modelToControl){
        if(modelToControl == null){
            throw new IllegalArgumentException("modelToControl should not be null");
        }

        this.model = modelToControl;
    }

    /**
     * Main game loop
     */
    public final void startGame(){
        boolean isGameOver = false;

        while(!isGameOver){
            InputLocation location;

            // Player input
            if(model.getPlayerSetup() == PlayerSetup.COMPUTER_VS_COMPUTER ||
                    (model.getPlayerSetup() == PlayerSetup.PLAYER_VS_COMPUTER) && (model.getPlayerTurn() == Disc.Color.WHITE)){
                location = model.receiveComputerInput();
            }
            else{
                location = receiveUserInput();

                // Null references are used to pass moves
                if(location == null){
                    continue;
                }
            }

            model.placeDisc(location.getRow(),location.getColumn());
            notifyViewMoveMessage(location.getRow(),location.getColumn());
            model.changeTurn();

            if(!model.validMoveExists(Disc.Color.WHITE) && !model.validMoveExists(Disc.Color.BLACK)){
                notifyView("No valid move exists for both players.");
                isGameOver = true;
            }
            else if(!model.validMoveExists(model.getPlayerTurn())){
                notifyView("Player " + model.getPlayerTurn().getFriendlyName() + " passes. No valid move exists.");
                model.changeTurn();
            }
        }

        gameOver();
    }

    /**
     * Passes a message along to the appropriate view to inform the user
     * @param message string to display
     */
    abstract void notifyView(final String message);


    /**
     * Passes a message along to the appropriate view to inform the user about a player made move
     * @param row of the new move
     * @param column of the new move
     */
    abstract void notifyViewMoveMessage(final int row, final int column);

    /**
     * Allows subclasses to process input appropriately according to the currently used view
     * @return a valid location to place a disc for the current players turn
     */
    abstract InputLocation receiveUserInput();

    /**
     * Defines custom game termination behavior according to the currently used view
     */
    abstract void gameOver();
}
