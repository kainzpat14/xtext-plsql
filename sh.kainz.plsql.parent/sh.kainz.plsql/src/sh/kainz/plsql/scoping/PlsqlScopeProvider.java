/*
 * generated by Xtext 2.25.0
 */
package sh.kainz.plsql.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import sh.kainz.plsql.plsql.PlsqlPackage;
import sh.kainz.plsql.plsql.Queryblock;
import sh.kainz.plsql.plsql.RemoteTableReference;
import sh.kainz.plsql.plsql.Select;
import sh.kainz.plsql.plsql.SelectColumn;
import sh.kainz.plsql.plsql.SelectSource;
import sh.kainz.plsql.plsql.SetOperation;
import sh.kainz.plsql.plsql.SetSubstraction;
import sh.kainz.plsql.plsql.Subquery;
import sh.kainz.plsql.plsql.SubqueryFactoring;
import sh.kainz.plsql.plsql.Subselect;
import sh.kainz.plsql.plsql.Table;
import sh.kainz.plsql.plsql.TableDefinition;
import sh.kainz.plsql.plsql.TableReference;
import sh.kainz.plsql.plsql.TopLevelSubquery;
import sh.kainz.plsql.plsql.Union;
import sh.kainz.plsql.plsql.impl.SubqueryImpl;
import sh.kainz.plsql.scoping.select.ISelectScopeProvider;

import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import sh.kainz.plsql.plsql.AtomicSubquery;
import sh.kainz.plsql.plsql.Column;
import sh.kainz.plsql.plsql.ConditionalJoin;
import sh.kainz.plsql.plsql.Intersection;
import sh.kainz.plsql.plsql.Join;
import sh.kainz.plsql.plsql.OrderBy;


public class PlsqlScopeProvider extends AbstractPlsqlScopeProvider {
	@Inject
	private ISelectScopeProvider selectScope;
	
	@Override
	public IScope getScope(EObject context, EReference reference) {
		var column = findParentSelectColumn(context);
		if (column != null || SelectColumn.class.isAssignableFrom(reference.getContainerClass())) {
			return lookupSelectColumn(column!=null?column:context);
		}
		if(context instanceof TableReference) {
			var result = new ArrayList<EObject>();
			appendWithObjects(result, context);
			return Scopes.scopeFor(result,super.getScope(context, reference));
		}
		if(context instanceof RemoteTableReference) {
			RemoteTableReference ref = (RemoteTableReference)context;
			var result = new ArrayList<EObject>();
			var scope = super.getScope(context, reference);
			scope.getAllElements().forEach(it -> {
				IEObjectDescription desc = it;
				var obj = desc.getEObjectOrProxy();
				if(obj instanceof TableDefinition) {
					var def = (TableDefinition)obj;
					if(ref.getRemote().equals(def.getRemote())) {
						result.add(def);
					}
				}
			});
			
			return Scopes.scopeFor(result);
		}
		return super.getScope(context, reference);
	}

	private void appendWithObjects(ArrayList<EObject> result, EObject context) {
		if(context instanceof Queryblock) {
			var queryBlock = (Queryblock) context;
			if(queryBlock.getWithDefinition()!=null) {
				result.addAll(queryBlock.getWithDefinition().getSubqueries());
			}
		} 
		if(context.eContainer() != null) {
			appendWithObjects(result,context.eContainer());
		}
	}

	private IScope lookupSelectColumn(EObject column) {
		Select select;
		if(column.eContainer() instanceof Select) {
			select = (Select)column.eContainer();
		} else if(column.eContainer() instanceof OrderBy) {
			var orderBy = (OrderBy)column.eContainer();
			select = (Select)orderBy.eContainer();
		} 
		else {
			select = findNextSelect(column);
		}
		return selectScope.findScope(select);
	}

	private Select findNextSelect(EObject eContainer) {
		if(eContainer == null) {
			throw new IllegalArgumentException("Cannot find select in path");
		} else if(eContainer instanceof Select) {
			return (Select) eContainer;
		} else {
			return findNextSelect(eContainer.eContainer());
		}
	}

	private SelectColumn findParentSelectColumn(EObject context) {
		if(context instanceof SelectColumn) {
			return (SelectColumn)context;
		}
		if(context.eContainer() != null) {
			return findParentSelectColumn(context.eContainer());
		}
		return null;
	}
}
