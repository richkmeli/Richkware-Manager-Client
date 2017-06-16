package richk.RMC.controller;

import richk.RMC.swing.MainPanel;
import richk.RMC.swing.SwingView;
import richk.RMC.view.View;

/**
 * Created by richk on 25/05/17.
 */
public class App implements Runnable {
    protected View view;    // comunicazione da controller a view

    public void run() {
        LookAndFeel.initLookAndFeel("System","Metal");
        view = new SwingView(this);
    }



}
