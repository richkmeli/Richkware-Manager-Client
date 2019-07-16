package it.richkmeli.RMC.model;

import java.util.List;

/**
 * Created by richk on 17/06/17.
 */
public interface Model {
    List<Device> RefreshDevice(String url) throws ModelException;

    boolean EditDevice(Device device) throws ModelException;

    boolean RemoveDevice(String string) throws ModelException;

    boolean IsDevicePresent(String name) throws ModelException;
}