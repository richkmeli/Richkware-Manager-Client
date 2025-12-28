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
            // Log full stacktrace to stderr (useful when running headless or from terminal)
            t.printStackTrace(System.err);
            // Also show a dialog if AWT is available
            try {
                JOptionPane.showMessageDialog(null, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Throwable ignore) {
                // ignore - we already printed stacktrace
            }
        }
    }

    public Controller getController() {
        return controller;
    }
}
