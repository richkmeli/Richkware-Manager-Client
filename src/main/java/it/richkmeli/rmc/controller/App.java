package it.richkmeli.rmc.controller;

import it.richkmeli.rmc.swing.RichkwarePanel;
import it.richkmeli.rmc.view.View;

import javax.swing.*;

/**
 * Created by richk on 25/05/17.
 */
public class App implements Runnable {
    public View view;    // communication from controller to view
    private Controller controller;

    public void run() {
        try {
            LookAndFeel.initLookAndFeel("System", "Metal");
            controller = new Controller();
            view = new RichkwarePanel(this);
        } catch (Throwable t) {
            // TODO cattura tutto e mostra dialog, tipo manca bouncy castle
            JOptionPane.showMessageDialog(null, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Controller getController() {
        return controller;
    }
}
