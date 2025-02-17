package org.fbme.smvDebugger.panel.items;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import org.fbme.smvDebugger.model.Counterexample;
import org.fbme.smvDebugger.panel.mvc.DebugPanelMVCItem;
import org.fbme.smvDebugger.panel.mvc.DebugPanelModel;
import org.fbme.smvDebugger.visualization.SystemHighlighter;

import java.awt.*;

public class ControlPanel extends JBPanel implements DebugPanelMVCItem {
    private static final String STEP_LABEL_NAME = "Current step:";
    private static final String TIME_LABEL_NAME = "Current time:";
    private static final String CONDITION_LABEL_NAME = "Condition:";

    private final StepChanger stepChanger;
    private final GlobalTime globalTime;
    private final ConditionNavigator conditionNavigator;

    public ControlPanel(final Counterexample counterexample, final SystemHighlighter systemHighlighter) {
        stepChanger = new StepChanger(counterexample, systemHighlighter);
        globalTime = new GlobalTime();
        conditionNavigator = new ConditionNavigator(counterexample);
    }

    @Override
    public void setPanelModel(final DebugPanelModel model) {
        stepChanger.setPanelModel(model);
        globalTime.setPanelModel(model);
        conditionNavigator.setPanelModel(model);
    }

    @Override
    public void initView() {
        stepChanger.initView();
        globalTime.initView();
        conditionNavigator.initView();

        setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(new JBLabel(STEP_LABEL_NAME), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        add(stepChanger, constraints);

    //    constraints.gridx = 0;
    //    constraints.gridy = 1;
    //    add(new JBLabel(TIME_LABEL_NAME), constraints);
    //
    //    constraints.gridx = 1;
    //    constraints.gridy = 1;
    //    add(globalTime, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(new JBLabel(CONDITION_LABEL_NAME), constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        add(conditionNavigator, constraints);
    }

    @Override
    public void initController() {
        stepChanger.initController();
        globalTime.initController();
        conditionNavigator.initController();
    }
}
