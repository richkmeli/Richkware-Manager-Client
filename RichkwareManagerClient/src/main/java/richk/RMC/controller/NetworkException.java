package richk.RMC.controller;

import richk.RMC.model.ModelException;

/**
 * Created by richk on 17/06/17.
 */
public class NetworkException extends ModelException {

    public NetworkException(Exception exception) {
        super(exception);
    }

}