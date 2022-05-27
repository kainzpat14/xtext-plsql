define(["ace/lib/oop", "ace/mode/text", "ace/mode/text_highlight_rules"], function(oop, mText, mTextHighlightRules) {
	var HighlightRules = function() {
		var keywords = "ALL|ALTER|AS|ASC|BY|CREATE|CYCLE|DEFAULT|DEPTH|DESC|DISTINCT|FETCH|FIRST|FROM|INTERFACE|INTERSECT|LAST|MINUS|NEXT|NULLS|NUMBER|OFFSET|ONLY|ORDER|PERCENT|ROW|ROWS|SEARCH|SELECT|SET|SIBLINGS|TABLE|TIES|TO|UNION|UNQIUE|VARCHAR2|WIDTH|WITH";
		this.$rules = {
			"start": [
				{token: "lparen", regex: "[(]"},
				{token: "rparen", regex: "[)]"},
				{token: "keyword", regex: "\\b(?:" + keywords + ")\\b"}
			]
		};
	};
	oop.inherits(HighlightRules, mTextHighlightRules.TextHighlightRules);
	
	var Mode = function() {
		this.HighlightRules = HighlightRules;
	};
	oop.inherits(Mode, mText.Mode);
	Mode.prototype.$id = "xtext/kainzplsql";
	Mode.prototype.getCompletions = function(state, session, pos, prefix) {
		return [];
	}
	
	return {
		Mode: Mode
	};
});
