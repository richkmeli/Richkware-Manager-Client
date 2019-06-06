package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
import it.richkmeli.RMC.controller.NetworkException;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import it.richkmeli.RMC.view.View;
import org.json.JSONException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class LoginPanel implements View {
    App app;
    private JFrame MainFrame;
    private List<Device> deviceList;
    private Device device;
    private JPanel loginPanel;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextField urlField;
    private JLabel errorField;
    private JTextField protocoloField;
    private JTextField serverField;
    private JTextField serviceField;
    private JTextField portField;

    public void initialize() {
        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        MainFrame.setContentPane(loginPanel);
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

    public LoginPanel(App appParam){
        this.app = appParam;
        initialize();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String email = emailField.getText();
                String password = passwordField.getText();
                try {
                    app.getController().getNetwork().setURL(protocoloField.getText(),serverField.getText(),portField.getText(),serviceField.getText());
                } catch (NetworkException e) {
                    errorField.setText(e.getMessage());
                }

                String response = app.getController().Login(email, password);
                try {
                    if (ResponseParser.isStatusOK(response)) {
                        //LOGIN EFFETTUATO
//                        errorField.setText(User());
                        errorField.setText(" ");
                        app.view = new MainPanel(app);
                    } else {
                        //LOGIN FALLITO
                        errorField.setText(ResponseParser.parseMessage(response));
                        errorField.setVisible(true);
                    }
                } catch (JSONException e){
                    Logger.e("Login error", e);
                    errorField.setText("Internal error");
                    errorField.setVisible(true);
                }
            }
        });
    }

    private void forceExit() {
        System.exit(0);
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

}
