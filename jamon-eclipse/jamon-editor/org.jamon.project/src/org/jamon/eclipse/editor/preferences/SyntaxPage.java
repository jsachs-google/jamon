/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

import java.util.EnumMap;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SyntaxPage extends PreferencePage implements IWorkbenchPreferencePage
{
    public class DefaultBackgroundListener implements SelectionListener
    {

        @Override
        public void widgetDefaultSelected(SelectionEvent p_event)
        {
            m_backgroundColorSelector.setEnabled(! m_backgroundUseDefault.getSelection());
        }

        @Override
        public void widgetSelected(SelectionEvent p_event)
        {
            m_backgroundColorSelector.setEnabled(! m_backgroundUseDefault.getSelection());
        }

    }

    private List styleList = null;
    private ColorSelector m_foregroundColorSelector = null;
    private ColorSelector m_backgroundColorSelector = null;
    private Button m_backgroundUseDefault = null;
    private Button m_syntaxHighlightingEnabledCheckbox = null;
    private EnumMap<StyleOption, Button> styleOptionButtons =
        new EnumMap<StyleOption, Button>(StyleOption.class);

    private SyntaxPreferences m_syntaxPreferences;
    private SyntaxType m_currentSelectedSyntaxType = null;

    private final class StyleListSelectionListener implements SelectionListener
    {

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {}

        @Override
        public void widgetSelected(SelectionEvent e)
        {
            saveCurrentStyle();
            int selectionIndex = styleList.getSelectionIndex();
            if (selectionIndex >= 0 && selectionIndex < SyntaxType.values().length)
            {
                m_currentSelectedSyntaxType = SyntaxType.values()[selectionIndex];
                Style style = m_syntaxPreferences.getStyle(m_currentSelectedSyntaxType);
                m_foregroundColorSelector.setColorValue(style.getForeground());
                m_backgroundColorSelector.setColorValue(style.getBackground() == null ? new RGB(255,255,255) : style.getBackground());
                m_backgroundUseDefault.setSelection(style.getBackground() == null);
                for (StyleOption styleOption: StyleOption.values())
                {
                    styleOptionButtons.get(styleOption).setSelection(
                        style.isOptionSet(styleOption));
                }
                setStyleEnablement(true);
            }
            else
            {
                m_currentSelectedSyntaxType = null;
                setStyleEnablement(false);
            }
        }
    }

    public SyntaxPage()
    {
        initialize();
    }

    public SyntaxPage(String title)
    {
        super(title);
        initialize();
    }

    public SyntaxPage(String title, ImageDescriptor image)
    {
        super(title, image);
        initialize();
    }


    private void initialize()
    {
        m_syntaxPreferences = new SyntaxPreferences();
        setPreferenceStore(SyntaxPreferences.getPreferenceStore());
    }



    /**
     * This method initializes group
     *
     */
    private void createOptionsGroup(Composite parent)
    {
        Group optionsGroup = new Group(parent, SWT.NONE);
        optionsGroup.setLayout(new GridLayout(4, false));

        new Label(optionsGroup, SWT.NONE).setText("Foreground:");
        m_foregroundColorSelector = new ColorSelector(optionsGroup);
        new Label(optionsGroup, SWT.NONE).setLayoutData(makeGridData(2));

        new Label(optionsGroup, SWT.NONE).setText("Background:");
        m_backgroundColorSelector = new ColorSelector(optionsGroup);

        new Label(optionsGroup, SWT.NONE).setText("use default background");
        m_backgroundUseDefault = new Button(optionsGroup, SWT.CHECK);
        m_backgroundUseDefault.addSelectionListener(new DefaultBackgroundListener());

        for (StyleOption styleOption: StyleOption.values())
        {
            Button checkbox = new Button(optionsGroup, SWT.CHECK);
            checkbox.setLayoutData(makeGridData(4));
            checkbox.setText(styleOption.getLabel());
            styleOptionButtons.put(styleOption, checkbox);
        }
        setStyleEnablement(false);
        setAllOptionsEnablement(m_syntaxPreferences.isHighlightingEnabled());
    }

    private GridData makeGridData(int p_horizontalSpan)
    {
        GridData gridData = new GridData();
        gridData.horizontalSpan = p_horizontalSpan;
        return gridData;
    }

    @Override protected Control createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(gridData);
        composite.setLayout(new GridLayout(2, false));

        m_syntaxHighlightingEnabledCheckbox = new Button(composite, SWT.CHECK | SWT.LEFT);
        m_syntaxHighlightingEnabledCheckbox.setText("Use syntax highlighting (only affects newly opened documents)");
        m_syntaxHighlightingEnabledCheckbox.setSelection(m_syntaxPreferences.isHighlightingEnabled());
        m_syntaxHighlightingEnabledCheckbox.setLayoutData(makeGridData(2));

        m_syntaxHighlightingEnabledCheckbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent)
            {
                widgetSelected(selectionEvent);
            }

            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                setAllOptionsEnablement(m_syntaxHighlightingEnabledCheckbox.getSelection());
            }
        });

        Label elementLabel = new Label(composite, SWT.NONE);
        elementLabel.setText("Element");

        elementLabel.setLayoutData(makeGridData(2));

        styleList = new List(composite, SWT.NONE);
        GridData styleListGridData = new GridData();
        styleListGridData.horizontalAlignment = GridData.FILL;
        styleListGridData.grabExcessHorizontalSpace = false;
        styleListGridData.verticalAlignment = GridData.BEGINNING;
        styleList.setLayoutData(styleListGridData);
        styleList.setItems(SyntaxType.getNames());

        styleList.addSelectionListener(new StyleListSelectionListener());

        createOptionsGroup(composite);
        return composite;
    }

    @Override
    public void init(IWorkbench workbench)
    {
    }

    @Override public boolean performOk()
    {
        saveCurrentStyle();
        m_syntaxPreferences.setHighlightingEnabled(m_syntaxHighlightingEnabledCheckbox.getSelection());
        m_syntaxPreferences.apply();
        return true;
    }

    @Override protected void performDefaults()
    {
        m_syntaxPreferences.restoreDefaults();
        super.performDefaults();
    }

    private void saveCurrentStyle()
    {
        if (m_currentSelectedSyntaxType != null) {
            Style style = m_syntaxPreferences.getStyle(m_currentSelectedSyntaxType);
            style.setForeground(m_foregroundColorSelector.getColorValue());
            if ( m_backgroundUseDefault.getSelection())
            {
                style.setBackground(null);
            }
            else
            {
                style.setBackground(m_backgroundColorSelector.getColorValue());
            }
            for (StyleOption styleOption: StyleOption.values())
            {
                style.setOption(
                    styleOption, styleOptionButtons.get(styleOption).getSelection());
            }
            m_syntaxPreferences.setStyle(m_currentSelectedSyntaxType, style);
        }
    }

    private void setStyleEnablement(boolean enabled)
    {
        m_foregroundColorSelector.setEnabled(enabled);
        m_backgroundUseDefault.setEnabled(enabled);
        m_backgroundColorSelector.setEnabled(! m_backgroundUseDefault.getSelection() && enabled);
        for (Button button: styleOptionButtons.values())
        {
            button.setEnabled(enabled);
        }
    }

    private void setAllOptionsEnablement(boolean enabled) {
        styleList.setEnabled(enabled);
        setStyleEnablement(false);
        styleList.setSelection(-1);
    }
}
