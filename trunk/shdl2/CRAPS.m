syn	lines :		SourceLines
  for PROG ;

inh     hlines :	SourceLines
  for LINES ;

syn	line :		SourceLine
  for LINE ;

inh	hline :		SourceLine
  for INSTR_BODY ;

syn	val :		String
  for REGISTER ;

syn	obj :		Object
  for REGISTER_IMM13 ;

syn	numexpr :	NumExpr	
  for NUM, NUMEXPR, TERM, FACTOR, TERMX, FACTORX ;

inh	hnumexpr :	NumExpr	
  for TERMX, FACTORX ;

inh	vals :		NumExprList 	
  for LISTNUM ;

syn	addrcontent :	AddrContent
  for MEM_REF ;

inh	haddrcontent :	AddrContent
  for MEM_REFX, MEM_REFXX ;


sugar	clr		is	"clr" ;
sugar	mov		is	"mov" ;
sugar	inc		is	"inc" ;
sugar	inccc	is	"inccc" ;
sugar	dec		is	"dec" ;
sugar	deccc	is	"deccc" ;
sugar	set		is	"set" ;
sugar	setq	is	"setq" ;
sugar	cmp		is	"cmp" ;
sugar	tst		is	"tst" ;
sugar	negcc	is	"negcc" ;
sugar	notcc	is	"notcc" ;
sugar	nop		is	"nop" ;
sugar	jmp		is	"jmp" ;
sugar	ret		is	"ret" ;
sugar	push	is	"push" ;
sugar	pop		is	"pop" ;

sugar	call	is	"call" ;
sugar	sethi	is	"sethi" ;

sugar	reti	is	"reti" ;

macro	add		is	"add" ;
macro	addcc	is	"addcc" ;
macro	sub		is	"sub" ;
macro	subcc	is	"subcc" ;
macro	umulcc	is	"umulcc" ;
macro	and		is	"and" ;
macro	andcc	is	"andcc" ;
macro	or		is	"or" ;
macro	orcc	is	"orcc" ;
macro	xorcc	is	"xorcc" ;
macro	xor		is	"xor" ;
macro	xnorcc	is	"xnorcc" ;
macro	xnor	is	"xnor" ;
macro	sll		is	"sll" ;
macro	srl		is	"srl" ;
macro	jmpl	is	"jmpl" ;
term	codeop3	is	"{add}|{addcc}|{sub}|{subcc}|{umulcc}|{and}|{andcc}|{or}|{orcc}|{xor}|{xorcc}|{xnor}|{xnorcc}|{sll}|{srl}|{jmpl}" ;

macro	ld		is	"ld" ;
macro	ldub	is	"ldub" ;
term	ld_ldub	is	"{ld}|{ldub}" ;
macro	st		is	"st" ;
macro	stb		is	"stb" ;
term	st_stb	is	"{st}|{stb}" ;

macro	ba		is	"ba" ;
macro	be		is	"be" ;
macro	beq		is	"beq" ;
macro	bz		is	"bz" ;
macro	bne		is	"bne" ;
macro	bnz		is	"bnz" ;
macro	bneg	is	"bneg" ;
macro	bn		is	"bn" ;
macro	bpos	is	"bpos" ;
macro	bnn		is	"bnn" ;
macro	bcs		is	"bcs" ;
macro	blu		is	"blu" ;
macro	bcc		is	"bcc" ;
macro	bgeu	is	"bgeu" ;
macro	bvs		is	"bvs" ;
macro	bvc		is	"bvc" ;
macro	bg		is	"bg" ;
macro	bgt		is	"bgt" ;
macro	bge		is	"bge" ;
macro	bl		is	"bl" ;
macro	blt		is	"blt" ;
macro	ble		is	"ble" ;
macro	bgu		is	"bgu" ;
macro	bleu	is	"bleu" ;
term	branch	is	"{ba}|{be}|{beq}|{bz}|{bne}|{bnz}|{bneg}|{bn}|{bpos}|{bnn}|{bcs}|{blu}|{bcc}|{bgeu}|{bvs}|{bvc}|{bg}|{bgt}|{bge}|{bl}|{blt}|{ble}|{bgu}|{bleu}" ;

sugar	org		is	".org" ;
sugar	equ		is	".equ" ;
sugar	global_	is	".global" ;
sugar	byte	is	".byte" ;
sugar	word	is	".word" ;

