package beretta.prajo.galeriapublica;

import android.app.Application;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import kotlinx.coroutines.CoroutineScope;

public class MainViewModel extends AndroidViewModel {
    int navigationOpSelected = R.id.gridViewOp; //guarda a opcao escolhida pelo usuario no menu btNav, ate quando a app perder o foco
    LiveData<PagingData<ImageData>> pageLv;

    public MainViewModel(@NonNull Application application){
        super(application);
        GalleryRepository galleryRepository = new GalleryRepository(application);
        GalleryPagingSource galleryPagingSource = new GalleryPagingSource(galleryRepository);
        Pager<Integer, ImageData> pager = new Pager(new PagingConfig(10), () -> galleryPagingSource);
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        pageLv = PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), viewModelScope);
    }

    //pega o valor da opcao
    public int getNavigationOpSelected(){
        return navigationOpSelected;
    }

    //seta o valor da opcao
    public void setNavigationOpSelected(int navigationOpSelected){
        this.navigationOpSelected = navigationOpSelected;
    }

    public LiveData<PagingData<ImageData>> getPageLv() {
        return pageLv;
    }
}
