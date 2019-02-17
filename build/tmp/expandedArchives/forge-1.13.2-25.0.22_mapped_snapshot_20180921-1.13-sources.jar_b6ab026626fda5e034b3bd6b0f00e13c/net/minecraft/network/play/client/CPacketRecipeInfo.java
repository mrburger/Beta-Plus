package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketRecipeInfo implements Packet<INetHandlerPlayServer> {
   private CPacketRecipeInfo.Purpose purpose;
   private ResourceLocation recipe;
   private boolean isGuiOpen;
   private boolean filteringCraftable;
   private boolean field_202498_e;
   private boolean field_202499_f;

   public CPacketRecipeInfo() {
   }

   public CPacketRecipeInfo(IRecipe p_i47518_1_) {
      this.purpose = CPacketRecipeInfo.Purpose.SHOWN;
      this.recipe = p_i47518_1_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketRecipeInfo(boolean p_i48734_1_, boolean p_i48734_2_, boolean p_i48734_3_, boolean p_i48734_4_) {
      this.purpose = CPacketRecipeInfo.Purpose.SETTINGS;
      this.isGuiOpen = p_i48734_1_;
      this.filteringCraftable = p_i48734_2_;
      this.field_202498_e = p_i48734_3_;
      this.field_202499_f = p_i48734_4_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.purpose = buf.readEnumValue(CPacketRecipeInfo.Purpose.class);
      if (this.purpose == CPacketRecipeInfo.Purpose.SHOWN) {
         this.recipe = buf.readResourceLocation();
      } else if (this.purpose == CPacketRecipeInfo.Purpose.SETTINGS) {
         this.isGuiOpen = buf.readBoolean();
         this.filteringCraftable = buf.readBoolean();
         this.field_202498_e = buf.readBoolean();
         this.field_202499_f = buf.readBoolean();
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.purpose);
      if (this.purpose == CPacketRecipeInfo.Purpose.SHOWN) {
         buf.writeResourceLocation(this.recipe);
      } else if (this.purpose == CPacketRecipeInfo.Purpose.SETTINGS) {
         buf.writeBoolean(this.isGuiOpen);
         buf.writeBoolean(this.filteringCraftable);
         buf.writeBoolean(this.field_202498_e);
         buf.writeBoolean(this.field_202499_f);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayServer handler) {
      handler.handleRecipeBookUpdate(this);
   }

   public CPacketRecipeInfo.Purpose getPurpose() {
      return this.purpose;
   }

   public ResourceLocation func_199619_b() {
      return this.recipe;
   }

   public boolean isGuiOpen() {
      return this.isGuiOpen;
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public boolean func_202496_e() {
      return this.field_202498_e;
   }

   public boolean func_202497_f() {
      return this.field_202499_f;
   }

   public static enum Purpose {
      SHOWN,
      SETTINGS;
   }
}