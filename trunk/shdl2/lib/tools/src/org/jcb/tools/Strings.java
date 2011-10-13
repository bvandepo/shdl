
package org.jcb.tools;
 
import org.jcb.tools.*;

import java.io.*;
import java.util.*;
import java.text.*;


public class Strings {

	// Sends back the index of a name "<root>.<index>", or -1 if there's none

	public static int indexName(String name) {
		try {
			int i = name.lastIndexOf('.');
			if (i != -1) {
				String ns = name.substring(i + 1);
				Integer N = Integer.valueOf(ns);
				int n = N.intValue();
				return n;
			}
		} catch (Exception e) {
			System.out.println("Exception(indexName): " + e);
		}
		return -1;
	}


	// Sends back the maximum length of the list of strings <strings>

	public static int stringListMaxLength(Vector strings) {
		int max = -1;
		for (Enumeration e = strings.elements(); e.hasMoreElements(); ) {
			String s = (String) e.nextElement();
			max = Math.max(max, s.length());
		}
		return max;
	}


	// Strip <text> of all its blank characters

	public static String stripBlanks(String text) {
		String cleanedText = "";
		StringTokenizer stb = new StringTokenizer(text, " \r\t\n");
		while(stb.hasMoreTokens()) {
			String word = (String) stb.nextToken();
			cleanedText = cleanedText + word;
		}
		return cleanedText;
	}


	// Encoding/decoding of text areas

	// Converts a multi lined string <str> into a string composed of its ascii codes
	// ex: str = "ABC\nEF" -> "65:66:67:10:69:70"
	// Exceptional case: str = "" -> "0"

	public static String codeString(String str) {
		if (str.length() == 0) return "0";
		byte[] bytes = str.getBytes();
		String res = "";
		for (int i = 0; i < bytes.length; i++) {
			Byte b = new Byte(bytes[i]);
			if (res.length() > 0) res = res + ":";
			res = res + b.toString();
		}
		return res;
	}

	public static String decodeString(String coded) {
		if (coded.equals("0")) return ("");
		Vector vbytes = new Vector();
		StringTokenizer st = new StringTokenizer(coded, ":");
		while (st.hasMoreTokens()) {
			String strb = (String) st.nextToken();
			vbytes.addElement(new Byte(Byte.parseByte(strb)));
		}
		byte[] bytes = new byte[vbytes.size()];
		for (int i = 0; i < vbytes.size(); i++) {
			Byte b = (Byte) vbytes.elementAt(i);
			bytes[i] = b.byteValue();
		}
		return (new String(bytes));
	}


	// Tableau de 150 blancs d'ou sont extraits les blancs des champs fixes des messages Nutrix
	static final String b150 = "                                                                                                                                                          ";

	// ex: formats("ok", 5) -> "ok   "
	public static String formats(String cmd, int lengthBuff) {
		StringBuffer sb = new StringBuffer(b150.substring(0, lengthBuff - cmd.length()));
		sb.insert(0, cmd);
		return (new String(sb));
	}


	// ex: formatd("34", 5) -> "   34"
	public static String formatd(String cmd, int lengthBuff) {
		StringBuffer sb = new StringBuffer(b150.substring(0, lengthBuff - cmd.length()));
		sb.append(cmd);
		return (new String(sb));
	}

	static final String b20_0 = "00000000000000000000";

	// ex: formatd0("34", 5) -> "00034"
	public static String formatd0(String cmd, int lengthBuff) {
		lengthBuff = Math.min(b20_0.length(), lengthBuff);
		StringBuffer sb = new StringBuffer(b20_0.substring(0, Math.max(0, lengthBuff - cmd.length())));
		sb.append(cmd);
		return (new String(sb));
	}

	// ex: formatf(34.5, 3, 2) -> " 3450"
	public static String formatf(double d, int i, int j) {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMinimumIntegerDigits(i);
		decimalFormat.setMinimumFractionDigits(j);
		decimalFormat.setMaximumIntegerDigits(i);
		decimalFormat.setMaximumFractionDigits(j);
		FieldPosition pos = new FieldPosition(0);
		StringBuffer res = new StringBuffer();
		decimalFormat.format(d, res, pos);
		res.delete(i, i + 1);
		return (new String(res));
	}
	
