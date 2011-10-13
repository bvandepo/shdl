/*
 * @(#)BarCode3of9.java	1.00 97/Nov Umberto Marzo  umarzo@eniware.it
 *
 * Copyright (c) 1997 Umberto Marzo All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT.
 * THE AUTHOR SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
	Last change:  U    17 Dec 97    5:58 pm
 */

package org.jcb.tools;

import java.awt.*;
import javax.swing.*;

/**
 * A class that produces a Barcode component.
 *
 * @version 	1.00 97/Nov
 * @author 	Umberto Marzo
 * @author      umarzo@eniware.it
 */

public class BarCode3of9 extends Canvas 
{

// CODE 39 CHARACTERS

private static String alphabet3of9="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*";

private static String coded3of9Char[]=
{
    /* 0 */ "000110100", /* 1 */ "100100001", /* 2 */ "001100001", /* 3 */ "101100000",
    /* 4 */ "000110001", /* 5 */ "100110000", /* 6 */ "001110000", /* 7 */ "000100101",
    /* 8 */ "100100100", /* 9 */ "001100100", /* A */ "100001001", /* B */ "001001001",
    /* C */ "101001000", /* D */ "000011001", /* E */ "100011000", /* F */ "001011000",
    /* G */ "000001101", /* H */ "100001100", /* I */ "001001100", /* J */ "000011100",
    /* K */ "100000011", /* L */ "001000011", /* M */ "101000010", /* N */ "000010011",
    /* O */ "100010010", /* P */ "001010010", /* Q */ "000000111", /* R */ "100000110",
    /* S */ "001000110", /* T */ "000010110", /* U */ "110000001", /* V */ "011000001",
    /* W */ "111000000", /* X */ "010010001", /* Y */ "110010000", /* Z */ "011010000",
    /* - */ "010000101", /* . */ "110000100", /*SPACE*/"011000100",/* $ */ "010101000",
    /* / */ "010100010", /* + */ "010001010", /* % */ "000101010", /* * */ "010010100" 
};

   /**
     * Costant for variant of the code.
     * @see #CODE3OF9CHK
     * @see #CODE3OF9
     * @see #setStyle
     * @see #getStyle
     */

   public final static int CODE3OF9=0, CODE3OF9CHK=1;

   /**
     * Costant for size of the narrowest bar.
     * @see #SMALL
     * @see #MEDIUM
     * @see #LARGE
     * @see #setDimension
     * @see #getDimension
     */

   public final static int SMALL=1,MEDIUM=2,LARGE=3;

   /**
     * Costant for text alignment.
     * @see #BASELINE
     * @see #MIDDLELINE
     * @see #TOPLINE
     * @see #setTextAlign
     * @see #getTextAlign
     */

   public final static int BASELINE=0,MIDDLELINE=1,TOPLINE=2;

   // default values

   private final static int DEFWIDTH=50;
   private final static int DEFHEIGHT=25;
   private final static boolean DEFTEXTINS=true;
   private final static int DEFSIZE=SMALL;
   private final static Color DEFBACKCOLOR=Color.white;
   private final static Color DEFFORECOLOR=Color.black;
   private final static Font DEFFONT=new Font("Courier",0,12);

   // private members

   private double wideToNarrowRatio=3;
   private String intercharacterGap="0";
   private double marginWidth,marginHeight;
   private double labelLength;
   private double labelHeight;
   private String stringToEncode="";
   private String filledStringToEncode="";
   private String encodedString="";
   private int narrowestDim=DEFSIZE;
   private boolean textInside=DEFTEXTINS;
   private int style=CODE3OF9;
   private int initialWidth=DEFWIDTH;
   private int initialHeight=DEFHEIGHT;
   private Color backColor=DEFBACKCOLOR;
   private Color foreColor=DEFFORECOLOR;
   private Font font=DEFFONT;
   private int textAlign=TOPLINE;

   // Constructors

   /**
     * Constructs a Barcode object with an empty string and the following defaults:<br>
     * size 100x50;<br>
     * label inside;<br>
     * small rendering;<br>
     * black on white color;<br>
     * Courier,12 font;
     */

   public BarCode3of9() 
    { 
	this("3OF9", DEFWIDTH, DEFHEIGHT, DEFSIZE, CODE3OF9,DEFTEXTINS, DEFBACKCOLOR, DEFFORECOLOR, DEFFONT, TOPLINE);
    }

