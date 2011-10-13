
package org.jcb.tools;
 
import org.jcb.tools.*;

import java.util.*;
import java.io.*;
import java.text.*;


public abstract class Quantity implements Serializable {

	public abstract String format();

	public abstract Quantity parseObject(Object obj) throws ParseException;

	public abstract Quantity add(Quantity a);

	public abstract Quantity sub(Quantity a);

	public abstract Quantity remove(Quantity a);

	public abstract Quantity mul(Quantity a);

	public abstract Quantity mul(double a);

	public abstract Quantity div(Quantity a);

	public abstract Quantity div(double a);

	public abstract double inclusionDegreeInto(Quantity interval);

	public abstract double overlapDegreeWith(Quantity interval);
	
	public abstract double middleValue();

}

