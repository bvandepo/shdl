
package org.jcb.tools;

import org.jcb.tools.*;

import java.util.*;
import java.io.*;
import java.text.*;


public class FuzzyQuantity extends Quantity implements Serializable {

	public static final long serialVersionUID = 5942656L;

	protected double d1;		// kerner lower bound
	protected double d2;		// kernel upper bound
	protected double dt1;		// left spread
	protected double dt2;		// right spread
	protected double h;		// global uncertainty degree

	private static DecimalFormat decimalFormat2d = new DecimalFormat("###0.00", new DecimalFormatSymbols(Locale.US));
	private static DecimalFormat decimalFormat1d = new DecimalFormat("###0.0", new DecimalFormatSymbols(Locale.US));
	private static DecimalFormat decimalFormat0d = new DecimalFormat("###0", new DecimalFormatSymbols(Locale.US));
	
	
	// ex: FuzzyQuantity(17.3, 0.1) = 17.3 +/- 10%
	public FuzzyQuantity(double f, double imprecision) {
		d1 = f - f * imprecision / 2;
		d2 = f + f * imprecision / 2;
		dt1 = f * imprecision / 2;
		dt2 = f * imprecision / 2;
		h = 0.;
	}

	public FuzzyQuantity(String str, double imprecision) {
		double f = Float.parseFloat(str);
		double delta = f * imprecision / 2;
		d1 = f - delta;
		d2 = f + delta;
		dt1 = delta;
		dt2 = delta;
		h = 0.;
	}
	
	public FuzzyQuantity(double d1, double d2, double dt1, double dt2, double h) {
		this.d1 = d1;
		this.d2 = d2;
		this.dt1 = dt1;
		this.dt2 = dt2;
		this.h = h;
	}
	
	public FuzzyQuantity(double d1, double d2, double dt1, double dt2) {
		this.d1 = d1;
		this.d2 = d2;
		this.dt1 = dt1;
		this.dt2 = dt2;
		this.h = 0.;
	}

	public final static FuzzyQuantity ZERO =
		new FuzzyQuantity(0., 0., 0., 0., 0.);

	public final static FuzzyQuantity UNKNOWN =
		new FuzzyQuantity(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0., 0., 0.);

	public final static FuzzyQuantity POSITIVE_UNKNOWN =
		new FuzzyQuantity(0., Double.POSITIVE_INFINITY, 0., 0., 0.);

	public final static FuzzyQuantity INF100 =
		new FuzzyQuantity(0., 100., 0., 0., 0.);

	public String toString() {
		//return ("(" + d1 + "," + d2 + "," + dt1 + "," + dt2 + ") " + format());
		return (format());
	}

	// Two fuzzy quantities are considered equals if their string
	// representations are equals

	public boolean equals(Object obj) {
		if (!(obj instanceof FuzzyQuantity)) return false;
		FuzzyQuantity f = (FuzzyQuantity) obj;
		return (format().equals(f.format()));
	}

/*
	// When serializing, nd is replaced with its name. On reconstruction, this name
	// will be used to get the right NutriData from a pool (see getNutriData)

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(format());
		oos.flush();
	}

	private Object readObject(ObjectInputStream ois) throws IOException {
		try {
			String str = (String) ois.readObject();
			FuzzyQuantity dummy = new FuzzyQuantity(0., 0.);
			FuzzyQuantity f = (FuzzyQuantity) dummy.parseObject(str);
			d1 = f.d1;
			d2 = f.d2;
			dt1 = f.dt1;
			dt2 = f.dt2;
			h = f.h;
			return this;
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new IOException();
		}
	}
*/

	public double middleValue() {
		return ((d1 + d2) / 2);
	}

	public double getCoreInf() {
		return d1;
	}

	public double getCoreSup() {
		return d2;
	}

	public double getSpreadInf() {
		return dt1;
	}

	public double getSpreadSup() {
		return dt2;
	}

	public double getSupportInf() {
		return d1 - dt1;
	}

	public double getSupportSup() {
		return d2 + dt2;
	}


	// choose a formatter adapted to the absolute value range
	private static DecimalFormat chooseFormater(double value) {
		DecimalFormat df = null;
		if (value >= 1000) df = decimalFormat0d;
		else if (value >= 100) df = decimalFormat1d;
		else  df = decimalFormat2d;
		return df;
	}

	
	// x : [x,x,0,0]
	// ~x : core in [x - 5%, x + 5%], support in [x - 10%, x + 10%]
	// ~~x : core in [x - 10%, x + 10%], support in [x - 20%, x + 20%]
	// ~x-y : core in [x, y], support in [x - 10%, x + 10%]
	// >x : support in [x, infinity(
	// <x : support in )-infinity, x]
	// ? : support = )-infinity, infinity(

