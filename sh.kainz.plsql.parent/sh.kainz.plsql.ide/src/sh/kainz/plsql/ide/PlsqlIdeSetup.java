/*
 * generated by Xtext 2.25.0
 */
package sh.kainz.plsql.ide;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.xtext.util.Modules2;
import sh.kainz.plsql.PlsqlRuntimeModule;
import sh.kainz.plsql.PlsqlStandaloneSetup;

/**
 * Initialization support for running Xtext languages as language servers.
 */
public class PlsqlIdeSetup extends PlsqlStandaloneSetup {

	@Override
	public Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new PlsqlRuntimeModule(), new PlsqlIdeModule()));
	}
	
}
