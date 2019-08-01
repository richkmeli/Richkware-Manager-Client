package it.richkmeli.rmc.controller;

import it.richkmeli.rmc.swing.RichkwarePanel;
import it.richkmeli.rmc.view.View;

/**
 * Created by richk on 25/05/17.
 */
public class App implements Runnable {
    public View view;    // comunicazione da controller a view
    private Controller controller;

    public void run() {
        LookAndFeel.initLookAndFeel("System", "Metal");
        controller = new Controller();
        view = new RichkwarePanel(this);
    }

    public Controller getController() {
        return controller;
    }
}
