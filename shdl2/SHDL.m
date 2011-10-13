
-- version 1.3.7

inh	hsignal :	SHDLSignal
  for SIGNALX, SIGNALXX, CMD, CMD1, CMD11, CMD21, CMD2 ;

syn	signal :	SHDLSignal
  for SIGNAL ;

inh	hmodules :	SHDLModules
  for CMDS_OR_MODULES, MODULE ;

syn	modules :	SHDLModules
  for PROG, MODULE ;

syn	module :	SHDLModule
  for MODULE ;

inh	hmodule :	SHDLModule
  for HEADER, INTERF_SIGNALS, INTERF_SIGNALSX, SIGNAL, SIGNAL_, SUBM_SIGNALS, SUBM_SIGNALSX,
      CMDS_OR_MODULES, CMDS, CMD, CMD1, CMD2, CMD21, CMD11, CMD111, CMD1111,
      SEQEQ, SEQEQX, TERM, TERMX, TERMSUM, TERMSUMX ;

syn	signalOccurence: SHDLSignalOccurence
  for SIGNAL_ ;

syn	terme :		SHDLTerm
  for TERM ;

inh	hterm :		SHDLTerm
  for TERM, TERMX ;

syn	termsum :	SHDLTermsSum
  for TERMSUM ;

inh	htermsum :	SHDLTermsSum
  for TERMSUM, TERMSUMX ;

syn	combinSetting :	SHDLCombinatorialSetting
  for CMD1 ;

inh	hcombinSetting : SHDLCombinatorialSetting
  for CMD11, CMD111, CMD1111 ;

syn	seqSetting :	SHDLSequentialSetting
  for CMD11 ;

inh	hseqSetting :	SHDLSequentialSetting
  for SEQEQ, SEQEQX ;

syn	modOccurence :	SHDLModuleOccurence
  for CMD ;

inh	hmodOccurence :	SHDLModuleOccurence
  for CMD2, CMD21, SUBM_SIGNALS, SUBM_SIGNALSX ;


space	comm		is	"//.*$" ; -- commentaires elimines
--space	separateur	is	"[\n\t ]+" ; -- blancs
space	blank		is	"[ ]+" ; -- blancs
space	rc		is	"[\n]+" ; -- blancs
space	lfs		is	"[\n\r]+" ; -- blancs
space	tabs		is	"[\t]+" ; -- blancs
sugar	or		is	"\+" ;
sugar	and		is	"\*" ;
sugar	parouv		is	"\(" ;
sugar	parfer		is	"\)" ;
sugar	crocouv		is	"\[" ;
sugar	crocfer		is	"\]" ;
sugar	ptpt		is	"\.\." ;
sugar	pt		is	"\." ;
sugar	semicol		is	":" ;
sugar	virg		is	"," ;
sugar	pv		is	";" ;
sugar	aff		is	"=" ;
sugar	affs		is	":=" ;
sugar	end_		is	"end" ;
sugar	module		is	"module" ;
sugar	slash		is	"\/" ;
term	ident		is	"[A-Za-z_]+[A-Za-z_0-9]*" ;

-- VHDL basic identifier
--macro	identx		is	"_*[A-Za-z0-9]" ;
--term	ident		is	"[A-Za-z_]{identx}*" ;

macro	digit2		is	"[0-1]" ;
term	num2		is	"0b{digit2}+" ;
macro	minus		is	"\-" ;
macro	digit10		is	"{minus}?[0-9]" ;
term	num10		is	"{digit10}+" ;
macro	digit16		is	"[0-9a-fA-F]" ;
term	num16		is	"0x{digit16}+" ;


PROG -> #init CMDS_OR_MODULES ;
#init {
	local
		modules : SHDLModules;
		mainModule : SHDLModule;
	do
		modules := new SHDLModules();
		CMDS_OR_MODULES^hmodules := modules;
		PROG^modules := modules;
		mainModule := new SHDLModule();
		call modules.addModule(mainModule);
		CMDS_OR_MODULES^hmodule := mainModule;
	end
}

