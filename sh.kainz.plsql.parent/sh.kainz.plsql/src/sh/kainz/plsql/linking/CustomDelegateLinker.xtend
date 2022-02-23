package sh.kainz.plsql.linking

import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer
import org.eclipse.xtext.linking.ILinker
import sh.kainz.plsql.plsql.Model
import org.eclipse.xtext.EcoreUtil2
import sh.kainz.plsql.plsql.AlterTable
import org.eclipse.xtext.linking.lazy.LazyLinker

class CustomDelegateLinker extends LazyLinker {

	override void linkModel(EObject model, IDiagnosticConsumer diagnosticsConsumer) { 
		super.linkModel(model,diagnosticsConsumer);
		if(model instanceof Model) {
			val candidates = EcoreUtil2.getAllContentsOfType(model, AlterTable)
			val alterTables = candidates.filter[it.table == table];
			for(alterTable : alterTables) {
				alterTable.table.columns.addAll(alterTable.columns);
			}
		}
	}
}
