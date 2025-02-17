package org.fbme.lib.iec61499.parser;


import org.fbme.lib.common.Identifier;
import org.fbme.lib.iec61499.declarations.CompositeFBTypeDeclaration;
import org.jetbrains.annotations.Nullable;

public final class CompositeFBTypeConverter extends DeclarationConverterBase<CompositeFBTypeDeclaration> {

    public CompositeFBTypeConverter(ConverterArguments arguments) {
        super(arguments);
    }

    @Override
    protected CompositeFBTypeDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
        assert myElement != null;

        CompositeFBTypeDeclaration fbtd = myFactory.createCompositeFBTypeDeclaration(identifier);
        new FBInterfaceConverter(this, fbtd).extractInterface();
        new FBInterfaceAdaptersConverter(this, fbtd).extractAdapters();
        new FBNetworkConverter(with(myElement.getChild("FBNetwork")), fbtd.getNetwork()).extractNetwork();
        return fbtd;
    }

}
