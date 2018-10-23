package it.richkmeli.RMC.controller;

import it.richkmeli.RMC.model.ModelException;

/**
 * Created by richk on 17/06/17.
 */
public class NetworkException extends ModelException {

    public NetworkException(Exception exception) {
        super(exception);
    }

}