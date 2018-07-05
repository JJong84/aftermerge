package e.q.week1_2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import FirstTab.contactHelper;
import FirstTab.parsePhone;
import contact.Person;

public class ContactAdd extends Activity {

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        LinearLayout lil = findViewById(R.id.lil);

        lil.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                hideKeyboard();
            }
        });
    }

    //확인 버튼 클릭
    public void mOnOk(View v) {
        EditText addName = findViewById(R.id.addName);
        EditText addPhone = findViewById(R.id.addPhone);
        EditText addEmail = findViewById(R.id.addEmail);
        EditText addad = findViewById(R.id.addad);
        EditText addNote = findViewById(R.id.addNote);

        if(addName.getText() == null){
            Toast.makeText(getApplicationContext(), "Please fill the name.", Toast.LENGTH_SHORT).show();
            return;
        }

        Person add = new Person();

        add.setName(addName.getText().toString());
        add.setPhone(addPhone.getText().toString());
        add.setEmail(addEmail.getText().toString());
        add.setAddress(addad.getText().toString());
        add.setNote(addNote.getText().toString());

        hideKeyboard();

        contactHelper.addContact(add, this);
        setResult(0);
        finish();
    }

    public void mOnCancel(View v){
        hideKeyboard();
        Toast.makeText(getApplicationContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
        setResult(0);
        finish();
    }

    private void hideKeyboard() {
        EditText addName = findViewById(R.id.addName);
        EditText addPhone = findViewById(R.id.addPhone);
        EditText addEmail = findViewById(R.id.addEmail);
        EditText addad = findViewById(R.id.addad);
        EditText addNote = findViewById(R.id.addNote);

        imm.hideSoftInputFromWindow(addName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addPhone.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addEmail.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addad.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addNote.getWindowToken(), 0);
    }
}