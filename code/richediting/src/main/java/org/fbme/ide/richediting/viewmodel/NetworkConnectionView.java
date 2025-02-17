package org.fbme.ide.richediting.viewmodel;

import org.fbme.ide.iec61499.repository.PlatformElement;
import org.fbme.lib.iec61499.declarations.ParameterAssignment;
import org.fbme.lib.iec61499.fbnetwork.ConnectionPath;
import org.fbme.lib.iec61499.fbnetwork.EntryKind;
import org.fbme.lib.iec61499.fbnetwork.FBNetworkConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.model.SNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NetworkConnectionView {

    @NotNull
    private final Object myConnection;
    private ConnectionPath myConnectionPath;
    private final boolean myEditable;
    private final SNode myAssociatedNode;
    private final EntryKind myKind;

    public NetworkConnectionView(@NotNull FBNetworkConnection connection, boolean editable) {
        myConnection = connection;
        myConnectionPath = connection.getPath();
        myKind = connection.getKind();
        myEditable = editable;
        myAssociatedNode = ((PlatformElement) connection).getNode();
    }

    public NetworkConnectionView(@NotNull ParameterAssignment parameter, boolean editable) {
        myConnection = parameter;
        myConnectionPath = new ConnectionPath();
        myKind = EntryKind.DATA;
        myEditable = editable;
        myAssociatedNode = ((PlatformElement) parameter).getNode();
    }

    public SNode getAssociatedNode() {
        return myAssociatedNode;
    }

    public EntryKind getKind() {
        return myKind;
    }

    public void shrink() {
        myConnectionPath = new ConnectionPath();
    }

    public void setPath(ConnectionPath path) {
        FBNetworkConnection connection = getConnection();
        if (myEditable && connection != null) {
            connection.setPath(path);
        }
    }

    @Nullable
    public FBNetworkConnection getConnection() {
        return myConnection instanceof FBNetworkConnection ? (FBNetworkConnection) myConnection : null;
    }

    public boolean isEditable() {
        return myEditable;
    }

    @NotNull
    public ConnectionPath getConnectionPath() {
        return myConnectionPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkConnectionView)) {
            return false;
        }

        NetworkConnectionView that = (NetworkConnectionView) o;
        return Objects.equals(myConnection, that.myConnection);
    }

    @Override
    public int hashCode() {
        return myConnection.hashCode();
    }
}
