package net.minecraft.state.properties;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;

public enum NoteBlockInstrument implements IStringSerializable {
   HARP("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP),
   BASEDRUM("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM),
   SNARE("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE),
   HAT("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT),
   BASS("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS),
   FLUTE("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE),
   BELL("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL),
   GUITAR("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR),
   CHIME("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME),
   XYLOPHONE("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE);

   private final String name;
   private final SoundEvent sound;

   private NoteBlockInstrument(String name, SoundEvent sound) {
      this.name = name;
      this.sound = sound;
   }

   public String getName() {
      return this.name;
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public static NoteBlockInstrument byState(IBlockState p_208087_0_) {
      Block block = p_208087_0_.getBlock();
      if (block == Blocks.CLAY) {
         return FLUTE;
      } else if (block == Blocks.GOLD_BLOCK) {
         return BELL;
      } else if (block.isIn(BlockTags.WOOL)) {
         return GUITAR;
      } else if (block == Blocks.PACKED_ICE) {
         return CHIME;
      } else if (block == Blocks.BONE_BLOCK) {
         return XYLOPHONE;
      } else {
         Material material = p_208087_0_.getMaterial();
         if (material == Material.ROCK) {
            return BASEDRUM;
         } else if (material == Material.SAND) {
            return SNARE;
         } else if (material == Material.GLASS) {
            return HAT;
         } else {
            return material == Material.WOOD ? BASS : HARP;
         }
      }
   }
}