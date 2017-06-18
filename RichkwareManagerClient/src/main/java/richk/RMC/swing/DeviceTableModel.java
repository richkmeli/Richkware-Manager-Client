package richk.RMC.swing;

import richk.RMC.model.Device;

import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richk on 17/06/17.
 */
public class DeviceTableModel implements TableModel {
    private List<Device> deviceList;

    public DeviceTableModel(List<Device> deviceList1) {
        super();
        this.deviceList = deviceList1;
    }

    public int getRowCount() {
        return deviceList.size();
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int columnIndex) {
        switch(columnIndex){
            case 0: return "name";
            case 1: return "IP";
            case 2: return "serverPort";
            case 3: return "lastConnection";
            default: throw new InvalidParameterException("Column Index not valid");
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < deviceList.size() && rowIndex >= 0){

            Device device = deviceList.get(rowIndex);
            switch (columnIndex){
                case 0: return device.getName();
                case 1: return device.getIP();
                case 2: return device.getServerPort();
                case 3: return device.getLastConnection();
                default: throw new InvalidParameterException("Column Index not valid");
            }
        }else{
            throw new InvalidParameterException("Row Index not valid");
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    public void addTableModelListener(TableModelListener l) {

    }

    public void removeTableModelListener(TableModelListener l) {

    }
}
