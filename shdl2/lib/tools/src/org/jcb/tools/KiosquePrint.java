
package org.jcb.tools;

import java.util.*;
import java.io.*;
import javax.comm.*;


// Impression sur une imprimante Kiosque connect�e sur un port serie


public class KiosquePrint {

	private OutputStream outputStream;
	private SerialPort serialPort;


	// <portName> = "/dev/ttyS0" pour COM1 sous Linux, "COM1" sous Windows

	public KiosquePrint(String portName) throws Exception {
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
//System.out.println("portId = " + portId.getName());
	            
			if (portId.getName().equals(portName)) {
				serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);
				outputStream = serialPort.getOutputStream();
				return;
			}
		}
		throw new Exception("port " + portName + " inexistant");
	}

	public void close() throws Exception {
		serialPort.close();
	}


	// imprime les <lines>

	public void print(String[] lines) throws Exception {
		String stringMessage = "\n\r";
		outputStream.write(stringMessage.getBytes());
		for (int i = 0; i < lines.length; i++) {
			String eligne = encoderCaracteresPourImpr(lines[i] + "\n\r");
			outputStream.write(eligne.getBytes());
		}
		outputStream.write((byte) 12);
		//outputStream.write((byte) 13);
		//outputStream.write((byte) 27);
		//outputStream.write((byte) 105);
	}



	private String encoderCaracteresPourImpr(String str) {
		return Strings.substitute(
			  Strings.substitute(
				     Strings.substitute(
						Strings.substitute(
							   Strings.substitute(
								      Strings.substitute(
										 Strings.substitute(
											    Strings.substitute(
												       Strings.substitute(
														  Strings.substitute(
															     Strings.substitute(
																	Strings.substitute(
																		   Strings.substitute(
																			      str,
																			      "�", new String(new byte[] {(byte) 130} )),
																		   "�", new String(new byte[] {(byte) 138} )),
																	"�", new String(new byte[] {(byte) 133} )),
															     "�", new String(new byte[] {(byte) 136} )),
														  "�", new String(new byte[] {(byte) 147} )),
												       "�", new String(new byte[] {(byte) 131} )),
											    "�", new String(new byte[] {(byte) 140} )),
										 "�", new String(new byte[] {(byte) 139} )),
								      "�", new String(new byte[] {(byte) 132} )),
							   "�", new String(new byte[] {(byte) 148} )),
						"�", new String(new byte[] {(byte) 137} )),
				     "�", new String(new byte[] {(byte) 129} )),
			  "�", new String(new byte[] {(byte) 135} ));
	}


	public static void main(String[] args) {
		String[] lines = { "ligne 1111111111", "ligne 222", "ligne 3333333", "ligne 44444" };

		try {
			//"/dev/ttyS0", "COM1", "COM2"
			KiosquePrint kp = new KiosquePrint(args[0]);
			kp.print(lines);
			kp.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
