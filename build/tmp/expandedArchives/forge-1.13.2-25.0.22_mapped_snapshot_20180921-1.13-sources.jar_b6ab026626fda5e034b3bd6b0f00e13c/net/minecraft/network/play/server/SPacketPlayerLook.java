package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlayerLook implements Packet<INetHandlerPlayClient> {
   private double x;
   private double y;
   private double z;
   private int field_200535_d;
   private EntityAnchorArgument.Type field_201065_e;
   private EntityAnchorArgument.Type field_201066_f;
   private boolean field_200536_e;

   public SPacketPlayerLook() {
   }

   public SPacketPlayerLook(EntityAnchorArgument.Type p_i48589_1_, double p_i48589_2_, double p_i48589_4_, double p_i48589_6_) {
      this.field_201065_e = p_i48589_1_;
      this.x = p_i48589_2_;
      this.y = p_i48589_4_;
      this.z = p_i48589_6_;
   }

   public SPacketPlayerLook(EntityAnchorArgument.Type p_i48590_1_, Entity p_i48590_2_, EntityAnchorArgument.Type p_i48590_3_) {
      this.field_201065_e = p_i48590_1_;
      this.field_200535_d = p_i48590_2_.getEntityId();
      this.field_201066_f = p_i48590_3_;
      Vec3d vec3d = p_i48590_3_.apply(p_i48590_2_);
      this.x = vec3d.x;
      this.y = vec3d.y;
      this.z = vec3d.z;
      this.field_200536_e = true;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_201065_e = buf.readEnumValue(EntityAnchorArgument.Type.class);
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      if (buf.readBoolean()) {
         this.field_200536_e = true;
         this.field_200535_d = buf.readVarInt();
         this.field_201066_f = buf.readEnumValue(EntityAnchorArgument.Type.class);
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.field_201065_e);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeBoolean(this.field_200536_e);
      if (this.field_200536_e) {
         buf.writeVarInt(this.field_200535_d);
         buf.writeEnumValue(this.field_201066_f);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityAnchorArgument.Type func_201064_a() {
      return this.field_201065_e;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d func_200531_a(World p_200531_1_) {
      if (this.field_200536_e) {
         Entity entity = p_200531_1_.getEntityByID(this.field_200535_d);
         return entity == null ? new Vec3d(this.x, this.y, this.z) : this.field_201066_f.apply(entity);
      } else {
         return new Vec3d(this.x, this.y, this.z);
      }
   }
}