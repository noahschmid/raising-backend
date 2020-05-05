package ch.raising.services;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.AssignmentTableRepositoryFactory;
import ch.raising.data.IconRepository;
import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.AssignmentTableWithDescritionAndIcon;
import ch.raising.models.Icon;

@Service
public class IconService{
	
	IconRepository iconRepo;
	AssignmentTableRepository invTypeRepo;
	AssignmentTableRepository supportRepo;
	AssignmentTableRepository industryRepo;
	AssignmentTableRepository phaseRepo;
	AssignmentTableRepository labelRepo;
	
	public IconService(IconRepository iconRepo, AssignmentTableRepositoryFactory assignmentFactory) throws SQLException {
		this.iconRepo = iconRepo;
		this.industryRepo = assignmentFactory.getRepository("industry");
		this.invTypeRepo = assignmentFactory.getRepository("investortype");
		this.supportRepo = assignmentFactory.getRepository("support");
		this.phaseRepo = assignmentFactory.getRepository("investmentphase");
		this.labelRepo = assignmentFactory.getRepository("label");
	}
	
	public void addToInvestortype(MultipartFile icon,long id ) throws IOException, DataAccessException, SQLException {
		long iconId = addIconAndReturnId(icon);
		invTypeRepo.addIconToTable(iconId, id);
	}
	public void addToSupport(MultipartFile icon,long id ) throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		supportRepo.addIconToTable(iconId, id);
	}
	public void addToIndustry(MultipartFile icon,long id ) throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		industryRepo.addIconToTable(iconId, id);
	}
	public void addToInvestmentPhase( MultipartFile icon,long id ) throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		phaseRepo.addIconToTable(iconId, id);
	}
	public void addToLabel(MultipartFile icon, long id) throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		labelRepo.addIconToTable(iconId, id);
	}
	public long addIconAndReturnId(MultipartFile icon) throws IOException, DataAccessException, SQLException {
		Icon insert = new Icon();
		insert.setIcon(icon.getBytes());
		insert.setContentType(icon.getContentType());
		return iconRepo.addMedia(insert);
	}
	
	public Icon getIcon(long id) {
		return iconRepo.find(id);
	}

	public void update(int id, MultipartFile build) throws DataAccessException, IOException {
		iconRepo.update(Icon.builder().icon(build.getBytes()).contentType(build.getContentType()).build());
	}

}
