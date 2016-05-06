package edu.utdallas.msh150130.cs2336.reversi.view.gui;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Sets up the main stage and window properties
 */
public final class GuiApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GuiApplication.fxml"));
        loader.setController(GuiView.getController());
        Parent root = loader.load();

        // Window Properties
        primaryStage.setTitle("Reversi");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.setMinWidth(733);
        primaryStage.setMinHeight(550);

        // Prevent oversizing board on small window widths
        primaryStage.maxHeightProperty().bind(primaryStage.getScene().widthProperty());

        // Event Handlers
        // TODO Fix resizing after window snapping
        primaryStage.getScene().widthProperty().addListener((observable, oldValue, newValue) -> GuiView.getController().onMainWindowResize());
        primaryStage.getScene().heightProperty().addListener((observable, oldValue, newValue) -> GuiView.getController().onMainWindowResize());
        primaryStage.getScene().setOnMouseClicked(GuiView.getController()::onMouseClicked);
        primaryStage.getScene().setOnMouseMoved(GuiView.getController()::onMouseMoved);

        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            // At the time of invocation, the new width and height properties have not been set.
            Platform.runLater(() -> GuiView.getController().onMainWindowResize());
        });
        primaryStage.setOnCloseRequest(GuiView.getController()::onCloseEventHandler);
        primaryStage.setOnShown(GuiView.getController()::onShownEventHandler);

        primaryStage.show();
    }
}
