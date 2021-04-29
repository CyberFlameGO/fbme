package org.fbme.ide.richediting.adapters.ecc.cell;

import com.intellij.ui.JBColor;
import jetbrains.mps.nodeEditor.cells.EditorCell_Collection;
import jetbrains.mps.nodeEditor.cells.ParentSettings;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import org.fbme.ide.richediting.editor.RichEditorStyleAttributes;
import org.fbme.lib.iec61499.declarations.AlgorithmDeclaration;
import org.fbme.lib.iec61499.ecc.StateAction;
import org.fbme.lib.iec61499.ecc.StateDeclaration;
import org.jetbrains.mps.openapi.model.SNode;

import java.awt.*;

public class AlgorithmCell extends ActionCell {
    private static final Color ALGORITHM_COLOR = new Color(199, 222, 193);
    private static final Color ALGORITHM_BODY_COLOR = new Color(230, 240, 212);
    private final EditorCell myAlgorithmBody;

    public AlgorithmCell(
            EditorContext editorContext,
            SNode node,
            StateAction action,
            EditorCell_Collection cellCollection,
            EditorCell body,
            StateDeclaration state
    ) {
        super(editorContext, node, ALGORITHM_COLOR, action, cellCollection, state);
        getStyle().set(RichEditorStyleAttributes.ALGORITHMS, action);
        this.myAlgorithmBody = body;
    }

    @Override
    protected void setTextFromAction() {
        AlgorithmDeclaration target = myAction.getAlgorithm().getTarget();
        if (target != null) {
            myNameText.setText(target.getName());
        } else {
            myNameText.setText("");
        }
    }

    @Override
    protected void paintContent(Graphics graphics, ParentSettings settings) {
        if (myAlgorithmBody != null) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(ALGORITHM_COLOR);
            g.fillRoundRect(myX, myY, myWidth + ACTIVE_WEIGHT_PADDING, myHeight + myAlgorithmBody.getHeight() + 5, 10, 10);
            if (!myNameText.getText().isEmpty()) {
                myNameText.paint(graphics, myX + SHIFT_X + (myWidth - myNameText.getWidth()) / 2, myY + SHIFT_Y, JBColor.BLACK);
            }
//            graphics.setColor(ALGORITHM_BODY_COLOR);
//            graphics.fillRect(myX, myAlgorithmBody.getY() - 10, myWidth + ACTIVE_WEIGHT_PADDING, myAlgorithmBody.getHeight());
//            graphics.fillRoundRect(myX, myAlgorithmBody.getY() - 10, myWidth + ACTIVE_WEIGHT_PADDING, myAlgorithmBody.getHeight() + 10);
        } else {
            super.paintContent(graphics, settings);
        }
    }
}