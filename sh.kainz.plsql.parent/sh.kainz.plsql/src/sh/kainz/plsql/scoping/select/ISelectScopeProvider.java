package sh.kainz.plsql.scoping.select;

import org.eclipse.xtext.scoping.IScope;

import sh.kainz.plsql.plsql.Select;

public interface ISelectScopeProvider {
	IScope findScope(Select select);
}
