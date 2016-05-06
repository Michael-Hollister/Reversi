package edu.utdallas.msh150130.cs2336.reversi;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.model.ComputerDifficulty;
import edu.utdallas.msh150130.cs2336.reversi.model.PlayerSetup;
import edu.utdallas.msh150130.cs2336.reversi.view.View;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Michael on 3/12/2016.
 */
public class ReversiTest {
    @Test
    public void parseCommandLineArgumentsTestGUI(){
        parseCommandLineArgumentsTest(new String[]{ "--gui" },View.Mode.GUI,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestConsole(){
        parseCommandLineArgumentsTest(new String[]{ "--console" },View.Mode.CONSOLE,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestSize(){
        parseCommandLineArgumentsTest(new String[]{ "--size=5" },null,5,null,null);
        parseCommandLineArgumentsTest(new String[]{ "--size=8" },null,8,null,null);
        parseCommandLineArgumentsTest(new String[]{ "--size=3" },null,3,null,null);
        parseCommandLineArgumentsTest(new String[]{ "--size=235" },null,235,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestPVC(){
        parseCommandLineArgumentsTest(new String[]{ "--player-vs-computer" },null,0,PlayerSetup.PLAYER_VS_COMPUTER,null);
    }

    @Test
    public void parseCommandLineArgumentsTestCVC(){
        parseCommandLineArgumentsTest(new String[]{ "--computer-vs-computer" },null,0,PlayerSetup.COMPUTER_VS_COMPUTER,null);
    }

    @Test
    public void parseCommandLineArgumentsTestPVP(){
        parseCommandLineArgumentsTest(new String[]{ "--player-vs-player" },null,0,PlayerSetup.PLAYER_VS_PLAYER,null);
    }

    @Test
    public void parseCommandLineArgumentsTestDifficulty(){
        parseCommandLineArgumentsTest(new String[]{ "--difficulty=easy" },null,0,null,ComputerDifficulty.EASY);
        parseCommandLineArgumentsTest(new String[]{ "--difficulty=meDium" },null,0,null,ComputerDifficulty.MEDIUM);
        parseCommandLineArgumentsTest(new String[]{ "--difficulty=HARD" },null,0,null,ComputerDifficulty.HARD);
    }

    @Test
    public void parseCommandLineArgumentsTestCombo1(){
        parseCommandLineArgumentsTest(new String[]{ "--gui", "--size=4", "--player-vs-player" },
                View.Mode.GUI,4,PlayerSetup.PLAYER_VS_PLAYER,ComputerDifficulty.EASY);
    }

    @Test
    public void parseCommandLineArgumentsTestCombo2(){
        parseCommandLineArgumentsTest(new String[]{ "--size=6", "--player-vs-computer", "--difficulty=medium" },
                null,6,PlayerSetup.PLAYER_VS_COMPUTER,ComputerDifficulty.MEDIUM);
    }

    @Test
    public void parseCommandLineArgumentsTestCombo3(){
        parseCommandLineArgumentsTest(new String[]{ "--console", "--computer-vs-computer", "--difficulty=hard" },
                View.Mode.CONSOLE,0,PlayerSetup.COMPUTER_VS_COMPUTER,ComputerDifficulty.HARD);
    }

    /**
     * Verifies that the input parameters are set.
     * @param args
     * @param viewModeValidation not checked if null
     * @param boardSizeVaildation if < 2, then it is not checked
     * @param gameSetupValidation not checked if null
     */
    protected void parseCommandLineArgumentsTest(String[] args, final View.Mode viewModeValidation,
                                                 final int boardSizeVaildation, final PlayerSetup gameSetupValidation,
                                                 final ComputerDifficulty difficultyVaildation){
        Field viewMode = null;
        Field boardSize = null;
        Field gameSetup = null;
        Field difficulty = null;
        Method parseCommandLineArguments = null;

        try{
            viewMode = Reversi.class.getDeclaredField("viewMode");
            boardSize = Reversi.class.getDeclaredField("boardSize");
            gameSetup = Reversi.class.getDeclaredField("gameSetup");
            difficulty = Reversi.class.getDeclaredField("difficulty");
            parseCommandLineArguments = Reversi.class.getDeclaredMethod("parseCommandLineArguments", String[].class);
        }
        catch (NoSuchFieldException | NoSuchMethodException e){
            e.printStackTrace();
        }

        viewMode.setAccessible(true);
        boardSize.setAccessible(true);
        gameSetup.setAccessible(true);
        difficulty.setAccessible(true);
        parseCommandLineArguments.setAccessible(true);

        try {
            // Setup
            viewMode.set(null, View.Mode.CONSOLE);
            boardSize.set(null, 8);
            gameSetup.set(null, PlayerSetup.PLAYER_VS_COMPUTER);
            difficulty.set(null, ComputerDifficulty.EASY);

            parseCommandLineArguments.invoke(null,new Object[]{ args });

            if(viewModeValidation != null){
                Assert.assertTrue(viewModeValidation == viewMode.get(null));
            }
            if(boardSizeVaildation > 2){
                Assert.assertTrue(boardSizeVaildation == boardSize.getInt(null));
            }
            if(gameSetupValidation != null){
                Assert.assertTrue(gameSetupValidation == gameSetup.get(null));
            }
            if(difficultyVaildation != null){
                Assert.assertTrue(difficultyVaildation == difficulty.get(null));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

}
