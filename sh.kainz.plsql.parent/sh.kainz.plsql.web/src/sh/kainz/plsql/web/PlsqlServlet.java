/*
 * generated by Xtext 2.25.0
 */
package sh.kainz.plsql.web;

import com.google.inject.Injector;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import org.eclipse.xtext.util.DisposableRegistry;
import org.eclipse.xtext.web.servlet.XtextServlet;

/**
 * Deploy this class into a servlet container to enable DSL-specific services.
 */
@WebServlet(name = "XtextServices", urlPatterns = "/xtext-service/*")
public class PlsqlServlet extends XtextServlet {
	
	private static final long serialVersionUID = 1L;
	
	DisposableRegistry disposableRegistry;
	
	public void init() throws ServletException {
		super.init();
		Injector injector = new PlsqlWebSetup().createInjectorAndDoEMFRegistration();
		this.disposableRegistry = injector.getInstance(DisposableRegistry.class);
	}
	
	public void destroy() {
		if (disposableRegistry != null) {
			disposableRegistry.dispose();
			disposableRegistry = null;
		}
		super.destroy();
	}
	
}
