/*
 * generated by Xtext 2.25.0
 */
package sh.kainz.plsql.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import sh.kainz.plsql.plsql.PlsqlPackage;
import sh.kainz.plsql.plsql.Select;
import org.eclipse.xtext.scoping.Scopes;
import java.util.ArrayList;
import sh.kainz.plsql.plsql.Column;


public class PlsqlScopeProvider extends AbstractPlsqlScopeProvider {
	@Override
	public IScope getScope(EObject context, EReference reference) {
		if (reference == PlsqlPackage.Literals.SELECT__COLUMNS) {
			var select = (Select)context;
			var table = select.getTable();
			if(table != null) {	
				var allColumns = new ArrayList<Column>(table.getColumns());
				return Scopes.scopeFor(allColumns);
			} else {
				return Scopes.scopeFor(new ArrayList<>());
			}
		}
		return super.getScope(context, reference);
	}
}