sugar	r		is	"%r" ;
sugar	fp		is	"%fp" ;
sugar	sp		is	"%sp" ;
sugar	pc		is	"%pc" ;

sugar	equals	is	"\=" ;
sugar	semicol	is	":" ;
sugar	virg	is	"," ;
sugar	lbra	is	"\[" ;
sugar	rbra	is	"\]" ;
sugar	lpar	is	"\(" ;
sugar	rpar	is	"\)" ;

macro	plus	is	"\+" ;
macro	minus	is	"\-" ;
macro	mult	is	"\*" ;
macro	div		is	"\/" ;
term	plus_minus	is	"{plus}|{minus}" ;
term	mult_div	is	"{mult}|{div}" ;

space   comm	is      "//.*$" ; -- commentaires elimines
space	blank	is	"[ ]+" ; -- blancs
space	rc		is	"[\n]+" ; -- blancs
space	lfs		is	"[\n\r]+" ; -- blancs
space	tabs	is	"[\t]+" ; -- blancs
term	ident	is	"[A-Za-z_]+[A-Za-z_0-9]*" ;

macro	digit2	is	"[0-1]" ;
term	num2	is	"0b{digit2}+" ;
macro	digit10	is	"[0-9]" ;
term	num10	is	"{digit10}+" ;
macro	digit16	is	"[0-9a-fA-F]" ;
term	num16	is	"0x{digit16}+" ;

PROG -> #init LINES ;
#init {
        local
                lines : SourceLines ;
        do
                lines := new SourceLines();
                LINES^hlines := lines;
                PROG^lines := lines;
        end
}

LINES -> #print ;
#print {
	local
	do
		write LINES^hlines @ "%N" ;
	end
}
LINES -> #trans LINE #add LINES ;
#trans {
	local
	do
		LINES1^hlines := LINES^hlines;
	end
}
#add {
	local
	do
		call LINES^hlines.add(LINE^line);
	end
}

LINE -> #create INSTR_BODY ;
#create {
	local
		line : SourceLine;
	do
		line := new SourceLine();
		LINE^line := line;
		call line.setLineno(LINE^scanner.getBeginLine());
		INSTR_BODY^hline := line ;
	end
}
LINE -> ident semicol #create INSTR_BODY ;
#create {
	local
		line : SourceLine;
	do
		line := new SourceLine();
		LINE^line := line;
		call line.setLineno(LINE^scanner.getBeginLine());
		call line.setLabel(ident^txt);
		INSTR_BODY^hline := line ;
	end
}

INSTR_BODY -> clr REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthClr(REGISTER^val));
	end
}
INSTR_BODY -> mov REGISTER virg REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthMov(REGISTER^val, REGISTER1^val));
	end
}
INSTR_BODY -> inc REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthInc(REGISTER^val));
	end
}
INSTR_BODY -> inccc REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthInccc(REGISTER^val));
	end
}
INSTR_BODY -> dec REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthDec(REGISTER^val));
	end
}
INSTR_BODY -> deccc REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthDeccc(REGISTER^val));
	end
}
INSTR_BODY -> set NUMEXPR virg REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthSet(NUMEXPR^numexpr, REGISTER^val));
	end
}
INSTR_BODY -> setq NUMEXPR virg REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthSetq(NUMEXPR^numexpr, REGISTER^val));
	end
}
INSTR_BODY -> cmp REGISTER virg REGISTER_IMM13 #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthCmp(REGISTER^val, REGISTER_IMM13^obj));
	end
}
INSTR_BODY -> tst REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthTst(REGISTER^val));
	end
}
INSTR_BODY -> negcc REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthNegcc(REGISTER^val));
	end
}
INSTR_BODY -> notcc REGISTER virg REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthNotcc(REGISTER^val, REGISTER^val));
	end
}
INSTR_BODY -> nop #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthNop());
	end
}
INSTR_BODY -> jmp REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthJmp(REGISTER^val));
	end
}
INSTR_BODY -> ret #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthRet());
	end
}
INSTR_BODY -> push REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthPush(REGISTER^val));
	end
}
INSTR_BODY -> pop REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthPop(REGISTER^val));
	end
}

