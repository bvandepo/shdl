package org.jcb.shdl;

import org.jcb.filedrop.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import org.jcb.shdl.shdlc.java.*;


public class SHDL2VHDLConverter extends JFrame {

	private SHDL2VHDLConverter frame;
	private JTextField mainModField = new JTextField("", 15);
	private JTextField shdlPath = new JTextField("", 30);
	private JCheckBox synthesizeCheck = new JCheckBox("synthesize");
	private JCheckBox verboseCheck = new JCheckBox("verbose");
	private String boardName = "Nexys-1000";
	private JTextArea messagesArea = new JTextArea();
	private JScrollPane scrollPane;
	private ByteArrayOutputStream baos;
	private PrintStream errorStream;
	private LibManager libManager;
	
	private final Font LARGEFONT = new Font("Dialog", Font.BOLD, 18);
	private final String newline = System.getProperty("line.separator");
	private final String pathSeparator = System.getProperty("path.separator");


	public static void main(String[] args ) {
		
		JFrame frame = new SHDL2VHDLConverter("SHDL2VHDLConverter v2.0.1");

		frame.setBounds(100, 300, 700, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		frame.show();
	}
	
	
	public SHDL2VHDLConverter(String title) {
		super(title);
		frame = this;
		baos = new ByteArrayOutputStream();
		errorStream = new PrintStream(baos);
		getContentPane().add(new SHDL2VHDLConverterPanel());
	}
	
	class SHDL2VHDLConverterPanel extends JPanel {
		public SHDL2VHDLConverterPanel() {
			setLayout(new BorderLayout());
			Container northPanel = Box.createVerticalBox();
			
			JLabel mainModLabel = new JLabel("drop .shd or .net file here :");
			mainModLabel.setFont(LARGEFONT);
			mainModField.setFont(LARGEFONT);
			mainModField.setBackground(Color.lightGray);
			MainModActionListener mainModActionListener = new MainModActionListener();
			mainModField.addActionListener(mainModActionListener);
			JButton okButton = new JButton("run");
			okButton.addActionListener(mainModActionListener);
			JButton resetButton = new JButton("reset");
			resetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					mainModField.setText("");
					shdlPath.setText("");
//					destDir.setText("");
					synthesizeCheck.setSelected(false);
					verboseCheck.setSelected(false);
				}
			});
			JPanel mainModPanel = new JPanel(new GridLayout(2, 1));
			JPanel p1 = new JPanel(); p1.add(mainModLabel);
			mainModPanel.add(p1);
			JPanel p2 = new JPanel(); p2.add(mainModField); p2.add(okButton); p2.add(resetButton);
			mainModPanel.add(p2);
			northPanel.add(mainModPanel);
			
			JPanel checksPanel = new JPanel();
			checksPanel.add(synthesizeCheck);
			checksPanel.add(verboseCheck);
			JComboBox cardComboBox = new JComboBox(new String[] {"Nexys-1000", "Nexys II-1200"});
			cardComboBox.addActionListener(new CardListener());
			cardComboBox.setSelectedIndex(1);
			checksPanel.add(cardComboBox);
			northPanel.add(checksPanel);
			
			JPanel pathPanel = new JPanel();
			pathPanel.add(new JLabel("SHDL path :"));
			pathPanel.add(shdlPath);
			northPanel.add(pathPanel);
			
			// create libmanager
			libManager = new LibManager();
							
			new FileDrop(null, mainModField, new MainModDropListener());
			new FileDrop(null, shdlPath, new PathDropListener());
			
