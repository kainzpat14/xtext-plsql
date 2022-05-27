package sh.kainz.plsql.naming;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.util.SimpleAttributeResolver;

import com.google.common.base.Function;

import sh.kainz.plsql.plsql.impl.ColumnExpressionImpl;

public class PlsqlQualifiedNameProvider extends DefaultDeclarativeQualifiedNameProvider {

	private Function<EObject, String> resolverDelegate = SimpleAttributeResolver.newResolver(String.class, "name");
	private static Set<EObject> runningResolutions = new HashSet<>();
	
	@Override
	protected Function<EObject, String> getResolver() {
		return this::resolveName;
	}

	protected String resolveName(EObject obj) {
		if(obj instanceof ColumnExpressionImpl) {
			ColumnExpressionImpl expression = (ColumnExpressionImpl)obj;
			if(expression.getName() == null && !runningResolutions.contains(obj)) {
				runningResolutions.add(obj);
				try {
					String name = resolverDelegate.apply(expression.getColumn());
					expression.setName(name);
					System.out.println("Resolved: "+name+" for "+expression.getColumn());
					return name;
				}
				finally {
					runningResolutions.remove(obj);
				}
			}
		}
		return resolverDelegate.apply(obj);
		
	}

	
}
