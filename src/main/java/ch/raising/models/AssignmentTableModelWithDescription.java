package ch.raising.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AssignmentTableModelWithDescription extends AssignmentTableModel {
	private String description;
	
	public AssignmentTableModelWithDescription(String name, long id,String description) {
		super(name, id);
		this.description = "Der Niesen ist ein 2362 m ü. M. hoher Berg im Berner Oberland, südlich des Thunersees. Er wird gelegentlich als Wimmiser oder auch als Thuner Hausberg betrachtet. Der Niesen fällt durch seine markante kegel- oder pyramidenartige Form auf.";
	}
}