-- list of commands or module definitions
CMDS_OR_MODULES -> #trans CMD CMDS_OR_MODULES;
#trans {
	local
	do
		CMDS_OR_MODULES1^hmodules := CMDS_OR_MODULES^hmodules;
		CMD^hmodule := CMDS_OR_MODULES^hmodule;
		CMD^hsignal := nil;
		CMDS_OR_MODULES1^hmodule := CMDS_OR_MODULES^hmodule;
	end
}

CMDS_OR_MODULES -> #trans MODULE #add CMDS_OR_MODULES;
	global
		beginLine : INTEGER;
#trans {
	local
	do
		CMDS_OR_MODULES1^hmodules := CMDS_OR_MODULES^hmodules;
		MODULE^hmodules := CMDS_OR_MODULES^hmodules;
		CMDS_OR_MODULES1^hmodule := CMDS_OR_MODULES^hmodule;
		beginLine := CMDS_OR_MODULES^scanner.getBeginLine();
	end
}
#add {
	local
	do
		call MODULE^modules.addModule(MODULE^module);
		call MODULE^module.setBeginLine(beginLine);
	end
}

CMDS_OR_MODULES -> #print;
#print {
	local
	do
		--write CMDS_OR_MODULES^hmodules @ "%N" ;
	end
}


-- list of commands
CMDS -> ;

CMDS -> #trans CMD CMDS ;
#trans {
	local
	do
		CMD^hmodule := CMDS^hmodule;
		CMD^hsignal := nil;
		CMDS1^hmodule := CMDS^hmodule;
	end
}

-- module definition
MODULE -> #create_module HEADER CMDS FOOTER #end_module;
	global
		module : SHDLModule;
#create_module {
	local
	do
		module := new SHDLModule();
		MODULE^module := module;
		HEADER^hmodule := module;
		CMDS^hmodule := module;
	end
}
#end_module {
	local
		line : INTEGER;
	do
		MODULE^modules := MODULE^hmodules;
		line := MODULE^scanner.getEndLine();
		call module.setEndLine(line);
	end
}

-- module definition header
HEADER -> module ident #add_name parouv #trans1 INTERF_SIGNALS parfer;
#add_name {
	local
	do
		call HEADER^hmodule.setName(ident^txt);
	end
}
#trans1 {
	local
	do
		INTERF_SIGNALS^hmodule := HEADER^hmodule;
	end
}

-- signal (and constants : they are considered as signals and have a specific constructor)
SIGNAL -> ident #init SIGNALX;
#init {
	local
		signal : SHDLSignal;
	do
		signal := new SHDLSignal(ident^txt, false, SIGNAL^hmodule);
		SIGNAL^signal := signal;
		SIGNALX^hsignal := signal;
	end
}
SIGNAL -> num2 #set; -- 0b1011
#set {
	local
		signal : SHDLSignal;
	do
		signal := new SHDLSignal(num2^txt, SIGNAL^hmodule); -- special constructor for constants
		SIGNAL^signal := signal;
	end
}
SIGNAL -> num10 #set; -- 1234
#set {
	local
		signal : SHDLSignal;
	do
		signal := new SHDLSignal(num10^txt, SIGNAL^hmodule); -- special constructor for constants
		SIGNAL^signal := signal;
	end
}
SIGNAL -> num16 #set; -- 0xaf5e
#set {
	local
		signal : SHDLSignal;
	do
		signal := new SHDLSignal(num16^txt, SIGNAL^hmodule); -- special constructor for constants
		SIGNAL^signal := signal;
	end
}
SIGNALX -> ;
SIGNALX -> crocouv num10 #set SIGNALXX ;
#set {
	local
	do
		SIGNALXX^hsignal := SIGNALX^hsignal;
		call SIGNALX^hsignal.setN1(num10^txt);
	end
}
SIGNALXX -> ptpt num10 #set crocfer ;
#set {
	local
	do
		call SIGNALXX^hsignal.setN2(num10^txt);
	end
}
SIGNALXX -> crocfer ;

