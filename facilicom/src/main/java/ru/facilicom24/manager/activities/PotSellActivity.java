package ru.facilicom24.manager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import database.PotSell;
import database.PotSellDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.fragments.InteractionsFragment;

import static ru.facilicom24.manager.R.id.listView;

public class PotSellActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String POT_SELL_NUMBER = "PotSellNumber";
	final static public String POT_SELL_NAME = "POT_SELL_NAME";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_pot_sell);

		List<PotSell> potSells = FacilicomApplication.getInstance().getDaoSession().getPotSellDao().queryBuilder()
				.where(PotSellDao.Properties.ClientID.eq(getIntent().getIntExtra(InteractionsFragment.CLIENT_ID, 0)))
				.list();

		if (potSells != null && !potSells.isEmpty()) {
			findViewById(R.id.listViewEmptyTextView).setVisibility(View.GONE);

			ListView listView = (ListView) findViewById(R.id.listView);

			listView.setOnItemClickListener(this);
			listView.setAdapter(new Adapter(potSells));
		} else {
			findViewById(R.id.listViewEmptyTextView).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PotSell potSell = (PotSell) ((ListView) findViewById(listView)).getAdapter().getItem(position);

		setResult(RESULT_OK, new Intent()
				.putExtra(POT_SELL_NUMBER, potSell.getNumber())
				.putExtra(POT_SELL_NAME, potSell.getName())
		);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class Adapter extends BaseAdapter {

		List<PotSell> potSells;
		LayoutInflater layoutInflater;

		Adapter(List<PotSell> potSells) {
			this.potSells = potSells;
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return potSells.size();
		}

		@Override
		public Object getItem(int position) {
			return potSells.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.activity_pot_sell_item, parent, false);
			}

			PotSell potSell = (PotSell) getItem(position);

			((TextView) convertView.findViewById(R.id.numberTextView)).setText(String.valueOf(potSell.getNumber()));
			((TextView) convertView.findViewById(R.id.nameTextView)).setText(potSell.getName());

			return convertView;
		}
	}
}
