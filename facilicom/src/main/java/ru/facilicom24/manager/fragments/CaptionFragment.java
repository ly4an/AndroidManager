package ru.facilicom24.manager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;

public class CaptionFragment
		extends Fragment
		implements View.OnClickListener {

	private OnFragmentInteractionListener listener;
	private CaptionSimpleFragment.OnFragmentInteractionListener simpleListener;

	public CaptionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_caption, container, false);

		((ImageView) view.findViewById(R.id.headerImageView)).setImageResource(FacilicomApplication.getLogoResId(getActivity()));

		//

		View backFontButton = view.findViewById(R.id.backFontButton);
		View tickImageView = view.findViewById(R.id.tickImageView);

		backFontButton.setVisibility(View.GONE);
		tickImageView.setVisibility(View.GONE);

		if ((getActivity() instanceof OnFragmentInteractionListener
				&& ((OnFragmentInteractionListener) getActivity()).backIcon())

				|| getActivity() instanceof CaptionSimpleFragment.OnFragmentInteractionListener) {

			backFontButton.setOnClickListener(this);
			backFontButton.setVisibility(View.VISIBLE);
			tickImageView.setVisibility(View.VISIBLE);
		}

		//

		View saveImageButton = view.findViewById(R.id.saveImageButton);

		saveImageButton.setVisibility(View.GONE);

		if (getActivity() instanceof OnFragmentInteractionListener
				&& ((OnFragmentInteractionListener) getActivity()).saveIcon()) {

			saveImageButton.setOnClickListener(this);
			saveImageButton.setVisibility(View.VISIBLE);
		}

		//

		View sendImageButton = view.findViewById(R.id.sendImageButton);

		sendImageButton.setVisibility(View.GONE);

		if (getActivity() instanceof OnFragmentInteractionListener
				&& ((OnFragmentInteractionListener) getActivity()).sendIcon()) {

			sendImageButton.setOnClickListener(this);
			sendImageButton.setVisibility(View.VISIBLE);
		}

		//

		View historyImageButton = view.findViewById(R.id.historyImageButton);

		historyImageButton.setVisibility(View.GONE);

		if (getActivity() instanceof OnFragmentInteractionListener
				&& ((OnFragmentInteractionListener) getActivity()).historyIcon()) {

			historyImageButton.setOnClickListener(this);
			historyImageButton.setVisibility(View.VISIBLE);
		}

		//

		View albumImageButton = view.findViewById(R.id.albumImageButton);

		albumImageButton.setVisibility(View.GONE);

		if (getActivity() instanceof OnFragmentInteractionListener
				&& ((OnFragmentInteractionListener) getActivity()).albumIcon()) {

			albumImageButton.setOnClickListener(this);
			albumImageButton.setVisibility(View.VISIBLE);
		}

		//

		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			listener = (OnFragmentInteractionListener) context;
		} else if (context instanceof CaptionSimpleFragment.OnFragmentInteractionListener) {
			simpleListener = (CaptionSimpleFragment.OnFragmentInteractionListener) context;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		listener = null;
		simpleListener = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.backFontButton: {
				if (listener != null) {
					listener.captionFragmentOnBackPressed();
				} else if (simpleListener != null) {
					simpleListener.captionFragmentOnBackPressed();
				}
			}
			break;

			case R.id.sendImageButton: {
				if (listener != null) {
					listener.captionFragmentOnSendPressed();
				}
			}
			break;

			case R.id.saveImageButton: {
				if (listener != null) {
					listener.captionFragmentOnSavePressed();
				}
			}
			break;

			case R.id.historyImageButton: {
				if (listener != null) {
					listener.captionFragmentOnHistoryPressed();
				}
			}
			break;

			case R.id.albumImageButton: {
				if (listener != null) {
					listener.captionFragmentOnAlbumPressed();
				}
			}
			break;
		}
	}

	public interface OnFragmentInteractionListener {
		void captionFragmentOnBackPressed();

		void captionFragmentOnSendPressed();

		void captionFragmentOnSavePressed();

		void captionFragmentOnHistoryPressed();

		void captionFragmentOnAlbumPressed();

		boolean backIcon();

		boolean sendIcon();

		boolean saveIcon();

		boolean historyIcon();

		boolean albumIcon();
	}
}
