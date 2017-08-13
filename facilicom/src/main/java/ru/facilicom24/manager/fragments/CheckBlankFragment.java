package ru.facilicom24.manager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.CheckBlanksRepository;
import ru.facilicom24.manager.model.CheckBlank;
import ru.facilicom24.manager.model.CheckType;

public class CheckBlankFragment
		extends BaseFragment
		implements AdapterView.OnItemSelectedListener {

	CheckType mCheckType;
	List<CheckBlank> mBlanks;
	CheckBlanksRepository mCheckBlanksRepository;

	Spinner mBlanksView;
	BlanksAdapter mAdapter;

	int mBlankSelected;

	ICheckBlankFragmentListener mListener;

	public CheckBlankFragment() {
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		Fragment targetFragment = getTargetFragment();

		if (targetFragment instanceof ICheckBlankFragmentListener) {
			mListener = (ICheckBlankFragmentListener) targetFragment;
		} else if (context instanceof ICheckBlankFragmentListener) {
			mListener = (ICheckBlankFragmentListener) context;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBlankSelected = -1;

		mBlanks = new ArrayList<>();
		mCheckBlanksRepository = new CheckBlanksRepository(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_blank, container, false);

		mBlanksView = (Spinner) view.findViewById(R.id.blank);

		mAdapter = new BlanksAdapter();
		mBlanksView.setAdapter(mAdapter);

		mBlanksView.setOnItemSelectedListener(this);
		mBlanksView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					if (mCheckType == null) {
						showAlertDialog(R.id.alert_dialog, R.string.error, R.string.choose_type);
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		});

		init(null);

		return view;
	}

	public void init(CheckType checkType) {
		mCheckType = checkType;

		List<CheckBlank> repoBlanks = null;

		if (mCheckType != null) {
			repoBlanks = mCheckBlanksRepository.getAll(mCheckType);
		}

		mBlanks.clear();
		mBlanks.add(new CheckBlank(0, getString(R.string.choose_blank)));

		if (repoBlanks != null && !repoBlanks.isEmpty()) {
			mBlanks.addAll(repoBlanks);
		}

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
		if (i == 0) {
			mBlankSelected = -1;
			notifyListener(null);
		} else {
			mBlankSelected = i;
			notifyListener(mBlanks.get(i));
		}
	}

	void notifyListener(CheckBlank blank) {
		if (mListener != null) {
			mListener.onCheckBlankSelected(blank);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
	}

	public interface ICheckBlankFragmentListener {
		void onCheckBlankSelected(CheckBlank blank);
	}

	class BlanksAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mBlanks.size();
		}

		@Override
		public CheckBlank getItem(int i) {
			return mBlanks.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getLayoutInflater(null).inflate(R.layout.spinner_item, viewGroup, false);
			}

			((TextView) view.findViewById(R.id.text)).setText(getItem(i).getName());

			return view;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater(null).inflate(R.layout.spinner_dropdown_item, parent, false);
			}

			TextView textView = (TextView) convertView.findViewById(R.id.text);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.check);

			if (mBlankSelected == position) {
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setVisibility(View.GONE);
			}

			textView.setText(getItem(position).getName());

			return convertView;
		}
	}
}
