package sh.kainz.plsql.scoping.select.dto;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public class SelectScopeElements {
	private Map<String, EObject> namedObjects;
	private String aliasPrefix;
	
	public SelectScopeElements(Map<String, EObject> namedObjects, String aliasPrefix) {
		super();
		this.namedObjects = namedObjects;
		this.aliasPrefix = aliasPrefix;
	}
	
	public String getAliasPrefix() {
		return aliasPrefix;
	}
	
	public void setAliasPrefix(String aliasPrefix) {
		this.aliasPrefix = aliasPrefix;
	}
	
	public Map<String, EObject> getNamedObjects() {
		return namedObjects;
	}
	
	public void setNamedObjects(Map<String, EObject> namedObjects) {
		this.namedObjects = namedObjects;
	}
	
	
}
