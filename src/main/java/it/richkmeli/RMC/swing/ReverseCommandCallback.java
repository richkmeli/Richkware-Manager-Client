package it.richkmeli.RMC.swing;

import java.util.Map;

public interface ReverseCommandCallback {

    public void onSuccess();

    public void onFailure(Map<String, String> responseMap);

}
