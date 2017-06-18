package richk.RMC.swing;

import richk.RMC.controller.App;
import richk.RMC.model.Device;
import richk.RMC.model.Model;
import richk.RMC.model.ModelException;
import richk.RMC.view.View;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richk on 25/05/17.
 */
public class MainPanel implements View{
    protected App app;
    private JFrame MainFrame;
    private List<Device> deviceList;

    protected JPanel MainPanel;
    private JButton SendCommandButton;
    private JButton Disconnect;
    private JPanel CommandsPanel;
    private JProgressBar progressBar1;
    private JTable InfoTable;
    private JPanel ServerInfoPanel;
    private JButton Connect;
    private JTextField serverAddressTextField;
    private JPanel ButtonsPanel;
    private JButton button2;
    private JButton button3;
    private JScrollPane InfoScrollPanel;

    public void initialize() {
        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        MainFrame.setContentPane(MainPanel);
        //MainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainFrame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent arg0) {
                exit();
            }
        });
        MainFrame.pack();
        MainFrame.setVisible(true);
    }

    public MainPanel(App appParam) {
        this.app = appParam;
        deviceList = new ArrayList<Device>();

        initialize();

       /* try {
            InfoTable.setModel(new DeviceTableModel());
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


        Connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RefreshTable();
            }
        });

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void errorPanel(String err){
        JOptionPane.showMessageDialog(MainFrame, err, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void exit(){
        if((JOptionPane.showConfirmDialog(MainFrame,
                "Are you sure?",
                "Exit",
                JOptionPane.YES_NO_OPTION)
                == JOptionPane.OK_OPTION)){
            System.exit(0);
        }
    }

    private void RefreshTable(){
        try {
            progressBar1.setValue(0);
            deviceList = app.RefreshDevice(serverAddressTextField.getText());
            progressBar1.setValue(50);
            InfoTable.setModel(new DeviceTableModel(deviceList));
            progressBar1.setValue(100);
        } catch (ModelException e1) {
            errorPanel(e1.toString());
            e1.printStackTrace();
        }
    }

}