INSTR_BODY -> sethi NUMEXPR virg REGISTER #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsInstrSetHi(NUMEXPR^numexpr, REGISTER^val));
	end
}
INSTR_BODY -> call NUMEXPR #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsSynthCall(NUMEXPR^numexpr));
	end
}
INSTR_BODY -> reti #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsInstrReti());
	end
}
INSTR_BODY -> branch NUMEXPR #create ;
#create {
	local
	do
		call INSTR_BODY^hline.setInstr(new CrapsInstrBr(branch^txt, NUMEXPR^numexpr));
	end
}
INSTR_BODY -> codeop3 REGISTER virg REGISTER_IMM13 virg REGISTER #create ;
#create {
	local
		instr : CrapsInstrArithLog3;
	do
		instr := new CrapsInstrArithLog3(codeop3^txt, REGISTER^val, REGISTER_IMM13^obj, REGISTER1^val);
		call INSTR_BODY^hline.setInstr(instr);
	end
}

INSTR_BODY -> ld_ldub MEM_REF virg REGISTER #create ;
#create {
	local
		instr : CrapsInstrLd;
	do
		instr := new CrapsInstrLd(ld_ldub^txt, MEM_REF^addrcontent, REGISTER^val);
		call INSTR_BODY^hline.setInstr(instr);
	end
}

INSTR_BODY -> st_stb REGISTER virg MEM_REF #create ;
#create {
	local
		instr : CrapsInstrSt;
	do
		instr := new CrapsInstrSt(st_stb^txt, REGISTER^val, MEM_REF^addrcontent);
		call INSTR_BODY^hline.setInstr(instr);
	end
}

MEM_REF -> lbra #create_and_trans MEM_REFX rbra ;
#create_and_trans {
	local
		addr_content : AddrContent;
	do
		addr_content := new AddrContent();
		MEM_REF^addrcontent := addr_content;
		MEM_REFX^haddrcontent := addr_content;
	end
}

MEM_REFX -> NUMEXPR #create ;
#create {
	local
	do
		call MEM_REFX^haddrcontent.setRs2_or_disp(NUMEXPR^numexpr);
	end
}
MEM_REFX -> REGISTER #trans_and_update MEM_REFXX ;
#trans_and_update {
	local
	do
		MEM_REFXX^haddrcontent := MEM_REFX^haddrcontent;
		call MEM_REFX^haddrcontent.setRs1(REGISTER^val);
	end
}
MEM_REFXX -> ;
MEM_REFXX -> plus_minus REGISTER #update ;
#update {
	local
	do
		--je n'arrive pas a imposer +
		--if (plus_minus^txt /= "+") then
			--error(P_00, "plus");
		--end
		call MEM_REFXX^haddrcontent.setRs2_or_disp(REGISTER^val);
	end
}
MEM_REFXX -> NUMEXPR #update ;
#update {
	local
	do
		call MEM_REFXX^haddrcontent.setRs2_or_disp(NUMEXPR^numexpr);
	end
}


INSTR_BODY -> org NUMEXPR #create ;
#create {
	local
		instr : CrapsDirecOrg;
	do
		instr := new CrapsDirecOrg(NUMEXPR^numexpr);
		call INSTR_BODY^hline.setInstr(instr);
	end
}
INSTR_BODY -> equ NUMEXPR #create ;
#create {
	local
		instr : CrapsDirecEqu;
	do
		instr := new CrapsDirecEqu(NUMEXPR^numexpr);
		call INSTR_BODY^hline.setInstr(instr);
	end
}
INSTR_BODY -> global_ ident #create ;
#create {
	local
		instr : CrapsDirecGlobal;
	do
		instr := new CrapsDirecGlobal(ident^txt);
		call INSTR_BODY^hline.setInstr(instr);
	end
}
LINE -> ident equals NUMEXPR #create;
#create {
	local
		line : SourceLine;
		instr : CrapsDirecEqu;
	do
		line := new SourceLine();
		LINE^line := line;
		call line.setLineno(LINE^scanner.getBeginLine());
		call line.setLabel(ident^txt);
		instr := new CrapsDirecEqu(NUMEXPR^numexpr);
		call line.setInstr(instr);
	end
}

