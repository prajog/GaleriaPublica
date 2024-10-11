package beretta.prajo.galeriapublica;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {
    int navigationOpSelected = R.id.gridViewOp; //guarda a opcao escolhida pelo usuario no menu btNav, ate quando a app perder o foco

    public MainViewModel(@NonNull Application application){
        super(application);
    }

    //pega o valor da opcao
    public int getNavigationOpSelected(){
        return navigationOpSelected;
    }

    //seta o valor da opcao
    public void setNavigationOpSelected(int navigationOpSelected){
        this.navigationOpSelected = navigationOpSelected;
    }
}
