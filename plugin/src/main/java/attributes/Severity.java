package attributes;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import java.util.HashMap;
import java.util.Map;

public enum Severity {
    BLOCKER(CodeInsightColors.WARNINGS_ATTRIBUTES, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, HighlightSeverity.WARNING),
    CRITICAL(CodeInsightColors.WARNINGS_ATTRIBUTES, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, HighlightSeverity.WARNING),
    MAJOR(CodeInsightColors.WARNINGS_ATTRIBUTES, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, HighlightSeverity.WARNING),
    MINOR(CodeInsightColors.WARNINGS_ATTRIBUTES, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, HighlightSeverity.WARNING),
    INFO(CodeInsightColors.WARNINGS_ATTRIBUTES, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, HighlightSeverity.WEAK_WARNING);

    private static final Map<String, Severity> cache;

    static {
        cache = new HashMap<>();
        for (Severity s : Severity.values()) {
            cache.put(s.toString(), s);
        }
    }

    private final TextAttributesKey defaultTextAttributes;
    private final ProblemHighlightType highlightType;
    private final HighlightSeverity highlightSeverity;

    Severity(TextAttributesKey defaultTextAttributes, ProblemHighlightType highlightType, HighlightSeverity highlightSeverity) {
        this.defaultTextAttributes = defaultTextAttributes;
        this.highlightType = highlightType;
        this.highlightSeverity = highlightSeverity;
    }

    public TextAttributesKey defaultTextAttributes() {
        return defaultTextAttributes;
    }

    public ProblemHighlightType highlightType() {
        return highlightType;
    }

    public HighlightSeverity highlightSeverity() {
        return highlightSeverity;
    }

    public static Severity byName(String name) {
        return cache.get(name);
    }
}

