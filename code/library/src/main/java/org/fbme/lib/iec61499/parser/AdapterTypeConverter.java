package org.fbme.lib.iec61499.parser;


import org.fbme.lib.common.Identifier;
import org.fbme.lib.iec61499.declarations.AdapterTypeDeclaration;
import org.jetbrains.annotations.Nullable;

public class AdapterTypeConverter extends DeclarationConverterBase<AdapterTypeDeclaration> {

    public AdapterTypeConverter(ConverterArguments arguments) {
        super(arguments);
    }

    protected AdapterTypeDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
        AdapterTypeDeclaration declaration = myFactory.createAdapterTypeDeclaration(identifier);
        new FBInterfaceConverter(this, declaration).extractInterface();
        return declaration;
    }
}
