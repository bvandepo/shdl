
package org.jcb.tools;

import java.util.*;
import java.text.*;


public class HexNum {


	public static int parseUnsigned(String str) throws ParseException {
		int val = 0;
		for (int i = 0; i < str.length(); i++) {
			int nc = ("0123456789abcdef").indexOf(str.substring(i, i + 1).toLowerCase());
			if (nc == -1) throw new ParseException("bad hex number: " + str, i);
			val = val * 16 + nc;
		}
		return val;
	}


	// 1 -> "00 01", 65535 -> "ff ff"
	public static String formatUnsigned4Hexa(String val, String space) {
		try {
			String ch1 = val.substring(0, 4);
			char c1 = ' ';
			if ((ch1.indexOf('Z') == -1) && (ch1.indexOf('X') == -1)) {
				int v1 = BinNum.parseUnsigned(ch1);
				c1 = ("0123456789abcdef").charAt(v1);
			} else if (ch1.indexOf('X') != -1) {
				c1 = 'X';
			} else {
				c1 = 'Z';
			}
			String ch2 = val.substring(4, 8);
			char c2 = ' ';
			if ((ch2.indexOf('Z') == -1) && (ch2.indexOf('X') == -1)) {
				int v2 = BinNum.parseUnsigned(ch2);
				c2 = ("0123456789abcdef").charAt(v2);
			} else if (ch2.indexOf('X') != -1) {
				c2 = 'X';
			} else {
				c2 = 'Z';
			}
			String ch3 = val.substring(8, 12);
			char c3 = ' ';
			if ((ch3.indexOf('Z') == -1) && (ch3.indexOf('X') == -1)) {
				int v3 = BinNum.parseUnsigned(ch3);
				c3 = ("0123456789abcdef").charAt(v3);
			} else if (ch3.indexOf('X') != -1) {
				c3 = 'X';
			} else {
				c3 = 'Z';
			}
			String ch4 = val.substring(12);
			char c4 = ' ';
			if ((ch4.indexOf('Z') == -1) && (ch4.indexOf('X') == -1)) {
				int v4 = BinNum.parseUnsigned(ch4);
				c4 = ("0123456789abcdef").charAt(v4);
			} else if (ch4.indexOf('X') != -1) {
				c4 = 'X';
			} else {
				c4 = 'Z';
			}
			return (c1 + "" + c2 + space + c3 + "" + c4);
		} catch(Exception ex) {
			return ("??" + space + "??");
		}
	}


	// 1 -> "0000 0001", 65536 -> "0001 ffff"
	public static String formatUnsigned8Hexa(long n, String space1, String space2) {
		int lsb = (int) (n % 65536);
		int msb = (int) (n / 65536);
		return (formatUnsigned4Hexa(msb, space1) + space2 + formatUnsigned4Hexa(lsb, space1));
	}

	// 1 -> "00 01", 65535 -> "ff ff"
	public static String formatUnsigned4Hexa(int n, String space) {
		int c0 = n % 16;
		n = n / 16;
		int c1 = n % 16;
		n = n / 16;
		int c2 = n % 16;
		n = n / 16;
		int c3 = n % 16;
		return ("" + ("0123456789abcdef").charAt(c3) + ("0123456789abcdef").charAt(c2) + space +
			("0123456789abcdef").charAt(c1) + ("0123456789abcdef").charAt(c0));
	}

	// <n> in [0, 255]; 1 -> "01", 255 -> "FF"
	public static String formatUnsigned2Hexa(int n) {
		int c0 = n % 16;
		n = n / 16;
		int c1 = n % 16;
		return ("" + ("0123456789abcdef").charAt(c1) + ("0123456789abcdef").charAt(c0));
	}

	// idem other, but use the minimum of digits
	public static String formatUnsignedHexa(int n) {
		ArrayList nums = new ArrayList();
		do {
			int c = n % 16;
			if (c < 0) c += 16;
			nums.add(new Integer(c));
			n = n / 16;
		} while (n > 0);
		StringBuffer sb = new StringBuffer();
		for (int i = nums.size() - 1; i >= 0; i--) {
			int c = ((Integer) nums.get(i)).intValue();
			sb.append(("0123456789abcdef").charAt(c));
		}
		return new String(sb);
	}
	
	public static String formatUnsignedHexa(long n) {
		ArrayList nums = new ArrayList();
		do {
			long c = n % 16;
			if (c < 0) c += 16;
			nums.add(new Long(c));
			n = n / 16;
		} while (n > 0);
		StringBuffer sb = new StringBuffer();
		for (int i = nums.size() - 1; i >= 0; i--) {
			int c = ((Long) nums.get(i)).intValue();
			sb.append(("0123456789abcdef").charAt(c));
		}
		return new String(sb);
	}
	
	// <n> in [-128, +127]; 1 -> "01", -1 -> "FF"
	public static String formatSigned2Hexa(int n) {
		int c0 = n % 16;
		n = n / 16;
		int c1 = n % 16;
		return ("" + ("0123456789abcdef").charAt(c1) + ("0123456789abcdef").charAt(c0));
	}
	
	
	public static void main(String[] args) {
		try {
			String str = HexNum.formatUnsigned8Hexa(0L, " ", ".");
			System.out.println("str=" + str);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}