	private static double positiveInfinity = Double.POSITIVE_INFINITY;
	//private static double negativeInfinity = Double.NEGATIVE_INFINITY;
	private static double negativeInfinity = 0;
	public static void setPositiveInfinity(double val) {
		positiveInfinity = val;
	}
	public static void setNegativeInfinity(double val) {
		negativeInfinity = val;
	}

	public String format() {
		double mid = middleValue();
		double s1 = d1 - dt1;
		double s2 = d2 + dt2;
		s1 = Math.max(s1, 0.);
		s2 = Math.max(s2, 0.);

		DecimalFormat df = chooseFormater(Math.min(Math.abs(d1), Math.abs(d2)));

		if (s2 >= positiveInfinity && s1 <= negativeInfinity) return "?";
		if (s2 >= positiveInfinity) {
			if (s1 < positiveInfinity)
				return ">" + df.format(s1);
			else
				return df.format(positiveInfinity);
		}
		if (s1 <= negativeInfinity) {
			if (s2 > negativeInfinity)
				return "<" + df.format(s2);
			else
				return df.format(negativeInfinity);
		}
		if ((d2 - d1 <= 0.041 * mid) && (s2 - s1 <= 0.11 * mid)) return "" + df.format(mid);
		if ((d2 - d1 <= 0.11 * mid) && (s2 - s1 <= 0.21 * mid)) return "~" + df.format(mid);
		if ((d2 - d1 <= 0.21 * mid) && (s2 - s1 <= 0.41 * mid)) return "~~" + df.format(mid);
		return "~" + df.format(d1) + "-" + df.format(d2);
	}

	public static FuzzyQuantity parseFuzzyQuantity(String str) throws ParseException {
		try {
			if (str.equals("?")) {
				return FuzzyQuantity.UNKNOWN;
			} else if (str.charAt(0) == '~') {
				if (str.charAt(1) == '~') {
					// ex: ~~125
					double q = decimalFormat2d.parse(str.substring(2).trim()).doubleValue();
					double d1 = q * 0.90;
					double d2 = q * 1.10;
					double s1 = q * 0.80;
					double s2 = q * 1.20;
					return new FuzzyQuantity(d1, d2, d1 - s1, s2 - d2, 0.);
				} else if (str.substring(1).indexOf("-") != -1) {
					// ex: ~100-150
					int ind = str.substring(1).indexOf("-");
					String str1 = str.substring(1, ind + 1).trim();
					String str2 = str.substring(ind + 2).trim();
					double d1 = decimalFormat2d.parse(str1).doubleValue();
					double d2 = decimalFormat2d.parse(str2).doubleValue();
					double mid = (d1 + d2) / 2;
					return new FuzzyQuantity(d1, d2, mid * 0.1, mid * 0.1, 0.);
				} else {
					// ex: ~125
					double q = decimalFormat2d.parse(str.substring(1).trim()).doubleValue();
					double d1 = q * 0.95;
					double d2 = q * 1.05;
					double s1 = q * 0.90;
					double s2 = q * 1.1;
					if (Math.abs(q) < 0.01) {
						d1 = 0.0;
						d2 = 0.01;
						s1 = 0.0;
						s2 = 0.02;
					}
					return new FuzzyQuantity(d1, d2, d1 - s1, s2 - d2, 0.);
				}
			} else if (str.charAt(0) == '>') {
				// ex: >125
				double q = decimalFormat2d.parse(str.substring(1).trim()).doubleValue();
				if (Math.abs(q) < 0.01) q = 0.01;
				return new FuzzyQuantity(q, positiveInfinity, 0., 0., 0.);
			} else if (str.charAt(0) == '<') {
				// ex: <125
				double q = decimalFormat2d.parse(str.substring(1).trim()).doubleValue();
				return new FuzzyQuantity(negativeInfinity, q, 0., 0., 0.);
			} else {
				// ex: 125.
				double q = decimalFormat2d.parse(str.trim()).doubleValue();
				return new FuzzyQuantity(q, q, 0., 0., 0.);
			}
		} catch(Exception ex) {
			throw new ParseException(str, 0);
		}
	}

