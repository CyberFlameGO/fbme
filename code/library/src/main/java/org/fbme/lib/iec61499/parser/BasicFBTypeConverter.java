package org.fbme.lib.iec61499.parser;


import org.fbme.lib.common.Identifier;
import org.fbme.lib.iec61499.declarations.AlgorithmBody;
import org.fbme.lib.iec61499.declarations.AlgorithmDeclaration;
import org.fbme.lib.iec61499.declarations.AlgorithmLanguage;
import org.fbme.lib.iec61499.declarations.BasicFBTypeDeclaration;
import org.fbme.lib.iec61499.ecc.*;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class BasicFBTypeConverter extends DeclarationConverterBase<BasicFBTypeDeclaration> {

    public BasicFBTypeConverter(ConverterArguments arguments) {
        super(arguments);
    }

    @Override
    protected BasicFBTypeDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
        assert myElement != null;

        BasicFBTypeDeclaration fbtd = myFactory.createBasicFBTypeDeclaration(identifier);
        Element basicFbElement = myElement.getChild("BasicFB");

        new FBInterfaceConverter(this, fbtd).extractInterface();
        new FBInterfaceAdaptersConverter(this, fbtd).extractAdapters();
        ParameterDeclarationConverter.extractAll(with(basicFbElement.getChild("InternalVars")), fbtd.getInternalVariables());

        Element eccElement = basicFbElement.getChild("ECC");
        if (eccElement != null) {
            ECC ecc = fbtd.getEcc();
            List<Element> ecStateElements = eccElement.getChildren("ECState");
            for (Element ecStateElement : ecStateElements) {
                ecc.getStates().add(new StateConverter(with(ecStateElement)).extract());
            }
            List<Element> ecTransitionElements = eccElement.getChildren("ECTransition");
            for (Element ecTransitionElement : ecTransitionElements) {
                ecc.getTransitions().add(convertEcTransition(ecTransitionElement));
            }
        }

        List<Element> algorithmElements = basicFbElement.getChildren("Algorithm");
        for (Element algorithmElement : algorithmElements) {
            fbtd.getAlgorithms().add(new AlgorithmConverter(with(algorithmElement)).extract());
        }

        return fbtd;
    }

    private static class StateConverter extends DeclarationConverterBase<StateDeclaration> {

        public StateConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected StateDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            assert myElement != null;

            StateDeclaration state = myFactory.createStateDeclaration(identifier);
            List<Element> ecActionElements = myElement.getChildren("ECAction");
            for (Element ecActionElement : ecActionElements) {
                StateAction action = myFactory.createStateAction();
                String algorithmName = ecActionElement.getAttributeValue("Algorithm");
                String eventName = ecActionElement.getAttributeValue("Output");
                if ((algorithmName != null && algorithmName.length() > 0)) {
                    action.getAlgorithm().setTargetName(algorithmName);
                }
                if ((eventName != null && eventName.length() > 0)) {
                    action.getEvent().setFQName(eventName);
                }
                state.getActions().add(action);
            }
            state.setX((int) Float.parseFloat(myElement.getAttributeValue("x")));
            state.setY((int) Float.parseFloat(myElement.getAttributeValue("y")));

            return state;
        }
    }

    private StateTransition convertEcTransition(Element ecTransitionElement) {
        StateTransition transition = myFactory.createStateTransition();
        transition.getSourceReference().setTargetName(ecTransitionElement.getAttributeValue("Source"));
        transition.getTargetReference().setTargetName(ecTransitionElement.getAttributeValue("Destination"));

        parseCondition(transition.getCondition(), ecTransitionElement.getAttributeValue("Condition"));
        transition.setCenterX((int) Float.parseFloat(ecTransitionElement.getAttributeValue("x")));
        transition.setCenterY((int) Float.parseFloat(ecTransitionElement.getAttributeValue("y")));

        return transition;
    }

    private void parseCondition(ECTransitionCondition condition, String rawCondition) {
        if (Objects.equals(rawCondition, "1")) {
            return;
        }
        int openBracketIndex = rawCondition.indexOf('[');
        int closeBracketIndex = rawCondition.lastIndexOf(']');
        if (openBracketIndex == -1) {
            condition.getEventReference().setFQName(rawCondition);
            return;
        }
        if (closeBracketIndex != rawCondition.length() - 1) {
            throw new IllegalArgumentException("Malformed transition condition");
        }
        if (openBracketIndex > 0) {
            condition.getEventReference().setFQName(rawCondition.substring(0, openBracketIndex));
        }
        String guardConditionText = unescapeXML(rawCondition.substring(openBracketIndex + 1, closeBracketIndex));
        condition.setGuardCondition(STConverter.parseExpression(myStFactory, guardConditionText));
    }

    private static class AlgorithmConverter extends DeclarationConverterBase<AlgorithmDeclaration> {

        public AlgorithmConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected AlgorithmDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            AlgorithmDeclaration algorithmDeclaration = myFactory.createAlgorithmDeclaration(identifier);
            Element stBodyElement = myElement.getChild("ST");
            if (stBodyElement != null) {
                AlgorithmBody.ST st = myFactory.createAlgorithmBody(AlgorithmLanguage.ST);
                algorithmDeclaration.setBody(st);
                String stText = unescapeXML(stBodyElement.getAttributeValue("Text"));
                if (stText != null) {
                    st.getStatements().addAll(STConverter.parseStatementList(myStFactory, stText));
                }
            }
            Element otherBodyElement = myElement.getChild("Other");
            if (otherBodyElement != null) {
                AlgorithmBody.Unknown unknown = myFactory.createAlgorithmBody(AlgorithmLanguage.unknown(otherBodyElement.getAttributeValue("Language")));
                algorithmDeclaration.setBody(unknown);
                String text = unescapeXML(otherBodyElement.getAttributeValue("Text"));
                if (text != null) {
                    unknown.setText(text);
                }
            }
            return algorithmDeclaration;
        }
    }
}
