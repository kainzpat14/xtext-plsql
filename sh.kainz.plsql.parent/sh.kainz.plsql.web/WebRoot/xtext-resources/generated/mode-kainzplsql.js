define(["ace/lib/oop", "ace/mode/text", "ace/mode/text_highlight_rules"], function(oop, mText, mTextHighlightRules) {
	var HighlightRules = function() {
		var keywords = "ALL|ALTER|AS|ASC|BY|CREATE|CROSS|CYCLE|DEFAULT|DEPTH|DESC|DISTINCT|FETCH|FIRST|FROM|INNER|INTERFACE|INTERSECT|JOIN|LAST|LEFT|MINUS|NATURAL|NEXT|NULLS|NUMBER|OFFSET|ON|ONLY|ORDER|OUTER|PERCENT|RIGHT|ROW|ROWS|SEARCH|SELECT|SET|SIBLINGS|TABLE|TIES|TO|TODO|UNION|UNQIUE|USING|VARCHAR2|WIDTH|WITH";
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
