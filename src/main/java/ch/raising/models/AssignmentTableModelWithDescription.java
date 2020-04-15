package ch.raising.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@EqualsAndHashCode(callSuper=false)
public class AssignmentTableModelWithDescription extends AssignmentTableModel{
	private final String description;
	
	public AssignmentTableModelWithDescription(String name, long id,String description) {
		super(name, id);
		this.description = description;
	}
}
