package beretta.prajo.galeriapublica;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView; //define o btNav como atributo dessa classe aqui

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //obtem uma referencia paara MainViewModel
        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        //obtem o btNav
        bottomNavigationView = findViewById(R.id.btNav);
        //seta em bottomNavigationView o “escutador” de eventos de selecao do menu
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            //chama o metodo onNavigationItemSelected para indicar qual opcao foi escolhida
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                //guarda em MainViewModel a opcao escolhida pelo usuario
                vm.setNavigationOpSelected(item.getItemId());
                //configurando as acoes que serao executadas para cada opcao selecionada no btNav
                switch (item.getItemId()){
                    //define o que
                    case R.id.gridViewOp:
                        GridViewFragment gridViewFragment = GridViewFragment.newInstance(); //cria fragmento
                        setFragment(gridViewFragment); //seta em MainActivity
                        break;

                    case R.id.listViewOp:
                        ListViewFragment listViewFragment = ListViewFragment.newInstance();
                        setFragment(listViewFragment);
                        break;
                }
                return true;
            }
        });
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }


}