/*
 * generated by Xtext 2.25.0
 */
package sh.kainz.plsql.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.xtext.util.Modules2;
import sh.kainz.plsql.PlsqlRuntimeModule;
import sh.kainz.plsql.PlsqlStandaloneSetup;
import sh.kainz.plsql.ide.PlsqlIdeModule;

/**
 * Initialization support for running Xtext languages in web applications.
 */
public class PlsqlWebSetup extends PlsqlStandaloneSetup {
	
	@Override
	public Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new PlsqlRuntimeModule(), new PlsqlIdeModule(), new PlsqlWebModule()));
	}
	
}
