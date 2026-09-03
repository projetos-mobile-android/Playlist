package com.aula.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterMusica extends RecyclerView.Adapter<AdapterMusica.MusicaViewHolder> {

    // Ativar opção no database
    public interface Acao{
        void votar(Musica musica);
        void excluir(Musica musica);
    }
    private Acao acao;

    public AdapterMusica(Acao arg){
        this.acao = arg;
    }
    private List<Musica> listaMusicas;

    public void atualizar(List<Musica> argumento){
        this.listaMusicas = argumento;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MusicaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tela = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musica, parent, false);
        return new MusicaViewHolder(tela);

    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        Musica item = listaMusicas.get(position);
        holder.txtTitulo.setText(item.getTitulo());
        holder.txtArtista.setText(item.getArtista());
        holder.txtIndicadoPor.setText(item.getIndicadoPor());
        holder.txtVotos.setText(String.valueOf(item.getVotos()));
        holder.btnVotar.setOnClickListener(v -> {
            acao.votar(item);
        });
        holder.btnExcluir.setOnClickListener(v -> {;
            acao.excluir(item);
        });
    }

    @Override
    public int getItemCount() {
        if (listaMusicas == null){
            return 0;
        }
        return listaMusicas.size();
    }

    // Vincular o obejto da tela ao View Holder
    public class MusicaViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtArtista, txtIndicadoPor, txtVotos;
        ImageButton btnVotar, btnExcluir;
        public MusicaViewHolder(@NonNull View itemView){
            super(itemView);
            txtArtista = itemView.findViewById(R.id.txtArtista);
            txtIndicadoPor = itemView.findViewById(R.id.txtIndicadoPor);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtVotos = itemView.findViewById(R.id.txtVotos);
            btnVotar = itemView.findViewById(R.id.btnVotar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
