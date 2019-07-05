package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
import it.richkmeli.RMC.controller.network.CommandCallback;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.utils.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DirectConnect {
    private App app;
    private JPanel CommandsPanel;
    private JButton SendCommandButton;
    private JTextField commandToSendTextField;
    private JTextArea DeviceResponseTextArea;
    private JPanel DeviceResponsePanel;
    private JScrollPane DeviceResponseScrollPanel;
    private JPanel SendCommandPanel;
    private JCheckBox directCheckBox;
    private JButton ConnectDevice;
    private JButton DisconnectDevice;
    private JTextField addressOfDeviceTextField;
    private JCheckBox forceEncryptionCommandCheckBox;
    private JPanel ConnectToDevicePanel;
    private Device device;
    private List<Device> devices;

    public DirectConnect(App app, DevicesPanel devicesPanel) {
        this.app = app;

        devices = new ArrayList<>();

        disableInput();

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
                    int devicesCount = devicesPanel.getSelectedDeviceCount();
                    if (devicesCount == 1) {
                        DisconnectDev();
                        device = devicesPanel.getSelectedDevice();
                        if (device.getServerPort().compareTo("none") == 0) {
                            DisconnectDev();
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
                        DisconnectDev();
                        devices = devicesPanel.getSelectedDevices();
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

        DisconnectDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DisconnectDev();
                disableInput();
            }
        });

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

        addressOfDeviceTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
//                addressOfDeviceTextField.setText("");
            }
        });
        commandToSendTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
//                commandToSendTextField.setText("");
            }
        });
    }

    private void DisconnectDev() {
        device = null;
        devices.clear();
        /*for(int i = 0; i < deviceList.size(); ++i){
            updateRowColor(InfoTable, i, Color.WHITE);
        }
        InfoTable.repaint();
        */
    }

    public JPanel getPanel() {
        return CommandsPanel;
    }

    // DISCONNECT
    private void disableInput() {
        Logger.i("disable");
        commandToSendTextField.setEnabled(false);
        directCheckBox.setEnabled(true);
        if (directCheckBox.isSelected())
            addressOfDeviceTextField.setEnabled(true);
        else {
            addressOfDeviceTextField.setText("Address of device");
            addressOfDeviceTextField.setEnabled(false);
        }
        SendCommandButton.setEnabled(false);
        DisconnectDevice.setEnabled(false);
        ConnectDevice.setEnabled(true);
    }

    // CONNECT
    private void enableInput() {
        Logger.i("enable");
        commandToSendTextField.setEnabled(true);
        directCheckBox.setEnabled(false);
        addressOfDeviceTextField.setEnabled(false);
        SendCommandButton.setEnabled(true);
        DisconnectDevice.setEnabled(true);
        ConnectDevice.setEnabled(false);
    }


}
