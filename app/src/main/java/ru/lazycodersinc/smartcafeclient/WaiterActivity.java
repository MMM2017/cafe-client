package ru.lazycodersinc.smartcafeclient;

import android.app.Dialog;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import ru.lazycodersinc.smartcafeclient.waiter.MenuCategoriesFragment;
import ru.lazycodersinc.smartcafeclient.waiter.MenuCategoryActivity;
import ru.lazycodersinc.smartcafeclient.waiter.MenuListFragment;

public class WaiterActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
				   MenuListFragment.OnFragmentInteractionListener,
				   MenuCategoriesFragment.OnFragmentInteractionListener
{

	private View currentSublayout;
	private SubLayout currentSublayoutType;

	private MenuAdapter menuAdapter;

	private Toast toast;

	private FragmentManager fMgr;
	private ViewGroup contentRoot;

	private boolean startingFirstTime = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (!AppState.isLoggedIn())
		{
			// disallow to run this for unauthorized user
			redirectToLoginActivity();
		}
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

		// fragments
		fMgr = getSupportFragmentManager();
		contentRoot = (ViewGroup) findViewById(R.id.content_officiant);

		// initial fragment
		if (savedInstanceState == null)
		{
			fMgr.beginTransaction()
				.add(R.id.content_officiant, MenuCategoriesFragment.newInstance())
				.commit();
		}
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

	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		switch (id)
		{
			case R.id.nav_logout:
				AppState.logOut();
				redirectToLoginActivity();
				break;

			case R.id.nav_menu:
				// switchLayout(SubLayout.MENU_LIST);
				fMgr.beginTransaction()
					.replace(R.id.content_officiant, MenuListFragment.newInstance(-1, ""))
					.commit();
				break;

			case R.id.nav_notifications:
				// switchLayout(SubLayout.NOTIFICATIONS);
				break;

			case R.id.nav_make_order:
				// toast.setText("Not implemented yet");
				// toast.show();
				break;

			case R.id.nav_orders:
				// switchLayout(SubLayout.ORDERS_LIST);
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void switchLayout(SubLayout to)
	{
		if (to == currentSublayoutType) return;
		currentSublayoutType = to;

		// all that stuff to replace views
		ViewGroup parent = (ViewGroup) currentSublayout.getParent();
		int index = parent.indexOfChild(currentSublayout);
		parent.removeView(currentSublayout);
		currentSublayout = getLayoutInflater().inflate(to.layoutId, parent, false);
		parent.addView(currentSublayout, index);

		setTitle(to.titleId);

		switch (to)
		{
			case CATEGORIES:
				final ArrayAdapter<Dish.Type> adapter = new ArrayAdapter<>(
					this,
					R.layout.menu_category_view,
					R.id.menuCategoryLabel,
					Dish.Type.values());
				GridView catsView = (GridView) findViewById(R.id.menuCatsGridView);
				catsView.setAdapter(adapter);

				catsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
					{
						Dish.Type cat = adapter.getItem(i);
						showMenuCategory(cat);
					}
				});

				break;

			case MENU_LIST:
				final MenuAdapter menuAdapter = new MenuAdapter(this, AppState.getMenuCache());
				menuAdapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener()
				{
					@Override
					public void onItemClick(View view, int i, MenuAdapter ma)
					{
						final Dish d = (Dish) ma.getItem(i);
//						dishPopup.setTitle("Order " + d.name);
//
//						dishPopupName.setText(d.name);
//						dishPopupQuantity.setText(d.getQuantityString());
//						dishPopupAmount.setText("1");
//						dishPopupComment.setText("");
//						dishPopupDescription.setText(d.description);
//
//						dishPopupOrderButton.setOnClickListener(new View.OnClickListener()
//						{
//							@Override
//							public void onClick(View view)
//							{
//								orderDish(d,
//									Integer.parseInt(dishPopupAmount.getText().toString()),
//									dishPopupComment.getText().toString());
//								dishPopup.hide();
//							}
//						});
//
//						dishPopup.show();
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

				this.menuAdapter = menuAdapter;

				break;

			case NOTIFICATIONS:

				ListView notiesList = (ListView) findViewById(R.id.notificationsListView);
				notiesList.setEmptyView(findViewById(R.id.noNotificationsMessage));

				break;

			case ORDERS_LIST:

				ListView ordersList = (ListView) findViewById(R.id.ordersListView);
				ordersList.setEmptyView(findViewById(R.id.noOrdersMessage));

				break;
		}
	}

	private void orderDish(Dish d, int amount, String comment)
	{
		toast.setText("Ordered " + amount + " of " + d.name);
		toast.show();
	}

	private void searchMenu(String query)
	{
		switchLayout(SubLayout.MENU_LIST);
		if (menuAdapter != null)
		{
			menuAdapter.setFilter(query);
		}
	}
	private void showMenuCategory(Dish.Type cat)
	{
		switchLayout(SubLayout.MENU_LIST);
		if (menuAdapter != null)
		{
			if (cat != null)
			{
				menuAdapter.setFilter(cat);
				setTitle(cat.name);
			}
			else
			{
				menuAdapter.clearFilter();
				setTitle(SubLayout.MENU_LIST.titleId);
			}
		}
	}

	@Override
	public void onFragmentInteraction(Uri uri)
	{

	}

	@Override
	public void onCategorySelected(Dish.Type cat)
	{
		Intent i = new Intent(this, MenuCategoryActivity.class);
		Bundle options = new Bundle();
		options.putInt(MenuCategoryActivity.ARG_CAT_ID, cat.ordinal());
		i.putExtras(options);
		startActivity(i);
	}

	private void redirectToLoginActivity()
	{
		Intent i = new Intent(this, LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}

	private enum SubLayout
	{
		CATEGORIES(R.layout.menu_cat_view_layout, R.string.menuViewTitle),
		MENU_LIST(R.layout.menu_list_view_layout, R.string.menuViewTitle),
		ORDERS_LIST(R.layout.orders_view_layout, R.string.ordersViewTitle),
		MAKE_ORDER(0, R.string.makeOrderViewTitle),
		NOTIFICATIONS(R.layout.notifications_view_layout, R.string.notificationsViewTitle);

		public int layoutId, titleId;

		SubLayout(int id, int titleId)
		{
			layoutId = id;
			this.titleId = titleId;
		}
	}
}
