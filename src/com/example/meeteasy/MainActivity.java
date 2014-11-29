package com.example.meeteasy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.meeteasy.model.Contact;
import com.example.meeteasy.model.MeetUp;
import com.example.meeteasy.util.HttpConnection;
import com.example.meeteasy.util.PathJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends ActionBarActivity
implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	BitmapDescriptor icon;
	MapFragment mapFragment;
	GoogleMap googleMap;
	ProgressDialog dialog;
	View createMeetUp;
	private String userPhoneNumber;
	AlertDialog.Builder phoneAlert;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;


	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_defaultcontact);
		Bundle receiveBundle = this.getIntent().getExtras();
		if (receiveBundle != null) {
			userPhoneNumber = receiveBundle.getString("userPhoneNumber");
		}
		if (userPhoneNumber == null) {
			phoneAlert = new AlertDialog.Builder(this);
			TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
			userPhoneNumber = tMgr.getLine1Number();
			if (userPhoneNumber == null || userPhoneNumber.equals("")) {
				phoneAlert.setTitle("Enter your phone number");
				phoneAlert.setMessage("Your phone number isn't stored or registered in your phone.");
				// Set an EditText view to get user input 
				final EditText phNo = new EditText(this);
				phoneAlert.setView(phNo);
				phoneAlert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Editable value = phNo.getText();
						userPhoneNumber = value.toString();
						phoneAlert.create().hide();
					}
				});
				phoneAlert.setCancelable(false);
				phoneAlert.show();
			}
		}
		createMeetUp = findViewById(R.id.createMeeting);
		createMeetUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MeetActivity.class);
				Bundle sendBundle = new Bundle();
				sendBundle.putString("userPhoneNumber", userPhoneNumber);
				intent.putExtras(sendBundle);
				startActivity(intent);
			}
		});

		mapFragment = (MapFragment)(getFragmentManager().findFragmentById(R.id.map));
		googleMap = mapFragment.getMap();
		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		// find meet up with start time closest to sys time 
		Contact[] members = new Contact[2];
		members[0] = new Contact("Radhikaa", "9952073549", null);
		members[0] = new Contact("Arvind Ram", "12345678", null);
		MeetUp upcomingMeet = new MeetUp(1, "hackathon", "happy coding", "11:00", members);
		// set source to meet up destination
		LatLng LOWER_MANHATTAN = new LatLng(40.722543,-73.998585);
		// find Locations of members of this meet up
		LatLng locations[] = new LatLng[2];
		locations[0] = new LatLng(40.7057, -73.9964); // member1
		locations[1] = new LatLng(40.7064, -74.0094); // member2
		// add markers on map
		addMarkerForDestination(LOWER_MANHATTAN, upcomingMeet.getName());
		addMarkersForMembers(locations, members);
		// draw routes from meetup spot to members
		String[] url = new String[2];
		int l = 0;
		for(LatLng location : locations) {
			url[l] = getMapsApiDirectionsUrl(LOWER_MANHATTAN, location);
			l++;
		}
		Log.i("MainActivity", "google maps rest url : "+url);
		ReadTask downloadTask = null;
		for(int i=0; i<url.length; i++) {
			downloadTask = new ReadTask();
			downloadTask.execute(url[i]);
		}

		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOWER_MANHATTAN,
				13));
		//dialog = new ProgressDialog(this);
	}
	private String getMapsApiDirectionsUrl(LatLng meetSpot, LatLng buddyLocation) {
		String waypoints = "waypoints=optimize:true|"
				+ meetSpot.latitude + "," + meetSpot.longitude
				+ "|" + "|" + buddyLocation.latitude + ","
				+ buddyLocation.longitude;

		String sensor = "sensor=false";
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params;
		return url;
	}
	private void addMarkerForDestination(LatLng destination, String name) {
		if (googleMap != null) {
			googleMap.addMarker(new MarkerOptions().position(destination)
					.title(name));
		}
	}
	private void addMarkersForMembers(LatLng[] locations, Contact[] members) {
		int i = 0;
		BitmapDescriptor iconForMember = null;
		if (googleMap != null) {
			for (LatLng location : locations) {
				if (members[i].getImage() == null) {
					googleMap.addMarker(new MarkerOptions().position(location)
							.title(members[i].getName())
							.icon(icon));
				} else {
					iconForMember = BitmapDescriptorFactory.fromBitmap(members[i].getImage());
					googleMap.addMarker(new MarkerOptions().position(location)
							.title(members[i].getName())
							.icon(iconForMember));
				}
			}
		}
	}

	private class ReadTask extends AsyncTask<String, String, String> {
		protected String doInBackground(String... url) {
			String data = "";
			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task for drawing routes running", e.toString());
			}
			Log.i("MainActivity", "json data from rest service :"+data);
			return data;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new ParserTask().execute(result);
		}
	}

	private class ParserTask extends AsyncTask<String, String, String> {
		List<List<HashMap<String, String>>> route;

		protected String doInBackground(String... jsonData) {
			JSONObject jObject;

			try {
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser();
				route = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}

		protected void onPostExecute(String result) {
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;

			// traversing through routes
			for (int i = 0; i < route.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = (List<HashMap<String, String>>) route.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = (HashMap<String, String>) path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(5);
				polyLineOptions.color(Color.BLUE);
			}
			googleMap.addPolyline(polyLineOptions);

		}
	}
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
		.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
}   

