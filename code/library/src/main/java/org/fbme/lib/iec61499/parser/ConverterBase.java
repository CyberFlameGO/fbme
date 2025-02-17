package org.fbme.lib.iec61499.parser;


import org.fbme.lib.iec61499.IEC61499Factory;
import org.fbme.lib.st.STFactory;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConverterBase implements ConverterArguments {

    @NotNull
    protected final IEC61499Factory myFactory;

    @NotNull
    protected final STFactory myStFactory;

    @NotNull
    protected final IdentifierLocus myIdentifierLocus;

    @Nullable
    protected final Element myElement;

    public ConverterBase(ConverterArguments arguments) {
        myFactory = arguments.getFactory();
        myStFactory = arguments.getStFactory();
        myIdentifierLocus = arguments.getIdentifierLocus();
        myElement = arguments.getElement();
    }

    @Nullable
    @Override
    public Element getElement() {
        return myElement;
    }

    @NotNull
    @Override
    public IEC61499Factory getFactory() {
        return myFactory;
    }

    @NotNull
    @Override
    public STFactory getStFactory() {
        return myStFactory;
    }

    @NotNull
    @Override
    public IdentifierLocus getIdentifierLocus() {
        return myIdentifierLocus;
    }

    protected String unescapeXML(String text) {
        if (text == null) {
            return null;
        }
        text = text.replace("&#10;", "\n");
        text = text.replace("&#34;", "\"");
        text = text.replace("&#38;", "&");
        text = text.replace("&#39;", "'");
        text = text.replace("&#60;", "<");
        text = text.replace("&#62;", ">");
        return text;
    }

}
