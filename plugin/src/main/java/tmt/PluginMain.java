package tmt;

import tmt.attributes.TextAttributes;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class PluginMain implements ProjectComponent {
    private final static Logger LOG = Logger.getInstance(PluginMain.class);

    private final Project project;

    /**
     * @param project The current project, i.e. the project which was just opened.
     */
    public PluginMain(Project project) {
        this.project = project;

        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {
                runInspectionOnFile (new GenerateInspection(), event.getParent(), event.getFile());
            }

          /*  public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                update(event);
            }

            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                update(event);
            }

            https://intellij-support.jetbrains.com/hc/en-us/community/posts/206111999-How-to-keep-inspections-created-by-LocalInspectionTool-in-place

            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                update(event);
            }*/
        }, project);
    }

    private void runInspectionOnFile(@NotNull LocalInspectionTool inspectionTool, PsiElement el, PsiFile file) {
        Document document = PsiDocumentManager
                .getInstance(project)
                .getDocument(file);

        AnnotationHolderImpl annotationHolder = new AnnotationHolderImpl(new AnnotationSession(file));

        LocalInspectionsPass localInspectionsPass = new LocalInspectionsPass(file,
                document,
                0,
                document.getTextLength(),
                LocalInspectionsPass.EMPTY_PRIORITY_RANGE,
                true,
                HighlightInfoProcessor.getEmpty());

        LocalInspectionToolWrapper lw = new LocalInspectionToolWrapper(inspectionTool);
        ArrayList lwl = new ArrayList<LocalInspectionToolWrapper>();
        lwl.add(lw);

        InspectionManagerEx inspectionManager = (InspectionManagerEx) InspectionManager.getInstance(file.getProject());
        GlobalInspectionContext context = inspectionManager.createNewGlobalContext(false);

        final List<ProblemDescriptor> problemDescriptors = InspectionEngine.runInspectionOnFile(file, new LocalInspectionToolWrapper(inspectionTool), context);

        ArrayList<HighlightInfo> hls = new ArrayList<>();
        for ( ProblemDescriptor p_ : problemDescriptors) {
            SuggestGenerate p = (SuggestGenerate) p_;

            int start_line = p.getLineNumber();

            int end_line = p.getLineNumber();

            if (document.getLineNumber(file.getLastChild().getTextOffset()) != end_line)
                end_line += 1;

            TextRange range = new TextRange(document.getLineStartOffset(start_line), document.getLineEndOffset(end_line));

            System.err.println(range);

//        holder.createWarningAnnotation(range, "Uresollve 1").
//                registerFix(new QuickFix(key));
//        holder.createWarningAnnotation(range, "Uresollve 2").
//                registerFix(new QuickFix(key + "!"));

//            hl.add(createHighlight(p.getPsiElement(), "huy!!!"));
//        }

            Annotation annotation = annotationHolder.createWarningAnnotation(range, "Suggest for next line:" +p.getAnnotationMessage());
            annotation.setHighlightType(ProblemHighlightType.INFORMATION);
            annotation.setTextAttributes(TextAttributes.CRITICAL);
            annotation.registerFix(new QuickFix(p.getFixMessage(), project, p.getLineNumber()));

            HighlightInfo hl = HighlightInfo.fromAnnotation(annotation);
            hls.add(hl);
        }

        UpdateHighlightersUtil.setHighlightersToEditor(project,
                document,
                0,
                document.getTextLength(),
                hls,
                null,
                0);
    }

/*    private static HighlightInfo createHighlight(TextRange range, @Nullable String message) {
        HighlightInfo.Builder builder = HighlightInfo.newHighlightInfo(HighlightInfoType.WARNING)
                .range(range)
                .severity(HighlightSeverity.ERROR)
                .textAttributes(tmt.attributes.TextAttributes.CRITICAL);

        if (message != null && !message.isEmpty() && !"...".equals(message)) {
            builder.descriptionAndTooltip("SonarLint: " + message);
        }
        return builder.create();
    }*/

    public void initComponent() {
        //called before projectOpened()
        System.err.println(project);


    }

    public void projectOpened() {
        LOG.info(String.format("Project '%s' has been opened, base dir '%s'", project.getName(), project.getBaseDir().getCanonicalPath()));
    }

    public void projectClosed() {
        LOG.info(String.format("Project '%s' has been closed.", project.getName()));
    }

    public void disposeComponent() {
        //called after projectClosed()
    }

    @NotNull
    public String getComponentName() {
        return "myProjectComponent";
    }
}