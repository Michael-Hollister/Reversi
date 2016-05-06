package edu.utdallas.msh150130.cs2336.reversi;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.model.*;
import org.junit.Assert;
import org.junit.Test;

import edu.utdallas.msh150130.cs2336.reversi.model.Disc.Color;

import java.lang.reflect.Field;

/**
 * Created by Michael on 3/19/2016.
 */
public class ModelTest {
    @Test
    public void isValidMoveTest() {

        Model model = new Model(6, PlayerSetup.PLAYER_VS_COMPUTER, ComputerDifficulty.EASY);
        Disc[][] board1 = {
                {null, null, null, null, null, null},
                {null, new Disc(Color.WHITE), null,                  new Disc(Color.BLACK), null, new Disc(Color.BLACK)},
                {null, null,                  new Disc(Color.WHITE), new Disc(Color.BLACK), null, null},
                {null, new Disc(Color.WHITE), new Disc(Color.BLACK), new Disc(Color.WHITE), new Disc(Color.WHITE), null},
                {null, new Disc(Color.BLACK), new Disc(Color.BLACK), null, null, null},
                {null, null, null, null, null, null}};
        assignBoardToModel(model,board1);

        // Cases: (capital = placement, lower = placed, n = null)
        Assert.assertFalse(model.isValidMove(3,1,Color.BLACK)); // Move on existing disc
        Assert.assertFalse(model.isValidMove(1,3,Color.WHITE)); // Wnb, ->, invalid
        Assert.assertFalse(model.isValidMove(3,5,Color.WHITE)); // Wwbw, <-, invalid
        Assert.assertFalse(model.isValidMove(1,4,Color.WHITE)); // Wbnw, <-, invalid
        Assert.assertFalse(model.isValidMove(4,3,Color.WHITE)); // Wbb, <-, invalid
        Assert.assertTrue(model.isValidMove(2,4,Color.WHITE)); // Wbw, <-, valid
        Assert.assertTrue(model.isValidMove(5,2,Color.WHITE)); // Wbbw, ^, valid

        Disc[][] board2 = {
                { null, null, null, null },
                { null, new Disc(Color.WHITE), new Disc(Color.BLACK), null },
                { null, new Disc(Color.BLACK), new Disc(Color.WHITE), null },
                { null, null, null, null }};
        assignBoardToModel(model,board2);

        Assert.assertTrue("Valid: Path S", model.isValidMove(0,1,Color.BLACK));
        Assert.assertTrue("Valid: Path N", model.isValidMove(3,1,Color.WHITE));
        Assert.assertTrue("Valid: Path W", model.isValidMove(1,3,Color.WHITE));
        Assert.assertTrue("Valid: Path E", model.isValidMove(2,0,Color.WHITE));

        Disc[][] board3 = {
                { null, null, null, null },
                { null, new Disc(Color.WHITE), new Disc(Color.WHITE), null },
                { null, new Disc(Color.BLACK), new Disc(Color.BLACK), null },
                { null, null, null, null }};
        assignBoardToModel(model,board3);

        Assert.assertTrue("Valid: Path NE", model.isValidMove(3,0,Color.WHITE));
        Assert.assertTrue("Valid: Path NW", model.isValidMove(3,3,Color.WHITE));
        Assert.assertTrue("Valid: Path SE", model.isValidMove(0,0,Color.BLACK));
        Assert.assertTrue("Valid: Path SW", model.isValidMove(0,3,Color.BLACK));

    }

    private void assignBoardToModel(Model model, final Disc[][] board) {
        try {
            Field modelBoard = Model.class.getDeclaredField("gameBoard");
            modelBoard.setAccessible(true);
            modelBoard.set(model, new Board(board));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

