package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import it.richkmeli.RMC.view.View;
import org.json.JSONObject;

import javax.swing.*;

public class MainJFrame implements View {

    private App app;
    private JFrame MainFrame;
    private JPanel MainPanel;
    private JPanel LoginPanel;
    private JButton skipButton;
    private JPanel SkipPanel;
    private JPanel devicesPanel;
    private DevicesPanel devicesPanelClass;

    @Override
    public void initialize() {
        MainFrame = new JFrame();
        MainFrame.setTitle("Richkware-Manager-Client");
        JPanel panel = new JPanel();
        LoginPanel.add(new LoginPanel(app, new PanelCallback() {
            @Override
            public void onSuccess() {
                Logger.i("Loggato con successo -> azione");
                refresh();
            }

            @Override
            public void onFailure(String error) {
                Logger.i("Login fallito -> null");
            }
        }).getPanel());
        MainFrame.setContentPane(MainPanel);
        MainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainFrame.pack();
        MainFrame.setVisible(true);
    }

    public MainJFrame(App app) {
        this.app = app;
        initialize();
    }

    private void refresh() {
        String userStatus = app.getController().userStatus();
        Logger.i("userStatus: " + userStatus);

        if (ResponseParser.isStatusOK(userStatus)) {
            //PASSARE A DEVICES LIST
            boolean userAdmin = new JSONObject(ResponseParser.parseMessage(userStatus)).getBoolean("admin");
            Logger.i("User is admin: " + userAdmin);
//            MainPanel.remove(LoginPanel);
            MainPanel.removeAll();
            devicesPanelClass = new DevicesPanel(app, new PanelCallback() {
                @Override
                public void onSuccess() {
                    Logger.i("Logout effettuato -> azione");
                    refresh();
                }

                @Override
                public void onFailure(String error) {
                    Logger.i("Logout fallito -> null");
                }
            });
            MainPanel.add(devicesPanelClass.getPanel());
            MainPanel.add(new DirectConnect(app, devicesPanelClass).getPanel());
            MainFrame.pack();
        } else {
            if (ResponseParser.parseStatusCode(userStatus) == 2100) {
                MainPanel.removeAll();
                MainPanel.add(LoginPanel);
                MainFrame.pack();
            }
        }
    }


}
