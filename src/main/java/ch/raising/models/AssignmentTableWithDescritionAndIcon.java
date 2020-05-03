package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentTableWithDescritionAndIcon extends AssignmentTableModelWithDescription{
	private byte[] icon;
	public AssignmentTableWithDescritionAndIcon(String name, long id, String description, byte[] icon) {
		super(name, id, description);
		this.icon = icon;
	}

}
