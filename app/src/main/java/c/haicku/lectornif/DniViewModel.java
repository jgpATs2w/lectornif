package c.haicku.lectornif;

import android.arch.lifecycle.MutableLiveData;

import android.arch.lifecycle.ViewModel;

public class DniViewModel extends ViewModel {

    private MutableLiveData<String> dniNumber;

    public MutableLiveData<String> getDni$() {
        if (dniNumber == null) {
            dniNumber = new MutableLiveData<String>();
        }
        return dniNumber;
    }

}
