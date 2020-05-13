package ch.raising.models.responses;

import java.util.List;

import ch.raising.interfaces.IAssignmentTableModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompletePublicInformation {
	List<IAssignmentTableModel> ticketSizes;
	List<IAssignmentTableModel> continents;
	List<IAssignmentTableModel> countries;
	List<IAssignmentTableModel> industries;
	List<IAssignmentTableModel> investmentPhases;
	List<IAssignmentTableModel> labels;
	List<IAssignmentTableModel> investorTypes;
	List<IAssignmentTableModel> support;
	List<IAssignmentTableModel> corporateBodies;
	List<IAssignmentTableModel> financeTypes;
	List<IAssignmentTableModel> revenues;
	List<IAssignmentTableModel> boardmemberTypes;
}
