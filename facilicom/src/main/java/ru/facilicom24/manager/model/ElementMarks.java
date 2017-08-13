package ru.facilicom24.manager.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementMarks {

	@JsonProperty("ElementId")
	private int elementId;

	@JsonProperty("Marks")
	private ElementMark[] marks;

	public ElementMarks() {
		super();
	}

	public ElementMarks(int eId, ElementMark[] marks) {
		this.elementId = eId;
		this.marks = marks;
	}

	public int getElementId() {
		return elementId;
	}

	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	public ElementMark[] getMarks() {
		return marks;
	}

	public void setMarks(ElementMark[] marks) {
		this.marks = marks;
	}
}
