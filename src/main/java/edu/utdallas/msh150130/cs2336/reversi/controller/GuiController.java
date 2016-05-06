package edu.utdallas.msh150130.cs2336.reversi.controller;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.Reversi;
import edu.utdallas.msh150130.cs2336.reversi.model.*;
import edu.utdallas.msh150130.cs2336.reversi.view.gui.CaptureTransition;
import edu.utdallas.msh150130.cs2336.reversi.view.gui.GuiView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handlers user input
 */
public final class GuiController extends Controller implements Initializable, Runnable {

    //region GUI References
    @FXML
    private VBox vBoxWindowContentParent;

    //region Menu Items
    @FXML
    private MenuItem menuItemGameNewGame;

    @FXML
    private MenuItem menuItemGameExit;

    //region Options
    @FXML
    private MenuItem menuItemOptionsSetBoardSize;


    @FXML
    private RadioMenuItem radioMenuItemOptionsPlayerSetupPVC;

    @FXML
    private RadioMenuItem radioMenuItemOptionsPlayerSetupCVC;

    @FXML
    private RadioMenuItem radioMenuItemOptionsPlayerSetupPVP;


    @FXML
    private RadioMenuItem radioMenuItemOptionsDifficultyEasy;

    @FXML
    private RadioMenuItem radioMenuItemOptionsDifficultyMedium;

    @FXML
    private RadioMenuItem radioMenuItemOptionsDifficultyHard;
    //endregion

    @FXML
    private MenuItem menuItemHelpAbout;
    //endregion

    @FXML
    private Pane board;

    @FXML
    private Label labelMessage;

    @FXML
    private Label labelScore;
    //endregion

    private final double CELL_PADDING = 6.0;
    private double discRadius = 0;

    // Board GUI elements
    private Line[] verticalGridLines;
    private Line[] horizontalGridLines;
    private Circle[][] discs;
    private Rectangle moveIndicator;

    /**
     * Used when new games are created
     */
    private int newGameBoardSize;
    private InputLocation userInputLocation;

    /**
     * Determines how the controller continues the game loop
     */
    private enum ModelThreadNotification{
        MOVE,PASS_MOVE
    }

    private ModelThreadNotification modelThreadNotification = null;

    /**
     * Constructs a {@code GuiController} that handles game input
     * @param modelToControl model that the controller is communicating with
     * @throws IllegalArgumentException if {@code modelToControl} is null
     */
    public GuiController(final Model modelToControl){
        super(modelToControl);
    }

