package ch.raising.utils;
/**
 * not currently used
 * @author manus
 *
 */
public class UidUtil {
	/**
     * Check whether given uid is a valid one
     *
     * @param uid
     * @return
     */
    public static boolean isValidUId(String uid) {
        if (!uid.matches("[A-Z]{3}-\\d\\d\\d\\.\\d\\d\\d\\.\\d\\d\\d")) {
            return false;
        }

        uid = uid.substring(4);
        uid = uid.replace(".", "");
        int number = 0;
        int[] multipliers = {5, 4, 3, 2, 7, 6, 5, 4};
        int checksum = Integer.parseInt(String.valueOf(uid.charAt(uid.length() - 1)));
        for (int i = 0; i < uid.length() - 1; ++i) {
            number += Integer.parseInt(String.valueOf(uid.charAt(i))) * multipliers[i];
        }

        int remainder = 0;
        if (number % 11 != 0)
            remainder = 11 - (number % 11);

        if (checksum == remainder) {
            return true;
        }

        return false;
    }

	/*
	private static boolean checkNumber(int[] uIdNumber) {
		int modulo = 0;
		int crossSum = 0;
		int[] checkNum = new int[] {5,4,3,2,7,6,5,4};
		for(int i = 0; i < checkNum.length; i++) {
			uIdNumber[i] *= checkNum[i];
		}
		for(int i = 0; i < uIdNumber.length-1; i++) {
			crossSum += uIdNumber[i];
		}
		modulo = crossSum % 11;
		if(11-modulo != uIdNumber[8])
			return false;
		return true;
	}

	private static int[] compileUIdNumber(char[] uId) {
		int[] uIdNumber = new int[9];
		int j = 0;
		for (int i = 0; i < uId.length && j < uIdNumber.length; i++) {
			if(uId[i] >= '0' && uId[i] <= '9') {
				uIdNumber[j] = uId[i] - '0';
				j++;
			}
		}
		return uIdNumber;
	}*/

}
