package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.controller.App;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.model.ModelException;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DevicesPanel {
    private App app;
    private List<Device> deviceList;
    private JPanel ServerInfoPanel;
    private JPanel ButtonsPanel;
    private JButton Disconnect;
    private JButton refresh;
    private JCheckBox encryptionCheckBox;
    private JProgressBar progressBar1;
    private JScrollPane TableScrollPanel;
    private JTable InfoTable;

    public DevicesPanel(App app, PanelCallback callback) {
        this.app = app;
        deviceList = new ArrayList<>();

        // jtable InfoTable initialisation
        try {
            InfoTable.setModel(new DeviceTableModel(deviceList));
        } catch (NullPointerException npe) {
            System.err.println(npe.getMessage());
        }

        RefreshTable();

        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RefreshTable();
            }
        });

        Disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                deviceList = new ArrayList<Device>();
//                InfoTable.setModel(new DeviceTableModel(deviceList));

                String response = app.getController().logout();
                try {
                    if (ResponseParser.isStatusOK(response)) {
                        //LOGOUT EFFETTUATO
                        callback.onSuccess();
                    } else {
                        //LOGOUT FALLITO
                        callback.onFailure(ResponseParser.parseMessage(response));
                    }
                } catch (JSONException exp) {
                    Logger.e("Logout error", exp);
                    callback.onFailure(exp.getMessage());
                }
            }
        });

    }

    private void errorPanel(String err) {
//        JOptionPane.showMessageDialog(MainFrame, err, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void RefreshTable() {
        try {
            progressBar1.setValue(0);

            // if encryption check box is selected, RMC uses encryption to refresh the list of devices
            deviceList = app.getController().refreshDevice(encryptionCheckBox.isSelected());
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

    public JPanel getPanel() {
        return ServerInfoPanel;
    }

    public int getSelectedDeviceCount() {
        return InfoTable.getSelectedRowCount();
    }

    public Device getSelectedDevice() {
        return deviceList.get(InfoTable.getSelectedRow());
    }

    public List<Device> getSelectedDevices() {
        List<Device> list = new ArrayList<>();
        for (int i : InfoTable.getSelectedRows())
            list.add(deviceList.get(i));
        return list;
    }
}