			add("North", northPanel);
			messagesArea.setEditable(false);
			scrollPane = new JScrollPane(messagesArea);
			add("Center", scrollPane);
		}
	}
	
	class MainModActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (shdlPath.getText().length() > 0) libManager.setPath(shdlPath.getText());
			ArrayList files = new ArrayList();
			try {
				File file = libManager.lookFor(mainModField.getText());
				if (!file.isFile())
					addMessage("** '" + file.getCanonicalPath() + "' is not a file" + newline);
				else {
					files.add(file);
				}
				
				processFile(file) ;

			} catch(Exception e) {
				// program exception are reported in console
				addMessage("** program exception: '" + e.getMessage() + "'" + newline);
			}
		}
	}
	
	class MainModDropListener implements FileDrop.Listener {
		public void filesDropped(File[] files_) {
			if (shdlPath.getText().length() > 0) libManager.setPath(shdlPath.getText());
			if (files_.length > 1)
				addMessage("** drop only one file here" + newline);
			else {
				try {
					if (!files_[0].isFile()) {
						addMessage("** '" + files_[0].getCanonicalPath() + "' is not a file" + newline);
						return;
					}
					File file = files_[0];
					mainModField.setText(file.getName());
					
					processFile(file) ;
					
				} catch(Exception e) {
					// program exception are reported in console
					addMessage("** program exception: '" + e.getMessage() + "'" + newline);
				}
			}
		}
	}
	
	class CardListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JComboBox cb = (JComboBox) ev.getSource();
			boardName = (String) cb.getSelectedItem();
		}
	}
		
		
	void addMessage(String message) {
		messagesArea.append(message);
		// il faut attendre un peu (?) avant de scroller
		try {
			Thread.sleep(10);
			messagesArea.repaint();
			JScrollBar sb = scrollPane.getVerticalScrollBar();
			sb.setValue(sb.getMaximum());
		} catch (Exception e) {
		}
	}

	
	// If <shdlPath> and <destDir> are empty, set them to the directory of <file> and its ../vhdl
	// check also that it has rights to write there
	
	boolean checkDirectories(File file) throws Exception {
		// get parent dir
		File shdlDir = file.getParentFile();
		
		// check for write access in parent dir by trying to create a temporary file
		File tempFile = new File(shdlDir, "file.tmp");
		if (tempFile.exists()) tempFile.delete();
		try {
			tempFile.createNewFile();
			tempFile.delete();
		} catch(Exception ex) {
			return false;
		}
		
		// if shdl path empty, set it
		if (shdlPath.getText().trim().length() == 0) {
			shdlPath.setText(shdlDir.getPath());
			libManager.setPath(shdlPath.getText());
		}
		// create vhdl dir if it does not exist
		File vhdlDir = new File(shdlDir.getParentFile(), "vhdl");
		if (!vhdlDir.exists()) vhdlDir.mkdir();
		
		if (synthesizeCheck.isSelected() && ((shdlPath.getText().indexOf(" ") != -1) || (shdlPath.getText().indexOf(" ") != -1))) {
			JOptionPane.showMessageDialog(frame, "Your paths contain blanks (' ') which may cause errors when synthesizing with WebPACK 8");
		}
		return true;
	}

	
	void processFile(File file) {
		try {
			// traduction d'une description de graphe d'�tats
			if (file.getAbsolutePath().endsWith(".net")) {
				NetConverter netConverter = new NetConverter(file, errorStream);
				file = netConverter.start();
				// mise � jour des messages
				addMessage(baos.toString()); baos.reset();
				if (file == null) {
					addMessage("** graph translation failed" + newline);
					addMessage("---------------------" + newline);
					return;
				}
			}
			
			boolean ok = checkDirectories(file);
			if (ok) {
				SHDLBoard board = SHDLBoard.getBoard(boardName) ;
				
				// perform VHDL translation
				File shdlDir = file.getParentFile();
				File vhdlDir = new File(shdlDir.getParentFile(), "vhdl");
				ShdlDesign design = processVHDLTranslation(board, file, vhdlDir);
				
				if (design == null) return;
				
				SHDLModule topModule = design.getTopModule();
				if (topModule == null) {
					addMessage("** could not find top module" + newline);
					return;
				}

				// on classe le design: 0=board only, 1=hybrid, 2=distant only
				int boardIOStatus = board.getModuleIOStatus(topModule) ;
				switch (boardIOStatus) {
				case 0: addMessage("*** BOARD ONLY I/O DESIGN***\n"); break;
				case 1: addMessage("*** BOARD & DISTANT I/O DESIGN***\n"); break;
				case 2: addMessage("*** DISTANT ONLY I/O DESIGN***\n"); break;
				}
				if (boardIOStatus > 0) {
					// hybrid or distant I/O: needs commUSB communication
					
					// create commUSB.vhd
					addMessage("-- creating 'commUSB.vhd'" + newline);
					SHDLPredefinedCommUSB commUSB = new SHDLPredefinedCommUSB(new SHDLModuleOccurence("commUSB", 0, null), null);
					PrintWriter pw = new PrintWriter(new FileOutputStream(new File(vhdlDir, "commUSB.vhd")));
					pw.println(commUSB.getVHDLDefinition());
					pw.flush();
					pw.close();
					
					// create <top>_comm.vhd file
					addMessage("-- creating '" + topModule.getName() + "_comm.vhd'" + newline);
					boolean overflow = design.generateDistantIOModule(topModule, board, vhdlDir);
					
					if (overflow) {
						addMessage("** WARNING: too many inputs/outputs" + newline);
						// create <top>_comm.shd file
						addMessage("-- creating '" + topModule.getName() + "_comm.shd'" + newline);
						design.generateCommShdModule(topModule, vhdlDir);
					}
					
					// create comm.ini
					addMessage("-- creating 'comm.ini'" + newline);
					design.generateCommIni(topModule, board, vhdlDir);
				}

                                JOptionPane.showConfirmDialog(this, "Synthétizer ?");
				
				if (synthesizeCheck.isSelected()) {
					File projectDir = new File(vhdlDir.getParentFile(), "ISEproject");
					ArrayList moduleNames = design.getListModuleNames();
					String topModuleName = design.getTopModule().getName();
					if (boardIOStatus > 0) {
						moduleNames.add("commUSB");
						topModuleName = topModuleName + "_comm";
						moduleNames.add(topModuleName);
					}
					// on l'ex�cute dans un thread, sinon aucun message ne s'affiche avant le retour de la m�thode
					Thread thread = new Thread(new SynthesizeThread(projectDir, vhdlDir, design, topModule, moduleNames, board));
					thread.start();
				}
			} else {
				addMessage("** you have no permission to write in these directories" + newline);
				return;
			}
		} catch (Exception e) {
			// program exception are reported in console
			addMessage("** program exception: '" + e.getMessage() + "'" + newline);
		} finally {
			if (!synthesizeCheck.isSelected()) addMessage("---------------------" + newline);
		}
	}
	
	ShdlDesign processVHDLTranslation(SHDLBoard board, File file, File destDir) throws Exception {
		// Build a design with the modules defined in these source files
		ShdlDesign design = new ShdlDesign(libManager, false, errorStream);
		
		// parse them, and collect all modules referenced from main statements
		ArrayList files = new ArrayList();
		files.add(file);
		boolean collectOk = design.collect(files);
		//addMessage("collectOk=" + collectOk);
		addMessage(baos.toString()); baos.reset();
		if (!collectOk) return null;
		
		// check for loops
		boolean loopsOk = design.checkModuleDependences();
		addMessage(baos.toString()); baos.reset();
		//addMessage("loopsOk=" + loopsOk);
		if (!loopsOk) return null;
		
		// check all design
		boolean checkOK = design.check();
		addMessage(baos.toString()); baos.reset();
		//addMessage("checkOK=" + checkOK);
		if (!checkOK) return null;
		
		addMessage("-- parse completed; 0 error" + newline);
		
		// generate VHDL text
		design.generateVHDL(board, destDir);
		addMessage("-- VHDL generation completed" + newline);
		
		return design;
	}


	class PathDropListener implements FileDrop.Listener {
		public void filesDropped(File[] files) {
			for (int i = 0; i < files.length; i++) {
				try {
					File file = files[i];
					if (file.isDirectory()) {
						if (shdlPath.getText().length() == 0)
							shdlPath.setText(file.getCanonicalPath());
						else
							shdlPath.setText(shdlPath.getText() + pathSeparator + file.getCanonicalPath());
					} else
						addMessage("** '" + file.getCanonicalPath() + "' is not a directory" + newline);
				} catch(Exception e) {}
			}
		}
	}	
	
	// on ex�cute synthesize dans un thread car sinon les messages ne s'affichent pas au fur et � mesure
	class SynthesizeThread implements Runnable {
		File projectDir;
		File vhdlDir;
		ShdlDesign design;
		SHDLModule topModule;
		ArrayList moduleNames;
		SHDLBoard board;
		public SynthesizeThread(File projectDir, File vhdlDir, ShdlDesign design, SHDLModule topModule, ArrayList moduleNames, SHDLBoard board) {
			this.projectDir = projectDir ;
			this.vhdlDir = vhdlDir;
			this.design = design;
			this.topModule = topModule;
			this.moduleNames = moduleNames;
			this.board = board;
		}
		public void run() {
			try {
				synthesize(projectDir, vhdlDir, design, topModule, moduleNames, board);
				addMessage("---------------------" + newline);
			} catch (Exception e) {
				addMessage("*** error found: " + e.getMessage() + newline);
				e.printStackTrace();
			}
		}
	}
	
	// produce .bit and .mcs files by calling Xilinx command-line tool
	void synthesize(File projectDir, File vhdlDir, ShdlDesign design, SHDLModule topModule, ArrayList moduleNames, SHDLBoard board) throws Exception {
		boolean hasCommModule = (board.getModuleIOStatus(topModule) > 0); // hybrid ou distant only I/O
		String topModuleName = topModule.getName();
		if (hasCommModule) topModuleName = topModuleName + "_comm";
		
		// d�truire le r�pertoire projet pr�c�dent, s'il existe
		if (projectDir.exists()) deleteDir(projectDir);
		
		// cr�er le r�pertoire projet
		projectDir.mkdir();
		
		//- cr�er un fichier myproj.lso qui contient le seul mot: work
		PrintWriter pw = new PrintWriter(new FileOutputStream(new File(projectDir, topModuleName + ".lso")));
		pw.println("work");
		pw.flush();
		pw.close();		
		
		//- cr�er un r�pertoire tmp
		File tmpDir = new File(projectDir, "tmp");
		tmpDir.mkdir();
		
		//- mettre dans myproj.prj la liste des fichiers VHDL � synth�tiser
		pw = new PrintWriter(new FileOutputStream(new File(projectDir, topModuleName + ".prj")));
		for (int i = 0; i < moduleNames.size(); i++) {
			pw.println("vhdl work \"../vhdl/" + moduleNames.get(i) + ".vhd\"");
		}
		pw.flush();
		pw.close();		
		
		//- mettre dans myproj.xst les options pour le synth�tiseur XST
		//(customiser les options -ifn, -ofn et -top)
		pw = new PrintWriter(new FileOutputStream(new File(projectDir, topModuleName + ".xst")));
		pw.println("set -tmpdir \"./tmp\"");
		pw.println("set -xsthdpdir \"./xst\"");
		pw.println("run");
		pw.println("-ifn " + topModuleName + ".prj");
		pw.println("-ifmt mixed");
		pw.println("-ofn " + topModuleName);
		pw.println("-ofmt NGC");
		if (boardName.equals("Nexys-1000")) {
			pw.println("-p xc3s1000-5-ft256");
		} else if (boardName.equals("Nexys II-1200")) {
			pw.println("-p xc3s1200e-5-fg320");
		}
		pw.println("-top " + topModuleName);
		pw.println("-opt_mode Speed");
		pw.println("-opt_level 1");
		pw.println("-iuc NO");
		pw.println("-lso " + topModuleName + ".lso");
		pw.println("-keep_hierarchy NO");
		pw.println("-rtlview Yes");
		pw.println("-glob_opt AllClockNets");
		pw.println("-read_cores YES");
		pw.println("-write_timing_constraints NO");
		pw.println("-cross_clock_analysis NO");
		pw.println("-hierarchy_separator /");
		pw.println("-bus_delimiter <>");
		pw.println("-case maintain");
		pw.println("-slice_utilization_ratio 100");
		pw.println("-verilog2001 YES");
		pw.println("-fsm_extract YES -fsm_encoding Auto");
		pw.println("-safe_implementation No");
		pw.println("-fsm_style lut");
		pw.println("-ram_extract Yes");
		pw.println("-ram_style Auto");
		pw.println("-rom_extract Yes");
		pw.println("-mux_style Auto");
		pw.println("-decoder_extract YES");
		pw.println("-priority_extract YES");
		pw.println("-shreg_extract YES");
		pw.println("-shift_extract YES");
		pw.println("-xor_collapse YES");
		pw.println("-rom_style Auto");
		pw.println("-mux_extract YES");
		pw.println("-resource_sharing YES");
		pw.println("-mult_style auto");
		pw.println("-iobuf YES");
		pw.println("-max_fanout 500");
		pw.println("-bufg 8");
		pw.println("-register_duplication YES");
		pw.println("-register_balancing No");
		pw.println("-slice_packing YES");
		pw.println("-optimize_primitives NO");
		pw.println("-use_clock_enable Yes");
		pw.println("-use_sync_set Yes");
		pw.println("-use_sync_reset Yes");
		pw.println("-iob auto");
		pw.println("-equivalent_register_removal YES");
		pw.println("-slice_utilization_ratio_maxmargin 5");
		pw.flush();
		pw.close();		
		
		//- ex�cuter (synth�se):
		//xst -ise myproj.ise -intstyle ise -ifn myproj.xst -ofn myproj.syr
		String[] command = new String[] { System.getenv("XILINX") + "\\bin\\nt\\"+"xst.exe",
			"-ise", topModuleName + ".ise",
			"-intstyle", "ise",
			"-ifn", topModuleName + ".xst",
			"-ofn", topModuleName + ".syr"
		};
		addMessage("--- synthesize . . .      ");
		int err = executeWin(command, projectDir);
		if (err == 0) addMessage("done\n"); else addMessage("** errors found\n");
		if (err != 0) return;
		
		//cr�er un fichier Nexys.ucf
		pw = new PrintWriter(new FileOutputStream(new File(projectDir, "Nexys.ucf")));
		ArrayList interfaceSignals = design.getTopModule().getInterfaceSignals();
		if (boardName.equals("Nexys-1000")) {			
			if (hasCommModule || new SHDLSignal("mclk", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"mclk\"    LOC = \"A8\"  ;");
			if (hasCommModule || new SHDLSignal("astb", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"astb\"    LOC = \"N8\";");
			if (hasCommModule || new SHDLSignal("dstb", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"dstb\"    LOC = \"P7\";");
			if (hasCommModule || new SHDLSignal("pwr", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pwr\"     LOC = \"N7\";");
			if (hasCommModule || new SHDLSignal("pwait", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pwait\"   LOC = \"N5\";");
			if (hasCommModule || new SHDLSignal("pdb", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<0>\"  LOC = \"N12\";");
			if (hasCommModule || new SHDLSignal("pdb", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<1>\"  LOC = \"P12\";");
			if (hasCommModule || new SHDLSignal("pdb", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<2>\"  LOC = \"N11\";");
			if (hasCommModule || new SHDLSignal("pdb", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<3>\"  LOC = \"P11\";");
			if (hasCommModule || new SHDLSignal("pdb", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<4>\"  LOC = \"N10\";");
			if (hasCommModule || new SHDLSignal("pdb", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<5>\"  LOC = \"P10\";");
			if (hasCommModule || new SHDLSignal("pdb", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<6>\"  LOC = \"M10\";");
			if (hasCommModule || new SHDLSignal("pdb", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<7>\"  LOC = \"R10\";");
			
			if (new SHDLSignal("btn", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<0>\"  LOC = \"J13\"  ;");
			if (new SHDLSignal("btn", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<1>\"  LOC = \"K14\"  ;");
			if (new SHDLSignal("btn", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<2>\"  LOC = \"K13\"  ;");
			if (new SHDLSignal("btn", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<3>\"  LOC = \"K12\"  ;");
			if (new SHDLSignal("sw", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<0>\"   LOC = \"N15\"  ;");
			if (new SHDLSignal("sw", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<1>\"   LOC = \"J16\"  ;");
			if (new SHDLSignal("sw", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<2>\"   LOC = \"K16\"  ;");
			if (new SHDLSignal("sw", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<3>\"   LOC = \"K15\"  ;");
			if (new SHDLSignal("sw", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<4>\"   LOC = \"L15\"  ;");
			if (new SHDLSignal("sw", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<5>\"   LOC = \"M16\"  ;");
			if (new SHDLSignal("sw", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<6>\"   LOC = \"M15\"  ;");
			if (new SHDLSignal("sw", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<7>\"   LOC = \"N16\"  ;");
			if (new SHDLSignal("ld", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<0>\"   LOC = \"L14\"  ;");
			if (new SHDLSignal("ld", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<1>\"   LOC = \"L13\"  ;");
			if (new SHDLSignal("ld", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<2>\"   LOC = \"M14\"  ;");
			if (new SHDLSignal("ld", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<3>\"   LOC = \"L12\"  ;");
			if (new SHDLSignal("ld", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<4>\"   LOC = \"N14\"  ;");
			if (new SHDLSignal("ld", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<5>\"   LOC = \"M13\"  ;");
			if (new SHDLSignal("ld", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<6>\"   LOC = \"P14\"  ;");
			if (new SHDLSignal("ld", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<7>\"   LOC = \"R16\"  ;");
			if (new SHDLSignal("an", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<3>\"   LOC = \"F12\"  ;");
			if (new SHDLSignal("an", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<2>\"   LOC = \"G13\"  ;");
			if (new SHDLSignal("an", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<1>\"   LOC = \"G12\"  ;");
			if (new SHDLSignal("an", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<0>\"   LOC = \"G14\"  ;");
			if (new SHDLSignal("ssg", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<0>\"  LOC = \"F13\"  ;");
			if (new SHDLSignal("ssg", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<1>\"  LOC = \"E13\"  ;");
			if (new SHDLSignal("ssg", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<2>\"  LOC = \"G15\"  ;");
			if (new SHDLSignal("ssg", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<3>\"  LOC = \"H13\"  ;");
			if (new SHDLSignal("ssg", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<4>\"  LOC = \"J14\"  ;");
			if (new SHDLSignal("ssg", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<5>\"  LOC = \"E14\"  ;");
			if (new SHDLSignal("ssg", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<6>\"  LOC = \"G16\"  ;");
			if (new SHDLSignal("ssg", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<7>\"  LOC = \"H14\"  ;");
			if (new SHDLSignal("ja1_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja1_out\" LOC = \"T14\"  ;");
			if (new SHDLSignal("ja2_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja2_out\" LOC = \"R13\"  ;");
			if (new SHDLSignal("ja3_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja3_out\" LOC = \"T13\"  ;");
			if (new SHDLSignal("ja4_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja4_out\" LOC = \"R12\"  ;");

		} else if (boardName.equals("Nexys II-1200")) {			
			if (hasCommModule || new SHDLSignal("mclk", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"mclk\"    LOC = \"B8\"  ;");
			if (hasCommModule || new SHDLSignal("astb", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"astb\"   LOC = \"V14\";");
			if (hasCommModule || new SHDLSignal("dstb", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"dstb\"   LOC = \"U14\";");
			if (hasCommModule || new SHDLSignal("pwr", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pwr\"     LOC = \"V16\";");  // appel� USBFlag dans les .ucf Digilent!!!!!!
			if (hasCommModule || new SHDLSignal("pwait", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pwait\"   LOC = \"N9\";");
			if (hasCommModule || new SHDLSignal("pdb", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<0>\"  LOC = \"R14\";");
			if (hasCommModule || new SHDLSignal("pdb", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<1>\"  LOC = \"R13\";");
			if (hasCommModule || new SHDLSignal("pdb", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<2>\"  LOC = \"P13\";");
			if (hasCommModule || new SHDLSignal("pdb", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<3>\"  LOC = \"T12\";");
			if (hasCommModule || new SHDLSignal("pdb", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<4>\"  LOC = \"N11\";");
			if (hasCommModule || new SHDLSignal("pdb", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<5>\"  LOC = \"R11\";");
			if (hasCommModule || new SHDLSignal("pdb", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<6>\"  LOC = \"P10\";");
			if (hasCommModule || new SHDLSignal("pdb", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"pdb<7>\"  LOC = \"R10\";");

			if (new SHDLSignal("btn", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<0>\"  LOC = \"B18\"  ;");
			if (new SHDLSignal("btn", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<1>\"  LOC = \"D18\"  ;");
			if (new SHDLSignal("btn", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<2>\"  LOC = \"E18\"  ;");
			if (new SHDLSignal("btn", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"btn<3>\"  LOC = \"H13\"  ;");
			if (new SHDLSignal("sw", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<0>\"  LOC = \"G18\"  ;");
			if (new SHDLSignal("sw", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<1>\"  LOC = \"H18\"  ;");
			if (new SHDLSignal("sw", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<2>\"  LOC = \"K18\"  ;");
			if (new SHDLSignal("sw", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<3>\"  LOC = \"K17\"  ;");
			if (new SHDLSignal("sw", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<4>\"  LOC = \"L14\"  ;");
			if (new SHDLSignal("sw", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<5>\"  LOC = \"L13\"  ;");
			if (new SHDLSignal("sw", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<6>\"  LOC = \"N17\"  ;");
			if (new SHDLSignal("sw", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"sw<7>\"  LOC = \"R17\"  ;");
			if (new SHDLSignal("ld", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<0>\"  LOC = \"J14\"  ;");
			if (new SHDLSignal("ld", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<1>\"  LOC = \"J15\"  ;");
			if (new SHDLSignal("ld", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<2>\"  LOC = \"K15\"  ;");
			if (new SHDLSignal("ld", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<3>\"  LOC = \"K14\"  ;");
			if (new SHDLSignal("ld", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<4>\"  LOC = \"E16\"  ;");
			if (new SHDLSignal("ld", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<5>\"  LOC = \"P16\"  ;");
			if (new SHDLSignal("ld", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<6>\"  LOC = \"E4\"  ;");
			if (new SHDLSignal("ld", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ld<7>\"  LOC = \"P4\"  ;");
			if (new SHDLSignal("an", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<0>\"   LOC = \"F17\"  ;");
			if (new SHDLSignal("an", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<1>\"   LOC = \"H17\"  ;");
			if (new SHDLSignal("an", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<2>\"   LOC = \"C18\"  ;");
			if (new SHDLSignal("an", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"an<3>\"   LOC = \"F15\"  ;");
			if (new SHDLSignal("ssg", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<0>\"  LOC = \"L18\"  ;");
			if (new SHDLSignal("ssg", 1, 1, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<1>\"  LOC = \"F18\"  ;");
			if (new SHDLSignal("ssg", 2, 2, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<2>\"  LOC = \"D17\"  ;");
			if (new SHDLSignal("ssg", 3, 3, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<3>\"  LOC = \"D16\"  ;");
			if (new SHDLSignal("ssg", 4, 4, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<4>\"  LOC = \"G14\"  ;");
			if (new SHDLSignal("ssg", 5, 5, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<5>\"  LOC = \"J17\"  ;");
			if (new SHDLSignal("ssg", 6, 6, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<6>\"  LOC = \"H14\"  ;");
			if (new SHDLSignal("ssg", 7, 7, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ssg<7>\"  LOC = \"C17\"  ;");
			if (new SHDLSignal("ja1_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja1_out\"  LOC = \"L15\"  ;");
			if (new SHDLSignal("ja2_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja2_out\"  LOC = \"K12\"  ;");
			if (new SHDLSignal("ja3_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja3_out\"  LOC = \"L17\"  ;");
			if (new SHDLSignal("ja4_out", 0, 0, design.getTopModule()).containedIn(interfaceSignals)) pw.println("NET \"ja4_out\"  LOC = \"M15\"  ;");
		}
		pw.flush();
		pw.close();
		
		//- ex�cuter (translation):
		//ngdbuild -ise myproj.ise -intstyle ise -dd _ngo -nt timestamp -uc Nexys.ucf -p xc3s1000-ft256-5 myproj.ngc myproj.ngd
		String fpga = "";
		if (boardName.equals("Nexys-1000")) {
			fpga = "xc3s1000-ft256-5";
		} else if (boardName.equals("Nexys II-1200")) {
			fpga = "xc3s1200e-fg320-5";
		}
		command = new String[] { System.getenv("XILINX") + "\\bin\\nt\\"+"ngdbuild.exe",
			"-ise", topModuleName + ".ise",
			"-intstyle", "ise",
			"-dd", "_ngo",
			"-nt", "timestamp",
			"-uc", "Nexys.ucf",
			"-p", fpga,
			topModuleName + ".ngc",
			topModuleName + ".ngd",
		};
		addMessage("--- translate . . .       ");
		err = executeWin(command, projectDir);
		if (err == 0) addMessage("done\n"); else addMessage("** errors found\n");
		if (err != 0) return;
		
		//- ex�cuter (map):
		//map -ise myproj.ise -intstyle ise -p xc3s1000-ft256-5 -cm area -pr b -k 4 -c 100 -o myproj.ncd myproj.ngd myproj.pcf
		command = new String[] { System.getenv("XILINX") + "\\bin\\nt\\"+"map.exe",
			"-ise", topModuleName + ".ise",
			"-intstyle", "ise",
			"-p", fpga,
			"-cm", "area",
			"-pr", "b",
			"-k", "4",
			"-c", "100",
			"-o", topModuleName + ".ncd",
			topModuleName + ".ngd",
			topModuleName + ".pcf",
		};
		addMessage("--- map . . .             ");
		err = executeWin(command, projectDir);
		if (err == 0) addMessage("done\n"); else addMessage("** errors found\n");
		if (err != 0) return;
		
		//- ex�cuter (place and route, par):
		//par -ise myproj.ise -w -intstyle ise -ol std -t 1 myproj.ncd myproj.ncd myproj.pcf
		command = new String[] { System.getenv("XILINX") + "\\bin\\nt\\"+"par.exe",
			"-ise", topModuleName + ".ise",
			"-w",
			"-intstyle", "ise",
			"-ol", "std",
			"-t", "1",
			topModuleName + ".ncd",
			topModuleName + ".ncd",
			topModuleName + ".pcf",
		};
		addMessage("--- place and route . . . ");
		err = executeWin(command, projectDir);
		if (err == 0) addMessage("done\n"); else addMessage("** errors found\n");
		if (err != 0) return;
		
		//- ex�cuter:
		//trce -ise myproj.ise -intstyle ise -e 3 -l 3 -s 5 -xml myproj myproj.ncd -o myproj.twr myproj.pcf -ucf Nexys.ucf
		command = new String[] { System.getenv("XILINX") + "\\bin\\nt\\"+"trce.exe",
			"-ise", topModuleName + ".ise",
			"-intstyle", "ise",
			"-l", "3",
			"-s", "5",
			"-xml", topModuleName,
			topModuleName + ".ncd",
			"-o", topModuleName + ".twr",
			topModuleName + ".pcf",
			"-ucf", "Nexys.ucf",
		};
		addMessage("--- trace . . .           ");
		err = executeWin(command, projectDir);
		if (err == 0) addMessage("done\n"); else addMessage("** errors found\n");
		if (err != 0) return;

		//- cr�er un fichier myproj.ut qui contient les options pour la bitgen
		pw = new PrintWriter(new FileOutputStream(new File(projectDir, topModuleName + ".ut")));
		pw.println("-w");
		pw.println("-g DebugBitstream:No");
		pw.println("-g Binary:no");
		pw.println("-g CRC:Enable");
		pw.println("-g ConfigRate:6");
		pw.println("-g CclkPin:PullUp");
		pw.println("-g M0Pin:PullUp");
		pw.println("-g M1Pin:PullUp");
		pw.println("-g M2Pin:PullUp");
		pw.println("-g ProgPin:PullUp");
		pw.println("-g DonePin:PullUp");
		pw.println("-g TckPin:PullUp");
		pw.println("-g TdiPin:PullUp");
		pw.println("-g TdoPin:PullUp");
		pw.println("-g TmsPin:PullUp");
		pw.println("-g UnusedPin:PullDown");
		pw.println("-g UserID:0xFFFFFFFF");
		pw.println("-g DCMShutdown:Disable");
		pw.println("-g DCIUpdateMode:AsRequired");
		pw.println("-g StartUpClk:JtagClk");
		pw.println("-g DONE_cycle:4");
		pw.println("-g GTS_cycle:5");
		pw.println("-g GWE_cycle:6");
		pw.println("-g LCK_cycle:NoWait");
		pw.println("-g Match_cycle:Auto");
		pw.println("-g Security:None");
		pw.println("-g DonePipe:No");
		pw.println("-g DriveDone:No");
		pw.flush();
		pw.close();		
		
		//- ex�cuter (bitgen):
		//bitgen -ise myproj.ise -intstyle ise -f myproj.ut myproj.ncd
		command = new String[] { System.getenv("XILINX") + "\\bin\\nt\\" + "bitgen.exe",
			//"-ise", topModuleName + ".ise",
			//"-f", topModuleName + ".ut",
			"-intstyle", "ise",
			"-w",
			"-g", "DebugBitstream:No",
			"-g", "Binary:no",
			"-g", "CRC:Enable",
			"-g", "ProgPin:PullUp",
			"-g", "DonePin:PullUp",
			"-g", "TckPin:PullUp",
			"-g", "TdiPin:PullUp",
			"-g", "TdoPin:PullUp",
			"-g", "TmsPin:PullUp",
			"-g", "UnusedPin:PullDown",
			"-g", "UserID:0xFFFFFFFF",
			"-g", "DCMShutdown:Disable",
			"-g", "StartupClk:JtagClk",
			"-g", "DONE_cycle:4",
			"-g", "GTS_cycle:5",
			"-g", "GWE_cycle:6",
			"-g", "LCK_cycle:NoWait",
			"-g", "Security:None",
			"-g", "DonePipe:No",
			"-g", "DriveDone:No",
			topModuleName + ".ncd",
		};
		addMessage("--- bitgen. . .           ");
		err = executeWin(command, projectDir);
		if (err == 0) addMessage("done\n"); else addMessage("** errors found\n");
		if (err != 0) return;
		
		// cr�ation du fichiers .mcs � partir du fichier .bit
		// g�n�ration d'un fichier impact.cmd
		pw = new PrintWriter(new FileOutputStream(new File(projectDir, "impact.cmd")));
		pw.println("setMode -pff");
		pw.println("setSubmode -pffserial");
		pw.println("addPromDevice -p 1 -name xcf04s");
		pw.println("addDesign -version 0 -name 0");
		pw.println("addDeviceChain -index 0");
		pw.println("addDevice -p 1 -file " + topModuleName + ".bit");
		pw.println("generate -format mcs -fillvalue FF -output " + topModuleName);
		pw.println("quit");
		pw.flush();
		pw.close();		
		command = new String[] { System.getenv("XILINX") + "\\bin\\nt\\"+"impact.exe",
			"-batch", "impact.cmd",
		};
		addMessage("--- .mcs file creation. . . ");
		err = executeWin(command, projectDir);
		if (err == 0) addMessage("done\n"); else addMessage("** errors found\n");
		if (err != 0) return;
		
	}
	
	// ex�cute <command> dans le r�pertoire de travail <dir>, et affiche les messages normaux et d'erreur
	// renvoie la valeur de retour
	int executeWin(String[] command, File dir) throws Exception {
		ProcessBuilder builder = new ProcessBuilder(command);
		Map<String, String> environ = builder.environment();
		builder.directory(dir);
	
		final Process process = builder.start();
		
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			if (verboseCheck.isSelected()) addMessage(line + "\n");
			System.out.println(line);
		}

		InputStream eis = process.getErrorStream();
		InputStreamReader eisr = new InputStreamReader(eis);
		BufferedReader ebr = new BufferedReader(eisr);
		String eline;
		while ((eline = ebr.readLine()) != null) {
			if (verboseCheck.isSelected()) addMessage("*** " + eline + "\n");
			System.out.println(line);
		}
		
		return process.exitValue();
	}
		
	
	// d�truit le r�pertoire <dir> et tout son contenu
	boolean deleteDir(File dir) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory())
				deleteDir(files[i]);
			else
				files[i].delete();
		}
		return dir.delete();
	}

}
