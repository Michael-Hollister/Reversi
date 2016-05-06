package edu.utdallas.msh150130.cs2336.reversi.model;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

/**
 * Represents the player setup for the game
 */
public enum PlayerSetup{
    PLAYER_VS_COMPUTER("Player vs Computer"),
    COMPUTER_VS_COMPUTER("Computer vs Computer"),
    PLAYER_VS_PLAYER("Player vs Player");

    private final String friendlyName;

    PlayerSetup(final String friendlyName){
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName(){
        return this.friendlyName;
    }
}