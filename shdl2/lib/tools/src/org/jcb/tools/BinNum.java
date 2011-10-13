
package org.jcb.tools;

import java.text.*;


public class BinNum {


	public static int parseUnsigned(String str) throws ParseException {
		int val = 0;
		for (int i = 0; i < str.length(); i++) {
			int nc = ("01").indexOf(str.substring(i, i + 1));
			if (nc == -1) throw new ParseException("bad bin number: " + str, i);
			val = val * 2 + nc;
			//if (val < 0) return Integer.
		}
		return val;
	}

	public static int parseSigned16(String str) throws ParseException {
		if (str.length() != 16) throw new ParseException("length must be 16: " + str, 0);
		boolean isNeg = (str.charAt(0) == '1');
		int val = 0;
		for (int i = 1; i < 16; i++) {
			int nc = ("01").indexOf(str.substring(i, i + 1));
			if (nc == -1) throw new ParseException("bad bin number: " + str, i);
			val = val * 2 + nc;
		}
		if (isNeg) return (val - 32768); else return val;
	}

	public static String formatUnsigned16(int n) {
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < 16; i++) {
			int r = n % 2;
			res.insert(0, r + "");
			n = n / 2;
		}
		return new String(res);
	}

	public static String formatUnsigned32(long n) {
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < 32; i++) {
			long r = n % 2;
			if (r < 0) r += 2;
			res.insert(0, r + "");
			n = n / 2;
		}
		return new String(res);
	}

	public static String formatUnsigned(int n, int nbDigit) {
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < nbDigit; i++) {
			int r = n % 2;
			res.insert(0, r + "");
			n = n / 2;
		}
		return new String(res);
	}

	public static String formatUnsigned(int n) {
		StringBuffer res = new StringBuffer();
		do {
			int r = n % 2;
			res.insert(0, r + "");
			n = n / 2;
		} while (n > 0);
		return new String(res);
	}

	// return the minimum number of binary digit to write <n>
	public static int nbBinDigits(int n) {
		if (n == 0) return 1;
		int nb = 0;
		while (n > 0) {
			n = n / 2;
			nb += 1;
		}
		return nb;
	}

	public static String formatSigned16(int n) {
		StringBuffer res = new StringBuffer();
		if (n < 0) n += 65536;
		for (int i = 0; i < 16; i++) {
			int r = n % 2;
			res.insert(0, r + "");
			n = n / 2;
		}
		return new String(res);
	}

	public static void main(String[] args) {
		try {
			int n = 12;
			String sn = BinNum.formatSigned16(n);
			int nc = BinNum.parseUnsigned(sn);
			int sc = BinNum.parseSigned16(sn);
			System.out.println("n=" + n + ", sn=" + sn + ", nc=" + nc + ", sc=" + sc);
	
			n = -1;
			sn = BinNum.formatSigned16(n);
			nc = BinNum.parseUnsigned(sn);
			sc = BinNum.parseSigned16(sn);
			System.out.println("n=" + n + ", sn=" + sn + ", nc=" + nc + ", sc=" + sc);
	
			n = 32767;
			sn = BinNum.formatSigned16(n);
			nc = BinNum.parseUnsigned(sn);
			sc = BinNum.parseSigned16(sn);
			System.out.println("n=" + n + ", sn=" + sn + ", nc=" + nc + ", sc=" + sc);
	
			n = 32768;
			sn = BinNum.formatSigned16(n);
			nc = BinNum.parseUnsigned(sn);
			sc = BinNum.parseSigned16(sn);
			System.out.println("n=" + n + ", sn=" + sn + ", nc=" + nc + ", sc=" + sc);
	
			n = 32769;
			sn = BinNum.formatSigned16(n);
			nc = BinNum.parseUnsigned(sn);
			sc = BinNum.parseSigned16(sn);
			System.out.println("n=" + n + ", sn=" + sn + ", nc=" + nc + ", sc=" + sc);
	
			System.out.println("nbBinDigits(0)=" + BinNum.nbBinDigits(0));
			System.out.println("nbBinDigits(1)=" + BinNum.nbBinDigits(1));
			System.out.println("nbBinDigits(2)=" + BinNum.nbBinDigits(2));
			System.out.println("nbBinDigits(7)=" + BinNum.nbBinDigits(7));
			System.out.println("nbBinDigits(8)=" + BinNum.nbBinDigits(8));
			System.out.println("nbBinDigits(123)=" + BinNum.nbBinDigits(123));
			System.out.println("nbBinDigits(128)=" + BinNum.nbBinDigits(128));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
	
