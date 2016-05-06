package edu.utdallas.msh150130.cs2336.reversi.model;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

/**
 * Represents the type of algorithm computer players use to make moves
 */
public enum ComputerDifficulty {
    /**
     * Computer randomly chooses a valid move
     */
    EASY("Easy"),

    /**
     * Computer 50% of the time chooses the optimal move
     */
    MEDIUM("Medium"),

    /**
     * Computer always chooses the optimal move
     */
    HARD("Hard");

    private final String friendlyName;

    ComputerDifficulty(final String friendlyName){
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName(){
        return this.friendlyName;
    }
}
