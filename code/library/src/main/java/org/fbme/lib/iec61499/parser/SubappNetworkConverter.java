package org.fbme.lib.iec61499.parser;


import org.fbme.lib.common.Identifier;
import org.fbme.lib.iec61499.fbnetwork.subapp.SubappNetwork;
import org.fbme.lib.iec61499.fbnetwork.subapp.SubapplicationDeclaration;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SubappNetworkConverter extends FBNetworkConverter {

    private final SubappNetwork myNetowrk;

    public SubappNetworkConverter(ConverterArguments arguments, SubappNetwork netowrk) {
        super(arguments, netowrk);
        myNetowrk = netowrk;
    }

    @Override
    public void extractNetwork() {
        if (myElement == null) {
            return;
        }
        super.extractNetwork();

        List<Element> subappElements = myElement.getChildren("SubApp");
        for (Element subappElement : subappElements) {
            myNetowrk.getSubapplications().add(new SubapplicationConverter(with(subappElement)).extract());
        }
    }

    private class SubapplicationConverter extends DeclarationConverterBase<SubapplicationDeclaration> {

        public SubapplicationConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected SubapplicationDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            SubapplicationDeclaration sd = myFactory.createSubapplicationDeclaration(identifier);
            sd.setX((int) Float.parseFloat(myElement.getAttributeValue("x")));
            sd.setY((int) Float.parseFloat(myElement.getAttributeValue("y")));
            sd.getTypeReference().setTargetName(myElement.getAttributeValue("Type"));
            return sd;
        }
    }
}
