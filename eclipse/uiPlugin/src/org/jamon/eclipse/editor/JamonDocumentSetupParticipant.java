package org.jamon.eclipse.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.jamon.eclipse.JamonEditor;

public class JamonDocumentSetupParticipant implements IDocumentSetupParticipant {
    
    public JamonDocumentSetupParticipant() {
    }

    /*
     * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
     */
    public void setup(IDocument document) {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3= (IDocumentExtension3) document;
            IDocumentPartitioner partitioner= new FastPartitioner(new JamonPartitionScanner(), JamonPartitionScanner.JAVA_PARTITION_TYPES);
            extension3.setDocumentPartitioner(JamonEditor.JAMON_PARTITIONING, partitioner);
            partitioner.connect(document);
        }
    }
}
