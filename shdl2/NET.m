
syn	diagram :	NETStateDiagram
  for DIAGRAM ;

syn	transition :	NETTransition
  for TRANSITION ;

syn	interface :	NETInterface
  for INTERFACE ;

syn	statements :	NETStatements
  for STATEMENTS ;

syn	statement :	NETStatement
  for STATEMENT ;

inh	hsignal :	NETSignal
  for SIGNALX, SIGNALXX ;

syn	signal :	NETSignal
  for SIGNAL ;

inh	hsignals :	NETSignals
  for SIGNALS, SIGNALSX ;

syn	signals :	NETSignals
  for SIGNALS, SIGNALSX ;

syn	signalOccurence: NETSignalOccurence
  for SIGNAL_ ;

syn	terme :		NETTerm
  for TERM ;

inh	hterm :		NETTerm
  for TERM, TERMX ;

syn	termsum :	NETTermsSum
  for TERMSUM ;

inh	htermsum :	NETTermsSum
  for TERMSUM, TERMSUMX ;
  
syn outputs : NETMooreOutputs
	for OUTPUTS ;
  
syn affectation : NETAffectation
	for AFFECTATION ;
  
syn affectations : NETAffectations
	for AFFECTATIONS, AFFECTATIONSX, OPT_OUTPUTS ;

inh	haffectations :	NETAffectations
  for AFFECTATIONS, AFFECTATIONSX, OPT_OUTPUTS ;


space	comm		is	"//.*$" ; -- commentaires elimines

space	blank		is	"[ ]+" ; -- blancs
space	rc			is	"[\n]+" ; -- blancs
space	lfs			is	"[\n\r]+" ; -- blancs
space	tabs		is	"[\t]+" ; -- blancs

sugar	module		is	"module" ;
sugar	reset		is	"reset" ;
sugar	clock		is	"clock" ;
sugar	inputs		is	"inputs" ;
sugar	outputs		is	"outputs" ;
sugar	added_outputs	is	"added_outputs" ;
sugar	when		is	"when" ;
sugar	and			is	"\*" ;
sugar	or			is	"\+" ;
sugar	arrow		is	"\-\>" ;
sugar	lpar		is	"\(" ;
sugar	rpar		is	"\)" ;
sugar	virg		is	"," ;
sugar	egal		is	"=" ;
sugar	pv			is	";" ;
sugar	semicol		is	":" ;
sugar	slash		is	"/" ;
sugar	crocouv		is	"\[" ;
sugar	crocfer		is	"\]" ;
sugar	ptpt		is	"\.\." ;

term	ident		is	"[A-Za-z_]+[A-Za-z_0-9]*" ;
term	added		is	"\+\+.*$" ;

macro	digit2		is	"[0-1]" ;
term	num2		is	"0b{digit2}+" ;
macro	minus		is	"\-" ;
macro	digit10		is	"{minus}?[0-9]" ;
term	num10		is	"{digit10}+" ;
macro	digit16		is	"[0-9a-fA-F]" ;
term	num16		is	"0x{digit16}+" ;



DIAGRAM -> INTERFACE STATEMENTS #create ;
#create {
	local
		diagram : NETStateDiagram ;
	do
		diagram := new NETStateDiagram(INTERFACE^interface, STATEMENTS^statements) ;
		DIAGRAM^diagram := diagram ;
		--write diagram @ "%N" ;
	end
}

INTERFACE -> module semicol ident reset semicol SIGNAL_ clock semicol SIGNAL_ inputs semicol #inputs SIGNALS outputs semicol #outputs SIGNALS added_outputs semicol #added_outputs SIGNALS #create;
#inputs {
	local
		inputs : NETSignals ;
	do
		inputs := new NETSignals() ;
		SIGNALS^hsignals := inputs ;
	end
}
#outputs {
	local
		outputs : NETSignals ;
	do
		outputs := new NETSignals() ;
		SIGNALS1^hsignals := outputs ;
	end
}
#added_outputs {
	local
		added_outputs : NETSignals ;
	do
		added_outputs := new NETSignals() ;
		SIGNALS2^hsignals := added_outputs ;
	end
}
#create {
	local
		interface : NETInterface ;
	do
		interface := new NETInterface(ident^txt, SIGNAL_^signalOccurence, SIGNAL_1^signalOccurence, SIGNALS^signals, SIGNALS1^signals, SIGNALS2^signals) ;
		INTERFACE^interface := interface ;
		--write interface @ "%N" ;
	end
}

