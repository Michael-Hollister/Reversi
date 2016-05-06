package edu.utdallas.msh150130.cs2336.reversi.model;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

/**
 * Represents a game disc for placement on the game board
 */
public final class Disc {
    private Color color;

    public enum Color{
        BLACK("Black"),
        WHITE("White");

        private final String friendlyName;

        Color(final String friendlyName){
            this.friendlyName = friendlyName;
        }

        public String getFriendlyName(){
            return this.friendlyName;
        }

        public static Color getOppositeColor(final Color color){
            return (color == BLACK) ? WHITE : BLACK;
        }
    }

    /**
     * Constructs a game disc with the specified color
     * @param discColor color the disc represents
     * @throws IllegalArgumentException if {@code discColor} is null
     */
    public Disc(final Color discColor){
        if(discColor == null){
            throw new IllegalArgumentException("discColor should not be null");
        }

        this.color = discColor;
    }

    public Color getColor() {
        return color;
    }

}
