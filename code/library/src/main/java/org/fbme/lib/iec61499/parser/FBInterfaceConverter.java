package org.fbme.lib.iec61499.parser;


import org.fbme.lib.common.Identifier;
import org.fbme.lib.iec61499.declarations.EventAssociation;
import org.fbme.lib.iec61499.declarations.EventDeclaration;
import org.fbme.lib.iec61499.declarations.FBInterfaceDeclaration;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class FBInterfaceConverter extends ConverterBase {

    private final FBInterfaceDeclaration myDeclaration;

    public FBInterfaceConverter(ConverterArguments arguments, FBInterfaceDeclaration declaration) {
        super(arguments);
        myDeclaration = declaration;
    }

    public void extractInterface() {
        assert myElement != null;

        Element interfaceListElement = myElement.getChild("InterfaceList");

        extractEvents(interfaceListElement.getChild("EventInputs"), myDeclaration.getInputEvents());
        ParameterDeclarationConverter.extractAll(with(interfaceListElement.getChild("InputVars")), myDeclaration.getInputParameters());
        extractEvents(interfaceListElement.getChild("EventOutputs"), myDeclaration.getOutputEvents());
        ParameterDeclarationConverter.extractAll(with(interfaceListElement.getChild("OutputVars")), myDeclaration.getOutputParameters());
    }

    public void extractSubappInterface() {
        assert myElement != null;

        Element interfaceListElement = myElement.getChild("SubAppInterfaceList");

        extractSubappEvents(interfaceListElement.getChild("SubAppEventInputs"), myDeclaration.getInputEvents());
        ParameterDeclarationConverter.extractAll(with(interfaceListElement.getChild("InputVars")), myDeclaration.getInputParameters());
        extractSubappEvents(interfaceListElement.getChild("SubAppEventOutputs"), myDeclaration.getOutputEvents());
        ParameterDeclarationConverter.extractAll(with(interfaceListElement.getChild("OutputVars")), myDeclaration.getOutputParameters());
    }

    private void extractEvents(Element container, List<EventDeclaration> events) {
        if (container == null) {
            return;
        }
        List<Element> eventElements = container.getChildren("Event");
        for (Element eventElement : eventElements) {
            events.add(new EventConverter(with(eventElement)).extract());
        }
    }

    private void extractSubappEvents(Element container, List<EventDeclaration> events) {
        if (container == null) {
            return;
        }
        List<Element> eventElements = container.getChildren("SubAppEvent");
        for (Element eventElement : eventElements) {
            events.add(new EventConverter(with(eventElement)).extract());
        }
    }

    private class EventConverter extends DeclarationConverterBase<EventDeclaration> {

        public EventConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected EventDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            EventDeclaration event = myFactory.createEventDeclaration(identifier);

            for (Element withElement : myElement.getChildren("With")) {
                EventAssociation eventAssociation = myFactory.createEventAssociation();
                eventAssociation.getParameterReference().setTargetName(withElement.getAttributeValue("Var"));
                event.getAssociations().add(eventAssociation);
            }
            return event;
        }
    }
}
