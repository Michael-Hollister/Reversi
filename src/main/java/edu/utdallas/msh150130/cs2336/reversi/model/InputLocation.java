package edu.utdallas.msh150130.cs2336.reversi.model;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

/**
 * Represents a location on the game board
 */
public final class InputLocation{
    private final int row;
    private final int column;

    public InputLocation(final int row, final int column){
        this.row = row;
        this.column = column;
    }

    public int getRow(){
        return row;
    }
    public int getColumn(){
        return column;
    }
}
