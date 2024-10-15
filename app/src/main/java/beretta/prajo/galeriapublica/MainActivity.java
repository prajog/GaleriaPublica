package beretta.prajo.galeriapublica;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView; //define o btNav como atributo dessa classe aqui

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //obtem uma referencia paara MainViewModel
        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        //obtem o btNav
        bottomNavigationView = findViewById(R.id.btNav);
        //seta em bottomNavigationView o “escutador” de eventos de selecao do menu
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemSelectedListener() {
            //chama o metodo onNavigationItemSelected para indicar qual opcao foi escolhida
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //guarda em MainViewModel a opcao escolhida pelo usuario
                vm.setNavigationOpSelected(item.getItemId());
                //configurando as acoes que serao executadas para cada opcao selecionada no btNav
                switch (item.getItemId()){
                    //define o que sera feito ao selecionar gridview
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
    }

    //recebe um fragmento e seta ele
    void setFragment(Fragment fragment){
        //inicia uma transacao do gerenciador de fragmentos
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //seta o fragmento recebido como parametro no espaco definido pelo fragContainer
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        //indica que o fragmento faz parte da pilha de tela do botao voltar do android
        fragmentTransaction.addToBackStack(null);
        //commita a transacao
        fragmentTransaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Lista que guarda as permissoes
        List<String> permissions = new ArrayList<>();
        //adiciona a permissao de ler armazenamento externo à app
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        //usa o metodo abaixo pra verificar a permissao
        checkForPermissions(permissions);
    }

    //recebe como entrada uma lista de permissoes
    //metodo que exibe o dialog ao usuario pedindo confirmacao de uso do recurso
    private void checkForPermissions(List<String> permissions){
        List<String> permissionsNotGranted = new ArrayList<>();

        //verifica cada permissao
        for(String permission : permissions) {
            //se o usuario nao tiver confirmado ainda uma permissao ...
            if( !hasPermission(permission)){
                // ... a permissao eh posta em uma lista de permissoes nao confirmadas
                permissionsNotGranted.add(permission);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsNotGranted.size() > 0){
                //requisita ao usuario as permissoes ainda nao concedidas quando esse metodo for chamado
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }
        }
    }

    //verifica se uma permissao foi concedida ou nao
    private boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    //metodo chamado apos o usuario conceder ou nao as permissoes requisitadas
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        //verifica se a permissao foi concedida ou nao
        if(requestCode == RESULT_REQUEST_PERMISSION){
            for(String permission : permissions){
                if(!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        //verifica se alguma permissao essencial para o funcionamento da app, e exibe uma mensagem informando ao usuario que aquela permissao eh realmente necessaria
        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //verifica se a aplicacao ainda tem permissao para pedir permissao
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    //cria uma caixa de dialogo
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //requisita novamente as permissoes
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();

                }
            }
        }
    }
}