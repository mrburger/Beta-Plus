package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<BlockParticleData> DESERIALIZER = new IParticleData.IDeserializer<BlockParticleData>() {
      public BlockParticleData deserialize(ParticleType<BlockParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
         reader.expect(' ');
         return new BlockParticleData(particleTypeIn, (new BlockStateParser(reader, false)).parse(false).getState());
      }

      public BlockParticleData read(ParticleType<BlockParticleData> particleTypeIn, PacketBuffer buffer) {
         return new BlockParticleData(particleTypeIn, Block.BLOCK_STATE_IDS.getByValue(buffer.readVarInt()));
      }
   };
   private final ParticleType<BlockParticleData> particleType;
   private final IBlockState blockState;

   public BlockParticleData(ParticleType<BlockParticleData> particleTypeIn, IBlockState blockStateIn) {
      this.particleType = particleTypeIn;
      this.blockState = blockStateIn;
   }

   public void write(PacketBuffer buffer) {
      buffer.writeVarInt(Block.BLOCK_STATE_IDS.get(this.blockState));
   }

   public String getParameters() {
      return this.getType().getId() + " " + BlockStateParser.toString(this.blockState, (NBTTagCompound)null);
   }

   public ParticleType<BlockParticleData> getType() {
      return this.particleType;
   }

   @OnlyIn(Dist.CLIENT)
   public IBlockState getBlockState() {
      return this.blockState;
   }
}