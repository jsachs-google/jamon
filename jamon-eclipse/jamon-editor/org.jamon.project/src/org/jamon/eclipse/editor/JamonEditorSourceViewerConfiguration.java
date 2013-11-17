/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.Collection;

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
import org.jamon.eclipse.editor.preferences.SyntaxPreferences;

public class JamonEditorSourceViewerConfiguration extends SourceViewerConfiguration
{
  private static final IAnnotationHover s_annotationHover = new JamonAnnotationHover();
  private PresentationReconciler m_reconciler;

  private Collection<DisposableScanner> m_disposableScanners = new ArrayList<DisposableScanner>();

  private boolean highlightingEnabled = SyntaxPreferences.loadIsHighlightingEnabled();

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
    if (!highlightingEnabled) {
      return super.getConfiguredDocumentPartitioning(sourceViewer);
    }
    return JamonEditor.JAMON_PARTITIONING;
  }

  @Override
  public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
  {
    if (!highlightingEnabled) {
        return super.getConfiguredContentTypes(sourceViewer);
    }
    String[] result = new String[JamonPartitionScanner.JAMON_PARTITION_TYPES.length + 1];
    System.arraycopy(JamonPartitionScanner.JAMON_PARTITION_TYPES, 0, result, 1, JamonPartitionScanner.JAMON_PARTITION_TYPES.length);
    result[0] = IDocument.DEFAULT_CONTENT_TYPE;
    return result;
  }

  private void addDamageRepairer(String name, ITokenScanner p_scanner)
  {
      DefaultDamagerRepairer dr = new DefaultDamagerRepairer(p_scanner);
      m_reconciler.setDamager(dr, name);
      m_reconciler.setRepairer(dr, name);
      if (p_scanner instanceof DisposableScanner) {
        m_disposableScanners.add((DisposableScanner)p_scanner);
    }
  }

  @Override
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
  {
      if (!highlightingEnabled) {
          return super.getPresentationReconciler(sourceViewer);
      }

      if (m_reconciler != null)
      {
          return m_reconciler;
      }
    m_reconciler = new PresentationReconciler();
    m_reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
    setDamageRepairers();
    return m_reconciler;
  }

  public void setDamageRepairers()
  {
      addDamageRepairer(IDocument.DEFAULT_CONTENT_TYPE, new RuleBasedScanner());
      for (PartitionDescriptor pd : PartitionDescriptor.values())
      {
        DisposableScanner scanner = pd.scanner();
        addDamageRepairer(pd.tokenname(), scanner);
      }
  }

  void dispose()
  {
      for (DisposableScanner disposableScanner: m_disposableScanners)
      {
          disposableScanner.dispose();
      }
  }
}
