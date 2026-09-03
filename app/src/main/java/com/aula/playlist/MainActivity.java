package com.aula.playlist;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterMusica.Acao{

    private String meuNome;
    private AdapterMusica adapterMusica;
    private CollectionReference playlist;
    private ListenerRegistration ativarRealTime;

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

        // carregar dados do usuario
        carregarUsuario();

        // Configuração da Recycle View
        RecyclerView rcMusica = findViewById(R.id.listaMusicas);
        rcMusica.setLayoutManager(new LinearLayoutManager(this));
        adapterMusica = new AdapterMusica();
        rcMusica.setAdapter(adapterMusica);

        //Conectar no database
        playlist = FirebaseFirestore.getInstance().collection("playlist");

        //Ativar modo REALTIME
        ativarRealTime = playlist.orderBy("votos", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null){
                        Log.e("Playlist", "Erro ao carregar músicas", error);
                        Toast.makeText(this, "Erro ao carregar músicas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshot == null) return;
                    List<Musica> musicas = snapshot.toObjects(Musica.class);
                    adapterMusica.atualizar(musicas);
                });
    }
    private void carregarUsuario(){
        // ler arquivo local
        SharedPreferences localStorage = getSharedPreferences("dadosLocal", MODE_PRIVATE);
        meuNome = localStorage.getString("meuNome", null);
        if (meuNome != null && !meuNome.equals("")){
            // usuario ja esta logado
            Toast.makeText(this, "Bem vindo" + meuNome, Toast.LENGTH_SHORT).show();
            return;
        }
        // solicitar o usuário
        EditText campo = new EditText(this);
        campo.setHint("Digite o seu nome");
        new AlertDialog.Builder(this)
                .setTitle("Quem é você?")
                .setMessage("Vai ao lado música que você digitar")
                .setView(campo)
                .setPositiveButton("Pronto", (dialogInteface, i) -> {
                    // Salvar no sharedPreferences
                    localStorage.edit().putString("meuNome", campo.getText().toString()).apply();
                }).show();
        // Configuração do novaMusica
        FloatingActionButton btAdicionar = findViewById(R.id.fabAdicionar);
        btAdicionar.setOnClickListener(v -> salvarMusica());
    }
    public void salvarMusica(){
        View telaFormulario = LayoutInflater.from(this).inflate(R.layout.tela_adicionar, null);
        EditText campoTitulo = telaFormulario.findViewById(R.id.campo_titulo);
        EditText campoArtista = telaFormulario.findViewById(R.id.campo_artista);

        new AlertDialog.Builder(this)
                .setTitle("Indicar música")
                .setView(telaFormulario)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Adicionar", (dialogInterface, i) -> {
                    if (campoTitulo.getText().toString().equals("") || campoArtista.getText().toString().equals("")){
                        Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    }
                    adicionarMusica(new Musica(
                            campoTitulo.getText().toString(),
                            campoArtista.getText().toString(),
                            meuNome,
                            0));
                }).show();
    }
    private void adicionarMusica(Musica musica){
        playlist.add(musica).addOnSuccessListener(runnable -> {
            Log.d("APP", "id doc: " + runnable.getId());
        })
                .addOnFailureListener(e -> {
                    Log.e("APP", "Erro ao adicionar música", e);
                    Toast.makeText(this, "Erro ao adicionar música", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void votar(Musica musica) {
        playlist.document(musica.getId()).update("votos", FieldValue.increment(1));
    }

    @Override
    public void excluir(Musica musica) {
        if (!meuNome.equals(musica.getIndicadoPor())){
            Toast.makeText(this, "Oxxxiii, não é o seu", Toast.LENGTH_SHORT).show();
            return;
        }
        playlist.document(musica.getId()).delete();
    }
}