	public Quantity parseObject(Object obj) throws ParseException {
		return FuzzyQuantity.parseFuzzyQuantity((String) obj);
	}

	public Quantity add(Quantity arg) {
		if (arg instanceof FuzzyQuantity) {
			FuzzyQuantity f = (FuzzyQuantity) arg;
			return new FuzzyQuantity(d1 + f.d1, d2 + f.d2, dt1 + f.dt1, dt2 + f.dt2, Math.max(h, f.h));
		}
		return null;
	}

	public Quantity sub(Quantity arg) {
		if (arg instanceof FuzzyQuantity) {
			FuzzyQuantity f = (FuzzyQuantity) arg;
			return new FuzzyQuantity(d1 - f.d2, d2 - f.d1, dt1 + f.dt2, dt2 + f.dt1, Math.max(h, f.h));
		}
		return null;
	}
	
	public Quantity remove(Quantity arg) {
		 // valable uniquement si l'incertitude arg.f de l'argument est nulle
		if (arg instanceof FuzzyQuantity) {
			FuzzyQuantity f = (FuzzyQuantity) arg;
			return new FuzzyQuantity(d1 - f.d1, d2 - f.d2, dt1 - f.dt1, dt2 - f.dt2, h);
		}
		return null;
	}
	

	public Quantity mul(Quantity arg) {
		if (arg instanceof FuzzyQuantity) {
			FuzzyQuantity f = (FuzzyQuantity) arg;
			return new FuzzyQuantity(d1 * f.d1, d2 * f.d2,
					f.dt1 * (d1 - dt1) + dt1 * f.d1,
					dt2 * f.d2 + (d2 + dt2) * f.dt2,
					Math.max(h, f.h));
		}
		return null;
	}

	public Quantity mul(double f) {
		return new FuzzyQuantity(d1*f, d2*f, dt1*f, dt2*f, h);
	}

	public Quantity div(Quantity arg) throws ArithmeticException {
		if (arg instanceof FuzzyQuantity) {
			FuzzyQuantity f = (FuzzyQuantity) arg;
			return new FuzzyQuantity(d1 / f.d1, d2 / f.d2,
					(d1 * f.dt2 + dt1 * f.d2) / (f.d2 * (f.dt2 + f.d2)),
					(f.d1 * dt2 + d2 * f.dt1) / (f.d1 * (f.d1 - f.dt1)),
					Math.max(h, f.h));
		}
		return null;
	}

	public Quantity div(double f) {
		return new FuzzyQuantity(d1/f, d2/f, dt1/f, dt2/f, h);
	}

	// ex: ~5 -> <5
	public static Quantity infTo(Quantity arg)  throws ArithmeticException {
		if (arg instanceof FuzzyQuantity) {
			FuzzyQuantity f = (FuzzyQuantity) arg;
			return new FuzzyQuantity(0., f.d2, 0., f.dt2, f.h);
		}
		return null;
	}


	// Return an inclusion degree of the current quantity into the interval <interval>
	public double inclusionDegreeInto(Quantity interval) {
		if (interval instanceof FuzzyQuantity) {
			FuzzyQuantity finterv = (FuzzyQuantity) interval;
			return necessity(finterv, this);
		}
		return 0.;
	}

	// Return an overlap degree of the current quantity with the interval <interval>
	public double overlapDegreeWith(Quantity interval) {
		if (interval instanceof FuzzyQuantity) {
			FuzzyQuantity finterv = (FuzzyQuantity) interval;
			return possibility(finterv, this);
		}
		return 0.;
	}


	public boolean greaterThan(Quantity x) {
		if (x instanceof FuzzyQuantity) {
			FuzzyQuantity fx = (FuzzyQuantity) x;
//System.out.println("this=" + this + ", x=" + x + ", d1 - dt1=" + (d1 - dt1) + ", fx.d2 + fx.dt2=" + (fx.d2 + fx.dt2));
			return (d1 - dt1 > fx.d2 + fx.dt2);
		}
		return false;
	}


	public boolean greaterThan(double x) {
		return (d1 - dt1 > x);
	}


	public boolean lessThan(Quantity x) {
		if (x instanceof FuzzyQuantity) {
			FuzzyQuantity fx = (FuzzyQuantity) x;
			return (fx.d1 - fx.dt1 > d2 + dt2);
		}
		return false;
	}