-- list of signals in interface list
INTERF_SIGNALS -> ;
INTERF_SIGNALS -> #trans SIGNAL #add INTERF_SIGNALSX ;
#trans {
	local
	do
		SIGNAL^hmodule := INTERF_SIGNALS^hmodule;
		INTERF_SIGNALSX^hmodule := INTERF_SIGNALS^hmodule;
	end
}
#add {
	local
	do
		call SIGNAL^signal.setIsInterface(true);
		call INTERF_SIGNALS^hmodule.addInterfaceSignal(SIGNAL^signal);
	end
}
INTERF_SIGNALSX -> ;
INTERF_SIGNALSX -> virg #trans INTERF_SIGNALS ;
#trans {
	local
	do
		INTERF_SIGNALS^hmodule := INTERF_SIGNALSX^hmodule;
	end
}
INTERF_SIGNALSX -> semicol #trans INTERF_SIGNALS ;
#trans {
	local
	do
		INTERF_SIGNALS^hmodule := INTERF_SIGNALSX^hmodule;
	end
}

-- list of signals in submodule instances
SUBM_SIGNALS -> ;
SUBM_SIGNALS -> #trans SIGNAL #set SUBM_SIGNALSX ;
#trans {
	local
	do
		SIGNAL^hmodule := SUBM_SIGNALS^hmodule;
		SUBM_SIGNALSX^hmodule := SUBM_SIGNALS^hmodule;
		SUBM_SIGNALSX^hmodOccurence := SUBM_SIGNALS^hmodOccurence;
	end
}
#set {
	local
	do
		call SUBM_SIGNALS^hmodOccurence.addArgument(SIGNAL^signal);
		call SUBM_SIGNALS^hmodule.addModuleSignal(SIGNAL^signal);
	end
}
SUBM_SIGNALSX -> ;
SUBM_SIGNALSX -> virg #trans SUBM_SIGNALS ;
#trans {
	local
	do
		SUBM_SIGNALS^hmodule := SUBM_SIGNALSX^hmodule;
		SUBM_SIGNALS^hmodOccurence := SUBM_SIGNALSX^hmodOccurence;
	end
}

FOOTER -> end_ module ;

-- commande : affectation combinatoire ou séquentielle
CMD -> #trans SIGNAL #trans2 CMD1 ;
#trans {
	local
	do
		SIGNAL^hmodule := CMD^hmodule;
		CMD1^hmodule := CMD^hmodule;
		CMD^modOccurence := nil;
	end
}
#trans2 {
	local
	do
		CMD1^hsignal := SIGNAL^signal;
		call CMD^hmodule.addModuleSignal(SIGNAL^signal);
	end
}

CMD1 -> aff #trans CMD111;
#trans {
	local
		combinSetting : SHDLCombinatorialSetting;
	do
		combinSetting := new SHDLCombinatorialSetting(CMD1^scanner.getBeginLine(), CMD1^hmodule);
		CMD1^combinSetting := combinSetting;
		CMD111^hmodule := CMD1^hmodule;
		CMD111^hcombinSetting := combinSetting;
		call combinSetting.setSignal(CMD1^hsignal);
		call CMD1^hmodule.addCombinSetting(combinSetting);
		--call CMD1^hmodule.addCombinSettingSource(combinSetting);
	end
}
CMD1 -> pt #trans CMD11 ;
#trans {
	local
	do
		CMD11^hmodule := CMD1^hmodule;
		CMD11^hsignal := CMD1^hsignal;
		CMD1^combinSetting := nil;
		CMD11^hcombinSetting := nil;
	end
}
CMD1 -> affs #init SEQEQ pv ;
#init {
	local
		seqSetting : SHDLSequentialSetting;
	do
		seqSetting := new SHDLSequentialSetting(CMD1^scanner.getBeginLine(), CMD1^hmodule);
		SEQEQ^hmodule := CMD1^hmodule;
		SEQEQ^hseqSetting := seqSetting;
		CMD1^combinSetting := nil;
		call seqSetting.setSignal(CMD1^hsignal);
		call CMD1^hmodule.addSeqSetting(seqSetting);
		--call CMD1^hmodule.addSeqSettingSource(seqSetting);
	end
}

