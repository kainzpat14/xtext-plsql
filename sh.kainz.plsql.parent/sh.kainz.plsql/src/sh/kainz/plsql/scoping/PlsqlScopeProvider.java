/*
 * generated by Xtext 2.25.0
 */
package sh.kainz.plsql.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import sh.kainz.plsql.plsql.PlsqlPackage;
import sh.kainz.plsql.plsql.Queryblock;
import sh.kainz.plsql.plsql.Select;
import sh.kainz.plsql.plsql.SelectColumn;
import sh.kainz.plsql.plsql.SelectSource;
import sh.kainz.plsql.plsql.SetOperation;
import sh.kainz.plsql.plsql.SetSubstraction;
import sh.kainz.plsql.plsql.Subselect;
import sh.kainz.plsql.plsql.TableReference;
import sh.kainz.plsql.plsql.Union;

import org.eclipse.xtext.scoping.Scopes;
import java.util.ArrayList;

import sh.kainz.plsql.plsql.AtomicSubquery;
import sh.kainz.plsql.plsql.Column;
import sh.kainz.plsql.plsql.Intersection;


public class PlsqlScopeProvider extends AbstractPlsqlScopeProvider {
	@Override
	public IScope getScope(EObject context, EReference reference) {
		var column = findParentSelectColumn(context);
		if (column != null) {
			var select = (Select)column.eContainer();
			var source = findFirstQueryBlock(select).getSource();
			if(source != null) {	
				if(source instanceof TableReference) {
					TableReference tableRef = (TableReference)source;
					var allColumns = new ArrayList<Column>(tableRef.getTable().getColumns());
					return Scopes.scopeFor(allColumns);
				} else if(source instanceof Select) {
					Select subSelect = (Select)source;
					return Scopes.scopeFor(findFirstQueryBlock(subSelect).getColumns());
				} 
				
			} 
			return Scopes.scopeFor(new ArrayList<>());
		}
		return super.getScope(context, reference);
	}

	private Queryblock findFirstQueryBlock(Select select) {
		if(select instanceof Union) {
			return (Queryblock)((Union)select).getLeft();
		} else if(select instanceof Intersection) {
			return (Queryblock)((Intersection)select).getLeft();
		} else if(select instanceof SetSubstraction) {
			return (Queryblock)((SetSubstraction)select).getLeft();
		} else if(select instanceof Queryblock) {
			return (Queryblock)select;
		} else {
			throw new IllegalArgumentException("Unsupported type: "+select.getClass().getName());
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
