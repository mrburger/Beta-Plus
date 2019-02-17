package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class BasicParticleType extends ParticleType<BasicParticleType> implements IParticleData {
   private static final IParticleData.IDeserializer<BasicParticleType> DESERIALIZER = new IParticleData.IDeserializer<BasicParticleType>() {
      public BasicParticleType deserialize(ParticleType<BasicParticleType> particleTypeIn, StringReader reader) throws CommandSyntaxException {
         return (BasicParticleType)particleTypeIn;
      }

      public BasicParticleType read(ParticleType<BasicParticleType> particleTypeIn, PacketBuffer buffer) {
         return (BasicParticleType)particleTypeIn;
      }
   };

   protected BasicParticleType(ResourceLocation resourceLocationIn, boolean alwaysRender) {
      super(resourceLocationIn, alwaysRender, DESERIALIZER);
   }

   public ParticleType<BasicParticleType> getType() {
      return this;
   }

   public void write(PacketBuffer buffer) {
   }

   public String getParameters() {
      return this.getId().toString();
   }
}