	// ("008150", 4, 2) -> 81.5
	public static double parsef(String str, int i, int j) {
		int n = Integer.parseInt(str.substring(0, i).trim());
		int fract = Integer.parseInt(str.substring(i).trim());
		return (n + fract / Math.pow(10., (double) j));
	}


	// ex: str = "abcXXXdefXXXghij", old = "XXX" new = "YY" -> "abcYYdefYYghij"
	public static String substitute(String str, String old, String neww) {
		StringBuffer newstr = new StringBuffer();
		int prevind = 0;
		int len = old.length();
		int nextind = str.indexOf(old, prevind);
		while (nextind != -1) {
			newstr.append(str.substring(prevind, nextind));
			newstr.append(neww);
			prevind = nextind + len;
			nextind = str.indexOf(old, prevind);
		}
		newstr.append(str.substring(prevind));
		return (new String(newstr));
	}
	
	
	// "spe'ciaux" -> "spéciaux"
	
	public static String decoderCaracteresSpeciaux(String str) {
		return substitute(
				substitute(
					substitute(
						substitute(
							substitute(
								substitute(
									str,
									"e'", "é"),
								"e`", "è"),
							"a`", "à"),
						"e^", "ê"),
					"o^", "ô"),
				"i^", "î");
	}


	public static String decoderCaracteresSpeciauxBis(String str) {
		return substitute(
				substitute(
					substitute(
						substitute(
							substitute(
								substitute(
									substitute(
										substitute(
											substitute(
												substitute(
													substitute(
														substitute(
															substitute(
																str,
																"&eacute;", "é"),
															"&egrave;", "è"),
														"&agrave;", "à"),
													"&ecirc;", "ê"),
												"&ocirc;", "ô"),
											"&acirc;", "â"),
										"&icirc;", "î"),
									"&iuml;", "ï"),
								"&auml;", "ä"),
							"&ouml;", "ö"),
						"&euml;", "ë"),
					"&uuml;", "ü"),
				"&ccedil;", "ç");
	}

	public static String encoderCaracteresSpeciauxBis(String str) {
		return substitute(
				substitute(
					substitute(
						substitute(
							substitute(
								substitute(
									substitute(
										substitute(
											substitute(
												substitute(
													substitute(
														substitute(
															substitute(
																str,
																"é", "&eacute;"),
															"è", "&egrave;"),
														"à", "&agrave;"),
													"ê", "&ecirc;"),
												"ô", "&ocirc;"),
											"â", "&acirc;"),
										"î", "&icirc;"),
									"ï", "&iuml;"),
								"ä", "&auml;"),
							"ö", "&ouml;"),
						"ë", "&euml;"),
					"ü", "&uuml;"),
				"ç", "&ccedil;");
	}


	public static StringBuffer fileText(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		StringBuffer text = new StringBuffer();
		String line = null;
		while (true) {
			line = br.readLine();
			if (line == null) break;
			text.append(line);
			text.append("\n");
		}
		return text;
	}


	public static StringBuffer filteredFileText(String fileName, String before, String after) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		StringBuffer text = new StringBuffer();
		String line = null;
		while (true) {
			line = br.readLine();
			if (line == null) break;
			text.append(Strings.substitute(line, before, after));
			text.append("\n");
		}
		return text;
	}


	// ex: "12:30" -> {12, 30}; "12:70" -> null
	public static int[] extractHHMM(String hhmm) {
		int i = hhmm.indexOf(':');
		if (i == -1) return null;
		String hh = hhmm.substring(0, i);
		String mm = hhmm.substring(i + 1);
		try {
			int h = Integer.parseInt(hh);
			if ((h < 0) || (h > 23)) return null;
			int m = Integer.parseInt(mm);
			if ((m < 0) || (m > 59)) return null;
			int [] res = { h, m };
			return res;
		} catch(Exception e) {
			return null;
		}
	}


	public static void main(String args[]) {
		System.out.println("formatd=" + formatd("~12,92-19,63", 8));
	}

}

