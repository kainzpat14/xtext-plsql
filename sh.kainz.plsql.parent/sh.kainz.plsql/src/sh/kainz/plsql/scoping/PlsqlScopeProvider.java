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

import org.eclipse.xtext.scoping.Scopes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import sh.kainz.plsql.plsql.AtomicSubquery;
import sh.kainz.plsql.plsql.Column;
import sh.kainz.plsql.plsql.Intersection;
import sh.kainz.plsql.plsql.OrderBy;


public class PlsqlScopeProvider extends AbstractPlsqlScopeProvider {
	@Override
	public IScope getScope(EObject context, EReference reference) {
		var column = findParentSelectColumn(context);
		if (column != null) {
			return lookupSelectColumn(column);
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

	private IScope lookupSelectColumn(SelectColumn column) {
		Select select;
		if(column.eContainer() instanceof Select) {
			select = (Select)column.eContainer();
		} else if(column.eContainer() instanceof OrderBy) {
			var orderBy = (OrderBy)column.eContainer();
			select = (Select)orderBy.eContainer();
		} else {
			throw new IllegalArgumentException("Unsupported class: "+column.eContainer().getClass().getName());
		}
		var source = findFirstQueryBlock(select).getSource();
		if(source != null) {	
			if(source instanceof TableReference) {
				TableReference tableRef = (TableReference)source;
				if(tableRef.getTable() instanceof Table) {
					var allColumns = new ArrayList<Column>(((Table)tableRef.getTable()).getColumns());
					return Scopes.scopeFor(allColumns);
				} else if(tableRef.getTable() instanceof SubqueryFactoring){
					var subquery = (SubqueryFactoring) tableRef.getTable();
					var innerQuery = findFirstQueryBlock(subquery.getQuery());
					if(!subquery.getAliases().isEmpty()) {
						return Scopes.scopeFor(innerQuery.getColumns(),col -> findColumnName(col, innerQuery.getColumns(),subquery.getAliases()),Scopes.scopeFor(new ArrayList<>()));
					} else {
						return Scopes.scopeFor(innerQuery.getColumns());
					}
				} 
			} else if(source instanceof Select) {
				Select subSelect = (Select)source;
				return Scopes.scopeFor(findFirstQueryBlock(subSelect).getColumns());
			} else if(source instanceof RemoteTableReference) {
				RemoteTableReference tableRef = (RemoteTableReference)source;
				if(tableRef.getTable() instanceof TableDefinition) {
					var allColumns = new ArrayList<Column>(((TableDefinition)tableRef.getTable()).getColumns());
					return Scopes.scopeFor(allColumns);
				}
			}
			
		} 
		return Scopes.scopeFor(new ArrayList<>());
	}

	private QualifiedName findColumnName(SelectColumn column, List<SelectColumn> columns, List<String> aliases) {
		int index = columns.indexOf(column);
		if(index<aliases.size()) {
			return QualifiedName.create(aliases.get(index));
		} else {
			return null;
		}
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
		} else if(select instanceof Subquery) {
			return findFirstQueryBlock(((Subquery)select).getSub());
		}else {
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
