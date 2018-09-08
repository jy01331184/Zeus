package com.zeus.core.size;

import com.zeus.ex.MethodSizeCase;
import com.zeus.ex.UnsafeProxy;
import com.zeus.ex.ZeusException;

/**
 * Created by tianyang on 18/5/24.
 */
public class ZeusMethodStructDalvik4_0 implements IZeusMethodStruct {
    
    private int methodSize = 0;
    private int directMethodOffset = 0;
    private int virtualMethodOffset = 0;
    private int superClassOffset;
    private int declaringClassOffset;


    public ZeusMethodStructDalvik4_0() {

        try {
            int declaringClassAddr = (int) UnsafeProxy.getObjectAddress(MethodSizeCase.class);

            for (int i = 0; i <= 100; i++) {
                int val = UnsafeProxy.getIntVolatile(declaringClassAddr + i * 4);
                if (val == 5) {
                    directMethodOffset = i + 1;
                } else if (val == 2) {
                    virtualMethodOffset = i + 1;
                }
            }
            int directMethodAddr = UnsafeProxy.getIntVolatile(MethodSizeCase.class, directMethodOffset * 4);

            int count = 0;
            int elementCount = 0;
            //通过扫面内存来确认Method的结构体大小
            for (int i = 0; i < 100; i++) {
                int value = UnsafeProxy.getIntVolatile(directMethodAddr + i * 4);
                if (value == declaringClassAddr) {
                    count++;
                }
                if (count == 2) {
                    break;
                }
                if (count == 1) {
                    elementCount++;
                }
            }

            methodSize = 4 * elementCount;

            long objectClassAddr = UnsafeProxy.getObjectAddress(Object.class);

            for (int i = 0; i < 20; i++) {
                int val = UnsafeProxy.getIntVolatile(declaringClassAddr + i * 4);
                if (val == objectClassAddr) {
                    superClassOffset = i * 4;
                    break;
                }
            }

            System.out.println("ZeusMethodStructDalvik4_0:init methodSize:" + methodSize + " declaringClassOffset:" + declaringClassOffset + "," + "superClassOffset:" + superClassOffset + ",directMethodOffset:" + directMethodOffset + ",virtualMethodOffset:" + virtualMethodOffset);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int methodSize() throws Exception {
        if (methodSize <= 0) {
            throw new ZeusException("ZeusMethodStructDalvik4_0 methodSize not correct:" + methodSize);
        }

        return methodSize;
    }

    @Override
    public int methodIndexOffset() throws Exception {
        return 0;
    }

    @Override
    public int declaringClassOffset() throws Exception {
        return declaringClassOffset;
    }

    @Override
    public int superClassOffset() throws Exception {
        return superClassOffset;
    }

    public int getDirectMethodOffset() throws Exception {
        if (directMethodOffset <= 0) {
            throw new ZeusException("ZeusMethodStructDalvik4_0 directMethodOffset not correct:" + methodSize);
        }

        return directMethodOffset;
    }

    public int getVirtualMethodOffset() throws Exception {
        if (virtualMethodOffset <= 0) {
            throw new ZeusException("ZeusMethodStructDalvik4_0 virtualMethodOffset not correct:" + methodSize);
        }

        return virtualMethodOffset;
    }
}
