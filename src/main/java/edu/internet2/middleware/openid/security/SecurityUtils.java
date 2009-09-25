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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;
import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.openid.Configuration;
import edu.internet2.middleware.openid.common.ParameterMap;
import edu.internet2.middleware.openid.common.OpenIDConstants.Parameter;
import edu.internet2.middleware.openid.message.SignableMessage;
import edu.internet2.middleware.openid.message.encoding.EncodingException;
import edu.internet2.middleware.openid.message.encoding.impl.KeyValueFormCodec;
import edu.internet2.middleware.openid.message.io.Marshaller;
import edu.internet2.middleware.openid.message.io.MarshallingException;

/**
 * Security utilities.
 */
public final class SecurityUtils {

    /** Logger. */
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    /** Constructor. */
    private SecurityUtils() {
    }

    /**
     * Sign the OpenID message using the specified assertion. All available message parameters and namespace
     * declarations, including those provided by message extensions, will be used to calculate the signature. This
     * method will populate both the signature value and list of signed fields for the message. Once a message has been
     * signed, it should not be modified any further.
     * 
     * @param message message to sign
     * @param association association used to generate signature
     * @throws SecurityException if unable to sign the message
     */
    public static void signMessage(SignableMessage message, Association association) throws SecurityException {
        ParameterMap messageParameters;
        try {
            Marshaller marshaller = Configuration.getMarshallers().getMarshaller(message);
            messageParameters = marshaller.marshall(message);
        } catch (MarshallingException e) {
            log.error("Unable to sign message - " + e.getMessage());
            throw new SecurityException("Unable to sign message", e);
        }

        // Build list of message parameters to include in signature
        List<QName> signedParameters = new ArrayList();
        for (String nsURI : messageParameters.getNamespaces().getURIs()) {
            QName nsQName = new QName(nsURI, "", messageParameters.getNamespaces().getAlias(nsURI));
            signedParameters.add(nsQName);
        }
        signedParameters.addAll(messageParameters.keySet());
        signedParameters.removeAll(Arrays.asList(new QName[] { Parameter.sig.QNAME, Parameter.signed.QNAME, }));

        log.info("signable parameters = {}", signedParameters);

        String signatureData = buildSignatureData(messageParameters, signedParameters);
        String signature = calculateSignature(association, signatureData);

        message.getSignedFields().addAll(signedParameters);
        message.setSignature(signature);
    }

    /**
     * Verify that the signature on an OpenID message is valid using the specified association.
     * 
     * @param message message to verify signature for
     * @param association association used to verify signature
     * @return true if the signature is valid, false if it is not
     * @throws SecurityException if unable to validate the signature
     */
    public static boolean signatureIsValid(SignableMessage message, Association association) throws SecurityException {
        ParameterMap messageParameters;
        try {
            Marshaller marshaller = Configuration.getMarshallers().getMarshaller(message);
            messageParameters = marshaller.marshall(message);
        } catch (MarshallingException e) {
            log.error("Unable verify message signature - " + e.getMessage());
            throw new SecurityException("Unable to verify message signature", e);
        }

        String signatureData = buildSignatureData(messageParameters, message.getSignedFields());
        String signature = calculateSignature(association, signatureData);
        return signature.equals(message.getSignature());
    }

    /**
     * Build the Key-Value Form encoded string of parameters used to calculate a signature. The result of this method is
     * suitable for passing to {@link #calculateSignature(Association, String)}.
     * 
     * @param parameters message parameter map
     * @param signedFields list of fields to include in the signature data
     * @return Key-Value Form encoded string of parameters
     * @throws SecurityException if unable to build the signature data
     */
    public static String buildSignatureData(ParameterMap parameters, List<QName> signedFields) throws SecurityException {
        log.debug("building signature data with parameters: {}", signedFields);
        ParameterMap signedParameters = new ParameterMap();

        for (QName field : signedFields) {
            signedParameters.put(field, parameters.get(field));
        }

        try {
            return KeyValueFormCodec.getInstance().encode(signedParameters);
        } catch (EncodingException e) {
            log.error("Unable to sign data - " + e.getMessage());
            throw new SecurityException("Unable to sign data", e);
        }
    }

    /**
     * Calculate signature for specified data using an Association.
     * 
     * @param association association
     * @param data data to calculate signature for
     * @return calculated signature
     * @throws SecurityException if unable to calculate the signature
     */
    public static String calculateSignature(Association association, String data) throws SecurityException {
        try {
            Mac mac = Mac.getInstance(association.getMacKey().getAlgorithm());
            mac.init(association.getMacKey());

            byte[] rawHmac = mac.doFinal(data.getBytes());
            return new String(Base64.encodeBase64(rawHmac));
        } catch (InvalidKeyException e) {
            log.error("Unable to generate MAC - " + e.getMessage());
            throw new SecurityException("Unable to generate MAC", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Unable to generate MAC - " + e.getMessage());
            throw new SecurityException("Unable to generate MAC", e);
        }
    }

}