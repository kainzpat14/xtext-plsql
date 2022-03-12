package sh.kainz.plsql.conversion;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import org.eclipse.xtext.nodemodel.INode;

public class PlsqlValueConverterService extends DefaultTerminalConverters{

	@Override
	public Object toValue(String string, String lexerRule, INode node) throws ValueConverterException {
		if(lexerRule.equals("ID") || lexerRule.equals("sh.kainz.plsql.Plsql.ID")) {
			if(string.startsWith("\"")) {
				return string.substring(1,string.length()-1);
			} else {
				return string.toLowerCase();
			}
		}
		return super.toValue(string, lexerRule, node);
	}

	
}
