package edu.utdallas.msh150130.cs2336.reversi.view;

/*******************************************************************
* Copyright (c) 2016, Michael Hollister                            *
*                                                                  *
* This source code is subject to the terms of The MIT License.     *
* If a copy of The MIT License was not distributed with this file, *
* you can obtain one at http://opensource.org/licenses/MIT.        *
*******************************************************************/

import edu.utdallas.msh150130.cs2336.reversi.model.Model;
import java.util.Observer;

/**
 * Base class defining common attributes and methods for GUI and console views
 */
public abstract class View implements Observer {

    /**
     * Represents the view that the user will interact with
     */
    public enum Mode{
        CONSOLE, GUI
    }

    /**
     * Model that the view is observing
     */
    protected Model model;

    /**
     * Constructs a {@code View} that will receive updates from the model
     * @param modelToObserve model that the view is observing
     * @throws IllegalArgumentException if {@code modelToObserve} is null
     */
    protected View(final Model modelToObserve){
        if(modelToObserve == null){
            throw new IllegalArgumentException("modelToObserve should not be null");
        }

        this.model = modelToObserve;
        model.addObserver(this);
    }
}
