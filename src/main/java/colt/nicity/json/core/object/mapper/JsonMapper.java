/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colt.nicity.json.core.object.mapper;

import colt.nicity.core.collection.CSet;
import colt.nicity.core.lang.ASetObject;
import colt.nicity.json.core.Ja;
import colt.nicity.json.core.Jo;
import colt.nicity.json.core.UJson;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 *
 * @author Administrator
 */
public class JsonMapper {

    final private static CSet<JoMapper<?>> mapper = new CSet<JoMapper<?>>();

    static {
        mapper.add(new JoMapper<Integer>(Integer.class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    Integer v = (Integer) field.get(instance);
                    if (v != null) {
                        UJson.add(jo, key, v);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        field.set(instance, UJson.getInt(jo, key));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        mapper.add(new JoMapper<Integer[]>(Integer[].class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    Integer[] vs = (Integer[]) field.get(instance);
                    if (vs != null) {
                        Ja ja = new Ja();
                        UJson.add(ja, vs);
                        UJson.add(jo, key, ja);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        Ja ja = UJson.getArray(jo, key);
                        field.set(instance, UJson.getInts(ja, null));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        mapper.add(new JoMapper<Long>(Long.class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    Long v = (Long) field.get(instance);
                    if (v != null) {
                        UJson.add(jo, key, v);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        field.set(instance, UJson.getLong(jo, key));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        mapper.add(new JoMapper<Long[]>(Long[].class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    Long[] vs = (Long[]) field.get(instance);
                    if (vs != null) {
                        Ja ja = new Ja();
                        UJson.add(ja, vs);
                        UJson.add(jo, key, ja);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        Ja ja = UJson.getArray(jo, key);
                        field.set(instance, UJson.getLongs(ja, null));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        mapper.add(new JoMapper<Double>(Double.class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    Double v = (Double) field.get(instance);
                    if (v != null) {
                        UJson.add(jo, key, v);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        field.set(instance, UJson.getDouble(jo, key));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        mapper.add(new JoMapper<Double[]>(Double[].class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    Double[] vs = (Double[]) field.get(instance);
                    if (vs != null) {
                        Ja ja = new Ja();
                        UJson.add(ja, vs);
                        UJson.add(jo, key, ja);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        Ja ja = UJson.getArray(jo, key);
                        field.set(instance, UJson.getDoubles(ja, null));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        mapper.add(new JoMapper<String>(String.class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    String v = (String) field.get(instance);
                    if (v != null) {
                        UJson.add(jo, key, v);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        field.set(instance, UJson.getString(jo, key));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        mapper.add(new JoMapper<String[]>(String[].class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                String key = field.getName();
                if (tSetFGet) {
                    String[] vs = (String[]) field.get(instance);
                    if (vs != null) {
                        Ja ja = new Ja();
                        UJson.add(ja, vs);
                        UJson.add(jo, key, ja);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (UJson.has(jo, key)) {
                        Ja ja = UJson.getArray(jo, key);
                        field.set(instance, UJson.getStrings(ja, null));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });

        mapper.add(new JoMapper<Object>(Object.class) {

            @Override
            boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception {
                Class _class = instance.getClass();
                if (tSetFGet) {
                    Jo innerJo = new Jo();
                    int added = 0;
                    for (Field gotField : _class.getFields()) {
                        Class type = gotField.getType();
                        Object got = gotField.get(instance);
                        if (got != null) {
                            JoMapper joMapper = mapper.get(type);
                            if (joMapper == null) {
                                if (type.isArray()) joMapper = mapper.get(Object[].class);
                                else joMapper = mapper.get(Object.class);
                                if (joMapper.et(gotField, got, tSetFGet, innerJo)) added++;
                            } else {
                                if (joMapper.et(gotField, instance, tSetFGet, innerJo)) added++;
                            }
                        }
                    }
                    if (added > 0) {
                        UJson.add(jo, _class.getSimpleName(), innerJo);
                        return true;
                    } else{
                        return false;
                    }
                } else {
                    Jo innerJo = UJson.getObject(jo, _class.getSimpleName());
                    if (innerJo == null) {
                        return false;
                    }
                    int added = 0;
                    for (Field gotField : _class.getFields()) {
                        Class type = gotField.getType();
                        JoMapper joMapper = mapper.get(type);
                        if (joMapper == null) {
                            joMapper = mapper.get(Object.class);
                            Object innerInstance = type.newInstance();
                            if (joMapper.et(gotField, innerInstance, tSetFGet, innerJo)) {
                                gotField.set(instance, innerInstance);
                                added++;
                            }
                        } else {
                            if (joMapper.et(gotField, instance, tSetFGet, innerJo)) added++;
                        }
                    }
                    return added > 0;
                }
            }
        });
        
    }

    public static Jo toJo(Object object) {
        try {
            if (object == null) {
                return null;
            }
            Jo jo = new Jo();
            JoMapper joMapper = mapper.get(Object.class);
            joMapper.et(null, object, true, jo);
            return jo;
        } catch (Exception x) {
            x.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJo(Jo jo, Class<? extends T> _class) {
        try {
            if (jo == null) {
                return null;
            }
            T object = _class.newInstance();
            JoMapper joMapper = mapper.get(Object.class);
            joMapper.et(null, object, false, jo);
            return object;
        } catch (Exception x) {
            x.printStackTrace();
            return null;
        }
    }

    private static abstract class JoMapper<V> extends ASetObject<Class> {

        final private Class clazz;

        JoMapper(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public Class hashObject() {
            return clazz;
        }

        abstract boolean et(Field field, Object instance, boolean tSetFGet, Jo jo) throws Exception;
    }
}
