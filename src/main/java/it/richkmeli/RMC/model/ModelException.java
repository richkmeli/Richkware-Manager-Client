package it.richkmeli.RMC.model;

/**
 * Created by richk on 17/06/17.
 */
@SuppressWarnings("serial")
public class ModelException extends Exception {
    public ModelException(Exception exception) {
        super(exception);

    }
}