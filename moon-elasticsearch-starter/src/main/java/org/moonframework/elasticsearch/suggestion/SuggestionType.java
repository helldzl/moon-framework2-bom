package org.moonframework.elasticsearch.suggestion;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/12/1
 */
public enum SuggestionType {

    TERM(1) {
        @Override
        protected void accept(Map<String, Object> map, Suggest.Suggestion.Entry.Option option) {
            if (option instanceof TermSuggestion.Entry.Option) {
                TermSuggestion.Entry.Option o = (TermSuggestion.Entry.Option) option;
                map.put("freq", o.getFreq());
            }
        }

    },

    COMPLETION(2) {
        @Override
        protected void accept(Map<String, Object> map, Suggest.Suggestion.Entry.Option option) {
            if (option instanceof CompletionSuggestion.Entry.Option) {
                CompletionSuggestion.Entry.Option o = (CompletionSuggestion.Entry.Option) option;
                map.put("payload", o.getPayloadAsMap());
            }
        }

    },

    PHRASE(3) {
        @Override
        protected void accept(Map<String, Object> map, Suggest.Suggestion.Entry.Option option) {

        }

    };

    private static final String TEXT = "text";
    private static final String OFFSET = "offset";
    private static final String LENGTH = "length";
    private static final String OPTIONS = "options";

    private static Map<Integer, SuggestionType> map = new HashMap<>();

    static {
        for (SuggestionType suggestionType : SuggestionType.values())
            map.put(suggestionType.type, suggestionType);
    }

    public static SuggestionType from(int type) {
        return map.get(type);
    }

    public static Map<String, Object> suggest(Suggest suggest) {
        if (suggest == null)
            return null;

        Map<String, Object> suggests = new HashMap<>();
        for (Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> suggestion : suggest) {

            List<Map<String, Object>> array = new ArrayList<>();
            for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : suggestion.getEntries()) {

                List<Map<String, Object>> list = new ArrayList<>();
                for (Suggest.Suggestion.Entry.Option option : entry.getOptions()) {
                    Map<String, Object> options = SuggestionType.option(option);
                    from(suggestion.getType()).accept(options, option);
                    list.add(options);
                }

                Map<String, Object> item = entry(entry);
                item.put(OPTIONS, list);
                array.add(item);
            }

            suggests.put(suggestion.getName(), array);
        }

//        Map<String, Object> root = new HashMap<>();
//        root.put("suggest", suggests);
        return suggests;
    }

    private static Map<String, Object> entry(Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry) {
        Map<String, Object> map = new HashMap<>();
        map.put(TEXT, entry.getText().toString());
        map.put(OFFSET, entry.getOffset());
        map.put(LENGTH, entry.getLength());
        return map;
    }

    private static Map<String, Object> option(Suggest.Suggestion.Entry.Option option) {
        Map<String, Object> map = new HashMap<>();
        map.put("text", option.getText().string());
        map.put("score", option.getScore());
        Text highlighted = option.getHighlighted();
        if (highlighted != null)
            map.put("highlighted", highlighted.string());
        return map;
    }

    private final int type;

    SuggestionType(int type) {
        this.type = type;
    }

    abstract protected void accept(Map<String, Object> map, Suggest.Suggestion.Entry.Option option);

}
