package richk.RMC.swing;

import richk.RMC.controller.App;
import richk.RMC.model.Device;
import richk.RMC.model.ModelException;
import richk.RMC.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richk on 25/05/17.
 */
public class MainPanel implements View {
    protected App app;
    private JFrame MainFrame;
    private List<Device> deviceList;
    private Integer selectedDeviceRow;

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
    private JButton DisconnectDevice;
    private JButton ConnectDevice;
    private JScrollPane TableScrollPanel;
    private JTextArea DeviceResponseTextArea;
    private JScrollPane DeviceResponseScrollPanel;
    private JPanel DeviceResponsePanel;

    public void initialize() {
        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        MainFrame.setContentPane(MainPanel);
        //MainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainFrame.addWindowListener(new WindowAdapter() {
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

        // jtable InfoTable initialisation
        InfoTable.setModel(new DeviceTableModel(deviceList));

        initialize();

        Connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RefreshTable();
            }
        });

        SendCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedDeviceRow != null) {
                    Device device = deviceList.get(selectedDeviceRow);
                    if (device.getServerPort().compareTo("none") == 0) {
                        errorPanel("This device has ServerPort close");
                    } else {
                        try {
                            String response = app.SendCommand(device.getIP(), device.getServerPort(), "[[1]]dir");
                            DeviceResponseTextArea.append(response);
                            DeviceResponseTextArea.setLineWrap(true);

                        } catch (ModelException e1) {
                            errorPanel(e1.toString());
                        }
                    }
                }else {
                    errorPanel("Connect to device. Before to send command select a device");
                }
            }
        });

        ConnectDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDeviceRow = InfoTable.getSelectedRow();
                updateRowColor(InfoTable,selectedDeviceRow,Color.GREEN);
                InfoTable.getSelectionModel().clearSelection();
            }
        });
        DisconnectDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRowColor(InfoTable,selectedDeviceRow,Color.WHITE);
                InfoTable.repaint();
                selectedDeviceRow = null;
            }
        });
    }

    private void createUIComponents() {
        // place custom component creation code here
    }

    private void errorPanel(String err) {
        JOptionPane.showMessageDialog(MainFrame, err, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void exit() {
        if ((JOptionPane.showConfirmDialog(MainFrame,
                "Are you sure?",
                "Exit",
                JOptionPane.YES_NO_OPTION)
                == JOptionPane.OK_OPTION)) {
            System.exit(0);
        }
    }

    private void RefreshTable() {
        try {
            progressBar1.setValue(0);
            deviceList = app.RefreshDevice(serverAddressTextField.getText());
            progressBar1.setValue(50);
            InfoTable.setModel(new DeviceTableModel(deviceList));
            updateRowHeights(InfoTable);
            progressBar1.setValue(100);
            //InfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } catch (ModelException e1) {
            errorPanel(e1.toString());
            e1.printStackTrace();
        }
    }

    private void updateRowHeights(JTable jTable) {
        Component comp;
        for (int row = 0; row < jTable.getRowCount(); row++) {
            int rowHeight = jTable.getRowHeight();
            for (int column = 0; column < jTable.getColumnCount(); column++) {
                comp = jTable.prepareRenderer(jTable.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            jTable.setRowHeight(row, rowHeight);
        }
    }

    private void updateRowColor(JTable jTable, int row, java.awt.Color color) {
        Component component;
        for (int column = 0; column < jTable.getColumnCount(); column++) {
            component = jTable.prepareRenderer(jTable.getCellRenderer(row, column), row, column);
            // TODO: bug, all rows colored after connection to a device
            component.setBackground(color);
        }
    }

}
