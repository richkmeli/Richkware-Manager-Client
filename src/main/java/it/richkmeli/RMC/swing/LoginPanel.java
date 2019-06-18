package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
import it.richkmeli.RMC.controller.network.NetworkException;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import org.json.JSONException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel {
    App app;
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

    public LoginPanel(App appParam, PanelCallback callback) {
        this.app = appParam;
//        initialize();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String email = emailField.getText();
                String password = passwordField.getText();
                try {
                    app.getController().getNetwork().setURL(protocoloField.getText(),serverField.getText(),portField.getText(),serviceField.getText());
                } catch (NetworkException e) {
                    errorField.setText(e.getMessage());
                    callback.onFailure(e.getMessage());
                }

                String response = app.getController().login(email, password);
                try {
                    if (ResponseParser.isStatusOK(response)) {
                        //LOGIN EFFETTUATO
//                        errorField.setText(User());
                        errorField.setText(" ");
//                        app.view = new MainPanel(app);
                        callback.onSuccess();
                    } else {
                        //LOGIN FALLITO
                        errorField.setText(ResponseParser.parseMessage(response));
                        errorField.setVisible(true);
                        callback.onFailure(ResponseParser.parseMessage(response));
                    }
                } catch (JSONException e){
                    Logger.e("Login error", e);
                    errorField.setText("Internal error");
                    errorField.setVisible(true);
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public JPanel getPanel() {
        return loginPanel;
    }

}

