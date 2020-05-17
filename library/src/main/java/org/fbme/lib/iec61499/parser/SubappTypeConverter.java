package org.fbme.lib.iec61499.parser;

/*Generated by MPS */

import org.fbme.lib.iec61499.declarations.SubapplicationTypeDeclaration;
import org.jetbrains.annotations.Nullable;
import org.fbme.lib.common.Identifier;
import org.jdom.Element;

public class SubappTypeConverter extends DeclarationConverterBase<SubapplicationTypeDeclaration> {

  public SubappTypeConverter(ConverterArguments arguments) {
    super(arguments);
  }

  @Override
  protected SubapplicationTypeDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
    Element subappnetworkElement = myElement.getChild("SubAppNetwork");
    SubapplicationTypeDeclaration std = myFactory.createSubapplicationTypeDeclaration(identifier);
    new FBInterfaceConverter(this, std).extractSubappInterface();
    new SubappNetworkConverter(with(subappnetworkElement), std.getNetwork()).extractNetwork();
    return std;
  }
}
