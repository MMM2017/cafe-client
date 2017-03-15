package ru.lazycodersinc.smartcafeclient;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import ru.lazycodersinc.smartcafeclient.model.AppState;
import ru.lazycodersinc.smartcafeclient.model.Dish;
import ru.lazycodersinc.smartcafeclient.model.MenuAdapter;

public class WaiterActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{

	private View currentSublayout;

	private Dialog dishPopup;
	private TextView dishPopupName, dishPopupQuantity, dishPopupDescription;
	private Button dishPopupOrderButton;
	private EditText dishPopupComment, dishPopupAmount;

	private Toast toast;

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
		switchLayout(SubLayout.CATEGORIES);

		// popup initialization
		dishPopup = new Dialog(this);
		dishPopup.setContentView(R.layout.dish_description_popup_layout);
		dishPopupName = (TextView) dishPopup.findViewById(R.id.popupDishName);
		dishPopupQuantity = (TextView) dishPopup.findViewById(R.id.popupDishQuantity);
		dishPopupDescription = (TextView) dishPopup.findViewById(R.id.popupDescription);
		dishPopupAmount = (EditText) dishPopup.findViewById(R.id.popupAmount);
		dishPopupComment = (EditText) dishPopup.findViewById(R.id.popupComment);
		dishPopupOrderButton = (Button) dishPopup.findViewById(R.id.popupOrderButton);

		// toast
		toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
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
				break;

			case R.id.nav_menu:
				switchLayout(SubLayout.MENU_LIST);
				break;

			case R.id.nav_notifications:
				switchLayout(SubLayout.NOTIFICATIONS);
				break;

			case R.id.nav_make_order:
				break;

			case R.id.nav_orders:
				break;
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
				final MenuAdapter menuAdapter = new MenuAdapter(this, AppState.getMenuCache());
				menuAdapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener()
				{
					@Override
					public void onItemClick(View view, int i, MenuAdapter ma)
					{
						final Dish d = (Dish) ma.getItem(i);
						dishPopup.setTitle("Order " + d.name);

						dishPopupName.setText(d.name);
						dishPopupQuantity.setText(d.getQuantityString());
						dishPopupAmount.setText("1");
						dishPopupComment.setText("");
						dishPopupDescription.setText(d.description);

						dishPopupOrderButton.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View view)
							{
								orderDish(d,
									Integer.parseInt(dishPopupAmount.getText().toString()),
									dishPopupComment.getText().toString());
								dishPopup.hide();
							}
						});

						dishPopup.show();
					}
				});
				menuAdapter.setOnItemButtonClickListener(new MenuAdapter.OnItemButtonClickListener()
				{
					@Override
					public void onItemButtonClick(int position, MenuAdapter adapter)
					{
						Dish d = (Dish) adapter.getItem(position);
						orderDish(d, 1, "");
					}
				});
				ListView list = (ListView) findViewById(R.id.menuListView);
				list.setAdapter(menuAdapter);
				list.setEmptyView(findViewById(R.id.noDishesMessage));

				break;

			case NOTIFICATIONS:

				ListView notiesList = (ListView) findViewById(R.id.notificationsListView);
				notiesList.setEmptyView(findViewById(R.id.noNotificationsMessage));

				break;
		}
	}

	private void orderDish(Dish d, int amount, String comment)
	{
		toast.setText("Ordered " + amount + " of " + d.name);
		toast.show();
	}

	private enum SubLayout
	{
		CATEGORIES(R.layout.menu_cat_view_layout),
		MENU_LIST(R.layout.menu_list_view_layout),
		ORDERS_LIST(0),
		NOTIFICATIONS(R.layout.notifications_view_layout);

		public int layoutId;

		SubLayout(int id) { layoutId = id; }
	}
}
