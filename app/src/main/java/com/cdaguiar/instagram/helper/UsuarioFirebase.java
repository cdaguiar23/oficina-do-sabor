package com.cdaguiar.instagram.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cdaguiar.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void atualizarNomeUsuario(String nome) {
        try {
            // Usuário loagdo no app
            FirebaseUser usuarioLoagado = getUsuarioAtual();

            // Configurar objeto para alteraçõ do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
            usuarioLoagado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar nome de perfil");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Usuario getDadoUsuarioLogado() {
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setId(firebaseUser.getUid());

        if (firebaseUser.getPhotoUrl() == null) {
            usuario.setCaminhoFoto("");
        } else {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }

        return usuario;
    }
}