CMD11 -> ident aff #trans SIGNAL_ #set pv;
	global
		seqModifier : SHDLSequentialModifier;
#trans {
	local
	do
		seqModifier := new SHDLSequentialModifier(CMD11^scanner.getBeginLine(), CMD11^hmodule);
		call seqModifier.setSignal(CMD11^hsignal);
		call seqModifier.setModifier(ident^txt);
		call CMD11^hmodule.addSeqModifier(seqModifier);
		if (ident^txt.equalsIgnoreCase("clk")) then
			SIGNAL_^hmodule := CMD11^hmodule;
		elseif (ident^txt.equalsIgnoreCase("rst")) then
			SIGNAL_^hmodule := CMD11^hmodule;
		elseif (ident^txt.equalsIgnoreCase("set")) then
			SIGNAL_^hmodule := CMD11^hmodule;
		elseif (ident^txt.equalsIgnoreCase("ena")) then
			SIGNAL_^hmodule := CMD11^hmodule;
		else
			error(P_02, ident^txt, ident^txt);
		end
		CMD11^seqSetting := nil;
	end
}
#set {
	local
	do
		call seqModifier.setSignalOccurence(SIGNAL_^signalOccurence);
	end
}

CMD111 -> #trans TERMSUM CMD1111 pv;
#trans {
	local
		termsum : SHDLTermsSum;
	do
		TERMSUM^hmodule := CMD111^hmodule;
		CMD1111^hmodule := CMD111^hmodule;
		CMD1111^hcombinSetting := CMD111^hcombinSetting;
		termsum := new SHDLTermsSum(CMD111^hmodule);
		TERMSUM^htermsum := termsum;
		call CMD111^hcombinSetting.setEquation(termsum);
	end
}
CMD1111 -> ;
CMD1111 -> #trans semicol SIGNAL_ #set ;
#trans {
	local
	do
		SIGNAL_^hmodule := CMD1111^hmodule;
	end
}
#set {
	local
	do
		call CMD1111^hcombinSetting.setOE(SIGNAL_^signalOccurence);
	end
}

---- commande : instance de module
CMD -> ident #trans CMD2 ;
#trans {
	local
		moduleOccurence : SHDLModuleOccurence;
		line : INTEGER;
	do
		CMD2^hmodule := CMD^hmodule;
		CMD2^hsignal := CMD^hsignal;
		line := CMD^scanner.getBeginLine();
		moduleOccurence := new SHDLModuleOccurence(ident^txt, line, CMD^hmodule);
		CMD^modOccurence := moduleOccurence;
		CMD2^hmodOccurence := moduleOccurence;
	end
}
CMD2 -> parouv #trans SUBM_SIGNALS CMD21 ;
#trans {
	local
	do
		SUBM_SIGNALS^hmodule := CMD2^hmodule;
		CMD21^hmodule := CMD2^hmodule;
		CMD21^hsignal := CMD2^hsignal;
		SUBM_SIGNALS^hmodOccurence := CMD2^hmodOccurence;
		CMD21^hmodOccurence := CMD2^hmodOccurence;
	end
}
CMD21 -> parfer #update pv ;
#update {
	local
	do
		call CMD21^hmodule.addModuleOccurence(CMD21^hmodOccurence);
	end
}
CMD21 -> semicol #trans SUBM_SIGNALS parfer #update pv ;
#trans {
	local
	do
		SUBM_SIGNALS^hmodule := CMD21^hmodule;
		SUBM_SIGNALS^hmodOccurence := CMD21^hmodOccurence;
	end
}
#update {
	local
	do
		call CMD21^hmodule.addModuleOccurence(CMD21^hmodOccurence);
	end
}

