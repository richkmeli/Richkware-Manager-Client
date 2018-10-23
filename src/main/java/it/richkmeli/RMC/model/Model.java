package it.richkmeli.RMC.model;

import java.util.List;

/**
 * Created by richk on 17/06/17.
 */
public interface Model {
    public List<Device> RefreshDevice(String url) throws ModelException;

    public boolean EditDevice(Device device) throws ModelException;

    public boolean RemoveDevice(String string) throws ModelException;

    public boolean IsDevicePresent(String name) throws ModelException;
}