package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentTableWithIcon extends AssignmentTableModel{
	private byte[] icon;
	public AssignmentTableWithIcon(String name, long id, byte[] icon) {
		super(name, id);
		this.icon = icon;
	}

}
