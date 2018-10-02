package c.haicku.lectornif.ui.main;

import android.arch.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    String nif = null;

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNif() {
        return nif;
    }
}
