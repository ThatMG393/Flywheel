package com.jozufozu.flywheel.core.vertex;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import com.jozufozu.flywheel.api.vertex.VertexList;
import com.jozufozu.flywheel.util.RenderMath;

public class PosTexNormalWriterUnsafe extends VertexWriterUnsafe<PosTexNormalVertex> {
    // Vertex layout: 3 floats (position), 2 floats (texture), 3 bytes (normal)
    private static final int FLOAT_SIZE = 4;
    private static final int VERTEX_SIZE = (3 * FLOAT_SIZE) + (2 * FLOAT_SIZE) + 3; // 12 + 8 + 3 = 23

    // Starting pointer and calculated buffer end.
    private long ptr;
    private final long end;

    public PosTexNormalWriterUnsafe(PosTexNormalVertex type, ByteBuffer buffer) {
        super(type, buffer);
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("Buffer must be direct");
        }
        ptr = MemoryUtil.memAddress(buffer);
        end = ptr + buffer.capacity();
    }

    @Override
    public void writeVertex(VertexList list, int i) {
        if (ptr + VERTEX_SIZE > end) {
            throw new BufferOverflowException();
        }
        
        // Write position (x, y, z)
        MemoryUtil.memPutFloat(ptr, list.getX(i));
        MemoryUtil.memPutFloat(ptr + FLOAT_SIZE, list.getY(i));
        MemoryUtil.memPutFloat(ptr + FLOAT_SIZE * 2, list.getZ(i));

        // Write texture coordinates (u, v)
        MemoryUtil.memPutFloat(ptr + FLOAT_SIZE * 3, list.getU(i));
        MemoryUtil.memPutFloat(ptr + FLOAT_SIZE * 4, list.getV(i));

        // Write normals as bytes (using RenderMath.nb() conversion)
        MemoryUtil.memPutByte(ptr + FLOAT_SIZE * 5, RenderMath.nb(list.getNX(i)));
        MemoryUtil.memPutByte(ptr + FLOAT_SIZE * 5 + 1, RenderMath.nb(list.getNY(i)));
        MemoryUtil.memPutByte(ptr + FLOAT_SIZE * 5 + 2, RenderMath.nb(list.getNZ(i)));

        ptr += VERTEX_SIZE;
        advance();
    }
}
