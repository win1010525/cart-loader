// Copyright 2024 Guilherme Calé <guicale@posteo.net>

// This file is part of CartLoader.

// CartLoader is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// CartLoader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.

package net.guicale.cartloader.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class CartLoaderMixin {

    final ChunkTicketType<Integer> minecartChunkTicketType = ChunkTicketType.create("minecart", Integer::compareTo, 1200);
    public ChunkPos chunkPos, oldChunkPos;
    public int minecartId;
    @Inject(at = @At("RETURN"), method = "moveOnRail")
    private void loadChunks(ServerWorld world, CallbackInfo ci) {
        minecartId = ((AbstractMinecartEntity) (Object) this).getId();
        oldChunkPos = chunkPos;
        chunkPos = ((AbstractMinecartEntity) (Object) this).getChunkPos();
        if (oldChunkPos == null) {
            oldChunkPos = chunkPos;
        }
        ServerWorld world = (ServerWorld) ((AbstractMinecartEntity) (Object) this).getWorld();
        if (world.isClient) {
            return;
        }
        if (world.getPlayers().size() == 0) {
            world.resetIdleTimeout();
        }
        ServerChunkManager chunkManager = world.getChunkManager();
        if (!((AbstractMinecartEntity)(Object)this).hasPlayerRider() & !oldChunkPos.equals(chunkPos)) {
            chunkManager.addTicket(minecartChunkTicketType,
                    chunkPos,
                    2,
                    minecartId
            );
        }
    }
}
