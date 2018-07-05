package e.q.week1_2;

import android.app.Activity;

import FirstTab.contactHelper;
import FirstTab.parsePhone;
import contact.Person;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class detailContact extends Activity {

    Person person;
    private boolean edit = false;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //데이터 가져오기
        Intent intent = getIntent();

        person = (Person) intent.getSerializableExtra("contact");

        RelativeLayout rel = findViewById(R.id.rel);

        TextView nameView = findViewById(R.id.nameView);
        final TextView phoneView = findViewById(R.id.phoneView);
        ImageView photoView = findViewById(R.id.photoView);
        final TextView emailView = findViewById(R.id.emailView);
        TextView addressView = findViewById(R.id.addressView);

        Button favorite = findViewById(R.id.favorite);
        ImageButton call = findViewById(R.id.call);
        ImageButton mail = findViewById(R.id.mail);
        Button delete = findViewById(R.id.delete);

        nameView.setText(person.getName());
        phoneView.setText(parsePhone.parse(person.getPhone()));
        emailView.setText(person.getEmail());
        addressView.setText(person.getAddress());

        if (person.getFavorite() == null) {
            person.setFavorite(false);
        }

        if(person.getFavorite()){
            favorite.setText(R.string.rf);
        } else {
            favorite.setText(R.string.af);
        }

        Bitmap bm = getImage(person.getPhoto_id());
        Log.d("bitmapsize", Integer.toString(bm.getByteCount()));
        RoundedBitmapDrawable bd = RoundedBitmapDrawableFactory.create(getResources(), bm);
        bd.setCornerRadius(Math.max(bm.getWidth(), bm.getHeight()) / 2.0f);
        bd.setAntiAlias(true);
        Log.d("bitmapsize", Integer.toString(bm.getByteCount()));
        photoView.setImageDrawable(bd);

        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + parsePhone.merge(phoneView.getText().toString())));
                try {
                    startActivity(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailView.getText().toString()});
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(detailContact.this);
                builder.setTitle("Contact Delete")        // 제목 설정
                        .setMessage("Are you sure to delete it?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                contactHelper.deleteContact(person, detailContact.this);
                                try {
                                    detailContact.this.deleteFile(person.getName() + "text");
                                } catch (Exception e) {;}
                                Toast.makeText(getApplicationContext(), "Successfully deleted.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });

        rel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                hideKeyboard();
            }
        });
    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        Person changed = new Person();
        changed.setId(person.getId());

        TextView nameView = findViewById(R.id.nameView);
        TextView phoneView = findViewById(R.id.phoneView);
        TextView emailView = findViewById(R.id.emailView);
        TextView addressView = findViewById(R.id.addressView);

        Log.d("namveview1", nameView.getText().toString());
        changed.setName(nameView.getText().toString());
        changed.setPhone(phoneView.getText().toString());
        changed.setEmail(emailView.getText().toString());
        changed.setAddress(addressView.getText().toString());

        changed.setFavorite(person.getFavorite());
        //contactHelper.edit(changed, this);

        //액티비티 닫기
        try {
            FileOutputStream fos = openFileOutput(changed.getName() + ".txt", MODE_PRIVATE);
            String str = Boolean.toString(person.getFavorite());
            fos.write(str.getBytes());
            fos.close();
        } catch (Exception e){;}

        Intent data = new Intent(detailContact.this, Tab1.class);
        data.putExtra("favorite", changed.getFavorite());
        data.putExtra("name", changed.getName());
        setResult(1, data);

        finish();
    }

    public void editMode(View v) {
        TextView nameView = findViewById(R.id.nameView);
        TextView phoneView = findViewById(R.id.phoneView);
        TextView emailView = findViewById(R.id.emailView);
        TextView addressView = findViewById(R.id.addressView);

        EditText nameEdit = findViewById(R.id.nameEdit);
        EditText phoneEdit = findViewById(R.id.phoneEdit);
        EditText emailEdit = findViewById(R.id.emailEdit);
        EditText addressEdit = findViewById(R.id.addressEdit);

        ImageButton call = findViewById(R.id.call);
        ImageButton mail = findViewById(R.id.mail);
        ImageButton back = findViewById(R.id.back);
        ImageButton cancel = findViewById(R.id.cancel);
        ImageButton edits = findViewById(R.id.edits);
        LinearLayout buttons = findViewById(R.id.buttons);

        //edit mode off
        if (edit == true) {
            edit = false;

            cancel.setVisibility(View.GONE);
            nameEdit.setVisibility(View.GONE);
            phoneEdit.setVisibility(View.GONE);
            emailEdit.setVisibility(View.GONE);
            addressEdit.setVisibility(View.GONE);

            nameView.setVisibility(View.VISIBLE);
            nameView.setText(nameEdit.getText().toString());

            phoneView.setVisibility(View.VISIBLE);
            phoneView.setText(parsePhone.parse(phoneEdit.getText().toString()));

            emailView.setVisibility(View.VISIBLE);
            emailView.setText(emailEdit.getText().toString());

            addressView.setVisibility(View.VISIBLE);
            addressView.setText(addressEdit.getText().toString());

            call.setVisibility(View.VISIBLE);
            mail.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            buttons.setVisibility(View.VISIBLE);

            edits.setImageResource(R.drawable.pencil);

            hideKeyboard();

            //TODO: edit mode off
            Toast.makeText(getApplicationContext(), "Changes Saved.", Toast.LENGTH_SHORT).show();
            return;
        }

        //edit mode on
        edit = true;

        cancel.setVisibility(View.VISIBLE);
        nameView.setVisibility(View.GONE);
        phoneView.setVisibility(View.GONE);
        emailView.setVisibility(View.GONE);
        addressView.setVisibility(View.GONE);

        nameEdit.setVisibility(View.VISIBLE);
        nameEdit.setText(nameView.getText().toString());

        phoneEdit.setVisibility(View.VISIBLE);
        phoneEdit.setText(parsePhone.merge(phoneView.getText().toString()));

        emailEdit.setVisibility(View.VISIBLE);
        emailEdit.setText(emailView.getText().toString());

        addressEdit.setVisibility(View.VISIBLE);
        addressEdit.setText(addressView.getText().toString());

        call.setVisibility(View.GONE);
        mail.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        buttons.setVisibility(View.GONE);

        edits.setImageResource(R.drawable.check);

        Toast.makeText(getApplicationContext(), "Edit Mode On", Toast.LENGTH_SHORT).show();
    }

    public void favoMode(View v) {
        Button button = findViewById(R.id.favorite);
        TextView textview = findViewById(R.id.nameView);
        boolean favorite = person.getFavorite();

        if (favorite == true) {
            person.setFavorite(false);
            button.setText(R.string.af);
            Toast.makeText(getApplicationContext(), textview.getText() + " is removed from favorites.", Toast.LENGTH_SHORT).show();
            return;
        }

        person.setFavorite(true);
        button.setText(R.string.rf);
        Toast.makeText(getApplicationContext(), textview.getText() + " is added to favorites.", Toast.LENGTH_SHORT).show();
        return;
    }

    public void cancel(View v) {
        TextView nameView = findViewById(R.id.nameView);
        TextView phoneView = findViewById(R.id.phoneView);
        TextView emailView = findViewById(R.id.emailView);
        TextView addressView = findViewById(R.id.addressView);

        EditText nameEdit = findViewById(R.id.nameEdit);
        EditText phoneEdit = findViewById(R.id.phoneEdit);
        EditText emailEdit = findViewById(R.id.emailEdit);
        EditText addressEdit = findViewById(R.id.addressEdit);

        ImageButton call = findViewById(R.id.call);
        ImageButton mail = findViewById(R.id.mail);
        ImageButton back = findViewById(R.id.back);
        ImageButton cancel = findViewById(R.id.cancel);
        LinearLayout buttons = findViewById(R.id.buttons);


        //edit mode off
        edit = false;

        cancel.setVisibility(View.GONE);
        nameEdit.setVisibility(View.GONE);
        phoneEdit.setVisibility(View.GONE);
        emailEdit.setVisibility(View.GONE);
        addressEdit.setVisibility(View.GONE);

        nameView.setVisibility(View.VISIBLE);
        nameEdit.setText(nameView.getText().toString());

        phoneView.setVisibility(View.VISIBLE);
        phoneEdit.setText(parsePhone.merge(phoneView.getText().toString()));

        emailView.setVisibility(View.VISIBLE);
        emailEdit.setText(emailView.getText().toString());

        addressView.setVisibility(View.VISIBLE);
        addressEdit.setText(addressView.getText().toString());

        call.setVisibility(View.VISIBLE);
        mail.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        buttons.setVisibility(View.VISIBLE);

        hideKeyboard();

        //TODO: edit mode off
        Toast.makeText(getApplicationContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
        return;
    }


    private Bitmap getImage(long imageDataRow) {
        Cursor c = getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                ContactsContract.CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[]{
                Long.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            Log.d("lengthd", Integer.toString(imageBytes.length));
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.profile);
        }
    }

    private void hideKeyboard() {
        EditText nameEdit = findViewById(R.id.nameEdit);
        EditText phoneEdit = findViewById(R.id.phoneEdit);
        EditText emailEdit = findViewById(R.id.emailEdit);
        EditText addressEdit = findViewById(R.id.addressEdit);

        imm.hideSoftInputFromWindow(nameEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(phoneEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(emailEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addressEdit.getWindowToken(), 0);
    }
}