package sh.kainz.plsql.ide.contentassist.antlr.lexer;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;


public class PlsqlLexer extends InternalPlsqlLexer{

	
	
	public PlsqlLexer() {
		super();
	}

	public PlsqlLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public PlsqlLexer(CharStream input) {
		super(input);
	}

	@Override
	public Token nextToken() {
		var token = super.nextToken();
		if(token.getType() == RULE_CASE_INSENSITIVE_ID) {
			token.setText(token.getText().toLowerCase());
		} else if(token.getType() == RULE_CASE_SENSITIVE_ID) {
			token.setText(token.getText().substring(1,token.getText().length()-1));
		} else if(token.getType() == RULE_ID) {
			if(token.getText().startsWith("\"")) {
				token.setText(token.getText().substring(1,token.getText().length()-1));
			} else {
				token.setText(token.getText().toLowerCase());
			}
		}
		System.out.print(token.getText());
		return token;
	}

}
