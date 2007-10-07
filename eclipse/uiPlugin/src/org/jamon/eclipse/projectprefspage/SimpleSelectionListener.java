/**
 * 
 */
package org.jamon.eclipse.projectprefspage;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

abstract class SimpleSelectionListener implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }
}