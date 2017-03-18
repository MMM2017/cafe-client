package ru.lazycodersinc.smartcafeclient.waiter;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.lazycodersinc.smartcafeclient.CafeApp;
import ru.lazycodersinc.smartcafeclient.R;
import ru.lazycodersinc.smartcafeclient.model.AppState;
import ru.lazycodersinc.smartcafeclient.model.Dish;
import ru.lazycodersinc.smartcafeclient.model.FailableActionListener;
import ru.lazycodersinc.smartcafeclient.model.MenuAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuListFragment extends Fragment
{
	private Dialog dishPopup;
	private TextView dishPopupName, dishPopupQuantity, dishPopupDescription;
	private Button dishPopupOrderButton;
	private EditText dishPopupComment, dishPopupAmount;

	private ProgressBar processingBar;

	private MenuAdapter menuAdapter;

	private Toast toast;


	private static final String ARG_CAT_ID = "menuListCatId";
	private static final String ARG_QUERY = "menuListSearchQuery";

	private int catId = -1;
	private String searchQuery = "";

	private OnFragmentInteractionListener mListener;

	public MenuListFragment()
	{
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param catId Id of dish type for filter (-1 for none).
	 * @param searchQuery Search string for filter.
	 * @return A new instance of fragment MenuListFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MenuListFragment newInstance(int catId, String searchQuery)
	{
		MenuListFragment fragment = new MenuListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_CAT_ID, catId);
		args.putString(ARG_QUERY, searchQuery);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			catId = getArguments().getInt(ARG_CAT_ID);
			searchQuery = getArguments().getString(ARG_QUERY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View menuListLayout = inflater.inflate(R.layout.menu_list_view_layout, container, false);

		final ListView menuList = (ListView) menuListLayout.findViewById(R.id.menuListView);
		processingBar = (ProgressBar) menuListLayout.findViewById(R.id.dishesListProgressBar);

		processingBar.setVisibility(View.VISIBLE);

		// popup initialization
		dishPopup = new Dialog(getContext());
		dishPopup.setContentView(R.layout.dish_description_popup_layout);
		dishPopupName = (TextView) dishPopup.findViewById(R.id.popupDishName);
		dishPopupQuantity = (TextView) dishPopup.findViewById(R.id.popupDishQuantity);
		dishPopupDescription = (TextView) dishPopup.findViewById(R.id.popupDescription);
		dishPopupAmount = (EditText) dishPopup.findViewById(R.id.popupAmount);
		dishPopupComment = (EditText) dishPopup.findViewById(R.id.popupComment);
		dishPopupOrderButton = (Button) dishPopup.findViewById(R.id.popupOrderButton);

		// toast
		toast = Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_SHORT);

		// async populating menu list
		CafeApp.getMenu(new FailableActionListener()
		{
			@Override
			public void onSuccess(Object... params)
			{
				processingBar.setVisibility(View.GONE);

				menuAdapter = new MenuAdapter(getContext(), AppState.getMenuCache());
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

				if (catId != -1)
				{
					Dish.Type cat = Dish.Type.values()[catId];
					menuAdapter.setFilter(cat);
				}
				menuAdapter.setFilter(searchQuery);

				menuList.setAdapter(menuAdapter);
				menuList.setEmptyView(menuListLayout.findViewById(R.id.noDishesMessage));
			}

			@Override
			public void onError(Object... params)
			{
				processingBar.setVisibility(View.GONE);
				menuList.setEmptyView(menuListLayout.findViewById(R.id.noDishesMessage));
				TextView errorView = (TextView) menuListLayout.findViewById(R.id.dishesErrorMessage);
				errorView.setVisibility(View.VISIBLE);
			}
		});

		return menuListLayout;
	}

	public void filterByCategory(int cat)
	{
		catId = cat;
		if (menuAdapter != null)
			menuAdapter.setFilter(Dish.Type.values()[cat]);
	}

	private void orderDish(Dish d, int amount, String comment)
	{
		toast.setText("Ordered " + amount + " of " + d.name);
		toast.show();
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener)
		{
			mListener = (OnFragmentInteractionListener) context;
		}
		else
		{
			throw new RuntimeException(context.toString()
				+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener
	{
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
}
