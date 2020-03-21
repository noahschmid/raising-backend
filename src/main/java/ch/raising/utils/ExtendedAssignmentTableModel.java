package ch.raising.utils;

import ch.raising.models.AssignmentTableModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExtendedAssignmentTableModel extends AssignmentTableModel{
	
	String description;
	public ExtendedAssignmentTableModel(String name, long id, String description) {
		super(name, id);
		this.description = description;
	}
	
}
