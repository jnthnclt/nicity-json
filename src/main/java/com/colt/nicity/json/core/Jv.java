/*
 * Jv.java.java
 *
 * Created on 12-27-2009 03:21:00 PM
 *
 * Copyright 2009 Jonathan Colt
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
package com.colt.nicity.json.core;

import com.colt.nicity.core.lang.UBase64;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;

/**
 *
 * @author Administrator
 */
public final class Jv extends AJ {

    static final Class<?>[] PRIMITIVE_TYPES = {int.class, long.class, short.class,
        float.class, double.class, byte.class, boolean.class, char.class, Integer.class, Long.class,
        Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};

    private Object value;

    /**
     *
     * @param bool
     */
    public Jv(Boolean bool) {
        this.value = bool;
    }

    /**
     *
     * @param number
     */
    public Jv(Number number) {
        this.value = number;
    }

    /**
     *
     * @param string
     */
    public Jv(String string) {
        this.value = string;
    }

    /**
     *
     * @param c
     */
    public Jv(Character c) {
        this.value = String.valueOf(c);
    }

    /**
     *
     * @param bytes
     */
    public Jv(byte[] bytes) {
        this.value = bytes;
    }

    Jv(Object primitive) {
        setValue(primitive);
    }

    void setValue(Object primitive) {
        if (primitive instanceof Character) {
            // convert characters to strings since in JSON, characters are represented as a single
            // character string
            char c = ((Character) primitive).charValue();
            this.value = String.valueOf(c);
        }
        else {
            checkArgument(primitive instanceof Number || isPrimitiveOrString(primitive));
            this.value = primitive;
        }
    }

    @Override
    byte[] getAsBytes() {
        if (this.value instanceof String) {
            return UBase64.decode(getAsString());
        }
        else if (this.value instanceof byte[]) {
            return (byte[]) value;
        }
        else {
            return null;//!! thow exception?
        }
    }

    boolean isBoolean() {
        return value instanceof Boolean;
    }

    @Override
    boolean getAsBoolean() {
        if (isBoolean()) {
            return ((Boolean) value).booleanValue();
        }
        else {
            return Boolean.parseBoolean(getAsString());
        }
    }

    boolean isNumber() {
        return value instanceof Number;
    }

    @Override
    Number getAsNumber() {
        return (Number) value;
    }

    boolean isString() {
        return value instanceof String;
    }

    @Override
    String getAsString() {
        if (isNumber()) {
            return getAsNumber().toString();
        }
        else if (isBoolean()) {
            return ((Boolean) value).toString();
        }
        else {
            return (String) value;
        }
    }

    @Override
    double getAsDouble() {
        if (isNumber()) {
            return getAsNumber().doubleValue();
        }
        else {
            return Double.parseDouble(getAsString());
        }
    }

    @Override
    BigDecimal getAsBigDecimal() {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        else {
            return new BigDecimal(value.toString());
        }
    }

    @Override
    BigInteger getAsBigInteger() {
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        else {
            return new BigInteger(value.toString());
        }
    }

    @Override
    float getAsFloat() {
        if (isNumber()) {
            return getAsNumber().floatValue();
        }
        else {
            return Float.parseFloat(getAsString());
        }
    }
    @Override
    long getAsLong() {
        if (isNumber()) {
            return getAsNumber().longValue();
        }
        else {
            return Long.parseLong(getAsString());
        }
    }
    @Override
    short getAsShort() {
        if (isNumber()) {
            return getAsNumber().shortValue();
        }
        else {
            return Short.parseShort(getAsString());
        }
    }
    @Override
    int getAsInt() {
        if (isNumber()) {
            return getAsNumber().intValue();
        }
        else {
            return Integer.parseInt(getAsString());
        }
    }
    @Override
    byte getAsByte() {
        if (isNumber()) {
            return getAsNumber().byteValue();
        }
        else {
            return Byte.parseByte(getAsString());
        }
    }
    @Override
    char getAsCharacter() {
        return getAsString().charAt(0);
    }

    /**
     *
     * @param sb
     * @throws IOException
     */
    @Override
    public void toString(AStringWriter sb) throws IOException {
        if (this.value instanceof String) {
            sb.value("\"" + (String) this.value + "\"");
        }
        else if (this.value instanceof byte[]) {
            sb.value(CharBuffer.wrap(UBase64.encode((byte[])this.value)));
        }
        else {
            sb.value(value.toString());
        }
    }

    /**
     *
     * @param bw
     * @throws IOException
     */
    protected void toBinary(ABinaryWriter bw) throws IOException {
        bw.appendValue(value);
    }

    private static boolean isPrimitiveOrString(Object target) {
        if (target instanceof String) {
            return true;
        }

        Class<?> classOfPrimitive = target.getClass();
        for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param obj
     */
    public static void checkNotNull(Object obj) {
        checkArgument(obj != null);
    }

    /**
     *
     * @param condition
     */
    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("condition failed: " + condition);
        }
    }

    /**
     *
     * @param condition
     */
    public static void checkState(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("condition failed: " + condition);
        }
    }
}