-- équation d'évolution de l'affectation séquentielle
SEQEQ -> #trans SIGNAL_ #trans2 SEQEQX;
#trans {
	local
	do
		SIGNAL_^hmodule := SEQEQ^hmodule;
		SEQEQX^hseqSetting := SEQEQ^hseqSetting;
	end
}
#trans2 {
	local
	do
		SEQEQX^hmodule := SEQEQ^hmodule;
		call SEQEQ^hseqSetting.setSig1(SIGNAL_^signalOccurence);
	end
}
SEQEQX -> ; -- bascule D
SEQEQX -> and #trans SIGNAL_ or SIGNAL_ and SIGNAL_ #set ; -- bascule T, JK
#trans {
	local
	do
		SIGNAL_^hmodule := SEQEQX^hmodule;
		SIGNAL_1^hmodule := SEQEQX^hmodule;
		SIGNAL_2^hmodule := SEQEQX^hmodule;
	end
}
#set {
	local
	do
		call SEQEQX^hseqSetting.setSig2(SIGNAL_^signalOccurence);
		call SEQEQX^hseqSetting.setSig3(SIGNAL_1^signalOccurence);
		call SEQEQX^hseqSetting.setSig4(SIGNAL_2^signalOccurence);
	end
}

-- signal simple ou inversÃ©
SIGNAL_ -> #trans SIGNAL #create;
#trans {
	local
	do
		SIGNAL^hmodule := SIGNAL_^hmodule;
	end
}
#create {
	local
		signalOccurence : SHDLSignalOccurence;
	do
		signalOccurence := new SHDLSignalOccurence(SIGNAL^signal, false, SIGNAL_^hmodule);
		SIGNAL_^signalOccurence := signalOccurence;
		call SIGNAL_^hmodule.addModuleSignal(SIGNAL^signal);
	end
}


SIGNAL_ -> slash #trans SIGNAL #create ;
#trans {
	local
	do
		SIGNAL^hmodule := SIGNAL_^hmodule;
	end
}
#create {
	local
		signalOccurence : SHDLSignalOccurence;
	do
		signalOccurence := new SHDLSignalOccurence(SIGNAL^signal, true, SIGNAL_^hmodule);
		SIGNAL_^signalOccurence := signalOccurence;
	end
}

TERMSUM -> #trans TERM TERMSUMX ;
#trans {
	local
		term : SHDLTerm;
	do
		TERM^hmodule := TERMSUM^hmodule;
		TERMSUMX^hmodule := TERMSUM^hmodule;
		term := new SHDLTerm(TERMSUM^hmodule);
		TERM^hterm := term;
		TERMSUM^termsum := TERMSUM^htermsum;
		TERMSUMX^htermsum := TERMSUM^htermsum;
		call TERMSUM^htermsum.addTerm(term);
	end
}
TERMSUMX -> ;
TERMSUMX -> or #trans TERMSUM;
#trans {
	local
	do
		TERMSUM^hmodule := TERMSUMX^hmodule;
		TERMSUM^htermsum := TERMSUMX^htermsum;
	end
}

TERM -> #trans SIGNAL_ #add TERMX ;
#trans {
	local
	do
		SIGNAL_^hmodule := TERM^hmodule;
		TERMX^hmodule := TERM^hmodule;
		TERM^terme := TERM^hterm;
		TERMX^hterm := TERM^hterm;
	end
}
#add {
	local
	do
		call TERM^hterm.addSignalOccurence(SIGNAL_^signalOccurence);
	end
}

TERMX -> ;
TERMX -> and #trans TERM ;
#trans {
	local
	do
		TERM^hmodule := TERMX^hmodule;
		TERM^hterm := TERMX^hterm;
	end
}

S_00, "unexpected symbol ^1 instead of ^2.", 2 ;
S_01, "end expected near ^1.", 1 ;
S_02, "end expected near ^1.", 1 ;

P_02, ".clk, .rst, .set or .ena expected, instead of ^1.", 1 ;

end

