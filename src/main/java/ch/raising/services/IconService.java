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

/**
 * Service used for managing Icons.
 * 
 * @author manus
 *
 */
@Service
public class IconService {

	IconRepository iconRepo;
	AssignmentTableRepository invTypeRepo;
	AssignmentTableRepository supportRepo;
	AssignmentTableRepository industryRepo;
	AssignmentTableRepository phaseRepo;
	AssignmentTableRepository labelRepo;

	public IconService(IconRepository iconRepo, AssignmentTableRepositoryFactory assignmentFactory)
			throws SQLException {
		this.iconRepo = iconRepo;
		this.industryRepo = assignmentFactory.getRepository("industry");
		this.invTypeRepo = assignmentFactory.getRepository("investortype");
		this.supportRepo = assignmentFactory.getRepository("support");
		this.phaseRepo = assignmentFactory.getRepository("investmentphase");
		this.labelRepo = assignmentFactory.getRepository("label");
	}

	/**
	 * adds an icon entry with the id of the investortype
	 * 
	 * @param icon
	 * @param id
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void addToInvestortype(MultipartFile icon, long id) throws IOException, DataAccessException, SQLException {
		long iconId = addIconAndReturnId(icon);
		invTypeRepo.addIconToTable(iconId, id);
	}

	/**
	 * adds an icon entry with the id of the support
	 * 
	 * @param icon
	 * @param id
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void addToSupport(MultipartFile icon, long id) throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		supportRepo.addIconToTable(iconId, id);
	}

	/**
	 * adds an icon entry with the id of the indurstry
	 * 
	 * @param icon
	 * @param id
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void addToIndustry(MultipartFile icon, long id) throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		industryRepo.addIconToTable(iconId, id);
	}

	/**
	 * adds an icon entry with the id of the investmentphase
	 * 
	 * @param icon
	 * @param id
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void addToInvestmentPhase(MultipartFile icon, long id)
			throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		phaseRepo.addIconToTable(iconId, id);
	}

	/**
	 * adds an icon entry with the id of the label
	 * 
	 * @param icon
	 * @param id
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void addToLabel(MultipartFile icon, long id) throws DataAccessException, IOException, SQLException {
		long iconId = addIconAndReturnId(icon);
		labelRepo.addIconToTable(iconId, id);
	}

	/**
	 * adds an icon to the table and returns the id
	 * 
	 * @param icon
	 * @param id   of the icon added
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public long addIconAndReturnId(MultipartFile icon) throws IOException, DataAccessException, SQLException {
		Icon insert = new Icon();
		insert.setIcon(icon.getBytes());
		insert.setContentType(icon.getContentType());
		return iconRepo.addMedia(insert);
	}

	/**
	 * 
	 * @param id
	 * @return an arbitary {@link Icon}
	 */
	public Icon getIcon(long id) {
		return iconRepo.find(id);
	}

	/**
	 * 
	 * @return a list of the current iconids saved in the database
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public List<Long> getAllIds() throws DataAccessException, SQLException {
		return iconRepo.findAllIds();
	}

	/**
	 * 
	 * @return a list of {@link Icon} in the database
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public List<Icon> getAllIcons() throws DataAccessException, SQLException {
		return iconRepo.findAllIcons();
	}

	/**
	 * updates an icon by id
	 * 
	 * @param id    of the icon
	 * @param build
	 * @throws DataAccessException
	 * @throws IOException
	 */
	public void update(int id, MultipartFile build) throws DataAccessException, IOException {
		iconRepo.update(Icon.builder().id(id).icon(build.getBytes()).contentType(build.getContentType()).build());
	}

}
