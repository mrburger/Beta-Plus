package net.minecraft.network.datasync;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
   private static final IntIdentityHashBiMap<DataSerializer<?>> REGISTRY = new IntIdentityHashBiMap<>(16);
   public static final DataSerializer<Byte> BYTE = new DataSerializer<Byte>() {
      public void write(PacketBuffer buf, Byte value) {
         buf.writeByte(value);
      }

      public Byte read(PacketBuffer buf) {
         return buf.readByte();
      }

      public DataParameter<Byte> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Byte copyValue(Byte value) {
         return value;
      }
   };
   public static final DataSerializer<Integer> VARINT = new DataSerializer<Integer>() {
      public void write(PacketBuffer buf, Integer value) {
         buf.writeVarInt(value);
      }

      public Integer read(PacketBuffer buf) {
         return buf.readVarInt();
      }

      public DataParameter<Integer> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Integer copyValue(Integer value) {
         return value;
      }
   };
   public static final DataSerializer<Float> FLOAT = new DataSerializer<Float>() {
      public void write(PacketBuffer buf, Float value) {
         buf.writeFloat(value);
      }

      public Float read(PacketBuffer buf) {
         return buf.readFloat();
      }

      public DataParameter<Float> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Float copyValue(Float value) {
         return value;
      }
   };
   public static final DataSerializer<String> STRING = new DataSerializer<String>() {
      public void write(PacketBuffer buf, String value) {
         buf.writeString(value);
      }

      public String read(PacketBuffer buf) {
         return buf.readString(32767);
      }

      public DataParameter<String> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public String copyValue(String value) {
         return value;
      }
   };
   public static final DataSerializer<ITextComponent> TEXT_COMPONENT = new DataSerializer<ITextComponent>() {
      public void write(PacketBuffer buf, ITextComponent value) {
         buf.writeTextComponent(value);
      }

      public ITextComponent read(PacketBuffer buf) {
         return buf.readTextComponent();
      }

      public DataParameter<ITextComponent> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public ITextComponent copyValue(ITextComponent value) {
         return value.func_212638_h();
      }
   };
   public static final DataSerializer<Optional<ITextComponent>> OPTIONAL_TEXT_COMPONENT = new DataSerializer<Optional<ITextComponent>>() {
      public void write(PacketBuffer buf, Optional<ITextComponent> value) {
         if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeTextComponent(value.get());
         } else {
            buf.writeBoolean(false);
         }

      }

      public Optional<ITextComponent> read(PacketBuffer buf) {
         return buf.readBoolean() ? Optional.of(buf.readTextComponent()) : Optional.empty();
      }

      public DataParameter<Optional<ITextComponent>> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Optional<ITextComponent> copyValue(Optional<ITextComponent> value) {
         return value.isPresent() ? Optional.of(value.get().func_212638_h()) : Optional.empty();
      }
   };
   public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<ItemStack>() {
      public void write(PacketBuffer buf, ItemStack value) {
         buf.writeItemStack(value);
      }

      public ItemStack read(PacketBuffer buf) {
         return buf.readItemStack();
      }

      public DataParameter<ItemStack> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public ItemStack copyValue(ItemStack value) {
         return value.copy();
      }
   };
   public static final DataSerializer<Optional<IBlockState>> OPTIONAL_BLOCK_STATE = new DataSerializer<Optional<IBlockState>>() {
      public void write(PacketBuffer buf, Optional<IBlockState> value) {
         if (value.isPresent()) {
            buf.writeVarInt(Block.getStateId(value.get()));
         } else {
            buf.writeVarInt(0);
         }

      }

      public Optional<IBlockState> read(PacketBuffer buf) {
         int i = buf.readVarInt();
         return i == 0 ? Optional.empty() : Optional.of(Block.getStateById(i));
      }

      public DataParameter<Optional<IBlockState>> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Optional<IBlockState> copyValue(Optional<IBlockState> value) {
         return value;
      }
   };
   public static final DataSerializer<Boolean> BOOLEAN = new DataSerializer<Boolean>() {
      public void write(PacketBuffer buf, Boolean value) {
         buf.writeBoolean(value);
      }

      public Boolean read(PacketBuffer buf) {
         return buf.readBoolean();
      }

      public DataParameter<Boolean> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Boolean copyValue(Boolean value) {
         return value;
      }
   };
   public static final DataSerializer<IParticleData> PARTICLE_DATA = new DataSerializer<IParticleData>() {
      public void write(PacketBuffer buf, IParticleData value) {
         buf.writeVarInt(IRegistry.field_212632_u.getId(value.getType()));
         value.write(buf);
      }

      public IParticleData read(PacketBuffer buf) {
         return this.func_200543_a(buf, IRegistry.field_212632_u.get(buf.readVarInt()));
      }

      private <T extends IParticleData> T func_200543_a(PacketBuffer p_200543_1_, ParticleType<T> p_200543_2_) {
         return p_200543_2_.getDeserializer().read(p_200543_2_, p_200543_1_);
      }

      public DataParameter<IParticleData> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public IParticleData copyValue(IParticleData value) {
         return value;
      }
   };
   public static final DataSerializer<Rotations> ROTATIONS = new DataSerializer<Rotations>() {
      public void write(PacketBuffer buf, Rotations value) {
         buf.writeFloat(value.getX());
         buf.writeFloat(value.getY());
         buf.writeFloat(value.getZ());
      }

      public Rotations read(PacketBuffer buf) {
         return new Rotations(buf.readFloat(), buf.readFloat(), buf.readFloat());
      }

      public DataParameter<Rotations> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Rotations copyValue(Rotations value) {
         return value;
      }
   };
   public static final DataSerializer<BlockPos> BLOCK_POS = new DataSerializer<BlockPos>() {
      public void write(PacketBuffer buf, BlockPos value) {
         buf.writeBlockPos(value);
      }

      public BlockPos read(PacketBuffer buf) {
         return buf.readBlockPos();
      }

      public DataParameter<BlockPos> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public BlockPos copyValue(BlockPos value) {
         return value;
      }
   };
   public static final DataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new DataSerializer<Optional<BlockPos>>() {
      public void write(PacketBuffer buf, Optional<BlockPos> value) {
         buf.writeBoolean(value.isPresent());
         if (value.isPresent()) {
            buf.writeBlockPos(value.get());
         }

      }

      public Optional<BlockPos> read(PacketBuffer buf) {
         return !buf.readBoolean() ? Optional.empty() : Optional.of(buf.readBlockPos());
      }

      public DataParameter<Optional<BlockPos>> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Optional<BlockPos> copyValue(Optional<BlockPos> value) {
         return value;
      }
   };
   public static final DataSerializer<EnumFacing> FACING = new DataSerializer<EnumFacing>() {
      public void write(PacketBuffer buf, EnumFacing value) {
         buf.writeEnumValue(value);
      }

      public EnumFacing read(PacketBuffer buf) {
         return buf.readEnumValue(EnumFacing.class);
      }

      public DataParameter<EnumFacing> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public EnumFacing copyValue(EnumFacing value) {
         return value;
      }
   };
   public static final DataSerializer<Optional<UUID>> OPTIONAL_UNIQUE_ID = new DataSerializer<Optional<UUID>>() {
      public void write(PacketBuffer buf, Optional<UUID> value) {
         buf.writeBoolean(value.isPresent());
         if (value.isPresent()) {
            buf.writeUniqueId(value.get());
         }

      }

      public Optional<UUID> read(PacketBuffer buf) {
         return !buf.readBoolean() ? Optional.empty() : Optional.of(buf.readUniqueId());
      }

      public DataParameter<Optional<UUID>> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public Optional<UUID> copyValue(Optional<UUID> value) {
         return value;
      }
   };
   public static final DataSerializer<NBTTagCompound> COMPOUND_TAG = new DataSerializer<NBTTagCompound>() {
      public void write(PacketBuffer buf, NBTTagCompound value) {
         buf.writeCompoundTag(value);
      }

      public NBTTagCompound read(PacketBuffer buf) {
         return buf.readCompoundTag();
      }

      public DataParameter<NBTTagCompound> createKey(int id) {
         return new DataParameter<>(id, this);
      }

      public NBTTagCompound copyValue(NBTTagCompound value) {
         return value.copy();
      }
   };

   public static void registerSerializer(DataSerializer<?> serializer) {
      REGISTRY.add(serializer);
   }

   @Nullable
   public static DataSerializer<?> getSerializer(int id) {
      return REGISTRY.get(id);
   }

   public static int getSerializerId(DataSerializer<?> serializer) {
      return REGISTRY.getId(serializer);
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(VARINT);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(TEXT_COMPONENT);
      registerSerializer(OPTIONAL_TEXT_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(FACING);
      registerSerializer(OPTIONAL_UNIQUE_ID);
      registerSerializer(OPTIONAL_BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE_DATA);
   }
}