package it.richkmeli.rmc.controller.network;

import it.richkmeli.rmc.model.ModelException;

/**
 * Created by richk on 17/06/17.
 */
public class NetworkException extends ModelException {

    public NetworkException(Exception exception) {
        super(exception);
    }

}