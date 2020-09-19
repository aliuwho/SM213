package arch.sm213.machine.student;

import machine.AbstractMainMemory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MainMemoryTest {
    public MainMemory mem;

    public static byte[] BYTES1;

    public static byte[] BYTES2;

    @Before
    public void before() {
        mem = new MainMemory(32);
        BYTES1 = new byte[4];
        byte init1 = (byte) 0xa0;
        for (int i = 0; i < BYTES1.length; i++) {
            BYTES1[i] = (byte) (init1 + (byte) i);
        }

        BYTES2 = new byte[15];
        byte init2 = (byte) 0x69;
        for (int i = 0; i < BYTES2.length; i++) {
            BYTES2[i] = (byte) (init2 + (byte) i);
        }
    }

    @Test
    //check cases where the address is aligned
    public void testIsAligned() {
        Assert.assertTrue(mem.isAccessAligned(0, 8));
        Assert.assertTrue(mem.isAccessAligned(4, 2));
        Assert.assertTrue(mem.isAccessAligned(1046, 2));
    }

    @Test
    // check cases where the address is not aligned
    public void testIsNotAligned() {
        Assert.assertFalse(mem.isAccessAligned(1, 8));
        Assert.assertFalse(mem.isAccessAligned(5, 10));
        Assert.assertFalse(mem.isAccessAligned(5, 2));
    }

    @Test
    // test get() with valid addresses and lengths
    // note: these tests ensure that get() works regardless of whether set() works
    //       so we can use get() to ensure set works in later tests
    public void testGetValid() {
        try {
            byte[] test = mem.get(0, 1);
            Assert.assertEquals(1, test.length);
            Assert.assertEquals(0, test[0]);
        } catch (AbstractMainMemory.InvalidAddressException e) {
            fail();
        }

        try {
            byte[] test = mem.get(0, mem.length() - 1);
            Assert.assertEquals(mem.length() - 1, test.length);
            for (byte b : test) {
                Assert.assertEquals(0, b);
                //since right now, all values are at default
            }
        } catch (AbstractMainMemory.InvalidAddressException e) {
            fail();
        }
    }

    @Test
    // testing get() with invalid addresses to test InvalidAddressException
    public void testGetInvalidAddress() {
        try {
            mem.get(-1, 1);
        } catch (AbstractMainMemory.InvalidAddressException e) {
            //should throw an exception
        }

        try {
            mem.get(mem.length(), 1);
        } catch (AbstractMainMemory.InvalidAddressException e) {
            //should throw exception
        }
    }

    @Test
    // test set() with valid addresses
    // note: we will use get() to test set() is working properly
    public void testSetValid() {
        try {
            mem.set((byte) 0, BYTES1);
            byte[] test = mem.get((byte) 0, BYTES1.length);
            for (int i = 0; i < test.length; i++) {
                assertEquals(BYTES1[i], test[i]);
            }
        } catch (AbstractMainMemory.InvalidAddressException e) {
            fail();
        }

        try {
            mem.set((byte) 0, BYTES2);
            byte[] test = mem.get((byte) 0, BYTES2.length);
            for (int i = 0; i < test.length; i++) {
                assertEquals(BYTES2[i], test[i]);
            }
        } catch (AbstractMainMemory.InvalidAddressException e) {
            fail();
        }

    }

    @Test
    //test set() with invalid addresses to test InvalidAddressException
    public void testSetInvalid() {
        try {
            mem.set((byte) -1, BYTES1);
        } catch (AbstractMainMemory.InvalidAddressException e) {
            //should catch
        }

        try {
            mem.set((byte) mem.length(), BYTES2);
        } catch (AbstractMainMemory.InvalidAddressException e) {
            //should catch
        }
    }

    @Test
    //test bytesToInteger() on non-negative signed integers
    public void testBytesToIntegerPos() {
        // hex input: 00 00 00 00
        int val1 = mem.bytesToInteger((byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0);
        Assert.assertEquals(0, val1);

        // hex input: 6c 6d 6e 6f
        int val2 = mem.bytesToInteger(BYTES2[3], BYTES2[4], BYTES2[5], BYTES2[6]);
        Assert.assertEquals(1819111023, val2);

    }

    @Test
    //test bytesToInteger() on negative signed integers
    public void testBytesToIntegerNeg() {
        //hex input: ff ff ff 92
        int val1 = mem.bytesToInteger((byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x92);
        Assert.assertEquals(-110, val1);

        //hex input: a0 a1 a2 a3
        int val2 = mem.bytesToInteger(BYTES1[0], BYTES1[1], BYTES1[2], BYTES1[3]);
        Assert.assertEquals(-1600019805, val2);
    }
}
