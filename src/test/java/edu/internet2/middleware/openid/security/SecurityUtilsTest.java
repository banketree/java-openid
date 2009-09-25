/*
 * Copyright 2009 University Corporation for Advanced Internet Development, Inc.
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

package edu.internet2.middleware.openid.security;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;

import org.opensaml.xml.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.openid.BaseTestCase;
import edu.internet2.middleware.openid.common.OpenIDConstants;
import edu.internet2.middleware.openid.common.OpenIDConstants.AssociationType;
import edu.internet2.middleware.openid.common.OpenIDConstants.Parameter;
import edu.internet2.middleware.openid.message.PositiveAssertion;
import edu.internet2.middleware.openid.security.impl.BasicAssociation;

/**
 * Security utilities tests.
 */
public class SecurityUtilsTest extends BaseTestCase {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(SecurityUtilsTest.class);

    /**
     * Test signing of data with the MAC.
     */
    public void testSignatureCreation() throws SecurityException {
        String algorithm = AssociationType.HMAC_SHA256.getAlgorithm();
        String encodedMacKey = "qMOYPeeb8PhGk8mlPUV+qBPPlFzY6xHk1AhSiRCQVsk=";
        Key macKey = new SecretKeySpec(encodedMacKey.getBytes(), algorithm);

        BasicAssociation association = new BasicAssociation();
        association.setAssociationType(AssociationType.HMAC_SHA256);
        association.setMacKey(macKey);

        String signature = SecurityUtils.calculateSignature(association, "foo");
        assertEquals("gp8cxaax9Hynh5JqM/kQ2mLdOC36fwYTrOUvQ4fX8nE=", signature);
    }

    public void testMessageSigning() throws MalformedURLException, SecurityException {
        QName qname = new QName(OpenIDConstants.OPENID_20_NS, PositiveAssertion.MODE);
        PositiveAssertion response = (PositiveAssertion) buildMessage(qname);

        Association association = getAssociation();

        response.setAssociationHandle(association.getHandle());
        response.setClaimedId("http://example.org/");
        response.setIdentity("http://example.com/username");
        response.setResponseNonce("123");
        response.setReturnTo(new URL("http://rp.example.com/consumer"));
        response.setEndpoint("http://openid.example.com/server");

        SecurityUtils.signMessage(response, association);
        logMessageParameters(response);
        assertEquals("xZKDxR9nYkbNQvxRIt6QwKyttdFX4s5Y6dvJGk3mKnU=", response.getSignature());
    }

    public void testSignatureVerification() throws MalformedURLException, SecurityException {
        String messageFile = "/data/edu/internet2/middleware/openid/security/signed-message-1.txt";
        PositiveAssertion response = (PositiveAssertion) unmarshallMessage(messageFile);
        Association association = getAssociation();

        logMessageParameters(response);
        assertTrue(SecurityUtils.signatureIsValid(response, association));
    }

    private Association getAssociation() {
        AssociationType type = AssociationType.HMAC_SHA256;
        String encodedMacKey = "hee0W816z4fMtFK4X3Y7IZPEmRo9eORfWC9QoA/d0hU=";
        SecretKey macKey = new SecretKeySpec(Base64.decode(encodedMacKey), type.getAlgorithm());
        String handle = "new-handle";

        BasicAssociation association = new BasicAssociation();
        association.setAssociationType(type);
        association.setHandle(handle);
        association.setMacKey(macKey);

        return association;
    }

}