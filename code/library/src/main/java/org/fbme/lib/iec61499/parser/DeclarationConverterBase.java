package org.fbme.lib.iec61499.parser;

import org.fbme.lib.common.Declaration;
import org.fbme.lib.common.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class DeclarationConverterBase<DeclarationT extends Declaration> extends ConverterBase {

    public DeclarationConverterBase(ConverterArguments arguments) {
        super(arguments);
    }

    public final DeclarationT extract() {
        assert myElement != null;

        Identifier identifier = myIdentifierLocus.onDeclarationEntered(myElement);
        try {
            DeclarationT declaration = extractDeclarationBody(identifier);
            declaration.setName(myElement.getAttributeValue("Name"));
            return declaration;
        } finally {
            myIdentifierLocus.onDeclarationLeaved();
        }
    }

    protected abstract DeclarationT extractDeclarationBody(@Nullable Identifier identifier);
}
