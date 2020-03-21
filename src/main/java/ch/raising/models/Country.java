package ch.raising.models;

import ch.raising.interfaces.IAssignmentTableModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Country implements IAssignmentTableModel {

	private String name;
	private long id;
	private long continentId;
}