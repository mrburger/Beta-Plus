package net.minecraft.tags;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.registry.IRegistry;

public class NetworkTagManager implements IResourceManagerReloadListener {
   private final NetworkTagCollection<Block> blocks = new NetworkTagCollection<>(IRegistry.field_212618_g, "tags/blocks", "block");
   private final NetworkTagCollection<Item> items = new NetworkTagCollection<>(IRegistry.field_212630_s, "tags/items", "item");
   private final NetworkTagCollection<Fluid> fluids = new NetworkTagCollection<>(IRegistry.field_212619_h, "tags/fluids", "fluid");

   public NetworkTagCollection<Block> getBlocks() {
      return this.blocks;
   }

   public NetworkTagCollection<Item> getItems() {
      return this.items;
   }

   public NetworkTagCollection<Fluid> getFluids() {
      return this.fluids;
   }

   public void clear() {
      this.blocks.clear();
      this.items.clear();
      this.fluids.clear();
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.clear();
      this.blocks.reload(resourceManager);
      this.items.reload(resourceManager);
      this.fluids.reload(resourceManager);
      BlockTags.setCollection(this.blocks);
      ItemTags.setCollection(this.items);
      FluidTags.setCollection(this.fluids);
   }

   public void write(PacketBuffer buffer) {
      this.blocks.write(buffer);
      this.items.write(buffer);
      this.fluids.write(buffer);
   }

   public static NetworkTagManager read(PacketBuffer buffer) {
      NetworkTagManager networktagmanager = new NetworkTagManager();
      networktagmanager.getBlocks().read(buffer);
      networktagmanager.getItems().read(buffer);
      networktagmanager.getFluids().read(buffer);
      return networktagmanager;
   }
}