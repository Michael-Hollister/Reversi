package edu.utdallas.msh150130.cs2336.reversi;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.controller.ConsoleController;
import edu.utdallas.msh150130.cs2336.reversi.controller.Controller;
import edu.utdallas.msh150130.cs2336.reversi.controller.GuiController;
import edu.utdallas.msh150130.cs2336.reversi.model.ComputerDifficulty;
import edu.utdallas.msh150130.cs2336.reversi.model.Model;
import edu.utdallas.msh150130.cs2336.reversi.model.PlayerSetup;
import edu.utdallas.msh150130.cs2336.reversi.view.View;

/**
 * Main application entry class
 */
public final class Reversi {
    //region "Private fields"
    public static final String version = "1.1";

    private static abstract class CLIArguments{
        private static final String HELP_SHORT = "-h";
        private static final String HELP_LONG = "--help";
        private static final String VERSION = "--version";
        private static final String GUI = "--gui";
        private static final String CONSOLE = "--console";
        private static final String PVC_SHORT = "-pvc";
        private static final String PVC_LONG = "--player-vs-computer";
        private static final String CVC_SHORT = "-cvc";
        private static final String CVC_LONG = "--computer-vs-computer";
        private static final String PVP_SHORT = "-pvp";
        private static final String PVP_LONG = "--player-vs-player";
    }

    // Error codes
    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_REINITIALIZATION = -1;
    private static final int EXIT_CODE_IMPROPER_USAGE = -2;
    private static final int EXIT_CODE_INVALID_BOARD_SIZE = -3;

    // Default game parameters
    private static View.Mode viewMode = View.Mode.GUI;
    private static int boardSize = 8;
    private static PlayerSetup gameSetup = PlayerSetup.PLAYER_VS_COMPUTER;
    private static ComputerDifficulty difficulty = ComputerDifficulty.EASY;
    //endregion

