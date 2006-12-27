/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Jamon code, released February, 2003.
 *
 * The Initial Developer of the Original Code is Ian Robertson.  Portions
 * created by Ian Robertson are Copyright (C) 2005 Ian Robertson.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.jamon.eclipse.JamonAnnotationHover;
import org.jamon.eclipse.JamonEditor;

public class JamonEditorSourceViewerConfiguration extends SourceViewerConfiguration
{
  private static final IAnnotationHover s_annotationHover = new JamonAnnotationHover();

  @Override
  public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer)
  {
    return s_annotationHover;
  }

  @Override
  public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer)
  {
    return s_annotationHover;
  }

  @Override
  public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer)
  {
    return JamonEditor.JAMON_PARTITIONING;
  }

  @Override
  public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
  {
    String[] result = new String[JamonPartitionScanner.JAMON_PARTITION_TYPES.length + 1];
    System.arraycopy(JamonPartitionScanner.JAMON_PARTITION_TYPES, 0, result, 1, JamonPartitionScanner.JAMON_PARTITION_TYPES.length);
    result[0] = IDocument.DEFAULT_CONTENT_TYPE;
    return result;
  }

  private void addDamageRepairer(String name, PresentationReconciler reconciler, ITokenScanner p_scanner)
  {
    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(p_scanner);
    reconciler.setDamager(dr, name);
    reconciler.setRepairer(dr, name);
  }
  
  @Override
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
  {
    PresentationReconciler reconciler = new PresentationReconciler();
    reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
    DefaultDamagerRepairer dr;
    RuleBasedScanner scanner = new RuleBasedScanner();
    // scanner.setDefaultReturnToken(Token.UNDEFINED);
    dr = new DefaultDamagerRepairer(scanner);
    reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
    reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    for (PartitionDescriptor pd : PartitionDescriptor.values())
    {
      addDamageRepairer(pd.tokenname(), reconciler, pd.scanner());
    }
    System.err.println(JamonPartitionScanner.JAMON_LINE_TOKEN.getData().toString());
    addDamageRepairer(JamonPartitionScanner.JAMON_LINE_TOKEN.getData().toString(), reconciler, new SimpleScanner("\n%", "\n", "java_line"));
    return reconciler;
  }
}
