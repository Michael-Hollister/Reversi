package edu.utdallas.msh150130.cs2336.reversi.view.gui;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.model.Disc;
import javafx.animation.Transition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Defines the algorithm for switching between black and white discs
 */
public class CaptureTransition extends Transition {
    private final Circle animatedDisc;
    private final Disc.Color originalColor;

    /**
     * Constructs a fading transition from one color to another
     * @param animatedDisc the graphics object to animate
     * @param animationDuration the duration in milliseconds
     * @throws IllegalArgumentException if {@code animatedDisc} is null or {@code animationDuration} is less than zero
     */
    public CaptureTransition(final Circle animatedDisc, final int animationDuration){
        if(animatedDisc == null){
            throw new IllegalArgumentException("animatedDisc should not be null");
        }
        if(animationDuration < 0){
            throw new IllegalArgumentException("animationDuration can't be negative");
        }

        this.animatedDisc = animatedDisc;
        this.originalColor = (Disc.Color)animatedDisc.getUserData();
        setCycleDuration(new Duration(animationDuration));
    }

    /**
     * Helper function for returning greyscale CSS colors
     * @param value the integer value of a single color
     * @return the greyscale CSS color string
     */
    private String hexColorString(final int value){
        assert value >= 0 : "value should not be < 0";
        String valueString = Integer.toHexString(value);

        if(value <= 15){
            valueString = "0" + valueString;
        }

        return "#" + valueString + valueString + valueString;
    }

    /**
     * Defines the algorithm for the capture transition
     * @param frac The relative position
     */
    @Override
    protected void interpolate(double frac) {
        int edgeColor = 0;
        int lowHighColor = 0;
        int middleColor = 0;

        switch(originalColor) {
            case BLACK:
                edgeColor = (int)(frac * 0xFF);
                lowHighColor = (int)(frac * (0xDD - 0x20)) + 0x20;
                middleColor = (int)(frac * (0xBB - 0x40)) + 0x40;
                break;
            case WHITE:
                edgeColor = 0xFF - (int)(frac * 0xFF);
                lowHighColor = 0xDD - (int)(frac*(0xDD - 0x20));
                middleColor = 0xBB - (int)(frac*(0xBB - 0x40));
                break;
        }

        String edgeColorString = hexColorString(edgeColor);
        String edgeLowHighColor = hexColorString(lowHighColor);
        String edgeMiddleColor = hexColorString(middleColor);

        this.animatedDisc.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, " +
                edgeColorString + " 0%, " + edgeLowHighColor + " 35%, " + edgeMiddleColor +
                " 50%, " + edgeLowHighColor + " 65%, " + edgeColorString + " 100%); " +
                "-fx-effect: dropshadow(gaussian, darkgray, 10, 0.2, 4px, 4px);");
    }
}
