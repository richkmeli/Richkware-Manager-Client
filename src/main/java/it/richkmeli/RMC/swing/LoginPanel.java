package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
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
import java.util.Arrays;
import java.util.List;

import static javafx.application.Platform.exit;

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
                String url = urlField.getText();

                String response = app.Login(url, email, password);
                Logger.i(response);
                try {
                    if (ResponseParser.isStatusOK(response)) {
                        //LOGIN EFFETTUATO
                        errorField.setText("Ciao");
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
}
