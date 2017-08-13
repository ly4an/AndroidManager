package ru.facilicom24.manager.retrofit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.facilicom24.manager.model.ElementMark;
import ru.facilicom24.manager.model.ElementMarks;

public class CheckContract {
	private int accountId;
	private int formId;
	private String createdOn;
	private float longitude;
	private float latitude;
	private String comments;
	private List<ElementMarkModel> elementMarks;

	public CheckContract(
			int accountId,
			int formId,
			String createdOn,
			float longitude,
			float latitude,
			String comments,
			Collection<ElementMarks> elementMarksCollection
	) {
		this.accountId = accountId;
		this.formId = formId;
		this.createdOn = createdOn;
		this.longitude = longitude;
		this.latitude = latitude;
		this.comments = comments;

		if (elementMarksCollection != null && !elementMarksCollection.isEmpty()) {
			this.elementMarks = new ArrayList<>();
			for (ElementMarks elementMarks : elementMarksCollection) {
				ElementMark[] elementMarkCollection = elementMarks.getMarks();

				if (elementMarkCollection != null && elementMarkCollection.length > 0) {
					ArrayList<MarkModel> marks = new ArrayList<>();
					for (ElementMark elementMark : elementMarkCollection) {
						marks.add(new MarkModel(
								elementMark.getValue(),
								elementMark.getComment()
						));
					}

					this.elementMarks.add(new CheckContract.ElementMarkModel(
							elementMarks.getElementId(),
							marks
					));
				}
			}
		}
	}

	private class ElementMarkModel {
		int elementId;
		List<MarkModel> marks;

		ElementMarkModel(int elementId, List<MarkModel> marks) {
			this.elementId = elementId;
			this.marks = marks;
		}
	}

	private class MarkModel {
		int value;
		String comment;

		MarkModel(int value, String comment) {
			this.value = value;
			this.comment = comment;
		}
	}
}
