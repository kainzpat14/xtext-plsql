package sh.kainz.plsql.scoping.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import sh.kainz.plsql.plsql.Column;
import sh.kainz.plsql.plsql.ColumnExpression;
import sh.kainz.plsql.plsql.Intersection;
import sh.kainz.plsql.plsql.Join;
import sh.kainz.plsql.plsql.Queryblock;
import sh.kainz.plsql.plsql.RemoteTableReference;
import sh.kainz.plsql.plsql.Select;
import sh.kainz.plsql.plsql.SelectColumn;
import sh.kainz.plsql.plsql.SelectSource;
import sh.kainz.plsql.plsql.SetSubstraction;
import sh.kainz.plsql.plsql.Subquery;
import sh.kainz.plsql.plsql.SubqueryFactoring;
import sh.kainz.plsql.plsql.Table;
import sh.kainz.plsql.plsql.TableColumn;
import sh.kainz.plsql.plsql.TableDefinition;
import sh.kainz.plsql.plsql.TableReference;
import sh.kainz.plsql.plsql.Union;
import sh.kainz.plsql.plsql.impl.SelectExpressionImpl;
import sh.kainz.plsql.scoping.select.dto.SelectScopeElements;

public class SelectScopeProvider implements ISelectScopeProvider {


	@Override
	public IScope findScope(Select select) {
		var sources = findFirstQueryBlock(select).getSource();

		var elements = new ArrayList<SelectScopeElements>();
		if(sources != null) {	
			var source = sources.getFirst();
			elements.add(getScopeElements(source));
			if(sources.getJoin() != null) {
				elements.addAll(getScopeElements(sources.getJoin()));
			}
			List<IEObjectDescription> namedObjects = new ArrayList<>();
			for(SelectScopeElements elementList : elements) {
				for(Entry<String,EObject> element : elementList.getNamedObjects().entrySet()) {
					boolean isUnique = elements.stream().noneMatch(it -> it != elementList && it.getNamedObjects().containsKey(element.getKey()));
					if(isUnique) {
						namedObjects.add(new EObjectDescription(QualifiedName.create(element.getKey()), element.getValue(), null));
					}
					if(elementList.getAliasPrefix() != null) {
						namedObjects.add(new EObjectDescription(QualifiedName.create(elementList.getAliasPrefix(), element.getKey()), element.getValue(), null));
					}
				}
			}
			return new SimpleScope(namedObjects);
		} 
		return Scopes.scopeFor(new ArrayList<EObject>());
	}
	
	private List<SelectScopeElements> getScopeElements(Join join) {
		var result = new ArrayList<SelectScopeElements>();
		result.add(getScopeElements(join.getNext()));
		if(join.getJoin() != null) {
			result.addAll(getScopeElements(join.getJoin()));
		}
		return result;
	}
	
	private SelectScopeElements getScopeElements(SelectSource source) {
		if(source instanceof TableReference) {
			TableReference tableRef = (TableReference)source;
			if(tableRef.getTable() instanceof Table) {
				var allColumns = new ArrayList<TableColumn>(((Table)tableRef.getTable()).getColumns());
				return new SelectScopeElements(allColumns.stream().collect(Collectors.toMap(it -> it.getName(), it -> it)),tableRef.getName()!=null?tableRef.getName():tableRef.getTable().getName());
			} else if(tableRef.getTable() instanceof SubqueryFactoring){
				var subquery = (SubqueryFactoring) tableRef.getTable();
				var innerQuery = findFirstQueryBlock(subquery.getQuery());
				Map<String, EObject> elements = innerQuery.getColumns().stream().collect(Collectors.toMap(it -> findColumnNameInSubquery(it, subquery.getAliases(),innerQuery.getColumns()), it -> it));
				return new SelectScopeElements(elements, tableRef.getName());
			} 
		} else if(source instanceof Select) {
			Select subSelect = (Select)source;
			var queryBlock = findFirstQueryBlock(subSelect);
			Map<String, EObject> elements = queryBlock.getColumns().stream().collect(Collectors.toMap(it -> findSimpleColumnName(it), it -> it));
			return new SelectScopeElements(elements, source.getName());
		} else if(source instanceof RemoteTableReference) {
			RemoteTableReference tableRef = (RemoteTableReference)source;
			if(tableRef.getTable() instanceof TableDefinition) {
				var allColumns = new ArrayList<TableColumn>(((TableDefinition)tableRef.getTable()).getColumns());
				return new SelectScopeElements(allColumns.stream().collect(Collectors.toMap(it -> it.getName(), it -> it)),tableRef.getName());
			} 
		} 
		return null;
	}

	private String findColumnName(SelectColumn column) {
		EObject parent = findParent(column, Select.class, SubqueryFactoring.class);
		if(parent instanceof SubqueryFactoring) {
			SubqueryFactoring subquery = (SubqueryFactoring) parent;
			var innerQuery = findFirstQueryBlock(subquery.getQuery());
			List<String> aliases = subquery.getAliases();
			List<SelectColumn> columns = innerQuery.getColumns();
			return findColumnNameInSubquery(column, aliases, columns);
		} else {
			return findSimpleColumnName(column);
		}
		
	}

	private String findColumnNameInSubquery(SelectColumn column, List<String> aliases, List<SelectColumn> columns) {
		int index = columns.indexOf(column);
		if(index<aliases.size()) {
			return aliases.get(index);
		} else if(aliases.isEmpty()) {
			return findSimpleColumnName(column);
		} else {
			return null;
		}
	}

	private String findSimpleColumnName(SelectColumn column) {
		String name = null;
		if(column instanceof SelectExpressionImpl) {
			name = ((SelectExpressionImpl)column).getName();
		}
		if(name == null) {
			if(column instanceof ColumnExpression) {
				name = findColumnName(((ColumnExpression)column).getColumn());
			}
		}
		if(name == null) {
			throw new IllegalArgumentException("Cannot determine name for column");
		}
		return name;
	}
	
	private EObject findParent(EObject obj, Class<?>... supportedParents) {
		for(Class<?> parent : supportedParents) {
			if(parent.isAssignableFrom(obj.getClass())) {
				return obj;
			}
		}
		if(obj.eContainer() == null) {
			return null;
		}
		return findParent(obj.eContainer(),supportedParents);
	}
	
	private String findColumnName(Column column) {
		if(column instanceof TableColumn) {
			return ((TableColumn)column).getName();
		} else if(column instanceof SelectColumn) {
			return null;
		}
		return null;
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

}