INSTR_BODY -> byte NUMEXPR #create LISTNUM ;
#create {
	local
		instr : CrapsDirecByte;
		list : NumExprList;
	do
		list := new NumExprList();
		LISTNUM^vals := list;
		call list.add(NUMEXPR^numexpr);
		instr := new CrapsDirecByte(list);
		call INSTR_BODY^hline.setInstr(instr);
	end
}
INSTR_BODY -> word NUMEXPR #create LISTNUM ;
#create {
	local
		instr : CrapsDirecWord;
		list : NumExprList;
	do
		list := new NumExprList();
		LISTNUM^vals := list;
		call list.add(NUMEXPR^numexpr);
		instr := new CrapsDirecWord(list);
		call INSTR_BODY^hline.setInstr(instr);
	end
}
LISTNUM -> ;
LISTNUM -> virg NUMEXPR #add LISTNUM ;
#add {
	local
	do
		LISTNUM1^vals := LISTNUM^vals;
		call LISTNUM^vals.add(NUMEXPR^numexpr);
	end
}


REGISTER -> r num10 #create ;
#create {
	local
	do
		REGISTER^val := "%r" @ num10^txt;
	end
}
REGISTER -> fp #create ;
#create {
	local
	do
		REGISTER^val := "%r27";
	end
}
REGISTER -> sp #create ;
#create {
	local
	do
		REGISTER^val := "%r29";
	end
}
REGISTER -> pc #create ;
#create {
	local
	do
		REGISTER^val := "%r30";
	end
}

REGISTER_IMM13 -> REGISTER #create ;
#create {
	local
	do
		REGISTER_IMM13^obj := REGISTER^val;
	end
}
REGISTER_IMM13 -> NUMEXPR #create ;
#create {
	local
	do
		REGISTER_IMM13^obj := NUMEXPR^numexpr;
	end
}


NUM -> num10 #create ;
#create {
	local
	do
		NUM^numexpr := new NumExprInt(num10^txt, 10);
	end
}
NUM -> num2 #create ;
#create {
	local
	do
		NUM^numexpr := new NumExprInt(num2^txt, 2);
	end
}
NUM -> num16 #create ;
#create {
	local
	do
		NUM^numexpr := new NumExprInt(num16^txt, 16);
	end
}


NUMEXPR -> TERM #trans TERMX #create ;
#trans {
	local
	do
		TERMX^hnumexpr := TERM^numexpr ;
	end
}
#create {
	local
	do
		NUMEXPR^numexpr := TERMX^numexpr ;
	end
}

TERM -> FACTOR #trans FACTORX #create ;
#trans {
	local
	do
		FACTORX^hnumexpr := FACTOR^numexpr ;
	end
}
#create {
	local
	do
		TERM^numexpr := FACTORX^numexpr ;
	end
}

TERMX -> plus_minus TERM #trans TERMX #create ;
#trans {
	local
	do
		TERMX1^hnumexpr := new NumExprOp2(plus_minus^txt, TERMX^hnumexpr, TERM^numexpr) ;
	end
}
#create {
	local
	do
		TERMX^numexpr := TERMX1^numexpr ;
	end
}
TERMX -> #create ;
#create {
	local
	do
		TERMX^numexpr := TERMX^hnumexpr ;
	end
}

FACTOR -> lpar NUMEXPR rpar #create ;
#create {
	local
	do
		FACTOR^numexpr := NUMEXPR^numexpr ;
	end
}
FACTOR -> ident #create ;
#create {
	local
	do
		FACTOR^numexpr := new NumExprVar(ident^txt) ;
	end
}
FACTOR -> NUM #create ;
#create {
	local
	do
		FACTOR^numexpr := NUM^numexpr ;
	end
}
FACTOR -> plus_minus NUM #create ;
#create {
	local
	do
		FACTOR^numexpr := new NumExprOp1(plus_minus^txt, NUM^numexpr) ;
	end
}

FACTORX -> mult_div FACTOR #trans FACTORX #create ;
#trans {
	local
	do
		FACTORX1^hnumexpr := new NumExprOp2(mult_div^txt, FACTORX^hnumexpr, FACTOR^numexpr) ;
	end
}
#create {
	local
	do
		FACTORX^numexpr := FACTORX1^numexpr ;
	end
}
FACTORX -> #create ;
#create {
	local
	do
		FACTORX^numexpr := FACTORX^hnumexpr ;
	end
}


S_00, "unexpected symbol ^1 instead of ^2.", 2 ;
S_01, "end expected near ^1.", 1 ;
S_02, "end expected near ^1.", 1 ;
P_00, "'+' expected instead of '-'.", 0 ;

end

