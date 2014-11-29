package com.example.meeteasy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.meeteasy.model.Contact;

public class MeetActivity extends ActionBarActivity {
	String userPhoneNumber;
	Spinner contactsSpinner;
	TextView name;
	TextView description;
	TextView destination;
	TextView time;
	TextView members;
	Button create;
	Button back;
	ArrayList<Contact> userContacts = new ArrayList<Contact>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet);
		name = (TextView) findViewById(R.id.editText_enter_name);
		description = (TextView) findViewById(R.id.editText_enter_description);
		destination = (TextView) findViewById(R.id.editText_enter_destination);
		time = (TextView) findViewById(R.id.editText_enter_time);
		members = (TextView) findViewById(R.id.editText_contacts);
		Bundle receiveBundle = this.getIntent().getExtras();
		if (receiveBundle != null) {
			userPhoneNumber = receiveBundle.getString("userPhoneNumber");
		}
		contactsSpinner = (Spinner) findViewById(R.id.spinner);
		readContacts();
		Resources res = getResources();
        ContactsAdapter adapter = new ContactsAdapter(this, userContacts,res);
        contactsSpinner.setAdapter(adapter);
        create = (Button) findViewById(R.id.button_register);
        back = (Button) findViewById(R.id.button_back);
        final AlertDialog.Builder successAlert = new AlertDialog.Builder(this);
        create.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				successAlert.setTitle("Meet Easy");
				successAlert.setIcon(R.drawable.ic_launcher);
				successAlert.setMessage("Plan is ON!");
				final AlertDialog successDialog = successAlert.create();
				successDialog.show();
				// Hide after few seconds
				final Handler handler  = new Handler();
				final Runnable runnable = new Runnable() {
					@Override
					public void run() {
						if (successDialog.isShowing()) {
							successDialog.dismiss();
						}
					}
				};
				successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						handler.removeCallbacks(runnable);
					}
				});
				handler.postDelayed(runnable, 1500);
				// sending meet up data and current user's phone number to server
			}
		});
        back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MeetActivity.this, MainActivity.class);
				Bundle sendBundle = new Bundle();
				sendBundle.putString("userPhoneNumber", userPhoneNumber);
				intent.putExtras(sendBundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void readContacts() { 
		String id = null;
		String phone = "";
		String image_uri = ""; 
		Bitmap bitmap = null; 
		Contact contact = null;
		ContentResolver cr = getContentResolver(); 
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);   
		if (cur.getCount() > 0) { 
			while (cur.moveToNext()) { 
				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)); 
				image_uri = cur .getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
				final String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				final String phoneFlag = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (Integer.parseInt(phoneFlag) > 0) { 
					Cursor pCur = cr.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null); 
					while (pCur.moveToNext()) { 
						phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
						// check if this phone number has a registered ID with the server : yes => proceed ; no => do not add this contact to userContacts
					}
					pCur.close(); 
					if (image_uri != null) { 
						try { 
							bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image_uri));
						} catch (FileNotFoundException e) { 
							e.printStackTrace(); 
						} catch (IOException e) { 
							e.printStackTrace(); 
						} 
					} else {
						bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_defaultcontact);
					} 
					//contactImage = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap, 100, 100, true));
					contact = new Contact(name, phone, bitmap);
					userContacts.add(contact);
				} 
			}
		}
	}
}
