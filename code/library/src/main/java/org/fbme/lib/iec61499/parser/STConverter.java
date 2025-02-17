package org.fbme.lib.iec61499.parser;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fbme.lib.common.StringIdentifier;
import org.fbme.lib.st.STFactory;
import org.fbme.lib.st.expressions.*;
import org.fbme.lib.st.parser.STLexer;
import org.fbme.lib.st.parser.STParser;
import org.fbme.lib.st.statements.*;
import org.fbme.lib.st.types.DataType;
import org.fbme.lib.st.types.ElementaryType;
import org.fbme.lib.st.types.GenericType;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class STConverter {

    public static List<Statement> parseStatementList(STFactory factory, String text) {
        return extractStatementList(factory, createParser(text).statementList());
    }

    public static Expression parseExpression(STFactory factory, String text) {
        return extractExpression(factory, createParser(text).expression());
    }

    public static Literal<?> parseLiteral(STFactory factory, String text) {
        return extractLiteral(factory, createParser(text).literal());
    }

    public static DataType parseType(STFactory factory, String text) {
        if (text == null) {
            return null;
        }
        for (GenericType genericType : GenericType.values()) {
            if (Objects.equals(genericType.name(), text)) {
                return genericType;
            }
        }
        for (ElementaryType elementaryType : ElementaryType.values()) {
            if (Objects.equals(elementaryType.name(), text)) {
                return elementaryType;
            }
        }
        return factory.createDerivedType(new StringIdentifier(text), text);
    }

    private static List<Statement> extractStatementList(STFactory factory, STParser.StatementListContext statementListCtx) {
        List<Statement> statements = new ArrayList<Statement>();
        List<STParser.StatementContext> statementCtxs = statementListCtx.statements;
        for (STParser.StatementContext statementCtx : statementCtxs) {
            statements.add(extractStatement(factory, statementCtx));
        }
        return statements;
    }

    private static Statement extractStatement(STFactory factory, STParser.StatementContext statementCtx) {
        if (statementCtx instanceof STParser.IfStatementContext) {
            STParser.IfStatementContext ifStatementCtx = (STParser.IfStatementContext) statementCtx;
            IfStatement ifStatement = factory.createIfStatement();
            ifStatement.setCondition(extractExpression(factory, ifStatementCtx.condition));
            ifStatement.getThenClause().addAll(extractStatementList(factory, ifStatementCtx.thenClause));
            List<STParser.ElsifClauseContext> elsifClauseCtxs = ifStatementCtx.elsifClauses;
            List<ElseIfClause> elseIfClauses = ifStatement.getElseIfClauses();
            for (STParser.ElsifClauseContext elsifClauseCtx : elsifClauseCtxs) {
                ElseIfClause elseIfClause = factory.createElseIfClause();
                elseIfClause.setCondition(extractExpression(factory, elsifClauseCtx.condition));
                elseIfClause.getBody().addAll(extractStatementList(factory, elsifClauseCtx.body));
                elseIfClauses.add(elseIfClause);
            }
            if (ifStatementCtx.elseClause != null) {
                ifStatement.addElseClause().addAll(extractStatementList(factory, ifStatementCtx.elseClause));
            }
            return ifStatement;
        }
        if (statementCtx instanceof STParser.CaseStatementContext) {
            STParser.CaseStatementContext caseStatementCtx = (STParser.CaseStatementContext) statementCtx;
            CaseStatement caseStatement = factory.createCaseStatement();
            caseStatement.setExpression(extractExpression(factory, caseStatementCtx.expression()));
            List<STParser.CaseClauseContext> caseClauseCtxs = caseStatementCtx.caseClauses;
            List<CaseElement> cases = caseStatement.getCases();
            for (STParser.CaseClauseContext caseClauseCtx : caseClauseCtxs) {
                CaseElement caseElement = factory.createCaseElement();
                caseElement.setLiteral(extractLiteral(factory, caseClauseCtx.literal()));
                caseElement.getStatements().addAll(extractStatementList(factory, caseClauseCtx.body));
                cases.add(caseElement);
            }
            if (caseStatementCtx.elseClause != null) {
                caseStatement.addElseCase().addAll(extractStatementList(factory, caseStatementCtx.elseClause));
            }
            return caseStatement;
        }
        if (statementCtx instanceof STParser.ForStatementContext) {
            STParser.ForStatementContext forStatementCtx = (STParser.ForStatementContext) statementCtx;
            ForStatement forStatement = factory.createForStatement();
            ControlVariableDeclaration controlVariable = forStatement.getControlVariable();
            controlVariable.setName(forStatementCtx.ID().getText());
            controlVariable.setBeginExpression(extractExpression(factory, forStatementCtx.varBegin));
            controlVariable.setEndExpression(extractExpression(factory, forStatementCtx.varEnd));
            if (forStatementCtx.varBy != null) {
                controlVariable.setStepExpression(extractExpression(factory, forStatementCtx.varBy));
            }
            forStatement.getStatements().addAll(extractStatementList(factory, forStatementCtx.body));
            return forStatement;
        }
        if (statementCtx instanceof STParser.WhileStatementContext) {
            STParser.WhileStatementContext whileStatementCtx = (STParser.WhileStatementContext) statementCtx;
            WhileStatement whileStatement = factory.createWhileStatement();
            whileStatement.setCondition(extractExpression(factory, whileStatementCtx.condition));
            whileStatement.getBody().addAll(extractStatementList(factory, whileStatementCtx.body));
            return whileStatement;
        }
        if (statementCtx instanceof STParser.RepeatStatementContext) {
            STParser.RepeatStatementContext repeatStatementCtx = (STParser.RepeatStatementContext) statementCtx;
            RepeatStatement repeatStatement = factory.createRepeatStatement();
            repeatStatement.setCondition(extractExpression(factory, repeatStatementCtx.condition));
            repeatStatement.getBody().addAll(extractStatementList(factory, repeatStatementCtx.body));
            return repeatStatement;
        }
        if (statementCtx instanceof STParser.AssigmentStatementContext) {
            STParser.AssigmentStatementContext assigmentStatementCtx = (STParser.AssigmentStatementContext) statementCtx;
            AssignmentStatement assigmentStatement = factory.createAssignmentStatement();
            assigmentStatement.setVariable(extractVariable(factory, assigmentStatementCtx.variable()));
            assigmentStatement.setExpression(extractExpression(factory, assigmentStatementCtx.expression()));
            return assigmentStatement;
        }
        if (statementCtx instanceof STParser.ExitStatementContext) {
            return factory.createExitStatement();
        }
        if (statementCtx instanceof STParser.ReturnStatementContext) {
            return factory.createReturnStatement();
        }
        return null;
    }

    private static Expression extractExpression(STFactory factory, STParser.ExpressionContext expressionCtx) {
        if (expressionCtx instanceof STParser.ConstantContext) {
            return extractLiteral(factory, ((STParser.ConstantContext) expressionCtx).literal());
        }
        if (expressionCtx instanceof STParser.VarExpressionContext) {
            return extractVariable(factory, ((STParser.VarExpressionContext) expressionCtx).variable());
        }
        if (expressionCtx instanceof STParser.ParensExpressionContext) {
            ParenthesisExpression parenthesisExpression = factory.createParenthesisExpression();
            parenthesisExpression.setInnerExpression(extractExpression(factory, ((STParser.ParensExpressionContext) expressionCtx).e));
            return parenthesisExpression;
        }
        if (expressionCtx instanceof STParser.FunctionCallContext) {
            STParser.FunctionCallContext functionCallCtx = (STParser.FunctionCallContext) expressionCtx;
            FunctionCall functionCall = factory.createFunctionCall();
            functionCall.setFunctionName(functionCallCtx.ID().getText());
            List<STParser.ExpressionContext> parameterCtxs = functionCallCtx.params;
            List<Expression> actualParameters = functionCall.getActualParameters();
            for (STParser.ExpressionContext parameterCtx : parameterCtxs) {
                actualParameters.add(extractExpression(factory, parameterCtx));
            }

            return functionCall;
        }
        if (expressionCtx instanceof STParser.UnaryExpressionContext) {
            STParser.UnaryExpressionContext unaryExpressionCtx = (STParser.UnaryExpressionContext) expressionCtx;
            UnaryOperation operation = chooseUnaryOperation(unaryExpressionCtx.op.getText());
            UnaryExpression unaryExpression = factory.createUnaryExpression(operation);
            unaryExpression.setInnerExpression(extractExpression(factory, unaryExpressionCtx.e));
            return unaryExpression;
        }
        if (expressionCtx instanceof STParser.BinaryExpressionContext) {
            STParser.BinaryExpressionContext binaryExpressionCtx = (STParser.BinaryExpressionContext) expressionCtx;
            BinaryOperation binaryOperation = chooseBinaryOperation(binaryExpressionCtx.op.getText());
            BinaryExpression binaryExpression = factory.createBinaryExpression(binaryOperation);
            binaryExpression.setLeftExpression(extractExpression(factory, binaryExpressionCtx.l));
            binaryExpression.setRightExpression(extractExpression(factory, binaryExpressionCtx.r));
            return binaryExpression;
        }
        return null;
    }

    private static UnaryOperation chooseUnaryOperation(String text) {
        for (UnaryOperation unaryOperation : UnaryOperation.values()) {
            if (Objects.equals(unaryOperation.getAlias(), text)) {
                return unaryOperation;
            }
        }
        return null;
    }

    private static BinaryOperation chooseBinaryOperation(String text) {
        for (BinaryOperation binaryOperation : BinaryOperation.values()) {
            if (Objects.equals(binaryOperation.getAlias(), text)) {
                return binaryOperation;
            }
        }
        return null;
    }

    private static Variable extractVariable(STFactory factory, STParser.VariableContext variableCtx) {
        if (variableCtx instanceof STParser.VarReferenceContext) {
            VariableReference variableReference = factory.createVariableReference();
            variableReference.getReference().setTargetName(variableCtx.getText());
            return variableReference;
        }
        if (variableCtx instanceof STParser.ArraySelectorContext) {
            STParser.ArraySelectorContext arraySelectorCtx = ((STParser.ArraySelectorContext) variableCtx);
            ArrayVariable arrayVariable = factory.createArrayVariable();
            arrayVariable.setSubscribedVariable(extractVariable(factory, arraySelectorCtx.subscripted));
            List<Expression> subscripts = arrayVariable.getSubscripts();
            List<STParser.ExpressionContext> subscriptCtxs = arraySelectorCtx.subscrpits;
            for (STParser.ExpressionContext subscriptCtx : subscriptCtxs) {
                subscripts.add(extractExpression(factory, subscriptCtx));
            }
            return arrayVariable;
        }
        // TODO field selector
        return null;
    }

    private static Literal extractLiteral(STFactory factory, STParser.LiteralContext literalCtx) {
        if (literalCtx instanceof STParser.DecContext) {
            Literal<Integer> literal = (Literal<Integer>) factory.createLiteral(LiteralKind.DEC_INT);
            literal.setValue(Integer.parseInt(literalCtx.getText()));
            return literal;
        }
        if (literalCtx instanceof STParser.BinContext) {
            Literal<Integer> literal = (Literal<Integer>) factory.createLiteral(LiteralKind.BINARY_INT);
            literal.setValue(Integer.parseInt(literalCtx.getText().substring(2), 2));
            return literal;
        }
        if (literalCtx instanceof STParser.OctContext) {
            Literal<Integer> literal = (Literal<Integer>) factory.createLiteral(LiteralKind.OCT_INT);
            literal.setValue(Integer.parseInt(literalCtx.getText().substring(2), 8));
            return literal;
        }
        if (literalCtx instanceof STParser.HexContext) {
            Literal<Integer> literal = (Literal<Integer>) factory.createLiteral(LiteralKind.HEX_INT);
            literal.setValue(Integer.parseInt(literalCtx.getText().substring(3), 16));
            return literal;
        }
        if (literalCtx instanceof STParser.StringContext) {
            Literal<String> literal = (Literal<String>) factory.createLiteral(LiteralKind.STRING);
            String text = literalCtx.getText();
            // TODO unescape parsed string
            literal.setValue(text.substring(1, text.length() - 1));
            return literal;
        }
        if (literalCtx instanceof STParser.WstringContext) {
            Literal<String> literal = (Literal<String>) factory.createLiteral(LiteralKind.WSTRING);
            String text = literalCtx.getText();
            // TODO unescape parsed string
            literal.setValue(text.substring(1, text.length() - 1));
            return literal;
        }
        if (literalCtx instanceof STParser.BooleanContext) {
            Literal<Boolean> literal = (Literal<Boolean>) factory.createLiteral(LiteralKind.BOOL);
            literal.setValue(Objects.equals(literalCtx.getText(), "TRUE"));
            return literal;
        }
        if (literalCtx instanceof STParser.BooleanBinContext) {
            Literal<Boolean> literal = (Literal<Boolean>) factory.createLiteral(LiteralKind.BINARY_BOOL);
            literal.setValue(Objects.equals(literalCtx.getText(), "BOOL#1"));
            return literal;
        }
        return null;
    }

    private static STParser createParser(String text) {
        try {
            return new STParser(new CommonTokenStream(new STLexer(new ANTLRInputStream(new StringReader(text)))));
        } catch (IOException e) {
            throw new RuntimeException("Can't instatiate ST parser");
        }
    }
}
