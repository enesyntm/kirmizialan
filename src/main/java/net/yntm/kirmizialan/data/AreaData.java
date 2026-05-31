/* hatalı bir deneme
package net.yntm.kirmizialan.data;

import net.minecraft.util.math.BlockPos;

public class AreaData {

    private BlockPos origin;
    private int radius;

    public AreaData(BlockPos origin) {
        this.origin = origin;
        this.radius = 1; // 3x3 alan
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public int getRadius() {
        return radius;
    }

    public void expand(int amount) {
        radius += amount;
    }
}
*/