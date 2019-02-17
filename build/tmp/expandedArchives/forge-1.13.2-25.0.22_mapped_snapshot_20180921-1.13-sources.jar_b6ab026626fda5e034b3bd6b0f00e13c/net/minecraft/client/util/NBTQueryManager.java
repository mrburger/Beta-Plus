package net.minecraft.client.util;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketNBTQueryEntity;
import net.minecraft.network.play.client.CPacketNBTQueryTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NBTQueryManager {
   private final NetHandlerPlayClient connection;
   private int transactionId = -1;
   @Nullable
   private Consumer<NBTTagCompound> handler;

   public NBTQueryManager(NetHandlerPlayClient p_i49773_1_) {
      this.connection = p_i49773_1_;
   }

   public boolean handleResponse(int p_211548_1_, @Nullable NBTTagCompound p_211548_2_) {
      if (this.transactionId == p_211548_1_ && this.handler != null) {
         this.handler.accept(p_211548_2_);
         this.handler = null;
         return true;
      } else {
         return false;
      }
   }

   private int setHandler(Consumer<NBTTagCompound> p_211546_1_) {
      this.handler = p_211546_1_;
      return ++this.transactionId;
   }

   public void queryEntity(int entId, Consumer<NBTTagCompound> p_211549_2_) {
      int i = this.setHandler(p_211549_2_);
      this.connection.sendPacket(new CPacketNBTQueryEntity(i, entId));
   }

   public void queryTileEntity(BlockPos p_211547_1_, Consumer<NBTTagCompound> p_211547_2_) {
      int i = this.setHandler(p_211547_2_);
      this.connection.sendPacket(new CPacketNBTQueryTileEntity(i, p_211547_1_));
   }
}