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

package edu.internet2.middleware.openid.message.encoding.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.openid.common.OpenIDConstants;
import edu.internet2.middleware.openid.message.ParameterMap;
import edu.internet2.middleware.openid.message.encoding.EncodingException;
import edu.internet2.middleware.openid.message.encoding.MessageCodec;

/**
 * Abstract class for namespace-aware codecs.
 * 
 * @param <EncodedType> object type the codec produces
 */
public abstract class AbstractNamespaceAwareCodec<EncodedType> implements MessageCodec<EncodedType> {

    /** Prefix attached to each namespace alias declarations. */
    private static final String NAMESPACE_PREFIX = "ns";

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(URLFormCodec.class);

    /** {@inheritDoc} */
    public ParameterMap decode(EncodedType encoded) throws EncodingException {
        Map<String, String> parameters = decodeMessage(encoded);
        Map<String, String> namespaces = new HashMap<String, String>();

        // build namespace map
        Iterator<String> keyIterator = parameters.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            String value = parameters.get(key);

            if (key.equals(NAMESPACE_PREFIX)) {
                // ns declaration (default namespace)
                log.debug("Registering default namespace: {}", value);
                namespaces.put(null, value);
                keyIterator.remove();
            } else if (key.startsWith(NAMESPACE_PREFIX + ".")) {
                // ns.X declaration (extension namespace)
                key = key.substring(NAMESPACE_PREFIX.length() + 1);
                log.debug("Registering '{}' namespace: {}", key, value);
                namespaces.put(key, value);
                keyIterator.remove();
            }
        }

        // build ParameterMap
        ParameterMap parameterMap = new ParameterMap();
        for (String key : parameters.keySet()) {
            QName qname;

            String[] parts = key.split("\\.", 2);
            if (parts.length == 2) {
                qname = new QName(namespaces.get(parts[0]), parts[1], parts[0]);
            } else {
                // default namespace
                qname = new QName(namespaces.get(null), parts[0]);
            }

            parameterMap.put(qname, parameters.get(key));
        }

        return parameterMap;
    }

    /**
     * Decode message into a simple key-value map of parameters.
     * 
     * @param encoded encoded message
     * @return map of parameters
     * @throws EncodingException if unable to decode the message
     */
    public abstract Map<String, String> decodeMessage(EncodedType encoded) throws EncodingException;

    /** {@inheritDoc} */
    public EncodedType encode(ParameterMap parameterMap) throws EncodingException {
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> namespaces = new HashMap<String, String>();

        // build namespace map
        for (QName key : parameterMap.keySet()) {
            String ns = key.getNamespaceURI();

            if (!namespaces.containsKey(ns)) {
                String alias;

                if (OpenIDConstants.OPENID_20_NS.equals(ns)) {
                    alias = null;
                } else if (key.getPrefix() != null) {
                    alias = key.getPrefix();
                } else {
                    alias = "n" + namespaces.size();
                }

                log.debug("Registering namespace alias {} = {}", alias, ns);
                namespaces.put(ns, alias);
            }
        }

        // add namespaces to key-value parameter map
        for (String ns : namespaces.keySet()) {
            String key = NAMESPACE_PREFIX;
            String alias = namespaces.get(ns);
            if (alias != null) {
                key += "." + alias;
            }
            parameters.put(key, ns);
        }

        // add parameters to key-value parameter map
        for (QName qname : parameterMap.keySet()) {
            String key = qname.getLocalPart();
            String ns = namespaces.get(qname.getNamespaceURI());
            if (ns != null) {
                key = ns + "." + key;
            }
            parameters.put(key, parameterMap.get(qname));
        }

        return encodeMessage(parameters);
    }

    /**
     * Encode a simple key-value map of parameters.
     * 
     * @param parameters map of parameters
     * @return encoded message
     * @throws EncodingException if unable to encode message
     */
    public abstract EncodedType encodeMessage(Map<String, String> parameters) throws EncodingException;

}