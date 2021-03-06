/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.util;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

import biz.source_code.base64Coder.Base64Coder;
import com.griddynamics.jagger.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Avoid static code. Extract interface and make this one default implementation.
public class SerializationUtils {
    private static final Logger log = LoggerFactory.getLogger(SerializationUtils.class);
    private static final AtomicLong fromStringCount = new AtomicLong(0);
    private static final AtomicLong toStringCount = new AtomicLong(0);

    private SerializationUtils() {
    }

    public static <T extends Serializable> T fromString(String s) {
        if (s.isEmpty()) {
            log.info("fromString({}, '{}')", fromStringCount.getAndIncrement(), s);
        }
        try {
            byte[] data = Base64Coder.decode(s);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            T obj = (T) ois.readObject();
            ois.close();
            return obj;
        } catch (IOException e) {
            log.error("Deserialization exception ", e);
            log.error("fromString('{}')", s);
            throw new TechnicalException(e);
        } catch (ClassNotFoundException e) {
            log.error("Deserialization exception ", e);
            throw new TechnicalException(e);
        }
    }

    public static String toString(Serializable o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            log.error("Serialization exception ", e);
            throw new TechnicalException(e);
        }

        String s = new String(Base64Coder.encode(baos.toByteArray()));
        if (s.isEmpty()) {
            log.info("toString({}, '{}', '{}')", new Object[] {toStringCount.getAndIncrement(), s, o});
        }
        return s;
    }

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream ous = new ObjectOutputStream(baos);
            ous.writeObject(obj);
            ous.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error during " + obj + " serialization", e);
        }
    }

    public static Object deserialize(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object payload = ois.readObject();
            ois.close();

            return payload;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
