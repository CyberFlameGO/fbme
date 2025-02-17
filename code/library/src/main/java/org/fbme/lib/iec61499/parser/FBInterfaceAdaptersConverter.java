package org.fbme.lib.iec61499.parser;


import org.fbme.lib.common.Identifier;
import org.fbme.lib.iec61499.declarations.FBInterfaceDeclarationWithAdapters;
import org.fbme.lib.iec61499.declarations.PlugDeclaration;
import org.fbme.lib.iec61499.declarations.SocketDeclaration;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class FBInterfaceAdaptersConverter extends ConverterBase {

    private final FBInterfaceDeclarationWithAdapters myDeclaration;

    public FBInterfaceAdaptersConverter(ConverterArguments arguments, FBInterfaceDeclarationWithAdapters declaration) {
        super(arguments);
        myDeclaration = declaration;
    }

    public void extractAdapters() {
        assert myElement != null;

        Element interfaceListElement = myElement.getChild("InterfaceList");
        extractPlugs(interfaceListElement.getChild("Plugs"), myDeclaration.getPlugs());
        extractSockets(interfaceListElement.getChild("Sockets"), myDeclaration.getSockets());
    }

    public void extractPlugs(Element container, List<PlugDeclaration> plugs) {
        if (container == null) {
            return;
        }
        List<Element> varElements = container.getChildren("AdapterDeclaration");
        for (Element plugElement : varElements) {
            plugs.add(new PlugConverter(with(plugElement)).extract());
        }
    }

    public void extractSockets(Element container, List<SocketDeclaration> sockets) {
        if (container == null) {
            return;
        }
        List<Element> varElements = container.getChildren("AdapterDeclaration");
        for (Element socketElement : varElements) {
            sockets.add(new SocketConverter(with(socketElement)).extract());
        }
    }

    public class PlugConverter extends DeclarationConverterBase<PlugDeclaration> {

        public PlugConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected PlugDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            PlugDeclaration plug = myFactory.createPlugDeclaration(identifier);
            plug.getTypeReference().setTargetName(myElement.getAttributeValue("Type"));
            return plug;
        }
    }

    public class SocketConverter extends DeclarationConverterBase<SocketDeclaration> {

        public SocketConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected SocketDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            SocketDeclaration socket = myFactory.createSocketDeclaration(identifier);
            socket.getTypeReference().setTargetName(myElement.getAttributeValue("Type"));
            return socket;
        }
    }
}
