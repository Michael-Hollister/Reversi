package edu.utdallas.msh150130.cs2336.reversi.view.gui;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.controller.GuiController;
import edu.utdallas.msh150130.cs2336.reversi.model.Model;
import edu.utdallas.msh150130.cs2336.reversi.view.View;

import java.util.Observable;


/**
 * Handles view updates from the model. Due to JavaFX conventions of processing and updating the GUI in the
 * controller class, the only responsibility of this class is to pass along board updates to the controller.
 */
public final class GuiView extends View {
    /**
     * JavaFX prevents multiple calls to the launch method, multiple instances cannot exist of this class.
     */
    private static boolean isConstructed = false;
    private static GuiView instance;

    /**
     * When the primary stage is being constructed, no knowledge of the instance that invoked GuiApplication.launch is
     * known (needs an instance to attach event handlers). Therefore this is made static.
     */
    private static GuiController controller;

    /**
     * Constructs a {@code GuiView} that will receive updates from the model.
     * @param modelToObserve model that the view is observing
     * @param controller controller that will control the view
     * @throws IllegalArgumentException if {@code modelToObserve} or {@code controller} is null
     * @throws IllegalStateException if the constructor is invoked multiple times
     */
    public GuiView(final Model modelToObserve, final GuiController controller){
        super(modelToObserve);
        if(isConstructed){
            throw new IllegalStateException("This object can only be constructed once");
        }
        if(controller == null){
            throw new IllegalArgumentException("controller should not be null");
        }

        isConstructed = true;
        instance = this;
        GuiView.controller = controller;
        GuiApplication.launch(GuiApplication.class);
    }

    static GuiController getController(){
        return GuiView.controller;
    }

    /**
     * Updates the model to the controller's model
     * @param modelToObserve model that the view is observing
     * @throws IllegalArgumentException if {@code modelToObserve} is null
     */
    public static void updateModel(final Model modelToObserve){
        if(modelToObserve == null){
            throw new IllegalArgumentException("modelToObserve should not be null");
        }

        instance.model = modelToObserve;
        instance.model.addObserver(instance);
    }

    /**
     * Updates the game board and score after a move has been performed
     * @param o   not used
     * @param arg not used
     */
    @Override
    public void update(final Observable o, final Object arg) {
        controller.updateGui();
    }
}
