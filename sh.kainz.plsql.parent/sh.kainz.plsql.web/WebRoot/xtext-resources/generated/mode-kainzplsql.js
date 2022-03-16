define(["ace/lib/oop", "ace/mode/text", "ace/mode/text_highlight_rules"], function(oop, mText, mTextHighlightRules) {
	var HighlightRules = function() {
		var keywords = "ALL|ALTER|AS|ASC|BY|CREATE|DESC|FETCH|FIRST|FROM|INTERSECT|LAST|MINUS|NEXT|NULLS|NUMBER|OFFSET|ONLY|ORDER|PERCENT|ROW|ROWS|SELECT|SIBLINGS|TABLE|TIES|UNION|VARCHAR2|WITH";
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
