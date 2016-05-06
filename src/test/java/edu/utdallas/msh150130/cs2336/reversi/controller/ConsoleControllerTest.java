package edu.utdallas.msh150130.cs2336.reversi.controller;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.model.ComputerDifficulty;
import edu.utdallas.msh150130.cs2336.reversi.model.Model;
import edu.utdallas.msh150130.cs2336.reversi.model.PlayerSetup;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by Michael on 3/28/2016.
 */
public class ConsoleControllerTest {

    @Test
    public void isValidInput() throws Exception {
        Model model = new Model(4, PlayerSetup.PLAYER_VS_COMPUTER, ComputerDifficulty.EASY);
        ConsoleController controller = new ConsoleController(model);

        Method isValidInput = ConsoleController.class.getDeclaredMethod("isValidInput", String.class);
        isValidInput.setAccessible(true);
        Assert.assertTrue("(1,0)",(boolean)isValidInput.invoke(controller,"(1,0)"));
        Assert.assertTrue("(0,1)",(boolean)isValidInput.invoke(controller,"(0,1)"));
        Assert.assertTrue("2,3",(boolean)isValidInput.invoke(controller,"2,3"));

        Assert.assertFalse("asdf",(boolean)isValidInput.invoke(controller,"asdf"));
        Assert.assertFalse("(-23,34)",(boolean)isValidInput.invoke(controller,"(-23,34)"));
        Assert.assertFalse("(2134172938471209384719208374,12093847129837409128374)",(boolean)isValidInput.invoke(controller,"(2134172938471209384719208374,12093847129837409128374)"));
        Assert.assertFalse("abc,3",(boolean)isValidInput.invoke(controller,"abc,3"));
        Assert.assertFalse("1,1",(boolean)isValidInput.invoke(controller,"1,1"));
        Assert.assertFalse("(0,2)",(boolean)isValidInput.invoke(controller,"(0,2)"));
    }
}