package richk.RMC.swing;

import richk.RMC.controller.App;
import richk.RMC.view.View;

import javax.swing.*;

/**
 * Created by richk on 16/06/17.
 */
public class SwingView implements View {
    protected App app;
    private JFrame MainFrame;

    public SwingView(App appParam) {
        this.app = appParam;
        initialize();
    }

    public void initialize() {
        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        MainFrame.setContentPane(new MainPanel().MainPanel);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.pack();
        MainFrame.setVisible(true);
    }
}
