/*
 * Copyright [2009] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.openid.message.impl;

import java.net.URL;

import edu.internet2.middleware.openid.message.AuthenticationRequest;

/**
 * AuthenticationRequestImpl.
 */
public class AuthenticationRequestImpl extends AbstractMessage implements AuthenticationRequest {

    /** Association handle. */
    private String associationHandle;

    /** Claimed ID. */
    private String claimedId;

    /** Identity. */
    private String identity;

    /** Realm. */
    private String realm;

    /** Return to URL. */
    private URL returnTo;

    /** Message mode. */
    private String mode;

    /** {@inheritDoc} */
    public String getAssociationHandle() {
        return associationHandle;
    }

    /** {@inheritDoc} */
    public String getClaimedId() {
        return claimedId;
    }

    /** {@inheritDoc} */
    public String getIdentity() {
        return identity;
    }

    /** {@inheritDoc} */
    public String getRealm() {
        return realm;
    }

    /** {@inheritDoc} */
    public URL getReturnTo() {
        return returnTo;
    }

    /** {@inheritDoc} */
    public String getMode() {
        return mode;
    }

    /**
     * Set the association handle.
     * 
     * @param newHandle the associationHandle to set
     */
    public void setAssociationHandle(String newHandle) {
        associationHandle = newHandle;
    }

    /**
     * Set the claimed Id.
     * 
     * @param newClaimedId the claimedId to set
     */
    public void setClaimedId(String newClaimedId) {
        claimedId = newClaimedId;
    }

    /**
     * Set the local identity.
     * 
     * @param newIdentity the identity to set
     */
    public void setIdentity(String newIdentity) {
        identity = newIdentity;
    }

    /**
     * Set the realm.
     * 
     * @param newRealm the realm to set
     */
    public void setRealm(String newRealm) {
        realm = newRealm;
    }

    /**
     * Set the return to address.
     * 
     * @param newReturnTo the returnTo to set
     */
    public void setReturnTo(URL newReturnTo) {
        returnTo = newReturnTo;
    }

    /**
     * Set the mode.
     * 
     * @param newMode the mode to set
     */
    public void setMode(String newMode) {
        mode = newMode;
    }

}