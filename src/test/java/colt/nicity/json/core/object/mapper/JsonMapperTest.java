/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package colt.nicity.json.core.object.mapper;

import colt.nicity.json.core.Jo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class JsonMapperTest {

    public JsonMapperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of toJo method, of class JsonMapper.
     */
    @Test
    public void testToFroJo() {
        System.out.println("toJo");
        A a = new A();
        a.i = 10;
        a.l = 10L;
        a.d = 10D;
        a.a = "a";

        B b = new B();
        b.i = 100;
        b.l = 100L;
        b.d = 100D;
        b.b = "b";

        a.nested = b;
        
        Jo jo = JsonMapper.toJo(a);
        //assertEquals(expResult, result);
        System.out.println(jo);

        A a2 = JsonMapper.fromJo(jo, A.class);
        Jo jo2 = JsonMapper.toJo(a2);
        System.out.println(jo2);

        assertTrue(jo.toString().equals(jo2.toString()));
    }


    static class A {
        public Integer i;
        public Integer[] iarray = new Integer[]{1,2,3};
        public Long l;
        public Long[] larray = new Long[]{1l,2l,3l};
        public Double d;
        public Double[] darray = new Double[]{1d,2d,3d};
        public String a;
        public String[] sarray = new String[]{"a","b","c"};
        public B nested;
    }

    static class B {
        public Integer i;
        public Integer[] iarray = new Integer[]{1,2,3};
        public Long l;
        public Long[] larray = new Long[]{1l,2l,3l};
        public Double d;
        public Double[] darray = new Double[]{1d,2d,3d};
        public String b;
        public String[] sarray = new String[]{"a","b","c"};
        
    }
}