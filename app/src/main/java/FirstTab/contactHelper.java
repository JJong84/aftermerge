package FirstTab;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import contact.Person;
import e.q.week1_2.Tab1;

public class contactHelper {

    public contactHelper() {
    }

    public static void edit(Person person, Context mainContext) {
        String cid = person.getId();
        String cname = person.getName();
        String cphone = person.getPhone();
        String cemail = person.getEmail();
        String caddress = person.getAddress();
        String cnote = person.getNote();


        String[] item = {ContactsContract.RawContacts._ID};
        String phone = cphone;
        String phoneFormat = PhoneNumberUtils.formatNumber(phone);

        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(cid)};
        Cursor c = mainContext.getContentResolver().query(uri, item, selection, selectionArgs, null);
        c.moveToNext();
        int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND"
                + ContactsContract.Contacts.Data.MIMETYPE + " = ?";

        if (cname != "") {
            String[] nameParams = new String[]{cid, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
            Cursor nameCursor = mainContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, nameParams, null);

            if (nameCursor.getCount() > 0) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, nameParams)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, cname)
                        .build());
            } else {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, cname);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValues(values)
                        .build());
            }
        }

        if (cphone != "") {
            String[] phoneParams = new String[]{cid, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
            Cursor phoneCursor = mainContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, phoneParams, null);

            if (phoneCursor.getCount() > 0) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, phoneParams)
                        .withValue(ContactsContract.CommonDataKinds.Phone.DATA, cphone)
                        .build());
            } else {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Phone.DATA, cphone);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValues(values)
                        .build());
            }
        }

        if (cemail != "") {
            String[] emailParams = new String[]{cid, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
            Cursor emailCursor = mainContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, emailParams, null);

            if (emailCursor.getCount() > 0) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, emailParams)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, cemail)
                        .build());
            } else {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Email.DATA, cemail);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValues(values)
                        .build());
            }
        }

        if (cnote != "") {
            String[] noteParams = new String[]{cid, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
            Cursor noteCursor = mainContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, noteParams, null);

            if (noteCursor.getCount() > 0) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, noteParams)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, cnote)
                        .build());
            } else {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Note.NOTE, cnote);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValues(values)
                        .build());
            }
        }

        if (caddress != "") {
            String[] addressParams = new String[]{cid, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
            Cursor addressCursor = mainContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, addressParams, null);

            if (addressCursor.getCount() > 0) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, addressParams)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, caddress)
                        .build());
            } else {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.DATA, caddress);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValues(values)
                        .build());
            }
        }

        try {
            if (ops.size() > 0) {
                mainContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------

    public static void addContact(Person person, Context context) {

        String name = person.getName();
        String phone = person.getPhone();
        String email = person.getEmail();
        String address = person.getAddress();
        String note = person.getNote();

        Log.d("success", "success");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

//------------------------------------------------------ Names
        if (name != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
        }

//------------------------------------------------------ Mobile Number
        if (phone != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA, phone).build());
        }

//------------------------------------------------------ Email
        if (email != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email).build());
        }

//------------------------------------------------------ Organization
        if (address != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, address).build());
        }

        //------------------------------------------------------ Organization
        if (note != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, note).build());
        }

// Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("why", e.getMessage());
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteContact(Person person, Context context) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[]{person.getId()};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args)
                .build());
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
