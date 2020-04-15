package ch.raising.models;

import ch.raising.interfaces.IAssignmentTableModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class AssignmentTableModel implements IAssignmentTableModel{
	
	@EqualsAndHashCode.Exclude private final String name;
	private final long id;


	@Override
    public boolean equals(Object o) { 
		if(o == this)
			return true;
		if(!(o instanceof AssignmentTableModel))
			return false;
		AssignmentTableModel m = (AssignmentTableModel)o;
		
		return m.getId() == getId();
	}
}
