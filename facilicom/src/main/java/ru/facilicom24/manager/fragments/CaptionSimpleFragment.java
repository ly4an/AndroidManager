package ru.facilicom24.manager.fragments;


public class CaptionSimpleFragment
		extends CaptionFragment
		implements CaptionFragment.OnFragmentInteractionListener {

	public CaptionSimpleFragment() {
	}

	@Override
	public void captionFragmentOnBackPressed() {
	}

	@Override
	public void captionFragmentOnSendPressed() {
	}

	@Override
	public void captionFragmentOnSavePressed() {
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
	}

	@Override
	public boolean backIcon() {
		return true;
	}

	@Override
	public boolean sendIcon() {
		return false;
	}

	@Override
	public boolean saveIcon() {
		return false;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return false;
	}

	public interface OnFragmentInteractionListener {
		void captionFragmentOnBackPressed();
	}
}
