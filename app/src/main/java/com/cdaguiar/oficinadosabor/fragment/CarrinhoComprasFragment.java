package com.cdaguiar.oficinadosabor.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.cdaguiar.oficinadosabor.R;
import com.cdaguiar.oficinadosabor.activity.PerfilUsuarioActivity;
import com.cdaguiar.oficinadosabor.adapter.AdapterPesquisa;
import com.cdaguiar.oficinadosabor.helper.ConfiguracaoFirebase;
import com.cdaguiar.oficinadosabor.helper.RecyclerItemClickListener;
import com.cdaguiar.oficinadosabor.helper.UsuarioFirebase;
import com.cdaguiar.oficinadosabor.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarrinhoComprasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarrinhoComprasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Widget
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;
    private String idUsuarioLogado;

    public CarrinhoComprasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarrinhoComprasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarrinhoComprasFragment newInstance(String param1, String param2) {
        CarrinhoComprasFragment fragment = new CarrinhoComprasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerViewPesquisa);

        // Configurações iniciais
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Configurar RecyclerView
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerViewPesquisa.setAdapter(adapterPesquisa);

        // Configurar evento de clique
        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewPesquisa, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaUsuarios.get(position);
                Intent i = new Intent(getActivity(), PerfilUsuarioActivity.class);
                i.putExtra("usuarioSelecionado", usuarioSelecionado);
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        // Configurar SearchView
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });
        return view;
    }

    private void pesquisarUsuarios(String texto) {
        // Limpar lista
        listaUsuarios.clear();

        // Pesquisar usuários caso texto na pesquisa
        if (texto.length() >= 2) {
            Query query = usuariosRef.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Limpar a lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        // Verifica se é usuário logado e remove da lista
                        Usuario usuario = ds.getValue(Usuario.class);
                        if (idUsuarioLogado.equals(usuario.getId())) {
                            continue;
                        }
                        listaUsuarios.add(usuario);
                    }

                    // adiciona usuário na lista
                    adapterPesquisa.notifyDataSetChanged();
//                    int total = listaUsuarios.size();
//                    Log.i("totalusuarios", "total" + total);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}