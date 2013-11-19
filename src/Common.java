import java.io.UnsupportedEncodingException;

public class Common {
	
    public static final byte DLE = 0x10;
    public static final byte STX = 0x02;
    public static final byte ETX = 0x03;
    
	public static byte[] makeBCC(byte[] orgTx) {
		int dleInOrgCnt = 0;
		for (int j = 0; j < orgTx.length; j++) {
			if (orgTx[j] == DLE)
				dleInOrgCnt++;
		}

		byte[] fwTx = new byte[orgTx.length + 5 + dleInOrgCnt];

		byte bcc_value = 0;
		int i = 0, k = 0;

		fwTx[k++] = DLE; // 0
		fwTx[k++] = STX; // 1
		for (i = 0; i < orgTx.length; i++) {
			fwTx[k++] = orgTx[i];
			if (orgTx[i] == DLE)
				fwTx[k++] = DLE;
		}
		for (i = 2; i < k; i++) {
			if ((fwTx[i] == DLE) && (fwTx[i + 1] == DLE))
				i++;
			bcc_value ^= fwTx[i];
		}
		fwTx[i++] = DLE;
		fwTx[i++] = ETX;
		bcc_value ^= DLE;
		bcc_value ^= ETX;

		// frm -> BCC_VALUE = (char)bcc_value;
		fwTx[i++] = bcc_value;
		// frm -> FwTxCnt = i;
		return fwTx;
	}

	public static byte[] checkBCC(byte[] in) {
		int dataCnt = 0;				// 복사할 배열의 INDEX
		byte[] result = new byte[1024]; // 복사할 배열
		byte BCC_VALUE = 0; 			// BCC 값 저장
		boolean breakTime = false;
		for (int i = 2; i < in.length; i++) {
			if (in[i] == DLE && in[i + 1] == DLE) {
				result[dataCnt++] = in[i++];
				BCC_VALUE ^= in[i];
			} else if (in[i] == DLE && in[i + 1] == ETX) {
				BCC_VALUE ^= in[i]; // DLE
				BCC_VALUE ^= in[i + 1]; // ETX
				if (BCC_VALUE != in[i + 2]) {
					result = new byte[1024];
					result[0] = -1;
				}
				breakTime = true;
			} else {
				if (breakTime == true)
					break;
				result[dataCnt++] = in[i];
				BCC_VALUE ^= in[i];
			}
		}
		return result;
	}
	
	// int -> 1byte
    public static byte[] intToOneByteArray(int integer) {
          byte[] byteArray = new byte[1];
          byteArray[0] |= (byte) (integer & 0xFF);
          return byteArray;
    }
	
	// int -> 2byte
    public static byte[] intToTwoByteArray(int integer) {
          byte[] byteArray = new byte[2];
          byteArray[0] |= (byte) ((integer & 0xFF00) >> 8);
          byteArray[1] |= (byte) (integer & 0xFF);
          return byteArray;
    }
	
	// int -> 4byte
    public static byte[] intToFourByteArray(int integer) {
          byte[] byteArray = new byte[4];
          byteArray[0] |= (byte) ((integer & 0xFF000000) >> 24);
          byteArray[1] |= (byte) ((integer & 0xFF0000) >> 16);
          byteArray[2] |= (byte) ((integer & 0xFF00) >> 8);
          byteArray[3] |= (byte) (integer & 0xFF);
          return byteArray;
    }
    
    // 1byte -> int
    public static int oneByteArrayToInt(byte[] byteArray, int index) {
          int s1 = byteArray[index] & 0xFF;
          return ((s1 << 0));
    }

    // 2byte -> int
    public static int twoByteArrayToInt(byte[] byteArray, int index) {
          int s1 = byteArray[index] & 0xFF;
          int s2 = byteArray[index + 1] & 0xFF;
          return ((s1 << 8) + (s2 << 0));
    }
    
    // 4byte -> int
    public static int fourByteArrayToInt(byte[] byteArray, int index) {
          int s1 = byteArray[index] & 0xFF;
          int s2 = byteArray[index + 1] & 0xFF;
          int s3 = byteArray[index + 2] & 0xFF;
          int s4 = byteArray[index + 3] & 0xFF;
          return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }
    
    // byte -> toHexString
	public static String toHexString(byte b) {
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toHexString(0x0100 + (b & 0x00FF)).substring(1));
		return sb.toString();
	}

    // String -> byte
    public static byte[] stringToByteArray(String str) {
          byte[] byteArray = null;
          try {
                 byteArray = str.getBytes("UTF-8");
          } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
          }
          return byteArray;
    }

    // byte -> String
    public static String byteArrayToString(byte[] byteArray) {
          String str = null;
          try {
                 str = new String(byteArray, "UTF-8");
          } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
          }
          return str;
    }
}
