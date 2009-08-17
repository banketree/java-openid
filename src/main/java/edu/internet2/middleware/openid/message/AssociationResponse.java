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

package edu.internet2.middleware.openid.message;

import java.security.Key;
import java.security.PublicKey;

import edu.internet2.middleware.openid.association.Association.AssociationType;
import edu.internet2.middleware.openid.association.Association.SessionType;

/**
 * Successful response to an association request.
 */
public interface AssociationResponse extends Message {

    /**
     * The association handle is used as a key to refer to this association in subsequent messages.
     * 
     * @return the association handle.
     */
    public String getAssociationHandle();

    /**
     * The session encryption method for this association.
     * 
     * @return the session type
     */
    public SessionType getSessionType();

    /**
     * The associate type algorithm for this association.
     * 
     * @return the association type
     */
    public AssociationType getAssociationType();

    /**
     * The lifetime, in seconds, of this association.
     * 
     * @return the lifetime
     */
    public int getLifetime();

    /**
     * The MAC key (shared secret) for this association. Depending on the session type, this may be the plain-text MAC
     * key or encrypted with the secret Diffie-Hellman value.
     * 
     * @return the MAC key
     */
    public Key getMacKey();

    /**
     * The OpenID Provider's Diffie-Hellman public key.
     * 
     * @return the DH server key
     */
    public PublicKey getDHServerPublic();

}