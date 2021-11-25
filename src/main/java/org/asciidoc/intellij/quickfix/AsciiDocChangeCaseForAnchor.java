package org.asciidoc.intellij.quickfix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import org.asciidoc.intellij.AsciiDocBundle;
import org.asciidoc.intellij.psi.AsciiDocBlockId;
import org.asciidoc.intellij.psi.AsciiDocFileReference;
import org.asciidoc.intellij.psi.AsciiDocLink;
import org.asciidoc.intellij.psi.AsciiDocSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alexander Schwartz 2020
 */
public class AsciiDocChangeCaseForAnchor implements LocalQuickFix {

  @Override
  public @IntentionFamilyName @NotNull String getFamilyName() {
    return AsciiDocBundle.message("asciidoc.quickfix.changeCaseForAnchor");
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    if (element instanceof AsciiDocLink) {
      AsciiDocLink link = (AsciiDocLink) element;
      AsciiDocFileReference anchor = link.getAnchorReference();
      if (anchor != null) {
        ResolveResult[] resolveResultsAnchor = anchor.multiResolve(false);
        if (resolveResultsAnchor.length == 0) {
          List<ResolveResult> resolveResultsAnchorCaseInsensitive = anchor.multiResolveAnchor(true);
          if (resolveResultsAnchorCaseInsensitive.size() == 1) {
            PsiElement target = resolveResultsAnchorCaseInsensitive.get(0).getElement();
            if (target instanceof PsiNamedElement) {
              link.setAnchor(((PsiNamedElement) target).getName());
            } else if (target instanceof AsciiDocSection) {
              AsciiDocSection section = (AsciiDocSection) target;
              AsciiDocBlockId blockId = section.getBlockId();
              if (blockId != null) {
                link.setAnchor(blockId.getName());
              } else {
                link.setAnchor(section.getAutogeneratedId());
              }
            }
          }
        }
      }

    }
  }

}