SIGNALS -> #trans ;
#trans {
	local
	do
		SIGNALS^signals := SIGNALS^hsignals;
	end
}
SIGNALS -> SIGNAL #trans SIGNALSX #add ;
#trans {
	local
	do
		SIGNALS^signals := SIGNALS^hsignals;
		SIGNALSX^hsignals := SIGNALS^hsignals;
	end
}
#add {
	local
	do
		call SIGNALS^hsignals.addSignal(SIGNAL^signal);
	end
}
SIGNALSX -> #trans ;
#trans {
	local
	do
		SIGNALSX^signals := SIGNALSX^hsignals;
	end
}
SIGNALSX -> virg #trans SIGNALS ;
#trans {
	local
	do
		SIGNALSX^signals := SIGNALSX^hsignals;
		SIGNALS^hsignals := SIGNALSX^hsignals;
	end
}


STATEMENTS -> #init ;
#init {
	local
		statements : NETStatements ;
	do
		statements := new NETStatements() ;
		STATEMENTS^statements := statements ;
	end
}
STATEMENTS -> STATEMENT STATEMENTS #add;
#add {
	local
	do
		call STATEMENTS1^statements.addStatement(STATEMENT^statement) ;
		STATEMENTS^statements := STATEMENTS1^statements ;
	end
}

STATEMENT -> TRANSITION #create;
#create {
	local
		statement : NETStatement ;
	do
		statement := new NETStatement(TRANSITION^transition) ;
		STATEMENT^statement := statement;
		--write statement @ "%N" ;
	end
}

STATEMENT -> OUTPUTS #create;
#create {
	local
		statement : NETStatement ;
	do
		statement := new NETStatement(OUTPUTS^outputs) ;
		STATEMENT^statement := statement;
		--write statement @ "%N" ;
	end
}

STATEMENT -> added #create ;
#create {
	do
		STATEMENT^statement := new NETStatement(added^txt);
		--write "++" @ added^txt @ "%N" ;
	end
}


TRANSITION -> ident arrow ident when #trans TERMSUM OPT_OUTPUTS #create pv ;
#trans {
	local
		termsum : NETTermsSum ;
		affectations : NETAffectations ;
	do
		termsum := new NETTermsSum();
		TERMSUM^htermsum := termsum;
		affectations := new NETAffectations() ;
		OPT_OUTPUTS^haffectations := affectations;
	end
}
#create {
	local
		transition : NETTransition ;
	do
		transition := new NETTransition(ident^txt, ident1^txt, TERMSUM^termsum, OPT_OUTPUTS^affectations) ;
		TRANSITION^transition := transition ;
	end
}

OPT_OUTPUTS -> #trans ;
#trans {
	local
	do
		OPT_OUTPUTS^affectations := nil;
	end
}
OPT_OUTPUTS -> outputs #trans AFFECTATIONS #set ;
#trans {
	local
	do
		AFFECTATIONS^haffectations := OPT_OUTPUTS^haffectations;
	end
}
#set {
	local
	do
		OPT_OUTPUTS^affectations := AFFECTATIONS^affectations;
	end
}

OUTPUTS -> outputs ident #trans AFFECTATIONS #create pv;
#trans {
	local
		affectations : NETAffectations ;
	do
		affectations := new NETAffectations() ;
		AFFECTATIONS^haffectations := affectations;
	end
}
#create {
	local
		mooreOutputs : NETMooreOutputs ;
	do
		mooreOutputs := new NETMooreOutputs(ident^txt, AFFECTATIONS^affectations) ;
		OUTPUTS^outputs := mooreOutputs ;
		--write mooreOutputs @ "%N" ;
	end
}

