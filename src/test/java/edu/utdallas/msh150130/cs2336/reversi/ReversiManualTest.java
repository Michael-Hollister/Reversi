package edu.utdallas.msh150130.cs2336.reversi;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import org.junit.Test;

/**
 * Created by Michael on 3/16/2016.
 */
public class ReversiManualTest extends ReversiTest {
    @Test
    public void parseCommandLineArgumentsTestHelpShort(){
        parseCommandLineArgumentsTest(new String[]{ "-h"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestHelpLong(){
        parseCommandLineArgumentsTest(new String[]{ "--help"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestHelpInvalid1(){
        parseCommandLineArgumentsTest(new String[]{ "--version", "--help"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestHelpInvalid2(){
        parseCommandLineArgumentsTest(new String[]{ "--abcdef"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestVersion(){
        parseCommandLineArgumentsTest(new String[]{ "--version"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalid1(){
        parseCommandLineArgumentsTest(new String[]{ "--gui", "--gui"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalid2(){
        parseCommandLineArgumentsTest(new String[]{ "--size=3", "--size=5"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalid3(){
        parseCommandLineArgumentsTest(new String[]{ "--player-vs-player", "--player-vs-computer"},null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalidSize1(){
        parseCommandLineArgumentsTest(new String[]{ "--size=2" },null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalidSize2(){
        parseCommandLineArgumentsTest(new String[]{ "--size=-2" },null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalidDifficulty1(){
        parseCommandLineArgumentsTest(new String[]{ "--difficulty=EZ" },null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalidDifficulty2(){
        parseCommandLineArgumentsTest(new String[]{ "--difficulty=Easyer" },null,0,null,null);
    }

    @Test
    public void parseCommandLineArgumentsTestInvalidDifficulty3(){
        parseCommandLineArgumentsTest(new String[]{ "--difficulty=med" },null,0,null,null);
    }
}
