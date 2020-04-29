package ch.raising.utils;

public class UidUtil {
	
	public static boolean isValidUId(String unternehmensId) {
		if(unternehmensId.length() < 13)
			return false;
		char[] uId = unternehmensId.toCharArray();
		int[] checkNum = new int[] {5,4,3,2,7,6,5,4};
		int[] uIdNumber = compileUIdNumber(uId);
		if(!beginsWithThreeUpper(uId))
			return false;
		if(uId[3] != '-')
			return false;
		if(uIdNumber.length != 9)
			return false;
		if(!checkNumber(checkNum, uIdNumber))
			return false;
		
		return true;
	}

	private static boolean checkNumber(int[] checkNum, int[] uIdNumber) {
		int modulo = 0;
		int crossSum = 0;
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

	private static boolean beginsWithThreeUpper(char[] uId) {
		int lower = 0;
		int upper = 'Z' - 'A';
		for (int i = 0; i < 3; i++) {
			if(uId[0] - 'A' < lower || uId[0] - 'A' > upper)
				return false;
		}
		return true;
	}

}
