
package org.jcb.tools;

import java.sql.*;
import java.io.*;

// hsql line command tool

public class hsql {

	// The entry point of this class
	public static void main(String args[]) {

		try {
			// Load the Hypersonic SQL JDBC driver
			Class.forName("org.hsql.jdbcDriver");

			// Connect to the database
			// It will be created automatically if it does not yet exist
			// 'hsql_ctdit_xusda' in the URL is the name of the database
			// "sa" is the user name and "" is the (empty) password
			Connection conn=DriverManager.getConnection("jdbc:HypersonicSQL:" + args[0],"sa","");

			InputStream is = null;
			if (args.length == 2)
				is = new FileInputStream(args[1]);
			else
				is = System.in;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			// Create a statement object
			Statement stat=conn.createStatement();

			while (true) {
				System.out.print("> ");
				System.out.flush();
				String line = br.readLine();
				if (line == null) break;
				try {
					if (stat.execute(line)) {
						ResultSet result=stat.getResultSet();
						ResultSetMetaData rsmd = result.getMetaData();
						while(result.next()) {
							for (int i = 0; i < rsmd.getColumnCount(); i++) {
								System.out.print(result.getString(i));
								if (i > 0) System.out.print(", ");
							}
							System.out.println("");
						}
					}
				} catch(Exception ex) {
					System.out.println(ex);
				}
			}

			// Finally, close the connection
			conn.close();

		} catch(Exception e) {
			System.out.println(e);
		}
	}

}
