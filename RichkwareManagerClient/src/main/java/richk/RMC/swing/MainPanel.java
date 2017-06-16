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
public class MainPanel{

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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
