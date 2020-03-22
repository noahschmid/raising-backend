package ch.raising.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Country extends AssignmentTableModel {
	private long continentId;
	
	public Country(String name, long id, long continentId) {
		super(name, id);
		this.continentId = continentId;
	}
}