AFFECTATIONS -> #trans ;
#trans {
	local
	do
		AFFECTATIONS^affectations := AFFECTATIONS^haffectations;
	end
}
AFFECTATIONS -> AFFECTATION #trans AFFECTATIONSX #add ;
#trans {
	local
	do
		AFFECTATIONS^affectations := AFFECTATIONS^haffectations;
		AFFECTATIONSX^haffectations := AFFECTATIONS^haffectations;
	end
}
#add {
	local
	do
		call AFFECTATIONS^haffectations.addAffectation(AFFECTATION^affectation);
	end
}
AFFECTATIONSX -> #trans ;
#trans {
	local
	do
		AFFECTATIONSX^affectations := AFFECTATIONSX^haffectations;
	end
}
AFFECTATIONSX -> virg #trans AFFECTATIONS ;
#trans {
	local
	do
		AFFECTATIONSX^affectations := AFFECTATIONSX^haffectations;
		AFFECTATIONS^haffectations := AFFECTATIONSX^haffectations;
	end
}

AFFECTATION -> SIGNAL egal #trans TERMSUM #create ;
#trans {
	local
		termsum : NETTermsSum;
	do
		termsum := new NETTermsSum();
		TERMSUM^htermsum := termsum;
	end
}
#create {
	local aff : NETAffectation ;
	do
		aff := new NETAffectation(SIGNAL^signal, TERMSUM^termsum);
		AFFECTATION^affectation := aff ;
		--write "affectation " @ TERMSUM^termsum @ "%N" ;
	end
}

-- signal simple ou inversé
SIGNAL_ -> SIGNAL #create;
#create {
	local
		signalOccurence : NETSignalOccurence;
	do
		signalOccurence := new NETSignalOccurence(SIGNAL^signal, false);
		SIGNAL_^signalOccurence := signalOccurence;
		--SIGNAL_^signal := SIGNAL^signal ;
	end
}

SIGNAL_ -> slash SIGNAL #create ;
#create {
	local
		signalOccurence : NETSignalOccurence;
	do
		signalOccurence := new NETSignalOccurence(SIGNAL^signal, true);
		SIGNAL_^signalOccurence := signalOccurence;
		--SIGNAL_^signal := SIGNAL^signal ;
	end
}

TERMSUM -> #trans TERM TERMSUMX ;
#trans {
	local
		term : NETTerm;
	do
		term := new NETTerm();
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
		TERMSUM^htermsum := TERMSUMX^htermsum;
	end
}

TERM -> #trans SIGNAL_ #add TERMX ;
#trans {
	local
	do
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
		TERM^hterm := TERMX^hterm;
	end
}

-- signal (and constants : they are considered as signals and have a specific constructor)
SIGNAL -> ident #init SIGNALX;
#init {
	local
		signal : NETSignal;
	do
		signal := new NETSignal(ident^txt, false);
		SIGNAL^signal := signal;
		SIGNALX^hsignal := signal;
	end
}
SIGNAL -> num2 #set; -- 0b1011
#set {
	local
		signal : NETSignal;
	do
		signal := new NETSignal(num2^txt); -- special constructor for constants
		SIGNAL^signal := signal;
	end
}
SIGNAL -> num10 #set; -- 1234
#set {
	local
		signal : NETSignal;
	do
		signal := new NETSignal(num10^txt); -- special constructor for constants
		SIGNAL^signal := signal;
	end
}
SIGNAL -> num16 #set; -- 0xaf5e
#set {
	local
		signal : NETSignal;
	do
		signal := new NETSignal(num16^txt); -- special constructor for constants
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


S_00, "unexpected symbol ^1 instead of ^2.", 2 ;
S_01, "end expected near ^1.", 1 ;
S_02, "end expected near ^1.", 1 ;

P_02, ".clk, .rst, .set or .ena expected, instead of ^1.", 1 ;

end
