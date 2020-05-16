package ch.raising.utils;
/**
 * not currently used
 * @author manus
 *
 */
public class UidUtil {
	
	public static boolean isValidUId(String unternehmensId) {
		char[] uId = unternehmensId.toCharArray();
		int[] uIdNumber = compileUIdNumber(uId);
		if(uIdNumber.length < 9)
			return false;
		
		return checkNumber(uIdNumber);
	}

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
	}

}
