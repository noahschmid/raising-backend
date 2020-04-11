package testutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.jdbc.core.JdbcTemplate;

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Media;

public class TestDataUtil {
	
	public static List<Long> getAssignmentTableModelList(int minId, int maxId, int length) {
		assert minId < maxId;
		Random rnd = new Random();
		List<Long> list = new ArrayList<Long>();
		Long newModel;
		for (int i = 0; i < length; i++) {
			long randomId = rnd.nextInt(maxId - minId) + minId;;
			list.add(randomId);
		}
		return list;
	}

	public static List<Media> getMedia() {
		List<Media> gallery = new ArrayList<Media>();
		Random rnd = new Random();
		for (int i = 0; i < 5; i++) {
			gallery.add(new Media(getRandBytes(rnd)));
		}
		return gallery;
	}

	
	public static byte[] getRandBytes(Random rnd) {
		byte[] bytes = new byte[1000];
		rnd.nextBytes(bytes);
		return bytes;
	}
}
