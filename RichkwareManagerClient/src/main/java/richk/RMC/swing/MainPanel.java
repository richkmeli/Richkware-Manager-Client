package richk.RMC.swing;

import richk.RMC.controller.App;
import richk.RMC.view.View;
import sun.applet.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by richk on 25/05/17.
 */
public class MainPanel implements View{
    protected App app;
    private JFrame MainFrame;

    protected JPanel MainPanel;
    private JButton SendCommandButton;
    private JButton Disconnect;
    private JPanel CommandsPanel;
    private JProgressBar progressBar1;
    private JScrollPane InfoScrollPanel;
    private JTable InfoTable;
    private JPanel ServerInfoPanel;
    private JButton Connect;
    private JTextField serverAddressTextField;
    private JPanel ButtonsPanel;
    private JButton button2;
    private JButton button3;

    public void initialize() {
        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        MainFrame.setContentPane(MainPanel);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.pack();
        MainFrame.setVisible(true);
    }

    public MainPanel(App appParam) {
        this.app = appParam;
        initialize();

        Connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                app.GetDevicesList(serverAddressTextField.getText());
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
