package org.jamon.eclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;

public class MockDocument implements IDocument
{
  public MockDocument(String p_initial)
  {
    this.m_content = new StringBuilder(p_initial);
  }
  
  private StringBuilder m_content;
  
  public void addDocumentListener(IDocumentListener p_listener)
  {
  }

  public void addDocumentPartitioningListener(IDocumentPartitioningListener p_listener)
  {
  }

  public void addPosition(Position p_position) throws BadLocationException
  {
  }

  public void addPosition(String p_category, Position p_position) throws BadLocationException, BadPositionCategoryException
  {
  }

  public void addPositionCategory(String p_category)
  {
  }

  public void addPositionUpdater(IPositionUpdater p_updater)
  {
  }

  public void addPrenotifiedDocumentListener(IDocumentListener p_documentAdapter)
  {
  }

  public int computeIndexInCategory(String p_category, int p_offset) throws BadLocationException, BadPositionCategoryException
  {
    return 0;
  }

  public int computeNumberOfLines(String p_text)
  {
    return 0;
  }

  public ITypedRegion[] computePartitioning(int p_offset, int p_length) throws BadLocationException
  {
    return null;
  }

  public boolean containsPosition(String p_category, int p_offset, int p_length)
  {
    return false;
  }

  public boolean containsPositionCategory(String p_category)
  {
    return false;
  }

  public String get()
  {
    return null;
  }

  public String get(int p_offset, int p_length) throws BadLocationException
  {
    return null;
  }

  public char getChar(int p_offset) throws BadLocationException
  {
    return m_content.charAt(p_offset);
  }

  public String getContentType(int p_offset) throws BadLocationException
  {
    return null;
  }

  public IDocumentPartitioner getDocumentPartitioner()
  {
    return null;
  }

  public String[] getLegalContentTypes()
  {
    return null;
  }

  public String[] getLegalLineDelimiters()
  {
    return new String[0];
  }

  public int getLength()
  {
    return m_content.length();
  }

  public String getLineDelimiter(int p_line) throws BadLocationException
  {
    return null;
  }

  public IRegion getLineInformation(int p_line) throws BadLocationException
  {
    return null;
  }

  public IRegion getLineInformationOfOffset(int p_offset) throws BadLocationException
  {
    return null;
  }

  public int getLineLength(int p_line) throws BadLocationException
  {

    return 0;
  }

  public int getLineOfOffset(int p_offset) throws BadLocationException
  {
    return 0;
  }

  public int getLineOffset(int p_line) throws BadLocationException
  {
    return 0;
  }

  public int getNumberOfLines()
  {
    return 0;
  }

  public int getNumberOfLines(int p_offset, int p_length) throws BadLocationException
  {
    return 0;
  }

  public ITypedRegion getPartition(int p_offset) throws BadLocationException
  {
    return null;
  }

  public String[] getPositionCategories()
  {
    return null;
  }

  public IPositionUpdater[] getPositionUpdaters()
  {
    return null;
  }

  public Position[] getPositions(String p_category) throws BadPositionCategoryException
  {
    return null;
  }

  public void insertPositionUpdater(IPositionUpdater p_updater, int p_index)
  {

  }

  public void removeDocumentListener(IDocumentListener p_listener)
  {

  }

  public void removeDocumentPartitioningListener(IDocumentPartitioningListener p_listener)
  {

  }

  public void removePosition(Position p_position)
  {

  }

  public void removePosition(String p_category, Position p_position) throws BadPositionCategoryException
  {

  }

  public void removePositionCategory(String p_category) throws BadPositionCategoryException
  {

  }

  public void removePositionUpdater(IPositionUpdater p_updater)
  {

  }

  public void removePrenotifiedDocumentListener(IDocumentListener p_documentAdapter)
  {

  }

  public void replace(int p_offset, int p_length, String p_text) throws BadLocationException
  {

  }

  public int search(int p_startOffset, String p_findString, boolean p_forwardSearch, boolean p_caseSensitive, boolean p_wholeWord)
      throws BadLocationException
  {
    return 0;
  }

  public void set(String p_text)
  {

  }

  public void setDocumentPartitioner(IDocumentPartitioner p_partitioner)
  {

  }

}