    private BarCode3of9(String str, int iniWidth, int iniHeight,int dimens, int Styl, boolean textIns,Color backColor, Color foreColor,Font font, int textAlign ) 
    {
	
	this.stringToEncode=str;
	this.narrowestDim=dimens;
	this.initialWidth=iniWidth;
	this.initialHeight=iniHeight;
	this.textInside=textIns;
	this.style=Styl;
	this.backColor=backColor;
	this.foreColor=foreColor;
	this.font=font;
	this.textAlign=textAlign;
	
	Encode();
    }
    
    /**
     * Returns the mininimum size of this component.
     * @see #getPreferredSize
     */
    
    public Dimension getMinimumSize() 
    {
      	return minimumSize();
    }
    
    /**
     * Returns the minimum size of this component.
     * @see #preferredSize
     */
    
    public Dimension minimumSize() 
    { 
	Dimension minSize = new Dimension(initialWidth,initialHeight); return minSize; 
    }
    
    /** 
     * Returns the preferred size of this component.
     * @see #getMinimumSize
     */
    
    public Dimension getPreferredSize() 
    { 
	return preferredSize();  
    }
    
    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize().
     */
    public Dimension preferredSize() 
    { 
	return minimumSize(); 
    }
    
    /**
     * Resizes the Component to the specified width and height.
     * @param width the width of the component
     * @param height the height of the component
     */
    
    public void resize(int width, int height)  
    { 
	initialWidth=width; initialHeight=height;
	super.resize( width, height); 
	repaint(); 
    }

   /**00
     * Resizes the Component to the specified dimension.
     * @param d the dimension of the component
     */

   public void resize(Dimension d)  
    { 
	initialWidth=d.width; initialHeight=d.height;
	super.resize(d.width,d.height); 
	repaint(); 
    }

   /**
     * Returns the parameter String of this Component.
     */

   protected String paramString() 
    {
        String str = filledStringToEncode + "," + initialWidth + "x" + initialHeight;
	return str;
    }
    
    /**
     * Returns the String representation of this Component's values.
     */
    
    public String toString() 
    { 	
	return getClass().getName() + "[" + paramString() + "]";  
    }
    
    
    /**
     * Return the minimum dimension needed to successfully display a code of string str.
     * Use it to resize the component using method  resize(Dimension d)
     * @param str the sting to encode
     * @return the minimum dimension
     */
    
    public Dimension requestedMinimumSize(String str) 
    {
	int width=str.length()*16*this.narrowestDim+31*this.narrowestDim;
	if ( this.style==CODE3OF9CHK ) width+=16*this.narrowestDim;
	//int height=Math.max((int)(.15*width),35);
	int height=50;
	return new Dimension(width,height);
	
    }
     
    public Dimension requestedMinimumSize(String str, int height) 
    {
	int width=str.length()*16*this.narrowestDim+31*this.narrowestDim;
	if ( this.style==CODE3OF9CHK ) width+=16*this.narrowestDim;
	return new Dimension(width,height);
	
    }
   
    
    /**
     * Sets the string encoded in the Barcode.
     * @param str the string to encode
     * @exception IllegalArgumentException If an improper string was given.
     * @see #getString
     */
    
    public void setString(String str) throws IllegalArgumentException 
    {
	stringToEncode=str; 
	stringValidate(); 
	Encode(); 
    }
    
    /**
     * Gets the string encoded in the Barcode.
     * @return the encoded string
     * @see #setString
     */

    public String getString() 
    { 
	return stringToEncode;   
    }
    
   /**
     * Sets the dimension of the narrowest bar. Values allowed are SMALL, MEDIUM and LARGE
     * @param dim the dimension
     * @see #SMALL
     * @see #MEDIUM
     * @see #LARGE
     * @see #getDimension
     */
    
   public void setDimension(int dim) 
    {
	switch (dim) 
	    {
	    case SMALL:
	    case MEDIUM:
	    case LARGE:
		narrowestDim=dim;
		repaint();
		return;
	    }
	narrowestDim=DEFSIZE;
	repaint();
    }
    
    /**
     * Gets the dimension of the narrowest bar.
     * @return the dim of the bar
     * @see #SMALL
     * @see #MEDIUM
     * @see #LARGE
     * @see #setDimension
     */
    
    public int getDimension() 
    { 
	return narrowestDim; 
    }
    
