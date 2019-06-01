package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.model.ModelException;
import it.richkmeli.RMC.view.View;

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
    private Device device;

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
    private JTextField addressOfDeviceTextField;
    private JTextField commandToSendTextField;
    private JCheckBox directCheckBox;
    private JCheckBox encryptionCheckBox;
    private JCheckBox forceEncryptionCommandCheckBox;
    private JPanel SendCommandPanel;
    private JPanel ConnectToDevicePanel;
    private JButton fileButton;
    private JButton editButton;
    private JButton viewButton;
    private JButton helpButton;

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

        Disconnect.setEnabled(false);
        SendCommandButton.setEnabled(false);
        DisconnectDevice.setEnabled(false);
        addressOfDeviceTextField.setEnabled(false);
    }

    public MainPanel(App appParam) {
        this.app = appParam;
        deviceList = new ArrayList<Device>();

        // jtable InfoTable initialisation
        try {
            InfoTable.setModel(new DeviceTableModel(deviceList));
        }catch (NullPointerException npe){
            System.err.println(npe.getMessage());
        }

        initialize();

        Connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RefreshTable();
                Disconnect.setEnabled(true);
            }
        });

        SendCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (device != null) {
                    if (device.getServerPort().compareTo("none") == 0) {
                        errorPanel("ServerPort of this device is closed");
                    } else {
                        try {
                            String command = commandToSendTextField.getText();
                            if (command.compareTo("") == 0 || command.compareTo("Command to send") == 0) {
                                errorPanel("Write the command to execute on device");
                            } else {
                                String response = null;
                                if (forceEncryptionCommandCheckBox.isSelected()) {
                                    response = app.getController().SendCommand(device.getIP(), device.getServerPort(), device.getEncryptionKey(), true, "[[1]]" + command);
                                } else {
                                    response = app.getController().SendCommand(device.getIP(), device.getServerPort(), device.getEncryptionKey(), false, "[[1]]" + command);
                                }
                                DeviceResponseTextArea.append(response);
                                DeviceResponseTextArea.setLineWrap(true);
                            }
                        } catch (ModelException e1) {
                            errorPanel(e1.toString());
                        }
                    }
                } else {
                    errorPanel("Connect to device. Before to send command select a device");
                }
            }
        });

        ConnectDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deviceList.isEmpty() && directCheckBox.isEnabled()) {
                    try {
                        String ipport = addressOfDeviceTextField.getText();
                        device = new Device("", ipport.substring(0, ipport.indexOf(":")), ipport.substring(ipport.indexOf(":") + 1, ipport.length()), "", "");

                        SendCommandButton.setEnabled(true);
                        DisconnectDevice.setEnabled(true);
                    } catch (Exception e1) {
                        errorPanel("Address not correct. The syntax is IP:PORT");
                    }
                } else {
                    Integer selectedDeviceRow = InfoTable.getSelectedRow();
                    if (selectedDeviceRow != null) {
                        DisconnectDev();
                        device = deviceList.get(selectedDeviceRow);
                        if (device.getServerPort().compareTo("none") == 0) {
                            DisconnectDev();
                            errorPanel("ServerPort of this device is closed");
                        } else {
                            addressOfDeviceTextField.setText(device.getIP() + ":" + device.getServerPort());

                            SendCommandButton.setEnabled(true);
                            DisconnectDevice.setEnabled(true);

                            //updateRowColor(InfoTable, selectedDeviceRow, Color.GREEN);
                            //InfoTable.getSelectionModel().clearSelection();
                        }
                    } else {
                        errorPanel("Select a device");
                    }
                }

            }
        });
        DisconnectDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DisconnectDev();
                SendCommandButton.setEnabled(false);
                DisconnectDevice.setEnabled(false);
            }
        });
        Disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deviceList = new ArrayList<Device>();
                InfoTable.setModel(new DeviceTableModel(deviceList));
                Disconnect.setEnabled(false);
            }
        });
        directCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (directCheckBox.isSelected()) {
                    addressOfDeviceTextField.setEnabled(true);
                } else {
                    addressOfDeviceTextField.setEnabled(false);
                    addressOfDeviceTextField.setText("");
                    deviceList = new ArrayList<Device>();
                }
            }
        });
        addressOfDeviceTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                addressOfDeviceTextField.setText("");
            }
        });
        commandToSendTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                commandToSendTextField.setText("");
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

            // if encryption check box is selected, RMC uses encryption to refresh the list of devices
            deviceList = app.getController().RefreshDevice(encryptionCheckBox.isSelected());
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

    private void DisconnectDev() {
        device = null;

        /*for(int i = 0; i < deviceList.size(); ++i){
            updateRowColor(InfoTable, i, Color.WHITE);
        }
        InfoTable.repaint();
        */
    }

}
