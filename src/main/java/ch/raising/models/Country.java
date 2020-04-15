package ch.raising.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper=false)
public class Country extends AssignmentTableModel {
	private final long continentId;
	
	public Country(String name, long id, long continentId) {
		super(name, id);
		this.continentId = continentId;
	}
}