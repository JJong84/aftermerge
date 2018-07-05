package e.q.week1_2;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.Collator;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import FirstTab.SoundSearcher;
import FirstTab.contactHelper;
import FirstTab.parsePhone;
import contact.Person;

public class Tab1 extends AppCompatActivity {

    private String info;
    private ListView lv;
    private ListViewAdapter ca = null;
    private ArrayList<Person> contact;
    private List<Person> result;
    private EditText searchText;
    private FloatingActionButton add;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab1);

        searchText = findViewById(R.id.searchText);
        lv = findViewById(R.id.lv);
        result = new ArrayList<Person>();
        getContact();

        contact = new ArrayList<Person>();
        contact.addAll(result);

        //merge();
        //parser();

        Collections.sort(result, myComparator);
        ca = new ListViewAdapter(result, this);
        lv.setAdapter(ca);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = searchText.getText().toString();
                search(text);
            }
        });

        add = findViewById(R.id.floating);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Tab1.this, ContactAdd.class);
                startActivityForResult(intent, 0);
            }
        });

        //click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Tab1.this, detailContact.class);
                intent.putExtra("contact", result.get(i));
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        result = new ArrayList<Person>();

        getContact();

        contact = new ArrayList<Person>();
        contact.addAll(result);

        //favorite
        if (resultCode == 1)

        {
            String name = data.getStringExtra("name");
            Boolean favorite = data.getBooleanExtra("favorite", false);
            Log.d("favorite", Boolean.toString(favorite));
            Log.d("namee", name);
            if (favorite) {
                for (int i = 0; i < contact.size(); i++) {
                    Log.d("contactName", contact.get(i).getName());
                    if (name.equals(contact.get(i).getName())) {
                        contact.get(i).setFavorite(true);
                        Log.d("favorite2", Boolean.toString(contact.get(i).getFavorite()));
                        break;

                    }
                }
            }
        }

        //merge();
        //parser();
        Collections.sort(result, myComparator);
        ca = new ListViewAdapter(result, this);
        lv.setAdapter(ca);

        searchText.addTextChangedListener(new

                                                  TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                      }

                                                      @Override
                                                      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                      }

                                                      @Override
                                                      public void afterTextChanged(Editable editable) {
                                                          String text = searchText.getText().toString();
                                                          search(text);
                                                      }
                                                  });

        add =

                findViewById(R.id.floating);
        add.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                Intent intent = new Intent(Tab1.this, ContactAdd.class);
                startActivity(intent);
            }
        });

        //click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Tab1.this, detailContact.class);
                intent.putExtra("contact", result.get(i));
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * protected void onResume(Bundle savedInstanceState) {
     * <p>
     * super.onCreate(savedInstanceState);
     * setContentView(R.layout.activity_tab1);
     * <p>
     * searchText = findViewById(R.id.searchText);
     * lv = findViewById(R.id.lv);
     * result = new ArrayList<Person>();
     * <p>
     * getContact();
     * <p>
     * contact = new ArrayList<Person>();
     * contact.addAll(result);
     * <p>
     * merge();
     * parser();
     * <p>
     * ca = new ListViewAdapter(result, this);
     * lv.setAdapter(ca);
     * <p>
     * searchText.addTextChangedListener(new TextWatcher() {
     *
     * @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
     * }
     * @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
     * }
     * @Override public void afterTextChanged(Editable editable) {
     * String text = searchText.getText().toString();
     * search(text);
     * }
     * });
     * <p>
     * //click
     * lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
     * @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
     * Intent intent = new Intent(Tab1.this, detailContact.class);
     * intent.putExtra("contact", result.get(i));
     * startActivityForResult(intent, 0);
     * }
     * });
     * }
     */

    // Get contact
    void getContact() {
        Uri muri = ContactsContract.Contacts.CONTENT_URI;
        Uri curi = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri euri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        Uri auri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        Uri nuri = ContactsContract.Data.CONTENT_URI;

        Person person;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        String[] mainProjection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        String[] contactProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID
        };

        String[] emailProjection = new String[]{
                ContactsContract.CommonDataKinds.Email.DATA
        };

        String[] addressProjection = new String[]{
                ContactsContract.CommonDataKinds.StructuredPostal.DATA,
        };

        ArrayList<Person> contactlist = new ArrayList<>();
        Cursor mainCursor = this.getContentResolver().query(muri, mainProjection, null, null, sortOrder);

        while (mainCursor.moveToNext()) {
            person = new Person();
            person.setId(mainCursor.getString(0));
            person.setName(mainCursor.getString(1));

            Cursor contactCursor = this.getContentResolver().query(curi, contactProjection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + person.getId(), null, sortOrder);

            while (contactCursor.moveToNext()) {
                String phone = contactCursor.getString(0);
                person.setPhone(phone);
                Log.d("phonephone", phone);

                Long photo_id = contactCursor.getLong(1);
                person.setPhoto_id(photo_id);
                Log.d("loops", "contact");
            }
            contactCursor.close();

            Cursor emailCursor = this.getContentResolver().query(euri, emailProjection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + person.getId(), null, sortOrder);

            while (emailCursor.moveToNext()) {
                String email = emailCursor.getString(0);
                person.setEmail(email);
                Log.d("loops", "email");
            }
            emailCursor.close();

            Cursor addressCursor = this.getContentResolver().query(auri, addressProjection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + person.getId(), null, sortOrder);

            while (addressCursor.moveToNext()) {
                String address = addressCursor.getString(0);
                person.setAddress(address);
                Log.d("loops", "address");
            }
            addressCursor.close();
            /**
             String noteSelection = ContactsContract.Data.CONTACT_ID + " = ? AND " +;
             String[] noteParams = new String[]{strContactId}
             Cursor noteCursor = this.getContentResolver().query(nuri, null, )
             */


            contactlist.add(person);
            Log.d("loops", "main");

            try {
                FileInputStream fis = openFileInput(person.getName() + ".txt");
                byte[] data = new byte[fis.available()];
                while (fis.read(data) != -1) {
                    ;
                }
                fis.close();
                String bool = new String(data);
                if (bool.equals("true")) {
                    person.setFavorite(true);
                } else {
                    person.setFavorite(false);
                }
            } catch (Exception e) {
                ;
            }
        }
        mainCursor.close();

        Log.d("passs", "sa");
        result = contactlist;
    }


    private class ListViewAdapter extends BaseAdapter {
        private Context context;
        private List<Person> ld;
        private LayoutInflater inflater;
        private ViewHolder holder;

        public ListViewAdapter(List<Person> list, Context context) {
            this.ld = list;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return ld.size();
        }

        @Override
        public Object getItem(int position) {
            return ld.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.lv_row, null);

                holder = new ViewHolder();

                holder.pic = convertView.findViewById(R.id.pic);
                holder.name = convertView.findViewById(R.id.name);
                holder.phone = convertView.findViewById(R.id.phone);
                holder.star = convertView.findViewById(R.id.star);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Person data = ld.get(position);

            holder.name.setText(data.getName());
            holder.phone.setText(parsePhone.parse(data.getPhone()));

            if (data.getFavorite() == null) {
                holder.star.setVisibility(View.GONE);
            } else {
                if (data.getFavorite() == true) {
                    holder.star.setVisibility(View.VISIBLE);
                } else {
                    holder.star.setVisibility(View.GONE);
                }
            }

            Bitmap bm = getImage(data.getPhoto_id());


            RoundedBitmapDrawable bd = RoundedBitmapDrawableFactory.create(getResources(), bm);
            bd.setCornerRadius(Math.max(bm.getWidth(), bm.getHeight()) / 2.0f);
            bd.setAntiAlias(true);

            holder.pic.setImageDrawable(bd);

            return convertView;
        }

        //listview
        private class ViewHolder {
            public ImageView pic;
            public TextView name;
            public TextView phone;
            public ImageView star;
        }

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
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.profile);
        }
    }


    public void search(String text) {
        Log.d("recieve", text);
        result.clear();

        if (text.length() == 0) {
            Log.d("route1", "route1");
            result.addAll(contact);
        } else {
            for (int i = 0; i < contact.size(); i++) {
                Person person = contact.get(i);
                if ((person.getName().toLowerCase().contains(text)) || SoundSearcher.matchString(person.getName(), text) || (person.getPhone().contains(text)) || (person.getEmail().contains(text))) {
                    result.add(person);
                }
            }
        }

        ca.notifyDataSetChanged();
    }


    //Comparator 를 만든다.
    Comparator<Person> myComparator = new Comparator<Person>() {

        @Override
        public int compare(Person object1, Person object2) {
            if (object1.getFavorite() == object2.getFavorite()) {
                return 0;
            } else if (object1.getFavorite() == true) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    /**
     //merge to string
     void merge() {
     //cursor = null;

     StringBuilder sb = new StringBuilder("[");
     Person person;
     int size = contact.size();

     for (int i = 0; i < size; i++) {
     person = contact.get(i);
     sb.append("{'");
     sb.append("name");
     sb.append("':'");
     sb.append(person.getName());
     sb.append("','");
     sb.append("phone");
     sb.append("':'");
     sb.append(person.getPhone());
     sb.append("','");
     sb.append("email");
     sb.append("':'");
     sb.append(person.getEmail());
     sb.append("','");
     sb.append("address");
     sb.append("':'");
     sb.append(person.getAddress());

     if (i == size - 1) {
     sb.append("'}]");
     break;
     }

     sb.append("'},");
     }
     info = sb.toString();
     Log.d("JJong", info);
     }


     //json parse
     void parser() {
     try {
     JSONArray jarray = new JSONArray(info);
     // JSONArray 생성
     for (int i = 0; i < jarray.length(); i++) {
     JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
     String name = jObject.getString("name");
     String phone = jObject.getString("phone");
     ;
     }

     } catch (JSONException e) {
     e.printStackTrace();
     }
     }
     */
}