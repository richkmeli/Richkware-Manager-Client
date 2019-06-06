package it.richkmeli.RMC.controller;

import it.richkmeli.RMC.swing.LoginPanel;
import it.richkmeli.RMC.view.View;

/**
 * Created by richk on 25/05/17.
 */
public class App implements Runnable {
    public View view;    // comunicazione da controller a view
    private Controller controller;

    public void run() {
        LookAndFeel.initLookAndFeel("System", "Metal");
        view = new LoginPanel(this);
        //        view = new MainPanel(this);
        controller = new Controller();
    }

    public Controller getController() {
        return controller;
    }
}