    /**
     * Places a disc on the visible grid
     * @param graphicsObject object to show
     * @param disc model disc reference
     */
    private void placeDisc(final Circle graphicsObject, final Disc disc){
        assert graphicsObject != null : "graphicsobject should not be null";
        assert disc != null : "disc should not be null";

        graphicsObject.setVisible(true);

        if(disc.getColor() == Disc.Color.BLACK){
            graphicsObject.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, " +
                    "black 0%, #202020 35%, #404040 50%, #202020 65%, black 100%); " +
                    "-fx-effect: dropshadow(gaussian, darkgray, 10, 0.2, 4px, 4px);");

        }
        else if(disc.getColor() == Disc.Color.WHITE){
            graphicsObject.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, " +
                    "white 0%, #DDDDDD 35%, #BBBBBB 50%, #DDDDDD 65%, white 100%); " +
                    "-fx-effect: dropshadow(gaussian, darkgray, 10, 0.2, 4px, 4px);");
        }

        graphicsObject.setUserData(disc.getColor());
    }

    /**
     * Updates the GUI when moves are performed
     */
    public void updateGui(){
        // Disc resize
        for(int rowIndex = 0; rowIndex < model.getGameBoard().getBoardSize(); rowIndex++){
            for(int columnIndex = 0; columnIndex < model.getGameBoard().getBoardSize(); columnIndex++){
                Disc disc = model.getGameBoard().getDiscAtLocation(rowIndex,columnIndex);

                if(disc != null){
                    Transition transition = null;
                    Disc.Color color = (Disc.Color)discs[rowIndex][columnIndex].getUserData();

                    if(!discs[rowIndex][columnIndex].isVisible()){
                        // Disc placement
                        transition = new FadeTransition(new Duration(500),discs[rowIndex][columnIndex]);
                        ((FadeTransition)transition).setFromValue(0);
                        ((FadeTransition)transition).setToValue(1);
                    }
                    else if(color != null && color != disc.getColor()){
                        // Disc capture
                        transition = new CaptureTransition(discs[rowIndex][columnIndex],500);
                    }

                    placeDisc(discs[rowIndex][columnIndex],disc);
                    if(transition != null){
                        transition.play();
                    }

                    Platform.runLater(() -> labelScore.setText("Score: Black: " + model.getPlayerScore(Disc.Color.BLACK)
                            + ", White: " + model.getPlayerScore(Disc.Color.WHITE)));
                }
            }
        }
    }

    /**
     * Resizes all dynamic GUI nodes
     */
    public void onMainWindowResize(){
        // Centering board
        double boardHeight = vBoxWindowContentParent.getHeight() - board.getLayoutY() - 2*CELL_PADDING;
        board.resize(boardHeight,boardHeight);
        board.setTranslateX((vBoxWindowContentParent.getWidth() - boardHeight)/4);

        // Height is used to calculate radius since it looks nicer on wide screens
        discRadius = ((board.getHeight() / model.getGameBoard().getBoardSize()) / 2) - CELL_PADDING;

        // Grid resize
        for(int index = 0; index <= model.getGameBoard().getBoardSize(); index++){
            horizontalGridLines[index].setStartX(board.getTranslateX());
            horizontalGridLines[index].setStartY(index*2*(discRadius + CELL_PADDING));
            horizontalGridLines[index].setEndX(board.getTranslateX() + board.getWidth());
            horizontalGridLines[index].setEndY(index*2*(discRadius + CELL_PADDING));

            verticalGridLines[index].setStartX(board.getTranslateX() + index*2*(discRadius + CELL_PADDING));
            verticalGridLines[index].setStartY(0);
            verticalGridLines[index].setEndX(board.getTranslateX() + index*2*(discRadius + CELL_PADDING));
            verticalGridLines[index].setEndY(board.getHeight());
        }

        // Disc resize
        for(int rowIndex = 0; rowIndex < model.getGameBoard().getBoardSize(); rowIndex++){
            for(int columnIndex = 0; columnIndex < model.getGameBoard().getBoardSize(); columnIndex++){
                discs[rowIndex][columnIndex].setTranslateX(board.getTranslateX() + (columnIndex*2*(discRadius + CELL_PADDING) + CELL_PADDING) + discRadius);
                discs[rowIndex][columnIndex].setTranslateY((rowIndex*2*(discRadius + CELL_PADDING) + CELL_PADDING) + discRadius);
                discs[rowIndex][columnIndex].setRadius(discRadius);
            }
        }

        moveIndicator.setWidth(discRadius*2+CELL_PADDING*2);
        moveIndicator.setHeight(discRadius*2+CELL_PADDING*2);
    }

    /**
     * Updates the valid move indicator
     * @param mouseEvent not used
     */
    public void onMouseMoved(MouseEvent mouseEvent){
        final double mouseX = mouseEvent.getSceneX() - 2*board.getTranslateX();
        final double mouseY = mouseEvent.getSceneY() - board.getLayoutY();
        Node intersectedNode = mouseEvent.getPickResult().getIntersectedNode();

        if(model.getPlayerSetup() != PlayerSetup.COMPUTER_VS_COMPUTER &&
                (intersectedNode == board || intersectedNode instanceof Rectangle)){
            for(int row = 0; row < model.getGameBoard().getBoardSize(); row++){
                for(int column = 0; column < model.getGameBoard().getBoardSize(); column++){
                    double boundingBoxStartX = column*2*(discRadius + CELL_PADDING);
                    double boundingBoxStartY = row*2*(discRadius + CELL_PADDING);
                    double boundingBoxEndX = boundingBoxStartX + 2*(discRadius + CELL_PADDING);
                    double boundingBoxEndY = boundingBoxStartY + 2*(discRadius + CELL_PADDING);

                    if(mouseX >= boundingBoxStartX && mouseX <= boundingBoxEndX &&
                            mouseY >= boundingBoxStartY && mouseY <= boundingBoxEndY){

                        if(model.isValidMove(row,column,model.getPlayerTurn()) && ((
                                (model.getPlayerSetup() == PlayerSetup.PLAYER_VS_COMPUTER &&
                                        model.getPlayerTurn() == Disc.Color.BLACK)) ||
                                model.getPlayerSetup() == PlayerSetup.PLAYER_VS_PLAYER)){
                            moveIndicator.setTranslateX(board.getTranslateX() + boundingBoxStartX);
                            moveIndicator.setTranslateY(boundingBoxStartY);
                            moveIndicator.setVisible(true);
                        }
                        else{
                            moveIndicator.setVisible(false);
                        }

                        break;
                    }
                }
            }
        }
        else{
            moveIndicator.setVisible(false);
        }
    }

    /**
     * Places disc if the user clicked a valid move location
     * @param mouseEvent not used
     */
    public synchronized void onMouseClicked(MouseEvent mouseEvent){
        final double mouseX = mouseEvent.getSceneX() - 2*board.getTranslateX();
        final double mouseY = mouseEvent.getSceneY() - board.getLayoutY();

        if(mouseEvent.getPickResult().getIntersectedNode() instanceof Rectangle){
            for(int row = 0; row < model.getGameBoard().getBoardSize(); row++){
                for(int column = 0; column < model.getGameBoard().getBoardSize(); column++){
                    double boundingBoxStartX = column*2*(discRadius + CELL_PADDING);
                    double boundingBoxStartY = row*2*(discRadius + CELL_PADDING);
                    double boundingBoxEndX = boundingBoxStartX + 2*(discRadius + CELL_PADDING);
                    double boundingBoxEndY = boundingBoxStartY + 2*(discRadius + CELL_PADDING);

                    if(mouseX >= boundingBoxStartX && mouseX <= boundingBoxEndX &&
                            mouseY >= boundingBoxStartY && mouseY <= boundingBoxEndY){
                        moveIndicator.setVisible(false);

                        if(model.isValidMove(row,column,model.getPlayerTurn())){
                            userInputLocation = new InputLocation(row,column);
                            modelThreadNotification = ModelThreadNotification.MOVE;
                            notify();
                        }

                        break;
                    }
                }
            }
        }
    }

    //region Menu Item Event Handlers

    synchronized private void onGameNewGameClickedEventHandler(ActionEvent event){
        final Model gameModel = new Model(newGameBoardSize, model.getPlayerSetup(), model.getComputerDifficulty());
        this.setGameModel(gameModel);

        board.getChildren().remove(0,board.getChildren().size());
        verticalGridLines = new Line[model.getGameBoard().getBoardSize() + 1];
        horizontalGridLines = new Line[model.getGameBoard().getBoardSize() + 1];
        discs = new Circle[model.getGameBoard().getBoardSize()][model.getGameBoard().getBoardSize()];

        // Need to update the observers
        GuiView.updateModel(model);
        onShownEventHandler(null);
        updateGui();

        modelThreadNotification = ModelThreadNotification.PASS_MOVE;
        notify();
    }

    public void onCloseEventHandler(Event event){
        Platform.exit();
        System.exit(0);
    }

    private void onOptionsSetBoardSizeClickedEventHandler(ActionEvent event){
        Alert warningDialog = new Alert(Alert.AlertType.WARNING);
        warningDialog.setTitle("Reversi");
        warningDialog.setContentText("Changing the board size will start a new game. Do you want to continue?");
        // OK already exists
        warningDialog.getDialogPane().getButtonTypes().add(new ButtonType("Cancel"));

        warningDialog.showAndWait().ifPresent(warningDialogResponse -> {
           if (warningDialogResponse == ButtonType.OK){
               TextInputDialog newBoardSizeDialog = new TextInputDialog(Integer.toString(model.getGameBoard().getBoardSize()));

               newBoardSizeDialog.setTitle("Reversi");
               newBoardSizeDialog.setHeaderText("Enter a new board size > 2");

               //Code is not invoked if the input dialog box is canceled
               newBoardSizeDialog.showAndWait().ifPresent(newBoardSizeDialogResponse -> {
                   try{
                       int newBoardSize = Integer.parseInt(newBoardSizeDialogResponse);

                       if(newBoardSize < 3){
                           Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                           errorDialog.setTitle("Reversi");
                           errorDialog.setHeaderText("The provided board size is not greater than 2");
                           errorDialog.show();
                           return;
                       }

                       // Side note: there are no restrictions on board size input.
                       // So the discs and grid will be very small for large board sizes.
                       newGameBoardSize = newBoardSize;
                       onGameNewGameClickedEventHandler(null);
                   }
                   catch(NumberFormatException e){
                       Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                       errorDialog.setTitle("Reversi");
                       errorDialog.setHeaderText("Sorry, I could not understand your input");
                       errorDialog.show();
                   }
               });
           }
        });
    }

    synchronized private void onPlayerSetupChangedEventHandler(ActionEvent event){
        if(event.getSource() == radioMenuItemOptionsPlayerSetupPVC){
            model.setPlayerSetup(PlayerSetup.PLAYER_VS_COMPUTER);

            // If its the players turn, it will wait for one more input before switching to PC input. So this is required.
            if(model.getPlayerTurn() == Disc.Color.WHITE){
                modelThreadNotification = ModelThreadNotification.PASS_MOVE;
                notify();
            }
        }
        else if(event.getSource() == radioMenuItemOptionsPlayerSetupCVC){
            model.setPlayerSetup(PlayerSetup.COMPUTER_VS_COMPUTER);

            // If its the players turn, it will wait for one more input before switching to PC input. So this is required.
            modelThreadNotification = ModelThreadNotification.PASS_MOVE;
            notify();
        }
        else if(event.getSource() == radioMenuItemOptionsPlayerSetupPVP){
            model.setPlayerSetup(PlayerSetup.PLAYER_VS_PLAYER);
        }
    }

    private void onDifficultyChangedEventHandler(ActionEvent event){
        if(event.getSource() == radioMenuItemOptionsDifficultyEasy){
            model.setComputerDifficulty(ComputerDifficulty.EASY);
        }
        else if(event.getSource() == radioMenuItemOptionsDifficultyMedium){
            model.setComputerDifficulty(ComputerDifficulty.MEDIUM);
        }
        else if(event.getSource() == radioMenuItemOptionsDifficultyHard){
            model.setComputerDifficulty(ComputerDifficulty.HARD);
        }
    }

    private void onHelpAboutClickedEventHandler(ActionEvent actionEvent){
        Alert aboutDialog = new Alert(Alert.AlertType.NONE);
        aboutDialog.setTitle("Reversi");
        aboutDialog.setContentText("Reversi version: " + Reversi.version);
        aboutDialog.getDialogPane().getButtonTypes().add(new ButtonType("OK"));
        aboutDialog.show();
    }

    //endregion

    /**
     * Called after the main window has been constructed to create shapes and the grid
     * @param event not used
     */
    public void onShownEventHandler(WindowEvent event){
        // Grid creation
        for(int index = 0; index <= model.getGameBoard().getBoardSize(); index++){
            horizontalGridLines[index] = new Line();
            board.getChildren().add(horizontalGridLines[index]);

            verticalGridLines[index] = new Line();
            board.getChildren().add(verticalGridLines[index]);

        }

        // Game disc creation
        for(int rowIndex = 0; rowIndex < model.getGameBoard().getBoardSize(); rowIndex++){
            for(int columnIndex = 0; columnIndex < model.getGameBoard().getBoardSize(); columnIndex++){
                discs[rowIndex][columnIndex] = new Circle(discRadius);

                Disc disc = model.getGameBoard().getDiscAtLocation(rowIndex,columnIndex);
                if(disc != null){
                    discs[rowIndex][columnIndex].setUserData(disc.getColor());
                    placeDisc(discs[rowIndex][columnIndex],disc);
                }
                else{
                    discs[rowIndex][columnIndex].setVisible(false);
                }

                board.getChildren().add(discs[rowIndex][columnIndex]);
            }
        }

        moveIndicator = new Rectangle(discRadius*2+CELL_PADDING*2,discRadius*2+CELL_PADDING*2,Paint.valueOf("green"));
        moveIndicator.setOpacity(0.4);
        moveIndicator.setVisible(false);
        board.getChildren().add(moveIndicator);

        onMainWindowResize();

        // Resume the main thread to start game
        synchronized (this){
            notify();
        }
    }

    /**
     * Passes a message along to the appropriate view to inform the user
     * Invoked from the game model's thread.
     *
     * @param message string to display
     */
    @Override
    synchronized void notifyView(String message) {
        Platform.runLater(() -> labelMessage.setText(message));

        try {
            // Delaying between moves so user can see the computer move and the disc transitions
            wait(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Passes a message along to the appropriate view to inform the user about a player made move
     * Invoked from the game model's thread.
     *
     * @param row    not used (easily seen on the grid)
     * @param column not used (easily seen on the grid)
     */
    @Override
    void notifyViewMoveMessage(int row, int column) {
        notifyView("Player " + Disc.Color.getOppositeColor(model.getPlayerTurn()).getFriendlyName() + "'s Turn");
    }

    /**
     * Allows subclasses to process input appropriately according to the currently used view
     *
     * @return a valid location to place a disc for the current players turn
     */
    @Override
    synchronized InputLocation receiveUserInput() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(modelThreadNotification == ModelThreadNotification.PASS_MOVE){
            return null;
        }

        return userInputLocation;
    }

    /**
     * Defines custom game termination behavior according to the currently used view
     */
    @Override
    synchronized void gameOver() {
        try {
            Platform.runLater(() -> labelMessage.setText("Game Over!"));

            while(true){
                wait();

                // Its possible that the user changed the difficulty settings which notifies the model thread, so we
                // need to make sure a new game has been created.
                if(model.validMoveExists(Disc.Color.BLACK) && model.validMoveExists(Disc.Color.WHITE)){
                    startGame();
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //region Event Handlers
        menuItemGameNewGame.setOnAction(this::onGameNewGameClickedEventHandler);
        menuItemGameExit.setOnAction(this::onCloseEventHandler);

        menuItemOptionsSetBoardSize.setOnAction(this::onOptionsSetBoardSizeClickedEventHandler);

        ToggleGroup menuItemOptionsPlayerSetupToggleGroup = new ToggleGroup();
        radioMenuItemOptionsPlayerSetupPVC.setToggleGroup(menuItemOptionsPlayerSetupToggleGroup);
        radioMenuItemOptionsPlayerSetupPVC.setOnAction(this::onPlayerSetupChangedEventHandler);
        radioMenuItemOptionsPlayerSetupCVC.setToggleGroup(menuItemOptionsPlayerSetupToggleGroup);
        radioMenuItemOptionsPlayerSetupCVC.setOnAction(this::onPlayerSetupChangedEventHandler);
        radioMenuItemOptionsPlayerSetupPVP.setToggleGroup(menuItemOptionsPlayerSetupToggleGroup);
        radioMenuItemOptionsPlayerSetupPVP.setOnAction(this::onPlayerSetupChangedEventHandler);

        ToggleGroup menuItemOptionsDifficultyToggleGroup = new ToggleGroup();
        radioMenuItemOptionsDifficultyEasy.setToggleGroup(menuItemOptionsDifficultyToggleGroup);
        radioMenuItemOptionsDifficultyEasy.setOnAction(this::onDifficultyChangedEventHandler);
        radioMenuItemOptionsDifficultyMedium.setToggleGroup(menuItemOptionsDifficultyToggleGroup);
        radioMenuItemOptionsDifficultyMedium.setOnAction(this::onDifficultyChangedEventHandler);
        radioMenuItemOptionsDifficultyHard.setToggleGroup(menuItemOptionsDifficultyToggleGroup);
        radioMenuItemOptionsDifficultyHard.setOnAction(this::onDifficultyChangedEventHandler);

        menuItemHelpAbout.setOnAction(this::onHelpAboutClickedEventHandler);
        //endregion

        //region GUI Setup
        switch(model.getPlayerSetup()){
            case PLAYER_VS_COMPUTER:
                radioMenuItemOptionsPlayerSetupPVC.setSelected(true);
                break;
            case COMPUTER_VS_COMPUTER:
                radioMenuItemOptionsPlayerSetupCVC.setSelected(true);
                break;
            case PLAYER_VS_PLAYER:
                radioMenuItemOptionsPlayerSetupPVP.setSelected(true);
                break;
        }

        switch(model.getComputerDifficulty()){
            case EASY:
                radioMenuItemOptionsDifficultyEasy.setSelected(true);
                break;
            case MEDIUM:
                radioMenuItemOptionsDifficultyMedium.setSelected(true);
                break;
            case HARD:
                radioMenuItemOptionsDifficultyHard.setSelected(true);
                break;
        }

        labelMessage.setText("Player " + model.getPlayerTurn().getFriendlyName() + "'s Turn");
        labelScore.setText("Score: Black: " + model.getPlayerScore(Disc.Color.BLACK) + ", White: " + model.getPlayerScore(Disc.Color.WHITE));
        //endregion

        verticalGridLines = new Line[model.getGameBoard().getBoardSize() + 1];
        horizontalGridLines = new Line[model.getGameBoard().getBoardSize() + 1];
        discs = new Circle[model.getGameBoard().getBoardSize()][model.getGameBoard().getBoardSize()];
        newGameBoardSize = model.getGameBoard().getBoardSize();
    }

    /**
     * Invoked to start the GUI on a separate thread
     */
    @Override
    public void run() {
        new GuiView(model, this);
    }
}
