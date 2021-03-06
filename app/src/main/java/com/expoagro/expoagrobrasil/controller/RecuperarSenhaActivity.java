package com.expoagro.expoagrobrasil.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.expoagro.expoagrobrasil.R;
import com.expoagro.expoagrobrasil.util.Regex;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private EditText mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        Button btn_recuperar = (Button) findViewById(R.id.btn_recuperar);
        Button btn_voltar = (Button) findViewById(R.id.btn_voltar);
        mEmailView = (EditText) findViewById(R.id.campoEmail);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        btn_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperar();
            }
        });
        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RecuperarSenhaActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    Toast.makeText(RecuperarSenhaActivity.this, "Você não está conectado a Internet", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RecuperarSenhaActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(RecuperarSenhaActivity.this, LoginActivity.class);
        startActivity(it);
        finish();
    }

    public void recuperar() {
        dialog.setMessage("Processando Dados...");
        dialog.show();

        mEmailView.setError(null);

        String email = mEmailView.getText().toString();

        boolean cancelar = false;
        View focusView = null;

        if ( TextUtils.isEmpty(email) ) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancelar = true;
        } else if (!Regex.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_email_invalido));
            focusView = mEmailView;
            cancelar = true;
        }

        if (cancelar) {
            if (focusView != null) {
                focusView.requestFocus();
            }
            dialog.dismiss();
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RecuperarSenhaActivity.this, "E-mail enviado com sucesso.", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(RecuperarSenhaActivity.this, LoginActivity.class);
                                startActivity(it);
                                finish();
                            } else {
                                Toast.makeText(RecuperarSenhaActivity.this, "Falha ao enviar e-mail. Tente Novamente", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
        }
    }
}
