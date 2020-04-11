package ch.raising.models;

import ch.raising.interfaces.IAssignmentTableModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentTableModel implements IAssignmentTableModel{
	private String name;
	private long id;
	
	
}
