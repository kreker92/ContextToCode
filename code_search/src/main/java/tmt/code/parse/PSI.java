package tmt.code.parse;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
//import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
public class PSI {

//  public void extract() {
//    SelectionContextExtractor contextExtractor = new SelectionContextExtractor(editor, file);
//    SelectionContext context = contextExtractor.extractContext();
//    SelectionContextQueryBuilder queryBuilder = new SelectionContextQueryBuilder(context);
//    String query;
//    try {
//      query = queryBuilder.buildQuery();
//    } catch (NotEnoughContextException ignored) {
//      showInfoDialog("Unable to describe the context.", project);
//      return;
//    }
//    ContextHelperProjectComponent helperComponent =
//        ContextHelperProjectComponent.Companion.getFor(project);
//    helperComponent.processTextQuery(query + " java");
//  }
  
  public SelectionContext extractContext() {
    List<PsiElement> psiElements = new ArrayList<>();
    
    PsiDirectory configurationDir = directory.findSubdirectory(CONFIGURATION_PATH);
    if (configurationDir != null) {
        PsiFile configurationFile = configurationDir.findFile("module.xml");

        if (configurationFile != null && configurationFile instanceof XmlFile) {
            XmlTag rootTag = ((XmlFile) configurationFile).getRootTag();
            if (rootTag != null) {
                XmlTag module = rootTag.findFirstSubTag("module");
                if (module != null && module.getAttributeValue("name") != null) {
                    moduleName = module.getAttributeValue("name");
                    return moduleName;
                }
            }
        }
    }
    
    traversePsiElement(psiFile, psiElements);
    return new SelectionContext(psiElements);
  }

  private void traversePsiElement(PsiElement element, List<PsiElement> selectedElements) {
    int elementStart = element.getTextOffset();
    int elementEnd = elementStart + element.getTextLength();
   // if (selectionStartOffset <= elementStart && elementEnd <= selectionEndOffset) {
      selectedElements.add(element);
    //}
    for (PsiElement childElement : element.getChildren()) {
      traversePsiElement(childElement, selectedElements);
    }
  }
}
