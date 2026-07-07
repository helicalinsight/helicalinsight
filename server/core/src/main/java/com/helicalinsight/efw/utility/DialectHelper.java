package com.helicalinsight.efw.utility;

import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.ReflectHelper;

public class DialectHelper {
	public static Dialect getDialect(Properties props) throws HibernateException {
		final String dialectName = props.getProperty( Environment.DIALECT );
		if ( dialectName == null ) {
			return getDialect(dialectName);
		}
		return getDialect( dialectName );
	}

	private static Dialect getDialect(String dialectName) {
		if ( dialectName == null ) {
			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
		}
		try {
			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
		}
		catch ( ClassNotFoundException cnfe ) {
			throw new HibernateException( "Dialect class not found: " + dialectName );
		}
		catch ( Exception e ) {
			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
		}
	}
}