	public boolean lessThan(double x) {
		return (x > d2 + dt2);
	}


///////////////////           NECESSITY / POSSIBILITY COMPUTATION           /////////////////


static boolean test1(double h, double h_, double d1_, double dt1_, double d2, double dt2) {
	return ((1 - h) * (d1_ - dt1_ - d2) - (1 - h_) * dt2 >= 0);
}

static double inters1(double h, double h_, double d1_, double dt1_, double d2, double dt2) {
	return ((double)(1 - (1 - h) * (1 - h_) * (d1_ - d2) /
		((1 - h) * dt1_ + (1 - h_) * dt2)));
}

static boolean test2(double h, double h_, double d1, double d1_, double dt1, double dt1_) {
	return ((1 - h) * (d1_ - dt1_ - d1) + dt1 * h_ >= 0);
}

static boolean test3(double h, double h_, double d1, double d1_, double dt1, double dt1_) {
	return ((1 - h_) * (d1_ - d1 + dt1) - dt1_ * h >= 0);
}

static double inters2(double h, double h_, double d1, double d1_, double dt1, double dt1_) {
	return ((double)((1 - h) * ((1 - h_) * (d1 - d1_) + dt1_) /
		((1 - h) * dt1_ + (1 - h) * dt1)));
}

static boolean test4(double h, double h_, double d2, double d2_, double dt2, double dt2_) {
	return ((1 - h_) * (d2 + dt2 - d2_) - dt2_ * h_ < 0);
}

static boolean test5(double h, double h_, double d2, double d2_, double dt2, double dt2_) {
	return ((1 - h) * (d2 - d2_ - dt2_) + h_ * dt2 >= 0);
}

static double inters3(double h, double h_, double d2, double d2_, double dt2, double dt2_) {
	return ((double)((1 - h) * ((1 - h_) * (d2_ - d2) + dt2_) /
		((1 - h_) * dt2 + (1 - h) * dt2_)));
}

static double alpha1(double h, double h_, double d1, double d1_, double dt1, double dt1_) {
	if (test2 (h, h_, d1, d1_, dt1, dt1_)) return ((double)h_);
	if (test3 (h, h_, d1, d1_, dt1, dt1_)) return (inters2 (h, h_, d1, d1_, dt1, dt1_));
	return ((double)(1 - h));
}

static double alpha2(double h, double h_, double d2, double d2_, double dt2, double dt2_) {
	if (test4 (h, h_, d2, d2_, dt2, dt2_)) return ((double)(1 - h));
	if (test5 (h, h_, d2, d2_, dt2, dt2_)) return ((double)(h_));
	return (inters3 (h, h_, d2, d2_, dt2, dt2_));
}

/*****************************************************************************/
/* ROLE : Calcule le degre de possibilite de la compatibilite de la          */
/*   donnee floue <D> avec le modele flou <P> (commutatif)                   */
/*****************************************************************************/
static double possibility(FuzzyQuantity P, FuzzyQuantity D) {
	if (P.d1 > D.d2) {
		FuzzyQuantity interm = D;
		D = P;
		P = interm;
	} else if (D.d1 <= P.d2)
		return ((double)1);
	if (test1 (P.h, D.h, D.d1, D.dt1, P.d2, P.dt2))
		return ((double)Math.max(P.h, D.h));
	return (inters1 (P.h, D.h, D.d1, D.dt1, P.d2, P.dt2));
}

/*****************************************************************************/
/* ROLE : Calcule le degre de necessite de la compatibilite de la            */
/*   donnee floue <D> avec le modele flou <P> (non commutatif)               */
/*****************************************************************************/
static double necessity(FuzzyQuantity P, FuzzyQuantity D) {
	double alph1, alph2;

	if (1 - P.h <= D.h) return (P.h);
	alph1 = alpha1 (P.h, D.h, P.d1, D.d1, P.dt1, D.dt1);
	if (alph1 == 1 - P.h) return (P.h);
	alph2 = alpha2 (P.h, D.h, P.d2, D.d2, P.dt2, D.dt2);
	return ((double) 1 - Math.max(alph1, alph2));
}


	public static void main(String[] args) {
		try {
			FuzzyQuantity P = FuzzyQuantity.parseFuzzyQuantity(args[0]);
			FuzzyQuantity D = FuzzyQuantity.parseFuzzyQuantity(args[1]);
			double pos = FuzzyQuantity.possibility(P, D);
			double nec = FuzzyQuantity.necessity(P, D);
			System.out.println("pos=" + pos + ", nec=" + nec);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