    /**
     * Call this method with true (or false) as argument if you want (or not) a text label inside.
     * @see #isTextInside
     */

    public void setTextInside(boolean bool) 
    {  
	textInside=bool; 
	repaint();   
    }

   /**
     * Return true if a label will be shown inside.
     * @see #setTextInside
     */

    public boolean isTextInside() 
    { 
	return textInside;   
    }
    
    /**
     * Set the style of the code to one of the two available style.
     * @see #CODE3OF9
     * @see #CODE3OF9CHK
     * @see #getStyle
     */
    
    public void setStyle(int styl) 
    {
	switch (styl) 
	    {
	    case CODE3OF9:
	    case CODE3OF9CHK:
		style=styl;
		Encode();
		return;
	    }
	style=CODE3OF9;
	
	Encode();
    }
    
    /**
     * return the style of the barcode currently used.
     * @see #CODE3OF9
     * @see #CODE3OF9CHK
     * @see #setStyle
     */
    
    public int  getStyle() 
    { 
	return style;  
    }
    
    /**
     * set the color of the background to the specified color.
     * @param c the color to be used
     * @see #getBackgroundColor
     * @see #setForegroundColor
     * @see #getForegroundColor
     */

    public void setBackground(Color c) 
    { 
	backColor=c;  
	repaint(); 
    }

   /**
     * return the color used for the background.
     * @return the color used
     * @see #setBackgroundColor
     * @see #setForegroundColor
     * @see #getForegroundColor
     */

    public Color getBackground() 
    { 
	return backColor; 
    }

    /**
     * set the color of the foreground to the specified color.
     * @param c the color to be used
     * @see #getBackgroundColor
     * @see #setBackgroundColor
     * @see #getForegroundColor
     */

    public void setForeground(Color c) 
    { 
	foreColor=c;  
	repaint(); 
    }

    /**
     * return the color used for the foreground.
     * @return the color used
     * @see #setBackgroundColor
     * @see #getBackgroundColor
     * @see #setForegroundColor
     */

    public Color getForeground() 
    { 
	return foreColor; 
    }
    
    /**
     * Sets the font of the component.
     * @param fnt the font
     * @see #getFont
     */

    public void setFont(Font fnt) 
    { 
	font=fnt; 
	repaint(); 
    }
    
   /**
     * Gets the font of the component.
     * @see #setFont
     */
    
    public Font getFont() 
    { 
	return font; 
    }
    
    /**
     * Set the alignment of text inside the bar code. Three option are available.
     * @see #BASELINE
     * @see #MIDDLELINE
     * @see #TOPLINE
     * @see #getTextAlign
     */
    
    public void setTextAlign(int align) 
    {
	switch (align) 
	    {
	    case BASELINE:
	    case MIDDLELINE:
	    case TOPLINE:
		textAlign=align;
		repaint();
		return;
	    }
	textAlign=TOPLINE;
	repaint();
	
    }
    
    /**
     * Set the alignment of text inside the bar code. Three option are available.
     * @see #BASELINE
     * @see #MIDDLELINE
     * @see #TOPLINE
     * @see #setTextAlign
     */
    
    public int  getTextAlign() 
    { 
	return textAlign; 
    }
    
    /**
     * Paints the component.
     * @param g the specified Graphics window
     */
    
