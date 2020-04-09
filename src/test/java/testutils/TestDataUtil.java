package testutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.jdbc.core.JdbcTemplate;

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Media;

public class TestDataUtil {
	
	public static List<AssignmentTableModel> getAssignmentTableModelList(int minId, int maxId, int length) {
		assert minId < maxId;
		Random rnd = new Random();
		List<AssignmentTableModel> list = new ArrayList<AssignmentTableModel>();
		AssignmentTableModel newModel;
		for (int i = 0; i < length; i++) {
			long randomId = rnd.nextInt(maxId - minId) + minId;
			newModel = new AssignmentTableModel();
			newModel.setId(randomId);
			list.add(newModel);
		}
		return list;
	}

	public static List<Media> getMedia() {
		List<Media> gallery = new ArrayList<Media>();
		Random rnd = new Random();
		for (int i = 0; i < 5; i++) {
			gallery.add(new Media(getRandString(rnd)));
		}
		return gallery;
	}

	
	public static String getRandString(Random rnd) {
		byte[] bytes = new byte[1000];
		rnd.nextBytes(bytes);
		return new String(bytes);
	}
}
