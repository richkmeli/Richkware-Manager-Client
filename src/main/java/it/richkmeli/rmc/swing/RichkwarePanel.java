package it.richkmeli.rmc.swing;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.network.tcp.client.okhttp.NetworkException;
import it.richkmeli.jframework.util.log.Logger;
import it.richkmeli.rmc.controller.App;
import it.richkmeli.rmc.model.Device;
import it.richkmeli.rmc.model.ModelException;
import it.richkmeli.rmc.view.View;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RichkwarePanel implements View {
    public static final String KEY_URL = "url";
    public static final String SECUREDATA_PROTOCOLLO_KEY = "protocollo";
    public static final String SECUREDATA_SERVER_KEY = "server";
    public static final String SECUREDATA_PORT_KEY = "port";
    public static final String SECUREDATA_SERVICE_KEY = "service";
    public static final String KEY_AUTO_ESTABLISH = "autoEstablish";
    private JFrame MainFrame;
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
    private JButton deleteCryptoStateButton;
    private JButton establishSecureConnectionButton;
    private JPanel credentialPanel;
    private JPanel urlPanel;
    private JPanel SecureConnectPanel;
    private JPanel EstablishDeletePanel;
    private JCheckBox autoEstablishSecureConnectionCheckBox;
    private JPanel DirectConnectPanel;

    private App app;
    private List<Device> deviceList;
    private Device device;
    private List<Device> devices;

    @Override
    public void initialize() {
        // Initialize fields if not already done by the IntelliJ Forms compiler
        if (MainPanel == null) {
            initializeDefaultUI();
        }

        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        MainFrame.setContentPane(MainPanel);
        MainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainFrame.setMinimumSize(new Dimension(800, 600));
        MainFrame.pack();
        MainFrame.setVisible(true);
    }

    private void initializeDefaultUI() {
        // Create a default main panel when forms designer initialization is not
        // available
        MainPanel = new JPanel(new CardLayout());

        // Initialize all critical panels
        FIRST_BLOCK = new JPanel(new GridBagLayout());
        AFTER_LOGIN = new JPanel(new BorderLayout());
        DIRECT_CONNECT = new JPanel(new BorderLayout());

        MainPanel.add(FIRST_BLOCK, "FIRST_BLOCK");
        MainPanel.add(AFTER_LOGIN, "AFTER_LOGIN");
        MainPanel.add(DIRECT_CONNECT, "DIRECT_CONNECT");

        // --- FIRST_BLOCK setup ---
        SecureConnectPanel = new JPanel(new BorderLayout());
        SecureConnectPanel.setBorder(new TitledBorder(new LineBorder(Color.RED, 2), "SecureConnection"));

        urlPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        urlPanel.setBorder(new TitledBorder("Server URL"));

        protocoloField = new JTextField("http", 10);
        serverField = new JTextField("127.0.0.1", 15);
        portField = new JTextField("8080", 5);
        serviceField = new JTextField("Richkware-Manager-Server", 15);

        urlPanel.add(new JLabel("Protocol:"));
        urlPanel.add(protocoloField);
        urlPanel.add(new JLabel("Server:"));
        urlPanel.add(serverField);
        urlPanel.add(new JLabel("Port:"));
        urlPanel.add(portField);
        urlPanel.add(new JLabel("Service:"));
        urlPanel.add(serviceField);

        credentialPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");

        credentialPanel.add(new JLabel("Email:"));
        credentialPanel.add(emailField);
        credentialPanel.add(new JLabel("Password:"));
        credentialPanel.add(passwordField);
        credentialPanel.add(new JLabel(""));
        credentialPanel.add(loginButton);

        errorField = new JLabel(" ");
        errorField.setForeground(Color.RED);
        errorField.setHorizontalAlignment(SwingConstants.CENTER);

        EstablishDeletePanel = new JPanel(new FlowLayout());
        establishSecureConnectionButton = new JButton("Establish Secure Connection");
        deleteCryptoStateButton = new JButton("Delete Crypto State");
        autoEstablishSecureConnectionCheckBox = new JCheckBox("Auto Establish");

        EstablishDeletePanel.add(establishSecureConnectionButton);
        EstablishDeletePanel.add(deleteCryptoStateButton);
        EstablishDeletePanel.add(autoEstablishSecureConnectionCheckBox);

        JPanel secureConnectContainer = new JPanel(new BorderLayout());
        secureConnectContainer.add(urlPanel, BorderLayout.NORTH);
        secureConnectContainer.add(credentialPanel, BorderLayout.CENTER);
        secureConnectContainer.add(errorField, BorderLayout.SOUTH);

        SecureConnectPanel.add(secureConnectContainer, BorderLayout.CENTER);
        SecureConnectPanel.add(EstablishDeletePanel, BorderLayout.SOUTH);

        StartSkipPanel = new JPanel(new FlowLayout());
        SkipButton = new JButton("Direct Connect");
        StartSkipPanel.add(SkipButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        FIRST_BLOCK.add(SecureConnectPanel, gbc);
        gbc.gridy = 1;
        gbc.weighty = 0;
        FIRST_BLOCK.add(StartSkipPanel, gbc);

        // --- AFTER_LOGIN setup ---
        ServerInfoPanel = new JPanel(new BorderLayout());
        ButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refresh = new JButton("Refresh");
        Disconnect = new JButton("Disconnect");
        encryptionCheckBox = new JCheckBox("Encryption");
        progressBar1 = new JProgressBar(0, 100);

        ButtonsPanel.add(refresh);
        ButtonsPanel.add(Disconnect);
        ButtonsPanel.add(encryptionCheckBox);
        ButtonsPanel.add(progressBar1);

        InfoTable = new JTable();
        TableScrollPanel = new JScrollPane(InfoTable);
        ServerInfoPanel.add(ButtonsPanel, BorderLayout.NORTH);
        ServerInfoPanel.add(TableScrollPanel, BorderLayout.CENTER);

        tabbedPane1 = new JTabbedPane();

        // Send Commands Panel
        SendCommandsPanel = new JPanel(new BorderLayout());
        ConnectToDevicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addressOfDeviceTextField = new JTextField("Address of device", 15);
        ConnectDevice = new JButton("Connect Device");
        DisconnectDevice = new JButton("Disconnect Device");
        forceEncryptionCommandCheckBox = new JCheckBox("Force Encryption");
        directCheckBox = new JCheckBox("Direct");

        ConnectToDevicePanel.add(addressOfDeviceTextField);
        ConnectToDevicePanel.add(ConnectDevice);
        ConnectToDevicePanel.add(DisconnectDevice);
        ConnectToDevicePanel.add(forceEncryptionCommandCheckBox);
        ConnectToDevicePanel.add(directCheckBox);

        JPanel commandBar = new JPanel(new BorderLayout());
        commandToSendTextField = new JTextField("Command to send");
        SendCommandButton = new JButton("Send");
        commandBar.add(commandToSendTextField, BorderLayout.CENTER);
        commandBar.add(SendCommandButton, BorderLayout.EAST);

        DeviceResponseTextArea = new JTextArea();
        DeviceResponseScrollPanel = new JScrollPane(DeviceResponseTextArea);

        SendCommandsPanel.add(ConnectToDevicePanel, BorderLayout.NORTH);
        SendCommandsPanel.add(commandBar, BorderLayout.CENTER);
        SendCommandsPanel.add(DeviceResponseScrollPanel, BorderLayout.SOUTH);

        // Reverse Commands Panel
        ReverseCmmandsPanel = new JPanel(new BorderLayout());
        JPanel reverseButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ConnectDeviceReverse = new JButton("Connect Device");
        DisconnectDeviceReverse = new JButton("Disconnect Device");
        forceEncryptionCommandCheckBoxReverse = new JCheckBox("Force Encryption");
        ReceiveResponseButtonReverse = new JButton("Receive Response");
        SendCommandButtonReverse = new JButton("Send");

        reverseButtons.add(ConnectDeviceReverse);
        reverseButtons.add(DisconnectDeviceReverse);
        reverseButtons.add(forceEncryptionCommandCheckBoxReverse);
        reverseButtons.add(ReceiveResponseButtonReverse);
        reverseButtons.add(SendCommandButtonReverse);

        CommandsTextAreaReverse = new JTextArea();
        ReverseCmmandsPanel.add(reverseButtons, BorderLayout.NORTH);
        ReverseCmmandsPanel.add(new JScrollPane(CommandsTextAreaReverse), BorderLayout.CENTER);

        tabbedPane1.addTab("Send Commands", SendCommandsPanel);
        tabbedPane1.addTab("Reverse Commands", ReverseCmmandsPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ServerInfoPanel, tabbedPane1);
        AFTER_LOGIN.add(splitPane, BorderLayout.CENTER);

        // --- DIRECT_CONNECT setup ---
        DIRECT_CONNECT.add(new JLabel("Direct Connect Mode - Not fully implemented in fallback", SwingConstants.CENTER),
                BorderLayout.CENTER);
        loginDirect = new JButton("Back");
        DIRECT_CONNECT.add(loginDirect, BorderLayout.SOUTH);
    }

    public RichkwarePanel(App app) {
        this.app = app;
        initialize();
        loadLoginPanel();

    }

    private void loadLoginPanel() {
        FIRST_BLOCK.setVisible(true);
        DIRECT_CONNECT.setVisible(false);
        AFTER_LOGIN.setVisible(false);
        MainFrame.pack();

        File urlFile = new File("url.conf");
        String filePassword = "test";
        String serverUrlJsonString = Crypto.getData(urlFile, filePassword, KEY_URL);
        boolean autoEstablish = Crypto.getData(urlFile, filePassword, KEY_AUTO_ESTABLISH).equalsIgnoreCase("true");
        if (serverUrlJsonString.equalsIgnoreCase("") || !autoEstablish) { // PRIMA APERTURA O SENZA STATO
            loadUrlPanel();
        } else { // auto-establish secure connection
            JSONObject urlJson = new JSONObject(serverUrlJsonString);
            autoEstablishSecureConnectionCheckBox.setSelected(autoEstablish);
            try {
                app.getController().getNetwork().setURL(urlJson.getString(SECUREDATA_PROTOCOLLO_KEY),
                        urlJson.getString(SECUREDATA_SERVER_KEY), urlJson.getString(SECUREDATA_PORT_KEY),
                        urlJson.getString(SECUREDATA_SERVICE_KEY));
                errorField.setText(" ");
            } catch (NetworkException ex) {
                ex.printStackTrace();
                // TODO GESTIRE ERRORE
            }
            app.getController().initSecureConnection(new RichkwareCallback() {
                @Override
                public void onSuccess(String response) {
                    loadCredentialPanel();
                    errorField.setText(" ");
                }

                @Override
                public void onFailure(String response) {
                    loadUrlPanel();
                    // TODO GESTIRE ERRORE
                    errorField.setText(response);
                }
            });
        }

        if (establishSecureConnectionButton.getActionListeners().length == 0) {
            establishSecureConnectionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (Component comp : urlPanel.getComponents()) // disable fields
                        comp.setEnabled(false);
                    String protocollo = protocoloField.getText();
                    String server = serverField.getText();
                    String port = portField.getText();
                    String service = serviceField.getText();
                    try {
                        app.getController().getNetwork().setURL(protocollo, server, port, service);
                        errorField.setText(" ");
                    } catch (NetworkException ex) {
                        ex.printStackTrace();
                        loadUrlPanel();
                        // TODO GESTIRE ERRORE
                    }
                    app.getController().initSecureConnection(new RichkwareCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // if success save url-data and load credential panel
                            JSONObject urlJson = new JSONObject()
                                    .put(SECUREDATA_PROTOCOLLO_KEY, protocollo)
                                    .put(SECUREDATA_SERVER_KEY, server)
                                    .put(SECUREDATA_PORT_KEY, port)
                                    .put(SECUREDATA_SERVICE_KEY, service);
                            Crypto.putData(urlFile, filePassword, KEY_URL, urlJson.toString());
                            loadCredentialPanel();
                            errorField.setText(" ");
                        }

                        @Override
                        public void onFailure(String response) {
                            // TODO GESTIRE ERRORE
                            loadUrlPanel();
                            errorField.setText(response);
                        }
                    });
                }
            });
        }

        if (loginButton.getActionListeners().length == 0) {
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    String email = emailField.getText();
                    String password = passwordField.getText();
                    app.getController().initSecureConnection(new RichkwareCallback() {
                        @Override
                        public void onSuccess(String response) {
                            app.getController().login(email, password, new RichkwareCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    errorField.setText(" ");
                                    loadDevicesPanel();
                                    errorField.setText(" ");
                                }

                                @Override
                                public void onFailure(String response) {
                                    errorField.setText(response);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String response) {
                            loadCredentialPanel();
                            // TODO GESTIRE ERRORE
                            errorField.setText(response);
                        }
                    });

                }
            });
        }

        if (SkipButton.getActionListeners().length == 0) {
            SkipButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    loadDirectConnectPanel();
                }
            });
        }
        if (deleteCryptoStateButton.getActionListeners().length == 0) {
            deleteCryptoStateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    app.getController().deleteCryptoState();
                    loadUrlPanel();
                    errorField.setText(" ");
                }
            });
        }
        if (autoEstablishSecureConnectionCheckBox.getItemListeners().length == 0) {
            autoEstablishSecureConnectionCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        Crypto.putData(urlFile, filePassword, KEY_AUTO_ESTABLISH, "true");
                    } else {
                        Crypto.putData(urlFile, filePassword, KEY_AUTO_ESTABLISH, "false");
                    }
                }
            });
        }
    }

    private void loadUrlPanel() {
        credentialPanel.setVisible(false);
        for (Component comp : urlPanel.getComponents())
            comp.setEnabled(true);
        urlPanel.setVisible(true);
        establishSecureConnectionButton.setVisible(true);
        SecureConnectPanel.setBorder(new TitledBorder(new LineBorder(Color.RED, 2), "SecureConnection",
                TitledBorder.LEFT, TitledBorder.TOP));
        MainFrame.pack();
    }

    private void loadCredentialPanel() {
        credentialPanel.setVisible(true);
        urlPanel.setVisible(false);
        establishSecureConnectionButton.setVisible(false);
        SecureConnectPanel.setBorder(new TitledBorder(new LineBorder(Color.GREEN, 2), "SecureConnection",
                TitledBorder.LEFT, TitledBorder.TOP));
        MainFrame.pack();
    }

    private void loadDevicesPanel() {
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

        if (refresh.getActionListeners().length == 0) {
            refresh.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshTable();
                }
            });
        }

        if (Disconnect.getActionListeners().length == 0) {
            Disconnect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // deviceList = new ArrayList<Device>();
                    // InfoTable.setModel(new DeviceTableModel(deviceList));

                    app.getController().logout(false, new RichkwareCallback() {
                        @Override
                        public void onSuccess(String response) {
                            deviceList.clear();
                            InfoTable.setModel(new DeviceTableModel(deviceList));
                            loadLoginPanel();
                        }

                        @Override
                        public void onFailure(String response) {
                            errorPanel(response);
                        }
                    });
                }
            });
        }
        loadConnectPanel();
    }

    private void loadConnectPanel() {
        Logger.info("loading openSocket panel");

        connectPanel(SendCommandButton, commandToSendTextField, DeviceResponseTextArea, ConnectDevice, directCheckBox,
                addressOfDeviceTextField, forceEncryptionCommandCheckBox, DisconnectDevice, Connect.DEFAULT);

        Logger.info("loaded openSocket panel");

        if (SendCommandButtonReverse.getActionListeners().length == 0) {
            SendCommandButtonReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command = CommandsTextAreaReverse.getText();
                    if (command.compareTo("") == 0 || command.compareTo("Command to send") == 0) {
                        Logger.error("Write the command to execute on device");
                    } else {
                        app.getController().reverseCommand(command, false, new RichkwareCallback() {
                            @Override
                            public void onSuccess(String s) {
                                // TODO COMMANDS SEND
                            }

                            @Override
                            public void onFailure(String response) {
                                errorPanel(response + ": " + response);
                            }
                        });
                    }
                }
            });
        }
        if (ConnectDeviceReverse.getActionListeners().length == 0) {
            ConnectDeviceReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int devicesCount = getSelectedDeviceCount();
                    Logger.info("connect");
                    if (devicesCount == 1) {
                        clearTable();
                        app.getController().connectDevice(getSelectedDevice());

                        enableInput();
                    } else if (devicesCount > 1) {
                        clearTable();
                        app.getController().connectDevice(getSelectedDevices());
                        enableInput();
                    } else {
                        errorPanel("Select a device");
                    }
                }
            });
        }
        if (ReceiveResponseButtonReverse.getActionListeners().length == 0) {
            ReceiveResponseButtonReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    app.getController().reverseCommandResponse(new RichkwareCallback() {
                        @Override
                        public void onSuccess(String response) {
                            response = new String(Base64.getUrlDecoder().decode(response));
                            CommandsTextAreaReverse.setText(response);
                        }

                        @Override
                        public void onFailure(String response) {
                            errorPanel(response);
                        }
                    });
                }
            });
        }
        if (DisconnectDeviceReverse.getActionListeners().length == 0) {
            DisconnectDeviceReverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    clearTable();
                    disableInput(Connect.REVERSE);
                }
            });
        }
    }

    private void loadDirectConnectPanel() {
        FIRST_BLOCK.setVisible(false);
        DIRECT_CONNECT.setVisible(true);
        AFTER_LOGIN.setVisible(false);
        MainFrame.pack();

        if (loginDirect.getActionListeners().length == 0) {
            loginDirect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    loadLoginPanel();
                }
            });
        }

        connectPanel(SendCommandButtonDirect, commandToSendTextFieldDirect, DeviceResponseTextAreaDirect,
                ConnectDeviceDirect, directCheckBoxDirect, addressOfDeviceTextFieldDirect,
                forceEncryptionCommandCheckBoxDirect, DisconnectDeviceDirect, Connect.DIRECT);
    }

    private void errorPanel(String err) {
        JOptionPane.showMessageDialog(MainFrame, err, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void connectPanel(JButton SendCommandButton, JTextField commandToSendTextField,
            JTextArea DeviceResponseTextArea, JButton ConnectDevice, JCheckBox directCheckBox,
            JTextField addressOfDeviceTextField, JCheckBox forceEncryptionCommandCheckBox, JButton DisconnectDevice,
            Connect connetionType) {
        devices = new ArrayList<>();

        disableInput(connetionType);

        if (SendCommandButton.getActionListeners().length == 0) {
            SendCommandButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command = commandToSendTextField.getText();
                    if (command.compareTo("") == 0 || command.compareTo("Command to send") == 0) {
                        Logger.error("Write the command to execute on device");
                    } else {
                        app.getController().sendCommand(command, new RichkwareCallback() {
                            @Override
                            public void onSuccess(String response) {
                                Logger.info("Response: " + response);
                                DeviceResponseTextArea.append(response);
                                DeviceResponseTextArea.setLineWrap(true);
                            }

                            @Override
                            public void onFailure(String error) {
                                Logger.error("Error: " + error);
                            }
                        });
                    }
                }
            });
        }

        if (ConnectDevice.getActionListeners().length == 0) {
            ConnectDevice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (directCheckBox.isSelected()) { // Direct command to selected devices from table
                        String ipport = addressOfDeviceTextField.getText();
                        device = new Device("", ipport.substring(0, ipport.indexOf(":")),
                                ipport.substring(ipport.indexOf(":") + 1), "", "", "", "", "");
                        app.getController().openSocket(device, forceEncryptionCommandCheckBox.isSelected(),
                                new RichkwareCallback() {
                                    @Override
                                    public void onSuccess(String response) {
                                        enableInput();
                                    }

                                    @Override
                                    public void onFailure(String response) {
                                        errorPanel(response);
                                    }
                                });
                    } else {
                        int devicesCount = getSelectedDeviceCount();
                        if (devicesCount == 1) {
                            clearTable();
                            device = getSelectedDevice();
                            if (device.getServerPort().compareTo("none") == 0) {
                                clearTable();
                                errorPanel("ServerPort of this device is closed");
                            } else {
                                app.getController().openSocket(device, forceEncryptionCommandCheckBox.isSelected(),
                                        new RichkwareCallback() {
                                            @Override
                                            public void onSuccess(String response) {
                                                addressOfDeviceTextField
                                                        .setText(device.getIp() + ":" + device.getServerPort());
                                                enableInput();
                                            }

                                            @Override
                                            public void onFailure(String response) {
                                                errorPanel(response);
                                            }
                                        });
                            }
                        } else if (devicesCount > 1) {
                            clearTable();
                            devices = getSelectedDevices();
                            // TODO check device ip and port
                            app.getController().openSocket(devices, forceEncryptionCommandCheckBox.isSelected(),
                                    new RichkwareCallback() {
                                        @Override
                                        public void onSuccess(String response) {
                                            addressOfDeviceTextField.setText("Multiple devices");
                                            enableInput();
                                        }

                                        @Override
                                        public void onFailure(String response) {
                                            errorPanel(response);
                                        }
                                    });
                        } else {
                            errorPanel("Select a device");
                        }
                    }
                }
            });
        }

        if (DisconnectDevice.getActionListeners().length == 0) {
            DisconnectDevice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearTable();
                    disableInput(connetionType);
                }
            });
        }

        if (directCheckBox.getActionListeners().length == 0) {
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
        if (addressOfDeviceTextField.getActionListeners().length == 0) {
            addressOfDeviceTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    // addressOfDeviceTextField.setText("");
                }
            });
        }
        if (addressOfDeviceTextField.getActionListeners().length == 0) {
            commandToSendTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    // commandToSendTextField.setText("");
                }
            });
        }

    }

    private void refreshTable() {
        // DO NOT replace with lambda
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO si può anche rimuovere
                    InfoTable.setModel(new DeviceTableModel(new ArrayList<>()));
                    progressBar1.setValue(0);

                    // if encryption check box is selected, RMC uses encryption to refresh the list
                    // of devices
                    app.getController().devicesList(encryptionCheckBox.isSelected(), new ListCallback() {
                        @Override
                        public void onSuccess(List<Device> response) {
                            deviceList = response;
                            // Logger.info("Devices list");
                            // for (Device device : response) {
                            // Logger.info("Device: " + device.getName());
                            // }
                            progressBar1.setValue(20);
                            // Logger.info("Before setModel");
                            InfoTable.setModel(new DeviceTableModel(deviceList));
                            // Logger.info("After setModel");
                            progressBar1.setValue(40);

                            updateRowHeights(InfoTable);
                        }

                        @Override
                        public void onFailure(String response) {

                        }
                    });

                    // InfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
            progressBar1.setValue((int) Math.round(count));
            jTable.setRowHeight(row, rowHeight);
        }
        progressBar1.setValue(100);
    }

    private int getSelectedDeviceCount() {
        // InfoTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
        // @Override
        // public Component getTableCellRendererComponent(JTable table, Object value,
        // boolean isSelected, boolean hasFocus, int row, int col) {
        // super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
        // col);
        // if (devices.contains(deviceList.get(row)) || device == deviceList.get(row)) {
        // setBackground(Color.GREEN);
        // setForeground(Color.WHITE);
        // } else {
        // setBackground(table.getBackground());
        // setForeground(table.getForeground());
        // }
        // return this;
        // }
        // });
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
        Logger.info("disable");
        commandToSendTextField.setEnabled(false);
        commandToSendTextFieldDirect.setEnabled(false);
        CommandsTextAreaReverse.setEnabled(false);
        directCheckBox.setEnabled(true);
        directCheckBoxDirect.setEnabled(true);
        forceEncryptionCommandCheckBoxReverse.setEnabled(true);
        if (connectionType == Connect.DIRECT) {
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
        SendCommandButtonReverse.setEnabled(false);
        DisconnectDevice.setEnabled(false);
        DisconnectDeviceDirect.setEnabled(false);
        DisconnectDeviceReverse.setEnabled(false);
        ConnectDevice.setEnabled(true);
        ConnectDeviceDirect.setEnabled(true);
        ConnectDeviceReverse.setEnabled(true);
    }

    private void enableInput() {
        Logger.info("enable");
        commandToSendTextField.setEnabled(true);
        commandToSendTextFieldDirect.setEnabled(true);
        CommandsTextAreaReverse.setEnabled(true);
        directCheckBox.setEnabled(false);
        directCheckBoxDirect.setEnabled(false);
        forceEncryptionCommandCheckBoxReverse.setEnabled(false);
        addressOfDeviceTextField.setEnabled(false);
        addressOfDeviceTextFieldDirect.setEnabled(false);
        SendCommandButton.setEnabled(true);
        SendCommandButtonDirect.setEnabled(true);
        SendCommandButtonReverse.setEnabled(true);
        DisconnectDevice.setEnabled(true);
        DisconnectDeviceDirect.setEnabled(true);
        DisconnectDeviceReverse.setEnabled(true);
        ConnectDevice.setEnabled(false);
        ConnectDeviceDirect.setEnabled(false);
        ConnectDeviceReverse.setEnabled(false);
    }

    private enum Connect {
        DEFAULT(0),
        DIRECT(1),
        REVERSE(2);

        private int code;

        Connect(int code) {
            this.code = code;
        }
    }

}