    public synchronized void paint (Graphics g) 
    {
	
	int width=size().width;
	int height=size().height;
	
        g.setFont(font);
        FontMetrics fm=g.getFontMetrics(font);
	int fmAscent=fm.getAscent();
	
        // calcolo la lunghezza della etichetta codificata
	
        labelLength= filledStringToEncode.length()*16*narrowestDim;
	
        // le dimensioni dei margini e l'altezza della etichetta
	
        marginWidth=(width-labelLength)/2; marginHeight=(height-labelLength)/2;

        if ( textInside ) 
	    {
		if ( textAlign==BASELINE )   labelHeight=height;
		if ( textAlign==MIDDLELINE ) labelHeight=height-(int)(fmAscent/2);
		if ( textAlign==TOPLINE )    labelHeight=height-fmAscent;
		
	    } 
	else 
	    { 
		labelHeight=height; 
	    }
	
        g.setColor(backColor);
        g.fillRect(0,0,width,height);
	
        int length=encodedString.length();
	
	int x0=0,y0=0,x=0,y=0;
        int wid=0;

        x0=(int) marginWidth; y0=0; x=0;  y=0;

        for ( int i=0;i<length ;i++ ) 
	    {
		if (i%2==0)  g.setColor(foreColor);  else  g.setColor(backColor);
		if ( Character.digit(encodedString.charAt(i),10)==1 ) wid=(int)(wideToNarrowRatio*narrowestDim);
		else wid=(int)narrowestDim;
		
		g.fillRect(x+x0,y+y0,wid,(int)labelHeight);
		x+=wid;
	    }
	
	
	if ( textInside ) 
	    {
		
		int k=fm.stringWidth(filledStringToEncode);
		int h=size().height;
		int mlk2=(int)(marginWidth+labelLength/2-k/2);
		
		g.setColor(backColor);
		g.fillRect(mlk2+35,h-fmAscent,k+35,fmAscent);
		g.setColor(foreColor);
		g.drawString(filledStringToEncode,mlk2+35,h);
		
	    }
	
    }
    
    
    private void stringValidate() throws IllegalArgumentException 
    {
	
	//valido la stringa secondo il codice
	
	int len=stringToEncode.length();
	for ( int i=0;i<len ;i++ ) 
	    {
		if (alphabet3of9.indexOf(stringToEncode.charAt(i))==-1 || stringToEncode.indexOf("*")!=-1)
		    throw new IllegalArgumentException("only digits and upper case letter for 3of9");
	    }
	
    }
    
    
    private void Encode()  
    {
	
	filledStringToEncode=new String(stringToEncode);
	
	// aggiungo il check char
	
	if ( style==CODE3OF9CHK )  
	    {
		int leng=stringToEncode.length();
		int sum=0;
		for ( int i=0;i<leng ;i++ ) 
		    {
			sum+=alphabet3of9.indexOf(stringToEncode.charAt(i));
		    }
		sum=sum%43;
		filledStringToEncode+=alphabet3of9.charAt(sum);
	    }
	
	// ci aggiungo i caratteri di start e stop
	filledStringToEncode="*"+filledStringToEncode+"*";
	
	//codifico la stringa secondo il codice
	encodedString="";
	int length=filledStringToEncode.length();
	for ( int i=0;i<length-1 ;i++ ) 
	    {
		encodedString+=coded3of9Char[alphabet3of9.indexOf(filledStringToEncode.charAt(i))];
		encodedString+=intercharacterGap;
	    }
	encodedString+=coded3of9Char[alphabet3of9.indexOf(filledStringToEncode.charAt(length-1))];
	
	repaint();
	
    }  // end Encode3of9()

    
    /*					        *	
    *  La sola gestione degli eventi necessaria *
    *  a mostrare la about frame                *
    */
    
    public boolean handleEvent(Event evt) 
    {
    	/*
	  if (evt.id==Event.MOUSE_DOWN) {
	  //showAboutFrame();
	  return true;
	  }
        */
    	return super.handleEvent(evt);
    }
    
    
    
    /*					      *	
     * Mostra una semplice finestra di about   *
     *                                         */
    
    /*
      
   final void showAboutFrame() {
        AboutFrame description = new AboutFrame();
            
        description.tell("\t\t"+"BarCode3of9"+"\n");
        description.tell("\n"+"from a full set of classes that implements the most common"+"\n");
	description.tell("bar code on the market"+"\n\n");
        description.tell("\t"+"developed by Umberto Marzo"+"\n\n");
	description.tell("Comments at:"+"\n"+"umarzo@eniware.it"+"\n\n");
        description.tell("The full set of classes, updates, documentation at:"+"\n");
        description.tell("www.geocities.com/SiliconValley/Pines/4619"+"\n\n");
	                
        description.show();
   }
   */
   
    public void inWhite() 
    {
	stringToEncode = "";
	filledStringToEncode = "";
	encodedString = "";
	repaint();
    }
    
    public static void main(String[] args) 
    {
	BarCode3of9 barcode = new BarCode3of9();
	barcode.setString("210002336");
	barcode.resize(barcode.requestedMinimumSize("210002336", 50));
	
	JFrame frame = new JFrame("barcode test");
	JPanel panel = new JPanel();
	panel.add(barcode);
	
	frame.setContentPane(panel);
	//frame.setSize(200, 200);
	frame.pack();
	frame.setVisible(true);
	
    }
    
    
}


