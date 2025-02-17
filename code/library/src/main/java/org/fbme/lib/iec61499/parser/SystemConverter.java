package org.fbme.lib.iec61499.parser;


import org.fbme.lib.common.Identifier;
import org.fbme.lib.iec61499.declarations.*;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class SystemConverter extends DeclarationConverterBase<SystemDeclaration> {

    public SystemConverter(ConverterArguments arguments) {
        super(arguments);
    }

    @Override
    protected SystemDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
        SystemDeclaration system = myFactory.createSystemDeclaration(identifier);
        extractApplications(system.getApplications());
        extractDevices(system.getDevices());
        extractMappings(system.getMappings());
        extractSegments(system.getSegments());
        extractLinks(system.getLinks());
        return system;
    }

    private void extractApplications(List<ApplicationDeclaration> applications) {
        List<Element> applicationElements = myElement.getChildren("Application");
        for (Element applicationElement : applicationElements) {
            applications.add(new ApplicationConverter(with(applicationElement)).extract());
        }
    }

    private static class ApplicationConverter extends DeclarationConverterBase<ApplicationDeclaration> {

        public ApplicationConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected ApplicationDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            ApplicationDeclaration application = myFactory.createApplicationDeclaration(identifier);
            new SubappNetworkConverter(with(myElement.getChild("SubAppNetwork")), application.getNetwork()).extractNetwork();
            return application;
        }
    }

    private void extractDevices(List<DeviceDeclaration> devices) {
        List<Element> deviceElements = myElement.getChildren("Device");
        for (Element deviceElement : deviceElements) {
            devices.add(new DeviceConverter(with(deviceElement)).extract());
        }
    }

    private static class DeviceConverter extends DeclarationConverterBase<DeviceDeclaration> {

        public DeviceConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected DeviceDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            assert myElement != null;

            DeviceDeclaration device = myFactory.createDeviceDeclaration(identifier);
            device.getTypeReference().setTargetName(myElement.getAttributeValue("Type"));
            new ParameterAssignmentsConverter(with(myElement), device.getParameters()).extractParameters();
            for (Element resourceElement : myElement.getChildren("Resource")) {
                device.getResources().add(new ResourceConverter(with(resourceElement)).extract());
            }
            return device;
        }
    }

    private void extractMappings(List<Mapping> mappings) {
        List<Element> mappingElements = myElement.getChildren("Mapping");
        for (Element mappingElement : mappingElements) {
            Mapping mapping = myFactory.createMapping();
            mapping.getApplicationFBReference().setFQName(mappingElement.getAttributeValue("From"));
            mapping.getResourceFBReference().setFQName(mappingElement.getAttributeValue("To"));

            mappings.add(mapping);
        }
    }


    private void extractSegments(List<SegmentDeclaration> segments) {
        List<Element> segmentElements = myElement.getChildren("Segment");
        for (Element segmentElement : segmentElements) {
            segments.add(new SegmentConverter(with(segmentElement)).extract());
        }
    }

    private static class SegmentConverter extends DeclarationConverterBase<SegmentDeclaration> {

        public SegmentConverter(ConverterArguments arguments) {
            super(arguments);
        }

        @Override
        protected SegmentDeclaration extractDeclarationBody(@Nullable Identifier identifier) {
            SegmentDeclaration segment = myFactory.createSegmentDeclaration(identifier);
            segment.getTypeReference().setTargetName(myElement.getAttributeValue("Type"));
            new ParameterAssignmentsConverter(this, segment.getParameters()).extractParameters();
            return segment;
        }
    }

    private void extractLinks(List<Link> links) {
        List<Element> linkElements = myElement.getChildren("Link");
        for (Element linkElement : linkElements) {
            Link link = myFactory.createLink();
            new ParameterAssignmentsConverter(this, link.getParameters()).extractParameters();
            link.getResourceReference().setFQName(linkElement.getAttributeValue("CommResource"));
            link.getSegmentReference().setTargetName(linkElement.getAttributeValue("SegmentName"));

            links.add(link);
        }
    }
}
