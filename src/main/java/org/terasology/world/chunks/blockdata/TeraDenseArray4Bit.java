package org.terasology.world.chunks.blockdata;

import org.terasology.world.chunks.deflate.TeraVisitingDeflator;

import com.google.common.base.Preconditions;


/**
 * TeraDenseArray4Bit implements a dense array with elements of 4 bit size.
 * Its elements are in the range 0 - 15 and it increases memory efficiency by storing two elements per byte.
 * 
 * @author Manuel Brotz <manu.brotz@gmx.ch>
 *
 */
public final class TeraDenseArray4Bit extends TeraDenseArrayByte {

    @Override
    protected final TeraArray createDense(byte[] data) {
        return new TeraDenseArray4Bit(getSizeX(), getSizeY(), getSizeZ(), data);
    }

    @Override
    protected final int rowSize() {
        return getSizeXZHalf();
    }

    public static class SerializationHandler extends TeraDenseArrayByte.SerializationHandler<TeraDenseArray4Bit> {

        @Override
        public boolean canHandle(Class<?> clazz) {
            return TeraDenseArray4Bit.class.equals(clazz);
        }

        @Override
        protected TeraDenseArray4Bit createArray(int sizeX, int sizeY, int sizeZ, byte[] data) {
            if (data == null) 
                return new TeraDenseArray4Bit(sizeX, sizeY, sizeZ);
            else
                return new TeraDenseArray4Bit(sizeX, sizeY, sizeZ, data);
        }
    }
    
    public static class Factory implements TeraArray.Factory<TeraDenseArray4Bit> {
        
        @Override
        public Class<TeraDenseArray4Bit> getArrayClass() {
            return TeraDenseArray4Bit.class;
        }

        @Override
        public SerializationHandler createSerializationHandler() {
            return new SerializationHandler();
        }
        
        @Override
        public TeraDenseArray4Bit create() {
            return new TeraDenseArray4Bit();
        }
        
        @Override
        public TeraDenseArray4Bit create(int sizeX, int sizeY, int sizeZ) {
            return new TeraDenseArray4Bit(sizeX, sizeY, sizeZ);
        }
    }
    
    public TeraDenseArray4Bit() {
        super();
    }

    public TeraDenseArray4Bit(int sizeX, int sizeY, int sizeZ) {
        super(sizeX, sizeY, sizeZ);
    }

    public TeraDenseArray4Bit(int sizeX, int sizeY, int sizeZ, byte[] data) {
        super(sizeX, sizeY, sizeZ, data);
    }

    public TeraDenseArray4Bit(TeraArray in) {
        super(in);
    }

    @Override
    public TeraArray deflate(TeraVisitingDeflator deflator) {
        return Preconditions.checkNotNull(deflator).deflateDenseArray4Bit(data, rowSize(), getSizeX(), getSizeY(), getSizeZ());
    }
    
    @Override
    public int getElementSizeInBits() {
        return 4;
    }
    
    @Override
    public final int get(int x, int y, int z) {
//        if (!contains(x, y, z)) throw new IndexOutOfBoundsException("Index out of bounds (" + x + ", " + y + ", " + z + ")");
        int row = y * getSizeXZHalf(), pos = pos(x, z);
        if (pos < getSizeXZHalf()) {
            return TeraArrayUtils.getHi(data[row + pos]);
        }
        pos = pos - getSizeXZHalf();
        return TeraArrayUtils.getLo(data[row + pos]);
    }

    @Override
    public final int set(int x, int y, int z, int value) {
//        if (!contains(x, y, z)) throw new IndexOutOfBoundsException("Index out of bounds (" + x + ", " + y + ", " + z + ")");
//        if (value < 0 || value > 15) throw new IllegalArgumentException("Parameter 'value' has to be in the range 0 - 15 (" + value + ")");
        int row = y * getSizeXZHalf(), pos = pos(x, z);
        if (pos < getSizeXZHalf()) {
            byte raw = data[row + pos];
            byte old = TeraArrayUtils.getHi(raw);
            data[row + pos] = TeraArrayUtils.setHi(raw, value);
            return old;
        }
        pos = pos - getSizeXZHalf();
        byte raw = data[row + pos];
        byte old = TeraArrayUtils.getLo(raw);
        data[row + pos] = TeraArrayUtils.setLo(raw, value);
        return old;
    }

    @Override
    public final boolean set(int x, int y, int z, int value, int expected) {
//        if (!contains(x, y, z)) throw new IndexOutOfBoundsException("Index out of bounds (" + x + ", " + y + ", " + z + ")");
//        if (value < 0 || value > 15) throw new IllegalArgumentException("Parameter 'value' has to be in the range 0 - 15 (" + value + ")");
//        if (expected < 0 || expected > 15) throw new IllegalArgumentException("Parameter 'expected' has to be in the range 0 - 15 (" + value + ")");
        int row = y * getSizeXZHalf(), pos = pos(x, z);
        if (pos < getSizeXZHalf()) {
            byte raw = data[row + pos];
            byte old = TeraArrayUtils.getHi(raw);
            if (old == expected) {
                data[row + pos] = TeraArrayUtils.setHi(raw, value);
                return true;
            }
            return false;
        }
        pos = pos - getSizeXZHalf();
        byte raw = data[row + pos];
        byte old = TeraArrayUtils.getLo(raw);
        if (old == expected) {
            data[row + pos] = TeraArrayUtils.setLo(raw, value);
            return true;
        }
        return false;
    }

}
