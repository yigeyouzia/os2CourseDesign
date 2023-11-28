package com.cyt.os.kernel.memory.data;

import com.cyt.os.enums.MemoryStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cyt
 * @date 2023/11/28
 */
@Data
@NoArgsConstructor
public class MemoryBlock {

    private int startAddress = 0;
    private int size = 0;
    private MemoryStatus status = MemoryStatus.FREE;
    private int id = -1;

    public MemoryBlock(int startAddress, int size) {
        this.startAddress = startAddress;
        this.size = size;
    }

    public int getLast() {
        return startAddress + size;
    }
}
