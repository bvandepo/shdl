
package org.jcb.shdl;

import java.io.*;
import java.util.*;

import org.jcb.shdl.shdlc.java.*;

public class SHDLDigilentNexysBoard extends SHDLBoard {

	public SHDLDigilentNexysBoard() {
		super() ;
	}

	public String getBoardName() {
		return "Nexys";
	}

	public String getBoardModuleName() {
		return "Nexys";
	}

	public String[] getBoardPrefixes() {
		return new String[] { "mclk", "btn", "sw", "ld", "an", "ssg",
//			"pdb", "astb", "dstb", "pwr", "pwait",
			/*
			"mem-addr", "mem-data", "mem_oe", "mem-we",
			"mt-adv", "mt-clk", "mt-ub", "mt-lb", "mt-cf", "mt-cre", "mt-wait",
			*/
			"ja1_out", "ja2_out", "ja3_out", "ja4_out",
		};
	}
	public String[] getBoardIOStatus() {
		return new String[] { "in", "in", "in", "out", "out", "out",
//			"inout", "in", "in", "in", "out",
			/*
			"out", "inout", "out", "out",
			"out", "out", "out", "out", "out", "out", "out",
			*/
			"out", "out", "out", "out",
		};
	}
	public int[] getBoardN1() {
		return new int[] { 0, 3, 7, 7, 3, 7,
//			7, 0, 0, 0, 0,
			/*
			23, 15, 0, 0,
			0, 0, 0, 0, 0, 0, 0,
			*/
			0, 0, 0, 0,
		};
	}
	public int[] getBoardN2() {
		return new int[] { 0, 0, 0, 0, 0, 0,
//			0, 0, 0, 0, 0,
			/*
			0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0,
			*/
			0, 0, 0, 0,
		};
	}
	// default values; only for 'out' signals
	public String[] getBoardDefaultValues() {
		return new String[] { "in", "in", "in", "00000000", "1111", "11111111",
//			"inout", "in", "in", "in", "0", 
			/*
			"0000000000000000", "inout", "1", "1",
			"1", "1", "1", "1", "1", "1", "1",
			*/
			"0", "0", "0", "0",
		 };
	}
}