    /**
     * Application entry
     * @param args not used
     */
    public static void main(String[] args){
        parseCommandLineArguments(args);
        final Model gameModel = new Model(boardSize, gameSetup, difficulty);
        Controller gameController = null;

        switch (viewMode) {
            case CONSOLE:
                gameController = new ConsoleController(gameModel);
                break;
            case GUI:
                gameController = new GuiController(gameModel);
                new Thread((Runnable)gameController).start();

                // Don't start game until GUI is fully constructed
                synchronized (gameController){
                    try {
                        gameController.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
        }

        gameController.startGame();
    }

    private static void parseCommandLineArguments(final String[] args){
        assert args != null : "args should not be null";

        // Prevents multiple initializations of the same parameters
        boolean isViewModeInitialized = false;
        boolean isBoardSizeInitialized = false;
        boolean isGameSetupInitialized = false;
        boolean isComputerDifficultyInitialized = false;

        for(String argument : args){
            if(argument.toLowerCase().matches("--size=\\d+")){
                if(isBoardSizeInitialized){
                    exit(EXIT_CODE_IMPROPER_USAGE);
                }

                try{

                    boardSize = Integer.parseInt(argument.substring(7));
                    if(boardSize < 3){
                        exit(EXIT_CODE_INVALID_BOARD_SIZE);
                    }

                    isBoardSizeInitialized = true;
                }
                catch(NumberFormatException e){
                    exit(EXIT_CODE_INVALID_BOARD_SIZE);
                }
            }
            else if(argument.toLowerCase().matches("--difficulty=(easy|medium|hard)")){
                if(isComputerDifficultyInitialized){
                    exit(EXIT_CODE_IMPROPER_USAGE);
                }

                difficulty = ComputerDifficulty.valueOf(argument.substring(13).toUpperCase());
                isComputerDifficultyInitialized = true;
            }
            else{
                switch(argument.toLowerCase()){
                    case CLIArguments.HELP_SHORT:
                    case CLIArguments.HELP_LONG:
                        if(isViewModeInitialized || isBoardSizeInitialized || isGameSetupInitialized){
                            exit(EXIT_CODE_IMPROPER_USAGE);
                        }
                        else{
                            exit(EXIT_CODE_SUCCESS);
                        }
                        break;

                    case CLIArguments.VERSION:
                        if(isViewModeInitialized || isBoardSizeInitialized || isGameSetupInitialized){
                            exit(EXIT_CODE_IMPROPER_USAGE);
                        }

                        System.out.println("Reversi version: " + version);
                        System.exit(EXIT_CODE_SUCCESS);
                        break;

                    case CLIArguments.GUI:
                        if(isViewModeInitialized){
                            exit(EXIT_CODE_REINITIALIZATION);
                        }

                        viewMode = View.Mode.GUI;
                        isViewModeInitialized = true;
                        break;

                    case CLIArguments.CONSOLE:
                        if(isViewModeInitialized){
                            exit(EXIT_CODE_REINITIALIZATION);
                        }

                        viewMode = View.Mode.CONSOLE;
                        isViewModeInitialized = true;
                        break;

                    case CLIArguments.PVC_SHORT:
                    case CLIArguments.PVC_LONG:
                        if(isGameSetupInitialized){
                            exit(EXIT_CODE_REINITIALIZATION);
                        }

                        gameSetup = PlayerSetup.PLAYER_VS_COMPUTER;
                        isGameSetupInitialized = true;
                        break;

                    case CLIArguments.CVC_SHORT:
                    case CLIArguments.CVC_LONG:
                        if(isGameSetupInitialized){
                            exit(EXIT_CODE_REINITIALIZATION);
                        }

                        gameSetup = PlayerSetup.COMPUTER_VS_COMPUTER;
                        isGameSetupInitialized = true;
                        break;

                    case CLIArguments.PVP_SHORT:
                    case CLIArguments.PVP_LONG:
                        if(isGameSetupInitialized){
                            exit(EXIT_CODE_REINITIALIZATION);
                        }

                        gameSetup = PlayerSetup.PLAYER_VS_PLAYER;
                        isGameSetupInitialized = true;
                        break;

                    default:
                        exit(EXIT_CODE_IMPROPER_USAGE);
                        break;
                }
            }
        }
    }

    /**
     * Displays help message and terminates the application
     * @param exitCode indicates the reason of termination
     */
    private static void exit(final int exitCode){
        displayUsageHelp();
        System.exit(exitCode);
    }

    private static void displayUsageHelp(){
        System.out.printf("Usage:\n");
        System.out.printf("\t%-13s %-13s\n","java Reversi","[--gui | --console] [--size=<int>] [--difficulty=<easy | medium | hard>]");
        System.out.printf("\t%-13s %-13s\n","","[--player-vs-computer | --computer-vs-computer | --player-vs-player]");
        System.out.printf("\t%-13s %-13s\n","java Reversi","(" + CLIArguments.HELP_SHORT + " | " + CLIArguments.HELP_LONG + ")");
        System.out.printf("\t%-13s %-13s\n","java Reversi",CLIArguments.VERSION);
        System.out.printf("Options:\n");
        System.out.printf("\t%-36s %-36s\n",CLIArguments.GUI,"Lanuch the game using the GUI [default]");
        System.out.printf("\t%-36s %-36s\n",CLIArguments.CONSOLE,"Lanuch the game using the CLI");
        System.out.printf("\t%-36s %-36s\n","--size=<int>","Board game size that is an integer greater than 2 [default = 8]");
        System.out.printf("\t%-36s %-36s\n",CLIArguments.PVC_SHORT + "," + CLIArguments.PVC_LONG,"Play the game versus the computer [default]");
        System.out.printf("\t%-36s %-36s\n",CLIArguments.CVC_SHORT + "," + CLIArguments.CVC_LONG,"Watch the game of computer versus the computer");
        System.out.printf("\t%-36s %-36s\n",CLIArguments.PVP_SHORT + "," + CLIArguments.PVP_LONG,"Play the game versus another player");
        System.out.printf("\t%-36s %-36s\n","--difficulty=<easy | medium | hard>","Sets the computer difficulty [default = easy]");
    }
}
