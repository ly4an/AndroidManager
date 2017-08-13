package ru.facilicom24.manager.network;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.activities.MapActivity;
import ru.facilicom24.manager.model.Check;
import ru.facilicom24.manager.model.CheckRequest;
import ru.facilicom24.manager.model.Element;
import ru.facilicom24.manager.model.ElementMark;
import ru.facilicom24.manager.model.ElementMarks;
import ru.facilicom24.manager.model.Zone;

public class ApiRequestHelper {

	static public CheckRequest buildCheckRequest(Activity activity, Check check) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(activity);

		float longitude = preference.getFloat(MapActivity.OBJECT_FIXED_Y, 0);
		float latitude = preference.getFloat(MapActivity.OBJECT_FIXED_X, 0);

		CheckRequest checkRequest = new CheckRequest();

		checkRequest.setAccountId(check.getCheckObject().getCheckObjectId());
		checkRequest.setFormId(check.getCheckBlank().getCheckBlankId());
		checkRequest.setCreatedOn(FacilicomApplication.dateTimeFormat1.format(check.getDate()));

		checkRequest.setLongitude(longitude);
		checkRequest.setLatitude(latitude);

		checkRequest.setComments(check.getComment());

		Map<Integer, List<ElementMark>> sortedMarks = new HashMap<>();

		for (ElementMark mark : check.getMarks()) {
			Element element = mark.getElement();

			if (element != null) {
				List<ElementMark> marks = sortedMarks.get(element.getElementId());

				if (marks == null) {
					marks = new LinkedList<>();
					sortedMarks.put(mark.getElement().getElementId(), marks);
				}

				marks.add(mark);
			}
		}

		List<ElementMarks> resultMarks = new LinkedList<>();

		for (Zone zone : check.getCheckBlank().getZones()) {
			for (Element element : zone.getElements()) {

				ElementMarks elementMarks = new ElementMarks();
				elementMarks.setElementId(element.getElementId());

				List<ElementMark> marks = sortedMarks.get(element.getElementId());

				if (marks != null) {
					ElementMark[] marksArray = new ElementMark[marks.size()];
					marksArray = marks.toArray(marksArray);
					elementMarks.setMarks(marksArray);
				} else {
					elementMarks.setMarks(new ElementMark[0]);
				}

				resultMarks.add(elementMarks);
			}
		}

		checkRequest.setMarks(resultMarks);

		return checkRequest;
	}
}
