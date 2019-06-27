package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
import it.richkmeli.RMC.controller.network.CommandCallback;
import it.richkmeli.RMC.controller.network.NetworkException;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.model.ModelException;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import it.richkmeli.RMC.view.View;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class RichkwarePanel implements View {
    private JFrame MainFrame;
    private JPanel LoginPanel;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField protocoloField;
    private JTextField serverField;
    private JTextField serviceField;
    private JTextField portField;
    private JButton loginButton;
    private JLabel errorField;
    private JPanel FIRST_BLOCK;
    private JPanel MainPanel;
    private JPanel StartSkipPanel;
    private JButton SkipButton;
    private JPanel AFTER_LOGIN;
    private JPanel ServerInfoPanel;
    private JPanel ButtonsPanel;
    private JButton Disconnect;
    private JButton refresh;
    private JCheckBox encryptionCheckBox;
    private JProgressBar progressBar1;
    private JScrollPane TableScrollPanel;
    private JTable InfoTable;
    private JPanel SendCommandsPanel;
    private JPanel ConnectToDevicePanel;
    private JCheckBox directCheckBox;
    private JButton ConnectDevice;
    private JButton DisconnectDevice;
    private JTextField addressOfDeviceTextField;
    private JCheckBox forceEncryptionCommandCheckBox;
    private JScrollPane DeviceResponseScrollPanel;
    private JPanel DeviceResponsePanel;
    private JTextArea DeviceResponseTextArea;
    private JButton SendCommandButton;
    private JTextField commandToSendTextField;
    private JPanel DIRECT_CONNECT;
    private JPanel ConnectToDevicePanelDirect;
    private JPanel CommandsPanelDirect;
    private JCheckBox directCheckBoxDirect;
    private JButton ConnectDeviceDirect;
    private JButton DisconnectDeviceDirect;
    private JTextField addressOfDeviceTextFieldDirect;
    private JCheckBox forceEncryptionCommandCheckBoxDirect;
    private JScrollPane DeviceResponseScrollPanelDirect;
    private JPanel DeviceResponsePanelDirect;
    private JTextArea DeviceResponseTextAreaDirect;
    private JButton SendCommandButtonDirect;
    private JTextField commandToSendTextFieldDirect;
    private JButton loginDirect;
    private JPanel SendCommandPanelDirect;
    private JTabbedPane tabbedPane1;
    private JButton ConnectDeviceReverse;
    private JButton DisconnectDeviceReverse;
    private JCheckBox forceEncryptionCommandCheckBoxReverse;
    private JButton SendCommandButtonReverse;
    private JTextArea CommandsTextAreaReverse;
    private JPanel ReverseCmmandsPanel;
    private JButton ReceiveResponseButtonReverse;
    private JPanel DirectConnectPanel;

    private App app;
    private List<Device> deviceList;
    private Device device;
    private List<Device> devices;

    @Override
    public void initialize() {
        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        MainFrame.setContentPane(MainPanel);
        MainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainFrame.pack();
        MainFrame.setVisible(true);
    }

    public RichkwarePanel(App app){
        this.app = app;
        initialize();
        loadLoginPanel();

    }

    private void loadLoginPanel(){
        FIRST_BLOCK.setVisible(true);
        DIRECT_CONNECT.setVisible(false);
        AFTER_LOGIN.setVisible(false);
        MainFrame.pack();
        if(loginButton.getActionListeners().length==0) {
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    String email = emailField.getText();
                    String password = passwordField.getText();
                    try {
                        app.getController().getNetwork().setURL(protocoloField.getText(), serverField.getText(), portField.getText(), serviceField.getText());
                    } catch (NetworkException e) {
                        errorField.setText(e.getMessage());
                    }

                    String response = app.getController().login(email, password);
                    try {
                        if (ResponseParser.isStatusOK(response)) {
                            //LOGIN EFFETTUATO
                            errorField.setText(" ");

                            loadDevicesPanel();

                        } else {
                            //LOGIN FALLITO
                            errorField.setText(ResponseParser.parseMessage(response));
                            errorField.setVisible(true);
                        }
                    } catch (JSONException e) {
                        Logger.e("Login error", e);
                        errorField.setText("Internal error");
                        errorField.setVisible(true);
                    }
                }
            });
        }
        if(SkipButton.getActionListeners().length==0) {
            SkipButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    loadDirectConnectPanel();
                }
            });
        }
    }

    private void loadDevicesPanel(){
        FIRST_BLOCK.setVisible(false);
        DIRECT_CONNECT.setVisible(false);
        AFTER_LOGIN.setVisible(true);

        MainFrame.pack();
        deviceList = new ArrayList<>();

        // jtable InfoTable initialisation
        try {
            InfoTable.setModel(new DeviceTableModel(deviceList));
        } catch (NullPointerException npe) {
            System.err.println(npe.getMessage());
        }

        refreshTable();

        if(refresh.getActionListeners().length==0) {
            refresh.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshTable();
                }
            });
        }

        if(Disconnect.getActionListeners().length==0) {
            Disconnect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                deviceList = new ArrayList<Device>();
//                InfoTable.setModel(new DeviceTableModel(deviceList));

                    String response = app.getController().logout();
                    try {
                        deviceList.clear();
                        InfoTable.setModel(new DeviceTableModel(deviceList));
                        if (ResponseParser.isStatusOK(response)) {
                            //LOGOUT EFFETTUATO
                            loadLoginPanel();

                        } else {
                            //LOGOUT FALLITO
                        }
                    } catch (JSONException exp) {
                        Logger.e("Logout error", exp);
                    }
                }
            });
        }
        loadConnectPanel();
    }

    private void loadConnectPanel(){
        Logger.i("loading connect panel");

        connectPanel(SendCommandButton, commandToSendTextField, DeviceResponseTextArea, ConnectDevice, directCheckBox,addressOfDeviceTextField, forceEncryptionCommandCheckBox, DisconnectDevice, Connect.DEFAULT);

        Logger.i("loaded connect panel");

        if(SendCommandButtonReverse.getActionListeners().length==0) {
            SendCommandButtonReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command = CommandsTextAreaReverse.getText();
                    if (command.compareTo("") == 0 || command.compareTo("Command to send") == 0) {
                        Logger.e("Write the command to execute on device");
                    } else {
                        app.getController().reverseCommand(device, command);
                    }
                }
            });
        }
        if(ConnectDeviceReverse.getActionListeners().length==0) {
            ConnectDeviceReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int devicesCount = getSelectedDeviceCount();
                    Logger.i("connect");
                    if (devicesCount == 1) {
                        clearTable();
                        device = getSelectedDevice();
                    } else if (devicesCount > 1) {
                        clearTable();
                        devices = getSelectedDevices();
                    } else {
//                      errorPanel("Select a device");
                        Logger.e("Select a device");
                    }
                }
            });
        }
        if(ReceiveResponseButtonReverse.getActionListeners().length==0) {
            ReceiveResponseButtonReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String response = new String(Base64.getDecoder().decode(ResponseParser.parseMessage(app.getController().reverseCommandResponse(device))));
                    CommandsTextAreaReverse.setText(response);
                }
            });
        }
        if(DisconnectDeviceReverse.getActionListeners().length==0) {
            DisconnectDeviceReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    clearTable();
                }
            });
        }
    }

    private void loadDirectConnectPanel(){
        FIRST_BLOCK.setVisible(false);
        DIRECT_CONNECT.setVisible(true);
        AFTER_LOGIN.setVisible(false);
        MainFrame.pack();

        if(loginDirect.getActionListeners().length==0) {
            loginDirect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    loadLoginPanel();
                }
            });
        }

        connectPanel(SendCommandButtonDirect, commandToSendTextFieldDirect, DeviceResponseTextAreaDirect, ConnectDeviceDirect, directCheckBoxDirect,addressOfDeviceTextFieldDirect, forceEncryptionCommandCheckBoxDirect, DisconnectDeviceDirect, Connect.DIRECT);
    }

    private void errorPanel(String err) {
        JOptionPane.showMessageDialog(MainFrame, err, "Error", JOptionPane.ERROR_MESSAGE);
    }


    private void connectPanel(JButton SendCommandButton, JTextField commandToSendTextField, JTextArea DeviceResponseTextArea, JButton ConnectDevice, JCheckBox directCheckBox, JTextField addressOfDeviceTextField, JCheckBox forceEncryptionCommandCheckBox, JButton DisconnectDevice, Connect connetionType){
        devices = new ArrayList<>();

        disableInput(connetionType);

        if(SendCommandButton.getActionListeners().length==0) {
            SendCommandButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command = commandToSendTextField.getText();
                    if (command.compareTo("") == 0 || command.compareTo("Command to send") == 0) {
                        Logger.e("Write the command to execute on device");
                    } else {
                        app.getController().sendCommand(command, new CommandCallback() {
                            @Override
                            public void onSuccess(String response) {
                                Logger.i("Response: " + response);
                                DeviceResponseTextArea.append(response);
                                DeviceResponseTextArea.setLineWrap(true);
                            }

                            @Override
                            public void onFailure(String error) {
                                Logger.e("Error: " + error);
                            }
                        });
                    }
                }
            });
        }

        if(ConnectDevice.getActionListeners().length==0) {
            ConnectDevice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (directCheckBox.isSelected()) { //Direct command to selected devices from table
                        try {
                            String ipport = addressOfDeviceTextField.getText();
                            device = new Device("", ipport.substring(0, ipport.indexOf(":")), ipport.substring(ipport.indexOf(":") + 1, ipport.length()), "", "", "");
                            app.getController().connect(device, forceEncryptionCommandCheckBox.isSelected(), new PanelCallback() {
                                @Override
                                public void onSuccess() {
                                    Logger.i("Device connected: " + device.getIp() + ":" + device.getServerPort());
                                    enableInput();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Logger.e("Device not connected: " + error);
                                }
                            });
                        } catch (Exception exp) {
//                        errorPanel("Address not correct. The syntax is IP:PORT");
                            Logger.e("Address not correct. The syntax is IP:PORT");
                        }
                    } else {
                        int devicesCount = getSelectedDeviceCount();
                        if (devicesCount == 1) {
                            clearTable();
                            device = getSelectedDevice();
                            if (device.getServerPort().compareTo("none") == 0) {
                                clearTable();
//                            errorPanel("ServerPort of this device is closed");
                                Logger.e("ServerPort of this device is close");
                            } else {
                                app.getController().connect(device, forceEncryptionCommandCheckBox.isSelected(), new PanelCallback() {
                                    @Override
                                    public void onSuccess() {
                                        addressOfDeviceTextField.setText(device.getIp() + ":" + device.getServerPort());
                                        Logger.i("Device connected: " + device.getIp() + ":" + device.getServerPort());
                                        enableInput();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Logger.e("Device not connected: " + error);
                                    }
                                });
                            }
                        } else if (devicesCount > 1) {
                            clearTable();
                            devices = getSelectedDevices();
                            //TODO check device ip and port
                            app.getController().connect(devices, forceEncryptionCommandCheckBox.isSelected(), new PanelCallback() {
                                @Override
                                public void onSuccess() {
                                    addressOfDeviceTextField.setText("Multiple devies");
                                    Logger.i("Devices connected");
                                    enableInput();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Logger.e("Devices not connected: " + error);
                                }
                            });
                        } else {
//                      errorPanel("Select a device");
                            Logger.e("Select a device");
                        }
                    }
                }
            });
        }

        if(DisconnectDevice.getActionListeners().length==0) {
            DisconnectDevice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearTable();
                    disableInput(connetionType);
                }
            });
        }

        if(directCheckBox.getActionListeners().length==0) {
            directCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (directCheckBox.isSelected()) {
                        addressOfDeviceTextField.setText("");
                        addressOfDeviceTextField.setEnabled(true);
                    } else {
                        addressOfDeviceTextField.setText("Address of device");
                        commandToSendTextField.setEnabled(false);
                    }
                }
            });
        }
        if(addressOfDeviceTextField.getActionListeners().length==0) {
            addressOfDeviceTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
//                addressOfDeviceTextField.setText("");
                }
            });
        }
        if(addressOfDeviceTextField.getActionListeners().length==0) {
            commandToSendTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
//                commandToSendTextField.setText("");
                }
            });
        }

    }

    private void refreshTable() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    progressBar1.setValue(0);

                    // if encryption check box is selected, RMC uses encryption to refresh the list of devices
                    deviceList = app.getController().refreshDevice(encryptionCheckBox.isSelected());

                    progressBar1.setValue(20);

                    InfoTable.setModel(new DeviceTableModel(deviceList));

                    progressBar1.setValue(40);

                    updateRowHeights(InfoTable);

                    //InfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                } catch (ModelException e1) {
                    errorPanel(e1.toString());
                    e1.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void clearTable() {
        device = null;
        devices.clear();
    }

    private void updateRowHeights(JTable jTable) {
        Component comp;
        double singleStep = 50.0 / jTable.getRowCount();
        progressBar1.setValue(50);
        double count = 50.0;
        for (int row = 0; row < jTable.getRowCount(); row++) {
            int rowHeight = jTable.getRowHeight();
            for (int column = 0; column < jTable.getColumnCount(); column++) {
                comp = jTable.prepareRenderer(jTable.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            count += singleStep;
            progressBar1.setValue((int)Math.round(count));
            jTable.setRowHeight(row, rowHeight);
        }
        progressBar1.setValue(100);
    }

    private int getSelectedDeviceCount() {
//        InfoTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
//                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
//                if (devices.contains(deviceList.get(row)) || device == deviceList.get(row)) {
//                    setBackground(Color.GREEN);
//                    setForeground(Color.WHITE);
//                } else {
//                    setBackground(table.getBackground());
//                    setForeground(table.getForeground());
//                }
//                return this;
//            }
//        });
        return InfoTable.getSelectedRowCount();
    }

    private Device getSelectedDevice() {
        return deviceList.get(InfoTable.getSelectedRow());
    }

    private List<Device> getSelectedDevices() {
        List<Device> list = new ArrayList<>();
        for (int i : InfoTable.getSelectedRows())
            list.add(deviceList.get(i));
        return list;
    }

    private void disableInput(Connect connectionType) {
        Logger.i("disable");
        commandToSendTextField.setEnabled(false);
        commandToSendTextFieldDirect.setEnabled(false);
        directCheckBox.setEnabled(true);
        directCheckBoxDirect.setEnabled(true);
        if(connectionType == Connect.DIRECT) {
            directCheckBox.setEnabled(false);
            directCheckBoxDirect.setEnabled(false);
            directCheckBox.setSelected(true);
            directCheckBoxDirect.setSelected(true);
        }
        if (directCheckBox.isSelected() || directCheckBoxDirect.isSelected()) {
            addressOfDeviceTextField.setEnabled(true);
            addressOfDeviceTextFieldDirect.setEnabled(true);
        } else {
            addressOfDeviceTextField.setText("Address of device");
            addressOfDeviceTextFieldDirect.setText("Address of device");
            addressOfDeviceTextField.setEnabled(false);
            addressOfDeviceTextFieldDirect.setEnabled(false);
        }

        SendCommandButton.setEnabled(false);
        SendCommandButtonDirect.setEnabled(false);
        DisconnectDevice.setEnabled(false);
        DisconnectDeviceDirect.setEnabled(false);
        ConnectDevice.setEnabled(true);
        ConnectDeviceDirect.setEnabled(true);
    }

    private void enableInput() {
        Logger.i("enable");
        commandToSendTextField.setEnabled(true);
        commandToSendTextFieldDirect.setEnabled(true);
        directCheckBox.setEnabled(false);
        directCheckBoxDirect.setEnabled(false);
        addressOfDeviceTextField.setEnabled(false);
        addressOfDeviceTextFieldDirect.setEnabled(false);
        SendCommandButton.setEnabled(true);
        SendCommandButtonDirect.setEnabled(true);
        DisconnectDevice.setEnabled(true);
        DisconnectDeviceDirect.setEnabled(true);
        ConnectDevice.setEnabled(false);
        ConnectDeviceDirect.setEnabled(false);
    }

    private enum Connect {
        DEFAULT(0),
        DIRECT(1),
        REVERSE(2);

        private int code;

        Connect(int code){
            this.code = code;
        }
    }

}