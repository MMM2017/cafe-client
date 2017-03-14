package ru.lazycodersinc.smartcafeclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import ru.lazycodersinc.smartcafeclient.model.AppState;
import ru.lazycodersinc.smartcafeclient.model.Dish;
import ru.lazycodersinc.smartcafeclient.model.MenuAdapter;

import java.util.ArrayList;

public class WaiterActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{

	private View currentSublayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_officiant);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// that (generated) stuff with sidebar
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		// and custom values insertion
		View header = navigationView.getHeaderView(0);
		TextView userLabel = (TextView) header.findViewById(R.id.navHeaderLoggedIn);
		userLabel.setText("Currently logged in as " + AppState.currentUser().login);
		TextView verLabel = (TextView) header.findViewById(R.id.navHeaderClientVersion);
		verLabel.setText(AppState.getAppVersion());

		// and now, sublayout initialization
		currentSublayout = findViewById(R.id.waiterPlaceholder);
		switchLayout(SubLayout.MENU_LIST);
	}

	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.officiant, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		switch (id)
		{
			case R.id.nav_logout:
				Intent i = new Intent(this, LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void switchLayout(SubLayout to)
	{
		// all that stuff to replace views
		ViewGroup parent = (ViewGroup) currentSublayout.getParent();
		int index = parent.indexOfChild(currentSublayout);
		parent.removeView(currentSublayout);
		currentSublayout = getLayoutInflater().inflate(to.layoutId, parent, false);
		parent.addView(currentSublayout, index);

		switch (to)
		{
			case CATEGORIES:
				String[] cats = new String[] { "First course", "Second course", "Drinks", "Desserts" };
				ArrayAdapter<String> adapter =
						new ArrayAdapter<>(this, R.layout.menu_category_view, R.id.menuCategoryLabel, cats);
				GridView catsView = (GridView) findViewById(R.id.menuCatsGridView);
				catsView.setAdapter(adapter);
				break;

			case MENU_LIST:
				ArrayList<Dish> test = new ArrayList<>();
				Dish d = new Dish();
				d.name = "Apple"; d.portionSize = 1; d.portionUnit = "unit";
				test.add(d);

				d = new Dish();
				d.name = "Potato"; d.portionSize = 1; d.portionUnit = "kg";
				test.add(d);

				d = new Dish();
				d.name = "Soup"; d.portionSize = 1; d.portionUnit = "vedro";
				test.add(d);

				MenuAdapter menuAdapter = new MenuAdapter(this, to.layoutId, test);
				ListView list = (ListView) findViewById(R.id.menuListView);
				list.setAdapter(menuAdapter);

				break;
		}
	}

	private enum SubLayout
	{
		CATEGORIES(R.layout.menu_cat_view_layout),
		MENU_LIST(R.layout.menu_list_view_layout),
		ORDERS_LIST(0),
		NOTIFICATIONS(0);

		public int layoutId;

		SubLayout(int id) { layoutId = id; }
	}
}
