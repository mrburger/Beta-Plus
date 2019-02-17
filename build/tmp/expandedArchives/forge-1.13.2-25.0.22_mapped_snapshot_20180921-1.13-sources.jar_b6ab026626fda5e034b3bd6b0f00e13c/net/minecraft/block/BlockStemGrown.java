package net.minecraft.block;

public abstract class BlockStemGrown extends Block {
   public BlockStemGrown(Block.Properties builder) {
      super(builder);
   }

   public abstract BlockStem getStem();

   public abstract BlockAttachedStem getAttachedStem();
}